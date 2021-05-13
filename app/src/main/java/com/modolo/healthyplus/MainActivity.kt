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

    /*
            INFORMAZIONI SULL'AGGIUNTA DI NUOVI MODULI
        Ho impostato i commenti in questo modo:
        - racchiusi tra /* */ ci sono le informazioni sul funzionamento del codice scritto
        - le righe commentate con // sono dei placeholder per capire come impostare i nuovi moduli
     */


    private lateinit var drawer: DrawerLayout
    /*Valori per le "preferences" utilizzate nell'onCreate per nascondere o meno i moduli dal drawer*/
    private val PREF_NAME = "data"
    private val MOD_MEAL_PLANNER = "meals"
    private val MOD_FITNESS_TRACKER = "fitness"
    //private val MOD_NUOVO_MODULO = "nuovo"

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
                //R.id.itemNuovoModulo -> { //questo id è quello inserito nel /res/menu/item_nav_view.xml
                    //findNavController(R.id.nav_host_fragment).navigate(R.id.nuovoModuloFragment)
                //}
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*inizializzo il drawer*/
        drawer = findViewById(R.id.drawerMain)

        /*recupero le "preferences"*/
        val sharedPref: SharedPreferences? = getSharedPreferences(PREF_NAME, 0)
        val mealPlanner = sharedPref?.getBoolean(MOD_MEAL_PLANNER, true)
        val fitnessTracker = sharedPref?.getBoolean(MOD_FITNESS_TRACKER, true)
        //val nuovoModulo = sharedPref?.getBoolean(MOD_NUOVO_MODULO, true)

        /*nascondo o mostro le voci nel drawer in base alle preferenze*/
        setDrawerElementVisible(R.id.itemMealPlanner, mealPlanner!!)
        setDrawerElementVisible(R.id.itemFitnessTracker, fitnessTracker!!)
        //setDrawerElementVisible(R.id.itemNuovoModulo, nuovoModulo!!)

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(navigationListener)
    }

    fun openDrawer() {
        /*funzione usata dai fragment figli per richiamare il drawer*/
        drawer.openDrawer(GravityCompat.START)
    }
    fun setDrawerEnabled(enabled: Boolean) {
        /*funzione usata dai fragment figli per impedire se necessario l'accesso al drawer tramite swipe*/
        val lockMode =
            if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        drawer.setDrawerLockMode(lockMode)
    }

    fun setDrawerElementVisible(id: Int, visible: Boolean){
        /*funzione per nascondere eventuali elementi del drawer, i parametri sono l'id dell'item
        da nascondere e il t/f per la visibilità*/
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val navMenu: Menu = navigationView.menu
        navMenu.findItem(id).isVisible = visible
    }
}