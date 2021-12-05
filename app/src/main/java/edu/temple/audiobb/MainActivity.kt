package edu.temple.audiobb

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.service.controls.Control
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.temple.audlibplayer.PlayerService


class MainActivity : AppCompatActivity(), BookListFragment.EventInterface, ControlFragment.EventInterface {
    private val singlePane: Boolean by lazy {
        findViewById<View>(R.id.fragmentContainerView2) == null
    }

    private lateinit  var serviceIntent : Intent

    // view models
    private lateinit var selectedBookViewModel: SelectedBookViewModel
    private lateinit var playingBookViewModel: PlayingBookViewModel

    private lateinit var controlFragment: ControlFragment
    private lateinit var bookList : BookList
    private lateinit var searchLauncher : ActivityResultLauncher<Intent>

    // media controller vars
    private var isConnected = false
    private lateinit var mediaControlBinder: PlayerService.MediaControlBinder

    private val progressHandler = Handler(Looper.getMainLooper()) { msg ->
        msg.obj?.let { msgObj ->
            val bookProgressObject = msgObj as PlayerService.BookProgress
            if(playingBookViewModel.getBookObject().value == null){
                val id = bookProgressObject.bookId
                Volley.newRequestQueue(this)
                    .add(JsonObjectRequest(
                        Request.Method.GET,
                        "https://kamorris.com/lab/cis3515/book.php?id=$id",
                        null,
                        { bookJSON ->
                            playingBookViewModel.setBookObject(Book(bookJSON.getString("title"), bookJSON.getString("author"), bookJSON.getInt("id"), bookJSON.getInt("duration"), bookJSON.getString("cover_url")))
                            if (selectedBookViewModel.getBookObject().value == null){
                                selectedBookViewModel.setBookObject(playingBookViewModel.getBookObject().value)
                            }
                        }, {}))
                }
                supportFragmentManager.findFragmentById(R.id.controllerFragmentContainerView)?.run{
                    with (this as ControlFragment){
                        playingBookViewModel.getBookObject().value?.also{
                            updateProgress(((bookProgressObject.progress / it.duration.toFloat()) * 100).toInt())
                        }
                    }
                }
        }
        true
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

        selectedBookViewModel = ViewModelProvider(this).get(SelectedBookViewModel::class.java)
        playingBookViewModel = ViewModelProvider(this).get(PlayingBookViewModel::class.java)

        controlFragment = supportFragmentManager.findFragmentById(R.id.controllerFragmentContainerView) as ControlFragment
        playingBookViewModel.getBookObject().observe(this, {
            controlFragment.setNowPlaying(it.title)
        })


        serviceIntent = Intent(this, PlayerService::class.java)
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)

        bookList = getBookList()

        if(supportFragmentManager.findFragmentById(R.id.fragmentContainerView) is BookDetailsFragment && selectedBookViewModel.getBookObject().value != null){
            supportFragmentManager.popBackStack()
        }

        // activity is being run for the first time
        if (savedInstanceState == null){
            controlFragment = ControlFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainerView, BookListFragment.newInstance(bookList))
                .commit()
        }
        // if single pane and a book has been previously selected, switch to that book detail
        else if (singlePane && selectedBookViewModel.getBookObject().value != null) {
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
        selectedBookViewModel.setBookObject(null)
        super.onBackPressed()
    }

    override fun play() {
        if(isConnected) {
            selectedBookViewModel.getBookObject().value?.id?.let { mediaControlBinder.play(it) }
            playingBookViewModel.setBookObject(selectedBookViewModel.getBookObject().value)
            startService(serviceIntent)
        }
    }

    override fun userChangedProgress(progress: Int) {
        if(isConnected)
        mediaControlBinder.seekTo((playingBookViewModel.getBookObject().value!!.duration * (progress.toFloat() / 100)).toInt())
    }

    override fun pause() {
        if(isConnected)
        mediaControlBinder.pause()
    }

    override fun stop() {
        if(isConnected){
            mediaControlBinder.stop()
            stopService(serviceIntent)
        }
    }

    override fun launchSearch() {
        val searchIntent = Intent(this, BookSearchActivity::class.java)
        searchLauncher.launch(searchIntent)
    }

}