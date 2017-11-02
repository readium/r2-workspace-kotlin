package org.readium.r2.navigator.UserSettings

import android.content.SharedPreferences

private const val fontSizeKey = "--USER__fontSize"
private const val fontKey = "--USER__fontFamily"
private const val appearanceKey = "--USER__appearance"
private const val scrollKey = "--USER__scroll"
private const val publisherSettingsKey = "--USER__advancedSettings"
private const val textAlignmentKey = "--USER__textAlign"
private const val columnCountKey = "--USER__colCount"
private const val wordSpacingKey = "--USER__wordSpacing"
private const val letterSpacingKey = "--USER__letterSpacing"
private const val pageMarginsKey = "--USER__pageMargins"

class UserSettings(preferences: SharedPreferences) {

    data class CssProperty(val key: String, val value: String)

    var fontSize = "100"
    var font = Font.Publisher
    var appearance = Appearance.Default
    var scroll = Scroll.Off
    var publisherSettings = false
    var textAlignment = TextAlignment.Justify
    var columnCount = ColumnCount.Auto
    var wordSpacing = WordSpacing(0.0)
    var letterSpacing = LetterSpacing(0.0)
    var pageMargins = PageMargins(1.0)

    init {
        appearance = Appearance.valueOf(preferences.getString("mode", "readium-default-on"))
    }

    fun getProperties() : List<CssProperty>{
        val properties = mutableListOf<CssProperty>()
        properties.add(CssProperty(fontSizeKey, fontSize + "%"))
        properties.add(CssProperty("--USER_fontOverride", when (font.name){
            "publisher" -> "readium-font-off"
            else -> "readium-font-on"
        }))
        properties.add(CssProperty(fontKey, font.name))
        properties.add(CssProperty(appearanceKey, appearance.name))
        properties.add(CssProperty(scrollKey, scroll.name))
        properties.add(CssProperty(publisherSettingsKey, when (publisherSettings){
            true -> "readium-advanded-off"
            false -> "readium-advanced-on"
        }))
        properties.add(CssProperty(textAlignmentKey, textAlignment.name))
        properties.add(CssProperty(columnCountKey, columnCount.name))
        properties.add(CssProperty(wordSpacingKey, wordSpacing.toString()))
        properties.add(CssProperty(letterSpacingKey, letterSpacing.toString()))
        properties.add(CssProperty(pageMarginsKey, pageMargins.toString()))
        return properties
    }


}