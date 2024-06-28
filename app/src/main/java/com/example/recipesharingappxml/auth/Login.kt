package com.example.recipesharingappxml.auth

import android.annotation.SuppressLint
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
import com.example.recipesharingappxml.services.LoginRequest
import com.example.recipesharingappxml.services.LoginResponse
import com.example.recipesharingappxml.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var login: Button
    private lateinit var LtoR: TextView
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login = findViewById(R.id.loginbtn)
        LtoR = findViewById(R.id.LtoR)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.ccpassword)
        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBarL)
        progressBar.visibility = View.INVISIBLE

        val intent2 = Intent(this, Register::class.java)
        LtoR.setOnClickListener {
            startActivity(intent2)
            finish()
        }

        login.setOnClickListener {
            login()
        }

        val arrowBack: ImageButton = findViewById(R.id.backArrowLogin)
        arrowBack.setOnClickListener {
            finish()
        }
    }

    private fun login() {
        progressBar.visibility = View.VISIBLE
        val emailText = email.text.toString()
        val passText = pass.text.toString()

        if (emailText.isBlank() || passText.isBlank()) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
            return
        }

        val baseUrl = "http://10.0.2.2:8080/"
        val loginRequest = LoginRequest(emailText, passText)
        Log.d("Login", "Request Body: $loginRequest")
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()?.data
                    Log.d("Login", "User Response: ${response.body()}")
                    user?.let {
                        saveUserData(it)
                        Toast.makeText(this@Login, "Welcome ${it.username}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Login, MainActivity::class.java))
                        finish()
                    }
                } else {
                    handleErrorResponse(response)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
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
        Log.d("Login", "Response Code: ${response.code()}")
        Log.d("Login", "Response Message: ${response.message()}")
        response.errorBody()?.string()?.let { Log.d("Login", "Error Body: $it") }
        Toast.makeText(this, "Account doesn't exist! Please create one.", Toast.LENGTH_SHORT).show()
    }

    private fun handleError(t: Throwable) {
        Log.e("Login", "Login failed", t)
        Toast.makeText(this, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
    }
}
