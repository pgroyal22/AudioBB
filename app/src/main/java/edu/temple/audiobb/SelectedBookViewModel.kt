package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SelectedBookViewModel : ViewModel() {
    private var bookObjectLiveData = MutableLiveData<Book>()
    fun getBookObject() : LiveData<Book>{
        return bookObjectLiveData
    }

    fun setBookObject(book : Book?){
        bookObjectLiveData.value = book
    }
}