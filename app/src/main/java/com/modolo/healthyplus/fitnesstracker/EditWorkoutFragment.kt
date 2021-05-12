package com.modolo.healthyplus.fitnesstracker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
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
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter
import com.modolo.healthyplus.mealplanner.mealdb.Meal
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

        workoutName = view.findViewById(R.id.title)

        val delete = view.findViewById<TextView>(R.id.btnDelete)
        val save = view.findViewById<TextView>(R.id.btnSave)

        exerciseRecycler = view.findViewById(R.id.exerciseRecycler)
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        exName = inputFields.findViewById(R.id.exerciseNameText)
        recValue = inputFields.findViewById(R.id.recValue)
        exRep = inputFields.findViewById(R.id.repText)
        exSerie = inputFields.findViewById(R.id.serieText)
        exKg = inputFields.findViewById(R.id.kgText)
        val addEx = inputFields.findViewById<TextView>(R.id.addBtn)
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

                exerciseListTmp.add(Exercise(exNameTmp, repTmp, serieTmp, kgTmp, recTmp))
                exerciseRecycler.adapter = ExerciseAdapter(exerciseListTmp, this, requireContext())
                //resetto i campi
                exName.setText("")
                exRep.setText("")
                exSerie.setText("")
                exKg.setText("")
                recValue.setText("")

                exName.requestFocus()
            }
        }

        delete.setOnClickListener {
            delete.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            viewModel.deleteWorkout(workoutPassed)
            findNavController().navigateUp()
        }

        save.setOnClickListener {
            save.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            viewModel.deleteWorkout(workoutPassed)
            workoutPassed.name = workoutName.text.toString()
            workoutPassed.exerciseList = Gson().toJson(exerciseListTmp)
            viewModel.insertWorkout(workoutPassed)
            findNavController().navigateUp()
        }

        //chiudi quando si preme la X senza salvare
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(FitnessSharedViewModel::class.java)
        workoutPassed = viewModel.getWorkouttoEdit()
        Log.i("devdebug", "EditWorkoutFragment received: $workoutPassed")
        workoutName.setText(workoutPassed.name)
        exerciseDeserializer(workoutPassed.exerciseList)

    }

    override fun onExerciseListener(exercise: Exercise, position: Int, longpress: Boolean) {
        exName.setText(exercise.name)
        exRep.setText(exercise.rep)
        exSerie.setText(exercise.set)
        exKg.setText(exercise.kg)
        recValue.setText(exercise.rec)
        //e viene rimosso dalla lista, dando la possibilità di modificarlo
        exerciseListTmp.remove(exercise)
        exerciseRecycler.adapter = ExerciseAdapter(exerciseListTmp, this, requireContext())
        exName.requestFocus()
    }

    private val listTypeExercise: ParameterizedType = Types.newParameterizedType(
        List::class.java, Exercise::class.java
    )

    private fun exerciseDeserializer(jsonListOfExercise: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Exercise>> = moshi.adapter(listTypeExercise)
        val exercises: List<Exercise>? = adapter.fromJson(jsonListOfExercise)
        exerciseListTmp = exercises as ArrayList<Exercise>
        exerciseRecycler.adapter = ExerciseAdapter(exercises, this, requireContext())
    }
}