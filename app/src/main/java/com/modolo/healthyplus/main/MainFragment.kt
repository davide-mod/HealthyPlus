package com.modolo.healthyplus.main

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R

class MainFragment : Fragment(), NotificationAdapter.NotificationListener{

    private val PREF_NAME = "data"
    private val MOD_FOOD_JOURNAL = "food"
    private val MOD_SPORT_JOURNAL = "sport"

    private lateinit var mAuth: FirebaseAuth



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        //requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, LoginFragment(), "LoginFragmentTag").commit()
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        //check which module has to be shown
        val sharedPref: SharedPreferences? = activity?.getSharedPreferences(PREF_NAME, 0)
        val foodJournal = sharedPref?.getBoolean(MOD_FOOD_JOURNAL, false)
        //if(foodJournal!!)
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }
        (activity as MainActivity?)!!.setDrawerEnabled(true)

        val notifications = ArrayList<Notification>()
        for(i in 1..20) {
            if(i%2 == 0)
                notifications.add(Notification("Titolo$i", "12-4"))
            else
                notifications.add(Notification(getString(R.string.app_name), "12-4", "notifica di test", getColor(requireContext(), R.color.main)))
        }
        Log.i("devdebug", notifications.toString())
        val notificationView = view.findViewById<RecyclerView>(R.id.recyclerNotifications)
        notificationView.adapter = NotificationAdapter(notifications, this)



        return view
    }

    override fun onStart() {
        super.onStart()
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        if(user == null) {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onNotificationListener(notification: Notification, position: Int, longpress: Boolean) {
        Log.i("lol", "ciao")
    }


}