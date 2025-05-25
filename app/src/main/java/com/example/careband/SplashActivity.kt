package com.example.careband

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12+ 스플래시 화면
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val splashLogo = findViewById<ImageView>(R.id.splash_logo)
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        splashLogo.startAnimation(fadeInAnimation)

        splashLogo.postDelayed({
            val user = FirebaseAuth.getInstance().currentUser
            val nextIntent = Intent(this, MainActivity::class.java)

            startActivity(nextIntent)
            finish()
        }, 2000)
    }
}
