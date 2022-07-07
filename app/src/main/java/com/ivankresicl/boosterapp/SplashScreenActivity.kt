package com.ivankresicl.boosterapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.ivankresicl.boosterapp.databinding.ActivitySplashScreenBinding
import kotlinx.android.synthetic.main.bottom_sheet.*
import kotlin.concurrent.thread

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashScreenActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        // create a new thread (and start it immediately)
        thread(start=true) {
                Thread.sleep(2000L)
                // assuming you've done the ``findViewById`` and assigned it to a variable
                runOnUiThread {
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
        }
    }
}