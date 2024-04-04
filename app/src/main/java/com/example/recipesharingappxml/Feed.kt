package com.example.recipesharingappxml

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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.ArrayList

class Feed : Fragment(), RecipeAdapter.OnRecipeClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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

        // Load recipes from Firestore and set them to the adapter
        loadRecipes()

        return view
    }

    // Function to load recipes from Firestore
    private fun loadRecipes() {
        firestore.collection("Recipes")
            .orderBy("date", Query.Direction.DESCENDING) // Assuming "date" is the field storing upload dates
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = ArrayList<Recipe>()
                for (document in querySnapshot.documents) {
                    val recipeData = document.data
                    // Safely extract recipe data and handle null values
                    val recipe = Recipe(
                        document.id as? String ?: "",
                        recipeData?.get("userName") as? String ?: "",
                        recipeData?.get("title") as? String ?: "",
                        recipeData?.get("ingredients") as? String ?: "",
                        recipeData?.get("steps") as? String ?: "",
                        recipeData?.get("imageUrl") as? String ?: "",
                        recipeData?.get("type") as? String ?: "",
                        recipeData?.get("tags") as? String ?: "",
                        recipeData?.get("date") as? String ?: ""
                    )
                    recipes.add(recipe)
                    progressBar.visibility = View.GONE
                }
                recipeAdapter.setRecipeList(recipes)
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(TAG, "Error fetching recipes: ", exception)
            }
    }


    override fun onRecipeClick(recipe: Recipe) {
        // Handle the click event, navigate to RecipeDetails activity
        val intent = Intent(context, RecipeDetails::class.java)
        // Pass necessary data to the RecipeDetails activity using Intent extras
        intent.putExtra("recipe", recipe)
        startActivity(intent)
    }
}


