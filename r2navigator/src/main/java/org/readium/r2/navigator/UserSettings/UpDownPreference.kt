package org.readium.r2.navigator.UserSettings

import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.readium.r2.navigator.R

class UpDownPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    var fontSize = 0f
    var step = 0f
    var min = 0f
    var max = 0f

    init {
        dialogLayoutResource = R.layout.up_down_preference
        setPositiveButtonText("Ok")
        setDialogIcon(null)
        for (i in 0..attrs.attributeCount - 1){
            when (attrs.getAttributeName(i).toLowerCase()) {
                "step" -> step = attrs.getAttributeFloatValue(i, 10f)
                "min" -> min = attrs.getAttributeFloatValue(i, 100f)
                "max" -> max = attrs.getAttributeFloatValue(i, 150f)
            }
        }
    }

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        fontSize = preferenceManager.sharedPreferences.getString("fontSize", "100").toFloat()
        val buttonUp = view!!.findViewById<Button>(R.id.upButton)
        buttonUp.setOnClickListener {
            if (fontSize + step <= max){
                fontSize += step
                preferenceManager.sharedPreferences.edit().putString("fontSize", fontSize.toString()).apply()
            }
            Toast.makeText(context, fontSize.toString(), Toast.LENGTH_SHORT).show()
        }
        val buttonDown = view.findViewById<Button>(R.id.downButton)
        buttonDown.setOnClickListener{
            if (fontSize - step >= min){
                fontSize -= step
                preferenceManager.sharedPreferences.edit().putString("fontSize", fontSize.toString()).apply()
            }
            Toast.makeText(context, fontSize.toString(), Toast.LENGTH_SHORT).show()
        }
    }

}