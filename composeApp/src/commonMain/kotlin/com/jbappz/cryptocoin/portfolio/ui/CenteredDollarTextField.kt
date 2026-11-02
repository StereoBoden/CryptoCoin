package com.jbappz.cryptocoin.portfolio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbappz.cryptocoin.theme.CryptoCoinTheme
import com.jbappz.cryptocoin.theme.LightDarkPreview

/**
 * A centered text field for entering numerical dollar amounts.
 *
 * Automatically requests focus when first displayed and filters input to ensure valid
 * numerical amounts up to the configured limit.
 *
 * @param modifier The [Modifier] to be applied to the layout.
 * @param amountText The current amount text value to display.
 * @param onAmountChange Callback invoked when the user enters a valid amount string.
 */
@Composable
fun CenteredDollarTextField(
    modifier: Modifier = Modifier,
    amountText: String,
    onAmountChange: (String) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val displayText = amountText.trimStart('$')

    BasicTextField(
        value = displayText,
        onValueChange = { newValue ->
            val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
            if (trimmed.isEmpty() || trimmed.toInt() <= 10000) {
                onAmountChange(trimmed)
            }
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .padding(16.dp),
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            textAlign = TextAlign.Center
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.height(56.dp).wrapContentWidth()
            ) {
                Text(
                    text = "$",
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                )
                innerTextField()
            }
        },
        cursorBrush = SolidColor(Color.White)
    )
}

@LightDarkPreview
@Composable
private fun CenteredDollarTextFieldPreview() {
    CryptoCoinTheme {
        Surface {
            CenteredDollarTextField(
                amountText = "250",
                onAmountChange = {}
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun CenteredDollarTextFieldEmptyPreview() {
    CryptoCoinTheme {
        Surface {
            CenteredDollarTextField(
                amountText = "",
                onAmountChange = {}
            )
        }
    }
}