package org.readium.r2.testapp

import org.readium.r2shared.RenditionLayout
import org.readium.r2shared.Contributor
import org.readium.r2shared.Publication
import org.readium.r2shared.Metadata
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        val contributor = Contributor()
        contributor.multilangName.singleString = "Patrick Bruel"

        val publication = Publication()
        publication.metadata = Metadata()
        publication.metadata.editors.add(contributor)
        publication.metadata.direction = "vertical"
        publication.metadata.rendition.layout = RenditionLayout.fixed

        textView.text = publication.manifest()

        val pub = Gson().fromJson(textView.text.toString(), Publication::class.java)

        Toast.makeText(this, pub.metadata.editors.first().name, Toast.LENGTH_LONG).show()
    }

}