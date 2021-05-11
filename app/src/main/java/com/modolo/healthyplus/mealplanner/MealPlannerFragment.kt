package com.modolo.healthyplus.mealplanner

import android.app.Application
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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.modolo.healthyplus.DButil
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.meal.MealAdapter
import com.modolo.healthyplus.mealplanner.meal.MealAdapterHistory
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import java.time.LocalDateTime

class MealPlannerFragment : Fragment(), MealAdapter.MealListener,
    MealAdapterHistory.MealHistoryListener {

    private val presets = ArrayList<Meal>()
    private val incoming = ArrayList<Meal>()
    private val history = ArrayList<Meal>()

    private lateinit var mAuth: FirebaseAuth


    private lateinit var viewModel: MealsSharedViewModel
    private var meals = ArrayList<Meal>()

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
        mAuth = FirebaseAuth.getInstance()

        //carico le Recycler dei vari pasti (preset, in arrivo e lo storico)
        presetsView = view.findViewById(R.id.presetsMeals)
        incomingView = view.findViewById(R.id.incomingMeals)
        historyView = view.findViewById(R.id.historyMeals)

        //aggiunta pasto
        val btnMeal = view.findViewById<TextView>(R.id.btnMeal)
        btnMeal.setOnClickListener {
            btnMeal.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            findNavController().navigate(R.id.addMealFragment)
        }

        val btnSnack = view.findViewById<TextView>(R.id.btnSnack)
        btnSnack.setOnClickListener {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.mealplanner_dialog_snack)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val snackTitle = dialog.findViewById<EditText>(R.id.title)
            val snackQuantity = dialog.findViewById<TextInputEditText>(R.id.quantityText)
            val snackSpinner = dialog.findViewById<Spinner>(R.id.spinnerUdm)
            val snackKcal = dialog.findViewById<EditText>(R.id.kcalText)
            val snackSave = dialog.findViewById<TextView>(R.id.addBtn)

            snackSave.setOnClickListener {
                //recupero i parametri dal dialog permettendo anche di lasciare il tutto non compilato
                val snackNam = if(snackTitle.text.toString() == "") "Snack" else snackTitle.text.toString()
                val snackQua = if(snackQuantity.text.toString() == "") 0.0F else snackQuantity.text.toString().toFloat()
                val snackSpi = snackSpinner.selectedItem.toString()
                val snackKca = if(snackKcal.text.toString() == "") 0.0F else snackKcal.text.toString().toFloat()
                val food = mutableListOf<Food>()
                food.add(Food(snackNam, snackQua, snackSpi, snackKca))
                val foodJson = Gson().toJson(food)
                val snackMeal = Meal(0, snackNam, foodJson, LocalDateTime.now().toString(), isdone = true, ispreset = false)
                viewModel.insertMeal(snackMeal)
                history.add(snackMeal)
                val sortedHistory =
                    history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
                historyView.adapter = MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
                dialog.dismiss()
            }

            dialog.show()
        }
        return view
    }


    //viewmodel per comunicare tra fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(MealsSharedViewModel::class.java)
        viewModel.meals.observe(viewLifecycleOwner, { mutableList ->
            meals = mutableList as ArrayList<Meal>
        })
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
        Log.i("devdebug", meals.toString())
        val sortedHistory =
            history.sortedByDescending { it.date } //ordino la lista per avere in cima gli ultimi
        presetsView.adapter = MealAdapter(presets, this, requireContext())
        incomingView.adapter = MealAdapter(incoming, this, requireContext())
        historyView.adapter = MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
    }

    //quando un pasto tra i preset o quelli in arrivo viene premuto
    override fun onMealListener(meal: Meal, position: Int, editMeal: Boolean, done: Boolean) {
        //si controlla se ad essere premuto è stato il pulsante di edit
        if (editMeal) {
            //carico il fragment di edit passando il pasto come parametro
            Log.i("devdebug", "MainFragment: wanna edit ${meal.name} e id ${meal.id}")

            val mealToEdit = Meal(meal.id, meal.name, meal.foodList, meal.date, meal.ispreset, meal.isdone)
            viewModel.setMealtoEdit(mealToEdit)

            findNavController().navigate(R.id.editMealFragment)
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
        Log.i("devdebug", "MainFragment: wannaEditHistory ${meal.name} e id ${meal.id}")
        viewModel.setMealtoEdit(meal)
        findNavController().navigate(R.id.editMealFragment)
    }

}