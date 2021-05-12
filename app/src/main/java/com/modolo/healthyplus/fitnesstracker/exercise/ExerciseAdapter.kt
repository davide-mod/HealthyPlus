package com.modolo.healthyplus.fitnesstracker.exercise

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.modolo.healthyplus.R
import java.time.format.DateTimeFormatter

class ExerciseAdapter(private val exerciseList: List<Exercise>, private val exerciseListener: ExerciseListener, private val context: Context) : RecyclerView.Adapter<ExerciseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exerciseName: TextView = itemView.findViewById(R.id.name)
        private val infolayout: LinearLayout = itemView.findViewById(R.id.linearExercise)
        val exerciseRep: TextView = infolayout.findViewById(R.id.rep)
        val exerciseSerie: TextView = infolayout.findViewById(R.id.serie)
        val exerciseKg: TextView = infolayout.findViewById(R.id.kg)
        val exerciseRec: TextView = infolayout.findViewById(R.id.rec)

    }

    override fun getItemCount(): Int = exerciseList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fitnesstracker_itm_exercise, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Log.i("devdebug", "FoodAdapter: $foodList")
        val exercise = exerciseList[position]
        with(holder) {
            exerciseName.text = exercise.name
            exerciseRep.text = "rep:\n"+exercise.rep.toString()
            exerciseSerie.text = "serie:\n"+exercise.set.toString()
            exerciseKg.text = "kg:\n"+exercise.kg.toString()
            exerciseRec.text = "rec:\n"+exercise.rec.toString()
            holder.itemView.setOnClickListener {
                exerciseListener.onExerciseListener(exercise, holder.layoutPosition, false)
            }
            holder.itemView.setOnLongClickListener {
                exerciseListener.onExerciseListener(exercise, holder.layoutPosition, true)
                true
            }
        }
    }


    interface ExerciseListener {
        fun onExerciseListener(exercise: Exercise, position: Int, longpress: Boolean)
    }
}