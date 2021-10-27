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
    private lateinit var bookObjects : BookList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookObjects = getBookList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
            viewModelProvider.get(BookObjectViewModel::class.java).setBookObject(bookObjects.get(position))
            (activity as EventInterface).selectionMade()
        }
        recyclerView.adapter = BookAdapter(bookObjects, onClickListener)
    }
    companion object {
            @JvmStatic
            fun newInstance(): BookListFragment {
                return BookListFragment()
            }
        }
    fun getBookList(): BookList{
        return BookList(arrayOf(Book("A Tale of Two Cities", "Charles Dickens"),
            Book("Cat's Cradle", "Kurt Vonnegut"),
            Book("The Scarlet Letter", "Nathaniel Hawthorne"),
            Book("Crime and  Punishment", "Fyodor Dostoevsky"),
            Book("Fahrenheit 451", "Ray Bradbury"),
            Book("War of the  Worlds", "H.G. Wells"),
            Book("Nineteen Eighty-Four", "George Orwell"),
            Book("Adventures of Huckleberry Finn", "Mark Twain"),
            Book("Slaughterhouse-Five", "Kurt Vonnegut"),
            Book("Welcome to the Monkey House", "Kurt Vonnegut")))
    }
    interface EventInterface{
        fun selectionMade()
    }
}