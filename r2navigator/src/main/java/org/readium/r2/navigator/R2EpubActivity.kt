package org.readium.r2.navigator

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_r2_epub.*
import org.readium.r2.shared.Publication

class R2EpubActivity : AppCompatActivity() {

    val TAG = this::class.java.simpleName
    lateinit var publication_path: String
    lateinit var epub_name: String
    var publication: Publication? = null
    lateinit var settings: SharedPreferences
    var myAdapter: MyPagerAdapter? = null
    lateinit var mListViews: MutableList<View>

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

        mListViews = ArrayList<View>()

        // TODO needs real triptych architecture
        for (spine in publication?.spine!!) {
            val spine_item_uri = URL + "/" + epub_name + spine.href
            addView(mListViews, spine_item_uri)
        }

        myAdapter = MyPagerAdapter(mListViews)
        resourcePager.adapter = myAdapter

        triptychLayout.setupWithViewPager(resourcePager);
    }

    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    private fun addView(viewList: MutableList<View>, url: String) {

        val webView = R2WebView(this)

        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = false
        webView.addJavascriptInterface(webView, "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        }
        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                super.onShowCustomView(view, callback)
            }
        }
        webView.loadUrl(url)
        viewList.add(webView)
    }

    internal inner class R2WebView : WebView {

        internal lateinit var context: Context
        private var web = this;

        constructor(context: Context) : super(context) {
            this.context = context
        }

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

        @android.webkit.JavascriptInterface
        fun scrollRight() {
            if (!web.canScrollHorizontally(1)) {
                nextResource()
            } else {
                web.scrollTo(web.scrollX + web.width, 0)
            }
        }

        @android.webkit.JavascriptInterface
        fun scrollLeft() {
            if (!web.canScrollHorizontally(-1)) {
                previousResource()
            } else {
                web.scrollTo(web.scrollX - web.width, 0)
            }
        }

        @android.webkit.JavascriptInterface
        fun CenterTapped() {
            toggleActionBar()
        }

    }


    fun toggleActionBar() {
        runOnUiThread {

            if (supportActionBar!!.isShowing) {
                supportActionBar!!.hide()
            } else {
                supportActionBar!!.show()
            }
        }
    }

    fun nextResource() {
        runOnUiThread {
            resourcePager.setCurrentItem(resourcePager.getCurrentItem() + 1)
        }
    }

    fun previousResource() {
        runOnUiThread {
            resourcePager.setCurrentItem(resourcePager.getCurrentItem() - 1)
        }
    }

    class MyPagerAdapter(private val mListViews: MutableList<View>) : PagerAdapter() {

        override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
            Log.d("k", "destroyItem")
            (container as ViewPager).removeView(mListViews.get(position))
        }

        override fun finishUpdate(container: ViewGroup?) {
            Log.d("k", "finishUpdate")
        }

        override fun getCount(): Int {
            Log.d("k", "getCount")
            return mListViews.size
        }

        override fun instantiateItem(container: ViewGroup?, position: Int): Any {
            Log.d("k", "instantiateItem")
            (container as ViewPager).addView(mListViews.get(position), 0)
            return mListViews.get(position)
        }

        override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
            Log.d("k", "isViewFromObject")
            return arg0 === arg1
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
            R.id.settings -> {
                fragmentManager.beginTransaction().add(R.id.frameLayout, SettingsFragment()).commit()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                val spine_item_index: Int = data.getIntExtra("spine_item_index", 0)
                resourcePager.setCurrentItem(spine_item_index)
            }
        } else if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
        }

    }

}
