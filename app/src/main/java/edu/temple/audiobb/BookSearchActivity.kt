package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable


class BookSearchActivity : AppCompatActivity() {

    companion object{
        var bookList = BookList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        val volleyQueue: RequestQueue by lazy {
            Volley.newRequestQueue(this)
        }

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        findViewById<Button>(R.id.enterSearchButton).setOnClickListener {
            val query = searchEditText.text.toString()

            if (query.isBlank()) {
                searchEditText.error = "No search entered"
            }

            else {
                volleyQueue.add(
                    JsonArrayRequest(Request.Method.GET
                        , "https://kamorris.com/lab/cis3515/search.php?term=$query"
                        ,null
                        , {
                            try {
                                bookList.removeAll()
                                for (i in 0 until it.length()){
                                    val bookJSON : JSONObject = it.getJSONObject(i)
                                    bookList.add(Book(bookJSON.getString("title"), bookJSON.getString("author"), bookJSON.getInt("id"), bookJSON.getInt("duration"), bookJSON.getString("cover_url")))
                                }
                                val resultIntent = Intent().putExtra("BOOK_LIST", bookList)
                                setResult(Activity.RESULT_OK, resultIntent)
                                this.finish()
                            } catch(e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        , {
                            searchEditText.error = "Network Error"
                        }))
            }
        }
    }
}