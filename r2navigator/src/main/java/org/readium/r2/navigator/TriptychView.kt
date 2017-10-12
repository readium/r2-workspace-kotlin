package org.readium.r2.navigator

import android.graphics.Rect
import android.view.View
import android.widget.ScrollView
import kotlin.system.exitProcess

interface TriptychViewDelegate {
    fun triptychView(view: TriptychView, index: Int, location: BinaryLocation) : View
}

class TriptychView (frame: Rect, viewCount: Int, initialIndex: Int) {

    class Views(views: List<View>) {

    }

    private var index: Int
    lateinit private var scrollView: ScrollView
    val viewCount: Int
    var views: Views? = null

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

}