package com.modolo.healthyplus.userpage

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R

class UserFragment : Fragment() {

    /*Valori per le "preferences" utilizzate per modificare la visibilità dei moduli*/
    private val PREF_NAME = "data"
    private val MOD_MEAL_PLANNER = "meals"
    private val MOD_FITNESS_TRACKER = "fitness"
    //private val MOD_NUOVO_MODULO = "nuovo"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        /*inizializzo i componenti del layout*/
        val userName = view.findViewById<TextView>(R.id.userName)
        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val logout = view.findViewById<TextView>(R.id.logout)
        /*recupero il drawer dalla main activity*/
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }

        /*
         LinearLayout che contiene le varie checkbox per nascondere/mostrare i moduli
         le checkbox vanno aggiunte (se utilizzate) nel /layout/fragment_user (è presente un placeholder)
         */
        val linear = view.findViewById<LinearLayout>(R.id.linearUser)

        val checkMealPlanner = linear.findViewById<CheckBox>(R.id.checkMealPlanner)
        val checkFitnessTracker = linear.findViewById<CheckBox>(R.id.checkFitnessTracker)
        //val checkNuovoModulo = linear.findViewById<CheckBox>(R.id.checkNuovoModulo)

        val savePref = linear.findViewById<Button>(R.id.btnSavePreferences)


        /*recupero le preferences per dare poi lo stato alle checkbox*/
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, 0)
        val mealPlanner = sharedPref?.getBoolean(MOD_MEAL_PLANNER, true)
        val fitnessTracker = sharedPref?.getBoolean(MOD_FITNESS_TRACKER, true)
        //val nuovoModulo = sharedPref?.getBoolean(MOD_NUOVO_MODULO, true)

        checkMealPlanner.isChecked = mealPlanner!!
        checkFitnessTracker.isChecked = fitnessTracker!!
        //checkNuovoModulo.isChecked = nuovoModulo!!

        /*ora recupero le informazioni dell'utente corrente per mostrarle a schermo*/
        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        userName.text = user?.displayName
        userEmail.text = user?.email

        savePref.setOnClickListener {
            /*salvo le nuovo preferences*/
            val editor = sharedPref.edit()
            editor?.putBoolean(MOD_MEAL_PLANNER, checkMealPlanner.isChecked)
            editor?.putBoolean(MOD_FITNESS_TRACKER, checkFitnessTracker.isChecked)
            //editor?.putBoolean(MOD_NUOVO_MODULO, checkNuovoModulo.isChecked)
            editor.apply()
            Log.i("hp_UserFragment", "Preferences aggiornate correttamente")

            /*aggiorno lo stato di visibilità nel drawer*/
            (activity as MainActivity?)?.setDrawerElementVisible(R.id.itemMealPlanner, checkMealPlanner.isChecked)
            (activity as MainActivity?)?.setDrawerElementVisible(R.id.itemFitnessTracker, checkFitnessTracker.isChecked)
            //(activity as MainActivity?)?.setDrawerElementVisible(R.id.itemNuovoModulo, checkNuovoModulo.isChecked)

            Toast.makeText(requireContext(), "Modifiche salvate", Toast.LENGTH_SHORT).show()
        }

        logout.setOnClickListener {
            /*per il logout richiamo la funzione apposita di Firebase e ritorno al Login*/
            mAuth.signOut()
            /*pulisco la stack dei fragment aperti in modo che dopo aver fatto il logout non si
            * può tornare alle varie schermate tramite il pulsante back */
            val count: Int = parentFragmentManager.backStackEntryCount
            for (i in 0..count) {
                parentFragmentManager.popBackStack()
            }
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }
}