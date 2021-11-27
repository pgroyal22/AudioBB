package edu.temple.audiobb

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService


class MainActivity : AppCompatActivity(), BookListFragment.EventInterface, ControlFragment.EventInterface {
    private val singlePane: Boolean by lazy {
        findViewById<View>(R.id.fragmentContainerView2) == null
    }
    private lateinit var bookObjectViewModel: BookObjectViewModel
    private lateinit var controlFragment: ControlFragment
    private lateinit var bookList : BookList
    private lateinit var searchLauncher : ActivityResultLauncher<Intent>


    // media controller vars
    private var isConnected = false
    private lateinit var mediaControlBinder: PlayerService.MediaControlBinder

    private val progressHandler = Handler(Looper.getMainLooper()) {
        if(it.obj != null) {
            controlFragment.updateProgress((it.obj as PlayerService.BookProgress).progress)
            controlFragment.updateNowPlaying((it.obj as PlayerService.BookProgress).bookId)
            true
        }
        else{
            false
        }
    }

    private val serviceConnection = object : ServiceConnection {
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

        bindService(
            Intent(this, PlayerService::class.java), serviceConnection, BIND_AUTO_CREATE
        )
        bookObjectViewModel = ViewModelProvider(this).get(BookObjectViewModel::class.java)

        bookList = getBookList()



        // activity is being run for the first time
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                .commit()
            controlFragment = ControlFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.controllerFragementContainerView, controlFragment)
                .commit()
        }
        // if single pane and a book has been previously selected, switch to that book detail
        else if (singlePane && bookObjectViewModel.getBookObject().value != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookDetailsFragment.newInstance())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
        else{
            controlFragment = ControlFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.controllerFragementContainerView, controlFragment)
                .commit()
        }

        // if in double pane, we need to make sure the bookdetails fragment is populated
        if (!singlePane && supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) !is BookDetailsFragment) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView2, BookDetailsFragment.newInstance())
                .commit()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
        }

        searchLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.data?.getSerializableExtra("BOOK_LIST") != null) {
                    bookList = it.data?.getSerializableExtra("BOOK_LIST") as BookList
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                        .commit()
                }
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
        bookObjectViewModel.getBookObject().value?.id?.let { mediaControlBinder.play(it) }
        bookObjectViewModel.getBookObject().value?.id?.toString()
            ?.let { Log.d("Started Playing" , it) }
    }

    override fun userChangedProgress(progress: Int) {
        if(bookObjectViewModel.getBookObject().value != null)
        mediaControlBinder.seekTo(progress)
    }

    override fun pause() {
        if(bookObjectViewModel.getBookObject().value != null)
        mediaControlBinder.pause()
    }

    override fun stop() {
        if(bookObjectViewModel.getBookObject().value != null)
        mediaControlBinder.stop()
    }

    override fun launchSearch() {
        val searchIntent = Intent(this, BookSearchActivity::class.java)
        searchLauncher.launch(searchIntent)
    }

}