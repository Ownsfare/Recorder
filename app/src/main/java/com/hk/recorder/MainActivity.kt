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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.hk.recorder.databinding.ActivityMainBinding
import java.io.File

import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var permissionToRecordAccepted = false
    private lateinit var fileName: String
    private var permissions: Array<String> =
        arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this, permissions, 200)

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
            play()
        }


    }

    private fun stopRec() {
//        recorder?.start()
//        Toast.makeText(this@MainActivity, "Recording started", Toast.LENGTH_SHORT).show()

//        recorder?.stop()
//        recorder?.release()
//        try {
//
//            Log.d("MediaRecorder", recorder.toString())
//            Log.d("MediaRecorder", recorder.toString())
//        } catch (e: IOException) {
//            Log.e(Companion.LOG_TAG, "prepare() failed")
//        }
        recorder = null
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
        Toast.makeText(this@MainActivity, "Recording started", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(
            {
                recorder?.stop()
                recorder?.release()
            }, 5000
        )
        Log.d("MediaRecorder", recorder.toString())
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
}