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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import com.modolo.healthyplus.fitnesstracker.workoutpresets.WorkoutPresetAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType
import java.time.LocalDateTime

class AddWorkoutFragment : Fragment(), ExerciseAdapter.ExerciseListener,
    WorkoutPresetAdapter.PresetListener {

    private lateinit var recValue: EditText
    private lateinit var exName: TextInputEditText
    private lateinit var exRep: TextInputEditText
    private lateinit var exSerie: TextInputEditText
    private lateinit var exKg: TextInputEditText
    private lateinit var exList: ArrayList<Exercise>
    private lateinit var exerciseRecycler: RecyclerView

    private var presetList = ArrayList<Workout>()
    private lateinit var presetDialog: Dialog
    private lateinit var workoutName: EditText
    private lateinit var newWorkout: Workout

    private lateinit var viewModel: FitnessSharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fitnesstracker_frag_add, container, false)

        /*disabilito il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(false)

        /*inizializzo il componente per il nome dell'allenamento*/
        workoutName = view.findViewById(R.id.title)

        /*recycler dove verrà mostrata la lista di esercizi*/
        exList = ArrayList()
        /*i campi per l'inserimento del nuovo esercizio sono in un ConstraintLayout apposito*/
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        exName = inputFields.findViewById(R.id.exerciseNameText)
        recValue = inputFields.findViewById(R.id.recValue)
        exRep = inputFields.findViewById(R.id.repText)
        exSerie = inputFields.findViewById(R.id.serieText)
        exKg = inputFields.findViewById(R.id.kgText)
        val addEx = inputFields.findViewById<TextView>(R.id.addBtn)

        exerciseRecycler = view.findViewById(R.id.exerciseRecycler)


        /*aggiungi un nuovo esercizio secondo i parametri inseriti*/
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
                exList.add(Exercise(exNameTmp, repTmp, serieTmp, kgTmp, recTmp))
                exerciseRecycler.adapter = ExerciseAdapter(exList, this)
                /*resetto i campi e sposto il focus sul nome del cibo*/
                exName.setText("")
                exRep.setText("")
                exSerie.setText("")
                exKg.setText("")
                recValue.setText("")
                exName.requestFocus()
            }
        }

        /*quando l'utente ha terminato di inserire gli esercizi si procede*/
        val proceed = view.findViewById<TextView>(R.id.btnProceed)
        proceed.setOnClickListener {
            proceed.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            val workoutNameTmp = workoutName.text.toString()
            if (exList.size > 0 && workoutNameTmp != "") {
                /*se il nostro allenamento ha un nome ed almeno un elemento possiamo salvarlo
                apro il dialog per la scelta*/
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.fitnesstracker_dialog_save)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                /*recupero i vari elementi per poter procedere*/
                val title = dialog.findViewById<TextView>(R.id.title)
                title.text = workoutNameTmp /*imposto il titolo del dialog in base al nome dell'allenamento*/
                /*la lista di esercizi viene salvata come stringa JSON nell'allenamento*/
                val exJson = Gson().toJson(exList)
                val newId = viewModel.getLastId() + 1
                newWorkout = Workout(
                    newId,
                    workoutNameTmp, exJson, LocalDateTime.now().toString(),
                    ispreset = false,
                    isdone = false
                )
                Log.i("hp_AddWorkoutFragment", "new workout ${newWorkout.name} e id ${newWorkout.id}")
                /*se effettuato verrà impostato come "fatto"*/
                val doneWorkout = dialog.findViewById<TextView>(R.id.doneWorkout)
                doneWorkout.setOnClickListener {
                    doneWorkout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    newWorkout.isdone = true
                    Log.i("hp_AddWorkoutFragment", "asDone ${newWorkout.name} e id ${newWorkout.id}")
                    /*passo il nuovo allenamento alla viewmodel che lo aggiungerà sia al db online che locale*/
                    viewModel.insertWorkout(newWorkout)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                /*se si vuole impostarlo come preset si procederà di conseguenza*/
                val aspreset = dialog.findViewById<TextView>(R.id.aspreset)
                aspreset.setOnClickListener {
                    aspreset.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    newWorkout.ispreset = true
                    Log.i("hp_AddWorkoutFragment", "asPreset ${newWorkout.name} e id ${newWorkout.id}")
                    /*passo il nuovo allenamento alla viewmodel che lo aggiungerà sia al db online che locale*/
                    viewModel.insertWorkout(newWorkout)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                /*se si vuole impostarlo come in arrivo si procederà di conseguenza*/
                val schedule = dialog.findViewById<TextView>(R.id.schedule)
                schedule.setOnClickListener {
                    schedule.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    Log.i("hp_AddWorkoutFragment", "asIncoming ${newWorkout.name} e id ${newWorkout.id}")
                    /*passo il nuovo allenamento alla viewmodel che lo aggiungerà sia al db online che locale*/
                    viewModel.insertWorkout(newWorkout)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                dialog.show()
            } else {/*sono necessari almeno un elemento e il nome*/
                Toast.makeText(
                    requireContext(),
                    "Inserisci il nome ed almeno un elemento",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*scelta di un eventuale preset*/
        val savedWorkouts = view.findViewById<TextView>(R.id.btnSavedWorkouts)
        savedWorkouts.setOnClickListener {
            savedWorkouts.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            presetDialog = Dialog(requireContext())
            presetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            presetDialog.setCancelable(true)
            presetDialog.setContentView(R.layout.fitnesstracker_dialog_presets)
            /*carico la lista dei preset nel dialog*/
            val recyclerPresets = presetDialog.findViewById<RecyclerView>(R.id.recyclerWorkouts)
            recyclerPresets.adapter = WorkoutPresetAdapter(presetList, this)
            presetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            presetDialog.show()
        }

        /*chiudi se premuto X*/
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*istanzio la viewmodel per comunicare col database*/
        viewModel = ViewModelProvider(requireActivity()).get(FitnessSharedViewModel::class.java)
        /*recupero la lista di preset*/
        presetList = viewModel.getPresets()

    }


    override fun onExerciseListener(exercise: Exercise, position: Int, longpress: Boolean) {
        /*quando un esercizio viene selezionato lo rimuovo dalla lista e metto i suoi dati nel Layout per la modifica*/
        exName.setText(exercise.name)
        exRep.setText(exercise.rep)
        exSerie.setText(exercise.set)
        exKg.setText(exercise.kg)
        recValue.setText(exercise.rec)
        exList.remove(exercise)
        exerciseRecycler.adapter = ExerciseAdapter(exList, this)
        exName.requestFocus()
    }

    private val listTypeExercise: ParameterizedType = Types.newParameterizedType(
        List::class.java, Exercise::class.java
    )

    /*se viene selezionato un preset carico le sue informazioni nella schermata*/
    override fun onPresetListener(
        presetName: String,
        presetExercises: String,
        position: Int,
        longpress: Boolean
    ) {
        workoutName.setText(presetName)
        exerciseDeserializer(presetExercises)
        presetDialog.dismiss()
        Log.i("hp_AddWorkoutFragment", "presetListener $presetName e $presetExercises")
    }
    /*funzione che prende il JSON degli esercizi e lo trasforma in una lista di oggetti Exercise*/
    private fun exerciseDeserializer(jsonListOfExercise: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Exercise>> = moshi.adapter(listTypeExercise)
        val exercises: List<Exercise>? = adapter.fromJson(jsonListOfExercise)
        /*aggiorna la lista di esercizi in locale e aggiorna la visualizzazione*/
        exList = exercises as ArrayList<Exercise>
        exerciseRecycler.adapter = ExerciseAdapter(exercises, this)
    }

}