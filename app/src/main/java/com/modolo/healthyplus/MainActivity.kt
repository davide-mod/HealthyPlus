package com.modolo.healthyplus

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.modolo.healthyplus.R

class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private lateinit var handler: Handler
    private val navigationListener =
            NavigationView.OnNavigationItemSelectedListener { item ->
                drawer.closeDrawer(GravityCompat.START)
                handler.postDelayed({
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
                }, 0)
                true
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handler = Handler()
        drawer = findViewById(R.id.drawerMain)
        val menulaterale = findViewById<NavigationView>(R.id.menulaterale)
        menulaterale.setNavigationItemSelectedListener(navigationListener)
    }

    fun openDrawer() {
        drawer.openDrawer(GravityCompat.START)
    }
}