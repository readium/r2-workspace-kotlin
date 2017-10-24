package org.readium.r2.navigator

import android.graphics.Rect
import android.view.View
import android.widget.ScrollView
import org.readium.r2.navigator.Views.*
import kotlin.system.exitProcess

interface TriptychViewDelegate {
    fun triptychView(view: TriptychView, index: Int, location: BinaryLocation) : View
}

enum class Views{
    one,
    two,
    many
}

class TriptychView (frame: Rect, viewCount: Int, initialIndex: Int) {

    private var index: Int
    lateinit private var scrollView: ScrollView
    val viewCount: Int
    var viewNumber: Views = Views.many
    var views: List<myWebView> = mutableListOf()

    private var isAtAnEdge: Boolean

    init {
        if (viewCount < 1 || initialIndex < 0 || initialIndex >= viewCount){
            exitProcess(1)
        }
        index = initialIndex
        isAtAnEdge = ((index == 0) || (index == (viewCount - 1)))
        this.viewCount = viewCount
        //ScrollView properties...
    }

    fun currentView() : myWebView {
        return when (viewNumber){
            one -> views[0]
            two, many -> views[index]
        }
    }

    fun moveTo(nextIndex: Int, id: String? = null){

        // If we stay in the current html document
        val cw  = currentView()
        if (index == nextIndex){
            if (id != null){
                if (id == ""){
                    cw.scrollAt(BinaryLocation.beginning)
                } else {
                    cw.scrollAt(id)
                }
            }
            return
        }

        //Else
        if (index < nextIndex) {
            cw.scrollAt(BinaryLocation.beginning)
        } else {
            cw.scrollAt(BinaryLocation.end)
        }
    }

}