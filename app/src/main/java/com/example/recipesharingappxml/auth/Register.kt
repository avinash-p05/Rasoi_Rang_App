package com.example.recipesharingappxml.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.recipesharingappxml.MainActivity
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.data.UserData
import com.example.recipesharingappxml.services.RegisterRequest
import com.example.recipesharingappxml.services.RegisterResponse
import com.example.recipesharingappxml.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var cpass: EditText
    private lateinit var username: EditText
    private lateinit var register: Button
    private lateinit var RtoL: TextView
    private lateinit var back: ImageButton
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        progressBar = findViewById(R.id.progressBarR)
        register = findViewById(R.id.createBtn)
        RtoL = findViewById(R.id.RtoL)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.password)
        username = findViewById(R.id.username)
        cpass = findViewById(R.id.cpassword)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        back = findViewById(R.id.backArrowRegister)
        progressBar.visibility = View.INVISIBLE

        val intent2 = Intent(this, Login::class.java)
        RtoL.setOnClickListener {
            startActivity(intent2)
            finish()
        }

        register.setOnClickListener {
            register()
        }

        val arrowBack: ImageButton = findViewById(R.id.backArrowRegister)
        arrowBack.setOnClickListener {
            startActivity(intent2)
            finish()
        }
    }

    private fun register() {
        val emailText = email.text.toString()
        val usernameText = username.text.toString()
        val passText = pass.text.toString()
        val cpassText = cpass.text.toString()

        if (emailText.isBlank() || usernameText.isBlank() || passText.isBlank()) {
            Toast.makeText(this, "All Fields are required!", Toast.LENGTH_SHORT).show()
            return
        }
        if (passText.length <= 7) {
            Toast.makeText(this, "Password must be between 8 to 16 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (passText != cpassText) {
            Toast.makeText(this, "Password and Confirm Password do not match!!", Toast.LENGTH_SHORT).show()
            return
        }

        progressBar.visibility = View.VISIBLE
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val registerRequest = RegisterRequest(emailText, usernameText, passText)
        Log.d("Register", "Request Body: $registerRequest")
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.registerUser(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()?.data
                    Log.d("Register", "User Response: ${response.body()}")
                    user?.let {
                        saveUserData(it)
                        Toast.makeText(this@Register, "Welcome ${it.username}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Register, MainActivity::class.java))
                        finish()
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                handleError(t)
            }
        })
    }

    private fun saveUserData(user: UserData) {
        val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        with(pref.edit()) {
            putBoolean("flag", true)
            putString("userid", user.email)
            putString("username", user.username)
            putString("email", user.email)
            apply()
        }
    }

    private fun handleErrorResponse(response: Response<*>) {
        Log.d("Register", "Response Code: ${response.code()}")
        Log.d("Register", "Response Message: ${response.message()}")
        response.errorBody()?.string()?.let { Log.d("Register", "Error Body: $it") }
        Toast.makeText(this, "Registration failed! Please try again.", Toast.LENGTH_SHORT).show()
    }

    private fun handleError(t: Throwable) {
        Log.e("Register", "Registration failed", t)
        Toast.makeText(this, "Registration failed: ${t.message}", Toast.LENGTH_SHORT).show()
    }
}
