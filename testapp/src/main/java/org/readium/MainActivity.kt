package org.readium.r2.testapp

import org.readium.r2shared.RenditionLayout
import org.readium.r2shared.Contributor
import org.readium.r2shared.Publication
import org.readium.r2shared.Metadata
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.readium.r2streamer.Parser.EpubParser

class MainActivity : AppCompatActivity() {

    lateinit var epubParser: EpubParser

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val publi = Publication()
        val textView = findViewById<TextView>(R.id.textView)
        val contrib = Contributor()

        publi.metadata = Metadata()
        contrib.multilangName.singleString = "Patrick Bruel"
        publi.metadata.editors.add(contrib)
        publi.metadata.direction = "vertical"
        publi.metadata.rendition.layout = RenditionLayout.fixed
        textView.text = publi.manifest()
        val gson = Gson()
        val pub = gson.fromJson(textView.text.toString(), Publication::class.java)
        test()
        Toast.makeText(this@MainActivity, pub.metadata.editors.first().name, Toast.LENGTH_LONG).show()
    }

    fun test(){
        epubParser = EpubParser()
        val pubBox = epubParser.parse(
                "/sdcard/Documents/book.epub")
        val publication = pubBox.publication
        Toast.makeText(this@MainActivity, publication.metadata.title, Toast.LENGTH_LONG).show()
    }

}