package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlayingBookViewModel : ViewModel() {
    private var bookObjectLiveData = MutableLiveData<Book>()
    private var _progress = 0
    fun getBookObject() : LiveData<Book> {
        return bookObjectLiveData
    }

    fun setBookObject(book : Book?){
        bookObjectLiveData.value = book
    }

    fun saveProgress(progress : Int){
        _progress = progress
    }
    fun getProgress() : Int{
        return _progress
    }
}