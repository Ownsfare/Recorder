package com.hk.recorder

import android.Manifest
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chibde.visualizer.LineBarVisualizer
import com.hk.recorder.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var permissionToRecordAccepted = false
    private lateinit var fileName: String
    private lateinit var lineBarVisualizer: LineBarVisualizer
    private lateinit var visualizerView: VisualizerView

    private lateinit var handler: Handler
    public val REPEAT_INTERVAL = 60     //40
    private var isRecording = false
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.playBtn.isEnabled = true

        visualizerView = findViewById(R.id.visualizerLineBar1)

        ActivityCompat.requestPermissions(this, permissions, 200)

        handler = Handler()
//        fileName = Environment.getExternalStorageDirectory().toString() + "/test1.3gp"
        fileName = Environment.getExternalStorageDirectory().absolutePath
        fileName += "/test1.3gp"
        Log.d("FileName", "is $fileName")
        binding.recordBtn.setOnClickListener {
            startRec()
        }
        binding.stop.setOnClickListener {
            stopRec()
        }
        binding.playBtn.setOnClickListener {
            lineBarVisualization2(it)
            play()
            binding.playBtn.isEnabled = false
        }


    }

    private fun stopRec() {

//        recorder?.stop()
//        recorder?.release()
        try {
            isRecording = false // stop recording
            handler.removeCallbacks(updateVisualizer)
            visualizerView.clear()
            recorder?.stop()
            recorder?.release()

        } catch (e: IOException) {
            Log.e(Companion.LOG_TAG , "prepare() failed")
        }

        recorder = null
        Toast.makeText(this@MainActivity, "Recording stopped", Toast.LENGTH_SHORT).show()
    }


    private fun startRec() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(getRecordingFilePath())
        }
        try {
            recorder?.prepare()
        } catch (e: IOException) {
            Log.e("Media", e.toString())
        }

        recorder?.start()
        isRecording = true
        handler.post(updateVisualizer)
        Toast.makeText(this@MainActivity, "Recording started", Toast.LENGTH_SHORT).show()
    }

    private fun play() {
        player = MediaPlayer().apply {
            try {
                setDataSource(getRecordingFilePath())
                prepare()
                start()
            } catch (e: IOException) {
                Log.e(LOG_TAG, "prepare() failed")
            }
        }
        lineBarVisualizer.setPlayer(player!!.audioSessionId)
    }

    companion object {
        private const val LOG_TAG = "Audio"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == 200) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    private fun getRecordingFilePath(): String {
        var contextWrapper: ContextWrapper = ContextWrapper(applicationContext)
        var music: File? = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        var file: File? = File(music, "testFile4" + ".3gp")

        Log.d("MediaPath", file?.path.toString())
        return file!!.path
    }

    public fun lineBarVisualization2(view: View){
        lineBarVisualizer = findViewById(R.id.visualizerLineBar2)
        lineBarVisualizer.visibility = View.VISIBLE
        lineBarVisualizer.setColor(ContextCompat.getColor(this,R.color.myColor6))
        lineBarVisualizer.setDensity(60F)
//        lineBarVisualizer.setPlayer(player!!.audioSessionId)


    }
//    public fun lineBarVisualization1(view: View){
//        lineBarVisualizer = findViewById(R.id.visualizerLineBar1)
//        lineBarVisualizer.visibility = View.VISIBLE
//        lineBarVisualizer.setColor(ContextCompat.getColor(this,R.color.myColor6))
//        lineBarVisualizer.setDensity(60F)
//        lineBarVisualizer.setPlayer(player!!.audioSessionId)
//
//    }

    var updateVisualizer: Runnable = object : Runnable {
        override fun run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                val x = recorder!!.maxAmplitude
                visualizerView.addAmplitude(x.toFloat()) // update the VisualizeView
                visualizerView.invalidate() // refresh the VisualizerView

                // update in 40 milliseconds
                handler.postDelayed(this, REPEAT_INTERVAL.toLong())
            }
        }
    }
}