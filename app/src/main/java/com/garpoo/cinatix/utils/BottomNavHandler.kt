package com.garpoo.cinatix.utils

import android.content.Context
import android.content.Intent
import com.garpoo.cinatix.R
import com.garpoo.cinatix.ui.activity.HomeActivity
import com.garpoo.cinatix.ui.activity.ProfileActivity
import com.garpoo.cinatix.ui.activity.WishlistMovieActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

object BottomNavigationHandler {
    fun handleNavigation(context: Context, bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    context.startActivity(Intent(context, HomeActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                R.id.nav_search -> {
                    true
                }
                R.id.nav_wishlist -> {
                    context.startActivity(Intent(context, WishlistMovieActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                R.id.nav_profile -> {
                    context.startActivity(Intent(context, ProfileActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    true
                }
                else -> false
            }
        }
    }
}