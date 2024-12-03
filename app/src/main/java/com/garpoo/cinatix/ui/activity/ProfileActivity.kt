package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.garpoo.cinatix.R
import com.garpoo.cinatix.databinding.ActivityProfileBinding
import com.garpoo.cinatix.utils.BottomNavigationHandler
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.tvUserName.text = firebaseAuth.currentUser?.displayName ?: "Anonymous"
        binding.llFavorite.setOnClickListener {
            val intent = Intent(this@ProfileActivity, WishlistMovieActivity::class.java)
            startActivity(intent)
        }
        binding.llLogOut.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this@ProfileActivity, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.bottomNavigation.selectedItemId = R.id.nav_profile
        BottomNavigationHandler.handleNavigation(this@ProfileActivity,binding.bottomNavigation)
    }

    override fun onRestart() {
        super.onRestart()
        binding.bottomNavigation.selectedItemId = R.id.nav_profile
    }
}