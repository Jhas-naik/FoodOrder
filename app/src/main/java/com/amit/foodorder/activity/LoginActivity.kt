package com.amit.foodorder.activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amit.foodorder.R
import com.amit.foodorder.fragment.LoginFragment

class LoginActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
        val isLoggedIn=sharedPreferences.getBoolean("isLoggedIn",false)
        openLoginFragment()

        if(isLoggedIn){
            val intent=Intent(this@LoginActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun openLoginFragment(){
        supportFragmentManager.beginTransaction().replace(R.id.loginFrameLayout, LoginFragment(this))
            .commit()
    }
    override fun onBackPressed() {
        when(supportFragmentManager.findFragmentById(R.id.loginFrameLayout)){
            !is LoginFragment -> openLoginFragment()
            else -> super.onBackPressed()
        }
    }
    fun savePreference(){
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
    }
}