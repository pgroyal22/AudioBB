package edu.temple.audiobb

import android.media.metrics.Event
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SeekBar


class ControlFragment : Fragment() {



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
        var view = inflater.inflate(R.layout.fragment_control, container, false)

        view.findViewById<Button>(R.id.playButton).setOnClickListener{
            (requireActivity() as EventInterface).play()
        }
        view.findViewById<Button>(R.id.pauseButton).setOnClickListener(){
            (requireActivity() as EventInterface).pause()
        }

        view.findViewById<Button>(R.id.stopButton).setOnClickListener{
            (requireActivity() as EventInterface).stop()
        }

        class MySeekBarChangeListener(private val eventInterface: EventInterface) : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    (requireActivity() as EventInterface).setBarProgress(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        }
        view.findViewById<SeekBar>(R.id.seekBar)
            .setOnSeekBarChangeListener(MySeekBarChangeListener(requireActivity() as EventInterface))

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
        fun setBarProgress(progress: Int)
    }
}