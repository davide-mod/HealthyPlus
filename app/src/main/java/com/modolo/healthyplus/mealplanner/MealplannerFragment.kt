package com.modolo.healthyplus.mealplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MealplannerFragment : Fragment(), MealAdapter.MealListener, MealAdapterHistory.MealHistoryListener {

    private val presets = ArrayList<Meal>()
    private val incoming = ArrayList<Meal>()
    private val history = ArrayList<Meal>()

    lateinit var  presetsView: RecyclerView
    lateinit var  incomingView: RecyclerView
    lateinit var  historyView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_mealplanner, container, false)
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }


        presetsView = view.findViewById(R.id.presetsMeals)
        incomingView = view.findViewById(R.id.incomingMeals)
        historyView = view.findViewById(R.id.historyMeals)


        for (meal in randomMeals(10)) {
            when {
                meal.ispreset -> presets.add(meal)
                !meal.isdone -> incoming.add(meal)
                else -> history.add(meal)
            }
        }

        presetsView.adapter = MealAdapter(presets, this, requireContext())
        incomingView.adapter = MealAdapter(incoming, this, requireContext())
        historyView.adapter = MealAdapterHistory(history, this, requireContext())
        return view
    }

    private fun randomMeals(number: Int): ArrayList<Meal> {
        val meals = ArrayList<Meal>()
        for (i in 0..number) {
            var date = LocalDateTime.now()
            if (i % 3 == 0) {
                date = LocalDateTime.parse("2021-05-01 10:30", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                meals.add(Meal("Pasto$i", randomFoods(5), date, i % 2 == 0, true, i))
            } else
                meals.add(Meal("Pasto$i", randomFoods(5), date, i % 2 == 0, i % 3 == 0, i))
        }
        return meals
    }

    private fun randomFoods(number: Int): ArrayList<Food> {
        val foods = ArrayList<Food>()
        for (i in 0..number) {
            foods.add(Food("Cibo$i", i.toFloat(), "l", i.toFloat()))
        }
        return foods
    }

    override fun onMealListener(meal: Meal, position: Int, editMeal: Boolean, done: Boolean) {
        if(editMeal) {
            val bundle = Bundle()
            bundle.putSerializable("meal", meal)
            findNavController().navigate(R.id.editMealFragment, bundle)
        }
        else if(done){
            incoming.remove(meal)
            meal.isdone = true
            //todo update database
            history.add(meal)
            incomingView.adapter = MealAdapter(incoming, this, requireContext())
            val sortedHistory = history.sortedByDescending { it.date }
            historyView.adapter = MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
        }
    }

    override fun onMealHistoryListener(meal: Meal, position: Int, editMeal: Boolean) {
        TODO("Not yet implemented")
    }

}