package edu.temple.audiobb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class ControlFragment : Fragment() {
    private lateinit var _seekBar: SeekBar
    private lateinit var _nowPlayingTextView : TextView
    private val volleyQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }
    private var nowPlayingBook : Book? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_control, container, false)

        view.findViewById<Button>(R.id.playButton).setOnClickListener{
            (requireActivity() as EventInterface).play()
        }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener(){
            (requireActivity() as EventInterface).pause()
        }

        view.findViewById<Button>(R.id.stopButton).setOnClickListener{
            (requireActivity() as EventInterface).stop()
        }

        view.findViewById<Button>(R.id.searchButton).setOnClickListener{
            (requireActivity() as EventInterface).launchSearch()
        }

        _nowPlayingTextView = view.findViewById<TextView>(R.id.nowPlayingTextView)

        class MySeekBarChangeListener() : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    _seekBar.setProgress(progress, true)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                (requireActivity() as EventInterface).userChangedProgress(_seekBar.progress)
            }
        }
        _seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        _seekBar.setOnSeekBarChangeListener(MySeekBarChangeListener())

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            ControlFragment().apply {
                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
                }
            }
    }
    interface EventInterface{
        fun play()
        fun pause()
        fun stop()
        fun launchSearch()
        fun userChangedProgress(progress: Int)
    }

    fun updateProgress(progress: Int){
        _seekBar.setProgress(progress, true)
    }

    fun updateNowPlaying(bookID : Int){

        if(nowPlayingBook?.id == bookID){
            return
        }

        Log.d("Book id ", bookID.toString())

        volleyQueue.add(
            JsonObjectRequest(Request.Method.GET
                , "https://kamorris.com/lab/cis3515/book.php?id=$bookID"
                ,null
                , {
                    try {
                        Log.d("Book JSON", it.toString())
                        //  val title : String, val author : String, val id : Int, val duration: Int, val coverURL : String
                        nowPlayingBook = Book(it.getString("title"), it.getString("author"), it.getInt("id"), it.getInt("duration"), it.getString("cover_url"))
                        if(nowPlayingBook != null){
                            _nowPlayingTextView.text = getString(R.string.now_playing_text, nowPlayingBook?.title)
                        }
                    } catch(e: JSONException) {
                        e.printStackTrace()
                    }
                }
                , {
                    it.printStackTrace()
                })
        )
    }
}