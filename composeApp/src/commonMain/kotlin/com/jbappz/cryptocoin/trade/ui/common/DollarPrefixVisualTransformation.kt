package com.jbappz.cryptocoin.trade.ui.common

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * A [VisualTransformation] that prepends a currency prefix (by default `"$ "`) to non-empty text input.
 *
 * When the input text is empty, no prefix is displayed. When text is present, the specified [prefix]
 * is prepended and cursor offset mapping is handled automatically so cursor positioning remains correct.
 *
 * @param prefix The currency prefix string to display before the entered text. Defaults to `"$ "`.
 */
class DollarPrefixVisualTransformation(
    private val prefix: String = "$ ",
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (text.text.isEmpty()) {
            return TransformedText(text, OffsetMapping.Identity)
        }
        val transformedText = AnnotatedString(prefix + text.text)
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return offset + prefix.length
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 0) return 0
                val transformedOffset = offset - prefix.length
                return if (transformedOffset < 0) 0 else transformedOffset
            }
        }
        return TransformedText(transformedText, offsetMapping)
    }
}
