package com.aymensoft.webviewtest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.aymensoft.webviewtest.databinding.ActivityHomeScreenBinding

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding

    private var isStartLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRefresh.setOnClickListener {
            if (checkForInternet(this)) {
                binding.apply {
                    webView.visibility = View.VISIBLE
                    btnRefresh.visibility = View.INVISIBLE
                }
                loadWeb()
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        registerReceiver(broadcastReceiver, intentFilter)

    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("action", "${checkForInternet(this@HomeScreenActivity)}")
            if (checkForInternet(this@HomeScreenActivity)) {
                loadWeb()
            } else {
                binding.apply {
                    imgSplash.visibility = View.INVISIBLE
                    webView.visibility = View.INVISIBLE
                    btnRefresh.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadWeb() {
        val webSettings = binding.webView.settings
        webSettings.builtInZoomControls = false
        binding.webView.webViewClient = WebClient()
        binding.webView.webChromeClient = MyWebChromeClient()
        try {
            binding.webView.loadData("", "text/html", null)
            binding.webView.loadUrl("https://cp3.tn.oxa.host:2083/")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    inner class WebClient : WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            Log.e("onPageStarted", "yes")
            isStartLoading = true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            Log.e("onPageFinished", "${view!!.contentHeight}")
            if (isStartLoading && view.contentHeight != 0) {
                binding.apply {
                    imgSplash.visibility = View.INVISIBLE
                    btnRefresh.visibility = View.INVISIBLE
                    webView.visibility = View.VISIBLE
                }
            }
        }

        override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
        ) {
            super.onReceivedError(view, request, error)
            Log.e("onPageFinished", "yes")
            binding.apply {
                imgSplash.visibility = View.INVISIBLE
                btnRefresh.visibility = View.VISIBLE
                webView.visibility = View.INVISIBLE
            }
        }
    }

    internal inner class MyWebChromeClient : WebChromeClient() {
        override fun onJsConfirm(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            return super.onJsConfirm(view, url, message, result)
        }

        override fun onJsPrompt(
            view: WebView,
            url: String,
            message: String,
            defaultValue: String,
            result: JsPromptResult
        ): Boolean {
            return super.onJsPrompt(view, url, message, defaultValue, result)
        }

        override fun onJsAlert(
            view: WebView,
            url: String,
            message: String,
            result: JsResult
        ): Boolean {
            result.confirm()
            if (message.equals("exit", ignoreCase = true)) {
                finish()
            } else {
                Log.e("onJsAlert", message)
            }
            return true
        }
    }


    override fun onDestroy() {
        unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

}