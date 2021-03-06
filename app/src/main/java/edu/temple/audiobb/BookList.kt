package edu.temple.audiobb

import java.io.Serializable


class BookList : Serializable{
    private val bookList : MutableList<Book> = ArrayList<Book>()

    fun add(book: Book) {
        bookList.add(book)
    }

    fun remove(book: Book){
        bookList.remove(book)
    }

    operator fun get(index: Int) = bookList[index]

    fun size() = bookList.size

    fun removeAll(){
        bookList.removeAll(this.bookList)
    }
}