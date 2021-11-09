package edu.temple.audiobb

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface {
    private var twoPane = false
    private lateinit var bookList: BookList
    private lateinit var bookObjectViewModel: BookObjectViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        twoPane = this.findViewById<View>(R.id.fragmentContainerView2) != null
        bookObjectViewModel = ViewModelProvider(this).get(BookObjectViewModel::class.java)

        val searchLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            bookList = it.data?.getSerializableExtra("BOOK_LIST") as BookList
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                .commit()
        }

        val launchSearchButton = findViewById<Button>(R.id.launchSearchButton).setOnClickListener{
            val searchIntent = Intent(this, BookSearchActivity::class.java)
            searchLauncher.launch(searchIntent)
        }


        val bookList = getBookList()

        if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) is BookDetailsFragment
            && twoPane)
            supportFragmentManager.popBackStack()

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                .commit()

        if (twoPane) {
            if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) == null)
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView2, BookDetailsFragment())
                    .commit()
        }
    }


    private fun getBookList(): BookList {
        return BookList()
    }

    override fun selectionMade() {
        if (!twoPane)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
        else{
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView2, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onBackPressed() {
        bookObjectViewModel.setBookObject(Book("", "", 0, ""))
        super.onBackPressed()

    }
}