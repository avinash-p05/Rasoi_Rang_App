package com.example.recipesharingappxml.trending

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipesharingappxml.R
import com.example.recipesharingappxml.common.RecipeTrend
import com.example.recipesharingappxml.data.RecipeData

class RecipeTrendAdapter(private var recipeList: List<RecipeData>, private val context: Context, private val onRecipeClickListener: OnRecipeClickListener) :
    RecyclerView.Adapter<RecipeTrendAdapter.RecipeViewHolder>() {

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val titleTextView: TextView = itemView.findViewById(R.id.titleT)
        val postBy: TextView = itemView.findViewById(R.id.postBy)
        val imageView: ImageView = itemView.findViewById(R.id.recipeImageT)
        val saves : TextView = itemView.findViewById(R.id.savesT)


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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.trendingrecipe, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentItem = recipeList[position]
        holder.titleTextView.text = currentItem.title
        holder.postBy.text = "Posted by - "+currentItem.userName
        holder.saves.text = currentItem.saves.toString()+"k+ Saves"
        Glide.with(context)
            .load(currentItem.imageUrl)
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount() = recipeList.size

    fun setRecipeList(recipes: List<RecipeData>) {
        recipeList = recipes
        notifyDataSetChanged()
    }

    interface OnRecipeClickListener {
        fun onRecipeClick(recipe: RecipeData)
    }
}


