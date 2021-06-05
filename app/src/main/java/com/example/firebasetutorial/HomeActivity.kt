package com.example.firebasetutorial

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import bolts.Task
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //Setup

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup (email ?: "", provider ?: "")


        //Guardado de datos

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()

        // Remote Config
        forzarErrorButton.visibility = View.INVISIBLE
        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (task.isSuccessful) {
                val showErrorButton = Firebase.remoteConfig.getBoolean("show_error_button")
                val errorButonText = Firebase.remoteConfig.getString("error_button_text")

                if (showErrorButton) {
                    forzarErrorButton.visibility = View.VISIBLE
                }
                forzarErrorButton.text = errorButonText

            }
        }

        fun setup(email: String, provider: String) {

            title = "Inicio / Home"
            emailTextView.text = email
            providerTextView.text = provider

            logOutButton.setOnClickListener {

                //Borrar datos

                val prefs = getSharedPreferences(
                    getString(R.string.prefs_file),
                    Context.MODE_PRIVATE
                ).edit()
                prefs.clear()
                prefs.apply()

                if (provider == ProviderType.FACEBOOK.name) {
                    LoginManager.getInstance().logOut()
                }

                FirebaseAuth.getInstance().signOut()
                onBackPressed()
            }
            forzarErrorButton.setOnClickListener {
                // Forzar Error
                throw RuntimeException("Forzar Error")
            }
        }
    }
}