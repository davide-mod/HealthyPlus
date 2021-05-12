package com.modolo.healthyplus.fitnesstracker

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.fitnesstracker.workout.WorkoutAdapter
import com.modolo.healthyplus.fitnesstracker.workout.WorkoutHistoryAdapter
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import java.time.LocalDateTime

class FitnessTrackerFragment : Fragment(), WorkoutAdapter.WorkoutListener, WorkoutHistoryAdapter.WorkoutHistoryListener{

    private val presets = ArrayList<Workout>()
    private val incoming = ArrayList<Workout>()
    private val history = ArrayList<Workout>()

    private lateinit var mAuth: FirebaseAuth


    private lateinit var viewModel: FitnessSharedViewModel
    private var workouts = ArrayList<Workout>()

    lateinit var presetsView: RecyclerView
    lateinit var incomingView: RecyclerView
    lateinit var historyView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fitnesstracker, container, false)

        //richiamo l'hamburger menu
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }
        //prendo la sessione del login per poi salvare i pasti all'utente corrente
        mAuth = FirebaseAuth.getInstance()

        //carico le Recycler dei vari pasti (preset, in arrivo e lo storico)
        presetsView = view.findViewById(R.id.presetsWorkout)
        incomingView = view.findViewById(R.id.incomingWorkout)
        historyView = view.findViewById(R.id.historyWorkout)

        //passo al fragment per l'aggiunta del pasto
        val btnWorkout = view.findViewById<TextView>(R.id.btnWorkout)
        btnWorkout.setOnClickListener {
            btnWorkout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            findNavController().navigate(R.id.addWorkoutFragment)
        }
        return view
    }


    //viewmodel per comunicare tra fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Snackbar.make(
            view,
            "Sincronizzo online...",
            Snackbar.LENGTH_SHORT
        ).show()
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
            val sortedHistory =
                history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
            presetsView.adapter = WorkoutAdapter(presets, this, requireContext())
            incomingView.adapter = WorkoutAdapter(incoming, this, requireContext())
            historyView.adapter =
                WorkoutHistoryAdapter(ArrayList(sortedHistory), this, requireContext())
        })

    }

    //quando un pasto tra i preset o quelli in arrivo viene premuto
    override fun onWorkoutListener(workout: Workout, position: Int, editWorkout: Boolean, done: Boolean) {
        //si controlla se ad essere premuto è stato il pulsante di edit
        if (editWorkout) {
            //salvo il pasto da modificare nella viewmodel e apro il fragment per la modifica
            viewModel.setWorkouttoEdit(workout)
            findNavController().navigate(R.id.editWorkoutFragment)

        } else if (done) { //se invece è stato premuto il tasto "fatto" lo sposto nello storico
            viewModel.deleteWorkout(workout)
            workout.isdone = true
            workout.date = LocalDateTime.now().toString()
            viewModel.insertWorkout(workout)

            incoming.remove(workout)
            history.add(workout)
            val sortedHistory =
                history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
            incomingView.adapter = WorkoutAdapter(incoming, this, requireContext())
            historyView.adapter =
                WorkoutHistoryAdapter(ArrayList(sortedHistory), this, requireContext())
        }
    }

    override fun onWorkoutHistoryListener(workout: Workout, position: Int, editMeal: Boolean) {
        Log.i("devdebug", "MainFragment: wannaEditHistory ${workout.name} e id ${workout.id}")
        viewModel.setWorkouttoEdit(workout)
        findNavController().navigate(R.id.editWorkoutFragment)
    }

}