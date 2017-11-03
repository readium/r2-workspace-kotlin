package org.readium.r2.navigator

import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.View
import android.view.ViewGroup

class MyPagerAdapter(private val mListViews: MutableList<View>) : PagerAdapter() {

    fun getViews(): MutableList<View> {
        return mListViews
    }

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
