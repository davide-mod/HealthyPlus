package com.modolo.healthyplus.fitnesstracker

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.fitnesstracker.workout.WorkoutAdapter
import com.modolo.healthyplus.fitnesstracker.workout.WorkoutHistoryAdapter
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import java.time.LocalDateTime

class FitnessTrackerFragment : Fragment(), WorkoutAdapter.WorkoutListener, WorkoutHistoryAdapter.WorkoutHistoryListener{

    private val PREF_NAME = "data"
    private val FIT_TRACK_FIRST = "fit1"

    private val presets = ArrayList<Workout>()
    private val incoming = ArrayList<Workout>()
    private val history = ArrayList<Workout>()

    private lateinit var mAuth: FirebaseAuth


    private lateinit var viewModel: FitnessSharedViewModel
    private var workouts = ArrayList<Workout>()

    private lateinit var presetsView: RecyclerView
    private lateinit var incomingView: RecyclerView
    private lateinit var historyView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fitnesstracker, container, false)
        /*abilito eventualmente il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(true)
        /*recupero il drawer dalla main activity*/
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }
        /*ora recupero le informazioni dell'utente per leggere e salvare le schede di allenamento*/
        mAuth = FirebaseAuth.getInstance()

        /*carico le Recycler dei vari workout (preset, in arrivo e lo storico)*/
        presetsView = view.findViewById(R.id.presetsWorkout)
        incomingView = view.findViewById(R.id.incomingWorkout)
        historyView = view.findViewById(R.id.historyWorkout)

        /*passo al fragment per l'aggiunta del workout*/
        val btnWorkout = view.findViewById<TextView>(R.id.btnWorkout)
        btnWorkout.setOnClickListener {
            btnWorkout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            findNavController().navigate(R.id.addWorkoutFragment)
        }
        return view
    }


    /*viewmodel per comunicare con il database*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*qui eseguo questo codice solo al primo avvio in assoluto della schermata, in quanto il download
        * di eventuale materiale dal server potrebbe impiegare qualche secondo
        * */
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, 0)
        val firstStart = sharedPref?.getBoolean(FIT_TRACK_FIRST, true)
        if (firstStart == true) {
            Snackbar.make(
                view,
                "Scarico eventuali dati dal server...",
                Snackbar.LENGTH_SHORT
            ).show()
            val editor = sharedPref.edit()
            editor?.putBoolean(FIT_TRACK_FIRST, false)
            editor.apply()
        }

        /*istanzio effettivamente la viewmodel per parlare col database e richiedo tutti i workout salvati
        * e li carico nelle varie RecyclerView
        * */
        viewModel = ViewModelProvider(requireActivity()).get(FitnessSharedViewModel::class.java)
        viewModel.workouts.observe(viewLifecycleOwner, { mutableList ->
            workouts= mutableList as ArrayList<Workout>
            presets.clear()
            history.clear()
            incoming.clear()
            for (workout in workouts) {
                when {
                    workout.ispreset -> presets.add(workout)
                    workout.isdone -> history.add(workout)
                    else -> incoming.add(workout)
                }
            }
            /*il caricamento nelle Recycler viene effettuato qui perché altrimenti la prima volta che si
            * accede a questo fragment ogni volta che si avvia l'app gli elementi non apparirebbero in tempo
            * per via delle coroutine
            * */
            val sortedHistory =
                history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
            presetsView.adapter = WorkoutAdapter(presets, this, requireContext())
            incomingView.adapter = WorkoutAdapter(incoming, this, requireContext())
            historyView.adapter =
                WorkoutHistoryAdapter(ArrayList(sortedHistory), this, requireContext())
        })

    }

    /*quando un workout tra i preset o in arrivo viene premuto*/
    override fun onWorkoutListener(workout: Workout, position: Int, editWorkout: Boolean, done: Boolean) {
        /*si controlla se ad essere premuto è stato il pulsante di edit*/
        if (editWorkout) {
            /* in questo caso salvo il workout da modificare nella viewmodel e apro il fragment per la modifica*/
            viewModel.setWorkouttoEdit(workout)
            findNavController().navigate(R.id.editWorkoutFragment)

        } else if (done) { /*se invece è stato premuto il tasto "fatto" lo sposto nello storico*/
            /*rimuovo il workout dal database*/
            viewModel.deleteWorkout(workout)
            /*aggiorno i parametri*/
            workout.isdone = true
            workout.date = LocalDateTime.now().toString()
            /*lo reinserisco nel database*/
            viewModel.insertWorkout(workout)

            /*aggiorno la vista locale*/
            incoming.remove(workout)
            history.add(workout)
            val sortedHistory =
                history.sortedByDescending { it.date } /*ordino la lista per avere in cima gli ultimi*/
            incomingView.adapter = WorkoutAdapter(incoming, this, requireContext())
            historyView.adapter =
                WorkoutHistoryAdapter(ArrayList(sortedHistory), this, requireContext())
        }
    }

    /*se viene premuto un elemento nello storico lo mando alla modifica come sopra*/
    override fun onWorkoutHistoryListener(workout: Workout, position: Int, editMeal: Boolean) {
        viewModel.setWorkouttoEdit(workout)
        findNavController().navigate(R.id.editWorkoutFragment)
    }

}