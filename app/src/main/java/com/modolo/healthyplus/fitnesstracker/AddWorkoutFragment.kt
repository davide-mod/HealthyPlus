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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.modolo.healthyplus.R
import com.modolo.healthyplus.fitnesstracker.exercise.Exercise
import com.modolo.healthyplus.fitnesstracker.exercise.ExerciseAdapter
import com.modolo.healthyplus.fitnesstracker.workoutdb.Workout
import com.modolo.healthyplus.fitnesstracker.workoutpresets.WorkoutPresetAdapter
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import com.modolo.healthyplus.mealplanner.mealpresets.MealPresetAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.synthetic.main.fitnesstracker_frag_add.*
import java.lang.reflect.ParameterizedType
import java.time.LocalDateTime
import kotlin.collections.ArrayList

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

        workoutName = view.findViewById(R.id.title)

        exList = ArrayList()
        //i campi per l'aggiunta di un cibo
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        exName = inputFields.findViewById(R.id.exerciseNameText)
        recValue = inputFields.findViewById(R.id.recValue)
        exRep = inputFields.findViewById(R.id.repText)
        exSerie = inputFields.findViewById(R.id.serieText)
        exKg = inputFields.findViewById(R.id.kgText)
        val addEx = inputFields.findViewById<TextView>(R.id.addBtn)

        exerciseRecycler = view.findViewById(R.id.exerciseRecycler)


        //aggiungi cibo secondo i parametri inseriti
        addEx.setOnClickListener {
            addEx.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
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

                exList.add(Exercise(exNameTmp, repTmp, serieTmp, kgTmp, recTmp))
                exerciseRecycler.adapter = ExerciseAdapter(exList, this, requireContext())
                //resetto i campi
                exName.setText("")
                exRep.setText("")
                exSerie.setText("")
                exKg.setText("")
                recValue.setText("")

                exName.requestFocus()
            }
        }

        //quando ha terminato di inserire i cibi si procede
        val proceed = view.findViewById<TextView>(R.id.btnProceed)
        proceed.setOnClickListener {
            proceed.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            val workoutNameTmp = workoutName.text.toString()
            if (exList.size > 0 && workoutNameTmp != "") {
                //se il nostro pasto ha un nome ed almeno un elemento possiamo salvarlo
                //apro il dialog per la scelta
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.fitnesstracker_dialog_save)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                //recupero i vari elementi per poter procedere
                val title = dialog.findViewById<TextView>(R.id.title)
                title.text = workoutNameTmp //imposto il titolo in base al nome del pasto
                val exJson = Gson().toJson(exList)
                val newId = viewModel.getLastId() + 1
                newWorkout = Workout(
                    newId,
                    workoutNameTmp, exJson, LocalDateTime.now().toString(),
                    ispreset = false,
                    isdone = false
                )
                Log.i("devdebug", "AddWorkoutFragment: newMeal ${newWorkout.name} e id ${newWorkout.id}")
                val doneWorkout = dialog.findViewById<TextView>(R.id.doneWorkout)
                doneWorkout.setOnClickListener {
                    doneWorkout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    newWorkout.isdone = true //imposto che è stato fatto
                    Log.i("devdebug", "AddWorkoutFragment: asDone ${newWorkout.name} e id ${newWorkout.id}")
                    viewModel.insertWorkout(newWorkout) //lo passo alla viewmodel condivisa
                    dialog.dismiss()
                    findNavController().navigateUp() //torno alla home del modulo
                }
                val aspreset = dialog.findViewById<TextView>(R.id.aspreset)
                aspreset.setOnClickListener {
                    aspreset.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    newWorkout.ispreset = true
                    Log.i("devdebug", "AddWorkoutFragment: asPreset ${newWorkout.name} e id ${newWorkout.id}")
                    viewModel.insertWorkout(newWorkout)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                val schedule = dialog.findViewById<TextView>(R.id.schedule)
                schedule.setOnClickListener {
                    schedule.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    Log.i("devdebug", "AddFragment: asIncoming ${newWorkout.name} e id ${newWorkout.id}")
                    viewModel.insertWorkout(newWorkout)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                dialog.show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Inserisci il nome ed almeno un elemento",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //scelta di un eventuale preset
        val savedWorkouts = view.findViewById<TextView>(R.id.btnSavedWorkouts)
        savedWorkouts.setOnClickListener {
            savedWorkouts.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            presetDialog = Dialog(requireContext())
            presetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            presetDialog.setCancelable(true)
            presetDialog.setContentView(R.layout.fitnesstracker_dialog_presets)

            val recyclerPresets = presetDialog.findViewById<RecyclerView>(R.id.recyclerWorkouts)
            recyclerPresets.adapter = WorkoutPresetAdapter(presetList, this)
            presetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            presetDialog.show()
        }

        //chiudi se premuto X
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(FitnessSharedViewModel::class.java)
        presetList = viewModel.getPresets()

    }


    override fun onExerciseListener(exercise: Exercise, position: Int, longpress: Boolean) {
        exName.setText(exercise.name)
        exRep.setText(exercise.rep)
        exSerie.setText(exercise.set)
        exKg.setText(exercise.kg)
        recValue.setText(exercise.rec)
        //e viene rimosso dalla lista, dando la possibilità di modificarlo
        exList.remove(exercise)
        exerciseRecycler.adapter = ExerciseAdapter(exList, this, requireContext())
        exName.requestFocus()
    }

    private val listTypeExercise: ParameterizedType = Types.newParameterizedType(
        List::class.java, Exercise::class.java
    )

    override fun onPresetListener(
        presetName: String,
        presetExercises: String,
        position: Int,
        longpress: Boolean
    ) {
        workoutName.setText(presetName)
        exerciseDeserializer(presetExercises)
        presetDialog.dismiss()
        Log.i("devdebug", "AddFragment: presetListener $presetName e $presetExercises")
    }

    private fun exerciseDeserializer(jsonListOfExercise: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Exercise>> = moshi.adapter(listTypeExercise)
        val exercises: List<Exercise>? = adapter.fromJson(jsonListOfExercise)
        exList = exercises as ArrayList<Exercise>
        exerciseRecycler.adapter = ExerciseAdapter(exercises, this, requireContext())
    }

}