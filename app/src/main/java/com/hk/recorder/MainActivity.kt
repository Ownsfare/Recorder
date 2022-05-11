package com.hk.recorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.hk.recorder.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var recorder: MediaRecorder? = null
    private var player: MediaPlayer? = null
    private var permissionToRecordAccepted = false
    private lateinit var fileName: String
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this, permissions,200)

//        var fileName = Environment.getExternalStorageDirectory().toString()+"/myrec.3gp"

        var fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/myrec.3gp";

        binding.recordBtn.setOnClickListener{
            startRec()
        }
        binding.stop.setOnClickListener{
           stopRec()
        }
        binding.playBtn.setOnClickListener{
            play()
        }


    }
    private fun stopRec() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
    }


    private fun startRec() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(fileName)


            try {
                prepare()
            } catch (e: IOException) {
                Log.e(Companion.LOG_TAG, "prepare() failed")
            }

            start()
            Toast.makeText(this@MainActivity,"Recording started",Toast.LENGTH_SHORT).show()
        }
    }
    private fun play() {
        player = MediaPlayer().apply {
            try {
                setDataSource(fileName)
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
}