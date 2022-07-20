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

class ForgotPasswordFragment(val contextParam: Context, val mobileNumber: String) : Fragment() {

    lateinit var etOTP: EditText
    lateinit var etNewPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var exitApp: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        etOTP = view.findViewById(R.id.etOTP)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        exitApp = view.findViewById(R.id.fogPasswordExitApp)

        btnSubmit.setOnClickListener {
            if (etOTP.text.isBlank()) {
                etOTP.error = "OTP missing"
            } else {
                if (ConnectionManager().checkConnectivity(activity as Context)) {
                    try {
                        val loginUser = JSONObject()
                        loginUser.put("mobile_number", mobileNumber)
                        loginUser.put("password", etNewPassword.text.toString())
                        loginUser.put("otp", etOTP.text.toString())

                        val queue = Volley.newRequestQueue(activity as Context)
                        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                        val jsonObjectRequest = object :
                            JsonObjectRequest(Method.POST, url, loginUser, Response.Listener {
                                val response=it.getJSONObject("data")
                                val success=response.getBoolean("success")
                                if (success){
                                    val serverMessage=response.getString("successMessage")
                                    Toast.makeText(contextParam,serverMessage,Toast.LENGTH_SHORT).show()

                                    val transaction=fragmentManager?.beginTransaction()
                                    transaction?.replace(R.id.frameLayout,LoginFragment(contextParam))
                                    transaction?.commit()
                                }else{
                                    val responseMessage=response.getString("errorMessage")
                                    Toast.makeText(contextParam,responseMessage,Toast.LENGTH_SHORT).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    contextParam,
                                    "Volley Error occurred",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"]="9120e979304c14"
                                return headers

                            }
                        }
                        queue.add(jsonObjectRequest)
                    } catch (e: JSONException) {
                        Toast.makeText(contextParam, "Json error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    val dialog = AlertDialog.Builder(activity as Context)
                    dialog.setTitle("no Internet")
                    dialog.setMessage("Check Internet Connection")
                    dialog.setPositiveButton("Open settings") { text, listner ->
                        val settingIntent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(settingIntent)
                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        activity?.finishAffinity()
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

        exitApp.setOnClickListener {
            activity?.finishAffinity()
        }

        return view
    }

}