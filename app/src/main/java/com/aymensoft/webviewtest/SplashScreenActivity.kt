package com.aymensoft.webviewtest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.aymensoft.webviewtest.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    private var isPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }

    override fun onResume() {
        super.onResume()
        isPaused = false
        val background: Thread = object : Thread() {
            override fun run() {
                try {
                    while (!isPaused && !checkForInternet(this@SplashScreenActivity) && !checkForServerConnection()) {
                        sleep(3000)
                        requestInternet()
                        Log.e("connexion", "no")
                    }
                    if (!isPaused && checkForInternet(this@SplashScreenActivity) && checkForServerConnection()) {
                        Log.e("connexion", "yes")
                        startActivity(Intent(this@SplashScreenActivity, HomeScreenActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        background.start()
    }

    private fun requestInternet(){
        isPaused = true
        runOnUiThread {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("WebViewTest")
            alertDialog.setMessage("enable internet")
            alertDialog.setNegativeButton("NO"){_,_ ->
                alertDialog.setCancelable(true)
            }
            alertDialog.setPositiveButton("YES"){_,_ ->
                startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
            }
            alertDialog.show()
        }
    }

    override fun onPause() {
        isPaused = true
        super.onPause()
    }

}