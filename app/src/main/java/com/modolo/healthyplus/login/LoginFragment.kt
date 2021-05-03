package com.modolo.healthyplus.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.islamkhsh.CardSliderViewPager
import com.modolo.healthyplus.R
import com.modolo.healthyplus.signup.SignupFragment

class LoginFragment : Fragment(){
    companion object {
        fun newInstance() = LoginFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val lista = ArrayList<String>()
        lista.add("Healthy Plus\nIl contenitore di funzionalità per una vita sana")
        lista.add("Meal Planner\nDiario Alimentare completo per aiutarti a programmare i pasti")
        lista.add("Fitness Tracker\nL'allenamento in palestra ancora più immediato")
        val cards = view.findViewById<CardSliderViewPager>(R.id.viewPager)
        cards.adapter = LoginCardsAdapter(lista)
        val layoutSignup = view.findViewById<ConstraintLayout>(R.id.layoutSignup)
        layoutSignup.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, SignupFragment(), "SignUpTag").commitNow()
        }


        return view
    }


}