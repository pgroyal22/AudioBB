package edu.temple.audiobb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter (bookObjects : BookList, ocl : View.OnClickListener) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>(){

    private val bookObjects = bookObjects
    val ocl = ocl

    class BookViewHolder(view: View, ocl : View.OnClickListener) : RecyclerView.ViewHolder(view){
        // constructor applies onClickListener to the imageView held in the holder
        lateinit var titleTextView : TextView
        lateinit var authorTextView : TextView
        init {
            view.setOnClickListener(ocl)
            titleTextView = view.findViewById<TextView>(R.id.textView)
            authorTextView = view.findViewById<TextView>(R.id.textView2)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent, false)
        // calls BookViewHolder constructor that applies the ocl to the view constructed above

        return BookViewHolder(view, ocl);
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        // binder only binds the image to the imageView, improves previous implementation by not rebinding ocl each time view is recycled
        holder.authorTextView.text = bookObjects.get(position).author
        holder.titleTextView.text = bookObjects.get(position).title
    }

    override fun getItemCount(): Int {
        return bookObjects.size()
    }
}