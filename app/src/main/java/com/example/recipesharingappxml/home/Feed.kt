package com.example.recipesharingappxml.home

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.common.RecipeAdapter
import com.example.recipesharingappxml.common.RecipeDetails
import com.example.recipesharingappxml.data.RecipeData
import com.example.recipesharingappxml.services.RecipesResponse
import com.example.recipesharingappxml.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Feed : Fragment(), RecipeAdapter.OnRecipeClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        recyclerView = view.findViewById(R.id.recipeView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeAdapter(ArrayList(), requireContext(), this)
        recyclerView.adapter = recipeAdapter

        progressBar = view.findViewById(R.id.progressBarF)
        progressBar.visibility = View.VISIBLE

        // Load recipes from backend API
        loadRecipes()

        return view
    }

    // Function to load recipes from backend API
    private fun loadRecipes() {
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getAllRecipes().enqueue(object : Callback<RecipesResponse> {
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
