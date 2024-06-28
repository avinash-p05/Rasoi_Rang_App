package com.example.recipesharingappxml.common

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.data.RecipeData
import com.example.recipesharingappxml.services.RecipesResponse
import com.example.recipesharingappxml.services.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetails : AppCompatActivity() {

    private lateinit var title:TextView
    private lateinit var userName : TextView
    private lateinit var type : TextView
    private lateinit var tags : TextView
    private lateinit var ingredients : TextView
    private lateinit var steps :  TextView
    private lateinit var like : ImageButton
    private lateinit var save : ImageButton
    private lateinit var back : ImageButton
    private lateinit var recipeImage : ImageView
    private lateinit var UserEmail : String
    private lateinit var recipeId:String
    private var isLiked = false
    private var isSaved= false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        title = findViewById(R.id.titleD)
        userName = findViewById(R.id.usernameD)
        type = findViewById(R.id.typeD)
        tags = findViewById(R.id.tagsD)
        ingredients = findViewById(R.id.ingredientsD)
        steps = findViewById(R.id.StepsD)
        like = findViewById(R.id.like)
        save = findViewById(R.id.saveD)
        back = findViewById(R.id.backArrowDetials)
        recipeImage = findViewById(R.id.imageViewD)


        val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        UserEmail = pref.getString("email", "").toString()


        val recipe: RecipeData? = intent.getParcelableExtra("recipe")
        recipe?.let { displayRecipeDetails(it) }



        back.setOnClickListener(View.OnClickListener {
            finish()
        })


        save.setOnClickListener {
            if (isSaved) {
                // If already liked, change to the unliked state
                save.setImageResource(R.drawable.outline_bookmarks_24)
                isSaved = false
            } else {
                // If not liked, change to the liked state
                saveRecipe(UserEmail,recipeId)
                save.setImageResource(R.drawable.baseline_bookmarks_24)
                isSaved = true
            }
        }

        like.setOnClickListener {
            if (isLiked) {
                // If already liked, change to the unliked state
                like.setImageResource(R.drawable.outline_thumb_up_off_alt_24)
                isLiked = false
            } else {
                // If not liked, change to the liked state
                like.setImageResource(R.drawable.baseline_thumb_up_24)
                isLiked = true
            }
        }

    }

    private fun saveRecipe(email: String, recipeId: String) {
        val baseUrl = "http://10.0.2.2:8080/"
        val apiService = RetrofitClient.getClient(baseUrl)
        Log.d("RecipeDetails", "Making request to save recipe: $apiService")

        apiService.saveRecipe(email, recipeId).enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        val success = responseBody["success"] as Boolean
                        val message = responseBody["message"] as String
                        Log.d("RecipeDetails", "Response: $message")
                        Toast.makeText(this@RecipeDetails, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("RecipeDetails", "Error response: ${response.errorBody()?.string()}")
                    Toast.makeText(this@RecipeDetails, "Recipe Already Saved!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                Log.e("RecipeDetails", "Request failed", t)
                Toast.makeText(this@RecipeDetails, "An error occurred: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun displayRecipeDetails(recipe: RecipeData) {
            recipeId = recipe.id
            title.text = recipe.title
            userName.text = recipe.userName
            type.text = recipe.type
            tags.text = recipe.tags
            ingredients.text = recipe.ingredients
            steps.text = recipe.steps

            Glide.with(this)
                .load(recipe.imageUrl)
                .into(recipeImage)

    }
}