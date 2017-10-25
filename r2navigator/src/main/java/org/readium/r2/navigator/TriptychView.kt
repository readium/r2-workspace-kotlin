package org.readium.r2.navigator

import android.graphics.Rect
import android.view.View
import android.widget.ScrollView
import org.readium.r2.navigator.Views.*
import kotlin.system.exitProcess

interface TriptychViewDelegate {
    fun triptychView(view: TriptychView, index: Int) : View
}

enum class Views{
    one,
    two,
    many
}

class TriptychView (frame: Rect, viewCount: Int, initialIndex: Int) {

}