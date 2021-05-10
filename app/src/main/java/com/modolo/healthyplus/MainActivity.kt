package com.modolo.healthyplus

import android.R.id.toggle
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout

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
                    findNavController(R.id.nav_host_fragment).navigate(R.id.editMealFragment)
                }
                R.id.itemUser -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.mainFragment)
                }
                R.id.itemCredits -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.mainFragment)
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawer = findViewById(R.id.drawerMain)
        val menulaterale = findViewById<NavigationView>(R.id.menulaterale)
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
}