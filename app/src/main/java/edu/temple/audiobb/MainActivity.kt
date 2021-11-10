package edu.temple.audiobb

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface {
    private val singlePane : Boolean by lazy{
        findViewById<View>(R.id.fragmentContainerView2) == null
    }
    private lateinit var bookList: BookList
    private lateinit var bookObjectViewModel: BookObjectViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bookObjectViewModel = ViewModelProvider(this).get(BookObjectViewModel::class.java)

        var bookList = getBookList()

        // activity is being run for the first time
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                .commit()
        // if single pane and a book has been previously selected, switch to that book detail
        else if(singlePane && bookObjectViewModel.getBookObject().value != null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookDetailsFragment.newInstance())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }

        // if in double pane, we need to make sure the bookdetails fragment is populated
        if (!singlePane && supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) !is BookDetailsFragment){
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView2, BookDetailsFragment.newInstance())
                .commit()
        }

        // handles callback for search activity finishign
        val searchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.data?.getSerializableExtra("BOOK_LIST") != null){
                bookList = it.data?.getSerializableExtra("BOOK_LIST") as BookList
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                    .commit()
            }
        }

        findViewById<Button>(R.id.launchSearchButton).setOnClickListener{
            val searchIntent = Intent(this, BookSearchActivity::class.java)
            searchLauncher.launch(searchIntent)
        }

    }


    private fun getBookList(): BookList {
        return BookList()
    }

    override fun selectionMade() {
        if (singlePane) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookDetailsFragment())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        bookObjectViewModel.setBookObject(null)
        super.onBackPressed()
    }
}