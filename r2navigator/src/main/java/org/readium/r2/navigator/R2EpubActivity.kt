package org.readium.r2.navigator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_r2_epub.*

import kotlinx.android.synthetic.main.activity_r2_epub2.*

class R2EpubActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName

    lateinit var publication_path:String
    lateinit var epub_name:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r2_epub2)
        setSupportActionBar(toolbar)
    }
}
