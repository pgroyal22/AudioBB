package edu.temple.audiobb

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface {
    var twoPane = false
    lateinit var bookObjectViewModel: BookObjectViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        twoPane = this.findViewById<View>(R.id.fragmentContainerView2) != null
        bookObjectViewModel = ViewModelProvider(this).get(BookObjectViewModel::class.java)


        // Pop DisplayFragment from stack if color was previously selected,
        // but user has since cleared selection
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                    is BookDetailsFragment
            && bookObjectViewModel.getBookObject() == null)
            supportFragmentManager.popBackStack()

        // Remove redundant DisplayFragment if we're moving from single-pane mode
        // (one container) to double pane mode (two containers)
        // and a book has been selected
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) is BookDetailsFragment
            && twoPane)
            supportFragmentManager.popBackStack();

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookListFragment.newInstance())
                .commit()

        if (twoPane) {
            if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) == null)
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView2, BookDetailsFragment())
                    .commit()
            else if (bookObjectViewModel.getBookObject() == null) { // If moving to single-pane
                supportFragmentManager.beginTransaction()                 // but a color was selected
                    .add(
                        R.id.fragmentContainerView2,
                        BookDetailsFragment()
                    )              // before the switch
                    .addToBackStack(null)
                    .commit()
            }
        }
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
        super.onBackPressed()
        bookObjectViewModel.setBookObject(Book("", ""))
    }
}