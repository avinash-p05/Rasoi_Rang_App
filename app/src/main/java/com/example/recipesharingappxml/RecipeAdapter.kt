package com.example.recipesharingappxml

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.MessageFormat
import java.util.ArrayList

class RecipeAdapter(
    private var recipeList: List<Recipe>,
    private val context: Context,
    private val onRecipeClickListener: OnRecipeClickListener
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val title: TextView = itemView.findViewById(R.id.titleR)
        val userName: TextView = itemView.findViewById(R.id.usernameR)
        val type: TextView = itemView.findViewById(R.id.typeR)
        val tag: TextView = itemView.findViewById(R.id.tagsR)
        val recipeImage : ImageView = itemView.findViewById(R.id.recipeImage2)
        val date : TextView = itemView.findViewById(R.id.dateR)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val recipe = recipeList[position]
                onRecipeClickListener.onRecipeClick(recipe)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.title.text = recipe.title
        holder.userName.text = recipe.userName
        holder.tag.text = recipe.tags
        holder.type.text = recipe.type
        holder.date.text = recipe.date
        // Set recipe image using Glide or Picasso or any other image loading library
        Glide.with(context)
            .load(recipe.imageUrl) // Optional placeholder image // Optional error placeholder image
            .into(holder.recipeImage)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    fun setRecipeList(recipeList: ArrayList<Recipe>) {
        this.recipeList = recipeList
        notifyDataSetChanged()
    }

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: Recipe)
    }
}

