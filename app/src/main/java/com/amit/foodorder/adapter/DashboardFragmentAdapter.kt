package com.amit.foodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.amit.foodorder.R
import com.amit.foodorder.model.Restaurant
import com.squareup.picasso.Picasso

class DashboardFragmentAdapter(val context: Context, var itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<DashboardFragmentAdapter.ViewHolderDashboard>() {
    class ViewHolderDashboard(view: View) : RecyclerView.ViewHolder(view) {
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurantHome)
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantNameHome)
        val txtPrice: TextView = view.findViewById(R.id.txtPriceHome)
        val txtRating: TextView = view.findViewById(R.id.txtRatingHome)
        val clkContent: RelativeLayout = view.findViewById(R.id.clkLayout)
        //val txtFavourite:TextView=view.findViewById(R.id.txtFavoriteHome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderDashboard {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_recycler_view_single_row, parent, false)
        return ViewHolderDashboard(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolderDashboard, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtPrice.text = restaurant.cost_for_one
        holder.txtRating.text = restaurant.restaurantRating

        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_default_restraunt)
            .into(holder.imgRestaurant)

        holder.clkContent.setOnClickListener {
            Toast.makeText(context, "Clicked on item", Toast.LENGTH_SHORT).show()
        }
    }

}