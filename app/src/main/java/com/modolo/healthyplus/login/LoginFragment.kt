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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.github.islamkhsh.CardSliderViewPager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
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
import com.modolo.healthyplus.signup.SignupFragment

class LoginFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    private val RC_SIGN_IN = 120


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        (activity as MainActivity?)!!.setDrawerEnabled(false)

        val lista = ArrayList<String>()
        lista.add("Healthy Plus\nIl contenitore di funzionalità per una vita sana")
        lista.add("Meal Planner\nDiario Alimentare completo per aiutarti a programmare i pasti")
        lista.add("Fitness Tracker\nL'allenamento in palestra ancora più immediato")
        val cards = view.findViewById<CardSliderViewPager>(R.id.viewPager)
        cards.adapter = LoginCardsAdapter(lista)
        val layoutSignup = view.findViewById<TextView>(R.id.layoutSignup)
        layoutSignup.setOnClickListener {
            layoutSignup.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            findNavController().navigate(R.id.signupFragment)
        }

        val gso: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        mAuth = FirebaseAuth.getInstance()

        val google = view.findViewById<TextView>(R.id.layoutGoogle)
        google.setOnClickListener {
            google.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            signIn()
        }

        val facebook = view.findViewById<TextView>(R.id.layoutFacebook)
        facebook.setOnClickListener {
            facebook.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            Toast.makeText(
                requireContext(),
                "Login con Facebook non disponibile",
                Toast.LENGTH_SHORT
            ).show()
        }

        val email = view.findViewById<TextView>(R.id.layoutEmail)
        email.setOnClickListener {
            email.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.login_dialog_mail)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val emailText = dialog.findViewById<TextInputEditText>(R.id.loginEmailText)
            val passwordText = dialog.findViewById<TextInputEditText>(R.id.loginPasswordText)
            val loginBtn = dialog.findViewById<TextView>(R.id.loginBtn)
            loginBtn.setOnClickListener {
                loginBtn.startAnimation(AnimationUtils.loadAnimation(context, R.anim.alpha))
                val emailTmp = emailText.text.toString()
                val passwordTmp = passwordText.text.toString()
                if (emailTmp != "" && passwordTmp != "") {
                    val mAuth = Firebase.auth
                    mAuth.signInWithEmailAndPassword(emailTmp, passwordTmp)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
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

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.i("devdebug", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Log.i("devdebug", "Google sign in failed", e)
                }
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login avvenuto con successo", Toast.LENGTH_SHORT)
                        .show()
                    val user = mAuth.currentUser
                    val userUpdate =
                        UserProfileChangeRequest.Builder().setDisplayName(user.displayName).build()
                    user?.updateProfile(userUpdate)?.addOnCompleteListener {
                        DButil(mAuth, Firebase.firestore).addUser("", "")
                    }
                    // Sign in success, update UI with the signed-in user's information
                    Log.i("devdebug", "signInWithCredential:success")
                    findNavController().navigate(R.id.mainFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.i("devdebug", "signInWithCredential:failure", task.exception)
                }
            }
    }


}