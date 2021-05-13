package com.modolo.healthyplus.fitnesstracker.workout

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
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
/*adapter per gli allenamenti "in arrivo" e "preset" nella schermata principale del modulo*/
class WorkoutAdapter(private val workouts: ArrayList<Workout>, private val workoutListener: WorkoutListener, private val context: Context) : RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.workoutName)
        val workoutDate: TextView = itemView.findViewById(R.id.workoutDate)
        val workoutDone: ImageView = itemView.findViewById(R.id.workoutDone)
        val workoutEdit: ImageView = itemView.findViewById(R.id.workoutEdit)
    }

    override fun getItemCount(): Int = workouts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fitnesstracker_itm_workout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val workout = workouts[position]
        with(holder) {
            /*per non effettuare un ulteriore adapter extra vado a modificare il layout da codice:
            * se il campo "ispreset" è true, vado a togliere il tasto di "fatto" e rimuovo la data
            * altrimenti rendo il tutto visibile*/
            workoutName.text = workout.name
            val aLDT = LocalDateTime.parse(workout.date)
            val formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM")
            workoutDate.text = aLDT.format(formatter)
            if (workout.ispreset) {
                workoutDone.isVisible = false
                workoutDate.isVisible = false
                workoutEdit.background = getDrawable(context, R.drawable.btn_end)
            } else {
                workoutDone.isVisible = true
                workoutDate.isVisible = true
                workoutEdit.background = getDrawable(context, R.drawable.btn_bottom_right)
            }

            /*qui c'è il listener nel caso venga premuto il tasto di edit, il tasto "fatto" o l'allenamento in sè*/
            workoutEdit.setOnClickListener {
                workoutEdit.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                workoutListener.onWorkoutListener(workout, holder.layoutPosition, editWorkout = true, done = false)
            }
            workoutDone.setOnClickListener {
                workoutDone.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                workoutListener.onWorkoutListener(workout, holder.layoutPosition, editWorkout = false, done = true)
            }
            holder.itemView.setOnClickListener {
                workoutListener.onWorkoutListener(workout, holder.layoutPosition, editWorkout = false, done = false)
            }
        }
    }

    interface WorkoutListener {
        fun onWorkoutListener(workout: Workout, position: Int, editWorkout: Boolean, done: Boolean)
    }
}