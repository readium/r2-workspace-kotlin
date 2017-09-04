package org.readium.testapp

import Link.RenditionLayout
import MetaData.Contributor
import Publication
import Metadata
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val publi = Publication()
        val textView = findViewById<TextView>(R.id.textView)
        val contrib = Contributor()

        publi.metadata = Metadata()
        contrib.name = "Patrick Bruel"
        publi.metadata.editors.add(contrib)
        publi.metadata.direction = "vertical"
        publi.metadata.rendition.layout = RenditionLayout.fixed
        textView.textSize = 10.0f
        textView.text = publi.manifest()
        val gson = Gson()
        val pub = gson.fromJson(textView.text.toString(), Publication::class.java)
        Toast.makeText(this@MainActivity, pub.metadata.rendition.layout.toString(), Toast.LENGTH_LONG).show()
    }

}