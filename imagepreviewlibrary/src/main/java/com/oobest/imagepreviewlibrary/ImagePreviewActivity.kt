package com.oobest.imagepreviewlibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_image_preview.*
import java.util.*


/**
 * 图片预览
 */
class ImagePreviewActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    OnImagePreviewClickListener {

    companion object {
        private const val EXTRA_URI = "extra_uri"
        private const val EXTRA_CURRENT_ITEM = "current_item"

        private const val UI_ANIMATION_DELAY = 300

        fun createIntent(packageContext: Context, uriList: ArrayList<Uri>, currentItem: Uri?) =
            Intent(packageContext, ImagePreviewActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_URI, uriList)
                putExtra(EXTRA_CURRENT_ITEM, currentItem)
            }

    }

    private val mHideHandler = Handler()
    private val mHidePart2Runnable = Runnable {
        pager.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }

    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }

    private var mPreviousPos: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)

        mVisible = true

        val adapter = ImagePagerAdapter(supportFragmentManager)
        pager.adapter = adapter
        pager.addOnPageChangeListener(this)

        val uriList = intent.getParcelableArrayListExtra<Uri>(EXTRA_URI)
        uriList?.let { list ->
            adapter.addAll(list)
            val defaultItem = intent.getParcelableExtra<Uri>(EXTRA_CURRENT_ITEM)?.toString()
            if (defaultItem != null) {
                pager.currentItem = list.indexOfFirst { it.toString() == defaultItem }
            } else {
                pager.currentItem = 0
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        delayedHide(UI_ANIMATION_DELAY)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        mVisible = false
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    private fun show() {
        pager.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        mHideHandler.removeCallbacks(mHidePart2Runnable)
    }


    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }


    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        val adapter = pager.adapter as ImagePagerAdapter
        if (mPreviousPos != -1 && mPreviousPos != position) {
            (adapter.instantiateItem(pager, mPreviousPos) as ImagePreviewItemFragment).resetView()
        }
        mPreviousPos = position
    }

    override fun onPageSelected(position: Int) {

    }

    override fun onClick() {
        toggle()
    }
}

/**
 * 预览图片
 */
fun Fragment.previewImage(uriList: ArrayList<Uri>, currentItem: Uri?) {
    val intent = ImagePreviewActivity.createIntent(
        packageContext = requireContext(),
        uriList = uriList, currentItem = currentItem
    )
    startActivity(intent)
}

/**
 * 预览图片
 */
fun Activity.previewImage(uriList: ArrayList<Uri>, currentItem: Uri?) {
    val intent = ImagePreviewActivity.createIntent(
        packageContext = this,
        uriList = uriList, currentItem = currentItem
    )
    startActivity(intent)
}
