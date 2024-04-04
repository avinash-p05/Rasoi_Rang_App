package com.example.recipesharingappxml

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class Login : AppCompatActivity() {

    private lateinit var  login : Button
    private lateinit var LtoR : TextView
    private lateinit var email : EditText
    private lateinit var pass : EditText
    private lateinit var auth : FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var progessBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        login = findViewById(R.id.loginbtn)
        LtoR = findViewById(R.id.LtoR)
        email = findViewById(R.id.email)
        pass = findViewById(R.id.ccpassword)
        auth = FirebaseAuth.getInstance()
        progessBar = findViewById(R.id.progressBarL)
        progessBar.visibility  = View.INVISIBLE

        val intent2 = Intent(this,Register::class.java)
        LtoR.setOnClickListener(View.OnClickListener {
            startActivity(intent2)
            finish()
        })

        login.setOnClickListener(View.OnClickListener {
            login()
        })

        val arrowBack : ImageButton = findViewById(R.id.backArrowLogin)
        arrowBack.setOnClickListener(View.OnClickListener {
            finish()
        })


    }

    fun login(){
        progessBar.visibility = View.VISIBLE
        val email = email.text.toString()
        val pass = pass.text.toString()

        if(email.isBlank() || pass.isBlank()){
            Toast.makeText(this, "Please fill the fields!!", Toast.LENGTH_SHORT).show()
            progessBar.visibility = View.GONE
            return
        }

        auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this) {
            if(it.isSuccessful){

                val uid = auth.currentUser?.uid?:""
                val user = saveData(uid)
                Toast.makeText(this, "Welcome Chef. $user", Toast.LENGTH_SHORT).show()
                val intent1 = Intent(this,MainActivity::class.java)
                startActivity(intent1)
                finish()
            }
            else{
                Toast.makeText(this, "Account Doesn't exists!! Create one ", Toast.LENGTH_SHORT).show()
            }
            progessBar.visibility = View.GONE
        }

    }

    private fun saveData(uid: String?) : String{
        val firestore = FirebaseFirestore.getInstance()
        val userRef = uid?.let { firestore.collection("Users").document(uid).collection("UserDetails").document("UserData") }
        var name = ""
        userRef?.get()
            ?.addOnSuccessListener { document ->
                val email = document?.getString("email")
                val userName = document?.getString("username")
                name = userName.toString()
                if (userName != null && email != null) {
                    UserData(uid,email,userName)
                }

                val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.putBoolean("flag", true)
                editor.putString("userid", uid)
                editor.putString("username", userName)
                editor.putString("email",email)
                editor.apply()
            }
            ?.addOnFailureListener { exception ->
                // Handle failure to retrieve user data from Firestore
            }
        return name
    }
}