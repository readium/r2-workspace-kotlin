package org.readium.testapp

import org.readium.r2shared.RenditionLayout
import org.readium.r2shared.Contributor
import org.readium.r2shared.Publication
import org.readium.r2shared.Metadata
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.PermissionChecker
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.readium.r2streamer.AEXML.AEXML
import org.readium.r2streamer.Parser.EpubParser
import java.security.Permission
import java.security.PermissionCollection

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        //if (checkSelfPermission())
        val parser = EpubParser()
        val pubBox = parser.parse(Environment.getExternalStorageDirectory().absolutePath
                + "/Documents/accessible.epub")
        Toast.makeText(this@MainActivity, pubBox.publication.metadata.title, Toast.LENGTH_LONG).show()
    }

}