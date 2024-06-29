package com.example.recipesharingappxml.trending

import android.content.ContentValues
import android.content.ContentValues.TAG
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
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.common.RecipeDetails
import com.example.recipesharingappxml.common.RecipeTrend
import com.example.recipesharingappxml.common.getCurrentDay
import com.example.recipesharingappxml.common.getCurrentMonth
import com.example.recipesharingappxml.common.getCurrentWeek
import com.example.recipesharingappxml.data.RecipeData
import com.example.recipesharingappxml.services.RecipesResponse
import com.example.recipesharingappxml.services.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayList


class Home : Fragment(), RecipeTrendAdapter.OnRecipeClickListener {
    private lateinit var allTime : TextView
    private lateinit var month :  TextView
    private lateinit var week : TextView
    private lateinit var today : TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeTrendAdapter
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var formattedDateStr:String

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

        val currentDate = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        formattedDateStr = currentDate.format(formatter).toString()

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
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getAllTime().enqueue(object : Callback<RecipesResponse> {
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadRecipesToday() {
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getByDate(formattedDateStr).enqueue(object : Callback<RecipesResponse> {
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

    private fun loadRecipesMonth() {
        val currentMonth = getCurrentMonth()
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getByMonth(currentMonth.toString()).enqueue(object : Callback<RecipesResponse> {
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

    private fun loadRecipesWeek() {
        val baseUrl = "https://recipe-sharing-backend.onrender.com/"
        val apiService = RetrofitClient.getClient(baseUrl)

        apiService.getByWeek(formattedDateStr).enqueue(object : Callback<RecipesResponse> {
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

    private fun changeTextColor(textView: TextView) {
        // Reset text color for all TextViews
        allTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
        month.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
        week.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))
        today.setTextColor(ContextCompat.getColor(requireContext(), R.color.text))

        // Change text color for the selected TextView
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.main))
    }

    override fun onRecipeClick(recipe: RecipeData) {
        // Handle the click event, navigate to RecipeDetails activity
        val intent = Intent(context, RecipeDetails::class.java)
        // Pass necessary data to the RecipeDetails activity using Intent extras
        intent.putExtra("recipe", recipe)
        startActivity(intent)
    }



}