package org.readium.r2.navigator

import android.app.Activity
import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_epub_list.*
import org.readium.r2.shared.Publication
import android.view.View
import android.view.ViewGroup
import org.readium.r2.shared.Link
import android.widget.TextView
import kotlinx.android.synthetic.main.toc_item.view.*


class TOCActivity : ListActivity() {

    val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_toc)

        val epub_name = intent.getStringExtra("epub_name")
        val publication_path = intent.getStringExtra("publication_path")
        val publication = intent.getSerializableExtra("publication") as Publication

        listAdapter = TOCAdapter(this, publication.spine)

        list.setOnItemClickListener { _, _, position, _ ->

            val spine_item_uri = URL + "/" + epub_name + publication.spine.get(position).href

            Log.d(TAG, spine_item_uri)

            val intent = Intent()
            intent.putExtra("spine_item_uri", spine_item_uri)
            intent.putExtra("spine_item_index", position)
            setResult(Activity.RESULT_OK, intent)
            finish()

        }
    }

    inner class TOCAdapter(context: Context, users: MutableList<Link>) : ArrayAdapter<Link>(context, R.layout.toc_item, users) {
        private inner class ViewHolder {
            internal var toc_textView: TextView? = null
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var myView = convertView

            val spine_item = getItem(position)

            val viewHolder: ViewHolder // view lookup cache stored in tag
            if (myView == null) {

                viewHolder = ViewHolder()
                val inflater = LayoutInflater.from(context)
                myView = inflater.inflate(R.layout.toc_item, parent, false)
                viewHolder.toc_textView = myView!!.toc_textView as TextView

                myView.tag = viewHolder

            } else {

                viewHolder = myView.tag as ViewHolder
            }

            viewHolder.toc_textView!!.setText(spine_item!!.href)

            return myView
        }
    }
}
