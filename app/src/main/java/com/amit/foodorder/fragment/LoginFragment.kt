package com.amit.foodorder.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextParams
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.amit.foodorder.R
import com.amit.foodorder.activity.MainActivity
import com.amit.foodorder.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject


class LoginFragment(val contextParam: Context) : Fragment() {
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView
    lateinit var etNumber: EditText
    lateinit var etPassword: EditText
    lateinit var btnLogin: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)


        txtForgotPassword = view.findViewById(R.id.txtForgotPassword)
        txtForgotPassword.setOnClickListener {
            Toast.makeText(context, "Clicked on forgot password", Toast.LENGTH_SHORT).show()
            openForgotPasswordFragment()
        }

        txtRegister = view.findViewById(R.id.txtRegister)
        txtRegister.setOnClickListener {
            openRegesterFragment()
        }

        etNumber = view.findViewById(R.id.etMobileNumber)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            Toast.makeText(contextParam,"clicked",Toast.LENGTH_SHORT).show()
            if (etNumber.text.isBlank() || etNumber.text.length != 10){
                etNumber.error="Enter valid Number"
            }else{
                if (etPassword.text.isBlank() || etPassword.text.length<=4){
                    etPassword.error="Enter valid Password"
                }else{
                    logInFun()
                }
            }
        }

        return view
    }

    fun openForgotPasswordFragment() {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.loginFrameLayout,
            ForgotPasswordInputFragment(activity as Context)
        )
        transaction?.commit()
    }

    fun openRegesterFragment() {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(
            R.id.loginFrameLayout,
            RegisterFragment(activity as Context)
        )?.commit()
    }

    fun userSuccessfullyLoggedIn() {
        val intent = Intent(activity as Context, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    fun logInFun() {
        val sharedPreferences = contextParam.getSharedPreferences(
            getString(R.string.preference_file_name),
            Context.MODE_PRIVATE
        )
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            try {
                val loginUser=JSONObject()
                loginUser.put("mobile_number",etNumber.text)
                loginUser.put("password",etPassword.text)

                val queue=Volley.newRequestQueue(activity as Context)
                val url="http://13.235.250.119/v2/login/fetch_result"
                val jsonObjectRequest = object : JsonObjectRequest(Method.POST,url,loginUser,
                    Response.Listener {
                        val respone=it.getJSONObject("data")
                        val success=respone.getBoolean("success")
                        if(success){
                            val data=respone.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                            sharedPreferences.edit().putString("user_id",data.getString("user_id")).apply()
                            sharedPreferences.edit().putString("name", data.getString("name")).apply()
                            sharedPreferences.edit().putString("email", data.getString("email")).apply()
                            sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address", data.getString("address")).apply()

                            Toast.makeText(contextParam,"Welcome"+data.getString("name"),Toast.LENGTH_SHORT).show()

                            userSuccessfullyLoggedIn()

                        }else{
                            Toast.makeText(contextParam,"Some error occurred",Toast.LENGTH_LONG).show()
                        }
                    },Response.ErrorListener {
                        Toast.makeText(contextParam,"Some Error occurred",Toast.LENGTH_SHORT).show()
                    }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers=HashMap<String,String>()
                        headers["Content-type"]="application/json"
                        headers["token"]="9120e979304c14"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                Toast.makeText(contextParam, "JsonException occurred", Toast.LENGTH_SHORT).show()
            }
        }else{
            val dialog=AlertDialog.Builder(activity as Context)
            dialog.setTitle("No Internet")
            dialog.setMessage("Check Internet connection")
            dialog.setPositiveButton("Open settings"){text,listner->
                val settingIntent=Intent(Settings.ACTION_SETTINGS)
                startActivity(settingIntent)
            }
            dialog.setNegativeButton("Exit"){text,listner->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }


}