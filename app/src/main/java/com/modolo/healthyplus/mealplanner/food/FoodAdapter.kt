package com.modolo.healthyplus.mealplanner.food

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R
import java.time.format.DateTimeFormatter

class FoodAdapter(private val foodList: ArrayList<Food>, private val foodListener: FoodListener, private val context: Context) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.name)
        val foodQuantity: TextView = itemView.findViewById(R.id.quantity)
        val foodKcal: TextView = itemView.findViewById(R.id.kcal)
    }

    override fun getItemCount(): Int = foodList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mealplanner_itm_food, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val food = foodList[position]
        with(holder) {
            foodName.text = food.name
            foodQuantity.text = food.quantity.toString() + food.udm
            foodKcal.text = food.kcal.toString()
            holder.itemView.setOnClickListener {
                foodListener.onFoodListener(food, holder.layoutPosition, false)
            }
            holder.itemView.setOnLongClickListener {
                foodListener.onFoodListener(food, holder.layoutPosition, true)
                true
            }
        }
    }


    interface FoodListener {
        fun onFoodListener(food: Food, position: Int, longpress: Boolean)
    }
}