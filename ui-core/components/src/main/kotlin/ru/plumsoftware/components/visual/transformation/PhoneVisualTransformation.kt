package ru.plumsoftware.components.visual.transformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.TransformedText

class PhoneVisualTransformation(
    private val mask: String,
    private val maskNumber: Char
) : VisualTransformation {
    private val maxLength = mask.count { it == maskNumber }

    override fun filter(text: AnnotatedString): TransformedText {
        // Скорректировать вводимый текст, если он превышает максимальную длину
        val trimmed = if (text.length > maxLength) text.take(maxLength) else text

        val annotatedString = buildAnnotatedString {
            if (trimmed.isEmpty()) return@buildAnnotatedString

            var maskIndex = 0
            var textIndex = 0

            while (textIndex < trimmed.length && maskIndex < mask.length) {
                // Если текущий символ маски не является маской для цифры
                if (mask[maskIndex] != maskNumber) {
                    append(mask[maskIndex])
                    maskIndex++
                } else {
                    // Добавляем цифру из введенного текста
                    if (textIndex < trimmed.length) {
                        append(trimmed[textIndex])
                        textIndex++
                    }
                    maskIndex++
                }
            }
        }

        return TransformedText(annotatedString, PhoneOffsetMapper(mask, maskNumber, trimmed.length))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PhoneVisualTransformation) return false
        if (mask != other.mask) return false
        if (maskNumber != other.maskNumber) return false
        return true
    }

    override fun hashCode(): Int {
        return mask.hashCode()
    }
}

private class PhoneOffsetMapper(
    private val mask: String,
    private val numberChar: Char,
    private val originalTextLength: Int
) : OffsetMapping {
    override fun originalToTransformed(offset: Int): Int {
        if (offset > originalTextLength) return mask.length // Если индекс вне диапазона, возвращаем конец маски

        var noneDigitCount = 0
        var i = 0
        while (i < offset) {
            if (mask[i] != numberChar) {
                noneDigitCount++
            }
            i++
        }
        return offset + noneDigitCount
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (offset > mask.length) return originalTextLength // Если индекс вне диапазона, возвращаем длину оригинального текста

        return offset - mask.take(offset).count { it != numberChar }
    }
}
