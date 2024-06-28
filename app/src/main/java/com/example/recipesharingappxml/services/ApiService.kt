package com.example.recipesharingappxml.services

import com.example.recipesharingappxml.data.RecipeData
import com.example.recipesharingappxml.data.UserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.time.Month
import java.util.Date


interface ApiService {
    @POST("auth/login")
    fun loginUser(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun registerUser(@Body registerRequest: RegisterRequest):Call<RegisterResponse>

    @POST("recipe/upload")
    fun uploadRecipe(@Body uploadRequest: UploadRequest): Call<UploadResponse>

    @GET("recipe/allRecipes")
    fun getAllRecipes(): Call<RecipesResponse>

    @GET("recipe/getAllTime")
    fun getAllTime(): Call<RecipesResponse>

    @GET("recipe/userRecipes/{email}")
    fun getUserRecipes(@Path("email") email: String): Call<RecipesResponse>

    @GET("recipe/getByMonth/{month}")
    fun getByMonth(@Path("month") month: String): Call<RecipesResponse>

    @GET("recipe/getByWeek/{week}")
    fun getByWeek(@Path("week") week: String): Call<RecipesResponse>

    @GET("recipe/getByDate/{date}")
    fun getByDate(@Path("date") date: String): Call<RecipesResponse>

    @PUT("recipe/saveRecipe/{email}/{recipeId}")
    fun saveRecipe(@Path("email") email: String,@Path("recipeId") recipeId:String): Call<Map<String, Any>>

    @GET("recipe/savedRecipes/{email}")
    fun getSavedRecipes(@Path("email") email: String): Call<RecipesResponse>
}



data class LoginRequest(val email: String, val password: String)

data class UploadRequest(
    val title:String,
    val type:String,
    val ingredients:String,
    val steps:String,
    val tags:String,
    val saves:Int,
    val imageUrl:String,
    val date : String,
    val userEmail : String,
    val userName : String
    )

data class RegisterRequest(val email: String, val username:String ,val password: String)



data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: UserData?
)

data class UploadResponse(
    val success: Boolean,
    val message: String,
    val data: RecipeData
)

data class RecipesResponse(
    val success: Boolean,
    val message: String,
    val data: List<RecipeData>
)


//{
//    "success": true,
//    "message": "Login successful",
//    "data": {
//    "email": "user@gmail.com",
//    "username": "Avinash Pauskar",
//    "password": "12345",
//    "savedRecipes": null,
//    "postedRecipes": null
//}
//}

//{
//    "title": "Butter Chicken",
//    "type": "Indian",
//    "ingredients": "Chicken, butter, cream, tomatoes, onion, garlic, ginger, spices",
//    "steps": "1. Marinate chicken. 2. Cook onion, garlic, ginger. 3. Add tomatoes and spices. 4. Add chicken, butter, and cream.",
//    "tags": "chicken, main course",
//    "saves": 0,
//    "imageUrl": "http://example.com/butterchicken.jpg",
//    "date": "2023-06-27T00:00:00.000Z",
//    "userEmail": "test@gmail.com",
//    "userName":"Shreya Sutar"
//}