package com.modolo.healthyplus.mealplanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.meal.MealAdapter
import com.modolo.healthyplus.mealplanner.meal.MealAdapterHistory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MealPlannerFragment : Fragment(), MealAdapter.MealListener,
    MealAdapterHistory.MealHistoryListener {

    private val presets = ArrayList<Meal>()
    private val incoming = ArrayList<Meal>()
    private val history = ArrayList<Meal>()

    private lateinit var viewModel: MealsSharedViewModel
    private var meals = mutableListOf<Meal>()
    lateinit var extraMeal: Meal
    var delete = false
    var edit = false

    lateinit var presetsView: RecyclerView
    lateinit var incomingView: RecyclerView
    lateinit var historyView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mealplanner, container, false)

        //richiamo l'hamburger menu
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }

        //carico le Recycler dei vari pasti (preset, in arrivo e lo storico)
        presetsView = view.findViewById(R.id.presetsMeals)
        incomingView = view.findViewById(R.id.incomingMeals)
        historyView = view.findViewById(R.id.historyMeals)

        //todo lettura pasti da DB
        //creo pasti randomici solo per testing
        /*if (presets.size == 0 && incoming.size == 0 && history.size == 0) {
            Log.i("devdebug", "${presets.size} ${incoming.size} ${history.size}")
            for (meal in randomMeals(10)) {
                when {
                    meal.ispreset -> presets.add(meal)
                    !meal.isdone -> incoming.add(meal)
                    else -> history.add(meal)
                }
            }
        }*/

        //aggiunta pasto
        val btnMeal = view.findViewById<TextView>(R.id.btnMeal)
        btnMeal.setOnClickListener {
            findNavController().navigate(R.id.addMealFragment)
        }
        return view
    }

    /*
        private fun randomMeals(number: Int): ArrayList<Meal> {
            val meals = ArrayList<Meal>()
            for (i in 0..number) {
                var date = LocalDateTime.now()
                if (i % 3 == 0) {
                    date = LocalDateTime.parse(
                        "2021-05-01 10:30",
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    )
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
    */
    //TODO remove arguments

    //viewmodel per comunicare tra fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MealsSharedViewModel::class.java)
        viewModel.meals.observe(viewLifecycleOwner, { mutableList ->
            meals = mutableList
            presets.clear()
            history.clear()
            incoming.clear()
            for (meal in meals) {
                when {
                    meal.ispreset -> presets.add(meal)
                    meal.isdone -> history.add(meal)
                    else -> incoming.add(meal)
                }
            }
            val sortedHistory =
                history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
            presetsView.adapter = MealAdapter(presets, this, requireContext())
            incomingView.adapter = MealAdapter(incoming, this, requireContext())
            historyView.adapter = MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
        })
    }

    //quando un pasto tra i preset o quelli in arrivo viene premuto
    override fun onMealListener(meal: Meal, position: Int, editMeal: Boolean, done: Boolean) {
        //si controlla se ad essere premuto è stato il pulsante di edit
        if (editMeal) {
            //carico il fragment di edit passando il pasto come parametro
            val bundle = Bundle()
            bundle.putSerializable("meal", meal)
            findNavController().navigate(R.id.editMealFragment, bundle)
        } else if (done) { //se invece è stato premuto il tasto "fatto" lo sposto nello storico
            meal.isdone = true
            viewModel.updateMeal(meal)
            incoming.remove(meal)
            history.add(meal)
            val sortedHistory =
                history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
            incomingView.adapter = MealAdapter(incoming, this, requireContext())
            historyView.adapter = MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
        }
    }

    override fun onMealHistoryListener(meal: Meal, position: Int, editMeal: Boolean) {
        val bundle = Bundle()
        bundle.putSerializable("meal", meal)
        findNavController().navigate(R.id.editMealFragment, bundle)
    }

}