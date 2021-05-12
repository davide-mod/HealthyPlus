package com.modolo.healthyplus.fitnesstracker.workoutpresets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout

class WorkoutPresetAdapter(
    private val presetList: ArrayList<Workout>,
    private val presetListener: PresetListener
) : RecyclerView.Adapter<WorkoutPresetAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.workoutName)
    }

    override fun getItemCount(): Int = presetList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fitnesstracker_itm_preset, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout = presetList[position]
        with(holder) {
            workoutName.text = workout.name
            holder.itemView.setOnClickListener {
                presetListener.onPresetListener(workout.name, workout.exerciseList, holder.layoutPosition, false)
            }
            holder.itemView.setOnLongClickListener {
                presetListener.onPresetListener(workout.name, workout.exerciseList, holder.layoutPosition, true)
                true
            }
        }
    }


    interface PresetListener {
        fun onPresetListener(presetName: String, presetExercises: String, position: Int, longpress: Boolean)
    }
}