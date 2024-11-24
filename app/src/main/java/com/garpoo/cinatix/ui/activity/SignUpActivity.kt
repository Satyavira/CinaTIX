package com.garpoo.cinatix.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.garpoo.cinatix.databinding.ActivitySignUpBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: TextInputEditText
    private lateinit var new_password: TextInputEditText
    private lateinit var retype_password: TextInputEditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener{
            val name = binding.yourName.text.toString()
            val email = binding.yourEmail.text.toString()
            val password = binding.newPassword.text.toString()
            val retype = binding.retypePassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && retype.isNotEmpty() && name.isNotEmpty()){
                if (password == retype){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{ it1 ->
                        if (it1.isSuccessful){
                            firebaseAuth.currentUser?.let {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()

                                it.updateProfile(profileUpdates)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val intent = Intent(this, SignInActivity::class.java)
                                            startActivity(intent)
                                            Log.d("Firebase", "User profile updated.")
                                            finish()
                                        } else {
                                            Log.e("Firebase", "Profile update failed", task.exception)
                                            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(this, it1.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password does not matched", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
