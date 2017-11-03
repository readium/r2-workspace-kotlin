package org.readium.r2.navigator

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    lateinit var change: Change

    interface Change {
        fun onModeChange(mode: String)
        fun onFontSizeChange(fontSize: String)
        fun onFragBack()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "mode"){
            change.onModeChange(sharedPreferences?.getString("mode", "readium-default-on") ?: "readium-default-on")
        } else if (key == "fontSize"){
            change.onFontSizeChange(sharedPreferences?.getString("fontSize", "100") ?: "100")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundColor(resources.getColor(android.R.color.white))
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        change = (activity as R2EpubActivity).cssOperator
    }

    override fun onPause() {
        super.onPause()
        change.onFragBack()
    }

}
