package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityAuthenticationBinding
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAuthenticationBinding>(this,R.layout.activity_authentication)
            .apply {
                viewBinding = this
                authViewModel = AuthViewModel()

            }

        viewBinding.lifecycleOwner = this


//         Done: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google

//          Done: If the user was authenticated, send him to RemindersActivity

        authViewModel.authStatus.observe(this , Observer{
            when (it) {

                AuthStatus.AUTHENTICATED -> {
                    Log.d("App" , "Authenticated")
                    val intent = Intent(this , RemindersActivity::class.java)
                    startActivity(intent)
                }
                AuthStatus.UNAUTHENTICATED -> {
                    Log.d("App" , "UnAuthenticated")

                    viewBinding.loginButton.setOnClickListener{
                        launchAuthFlow()
                    }

                }
            }
        })

//          Done: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchAuthFlow(){
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build() ,AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            SIGN_IN_RESULT_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == SIGN_IN_RESULT_CODE){
//            val res = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this,"Signed in successfully" , Toast.LENGTH_SHORT).show()
                // Start REminder activity
                val intent = Intent(this,RemindersActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    companion object {
        const val SIGN_IN_RESULT_CODE = 1000
    }
}
