package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.garpoo.cinatix.R
import com.garpoo.cinatix.ui.adapter.OnboardingPagerAdapter
import com.garpoo.cinatix.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layouts = listOf(
            R.layout.activity_on_boarding_page_one,
            R.layout.activity_on_boarding_page_two,
            R.layout.activity_onboard_page_three
        )

        val adapter = OnboardingPagerAdapter(
            this,
            layouts,
            onSkipClicked = {
                navigateToAnotherActivity()
            }
        )

        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Optionally, update dots or indicators here
            }
        })
    }

    private fun navigateToAnotherActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }
}