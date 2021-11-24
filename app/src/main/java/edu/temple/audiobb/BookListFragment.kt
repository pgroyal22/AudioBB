package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BookListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layout : View
    private lateinit var viewModelProvider : ViewModelProvider
    private var bookList : BookList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let{
            bookList = it.getSerializable("BOOK_LIST") as BookList?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        layout = inflater.inflate(R.layout.fragment_book_list, container, false)

        recyclerView = layout.findViewById<RecyclerView>(R.id.bookRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(this.context, 1)
        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelProvider = ViewModelProvider(requireActivity())
        val onClickListener = View.OnClickListener {
            val position = recyclerView.getChildAdapterPosition(it)
            bookList?.let { it1 ->
                viewModelProvider.get(BookObjectViewModel::class.java).setBookObject(
                    it1.get(position))
            }
            (activity as EventInterface).selectionMade()
        }
        recyclerView.adapter = bookList?.let { BookAdapter(it, onClickListener) }
    }
    companion object {
            @JvmStatic
            fun newInstance(bookList: BookList) =
                BookListFragment().apply {
                    this.arguments = Bundle().apply{
                        putSerializable("BOOK_LIST", bookList)
                    }
                }
        }

    interface EventInterface{
        fun selectionMade()
    }
}