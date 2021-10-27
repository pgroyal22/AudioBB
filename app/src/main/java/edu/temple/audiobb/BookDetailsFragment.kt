package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class BookDetailsFragment : Fragment() {

    lateinit var  layoutView : View
    lateinit var  titleTextView: TextView
    lateinit var  authorTextView: TextView
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
    }
}