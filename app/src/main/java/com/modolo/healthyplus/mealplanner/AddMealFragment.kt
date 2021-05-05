package com.modolo.healthyplus.mealplanner

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
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

    lateinit var foodName: TextInputEditText
    lateinit var foodQuantity: TextInputEditText
    lateinit var udmSpinner: Spinner
    lateinit var foodKcal: TextInputEditText
    lateinit var foodList: ArrayList<Food>
    lateinit var foodRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.mealplanner_frag_add, container, false)

        val name = view.findViewById<EditText>(R.id.title)
        val savedMeals = view.findViewById<TextView>(R.id.btnSavedMeals)

        foodList = ArrayList()
        //i campi per l'aggiunta di un cibo
        val inputFields = view.findViewById<ConstraintLayout>(R.id.inputLayout)
        foodName = inputFields.findViewById(R.id.foodNameText)
        foodQuantity = inputFields.findViewById(R.id.quantityText)
        udmSpinner = inputFields.findViewById(R.id.spinnerUdm)
        foodKcal = inputFields.findViewById(R.id.kcalText)
        val addFood = inputFields.findViewById<TextView>(R.id.addBtn)
        foodRecycler = view.findViewById(R.id.foodRecycler)
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
                //resetto i campi
                foodName.setText("")
                foodQuantity.setText("")
                foodKcal.setText("")
            }
        }

        val proceed = view.findViewById<TextView>(R.id.btnProceed)
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
                    val meal = Meal(
                        nameTmp, foodList, LocalDateTime.now(),
                        ispreset = false,
                        isdone = true,
                        id = 0
                    )
                    val bundle = Bundle()
                    bundle.putSerializable("meal", meal)
                    findNavController().navigate(R.id.mealplannerFragment, bundle)
                    dialog.dismiss()
                    //verrà semplicemente aggiunto allo storico
                    //todo OPEN DIALOG
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
            else{
                Toast.makeText(requireContext(), "Inserisci il nome ed almeno un elemento", Toast.LENGTH_SHORT).show()            }
        }

        //chiudi se premuto X
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
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
}