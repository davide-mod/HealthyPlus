package com.modolo.healthyplus.signup

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.modolo.healthyplus.main.MainFragment
import com.modolo.healthyplus.R
import com.modolo.healthyplus.login.LoginFragment
import java.util.*

class SignupFragment : Fragment() {
    companion object {
        fun newInstance() = SignupFragment()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)
        val back = view.findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, LoginFragment(), "LoginFragmentTag").commit()
        }

        var email = "";
        var password = "";
        var name = "";
        var surname = "";
        var dateofbirth = "";
        val scroll = view.findViewById<ScrollView>(R.id.scrollData)
        val fields = scroll.findViewById<LinearLayout>(R.id.fieldsLayout)

        val emailText = fields.findViewById<TextInputEditText>(R.id.emailText)

        val passwordText = fields.findViewById<TextInputEditText>(R.id.passwordText)

        val password2Text = fields.findViewById<TextInputEditText>(R.id.password2Text)

        val nameText = fields.findViewById<TextInputEditText>(R.id.nameText)

        val surnameText = fields.findViewById<TextInputEditText>(R.id.surnameText)


        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val dateText = scroll.findViewById<TextView>(R.id.dateText)
        dateText.setOnClickListener {
            DatePickerDialog(
                    requireContext(),
                    { _, year, month, dayOfMonth ->
                        val text = "$dayOfMonth-${month + 1}-$year"
                        dateofbirth = text
                        dateText.text = text
                    },
                    y,
                    m - 1,
                    d
            ).show()
        }

        val layoutSignup = view.findViewById<TextView>(R.id.layoutSignup)
        layoutSignup.setOnClickListener {
            var ready = true
            val emailtmp = emailText.text.toString()
            if (emailtmp != "" && emailtmp.isEmailValid())
                email = emailtmp
            else
                ready = false

            val pswtmp = passwordText.text.toString()
            val psw2tmp = password2Text.text.toString()
            if (pswtmp != "" && pswtmp == psw2tmp)
                password = pswtmp
            else
                ready = false

            val nametmp = nameText.text.toString()
            if (nametmp != "")
                name = nametmp
            else
                ready = false

            val surnametmp = surnameText.text.toString()
            if (surnametmp != "")
                surname = surnametmp
            else
                ready = false

            if (dateofbirth == "")
                ready = false

            if (ready) {
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, MainFragment(), "MainFragmentTag").commit()
            } else
                Toast.makeText(requireContext(), "Problema/i nei campi, ricontrolla", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


}