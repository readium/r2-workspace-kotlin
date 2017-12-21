package org.readium.r2.testapp

import android.app.Activity
import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_epub_list.*
import java.io.File


class EpubListActivity : ListActivity() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_epub_list)
        val r2test_path = Environment.getExternalStorageDirectory().path + "/r2test/"
        val listOfFiles = File(r2test_path).listFiles()
        for (i in listOfFiles.indices) {
            Log.d(TAG, "FileName:" + listOfFiles[i].name)
        }

        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfFiles)

        list.setOnItemClickListener { parent, view, position, id ->

            val intent = Intent()
            intent.putExtra("name", listOfFiles[position].name)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }

}
