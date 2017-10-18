package org.readium.r2.testapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Layout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import com.fasterxml.jackson.databind.ext.Java7Support
import kotlinx.android.synthetic.main.activity_web_view.*
import org.readium.r2.shared.Publication
import org.readium.r2.streamer.Parser.EpubParser
import java.net.URL

class WebViewActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName
    var maxWidth = 0
    lateinit var publication_path:String
    lateinit var epub_name:String
    var publication: Publication? = null
    var spineItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val urlString = intent.getStringExtra("url")
        publication_path = intent.getStringExtra("publication_path")
        epub_name = intent.getStringExtra("epub_name")
        publication = EpubParser().parse(publication_path)?.publication

        Log.d(TAG, urlString)
        Log.d(TAG, publication_path)
        Log.d(TAG, epub_name)

        // TODO : prevent vertical scrolling
        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = false
        webView.addJavascriptInterface(this, "Android")



        webView.loadUrl(urlString)
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        }

        val webView = webView
        webView.isVerticalScrollBarEnabled = false
    }

    fun load(url: String, goToEnd: Boolean){
        runOnUiThread(Runnable {
            webView.loadUrl(url)
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    view.loadUrl(request.url.toString())
                    return false
                }
            }
        })
    }

    @android.webkit.JavascriptInterface
    fun scrollRight(result: Int){
        if (spineItem < publication!!.spine.size - 1 && !webView.canScrollHorizontally(1)) {
            load(URL + "/" + epub_name + publication!!.spine.get(spineItem + 1).href, false)
            spineItem++
        } else {
            webView.scrollX = webView.scrollX + webView.width
        }
    }

    @android.webkit.JavascriptInterface
    fun scrollLeft(result: Int){
        if (spineItem > 0 && !webView.canScrollHorizontally(-1)) {
            load(URL + "/" + epub_name + publication!!.spine.get(spineItem - 1).href, true)
            spineItem--
        } else {
            webView.scrollX = webView.scrollX - webView.width
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toc, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.toc -> {

                val intent = Intent(this, TOCActivity::class.java)
                intent.putExtra("publication_path", publication_path)
                intent.putExtra("epub_name", epub_name)
                startActivityForResult(intent, 2)

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            var spine_item_uri: String? = null
            if (data != null) {
                spine_item_uri = data.getStringExtra("spine_item_uri")
                webView.loadUrl(spine_item_uri)
                webView.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                        view.loadUrl(request.url.toString())
                        return false
                    }
                }
            }
        }
    }

}
