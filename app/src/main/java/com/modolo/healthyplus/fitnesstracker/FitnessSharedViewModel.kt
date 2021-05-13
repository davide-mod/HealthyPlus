package com.modolo.healthyplus.fitnesstracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import com.modolo.healthyplus.fitnesstracker.workoutdb.WorkoutRepository

/*Viewmodel utilizzata dal modulo FitnessTracker per far comunicare i vari fragment e per comunicare col
* database*/
class FitnessSharedViewModel(val app: Application) : AndroidViewModel(app) {
    /*Di seguito ci sono, gli oggetti per l'autenticazione e per comunicare col db locale, la
    * lista di allenamenti "in RAM" e l'oggetto allenamento da modificare*/
    private val mAuth = FirebaseAuth.getInstance()
    private val workoutsdb = WorkoutRepository(app)
    var workouts = MutableLiveData<List<Workout>>()
    private var workoutToEdit: Workout? = null

    /*inizializzo prendendo dal DB locale tutti i pasti e salvandoli localmente*/
    init {
        workoutsdb.getAll()
        workouts = workoutsdb.workoutData
        Log.i("hp_FitnessSharedViewModel", "init: $workouts")
    }

    /*funzione alla quale si passa l'allenamento da modificare*/
    fun setWorkouttoEdit(workout: Workout) {
        workoutToEdit = workout
    }

    /*funzione con la quale si recupera l'allenamento da modificare*/
    fun getWorkouttoEdit(): Workout {
        return workoutToEdit!!
    }

    /*inserimento workout sia nel DB locale che su Firebase*/
    fun insertWorkout(workout: Workout) {
        workoutsdb.insertWorkout(workout)
        DButilFitnessTracker(mAuth, Firebase.firestore).addWorkout(workout)
    }

    /*rimozione allenamento sia dal DB locale che da Firebase*/
    fun deleteWorkout(workout: Workout) {
        workoutsdb.deleteWorkout(workout)
        DButilFitnessTracker(mAuth, Firebase.firestore).deleteWorkout(workout)
    }

    /*calcolo ultimo ID presente e lo restituisco*/
    fun getLastId(): Int {
        var maxId = 0
        workouts.value!!.forEach {
            maxId = if (it.id > maxId) it.id else maxId
        }
        return maxId
    }

    /*restituisce la lista dei allenamenti impostati come preset*/
    fun getPresets(): ArrayList<Workout> {
        val presets = ArrayList<Workout>()
        workouts.value!!.forEach {
            if (it.ispreset)
                presets.add(it)
        }
        return presets
    }
}

