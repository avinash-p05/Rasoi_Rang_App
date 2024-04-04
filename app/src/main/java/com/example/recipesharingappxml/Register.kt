package com.example.recipesharingappxml

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.autofill.UserData
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

class Register : AppCompatActivity() {

    private lateinit var email : EditText
    private lateinit var pass : EditText
    private lateinit var cpass : EditText
    private lateinit var username : EditText
    private lateinit var register :  Button
    private lateinit var RtoL : TextView
    private lateinit var back : ImageButton
    private lateinit var auth : FirebaseAuth
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
        auth = Firebase.auth
        firestore = Firebase.firestore
        back = findViewById(R.id.backArrowRegister)
        progressBar.visibility = View.INVISIBLE

        val intent2 = Intent(this,Login::class.java)
        RtoL.setOnClickListener(View.OnClickListener {
            startActivity(intent2)
            finish()
        })


        register.setOnClickListener(View.OnClickListener {
            register()
        })

        val arrowBack : ImageButton = findViewById(R.id.backArrowRegister)
        arrowBack.setOnClickListener(View.OnClickListener {
            startActivity(intent2)
            finish()
        })
    }

    private fun register(){
        val email = email.text.toString()
        val username = username.text.toString()
        val pass= pass.text.toString()
        val cpass = cpass.text.toString()

        if(email.isBlank() ||  username.isBlank() || pass.isBlank()){
            Toast.makeText(this, "All Fields are required! ", Toast.LENGTH_SHORT).show()
            return
        }
        if(pass.length<=7){
            Toast.makeText(this,"Password must be between 8 to 16 characters",Toast.LENGTH_SHORT).show()
            return
        }

        if(pass !=cpass){
            Toast.makeText(this,"Password and Confirm Password do not match!!",Toast.LENGTH_SHORT).show()
            return
        }
        progressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
            if (it.isSuccessful){
                saveUserData(auth.currentUser?.uid?:"",email,username)
                Toast.makeText(this, "Successfully Signed Up ", Toast.LENGTH_SHORT).show()

                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "Account already exists, Login", Toast.LENGTH_SHORT).show()
            }
            progressBar.visibility = View.GONE
        }

    }

    private fun saveUserData(uid: String, email: String, username: String) {
        // Save user details in UserDetails collection
        val userDetails = UserData(uid, email, username)
        val userDetailsRef = firestore.collection("Users").document(uid).collection("UserDetails").document("UserData")

        userDetailsRef.set(userDetails)
            .addOnSuccessListener {
                // User details saved successfully

                val userSavedRecipesRef = firestore.collection("Users").document(uid).collection("UserSavedRecipes")
                userSavedRecipesRef.document("dummy").set(mapOf("dummy" to true))
                    .addOnSuccessListener {
                        // Dummy document added successfully
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure to add dummy document
                    }
                // Save user authentication status in SharedPreferences
                val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.putBoolean("flag", true)
                editor.putString("userid", uid)
                editor.putString("username", username)
                editor.putString("email", email)
                editor.apply()
            }
            .addOnFailureListener { exception ->
                // Handle failure
            }

    }

}