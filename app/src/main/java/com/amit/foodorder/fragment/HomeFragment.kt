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
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amit.foodorder.R
import com.amit.foodorder.adapter.DashboardFragmentAdapter
import com.amit.foodorder.model.Restaurant
import com.amit.foodorder.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException


class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerAdapter: DashboardFragmentAdapter
    var resInfoList = arrayListOf<Restaurant>(

    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        progressLayout = view.findViewById(R.id.progressLayoutHome)
        progressLayout.visibility = View.VISIBLE

        recyclerView = view.findViewById(R.id.recyclerViewHome)
        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val response = it.getJSONObject("data")
                        val success = response.getBoolean("success")
                        if (success) {
                            val data = response.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val resJSONObject = data.getJSONObject(i)
                                val resObject = Restaurant(
                                    resJSONObject.getString("id"),
                                    resJSONObject.getString("name"),
                                    resJSONObject.getString("rating"),
                                    resJSONObject.getString("cost_for_one"),
                                    resJSONObject.getString("image_url")
                                )
                                resInfoList.add(resObject)

                                recyclerAdapter =
                                    DashboardFragmentAdapter(activity as Context, resInfoList)
                                recyclerView.adapter = recyclerAdapter
                                recyclerView.layoutManager=layoutManager
                                recyclerView.addItemDecoration(
                                    DividerItemDecoration(
                                        recyclerView.context,
                                        (layoutManager as LinearLayoutManager).orientation
                                    )
                                )
                            }
                        } else {
                            Toast.makeText(activity as Context, "Database error", Toast.LENGTH_LONG)
                                .show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "JsonException Error",
                            Toast.LENGTH_LONG
                        ).show()
                        println("Error is ${it}")
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, "Volley Error occurred", Toast.LENGTH_LONG)
                        .show()
                    println("Error is ${it}")
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "9120e979304c14"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        } else {
            progressLayout.visibility = View.GONE
            var dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("InterNet not available")
            dialog.setPositiveButton("Open settings") { text, listner ->
                val settingintent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingintent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
        return view
    }


}