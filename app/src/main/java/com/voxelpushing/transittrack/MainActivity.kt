package com.voxelpushing.transittrack

import android.app.Activity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
import com.voxelpushing.transittrack.models.ResponseState
import com.voxelpushing.transittrack.models.VehicleStatus
import com.voxelpushing.transittrack.util.formatLocation
import com.voxelpushing.transittrack.vm.TrackingViewModel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Update UI when results are fetched
        val model = ViewModelProviders.of(this).get(TrackingViewModel::class.java)
        model.getStatus().observe(this, Observer { data ->
            when (data.responseState) {
                ResponseState.SUCCESS -> data.vehicleStatus?.let {
                    findViewById<View>(R.id.loadingIndicator).visibility = View.GONE
                    updateCard(it)
                }
                ResponseState.ERROR -> {
                    findViewById<View>(R.id.loadingIndicator).visibility = View.GONE
                    findViewById<View>(R.id.statusDisplay).visibility = View.GONE
                    Snackbar.make(findViewById(R.id.track_button), R.string.error, LENGTH_SHORT).show()
                    findViewById<View>(R.id.emptyResult).visibility = View.VISIBLE
                }
                ResponseState.LOADING -> {
                    findViewById<View>(R.id.emptyResult).visibility = View.GONE
                    findViewById<View>(R.id.loadingIndicator).visibility = View.VISIBLE
                }
            }
        })

        // Set button to track vehicle
        findViewById<Button>(R.id.track_button).setOnClickListener {
            val textInput = findViewById<EditText>(R.id.textInput)
            val vehicleId = textInput?.text?.toString()
            model.getVehicleStatus(vehicleId)
            textInput.text.clear()
            textInput.clearFocus()
            hideKeyboard()
        }

        // Show all results
        findViewById<Button>(R.id.viewAllResultsButton).setOnClickListener {
            FullScreenDialogFragment().show(supportFragmentManager, null)
        }

        setSupportActionBar(findViewById(R.id.appBar))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.acknowledge -> {
                AcknowledgementDialogFragment().show(supportFragmentManager, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateCard(vehicleStatus: VehicleStatus) {
        findViewById<View>(R.id.statusDisplay)?.visibility = View.VISIBLE

        findViewById<TextView>(R.id.vehicle_number)?.text =
            getString(R.string.vehicle_number, vehicleStatus.id.toString())

        findViewById<TextView>(R.id.routeNumber)?.apply {
            text = vehicleStatus.getRouteNumber(context)
        }

        findViewById<TextView>(R.id.routeDesc)?.apply {
            if (vehicleStatus.dirDescription.isNullOrEmpty()) {
                visibility = View.GONE
            } else {
                visibility = View.VISIBLE
                text = vehicleStatus.dirDescription
            }
        }

        findViewById<TextView>(R.id.statusLocation)?.formatLocation(
            this,
            vehicleStatus.lat,
            vehicleStatus.lon
        )
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        var view = currentFocus
        if (view == null) {
            view = View(this)
        }
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
