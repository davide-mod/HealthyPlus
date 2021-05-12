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
import kotlin.reflect.jvm.internal.impl.util.Check

class UserFragment : Fragment() {
    private val PREF_NAME = "data"
    private val MOD_MEAL_PLANNER = "meals"
    private val MOD_FITNESS_TRACKER = "fitness"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        val userName = view.findViewById<TextView>(R.id.userName)
        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val logout = view.findViewById<TextView>(R.id.logout)
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }
        val linear = view.findViewById<LinearLayout>(R.id.linearUser)
        val savePref = linear.findViewById<Button>(R.id.btnSavePreferences)
        val checkMealPlanner = linear.findViewById<CheckBox>(R.id.checkMealPlanner)
        val checkFitnessTracker = linear.findViewById<CheckBox>(R.id.checkFitnessTracker)


        //check which module has to be shown
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, 0)
        val mealPlanner = sharedPref?.getBoolean(MOD_MEAL_PLANNER, true)
        val fitnessTracker = sharedPref?.getBoolean(MOD_FITNESS_TRACKER, true)
        checkMealPlanner.isChecked = mealPlanner!!
        checkFitnessTracker.isChecked = fitnessTracker!!

        val mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        userName.text = user?.displayName
        userEmail.text = user?.email

        savePref.setOnClickListener {
            val editor = sharedPref.edit()
            editor?.putBoolean(MOD_MEAL_PLANNER, checkMealPlanner.isChecked)
            editor?.putBoolean(MOD_FITNESS_TRACKER, checkFitnessTracker.isChecked)
            editor.apply()
            Log.i("devdebug", "${checkMealPlanner.isChecked} e ${checkFitnessTracker.isChecked}")
            (activity as MainActivity?)?.setDrawerElementVisible(R.id.itemMealPlanner, checkMealPlanner.isChecked)
            (activity as MainActivity?)?.setDrawerElementVisible(R.id.itemFitnessTracker, checkFitnessTracker.isChecked)
            Toast.makeText(requireContext(), "Modifiche salvate", Toast.LENGTH_SHORT).show()
        }

        logout.setOnClickListener {
            mAuth.signOut()
            findNavController().navigate(R.id.loginFragment)
        }

        return view
    }
}