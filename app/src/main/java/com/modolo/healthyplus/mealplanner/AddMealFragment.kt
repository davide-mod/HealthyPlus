package com.modolo.healthyplus.mealplanner

import android.app.Dialog
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
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter
import com.modolo.healthyplus.mealplanner.presets.PresetAdapter
import java.time.LocalDateTime
import kotlin.collections.ArrayList

class AddMealFragment : Fragment(), FoodAdapter.FoodListener, PresetAdapter.PresetListener{

    private lateinit var foodName: TextInputEditText
    private lateinit var foodQuantity: TextInputEditText
    private lateinit var udmSpinner: Spinner
    private lateinit var foodKcal: TextInputEditText
    private lateinit var foodList: ArrayList<Food>
    private lateinit var foodRecycler: RecyclerView

    private var presetList = ArrayList<Meal>()
    private lateinit var presetDialog: Dialog
    private lateinit var mealTitle: EditText

    private lateinit var viewModel: MealsSharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.mealplanner_frag_add, container, false)

        mealTitle = view.findViewById(R.id.title)

        foodList = ArrayList()
        //i campi per l'aggiunta di un cibo
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        foodName = inputFields.findViewById(R.id.foodNameText)
        foodQuantity = inputFields.findViewById(R.id.quantityText)
        udmSpinner = inputFields.findViewById(R.id.spinnerUdm)
        foodKcal = inputFields.findViewById(R.id.kcalText)
        val addFood = inputFields.findViewById<TextView>(R.id.addBtn)
        foodRecycler = view.findViewById(R.id.foodRecycler)


        //aggiungi cibo secondo i parametri inseriti
        addFood.setOnClickListener {
            addFood.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            if (foodName.text.toString() != "") {
                val nameTmp = foodName.text.toString()
                val quantityTmp =
                    if (foodQuantity.text.toString() != "") foodQuantity.text.toString()
                        .toFloat() else 0.0F
                val udmTmp = udmSpinner.selectedItem.toString()
                val kcalTmp =
                    if (foodKcal.text.toString() != "") foodKcal.text.toString().toFloat() else 0.0F
                foodList.add(Food(nameTmp, quantityTmp, udmTmp, kcalTmp))
                foodRecycler.adapter = FoodAdapter(foodList, this, requireContext())
                //resetto i campi
                foodName.setText("")
                foodQuantity.setText("")
                foodKcal.setText("")
                foodName.requestFocus()
            }
        }

        //quando ha terminato di inserire i cibi si procede
        val proceed = view.findViewById<TextView>(R.id.btnProceed)
        proceed.setOnClickListener {
            proceed.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            val nameTmp = mealTitle.text.toString()
            if (foodList.size > 0 && nameTmp != "") {
                //se il nostro pasto ha un nome ed almeno un elemento possiamo salvarlo
                //apro il dialog per la scelta
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.mealplanner_dialog_save)

                //recupero i vari elementi per poter procedere
                val title = dialog.findViewById<TextView>(R.id.title)
                title.text = nameTmp //imposto il titolo in base al nome del pasto

                val id = viewModel.getLastId()
                val meal = Meal(
                    nameTmp, foodList, LocalDateTime.now(),
                    ispreset = false,
                    isdone = false,
                    id = id+1
                )

                val eaten = dialog.findViewById<TextView>(R.id.eaten)
                eaten.setOnClickListener {
                    eaten.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    meal.isdone = true //imposto che è stato mangiato
                    viewModel.addMeal(meal) //lo passo alla viewmodel condivisa
                    Log.i("devdebug", "added meal: $meal")
                    dialog.dismiss()
                    findNavController().navigateUp() //torno alla home del modulo
                }
                val aspreset = dialog.findViewById<TextView>(R.id.aspreset)
                aspreset.setOnClickListener {
                    aspreset.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    meal.ispreset = true
                    viewModel.addMeal(meal)
                    Log.i("devdebug", "added preset: $meal")
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
                val schedule = dialog.findViewById<TextView>(R.id.schedule)
                schedule.setOnClickListener {
                    schedule.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                    viewModel.addMeal(meal)
                    Log.i("devdebug", "added incoming: $meal")
                    dialog.dismiss()
                    findNavController().navigateUp()
                    //dopo aver selezionato data e ora si manderà ai pasti in arrivo
                    //TODO dateandtimepicker dialog -> add to db ->open mealplannerFrag
                }
                dialog.show()
            }
            else{
                Toast.makeText(requireContext(), "Inserisci il nome ed almeno un elemento", Toast.LENGTH_SHORT).show()            }
        }

        //scelta di un eventuale preset
        val savedMeals = view.findViewById<TextView>(R.id.btnSavedMeals)
        savedMeals.setOnClickListener {
            savedMeals.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            presetDialog = Dialog(requireContext())
            presetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            presetDialog.setCancelable(true)
            presetDialog.setContentView(R.layout.mealplanner_dialog_presets)

            val recyclerPresets = presetDialog.findViewById<RecyclerView>(R.id.recyclerMeals)
            recyclerPresets.adapter=PresetAdapter(presetList, this, requireContext())
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
        viewModel = ViewModelProvider(requireActivity()).get(MealsSharedViewModel::class.java)
        for(meal in viewModel.getMeals()){
            if(meal.ispreset)
                presetList.add(meal)
        }

    }


    override fun onFoodListener(food: Food, position: Int, longpress: Boolean) {
        //quando un cibo viene selezionato, vengono caricati i suoi parametri nei vari campi
        foodName.setText(food.name)
        foodQuantity.setText(food.quantity.toString())
        udmSpinner.setSelection(findSpinnerElement(food.udm))
        foodKcal.setText(food.kcal.toString())
        //e viene rimosso dalla lista, dando la possibilità di modificarlo
        foodList.remove(food)
        foodRecycler.adapter = FoodAdapter(foodList, this, requireContext())
    }

    //funzione per recuperare l'id di un valore nello spinner partendo dal testo
    private fun findSpinnerElement(value: String): Int {
        val udmlist = resources.getStringArray(R.array.udms_short)
        udmlist.forEachIndexed { index, s ->
            if (s == value)
                return index
        }
        return 0
    }

    override fun onPresetListener(meal: Meal, position: Int, longpress: Boolean) {
        mealTitle.setText(meal.name)
        foodList = meal.foodList
        foodRecycler.adapter = FoodAdapter(foodList, this, requireContext())
        presetDialog.dismiss()

    }
}