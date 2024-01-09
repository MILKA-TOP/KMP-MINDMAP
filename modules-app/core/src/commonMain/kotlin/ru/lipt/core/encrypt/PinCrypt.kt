package ru.lipt.core.encrypt

object PinCrypt {
    fun encrypt(value: String, pin: String, pinLength: Int = DEFAULT_PIN_LENGTH): String {
        if (pin.length != pinLength) throw IllegalArgumentException()
        val key = arrayOf(
            pin.substring(3, pinLength).toInt(HEX_RADIX),
            pin.substring(2, 4).toInt(HEX_RADIX),
            pin.substring(0, 2).toInt(HEX_RADIX),
        )
        return (value.indices step 2).joinToString("") { i ->
            value.substring(i, i + 2)
                .toInt(HEX_RADIX)
                .xor(key[i / 2 % key.size])
                .toString(HEX_RADIX)
                .padStart(2, '0')
        }
    }

    const val HEX_RADIX = 16
    const val DEFAULT_PIN_LENGTH = 4
}
