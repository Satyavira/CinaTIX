package com.garpoo.cinatix

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.garpoo.cinatix.databinding.ActivityOnBoardingPageTwoBinding

class OnBoardingPageTwoActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnBoardingPageTwoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnBoardingPageTwoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.nextButton.setOnClickListener {
            val intent = Intent(this, OnboardPageThreeActivity::class.java)
            startActivity(intent)
        }
        binding.skipButton.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }
}