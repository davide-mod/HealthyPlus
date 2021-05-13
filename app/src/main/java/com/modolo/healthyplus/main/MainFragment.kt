package com.modolo.healthyplus.main

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        /*recupero il drawer dalla main activity*/
        val ham = view.findViewById<ImageView>(R.id.hamburger)
        ham.setOnClickListener {
            (activity as MainActivity?)?.openDrawer()
        }
        /*rendo il drawer attivo*/
        (activity as MainActivity?)!!.setDrawerEnabled(true)

        /*creazione di notifiche casuali per debug e per vedere l'effetto, si possono creare:
        * - notizie base con titolo e data di arrivo (negli esempi messa come statica)
        * - notizie "complesse" dove si può scegliere il colore del titolo e si può aggiungere una descrizione
        * */
        val notifications = ArrayList<Notification>()
        for(i in 1..20) {
            if(i%2 == 0)
                notifications.add(Notification("Notifica/Notizia Base $i", "12/4"))
            else if(i%3 == 0)
                notifications.add(Notification("Notifica colorata $i", "16/4", "descrizione di test $i", getColor(requireContext(), R.color.main)))
            else
                notifications.add(Notification("Notifica colorata $i", "18/4", "descrizione di test $i", getColor(requireContext(), R.color.main_fitnesstracker)))
        }
        val notificationView = view.findViewById<RecyclerView>(R.id.recyclerNotifications)
        notificationView.adapter = NotificationAdapter(notifications, this)

        return view
    }

    override fun onStart() {
        super.onStart()
        val mAuth = FirebaseAuth.getInstance()
        /*se l'utente non è autenticato viene caricato il Fragment di Login*/
        val user = mAuth.currentUser
        if(user == null) {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onNotificationListener(notification: Notification, position: Int, longpress: Boolean) {
        Log.i("hp_MainFragment", "Notification clicked: $notification")
    }


}