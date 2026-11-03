#!/usr/bin/env python3
"""
AI PR Review Script
-------------------
This script reads a pull request diff ('changes.diff'), combines it with project-specific
reviewer instructions ('AGENTS.md'), and sends it to an LLM provider endpoint for automated code review.

Output:
  Writes the review or error notice to 'review.md' for GitHub Actions to post as a PR comment.
"""

import ipaddress
import json
import os
import re
import sys
import time
import urllib.error
import urllib.parse
import urllib.request

REQUEST_TIMEOUT = 45

# Secret scanning regex patterns to prevent accidental credential transmission in diff payloads.
SECRET_PATTERNS = [
    r"\b(ghp|gho|ghu|ghs|ghr)_[A-Za-z0-9_]{36}\b",
    r"\bsk-[A-Za-z0-9_-]{32,}\b",
    r"\bAIza[0-9A-Za-z-_]{35}\b",
    r"\bAKIA[0-9A-Z]{16}\b",
    r"-----BEGIN (?:RSA|EC|OPENSSH|DSA|PGP|PRIVATE) KEY-----[\s\S]*?-----END (?:RSA|EC|OPENSSH|DSA|PGP|PRIVATE) KEY-----",
    r"\bBearer\s+[A-Za-z0-9_.-]{20,}\b",
]


def validate_url(url: str) -> None:
    """
    Security Helper: Validates that target URL uses https:// and blocks SSRF attempts against
    loopback addresses, local network IPs, or cloud metadata endpoints (e.g. 169.254.169.254).
    """
    parsed = urllib.parse.urlparse(url)
    if parsed.scheme != "https":
        raise ValueError(f"Insecure scheme '{parsed.scheme}': LLM_BASE_URL must use https://")

    hostname = parsed.hostname
    if not hostname:
        raise ValueError("Invalid LLM_BASE_URL: missing hostname")

    hostname_lower = hostname.lower()
    if hostname_lower in ("localhost", "0.0.0.0"):
        raise ValueError(f"Blocked internal hostname: '{hostname}'")

    try:
        ip = ipaddress.ip_address(hostname)
        if ip.is_loopback or ip.is_private or ip.is_link_local or ip.is_unspecified or ip.is_multicast:
            raise ValueError(f"Blocked private/internal IP address: {ip}")
    except ValueError as e:
        # Ignore string parsing exception if hostname is a standard domain name (e.g., openrouter.ai).
        if "has no attribute" in str(e) or "does not appear to be an IPv4 or IPv6 address" in str(e):
            pass
        elif "Blocked" in str(e):
            raise e


def scan_and_redact_secrets(content: str) -> tuple[str, int]:
    """
    Security Helper: Scans diff text for common hardcoded secret patterns and redacts them
    prior to sending the payload to third-party LLM APIs.
    """
    count = 0
    redacted_content = content
    for pattern in SECRET_PATTERNS:
        matches = re.findall(pattern, redacted_content)
        if matches:
            count += len(matches)
            redacted_content = re.sub(pattern, "***REDACTED_SECRET***", redacted_content)
    return redacted_content, count


def sanitize(text: str, secret: str) -> str:
    """
    Security Helper: Redacts any occurrences of sensitive API secrets from text or error messages
    before printing them to GitHub Actions console logs.
    """
    if not text:
        return ""
    if secret:
        text = text.replace(secret, "***REDACTED***")
    return text


def main() -> None:
    # ---------------------------------------------------------------------------
    # 1. ENVIRONMENT VARIABLE VALIDATION
    # ---------------------------------------------------------------------------
    # Ensure all required configuration variables provided by GitHub Actions exist.
    api_key = os.environ.get("LLM_API_KEY")
    if not api_key:
        print("Error: Missing required environment variable: LLM_API_KEY", flush=True)
        sys.exit(1)

    model = os.environ.get("LLM_MODEL_NAME")
    if not model:
        print("Error: Missing required environment variable: LLM_MODEL_NAME", flush=True)
        sys.exit(1)

    base_url = os.environ.get("LLM_BASE_URL")
    if not base_url:
        print("Error: Missing required environment variable: LLM_BASE_URL", flush=True)
        sys.exit(1)

    # ---------------------------------------------------------------------------
    # 2. ENDPOINT URL NORMALIZATION & SSRF VALIDATION
    # ---------------------------------------------------------------------------
    # Clean up trailing slashes and ensure endpoint ends with /chat/completions.
    clean_base_url = base_url.strip().rstrip("/")
    if "chat/completions" in clean_base_url:
        api_url = clean_base_url
    else:
        api_url = f"{clean_base_url}/chat/completions"

    try:
        validate_url(api_url)
    except ValueError as e:
        print(f"Security Error: {e}", flush=True)
        sys.exit(1)

    # ---------------------------------------------------------------------------
    # 3. DIFF FILE INGESTION, SECRET REDACTION & TRUNCATION
    # ---------------------------------------------------------------------------
    # Verify changes.diff exists and contains meaningful content to review.
    if not os.path.exists("changes.diff"):
        print("No changes.diff file found.", flush=True)
        sys.exit(0)

    # Use errors="replace" so invalid UTF-8 bytes are safely replaced rather than dropped.
    with open("changes.diff", "r", encoding="utf-8", errors="replace") as f:
        diff = f.read().strip()

    # Skip review if diff is empty or too trivial (< 10 characters).
    if len(diff) < 10:
        print("Diff is empty or too small, skipping review.", flush=True)
        sys.exit(0)

    # Scan diff for hardcoded credentials and redact before payload construction.
    diff, secret_count = scan_and_redact_secrets(diff)
    if secret_count > 0:
        print(f"Security Warning: Redacted {secret_count} hardcoded secret(s) from diff before sending to LLM.", flush=True)

    # Cap diff size to 50,000 characters to stay within model context limits.
    if len(diff) > 50000:
        print("Warning: Diff truncated to 50,000 characters.", flush=True)
        diff = diff[:50000] + "\n... [TRUNCATED]"

    # ---------------------------------------------------------------------------
    # 4. SYSTEM PROMPT & PROJECT CONVENTIONS
    # ---------------------------------------------------------------------------
    # Base instructions for the AI reviewer.
    system_prompt = (
        "You are a senior Android and Kotlin Multiplatform developer. "
        "Review this PR diff for bugs, error handling, concurrency, and code quality."
    )
    # Include repository guidelines (AGENTS.md) if present to prevent false positives.
    if os.path.exists("AGENTS.md"):
        with open("AGENTS.md", "r", encoding="utf-8") as f:
            system_prompt += f"\n\nAdhere strictly to project guidelines:\n\n{f.read().strip()}"

    # Construct the JSON payload for OpenAI-compatible chat completion API.
    payload = json.dumps({
        "model": model,
        "messages": [
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": f"Review this diff:\n\n```diff\n{diff}\n```"}
        ]
    }).encode("utf-8")

    # ---------------------------------------------------------------------------
    # 5. API REQUEST & EXPONENTIAL BACKOFF RETRY LOOP
    # ---------------------------------------------------------------------------
    max_attempts = 3
    for attempt in range(max_attempts):
        try:
            print(f"Sending request to LLM (Attempt {attempt + 1}/{max_attempts}, Model: {model})...", flush=True)
            req = urllib.request.Request(
                api_url,
                data=payload,
                headers={
                    "Authorization": f"Bearer {api_key}",
                    "Content-Type": "application/json"
                },
                method="POST"
            )
            # Execute HTTP request with per-call REQUEST_TIMEOUT timeout.
            with urllib.request.urlopen(req, timeout=REQUEST_TIMEOUT) as resp:
                body = resp.read().decode("utf-8", errors="replace")
                data = json.loads(body)

            # Check if API returned an in-body error payload (e.g. model overload / quota / model error).
            if isinstance(data, dict) and "error" in data:
                err_info = data["error"]
                err_detail = err_info.get("message") if isinstance(err_info, dict) else str(err_info)
                clean_err = sanitize(str(err_detail), api_key)
                raise RuntimeError(f"Model provider error: {clean_err}")

            # Validate expected response structure.
            if not isinstance(data, dict) or not data.get("choices"):
                clean_data = sanitize(str(data), api_key)
                raise RuntimeError(f"Unexpected response format: {clean_data}")

            # Extract generated review text.
            message = data["choices"][0].get("message", {})
            review = message.get("content", "")
            if not review or not review.strip():
                raise RuntimeError("API returned empty review content.")

            # SUCCESS: Output review to console and write to review.md for posting.
            print("Review generated successfully.", flush=True)
            print(review, flush=True)
            with open("review.md", "w", encoding="utf-8") as f:
                f.write(review)
            return

        # -----------------------------------------------------------------------
        # 6. ERROR HANDLING, LOG SANITIZATION & USER NOTICES
        # -----------------------------------------------------------------------
        except (urllib.error.HTTPError, urllib.error.URLError, json.JSONDecodeError, KeyError, IndexError, RuntimeError, TimeoutError) as e:
            # Format and sanitize error message to avoid secret exposure in logs.
            if isinstance(e, urllib.error.HTTPError):
                if e.code in (401, 403):
                    err_msg = f"Authentication error (HTTP {e.code})"
                else:
                    err_body = sanitize(e.read().decode("utf-8", errors="replace"), api_key)
                    err_msg = f"HTTP error {e.code}: {err_body}"
            else:
                err_msg = sanitize(str(e), api_key)

            print(f"Attempt {attempt + 1} failed (URL: {api_url}): {err_msg}", flush=True)

            err_msg_lower = err_msg.lower()

            # Detect if model choice is invalid or incompatible (e.g. embedding model or Bad Request).
            is_invalid_model = any(
                keyword in err_msg_lower
                for keyword in [
                    "embedding",
                    "cannot be used",
                    "use the /embeddings",
                    "invalid model",
                    "model not found",
                    "not supported",
                    "does not exist",
                    "http error 400"
                ]
            )

            if is_invalid_model:
                notice = (
                    f"⚠️ **AI PR Review Notice**: The configured model (`{model}`) "
                    f"is invalid or incompatible with PR code reviews.\n\n"
                    f"**Details:** {err_msg}\n\n"
                    f"Please update the `LLM_MODEL_NAME` repository variable to a valid chat completion model (e.g., `openai/gpt-4o-mini`)."
                )
                with open("review.md", "w", encoding="utf-8") as f:
                    f.write(notice)
                print(notice, flush=True)
                return

            # Detect if failure is due to model provider rate limit or capacity overload.
            is_overloaded = any(
                keyword in err_msg_lower
                for keyword in [
                    "overloaded",
                    "rate limit",
                    "429",
                    "503",
                    "502",
                    "504",
                    "capacity",
                    "quota",
                    "provider_overloaded"
                ]
            )

            # If model is overloaded and retries are exhausted, write a helpful notice to review.md.
            if attempt == max_attempts - 1 and is_overloaded:
                notice = (
                    f"⚠️ **AI PR Review Notice**: The configured AI model (`{model}`) "
                    f"for the PR review is currently at its limit or overloaded. "
                    f"Please try again later or switch to another model in `LLM_MODEL_NAME`."
                )
                with open("review.md", "w", encoding="utf-8") as f:
                    f.write(notice)
                print(notice, flush=True)
                return

            # Exponential backoff sleep before next retry attempt (1s, 2s).
            if attempt < max_attempts - 1:
                time.sleep(2 ** attempt)

    # Exit with code 1 if all retries fail without a user notification notice.
    print("Error: Failed to obtain code review after retries.", flush=True)
    sys.exit(1)


if __name__ == "__main__":
    main()
