package com.modolo.healthyplus.mealplanner

import android.app.Dialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import com.modolo.healthyplus.mealplanner.food.Food
import com.modolo.healthyplus.mealplanner.meal.MealAdapter
import com.modolo.healthyplus.mealplanner.meal.MealAdapterHistory
import com.modolo.healthyplus.mealplanner.mealdb.Meal
import java.time.LocalDateTime

class MealPlannerFragment : Fragment(), MealAdapter.MealListener,
    MealAdapterHistory.MealHistoryListener {

    private val PREF_NAME = "data"
    private val MEAL_PLAN_FIRST = "meal1"

    private val presets = ArrayList<Meal>()
    private val incoming = ArrayList<Meal>()
    private val history = ArrayList<Meal>()

    private lateinit var mAuth: FirebaseAuth


    private lateinit var viewModel: MealsSharedViewModel
    private var meals = ArrayList<Meal>()

    private lateinit var presetsView: RecyclerView
    private lateinit var incomingView: RecyclerView
    private lateinit var historyView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_mealplanner, container, false)
        /*abilito eventualmente il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(true)
        /*recupero il drawer dalla main activity*/
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }
        /*ora recupero le informazioni dell'utente per leggere e salvare i pasti*/
        mAuth = FirebaseAuth.getInstance()

        /*carico le Recycler dei vari pasti (preset, in arrivo e lo storico)*/
        presetsView = view.findViewById(R.id.presetsMeals)
        incomingView = view.findViewById(R.id.incomingMeals)
        historyView = view.findViewById(R.id.historyMeals)

        /*passo al fragment per l'aggiunta del pasto*/
        val btnMeal = view.findViewById<TextView>(R.id.btnMeal)
        btnMeal.setOnClickListener {
            btnMeal.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            findNavController().navigate(R.id.addMealFragment)
        }

        /*apro il dialog per l'aggiunta di uno snack rapido
        -> snack = pasto con singolo cibo con nome pari al titolo, modificabile poi come pasto normale*/
        val btnSnack = view.findViewById<TextView>(R.id.btnSnack)
        btnSnack.setOnClickListener {
            /*creo il dialog e richiamo le varie componenti del layout*/
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
                /*recupero i parametri dal dialog permettendo anche di lasciare il tutto non compilato*/
                val snackNam =
                    if (snackTitle.text.toString() == "") "Snack" else snackTitle.text.toString()
                val snackQua =
                    if (snackQuantity.text.toString() == "") 0.0F else snackQuantity.text.toString()
                        .toFloat()
                val snackSpi = snackSpinner.selectedItem.toString()
                val snackKca =
                    if (snackKcal.text.toString() == "") 0.0F else snackKcal.text.toString()
                        .toFloat()
                val food = mutableListOf<Food>()
                food.add(Food(snackNam, snackQua, snackSpi, snackKca))
                /*converto la lista di cibi in Json per avere una singola stringa come parametro*/
                val foodJson = Gson().toJson(food)
                /*richiedo alla viewmodel l'ultimo ID salvato*/
                val newId = viewModel.getLastId() + 1
                /*creo il pasto/snack*/
                val snackMeal = Meal(
                    newId,
                    snackNam,
                    foodJson,
                    LocalDateTime.now().toString(),
                    isdone = true,
                    ispreset = false
                )
                /*mando il nuovo pasto al database e aggiorno le liste della schermata*/
                viewModel.insertMeal(snackMeal)
                history.add(snackMeal)
                val sortedHistory =
                    history.sortedByDescending { it.date } /*ordino la lista per avere in cima gli ultimi*/
                historyView.adapter =
                    MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
                dialog.dismiss()
            }

            dialog.show()
        }
        return view
    }


    /*viewmodel per comunicare con il database*/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*qui eseguo questo codice solo al primo avvio in assoluto della schermata, in quanto il download
        * di eventuale materiale dal server potrebbe impiegare qualche secondo
        * */
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, 0)
        val firstStart = sharedPref?.getBoolean(MEAL_PLAN_FIRST, true)
        if (firstStart == true) {
            Snackbar.make(
                view,
                "Scarico eventuali dati dal server...",
                Snackbar.LENGTH_SHORT
            ).show()
            val editor = sharedPref.edit()
            editor?.putBoolean(MEAL_PLAN_FIRST, false)
            editor.apply()
        }

        /*istanzio effettivamente la viewmodel per parlare col database e richiedo tutti i pasti salvati
        * e li carico nelle varie RecyclerView
        * */
        viewModel = ViewModelProvider(requireActivity()).get(MealsSharedViewModel::class.java)
        viewModel.meals.observe(viewLifecycleOwner, { mutableList ->
            meals = mutableList as ArrayList<Meal>
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
            /*il caricamento nelle Recycler viene effettuato qui perché altrimenti la prima volta che si
            * accede a questo fragment ogni volta che si avvia l'app i pasti non apparirebbero in tempo
            * per via delle coroutine
            * */
            val sortedHistory =
                history.sortedByDescending { it.date } /*ordino la lista per avere in cima gli ultimi*/
            presetsView.adapter = MealAdapter(presets, this, requireContext())
            incomingView.adapter = MealAdapter(incoming, this, requireContext())
            historyView.adapter =
                MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
        })

    }

    /*quando un pasto tra i preset o in arrivo viene premuto*/
    override fun onMealListener(meal: Meal, position: Int, editMeal: Boolean, done: Boolean) {
        /*si controlla se ad essere premuto è stato il pulsante di edit*/
        if (editMeal) {
            /* in questo caso salvo il pasto da modificare nella viewmodel e apro il fragment per la modifica*/
            viewModel.setMealtoEdit(meal)
            findNavController().navigate(R.id.editMealFragment)

        } else if (done) { /*se invece è stato premuto il tasto "fatto" lo sposto nello storico*/
            /*rimuovo il pasto dal database*/
            viewModel.deleteMeal(meal)
            /*aggiorno i parametri*/
            meal.isdone = true
            meal.date = LocalDateTime.now().toString()
            /*lo reinserisco nel database*/
            viewModel.insertMeal(meal)

            /*aggiorno la vista locale*/
            incoming.remove(meal)
            history.add(meal)
            val sortedHistory =
                history.sortedByDescending { it.date } /*ordino la lista per avere in cima gli ultimi*/
            incomingView.adapter = MealAdapter(incoming, this, requireContext())
            historyView.adapter =
                MealAdapterHistory(ArrayList(sortedHistory), this, requireContext())
        }
    }

    /*se viene premuto un pasto nello storico lo mando alla modifica come sopra*/
    override fun onMealHistoryListener(meal: Meal, position: Int, editMeal: Boolean) {
        viewModel.setMealtoEdit(meal)
        findNavController().navigate(R.id.editMealFragment)
    }

}