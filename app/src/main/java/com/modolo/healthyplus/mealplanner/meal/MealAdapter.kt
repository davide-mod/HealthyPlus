package com.modolo.healthyplus.mealplanner.meal

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*adapter per i pasti "in arrivo" e "preset" nella schermata principale del modulo*/
class MealAdapter(private val meals: ArrayList<Meal>, private val mealListener: MealListener, private val context: Context) : RecyclerView.Adapter<MealAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mealName: TextView = itemView.findViewById(R.id.mealName)
        val mealDate: TextView = itemView.findViewById(R.id.mealDate)
        val mealDone: ImageView = itemView.findViewById(R.id.mealDone)
        val mealEdit: ImageView = itemView.findViewById(R.id.mealEdit)
    }

    override fun getItemCount(): Int = meals.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.mealplanner_itm_meal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meal = meals[position]
        with(holder) {
            /*per non effettuare un ulteriore adapter extra vado a modificare il layout da codice:
            * se il campo "ispreset" è true, vado a togliere il tasto di "mangiato" e rimuovo la data
            * altrimenti rendo il tutto visibile */
            mealName.text = meal.name
            val aLDT = LocalDateTime.parse(meal.date)
            val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM")
            mealDate.text = aLDT.format(formatter)
            if (meal.ispreset) {
                mealDone.isVisible = false
                mealDate.isVisible = false
                mealEdit.background = getDrawable(context, R.drawable.btn_end)
            } else {
                mealDone.isVisible = true
                mealDate.isVisible = true
                mealEdit.background = getDrawable(context, R.drawable.btn_bottom_right)
            }

            /*qui c'è il listener nel caso venga premuto il tasto di edit, il tasto di "mangiato" e il pasto in sè*/
            mealEdit.setOnClickListener {
                mealEdit.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                mealListener.onMealListener(meal, holder.layoutPosition, editMeal = true, done = false)
            }
            mealDone.setOnClickListener {
                mealDone.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                mealListener.onMealListener(meal, holder.layoutPosition, editMeal = false, done = true)
            }
            holder.itemView.setOnClickListener {
                mealListener.onMealListener(meal, holder.layoutPosition, editMeal = false, done = false)
            }
        }
    }


    interface MealListener {
        fun onMealListener(meal: Meal, position: Int, editMeal: Boolean, done: Boolean)
    }
}