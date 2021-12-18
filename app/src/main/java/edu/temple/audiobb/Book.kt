package edu.temple.audiobb

import java.io.Serializable

data class Book(val title : String, val author : String, val id : Int, val duration: Int, val coverURL : String) : Serializable{
}
