package com.example.recipesharingappxml


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.ArrayList

class RecipeOwnAdapter(private var recipeList: List<RecipeOwn>, private val context: Context) :
    RecyclerView.Adapter<RecipeOwnAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleP)
        val type: TextView = itemView.findViewById(R.id.typeP)
        val date : TextView = itemView.findViewById(R.id.dateP)
        val recipeImage : ImageView = itemView.findViewById(R.id.recipeImageP)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipeown, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.title.text = recipe.title
        holder.type.text = recipe.type
        holder.date.text = recipe.date
        // Set recipe image using Glide or Picasso or any other image loading library
        Glide.with(context)
            .load(recipe.image) // Optional placeholder image // Optional error placeholder image
            .into(holder.recipeImage)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun setRecipeList(recipeList: ArrayList<RecipeOwn>) {
        this.recipeList = recipeList
        notifyDataSetChanged()
    }
}
