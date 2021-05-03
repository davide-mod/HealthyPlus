package com.modolo.healthyplus.mealplanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class MealplannerFragment : Fragment(), MealAdapter.MealListener, MealAdapterHistory.MealHistoryListener {
    companion object {
        fun newInstance() = MealplannerFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_mealplanner, container, false)
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }


        val presetsView = view.findViewById<RecyclerView>(R.id.presetsMeals)
        val incomingView = view.findViewById<RecyclerView>(R.id.incomingMeals)
        val historyView = view.findViewById<RecyclerView>(R.id.historyMeals)

        val dummyMeals = randomMeals(20)

        val presets = ArrayList<Meal>()
        val incoming = ArrayList<Meal>()
        val history = ArrayList<Meal>()

        for (meal in dummyMeals) {
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

    override fun onMealListener(meal: Meal, position: Int, longpress: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onMealHistoryListener(meal: Meal, position: Int, longpress: Boolean) {
        TODO("Not yet implemented")
    }

}