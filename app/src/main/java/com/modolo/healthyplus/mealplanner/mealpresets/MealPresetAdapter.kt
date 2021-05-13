package com.modolo.healthyplus.mealplanner.mealpresets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.mealdb.Meal

/*adapter per i preset richiamati dal NewMealFragment che riceve la lista dei presets*/
class MealPresetAdapter(
    private val presetList: ArrayList<Meal>,
    private val presetListener: PresetListener
) : RecyclerView.Adapter<MealPresetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        /*il layout comprende solo il nome del pasto in sè*/
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
            /*metto il nome del pasto nel layout e setto il listener per quando viene selezionato l'oggetto*/
            mealName.text = meal.name
            holder.itemView.setOnClickListener {
                presetListener.onPresetListener(meal.name, meal.foodList, holder.layoutPosition, false)
            }
            /*ho impostato anche un "long click listener" per future implementazioni*/
            holder.itemView.setOnLongClickListener {
                presetListener.onPresetListener(meal.name, meal.foodList, holder.layoutPosition, true)
                true
            }
        }
    }

    interface PresetListener {
        fun onPresetListener(mealName: String, presetFoods: String, position: Int, longpress: Boolean)
    }
}