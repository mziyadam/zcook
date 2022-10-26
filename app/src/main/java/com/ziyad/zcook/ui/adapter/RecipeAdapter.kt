package com.ziyad.zcook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ItemRecipeBinding
import com.ziyad.zcook.model.Recipe
import com.ziyad.zcook.utils.loadImage
import com.ziyad.zcook.utils.toCurrencyFormat

class RecipeAdapter(
    private val items: ArrayList<Recipe>, private val listSavedRecipeId: ArrayList<String>,
    private val onItemClicked: (Recipe) -> Unit, private val onSaveIconClicked: (Recipe) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    class RecipeViewHolder(val binding: ItemRecipeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder =
        RecipeViewHolder(
            ItemRecipeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = items[position]

        // Rating Count
        var rating = 0.0
        var reviewCount = 0
        for (i in recipe.listReview) {
            rating += i.rating
            reviewCount++
        }
        if (reviewCount > 0)
            rating /= reviewCount

        // Drawables for save
        val saveDrawable = holder.itemView.context.resources.getDrawable(R.drawable.ic_bookmark)
        val saveBorderDrawable =
            holder.itemView.context.resources.getDrawable(R.drawable.ic_bookmark_border_white)

        holder.apply {
            itemView.setOnClickListener {
                onItemClicked(recipe)
            }
            binding.apply {
                ivRecipe.loadImage(recipe.imageUrl)
                tvEstimatedPrice.text = "± ${recipe.estimatedPrice.toString().toCurrencyFormat()}"
                tvRating.text = rating.toString()
                tvRecipeName.text = recipe.name
                ivSave.setOnClickListener {
                    onSaveIconClicked(recipe)
                }
                // Check if saved
                // TODO ADD INTENT IN CORRESPONDENT ACTIVITIES
                if (listSavedRecipeId.contains(recipe.id)) {
                    ivSave.setImageDrawable(saveDrawable)
                } else {
                    ivSave.setImageDrawable(saveBorderDrawable)
                }
            }
        }
    }

    override fun getItemCount() = items.size
}