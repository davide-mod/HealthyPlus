package com.modolo.healthyplus.mealplanner

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
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType

class EditMealFragment : Fragment(), FoodAdapter.FoodListener {

    private lateinit var mealPassed: Meal
    private var foodListTmp = ArrayList<Food>()


    private lateinit var foodName: TextInputEditText
    private lateinit var foodQuantity: TextInputEditText
    private lateinit var udmSpinner: Spinner
    private lateinit var foodRecycler: RecyclerView
    private lateinit var foodKcal: TextInputEditText
    private lateinit var nameEdit: EditText
    private lateinit var viewModel: MealsSharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.mealplanner_frag_edit, container, false)
        /*disabilito il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(false)

        /*inizializzo i componente per modificare il pasto*/
        nameEdit = view.findViewById(R.id.title)

        /*recycler dove verrà mostrata la lista di cibi nel pasto*/
        foodRecycler = view.findViewById(R.id.foodRecycler)
        /*i campi per l'inserimento del nuovo cibo sono in un ConstraintLayout apposito*/
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        foodName = inputFields.findViewById(R.id.foodNameText)
        foodQuantity = inputFields.findViewById(R.id.quantityText)
        udmSpinner = inputFields.findViewById(R.id.spinnerUdm)
        foodKcal = inputFields.findViewById(R.id.kcalText)

        val addFood = inputFields.findViewById<TextView>(R.id.addBtn)
        addFood.setOnClickListener {
            addFood.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*controllo che ci sia almeno il nome del cibo da aggiungere*/
            if (foodName.text.toString() != "") {
                val nameTmp = foodName.text.toString()
                val quantityTmp =
                    if (foodQuantity.text.toString() != "") foodQuantity.text.toString()
                        .toFloat() else 0.0F
                val udmTmp = udmSpinner.selectedItem.toString()
                val kcalTmp =
                    if (foodKcal.text.toString() != "") foodKcal.text.toString().toFloat() else 0.0F
                /*aggiungo il cibo alla lista di cibi e aggiorno la Recycler che li mostra*/
                foodListTmp.add(Food(nameTmp, quantityTmp, udmTmp, kcalTmp))
                foodRecycler.adapter = FoodAdapter(foodListTmp, this)

                /*resetto i campi e sposto il focus sul nome del cibo*/
                foodName.setText("")
                foodQuantity.setText("")
                foodKcal.setText("")
                foodName.requestFocus()
            }
        }

        val delete = view.findViewById<TextView>(R.id.btnDelete)
        delete.setOnClickListener {
            delete.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*mando il pasto da cancellare alla viewmodel e torno alla home*/
            viewModel.deleteMeal(mealPassed)
            findNavController().navigateUp()
        }

        val save = view.findViewById<TextView>(R.id.btnSave)
        save.setOnClickListener {
            save.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*per salvare le modifiche rimuovo il pasto già presente, aggiorno i dati e lo inserisco nuovamente*/
            viewModel.deleteMeal(mealPassed)
            mealPassed.name = nameEdit.text.toString()
            mealPassed.foodList = Gson().toJson(foodListTmp)
            viewModel.insertMeal(mealPassed)
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
        /*istanzio la viewmodel e recupero il pasto da modificare*/
        viewModel = ViewModelProvider(requireActivity()).get(MealsSharedViewModel::class.java)
        mealPassed = viewModel.getMealtoEdit()
        Log.i("hp_EditMealFragment", "Meal received: $mealPassed")
        /*modifico la schermata secondo i dati del pasto da modificare*/
        nameEdit.setText(mealPassed.name)
        foodDeserializer(mealPassed.foodList)

    }

    private val listTypeFood: ParameterizedType = Types.newParameterizedType(
        List::class.java, Food::class.java
    )
    /*funzione che prende il JSON della lista di cibi e lo trasforma in una lista di oggetti Food*/
    private fun foodDeserializer(jsonListOfFood: String) {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val adapter: JsonAdapter<List<Food>> = moshi.adapter(listTypeFood)
        val foods: List<Food>? = adapter.fromJson(jsonListOfFood)
        /*aggiorna la lista di cibi locale e aggiorna la visualizzazione*/
        foodListTmp = foods as ArrayList<Food>
        foodRecycler.adapter = FoodAdapter(foods, this)
    }

    override fun onFoodListener(food: Food, position: Int, longpress: Boolean) {
        /*quando un cibo viene selezionato lo rimuovo dalla lista e metto i suoi dati nel Layout per la modifica*/
        foodName.setText(food.name)
        foodQuantity.setText(food.quantity.toString())
        udmSpinner.setSelection(findSpinnerElement(food.udm))
        foodKcal.setText(food.kcal.toString())
        foodListTmp.remove(food)
        foodRecycler.adapter = FoodAdapter(foodListTmp, this)
        foodName.requestFocus()
    }

    /*funzione per trovare l'indice di un certo valore in uno spinner*/
    private fun findSpinnerElement(value: String): Int {
        val udmlist = resources.getStringArray(R.array.udms_short)
        udmlist.forEachIndexed { index, s ->
            if (s == value)
                return index
        }
        return 0
    }
}