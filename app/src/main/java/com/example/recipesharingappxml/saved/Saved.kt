package com.example.recipesharingappxml.saved

import android.content.ContentValues.TAG
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.common.Recipe
import com.example.recipesharingappxml.common.RecipeAdapter
import com.example.recipesharingappxml.common.RecipeDetails
import com.example.recipesharingappxml.data.RecipeData
import com.example.recipesharingappxml.services.RecipesResponse
import com.example.recipesharingappxml.services.RetrofitClient
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class Saved : Fragment(), RecipeAdapter.OnRecipeClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)

        recyclerView = view.findViewById(R.id.recipeViewS)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeAdapter(ArrayList(), requireContext(), this)
        recyclerView.adapter = recipeAdapter

        progressBar = view.findViewById(R.id.progressBarS)
        progressBar.visibility = View.VISIBLE

        // Load recipes from Firestore and set them to the adapter
        loadSavedRecipes()

        return view
    }

    // Function to load recipes from Firestore
    private fun loadSavedRecipes() {
        val pref: SharedPreferences = requireContext().getSharedPreferences("login", MODE_PRIVATE)
        val uid = pref.getString("email", "") ?: ""

        val baseUrl = "http://10.0.2.2:8080/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getSavedRecipes(uid).enqueue(object : Callback<RecipesResponse> {
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



    override fun onRecipeClick(recipe: RecipeData) {
        // Handle the click event, navigate to RecipeDetails activity
        val intent = Intent(context, RecipeDetails::class.java)
        // Pass necessary data to the RecipeDetails activity using Intent extras
        intent.putExtra("recipe", recipe)
        startActivity(intent)
    }
}


