package org.readium.r2.testapp

import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import android.widget.TextView
import org.readium.r2.shared.Contributor
import org.readium.r2.shared.Publication
import org.readium.r2.shared.Metadata
import org.readium.r2.shared.RenditionLayout
import org.readium.r2.streamer.Parser.EpubParser
import android.support.v7.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val publi = Publication()
        val textView = findViewById<TextView>(R.id.textView)
        val contrib = Contributor()
        val button = findViewById<Button>(R.id.button)
        var publication: Publication
        button.setOnClickListener {
            publication = test()
            textView.text = publication.manifest()
        }

    }

    fun test() = EpubParser().parse("/sdcard/Download/marron.epub").publication

}