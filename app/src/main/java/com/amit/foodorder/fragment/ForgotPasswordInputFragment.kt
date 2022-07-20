package com.amit.foodorder.fragment

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
import com.amit.foodorder.R
import com.amit.foodorder.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordInputFragment(val contextParam: Context) : Fragment() {

    lateinit var etNumber:EditText
    lateinit var etEmail:EditText
    lateinit var btnNext:Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_forgot_password_input,container,false)

        etNumber=view.findViewById(R.id.etNumberForgotInput)
        etEmail=view.findViewById(R.id.etEmailForgotInput)
        btnNext=view.findViewById(R.id.btnNextForgotInput)

        btnNext.setOnClickListener {
            if (etNumber.text.isBlank() || etNumber.text.length != 10){
                etNumber.error="Invalid Mobile Number"
            }else{
                if(etEmail.text.isBlank()){
                    etEmail.error="Email Missing"
                }else{
                    if (ConnectionManager().checkConnectivity(activity as Context)){
                        try {
                            val loginUser=JSONObject()
                            loginUser.put("mobile_number",etNumber.text)
                            loginUser.put("email",etEmail.text)

                            println(loginUser.getString("mobile_number"))
                            println(loginUser.getString("email"))

                            val queue=Volley.newRequestQueue(activity as Context)
                            val url="http://13.235.250.119/v2/forgot_password/fetch_result"

                            val jsonObjectRequest=object :JsonObjectRequest(Method.POST,url,loginUser,Response.Listener {
                                val response=it.getJSONObject("data")
                                val success=response.getBoolean("success")
                                if (success){
                                    val firstTry=response.getBoolean("first_try")

                                    if(firstTry){
                                        Toast.makeText(contextParam,"OTP sent",Toast.LENGTH_SHORT).show()

                                        val transaction=fragmentManager?.beginTransaction()
                                        transaction?.replace(
                                            R.id.loginFrameLayout,ForgotPasswordFragment(contextParam,etNumber.text.toString())
                                        )
                                        transaction?.commit()

                                    }else{
                                        Toast.makeText(contextParam,"OTP sent already",Toast.LENGTH_SHORT).show()

                                        val transaction=fragmentManager?.beginTransaction()
                                        transaction?.replace(R.id.loginFrameLayout,ForgotPasswordFragment(contextParam,etNumber.text.toString()))
                                        transaction?.commit()
                                    }
                                }else{
                                    val responseMessage=response.getString("errorMessage")
                                    Toast.makeText(contextParam,responseMessage.toString(),Toast.LENGTH_SHORT).show()
                                }
                            },
                                Response.ErrorListener {
                                    Toast.makeText(contextParam,"Volley error occurred",Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(contextParam,"Json error occurred",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        val dialogBox=androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                        dialogBox.setTitle("No Internet")
                        dialogBox.setMessage("Check internet connectivity")
                        dialogBox.setPositiveButton("Open settings"){text,listner ->
                            val settingintent=Intent(Settings.ACTION_SETTINGS)
                            startActivity(settingintent)
                        }
                        dialogBox.setNegativeButton("Exit"){text,listner ->
                            activity?.finishAffinity()
                        }
                        dialogBox.create()
                        dialogBox.show()
                    }
                }
            }
        }

        return view
    }


}