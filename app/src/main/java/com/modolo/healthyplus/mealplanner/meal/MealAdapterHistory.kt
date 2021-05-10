package com.modolo.healthyplus.mealplanner.meal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.Meal
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MealAdapterHistory(private val meals: ArrayList<Meal>, private val mealListener: MealHistoryListener, private val context: Context) : RecyclerView.Adapter<MealAdapterHistory.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealDate: TextView = itemView.findViewById(R.id.mealDate)
        val mealDateAgo: TextView = itemView.findViewById(R.id.mealDateAgo)
        val mealEdit: ImageView = itemView.findViewById(R.id.mealEdit)
    }

    override fun getItemCount(): Int = meals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mealplanner_itm_mealhistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = meals[position]
        with(holder) {
            mealName.text = meal.name
            //val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM")
            //mealDate.text = meal.date.format(formatter).toString()
            //val daysBetween = Duration.between(meal.date as LocalDateTime, LocalDateTime.now()).toDays()
            mealDateAgo.text = meal.date

            mealEdit.setOnClickListener {
                mealEdit.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                mealListener.onMealHistoryListener(meal, holder.layoutPosition, true)
            }
            holder.itemView.setOnClickListener {
                mealListener.onMealHistoryListener(meal, holder.layoutPosition, false)
            }
        }
    }


    interface MealHistoryListener {
        fun onMealHistoryListener(meal: Meal, position: Int, editMeal: Boolean)
    }
}