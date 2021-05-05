package com.modolo.healthyplus.mealplanner

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.textfield.TextInputEditText
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class AddMealFragment : Fragment(), FoodAdapter.FoodListener {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.mealplanner_frag_add, container, false)

        val name = view.findViewById<EditText>(R.id.title)
        val savedMeals = view.findViewById<TextView>(R.id.btnSavedMeals)
        val proceed = view.findViewById<TextView>(R.id.btnProceed)


        val foodList = ArrayList<Food>()
        //i campi per l'aggiunta di un cibo
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        val foodName = inputFields.findViewById<TextInputEditText>(R.id.foodNameText)
        val foodQuantity = inputFields.findViewById<TextInputEditText>(R.id.quantityText)
        val udmSpinner = inputFields.findViewById<Spinner>(R.id.spinnerUdm)
        val foodKcal = inputFields.findViewById<TextInputEditText>(R.id.kcalText)
        val addFood = inputFields.findViewById<TextView>(R.id.addBtn)
        val foodRecycler = view.findViewById<RecyclerView>(R.id.foodRecycler)
        //aggiungi cibo
        addFood.setOnClickListener {
            Log.i("devdebug", "gheson")
            if (foodName.text.toString() != "") {
                val nameTmp = foodName.text.toString()
                val quantityTmp =
                    if (foodQuantity.text.toString() != "") foodQuantity.text.toString()
                        .toFloat() else 0.0F
                val udmTmp = udmSpinner.selectedItem.toString()
                val kcalTmp =
                    if (foodKcal.text.toString() != "") foodKcal.text.toString().toFloat() else 0.0F
                foodList.add(Food(nameTmp, quantityTmp, udmTmp, kcalTmp))
                //mostro i cibi nella Recycler
                foodRecycler.adapter = FoodAdapter(foodList, this, requireContext())
            }
        }


        proceed.setOnClickListener {
            val nameTmp = name.text.toString()
            if (foodList.size > 0 && nameTmp != "") {
                //se il nostro pasto ha un nome ed almeno un elemento possiamo salvarlo
                //apro il dialog per la scelta
                val dialog = Dialog(requireContext())
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(true)
                dialog.setContentView(R.layout.mealplanner_save_dialog)
                //recupero i vari elementi per poter procedere
                val title = dialog.findViewById<TextView>(R.id.title)
                title.text = nameTmp //imposto il titolo in base al nome del pasto

                val eaten = dialog.findViewById<TextView>(R.id.eaten)
                eaten.setOnClickListener {
                    //se premuto il tasto mangiato, verrà semplicemente aggiunto allo storico
                    //todo add to eaten db -> open mealPlannerFrag
                }
                val aspreset = dialog.findViewById<TextView>(R.id.aspreset)
                aspreset.setOnClickListener {
                    //qui si aggiunge ai presets
                    //todo add to preset db - > open mealPlannerFrag
                }
                val schedule = dialog.findViewById<TextView>(R.id.schedule)
                schedule.setOnClickListener {
                    //dopo aver selezionato data e ora si manderà ai pasti in arrivo
                    //todo dateandtimepicker dialog -> add to db ->open mealplannerFrag
                }
                dialog.show()
            }
        }

        //chiudi se premuto X
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }


    override fun onFoodListener(food: Food, position: Int, longpress: Boolean) {
        /*foodName.setText(food.name)
        foodQuantity.setText(food.quantity.toString())
        udmSpinner.setSelection(findSpinnerElement(food.udm))
        foodKcal.setText(food.kcal.toString())
        foodListTmp.remove(food)*/
        TODO("Not yet implemented")
    }
}