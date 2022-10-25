package com.ziyad.zcook.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ziyad.zcook.R
import com.ziyad.zcook.databinding.ItemReviewBinding
import com.ziyad.zcook.model.Review
import com.ziyad.zcook.ui.adapter.ReviewAdapter.ReviewViewHolder

class ReviewAdapter(private val items: ArrayList<Review>) :
    RecyclerView.Adapter<ReviewViewHolder>() {
    class ReviewViewHolder(val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ReviewViewHolder(
            ItemReviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = items[position]
        val starDrawable=holder.itemView.context.resources.getDrawable(R.drawable.ic_sharp_star_24)
        val starBorderDrawable=holder.itemView.context.resources.getDrawable(R.drawable.ic_sharp_star_border_24)
        holder.binding.apply {
            tvName.text=review.userName
            when(review.rating){
                1.0->{
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starBorderDrawable)
                    ivStar3.setImageDrawable(starBorderDrawable)
                    ivStar4.setImageDrawable(starBorderDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                2.0->{
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starBorderDrawable)
                    ivStar4.setImageDrawable(starBorderDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                3.0->{
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starDrawable)
                    ivStar4.setImageDrawable(starBorderDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                4.0->{
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starDrawable)
                    ivStar4.setImageDrawable(starDrawable)
                    ivStar5.setImageDrawable(starBorderDrawable)
                }
                5.0->{
                    ivStar1.setImageDrawable(starDrawable)
                    ivStar2.setImageDrawable(starDrawable)
                    ivStar3.setImageDrawable(starDrawable)
                    ivStar4.setImageDrawable(starDrawable)
                    ivStar5.setImageDrawable(starDrawable)
                }
            }
            tvReview.text=review.review
        }
    }

    override fun getItemCount() = items.size
}