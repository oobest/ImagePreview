package com.oobest.imagepreviewlibrary

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ImagePagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var items = mutableListOf<Uri>()

    override fun getItem(position: Int): Fragment {
        return ImagePreviewItemFragment.newInstance(items[position])
    }

    override fun getCount() = items.size

    fun addAll(uriList: List<Uri>) {
        items.addAll(uriList)
    }
}