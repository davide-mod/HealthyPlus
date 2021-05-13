package com.modolo.healthyplus.signup

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.DButil
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R
import java.util.*

class SignupFragment : Fragment() {

    lateinit var mAuth: FirebaseAuth

    private lateinit var emailText: TextInputEditText
    private lateinit var passwordText: TextInputEditText
    private lateinit var password2Text: TextInputEditText
    private lateinit var nameText: TextInputEditText
    private lateinit var surnameText: TextInputEditText


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        /*disabilitp il drawer*/
        (activity as MainActivity?)!!.setDrawerEnabled(false)
        /*pulsante per tornare alla schermata di login*/
        val back = view.findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        /*mi preparo per l'iscrizione istanziando l'oggetto necessario*/
        mAuth = Firebase.auth

        var email = "";
        var password = "";
        var name = "";
        var surname = "";
        var dateofbirth = "";

        /*i dati sono inseriti in un LinearLayout all'interno di una ScrollView*/
        val scroll = view.findViewById<ScrollView>(R.id.scrollData)
        val fields = scroll.findViewById<LinearLayout>(R.id.fieldsLayout)

        emailText = fields.findViewById(R.id.emailText)
        passwordText = fields.findViewById(R.id.passwordText)
        password2Text = fields.findViewById(R.id.password2Text)
        nameText = fields.findViewById(R.id.nameText)
        surnameText = fields.findViewById(R.id.surnameText)

        /*faccio apparire il DatePickerDialog per selezionare la data di nascita dell'utente*/
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

        /*quando preme signup controllo tutti i campi*/
        val signupBtn = view.findViewById<TextView>(R.id.layoutSignup)
        signupBtn.setOnClickListener {
            signupBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            var ready = true
            val emailtmp = emailText.text.toString()
            if (emailtmp != "" && emailtmp.isEmailValid()) email = emailtmp
            else ready = false

            val pswtmp = passwordText.text.toString()
            val psw2tmp = password2Text.text.toString()
            if (pswtmp != "" && pswtmp == psw2tmp) password = pswtmp
            else ready = false

            val nametmp = nameText.text.toString()
            if (nametmp != "") name = nametmp
            else ready = false

            val surnametmp = surnameText.text.toString()
            if (surnametmp != "") surname = surnametmp
            else ready = false

            if (dateofbirth == "") ready = false

            if (ready) {
                /*passo alla funzione i dati solo se sono inseriti correttamente*/
                register(email, password, name, surname, dateofbirth)
            } else
                Toast.makeText(
                    requireContext(),
                    "Problema/i nei campi, ricontrolla",
                    Toast.LENGTH_SHORT
                ).show()
        }

        return view
    }

    /*funzione per controllare la validità della mail*/
    private fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }

    private fun register(
        email: String,
        password: String,
        name: String,
        surname: String,
        dateofbirth: String
    ) {
        /*codice standard di Firebase per la registrazione tramite email e password con l'aggiunta del mio db degli utenti*/
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = mAuth.currentUser
                val userUpdate = UserProfileChangeRequest.Builder().setDisplayName(name).build()
                user?.updateProfile(userUpdate)?.addOnCompleteListener {
                    DButil(mAuth, Firebase.firestore).addUser(surname, dateofbirth)
                }
                findNavController().navigate(R.id.mainFragment)

            } else {
                Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}