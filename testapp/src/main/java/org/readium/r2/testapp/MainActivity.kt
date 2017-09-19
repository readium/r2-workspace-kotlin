package org.readium.r2.testapp

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import org.readium.r2.shared.Publication
import org.readium.r2.streamer.Parser.EpubParser
import java.io.File
import java.io.InputStream


class MainActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName
    val r2test_path = Environment.getExternalStorageDirectory().path + "/r2test/"
    var publication_path: String = r2test_path + "test.epub"

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100)


        parse_button.setOnClickListener {
            val publication: Publication = EpubParser().parse(publication_path).publication
            textView.text = publication.metadata.title
        }

        pick_button.setOnClickListener {
            showEpubFiles()
        }


        val intent = intent
        val action = intent.action

        if (action.compareTo(Intent.ACTION_VIEW) == 0) {
            val scheme = intent.scheme
            val resolver = contentResolver

            if (scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                val uri = intent.data
                val name = getContentName(resolver, uri)
                Log.v("tag", "Content intent detected: " + action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val input = resolver.openInputStream(uri)
                val importfilepath: String = r2test_path + name
                val dir = File(r2test_path)
                if (!dir.exists()) dir.mkdirs()

                publication_path = importfilepath
                input.toFile(importfilepath)

                parse_button.callOnClick()

            } else if (scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                val uri = intent.data
                val name = uri.lastPathSegment
                Log.v("tag", "File intent detected: " + action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val input = resolver.openInputStream(uri)
                val importfilepath: String = r2test_path + name
                val dir = File(r2test_path)
                if (!dir.exists()) dir.mkdirs()
                publication_path = importfilepath
                input.toFile(importfilepath)

                parse_button.callOnClick()

            } else if (scheme.compareTo("http") == 0) {
                // TODO Import from HTTP!
            } else if (scheme.compareTo("ftp") == 0) {
                // TODO Import from FTP!
            }
        }
    }


    private fun askForPermission(permission: String, requestCode: Int?) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode!!)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode!!)
            }
        }
    }

    fun showEpubFiles() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.type = "application/epub+zip"

        startActivityForResult(intent, 1)
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int,
                                         data: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            var uri: Uri? = null
            if (data != null) {
                uri = data.data
                Log.i(TAG, "Uri: " + uri!!.toString())
                val intent = intent
                val action = intent.action
                val resolver = contentResolver
                val name = getContentName(resolver, uri)
                Log.v("tag", "Content intent detected: " + action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val input = resolver.openInputStream(uri)
                val importfilepath: String = r2test_path + name
                val dir = File(r2test_path)
                if (!dir.exists()) dir.mkdirs()

                publication_path = importfilepath
                input.toFile(importfilepath)


                parse_button.callOnClick()

            }
        }
        else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {


            if (data != null) {
                val name = data.getStringExtra("name")
                val intent = intent
                val action = intent.action
                Log.v("tag", "Content intent detected: " + action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val importfilepath: String = r2test_path + name
                publication_path = importfilepath
                parse_button.callOnClick()
            }

        }
    }

    private fun getContentName(resolver: ContentResolver, uri: Uri): String? {
        val cursor = resolver.query(uri, null, null, null, null)
        cursor!!.moveToFirst()
        val nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
        if (nameIndex >= 0) {
            val name = cursor.getString(nameIndex)
            cursor.close()
            return name
        } else {
            return null
        }
    }

    fun InputStream.toFile(path: String) {
        use { input ->
            File(path).outputStream().use { input.copyTo(it) }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.getItemId()) {
            R.id.list -> {

                val i = Intent(this, EpubListActivity::class.java)
                startActivityForResult(i, 2)

                return false
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}