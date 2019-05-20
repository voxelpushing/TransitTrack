package com.voxelpushing.transittrack.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.TextView
import com.voxelpushing.transittrack.R
import java.text.MessageFormat

fun TextView.formatLocation(context: Context?, lat: Double, lon: Double) {
    if (lat == 0.0 || lon == 0.0) {
        // If no location, hide view
        visibility = View.GONE
    } else {
        context?.let {
            // Otherwise display data and make the location accessible
            text = context.getString(
                R.string.location,
                lat.toString(),
                lon.toString()
            )
            setOnClickListener {
                val data = MessageFormat.format(
                    "geo:{0},{1}?q={0},{1}",
                    lat.toString(),
                    lon.toString()
                )
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data))
                context.startActivity(intent)
            }
        }
    }
}