package com.example.robertstitic.detector

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_more_info.*
import android.webkit.WebView
import android.webkit.WebViewClient

class MoreInfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_info)

        wvMoreInfo.getSettings().setLoadsImagesAutomatically(true);
        wvMoreInfo.getSettings().setJavaScriptEnabled(true);
        wvMoreInfo.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        wvMoreInfo.setWebViewClient(MyBrowser())
        val result = intent.getStringExtra("Title")
        tv_more_info.text = result
        if(result == "dioda") {
            wvMoreInfo.loadUrl("https://hr.wikipedia.org/wiki/Dioda")
        } else if(result == "display") {
            wvMoreInfo.loadUrl("https://hr.wikipedia.org/wiki/Digitalni_displej")
        } else if(result == "baterija") {
            wvMoreInfo.loadUrl("https://hr.wikipedia.org/wiki/Baterija")
        } else if(result == "prototipna plocica") {
            wvMoreInfo.loadUrl("https://en.wikipedia.org/wiki/Breadboard")
        } else if(result == "senzor dima") {
            wvMoreInfo.loadUrl("https://create.arduino.cc/projecthub/Aritro/smoke-detection-using-mq-2-gas-sensor-79c54a")
        } else if(result == "senzor za pracenje linije") {
            wvMoreInfo.loadUrl("https://www.chipoteka.hr/artikl/139851/arduino-kompatibilni-senzor-za-pracenje-linija-velleman-vma326-8090229179")
        } else if(result == "servo motor") {
            wvMoreInfo.loadUrl("https://en.wikipedia.org/wiki/Servomotor")
        } else if(result == "ultrazvucni modul") {
            wvMoreInfo.loadUrl("https://www.itead.cc/wiki/Ultrasonic_Ranging_Module_HC-SR04")
        } else if(result == "zica") {
            wvMoreInfo.loadUrl("https://hr.wikipedia.org/wiki/Žica")
        }
    }
}

private class MyBrowser : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        view.loadUrl(url)
        return true
    }
}
