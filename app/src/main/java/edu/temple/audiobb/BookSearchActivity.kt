package edu.temple.audiobb

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.ArrayList


class BookSearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        val searchEditText = findViewById<EditText>(R.id.searchEditText)
        val searchButton = findViewById<Button>(R.id.enterSearchButton).setOnClickListener{

            val query = searchEditText.text.toString()

            if(query.isBlank()){
                searchEditText.error = "No search entered"
            }
            else{
                val t = Thread(){
                    val url = URL("https://kamorris.com/lab/cis3515/search.php?term=$query")
                    val reader = BufferedReader(InputStreamReader(url.openStream()))
                    val JSONresponseArray = JSONArray(reader.readLine())

                    var bookList = ArrayList<Book>()
                    for (i in 0 until JSONresponseArray.length()){
                        val bookJSON : JSONObject = JSONresponseArray.getJSONObject(i)
                        bookList.add(Book(bookJSON.getString("title"), bookJSON.getString("author"), bookJSON.getInt("id"), bookJSON.getString("cover_url")))
                    }

                    val resultIntent = Intent().putExtra("BOOK_LIST", BookList(bookList))
                    setResult(Activity.RESULT_OK, resultIntent)

                }
                t.start()
            }
        }
    }
}