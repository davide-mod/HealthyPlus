package com.modolo.healthyplus.mealplanner.presets

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
import com.modolo.healthyplus.mealplanner.Meal
import com.modolo.healthyplus.mealplanner.food.Food
import java.time.format.DateTimeFormatter

class PresetAdapter(
    private val presetList: ArrayList<Meal>,
    private val presetListener: PresetListener,
    private val context: Context
) : RecyclerView.Adapter<PresetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
    }

    override fun getItemCount(): Int = presetList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mealplanner_itm_preset, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = presetList[position]
        with(holder) {
            mealName.text = meal.name
            holder.itemView.setOnClickListener {
                presetListener.onPresetListener(meal.name, meal.foodList, holder.layoutPosition, false)
            }
            holder.itemView.setOnLongClickListener {
                presetListener.onPresetListener(meal.name, meal.foodList, holder.layoutPosition, true)
                true
            }
        }
    }


    interface PresetListener {
        fun onPresetListener(mealName: String, foodListPar :MutableList<Food>, position: Int, longpress: Boolean)
    }
}