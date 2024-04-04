package com.example.recipesharingappxml

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

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
    private lateinit var firestore : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
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

        firestore = FirebaseFirestore.getInstance()


        val recipe: Parcelable? = intent.getParcelableExtra("recipe")
        var recipeId : String =""
// Check the type of the received parcelable
        when (recipe) {
            is Recipe -> {
                // Populate views with Recipe details
                recipeId = recipe.recipeId
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
            is RecipeTrend -> {
                // Populate views with RecipeTrend details
                recipeId = recipe.recipeId
                title.text = recipe.title
                userName.text = recipe.userName
                type.text = recipe.type
                tags.text = recipe.tags
                ingredients.text = recipe.ingredients
                steps.text = recipe.steps
                Glide.with(this)
                    .load(recipe.imageUrl)
                    .into(recipeImage)

                // Handle other properties specific to RecipeTrend
            }
//
            else -> {
                // Handle other cases if needed
            }
        }



        back.setOnClickListener(View.OnClickListener {
            finish()
        })

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

        save.setOnClickListener {
            val pref: SharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
            val uid = pref.getString("userid","").toString()
            val userSavedRecipesRef = firestore.collection("Users").document(uid).collection("UserSavedRecipes").document(recipeId)

            if (isSaved) {
                // If already saved, remove the recipe from the UserSavedRecipes collection
                userSavedRecipesRef.delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Recipe Unsaved!!", Toast.LENGTH_SHORT).show()
                        save.setImageResource(R.drawable.outline_bookmarks_24)
                        isSaved = false
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            } else {
                // If not saved, add the recipe to the UserSavedRecipes collection
                userSavedRecipesRef.set(mapOf("saved" to true))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Recipe Saved!!", Toast.LENGTH_SHORT).show()
                        save.setImageResource(R.drawable.baseline_bookmarks_24)
                        isSaved = true
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            }
        }




    }
}