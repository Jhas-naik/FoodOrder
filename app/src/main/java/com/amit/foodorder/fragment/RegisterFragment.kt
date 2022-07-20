package com.amit.foodorder.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
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


class RegisterFragment(val contextParam: Context) : Fragment() {

    lateinit var  etName:EditText
    lateinit var etEmail:EditText
    lateinit var etMobile:EditText
    lateinit var etAddress:EditText
    lateinit var etPassword:EditText
    lateinit var etConfirmPassword:EditText
    lateinit var btnRegister:Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register,container,false)

        etName=view.findViewById(R.id.etRegisterName)
        etEmail=view.findViewById(R.id.etRegisterEmail)
        etMobile=view.findViewById(R.id.etRegisterMobileNumber)
        etAddress=view.findViewById(R.id.etRegisterDeliveryAddress)
        etPassword=view.findViewById(R.id.etRegisterPassword)
        etConfirmPassword=view.findViewById(R.id.etRegisterConfirmPassword)
        btnRegister=view.findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener{
            val sharedPreferences=contextParam.getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("isLoggedIn",false).apply()

            if(ConnectionManager().checkConnectivity(activity as Context)){
                try{
                    val registerUser=JSONObject()
                    registerUser.put("name",etName.text)
                    registerUser.put("mobile_number",etMobile.text)
                    registerUser.put("email",etEmail.text)
                    registerUser.put("password",etPassword.text)
                    registerUser.put("address",etAddress.text)

                    val queue=Volley.newRequestQueue(activity as Context)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonObjectRequest=object :JsonObjectRequest(Method.POST,url,registerUser,Response.Listener {
                        val response=it.getJSONObject("data")
                        val success=response.getBoolean("success")
                        if(success){
                            val data=response.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
                            sharedPreferences.edit().putString("user_id",data.getString("user_id")).apply()
                            sharedPreferences.edit().putString("name",data.getString("name")).apply()
                            sharedPreferences.edit().putString("email",data.getString("email")).apply()
                            sharedPreferences.edit().putString("mobile_number",data.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address",data.getString("address")).apply()

                            Toast.makeText(contextParam,"Registered successfully",Toast.LENGTH_LONG).show()

                            val intent=Intent(activity as Context,MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()

                        }else{
                            val responseMessage=response.getString("errorMessage")
                            Toast.makeText(contextParam,responseMessage.toString(),Toast.LENGTH_SHORT).show()
                        }
                    },
                        Response.ErrorListener {
                            println("Error is $it")
                            Toast.makeText(contextParam,"some error occured",Toast.LENGTH_SHORT).show()
                        }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers=HashMap<String,String>()
                            headers["Content-type"]="application/json"
                            headers["token"]="9120e979304c14"
                            return headers
                        }
                    }
                    queue.add(jsonObjectRequest)
                }catch (e:JSONException){
                    Toast.makeText(contextParam,"Json exception occured",Toast.LENGTH_LONG).show()
                }
            }else{
                val dialog=AlertDialog.Builder(activity as Context)
                dialog.setTitle("No Internet")
                dialog.setMessage("Check internet")
                dialog.setPositiveButton("Open settings"){text,listner->
                    val settingintent= Intent(Settings.ACTION_SETTINGS)
                    startActivity(settingintent)
                }
                dialog.setNegativeButton("Exit"){text,listner->
                    ActivityCompat.finishAffinity(activity as Activity)
                }
                dialog.create()
                dialog.show()
            }
        }


        return view
    }


}