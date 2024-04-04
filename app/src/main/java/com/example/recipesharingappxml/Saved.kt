package com.example.recipesharingappxml

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
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
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
        val uid = pref.getString("userid", "") ?: ""

        if (uid.isNotBlank()) {
            firestore.collection("Users").document(uid).collection("UserSavedRecipes")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val savedRecipeIds = ArrayList<String>()
                    for (document in querySnapshot.documents) {
                        val recipeId = document.id // Get the recipe ID from the document ID
                        savedRecipeIds.add(recipeId)
                    }

                    // Retrieve the recipes corresponding to the saved recipe IDs
                    firestore.collection("Recipes")
                        .whereIn(FieldPath.documentId(), savedRecipeIds)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            val recipes = ArrayList<Recipe>()
                            for (document in querySnapshot.documents) {
                                val recipeData = document.data
                                // Safely extract recipe data and handle null values
                                val recipe = Recipe(
                                    document.id, // Use the document ID as the recipe ID
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
                            }
                            recipeAdapter.setRecipeList(recipes)
                            progressBar.visibility = View.GONE
                        }
                        .addOnFailureListener { exception ->
                            // Handle any errors in fetching recipes
                            Log.e(TAG, "Error fetching recipes: ", exception)
                            Toast.makeText(requireContext(), "Error fetching recipes", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors in fetching saved recipe IDs
                    Log.e(TAG, "Error fetching saved recipe IDs: ", exception)
                    Toast.makeText(requireContext(), "Error fetching saved recipe IDs", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                }
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


