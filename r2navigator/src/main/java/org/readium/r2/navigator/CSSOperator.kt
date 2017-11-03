package org.readium.r2.navigator

import android.support.v4.view.ViewPager
import android.view.View
import org.readium.r2.navigator.UserSettings.Appearance
import org.readium.r2.navigator.UserSettings.UserSettings

class CSSOperator(val userSettings: UserSettings) : SettingsFragment.Change {

    lateinit var resourcePager: ViewPager
    lateinit var myAdapter: MyPagerAdapter

    override fun onModeChange(mode: String){
        userSettings.appearance = when(mode){
            "readium-night-on" -> Appearance.Night
            "readium-sepia-on" -> Appearance.Sepia
            else -> Appearance.Default
        }
        updateViewCSS()
    }

    override fun onFontSizeChange(fontSize: String) {
        userSettings.fontSize = (fontSize.toFloat()).toString()
        updateViewCSS()
    }

    override fun onFragBack() {
        updateViewsCSS()
    }

    fun updateViewCSS(){
        val view = myAdapter.getViews()[resourcePager.currentItem] as R2EpubActivity.R2WebView
        applyCSS(view)
    }

    fun updateViewsCSS(){
        for (view in myAdapter.getViews()){
            applyCSS(view as R2EpubActivity.R2WebView)
        }
    }

    fun applyCSS(view: R2EpubActivity.R2WebView){
        for (property in userSettings.getProperties()){
            view.setProperty(property.key, property.value)
        }
    }

}
