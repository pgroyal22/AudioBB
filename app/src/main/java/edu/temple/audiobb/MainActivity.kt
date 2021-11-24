package edu.temple.audiobb

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService


class MainActivity : AppCompatActivity(), BookListFragment.EventInterface, ControlFragment.EventInterface {
    private val singlePane: Boolean by lazy {
        findViewById<View>(R.id.fragmentContainerView2) == null
    }
    private lateinit var bookObjectViewModel: BookObjectViewModel

    // media controller vars
    private var isConnected = false
    private lateinit var mediaControlBinder: PlayerService.MediaControlBinder

    val progressHandler = Handler(Looper.getMainLooper()) {
        it.what.toInt()
        true
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnected = true
            mediaControlBinder = service as PlayerService.MediaControlBinder
            mediaControlBinder.setProgressHandler(progressHandler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bookObjectViewModel = ViewModelProvider(this).get(BookObjectViewModel::class.java)

        var bookList = getBookList()

        supportFragmentManager.beginTransaction()
            .add(R.id.controllerFragementContainerView, ControlFragment.newInstance())
            .commit()

        // activity is being run for the first time
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                .commit()
        // if single pane and a book has been previously selected, switch to that book detail
        else if (singlePane && bookObjectViewModel.getBookObject().value != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookDetailsFragment.newInstance())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }

        // if in double pane, we need to make sure the bookdetails fragment is populated
        if (!singlePane && supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) !is BookDetailsFragment) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView2, BookDetailsFragment.newInstance())
                .commit()
        }

        // handles callback for search activity finishing
        val searchLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data?.getSerializableExtra("BOOK_LIST") != null) {
                    bookList = it.data?.getSerializableExtra("BOOK_LIST") as BookList
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                        .commit()
                }
            }

        findViewById<Button>(R.id.launchSearchButton).setOnClickListener {
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

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun setBarProgress(progress: Int) {
        Log.d("Progress bar", "changed")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun stop() {
        TODO("Not yet implemented")
    }

}