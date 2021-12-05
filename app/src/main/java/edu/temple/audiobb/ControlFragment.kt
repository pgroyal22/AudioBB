package edu.temple.audiobb

import android.content.Intent
import android.icu.text.CaseMap
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
    private var _seekBar : SeekBar? = null
    private var _nowPlayingTextView : TextView? = null

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
        _seekBar = view.findViewById<SeekBar>(R.id.seekBar)

        _seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    (activity as EventInterface).userChangedProgress(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
        )
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
        _seekBar?.setProgress(progress, true)
    }

    fun setNowPlaying(title: String){
        _nowPlayingTextView?.text = title
    }
}