package com.modolo.healthyplus.mealplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.food.FoodAdapter

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
                foodRecycler.adapter = FoodAdapter(foodList, this, requireContext())
            }
        }

        //mostro i cibi nella Recycler


        proceed.setOnClickListener {
            /*val nameTmp = name.text.toString()
            if(foodList.size > 0){
                val newMeal = Meal(nameTmp, foodList, )
            }*/
            //todo alert proceed
        }

        //chiudi se premuto X
        val close = view.findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            findNavController().navigateUp()
        }
        return view
    }


    override fun onFoodListener(food: Food, position: Int, longpress: Boolean) {
        TODO("Not yet implemented")
    }
}