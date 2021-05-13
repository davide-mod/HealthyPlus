package com.modolo.healthyplus.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.github.islamkhsh.CardSliderViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.modolo.healthyplus.DButil
import com.modolo.healthyplus.MainActivity
import com.modolo.healthyplus.R

class LoginFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 120


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        /*rendo impossibile per l'utente aprire il drawer da questa schermata*/
        (activity as MainActivity?)!!.setDrawerEnabled(false)

        /*creo la lista di carte da mandare all'adapter per visualizzarle poi nella schermata di Login
        * ho preimpostato due costruttori:
        * - uno in cui l'unico dato necessario è il titolo che verrà messo nero di default
        *    (mentre l'immagine sarà quella standard dell'app)
        * - l'altro in cui si passano il titolo, la descrizione, l'int relativo ad un colore e l'id dell'immagine
        * */
        val cardList = ArrayList<LoginCard>()
        cardList.add(
            LoginCard(
                "Healthy Plus",
                "Il contenitore di funzionalità per una vita sana",
                ContextCompat.getColor(requireContext(), R.color.main),
                R.drawable.cardimg_healthyplus
            )
        )
        cardList.add(
            LoginCard(
                "Meal Planner",
                "Diario Alimentare completo",
                ContextCompat.getColor(requireContext(), R.color.main_mealplanner),
                R.drawable.cardimg_mealplanner
            )
        )
        cardList.add(
            LoginCard(
                "Fitness Tracker",
                "Schede di allenamento più immediate",
                ContextCompat.getColor(requireContext(), R.color.main_fitnesstracker),
                R.drawable.cardimg_fitnesstracker
            )
        )
        //cardList.add(
        //LoginCard(
        //"Nuovo Modulo",
        //"Descrizione nuovo modulo",
        //ContextCompat.getColor(requireContext(), R.color.main_nuovomodulo),
        //R.drawable.cardimg_nuovomodulo
        //)
        //)
        /*oppure eventualmente*/
        //cardList.add(LoginCard("Nuovo Modulo"))

        val cards = view.findViewById<CardSliderViewPager>(R.id.viewPager)
        cards.adapter = LoginCardsAdapter(cardList)

        /*se l'utente si vuole registrare carico quella schermata*/
        val layoutSignup = view.findViewById<TextView>(R.id.layoutSignup)
        layoutSignup.setOnClickListener {
            layoutSignup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            findNavController().navigate(R.id.signupFragment)
        }

        /*mentre per il login tramite social si procede dagli appositi pulsanti*/
        val google = view.findViewById<TextView>(R.id.layoutGoogle)
        google.setOnClickListener {
            google.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            signIn() /*funzione per raggruppare il codice per il login con Google*/
        }

        val facebook = view.findViewById<TextView>(R.id.layoutFacebook)
        facebook.setOnClickListener {
            facebook.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            /*login con facebook non implementato*/
            Toast.makeText(
                requireContext(),
                "Login con Facebook non disponibile",
                Toast.LENGTH_SHORT
            ).show()
        }

        /*codice per connettersi a firebase ed autenticare l'utente*/
        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        mAuth = FirebaseAuth.getInstance()

        /*questa è il login tramite email che fa apparire un dialog dove inserire i propri dati*/
        val email = view.findViewById<TextView>(R.id.layoutEmail)
        email.setOnClickListener {
            email.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.login_dialog_mail)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            /*recupero i campi dal layout*/
            val emailText = dialog.findViewById<TextInputEditText>(R.id.loginEmailText)
            val passwordText = dialog.findViewById<TextInputEditText>(R.id.loginPasswordText)
            val loginBtn = dialog.findViewById<TextView>(R.id.loginBtn)
            loginBtn.setOnClickListener {
                loginBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                val emailTmp = emailText.text.toString()
                val passwordTmp = passwordText.text.toString()
                if (emailTmp != "" && passwordTmp != "") { /*se i campi sono completi provo ad effetturare il login*/
                    val auth = Firebase.auth
                    auth.signInWithEmailAndPassword(emailTmp, passwordTmp)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) { /*se il login avviene con successo, carico il fragment*/
                                findNavController().navigate(R.id.mainFragment)
                                dialog.dismiss()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Login fallito",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else Toast.makeText(requireContext(), "Completa i campi", Toast.LENGTH_SHORT)
                    .show()
            }
            dialog.show()
        }
        return view
    }

    /*da qui in poi c'è il codice per l'autenticazione tramite Account Google*/
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    Toast.makeText(requireContext(),"Login avvenuto con successo", Toast.LENGTH_SHORT).show()
                    /*Google Sign In was successful, authenticate with Firebase*/
                    val account = task.getResult(ApiException::class.java)!!
                    Log.i("hp_LoginFragment", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    /*Google Sign In failed*/
                    Toast.makeText(requireContext(),"$e", Toast.LENGTH_SHORT).show()
                    Log.i("hp_LoginFragment", "Google sign in failed", e)
                }
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    /*autenticazione avvenuta con successo, aggiungo le info al database*/
                    val userUpdate =
                        UserProfileChangeRequest.Builder().setDisplayName(user?.displayName).build()
                    user?.updateProfile(userUpdate)?.addOnCompleteListener {
                        DButil(mAuth, Firebase.firestore).addUser("", "")
                    }
                    /*Sign in success, update UI with the signed-in user's information*/
                    Log.i("hp_LoginFragment", "signInWithCredential:success")
                    findNavController().navigate(R.id.mainFragment)
                } else {
                    /*If sign in fails, display a message to the user.*/
                    Toast.makeText(requireContext(), "${task.exception}", Toast.LENGTH_SHORT).show()
                    Log.i("hp_LoginFragment", "signInWithCredential:failure", task.exception)
                }
            }
    }


}