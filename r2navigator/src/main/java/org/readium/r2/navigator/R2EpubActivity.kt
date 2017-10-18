package org.readium.r2.navigator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.activity_r2_epub.*
import org.readium.r2.shared.Publication

class R2EpubActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName
    lateinit var publication_path:String
    lateinit var epub_name:String
    var publication: Publication? = null
    var spineItem = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r2_epub)

        val urlString = intent.getStringExtra("url")
        publication_path = intent.getStringExtra("publication_path")
        epub_name = intent.getStringExtra("epub_name")
        publication = intent.getSerializableExtra("publication") as Publication

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
    fun scrollRight(){
        if (spineItem < publication!!.spine.size - 1 && !webView.canScrollHorizontally(1)) {
            load(URL + "/" + epub_name + publication!!.spine.get(spineItem + 1).href, false)
            spineItem++
        } else {
            webView.scrollX = webView.scrollX + webView.width + 2
        }
    }

    @android.webkit.JavascriptInterface
    fun scrollLeft(){
        if (spineItem > 0 && !webView.canScrollHorizontally(-1)) {
            load(URL + "/" + epub_name + publication!!.spine.get(spineItem - 1).href, true)
            spineItem--
        } else {
            webView.scrollX = webView.scrollX - (webView.width + 2)
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
                intent.putExtra("publication", publication)
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
