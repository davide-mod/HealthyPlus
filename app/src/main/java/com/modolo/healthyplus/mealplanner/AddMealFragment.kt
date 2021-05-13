package com.modolo.healthyplus.mealplanner

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
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import com.modolo.healthyplus.mealplanner.mealpresets.MealPresetAdapter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType
import java.time.LocalDateTime
import kotlin.collections.ArrayList

class AddMealFragment : Fragment(), FoodAdapter.FoodListener, MealPresetAdapter.PresetListener {

    private lateinit var foodName: TextInputEditText
    private lateinit var foodQuantity: TextInputEditText
    private lateinit var udmSpinner: Spinner
    private lateinit var foodKcal: TextInputEditText
    private lateinit var foodList: ArrayList<Food>
    private lateinit var foodRecycler: RecyclerView

    private var presetList = ArrayList<Meal>()
    private lateinit var presetDialog: Dialog
    private lateinit var mealTitle: EditText
    private lateinit var newMeal: Meal

    private lateinit var viewModel: MealsSharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.mealplanner_frag_add, container, false)
        /*disabilito il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(false)

        /*inizializzo il componente per il nome del pasto*/
        mealTitle = view.findViewById(R.id.title)

        /*recycler dove verrà mostrata la lista di cibi nel pasto*/
        foodList = ArrayList()
        /*i campi per l'inserimento del nuovo cibo sono in un ConstraintLayout apposito*/
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        foodName = inputFields.findViewById(R.id.foodNameText)
        foodQuantity = inputFields.findViewById(R.id.quantityText)
        udmSpinner = inputFields.findViewById(R.id.spinnerUdm)
        foodKcal = inputFields.findViewById(R.id.kcalText)
        foodRecycler = view.findViewById(R.id.foodRecycler)

        /*aggiungi un nuovo cibo secondo i parametri inseriti*/
        val addFood = inputFields.findViewById<TextView>(R.id.addBtn)
        addFood.setOnClickListener {
            addFood.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*controllo che ci sia almeno il nome del cibo da aggiungere*/
            if (foodName.text.toString() != "") {
                val foodNameTmp = foodName.text.toString()
                val quantityTmp =
                    if (foodQuantity.text.toString() != "") foodQuantity.text.toString()
                        .toFloat() else 0.0F
                val udmTmp = udmSpinner.selectedItem.toString()
                val kcalTmp =
                    if (foodKcal.text.toString() != "") foodKcal.text.toString().toFloat() else 0.0F
                /*aggiungo il cibo alla lista di cibi e aggiorno la Recycler che li mostra*/
                foodList.add(Food(foodNameTmp, quantityTmp, udmTmp, kcalTmp))
                foodRecycler.adapter = FoodAdapter(foodList, this)
                /*resetto i campi e sposto il focus sul nome del cibo*/
                foodName.setText("")
                foodQuantity.setText("")
                foodKcal.setText("")
                foodName.requestFocus()
            }
        }

        /*quando l'utente ha terminato di inserire i cibi si procede*/
        val proceed = view.findViewById<TextView>(R.id.btnProceed)
        proceed.setOnClickListener {
            proceed.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            val mealNameTmp = mealTitle.text.toString()
            if (foodList.size > 0 && mealNameTmp != "") {
                /*se il nostro pasto ha un nome ed almeno un elemento possiamo salvarlo
                apro il dialog per la scelta*/
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.mealplanner_dialog_save)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                /*recupero i vari elementi per poter procedere*/
                val title = dialog.findViewById<TextView>(R.id.title)
                title.text = mealNameTmp /*imposto il titolo del dialog in base al nome del pasto*/
                /*la lista di cibi del pasto viene salvata come stringa JSON*/
                val foodJson = Gson().toJson(foodList)
                val newId = viewModel.getLastId() + 1
                newMeal = Meal(
                    newId,
                    mealNameTmp, foodJson, LocalDateTime.now().toString(),
                    ispreset = false,
                    isdone = false
                )
                Log.i("hp_AddMealFragment", "new meal ${newMeal.name} e id ${newMeal.id}")

                /*se mangiato verrà impostato come tale*/
                val eaten = dialog.findViewById<TextView>(R.id.eaten)
                eaten.setOnClickListener {
                    eaten.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    newMeal.isdone = true
                    Log.i("hp_AddMealFragment", "asEaten ${newMeal.name} e id ${newMeal.id}")
                    /*passo il nuovo pasto alla viewmodel che lo aggiungerà sia al db online che locale*/
                    viewModel.insertMeal(newMeal)
                    dialog.dismiss()
                    findNavController().navigateUp() /*torno alla home del modulo*/
                }

                /*se impostato come preset si procederà di conseguenza*/
                val aspreset = dialog.findViewById<TextView>(R.id.aspreset)
                aspreset.setOnClickListener {
                    aspreset.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    newMeal.ispreset = true
                    Log.i("hp_AddMealFragment", "asPreset ${newMeal.name} e id ${newMeal.id}")
                    /*passo il nuovo pasto alla viewmodel che lo aggiungerà sia al db online che locale*/
                    viewModel.insertMeal(newMeal)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                /*se impostato come in arrivo si procederà di conseguenza*/
                val schedule = dialog.findViewById<TextView>(R.id.schedule)
                schedule.setOnClickListener {
                    schedule.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    Log.i("hp_AddMealFragment", "asIncoming ${newMeal.name} e id ${newMeal.id}")
                    /*passo il nuovo pasto alla viewmodel che lo aggiungerà sia al db online che locale*/
                    viewModel.insertMeal(newMeal)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                dialog.show()
            } else { /*sono necessari almeno un elemento e il nome*/
                Toast.makeText(
                    requireContext(),
                    "Inserisci il nome ed almeno un elemento",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        /*scelta di un eventuale preset*/
        val savedMeals = view.findViewById<TextView>(R.id.btnSavedMeals)
        savedMeals.setOnClickListener {
            savedMeals.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            presetDialog = Dialog(requireContext())
            presetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            presetDialog.setCancelable(true)
            presetDialog.setContentView(R.layout.mealplanner_dialog_presets)
            /*carico la lista dei preset nel dialog*/
            val recyclerPresets = presetDialog.findViewById<RecyclerView>(R.id.recyclerMeals)
            recyclerPresets.adapter = MealPresetAdapter(presetList, this)
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
        viewModel = ViewModelProvider(requireActivity()).get(MealsSharedViewModel::class.java)
        /*recupero la lista di preset*/
        presetList = viewModel.getPresets()

    }


    override fun onFoodListener(food: Food, position: Int, longpress: Boolean) {
        /*quando un cibo viene selezionato lo rimuovo dalla lista e metto i suoi dati nel Layout per la modifica*/
        foodName.setText(food.name)
        foodQuantity.setText(food.quantity.toString())
        udmSpinner.setSelection(findSpinnerElement(food.udm))
        foodKcal.setText(food.kcal.toString())
        foodList.remove(food)
        foodRecycler.adapter = FoodAdapter(foodList, this)
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

    /*se viene selezionato un preset carico le sue informazioni nella schermata*/
    override fun onPresetListener(
        mealName: String,
        presetFoods: String,
        position: Int,
        longpress: Boolean
    ) {
        mealTitle.setText(mealName)
        foodDeserializer(presetFoods)
        presetDialog.dismiss()
        Log.i("hp_AddMealFragment", "presetListener $mealName e $foodList")

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
        foodList = foods as ArrayList<Food>
        foodRecycler.adapter = FoodAdapter(foods, this)
    }

}