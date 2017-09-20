package org.readium.r2.testapp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
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
import java.net.URL


class MainActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName

    val r2test_directory_path = Environment.getExternalStorageDirectory().path + "/r2test/"
    var publication_path: String = r2test_directory_path + "dummy.epub"

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 100)

        val dir = File(r2test_directory_path)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        if (intent.action.compareTo(Intent.ACTION_VIEW) == 0) {

            if (intent.scheme.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
                val uri = intent.data
                val name = getContentName(contentResolver, uri)
                Log.v("tag", "Content intent detected: " + intent.action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val input = contentResolver.openInputStream(uri)
                val local_epub_path: String = r2test_directory_path + name

                publication_path = local_epub_path
                input.toFile(local_epub_path)

                parseAndShowEpub()

            } else if (intent.scheme.compareTo(ContentResolver.SCHEME_FILE) == 0) {
                val uri = intent.data
                val name = uri.lastPathSegment
                Log.v("tag", "File intent detected: " + intent.action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val input = contentResolver.openInputStream(uri)
                val local_path: String = r2test_directory_path + name

                publication_path = local_path
                input.toFile(local_path)

                parseAndShowEpub()

            } else if (intent.scheme.compareTo("http") == 0) {
                val uri = intent.data
                val name = uri.lastPathSegment
                Log.v("tag", "HTTP intent detected: " + intent.action + " : " + intent.dataString + " : " + intent.type + " : " + name)
                val local_epub_path: String = r2test_directory_path + name

                publication_path = local_epub_path

                val progress = showProgress(this, null, "Please wait while your books is being downloaded.")
                progress.show()
                val thread = Thread(Runnable {
                    try {
                        val input = URL(uri.toString()).openStream()
                        input.toFile(local_epub_path)
                        runOnUiThread(Runnable {
                            parseAndShowEpub()
                            progress.dismiss()
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                })
                thread.start()

            } else if (intent.scheme.compareTo("ftp") == 0) {
                // TODO Import from FTP!
            }
        }
    }

    private fun parseAndShowEpub() {
        val publication: Publication = EpubParser().parse(publication_path).publication
        textView.text = String.format("%s\n\n%s", publication.metadata.title, publication.metadata.description ?: "no description")
    }

    private fun showProgress(context: Context, title: String?, message: String?): ProgressDialog {

        val b = ProgressDialog(context)
        b.setButton(DialogInterface.BUTTON_NEGATIVE, "DISMISS", DialogInterface.OnClickListener { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        b.setMessage(message)
        b.setTitle(title)

        return b
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
                val name = getContentName(contentResolver, uri)
                val input = contentResolver.openInputStream(uri)
                val local_epub_path: String = r2test_directory_path + name

                publication_path = local_epub_path
                input.toFile(local_epub_path)

                parseAndShowEpub()

            }
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {

            // existing epub selected through the list activity
            if (data != null) {

                val name = data.getStringExtra("name")
                val local_epub_path: String = r2test_directory_path + name
                publication_path = local_epub_path

                parseAndShowEpub()

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

    private fun InputStream.toFile(path: String) {
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

                val intent = Intent(this, EpubListActivity::class.java)
                startActivityForResult(intent, 2)

                return false
            }
            R.id.add -> {

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

                return false
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}