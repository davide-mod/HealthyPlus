package com.modolo.healthyplus

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private val PREF_NAME = "data"
    private val MOD_MEAL_PLANNER = "meals"
    private val MOD_FITNESS_TRACKER = "fitness"

    private val navigationListener =
        NavigationView.OnNavigationItemSelectedListener { item ->
            drawer.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.itemHome -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.mainFragment)
                }
                R.id.itemMealPlanner -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.mealplannerFragment)
                }
                R.id.itemFitnessTracker -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.fitnessTrackerFragment)
                }
                R.id.itemUser -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.userFragment)
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer = findViewById(R.id.drawerMain)

        val sharedPref: SharedPreferences? = getSharedPreferences(PREF_NAME, 0)
        val mealPlanner = sharedPref?.getBoolean(MOD_MEAL_PLANNER, true)
        val fitnessTracker = sharedPref?.getBoolean(MOD_FITNESS_TRACKER, true)
        setDrawerElementVisible(R.id.itemMealPlanner, mealPlanner!!)
        setDrawerElementVisible(R.id.itemFitnessTracker, fitnessTracker!!)

        val menulaterale = findViewById<NavigationView>(R.id.nav_view)
        menulaterale.setNavigationItemSelectedListener(navigationListener)
    }

    fun openDrawer() {
        drawer.openDrawer(GravityCompat.START)
    }
    fun setDrawerEnabled(enabled: Boolean) {
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawer.setDrawerLockMode(lockMode)
    }

    fun setDrawerElementVisible(id: Int, visible: Boolean){
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val navMenu: Menu = navigationView.menu
        navMenu.findItem(id).isVisible = visible
    }
}