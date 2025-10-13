package com.promni.mft.presentation.ui.utils

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

@Composable
expect fun getWindowSizeClass(): WindowSizeClass


/**
 * Adjusts the brightness of a color by a given factor.
 *
 * This function converts the color to the HSL color space, modifies the lightness component,
 * and then converts it back to RGB. This approach preserves the original hue and saturation
 * better than simply manipulating RGB values.
 *
 * @param factor A float value to adjust the brightness.
 * - `factor > 1.0f` will make the color lighter.
 * - `factor < 1.0f` will make the color darker.
 * - `factor = 1.0f` will return the original color.
 * A factor of 0 will result in black, and a very large factor will result in white.
 * @return A new [Color] object with the adjusted brightness.
 */
fun Color.adjustBrightness(factor: Float): Color {
    // Convert RGB to HSL
    val hsl = rgbToHsl(this.red, this.green, this.blue)

    // Adjust the Lightness component
    // We use coerceIn to ensure the new lightness value stays within the valid [0, 1] range.
    val newLightness = (hsl[2] * factor).coerceIn(0f, 1f)

    // Convert back to RGB and return the new color, preserving the original alpha
    return hslToRgb(hsl[0], hsl[1], newLightness, this.alpha)
}

/**
 * Converts an RGB color value to HSL. Conversion formula
 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
 *
 * @param r The red color value (0-1).
 * @param g The green color value (0-1).
 * @param b The blue color value (0-1).
 * @return A FloatArray containing the HSL values: {hue, saturation, lightness}.
 */
private fun rgbToHsl(r: Float, g: Float, b: Float): FloatArray {
    val max = max(r, max(g, b))
    val min = min(r, min(g, b))
    var h: Float
    val s: Float
    val l = (max + min) / 2

    if (max == min) {
        // achromatic
        h = 0f
        s = 0f
    } else {
        val d = max - min
        s = if (l > 0.5f) d / (2 - max - min) else d / (max + min)
        h = when (max) {
            r -> (g - b) / d + (if (g < b) 6 else 0)
            g -> (b - r) / d + 2
            b -> (r - g) / d + 4
            else -> 0f // Should not happen
        }
        h /= 6
    }

    return floatArrayOf(h, s, l)
}

/**
 * Converts an HSL color value to RGB. Conversion formula
 * adapted from http://en.wikipedia.org/wiki/HSL_color_space.
 *
 * @param h The hue (0-1).
 * @param s The saturation (0-1).
 * @param l The lightness (0-1).
 * @param alpha The alpha value (0-1).
 * @return A [Color] object.
 */
private fun hslToRgb(h: Float, s: Float, l: Float, alpha: Float): Color {
    val r: Float
    val g: Float
    val b: Float

    if (s == 0f) {
        // achromatic
        r = l
        g = l
        b = l
    } else {
        val q = if (l < 0.5f) l * (1 + s) else l + s - l * s
        val p = 2 * l - q
        r = hueToRgb(p, q, h + 1f / 3)
        g = hueToRgb(p, q, h)
        b = hueToRgb(p, q, h - 1f / 3)
    }

    return Color(red = r, green = g, blue = b, alpha = alpha)
}

private fun hueToRgb(p: Float, q: Float, t: Float): Float {
    var tt = t
    if (tt < 0) tt += 1
    if (tt > 1) tt -= 1
    return when {
        tt < 1f / 6 -> p + (q - p) * 6 * tt
        tt < 1f / 2 -> q
        tt < 2f / 3 -> p + (q - p) * (2f / 3 - tt) * 6
        else -> p
    }
}
