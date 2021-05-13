package com.modolo.healthyplus.mealplanner.food

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R

/*adapter per la lista di cibi nei fragment di Aggiunta e Modifica pasto*/
class FoodAdapter(private val foodList: List<Food>, private val foodListener: FoodListener) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

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
            val quantity = food.quantity.toString() + food.udm
            foodQuantity.text = quantity
            val kcal = food.kcal.toString() + "kcal"
            foodKcal.text = kcal
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