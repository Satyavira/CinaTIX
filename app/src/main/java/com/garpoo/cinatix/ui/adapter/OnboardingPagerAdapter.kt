package com.garpoo.cinatix.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.garpoo.cinatix.ui.activity.OnboardingActivity
import com.garpoo.cinatix.R
import com.garpoo.cinatix.databinding.ActivityOnBoardingPageOneBinding
import com.garpoo.cinatix.databinding.ActivityOnBoardingPageTwoBinding
import com.garpoo.cinatix.databinding.ActivityOnboardPageThreeBinding

class OnboardingPagerAdapter(
    private val context: Context,
    private val items: List<Int>,
    private val onSkipClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class PageOneViewHolder(private val binding: ActivityOnBoardingPageOneBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.nextButton.setOnClickListener {
                moveToNextPage()
            }
            binding.skipButton.setOnClickListener {
                onSkipClicked()
            }
        }
    }

    inner class PageTwoViewHolder(private val binding: ActivityOnBoardingPageTwoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.nextButton.setOnClickListener {
                moveToNextPage()
            }
            binding.skipButton.setOnClickListener {
                onSkipClicked()
            }
        }
    }

    inner class PageThreeViewHolder(private val binding: ActivityOnboardPageThreeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.nextButton.setOnClickListener {
                onSkipClicked()
            }
            binding.skipButton.setOnClickListener {
                onSkipClicked()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.activity_on_boarding_page_one -> {
                val binding = ActivityOnBoardingPageOneBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                PageOneViewHolder(binding)
            }
            R.layout.activity_on_boarding_page_two -> {
                val binding = ActivityOnBoardingPageTwoBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                PageTwoViewHolder(binding)
            }
            R.layout.activity_onboard_page_three -> {
                val binding = ActivityOnboardPageThreeBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                PageThreeViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PageOneViewHolder -> holder.bind()
            is PageTwoViewHolder -> holder.bind()
            is PageThreeViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = items.size

    private fun moveToNextPage() {
        if (context is OnboardingActivity) {
            context.binding.viewPager.currentItem += 1
        }
    }
}
