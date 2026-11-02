import os
import sys
import json
import urllib.request
import urllib.error

def post_or_update_comment(marker, markdown_file):
    if os.environ.get('GITHUB_EVENT_NAME') != 'pull_request':
        print("Not a pull request event. Skipping PR comment.")
        return

    event_path = os.environ.get('GITHUB_EVENT_PATH')
    if not event_path or not os.path.exists(event_path):
        print("GITHUB_EVENT_PATH not found.")
        return

    with open(event_path, 'r') as f:
        event_data = json.load(f)

    if 'pull_request' not in event_data:
        print("No pull request found in event data.")
        return

    pr_number = event_data['pull_request']['number']
    repo = os.environ.get('GITHUB_REPOSITORY')
    token = os.environ.get('GITHUB_TOKEN')

    if not repo or not token:
        print("GITHUB_REPOSITORY or GITHUB_TOKEN not set.")
        return

    if not os.path.exists(markdown_file):
        print(f"Markdown file not found: {markdown_file}")
        sys.exit(1)

    with open(markdown_file, 'r') as f:
        body = f.read()

    headers = {
        "Authorization": f"Bearer {token}",
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28",
        "User-Agent": "GitHub-Actions-Script"
    }

    api_base = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"

    # 1. List comments to find existing comment with marker
    req = urllib.request.Request(api_base, headers=headers)
    existing_comment_id = None
    try:
        with urllib.request.urlopen(req) as response:
            comments = json.loads(response.read().data.decode('utf-8') if hasattr(response, 'data') else response.read().decode('utf-8'))
            for comment in comments:
                if marker in comment.get('body', ''):
                    existing_comment_id = comment['id']
                    break
    except urllib.error.HTTPError as e:
        print(f"Error listing comments: {e.code} {e.reason} - {e.read().decode('utf-8')}")
        sys.exit(1)

    # 2. Create or update comment
    data = json.dumps({"body": body}).encode('utf-8')
    if existing_comment_id:
        comment_url = f"{api_base}/{existing_comment_id}"
        req = urllib.request.Request(comment_url, data=data, headers=headers, method="PATCH")
        print(f"Updating existing comment ID {existing_comment_id}")
    else:
        req = urllib.request.Request(api_base, data=data, headers=headers, method="POST")
        print("Creating new comment")

    try:
        with urllib.request.urlopen(req) as response:
            print("Comment posted/updated successfully.")
    except urllib.error.HTTPError as e:
        print(f"Error posting/updating comment: {e.code} {e.reason} - {e.read().decode('utf-8')}")
        sys.exit(1)

if __name__ == '__main__':
    if len(sys.argv) < 3:
        print("Usage: python3 post_comment.py <marker> <markdown_file>")
        sys.exit(1)

    marker = sys.argv[1]
    markdown_file = sys.argv[2]
    post_or_update_comment(marker, markdown_file)
