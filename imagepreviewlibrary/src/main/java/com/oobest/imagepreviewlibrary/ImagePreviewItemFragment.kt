package com.oobest.imagepreviewlibrary

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import it.sephiroth.android.library.imagezoom.ImageViewTouch
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase
import kotlinx.android.synthetic.main.fragment_image_preview_item.*

class ImagePreviewItemFragment : Fragment() {

    private var mListener: OnImagePreviewClickListener? = null

    companion object {
        private const val ARGS_URI = "args_uri"

        fun newInstance(uri: Uri) = ImagePreviewItemFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARGS_URI, uri)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_image_preview_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val uri = arguments!!.getParcelable<Uri>(ARGS_URI)

        video_play_button.visibility = View.GONE

        image_view.apply {
            displayType = ImageViewTouchBase.DisplayType.FIT_TO_SCREEN
            setSingleTapListener {
                mListener?.onClick()
            }
        }

        Glide.with(image_view)
            .load(uri)
            .apply(
                RequestOptions()
                    .priority(Priority.HIGH)
                    .fitCenter()
            )
            .into(image_view)
    }

    fun resetView() {
        view?.findViewById<ImageViewTouch>(R.id.image_view)?.resetMatrix()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnImagePreviewClickListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mListener = null
    }
}