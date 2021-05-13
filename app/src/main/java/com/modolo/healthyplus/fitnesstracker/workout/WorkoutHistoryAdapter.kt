package com.modolo.healthyplus.fitnesstracker.workout

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.R
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/*adapter per i gli allenamenti già fatti, con la dicitura "X giorni fa"*/
class WorkoutHistoryAdapter(
    private val workouts: ArrayList<Workout>,
    private val workoutListener: WorkoutHistoryListener,
    private val context: Context
) : RecyclerView.Adapter<WorkoutHistoryAdapter.ViewHolder>() {
    /*dichiaro i campi del layout*/
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val workoutName: TextView = itemView.findViewById(R.id.workoutName)
        val workoutDate: TextView = itemView.findViewById(R.id.workoutDate)
        val workoutDateAgo: TextView = itemView.findViewById(R.id.workoutDateAgo)
        val workoutEdit: ImageView = itemView.findViewById(R.id.workoutEdit)
    }

    override fun getItemCount(): Int = workouts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.fitnesstracker_itm_workouthistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*scorro la lista e modifico gli elementi del layout in base ai dati di ogni allenamento*/
        val workout = workouts[position]
        with(holder) {
            workoutName.text = workout.name
            /*visto che la data è salvata come stringa richiede un passaggio extra*/
            val aLDT = LocalDateTime.parse(workout.date)
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm")
            workoutDate.text = aLDT.format(formatter)
            /*calcolo la differenza di giorni*/
            val daysBetween = Duration.between(aLDT, LocalDateTime.now()).toDays()
            val daysAgo =
                if (daysBetween.toInt() != 1) "$daysBetween giorni fa" else "$daysBetween giorno fa"
            workoutDateAgo.text = daysAgo
            /*imposto i listener*/
            workoutEdit.setOnClickListener {
                workoutEdit.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                workoutListener.onWorkoutHistoryListener(workout, holder.layoutPosition, true)
            }
            holder.itemView.setOnClickListener {
                workoutListener.onWorkoutHistoryListener(workout, holder.layoutPosition, false)
            }
        }
    }

    interface WorkoutHistoryListener {
        fun onWorkoutHistoryListener(workout: Workout, position: Int, editMeal: Boolean)
    }
}