package com.example.recipesharingappxml.profile

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.auth.start
import com.example.recipesharingappxml.services.RecipesResponse
import com.example.recipesharingappxml.services.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Profile : Fragment() {

    private lateinit var userName: TextView
    private lateinit var email: TextView
    private lateinit var logout : Button
    private lateinit var edit : Button
    private lateinit var progressBar: ProgressBar
    private lateinit var setting : ImageButton
    //for recycler
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeOwnAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var  userId : String



    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        edit = view.findViewById(R.id.editBtn)
        userName = view.findViewById(R.id.usernameP)
        email = view.findViewById(R.id.emailP)
        logout= view.findViewById(R.id.logoutbtn)
        progressBar = view.findViewById(R.id.progressBarP)
        progressBar.visibility = View.INVISIBLE
        setting = view.findViewById(R.id.settings)

        // Access SharedPreferences using the context of the Fragment
        val pref: SharedPreferences = requireActivity().getSharedPreferences("login", MODE_PRIVATE)
        val savedUserName = pref.getString("username", "")
        val savedEmail = pref.getString("email", "")
        userId = pref.getString("email","").toString()
        // Display the saved data in TextViews
        userName.text = savedUserName
        email.text = savedEmail


        recyclerView = view.findViewById(R.id.recipeViewP)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeOwnAdapter(ArrayList(), requireContext())
        recyclerView.adapter = recipeAdapter


        loadRecipes()


        setting.setOnClickListener(View.OnClickListener {
            val intentStart = Intent(requireContext(), Contact::class.java)
            startActivity(intentStart)
        })


        logout.setOnClickListener(View.OnClickListener {
            progressBar.visibility = View.VISIBLE
            val handler = Handler(Looper.getMainLooper())
            val runnable = Runnable{
                val editor :SharedPreferences.Editor = pref.edit()
                editor.putBoolean("flag",false)
                editor.apply()
                progressBar.visibility = View.GONE
                val intentStart = Intent(requireContext(), start::class.java)
                startActivity(intentStart)

            }
            handler.postDelayed(runnable,2000)

        })
        edit.setOnClickListener(View.OnClickListener {

        })

        return view
    }


    private fun loadRecipes() {
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getUserRecipes(userId).enqueue(object : Callback<RecipesResponse> {
            override fun onResponse(call: Call<RecipesResponse>, response: Response<RecipesResponse>) {
                if (response.isSuccessful) {
                    val recipesResponse = response.body()
                    val recipes = recipesResponse?.data ?: emptyList()
                    recipeAdapter.setRecipeList(recipes)
                    progressBar.visibility = View.GONE
                } else {
                    Log.e(TAG, "Failed to fetch recipes: ${response.message()}")
                    progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<RecipesResponse>, t: Throwable) {
                Log.e(TAG, "Error fetching recipes", t)
                progressBar.visibility = View.GONE
            }
        })
    }

}
