package com.modolo.healthyplus.fitnesstracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.fitnesstracker.exercise.Exercise
import com.modolo.healthyplus.fitnesstracker.exercise.ExerciseAdapter
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType

class EditWorkoutFragment : Fragment(), ExerciseAdapter.ExerciseListener {

    lateinit var workoutPassed: Workout
    private var exerciseListTmp = ArrayList<Exercise>()


    private lateinit var recValue: EditText
    private lateinit var exName: TextInputEditText
    private lateinit var exRep: TextInputEditText
    private lateinit var exSerie: TextInputEditText
    private lateinit var exKg: TextInputEditText
    private lateinit var exerciseRecycler: RecyclerView

    private lateinit var workoutName: EditText

    private lateinit var viewModel: FitnessSharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fitnesstracker_frag_edit, container, false)
        /*disabilito il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(false)
        /*inizializzo il componente per modificare il nome dell'allenamento*/
        workoutName = view.findViewById(R.id.title)

        /*recycler dove verrà mostrata la lista di esercizi nella scheda*/
        exerciseRecycler = view.findViewById(R.id.exerciseRecycler)
        /*i campi per l'inserimento del nuovo esercizio sono in un ConstraintLayout apposito*/
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        exName = inputFields.findViewById(R.id.exerciseNameText)
        recValue = inputFields.findViewById(R.id.recValue)
        exRep = inputFields.findViewById(R.id.repText)
        exSerie = inputFields.findViewById(R.id.serieText)
        exKg = inputFields.findViewById(R.id.kgText)

        val addEx = inputFields.findViewById<TextView>(R.id.addBtn)
        addEx.setOnClickListener {
            addEx.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*controllo che ci sia almeno il nome dell'esercizio da aggiungere*/
            if (exName.text.toString() != "") {
                val exNameTmp = exName.text.toString()
                val repTmp =
                    if (exRep.text.toString() != "") exRep.text.toString()
                        .toInt() else 0
                val serieTmp =
                    if (exSerie.text.toString() != "") exSerie.text.toString()
                        .toInt() else 0
                val kgTmp =
                    if (exKg.text.toString() != "") exKg.text.toString()
                        .toInt() else 0
                val recTmp =
                    if (recValue.text.toString() != "") recValue.text.toString()
                        .toInt() else 0
                /*aggiungo l'esercizio alla lista e aggiorno la Recycler che li mostra*/
                exerciseListTmp.add(Exercise(exNameTmp, repTmp, serieTmp, kgTmp, recTmp))
                exerciseRecycler.adapter = ExerciseAdapter(exerciseListTmp, this)
                /*resetto i campi e sposto il focus sul nome*/
                exName.setText("")
                exRep.setText("")
                exSerie.setText("")
                exKg.setText("")
                recValue.setText("")
                exName.requestFocus()
            }
        }

        val delete = view.findViewById<TextView>(R.id.btnDelete)
        delete.setOnClickListener {
            delete.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*mando l'allenamento da cancellare alla viewmodel e torno alla home*/
            viewModel.deleteWorkout(workoutPassed)
            findNavController().navigateUp()
        }

        val save = view.findViewById<TextView>(R.id.btnSave)
        save.setOnClickListener {
            save.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*per salvare le modifiche rimuovo l'allenamento, aggiorno i dati e lo inserisco nuovamente*/
            viewModel.deleteWorkout(workoutPassed)
            workoutPassed.name = workoutName.text.toString()
            workoutPassed.exerciseList = Gson().toJson(exerciseListTmp)
            viewModel.insertWorkout(workoutPassed)
            findNavController().navigateUp()
        }

        /*chiudi quando si preme la X senza salvare*/
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*istanzio la viewmodel e recupero l'allenamento da modificare*/
        viewModel = ViewModelProvider(requireActivity()).get(FitnessSharedViewModel::class.java)
        workoutPassed = viewModel.getWorkouttoEdit()
        Log.i("hp_EditWorkoutFragment", "received: $workoutPassed")
        /*modifico la schermata secondo i dati ricevuti*/
        workoutName.setText(workoutPassed.name)
        exerciseDeserializer(workoutPassed.exerciseList)

    }

    override fun onExerciseListener(exercise: Exercise, position: Int, longpress: Boolean) {
        /*quando un esercizio viene selezionato lo rimuovo dalla lista e metto i suoi dati nel Layout per la modifica*/
        exName.setText(exercise.name)
        exRep.setText(exercise.rep)
        exSerie.setText(exercise.set)
        exKg.setText(exercise.kg)
        recValue.setText(exercise.rec)
        exerciseListTmp.remove(exercise)
        exerciseRecycler.adapter = ExerciseAdapter(exerciseListTmp, this)
        exName.requestFocus()
    }

    private val listTypeExercise: ParameterizedType = Types.newParameterizedType(
        List::class.java, Exercise::class.java
    )

    /*funzione che prende il JSON della lista di esercizi e lo trasforma in una lista di oggetti Exercise*/
    private fun exerciseDeserializer(jsonListOfExercise: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Exercise>> = moshi.adapter(listTypeExercise)
        val exercises: List<Exercise>? = adapter.fromJson(jsonListOfExercise)
        exerciseListTmp = exercises as ArrayList<Exercise>
        exerciseRecycler.adapter = ExerciseAdapter(exercises, this)
    }
}