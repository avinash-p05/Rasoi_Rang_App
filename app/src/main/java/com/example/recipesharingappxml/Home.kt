package com.example.recipesharingappxml

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDate
import java.util.ArrayList


class Home : Fragment(),RecipeTrendAdapter.OnRecipeClickListener {
    private lateinit var allTime : TextView
    private lateinit var month :  TextView
    private lateinit var week : TextView
    private lateinit var today : TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeTrendAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        allTime = view.findViewById(R.id.allTimeTextView)
        month = view.findViewById(R.id.thisMonthTextView)
        week= view.findViewById(R.id.thisWeekTextView)
        today = view.findViewById(R.id.todayTextView)
        progressBar = view.findViewById(R.id.progressBarT)
        progressBar.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.recipeViewT)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recipeAdapter = RecipeTrendAdapter(ArrayList(), requireContext(),this)
        recyclerView.adapter = recipeAdapter

        changeTextColor(allTime)
        loadRecipesAll()

        allTime.setOnClickListener(View.OnClickListener {
            changeTextColor(allTime)
            progressBar.visibility = View.VISIBLE
            loadRecipesAll()
        })

        month.setOnClickListener(View.OnClickListener {
            changeTextColor(month)
            progressBar.visibility = View.VISIBLE
            loadRecipesMonth()
        })

        week.setOnClickListener(View.OnClickListener {
            changeTextColor(week)
            progressBar.visibility = View.VISIBLE
            loadRecipesWeek()
        })

        today.setOnClickListener(View.OnClickListener {
            changeTextColor(today)
            progressBar.visibility = View.VISIBLE
            loadRecipesToday()
        })



        return view
    }
    private fun loadRecipesAll() {
        firestore.collection("Recipes")
            .orderBy("saves", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = ArrayList<RecipeTrend>()
                for (document in querySnapshot.documents) {
                    val recipeData = document.data
                        // Safely extract recipe data and handle null values
                    val recipe = RecipeTrend(
                        document.id as? String ?: "",
                        recipeData?.get("userName") as? String ?: "",
                        recipeData?.get("title") as? String ?: "",
                        recipeData?.get("ingredients") as? String ?: "",
                        recipeData?.get("steps") as? String ?: "",
                        recipeData?.get("imageUrl") as? String ?: "",
                        recipeData?.get("type") as? String ?: "",
                        recipeData?.get("tags") as? String ?: "",
                        recipeData?.get("date") as? String ?: "",
                        recipeData?.get("saves") as? String ?:""
                    )
                        recipes.add(recipe)
                    recipeAdapter.setRecipeList(recipes)
                }
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(ContentValues.TAG, "Error fetching recipes: ", exception)
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadRecipesToday() {
        val todayDate = getCurrentDay()
        firestore.collection("Recipes")
            .orderBy("saves", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = ArrayList<RecipeTrend>()
                for (document in querySnapshot.documents) {
                    val recipeData = document.data
                    // Safely extract recipe data and handle null values
                    if (recipeData?.get("day") as String == todayDate.toString()) {
                        val recipe = RecipeTrend(
                            document.id as? String ?: "",
                            recipeData?.get("userName") as? String ?: "",
                            recipeData?.get("title") as? String ?: "",
                            recipeData?.get("ingredients") as? String ?: "",
                            recipeData?.get("steps") as? String ?: "",
                            recipeData?.get("imageUrl") as? String ?: "",
                            recipeData?.get("type") as? String ?: "",
                            recipeData?.get("tags") as? String ?: "",
                            recipeData?.get("date") as? String ?: "",
                            recipeData?.get("saves") as? String ?:""
                        )
                        recipes.add(recipe)
                    }
                }
                recipeAdapter.setRecipeList(recipes)
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(ContentValues.TAG, "Error fetching recipes: ", exception)
            }
    }

    private fun loadRecipesMonth() {
        val currentMonth = getCurrentMonth()
        firestore.collection("Recipes")
            .orderBy("saves", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = ArrayList<RecipeTrend>()
                for (document in querySnapshot.documents) {
                    val recipeData = document.data
                    if (recipeData?.get("month") as String == currentMonth.toString()) {
                        val recipe = RecipeTrend(
                            document.id as? String ?: "",
                            recipeData?.get("userName") as? String ?: "",
                            recipeData?.get("title") as? String ?: "",
                            recipeData?.get("ingredients") as? String ?: "",
                            recipeData?.get("steps") as? String ?: "",
                            recipeData?.get("imageUrl") as? String ?: "",
                            recipeData?.get("type") as? String ?: "",
                            recipeData?.get("tags") as? String ?: "",
                            recipeData?.get("date") as? String ?: "",
                            recipeData?.get("saves") as? String ?:""
                        )
                        recipes.add(recipe)
                    }
                }
                recipeAdapter.setRecipeList(recipes)
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(ContentValues.TAG, "Error fetching recipes: ", exception)
            }
    }

    private fun loadRecipesWeek() {
        val currentWeek = getCurrentWeek()
        firestore.collection("Recipes")
            .orderBy("saves", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val recipes = ArrayList<RecipeTrend>()
                for (document in querySnapshot.documents) {
                    val recipeData = document.data
                    if (recipeData?.get("week") as String == currentWeek.toString()) {
                        val recipe = RecipeTrend(
                            document.id as? String ?: "",
                            recipeData?.get("userName") as? String ?: "",
                            recipeData?.get("title") as? String ?: "",
                            recipeData?.get("ingredients") as? String ?: "",
                            recipeData?.get("steps") as? String ?: "",
                            recipeData?.get("imageUrl") as? String ?: "",
                            recipeData?.get("type") as? String ?: "",
                            recipeData?.get("tags") as? String ?: "",
                            recipeData?.get("date") as? String ?: "",
                            recipeData?.get("saves") as? String ?:""
                        )
                        recipes.add(recipe)
                    }
                }
                recipeAdapter.setRecipeList(recipes)
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { exception ->
                // Handle any errors
                Log.e(ContentValues.TAG, "Error fetching recipes: ", exception)
            }
    }

    private fun changeTextColor(textView: TextView) {
        // Reset text color for all TextViews
        allTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
        month.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
        week.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
        today.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))

        // Change text color for the selected TextView
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.main))
    }

    override fun onRecipeClick(recipe: RecipeTrend) {
        // Handle the click event, navigate to RecipeDetails activity
        val intent = Intent(context, RecipeDetails::class.java)
        // Pass necessary data to the RecipeDetails activity using Intent extras
        intent.putExtra("recipe", recipe)
        startActivity(intent)
    }


}