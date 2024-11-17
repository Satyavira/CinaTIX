package com.garpoo.cinatix

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.garpoo.cinatix.databinding.ActivityOnBoardingPageOneBinding

class OnBoardingPageOneActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnBoardingPageOneBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnBoardingPageOneBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            val intent = Intent(this, OnBoardingPageTwoActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.skipButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}