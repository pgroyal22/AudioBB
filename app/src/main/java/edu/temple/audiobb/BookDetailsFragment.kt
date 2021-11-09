package edu.temple.audiobb

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley

class BookDetailsFragment : Fragment() {

    lateinit var  layoutView : View
    lateinit var  titleTextView: TextView
    lateinit var  authorTextView: TextView
    lateinit var  coverImageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        layoutView = inflater.inflate(R.layout.fragment_book_details, container, false)
        titleTextView = layoutView.findViewById<TextView>(R.id.detailsTitleText)
        authorTextView = layoutView.findViewById<TextView>(R.id.detailsAuthorText)
        coverImageView =  layoutView.findViewById<ImageView>(R.id.coverImageView)

        ViewModelProvider(requireActivity())
            .get(BookObjectViewModel::class.java)
            .getBookObject().observe(requireActivity(), {
                setSelection(it)
            })

        return layoutView
    }

    companion object {

        @JvmStatic
        fun newInstance(): BookDetailsFragment {
            return (BookDetailsFragment())

        }
    }

    private fun setSelection(book: Book){
        titleTextView.text = book.title
        authorTextView.text = book.author

        if (!this.isDetached)
        {
            val volleyQueue = Volley.newRequestQueue(this.requireContext())
            volleyQueue.add(ImageRequest(
                book.coverURL,
                {
                    coverImageView.setImageBitmap(it)
                },
                0,
                0,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.ARGB_8888,
                {
                    coverImageView.setImageResource(R.drawable.ic_launcher_background)
                }
            ))
        }
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onDetach() {
        viewModelStore.clear()
        super.onDetach()
    }
}