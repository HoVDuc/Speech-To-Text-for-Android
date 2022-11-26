package com.example.speechtotext
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.speech.RecognizerIntent
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*

class SecondActivity : AppCompatActivity() {
    private val REQUEST_CODE_SPEECH_INPUT = 1

    private companion object{
        private const val STORAGE_PERMISSION_CODE = 100
        private const val TAG = "PERMISSION_TAG"
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }


    private fun checkPermission(): Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            Environment.isExternalStorageManager()
        }
        else{
            //Android is below 11(R)
            val write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()){
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (write && read){
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission granted")
                }
                else{
                    Log.d(TAG, "onRequestPermissionsResult: External Storage Permission denied...")
                    toast("External Storage Permission denied...")
                }
            }
        }
    }


    private fun toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val intent = intent
        val textFileName = findViewById<TextView>(R.id.textFileName)

        textFileName.text = intent.getStringExtra("fileName").toString()

        val micIV = findViewById<ImageView>(R.id.idIVMic)

        micIV.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                Locale.getDefault()
            )

            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            try {
                startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT)
            } catch (e: Exception) {
                // on below line we are displaying error message in toast
                Toast
                    .makeText(
                        this@SecondActivity, " " + e.message,
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
        }

        val btnSave = findViewById<ImageView>(R.id.idIVSave)
        btnSave.setOnClickListener {
            var root: String = Environment.getExternalStorageDirectory().toString()
            val fileNameTxt = textFileName.text.toString() + ".txt"
            val contentText =  findViewById<TextView>(R.id.idTVOutput)
            val fileOutputStream: FileOutputStream
            val folder = "$root/${Environment.DIRECTORY_DOWNLOADS}/Speech2Test"
            val myDir = File(folder)
            if (checkPermission()){
                Log.d(TAG, "onCreate: Permission already granted, create folder")
                if(!myDir.exists()) {
                    if (myDir.mkdir()) {
                        println("Directory created successfully")
                    } else {
                        println("Directory created not successfully")
                    }
                }
                val pathFile: File = File(myDir, fileNameTxt)
                try {
                    fileOutputStream = FileOutputStream(pathFile)
                    fileOutputStream.write(contentText.text.toString().toByteArray())
                    Toast.makeText(this, "Save to " + pathFile.toString(), Toast.LENGTH_LONG).show()
                    fileOutputStream.close()
                }
                catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
                catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                Log.d(TAG, "onCreate: Permission was not granted, request")
                requestPermission()
            }
        }
    }

    // on below line we are calling on activity result method.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val outputTV = findViewById<TextView>(R.id.idTVOutput)

        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == AppCompatActivity.RESULT_OK && data != null) {

                val res: ArrayList<String> =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS) as ArrayList<String>

                outputTV.setText(
                    Objects.requireNonNull(res)[0]
                )
            }
        }
    }
}