package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookObjectViewModel : ViewModel() {
    private var bookObjectLiveData = MutableLiveData<Book>()
    fun getBookObject() : LiveData<Book>{
        return bookObjectLiveData
    }

    fun setBookObject(bookObject : Book){
        bookObjectLiveData.value = bookObject
    }
}