package org.readium.r2.navigator

import android.app.Fragment
import android.content.Context
import android.webkit.WebView
import android.widget.ScrollView

class myWebView : Fragment() {

    lateinit var webView: WebView

    fun initWebView(context: Context){
        webView = WebView(context)
    }

    fun scrollAt(position: Double){

    }

    fun scrollAt(tagId: String){

    }

    fun scrollAt(location: BinaryLocation){

    }
}