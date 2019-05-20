package com.voxelpushing.transittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.voxelpushing.transittrack.models.VehicleStatus
import com.voxelpushing.transittrack.util.formatLocation
import com.voxelpushing.transittrack.vm.DatabaseViewModel
import kotlinx.android.synthetic.main.list_item_vehicle_result_card.view.*
import java.text.DateFormat
import java.util.*

class PastResultsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: VehicleStatusListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_past_results_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        listAdapter = VehicleStatusListAdapter()
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }

    override fun onStart() {
        super.onStart()
        val model = ViewModelProviders.of(activity!!).get(DatabaseViewModel::class.java)
        model.getAllVehicleResults().observe(this, Observer { data ->
            listAdapter.setData(data)
            view?.findViewById<View>(R.id.recyclerView)?.visibility = View.VISIBLE
            view?.findViewById<View>(R.id.loadingIndicator)?.visibility = View.GONE
        })
    }

    private inner class VehicleStatusListAdapter : RecyclerView.Adapter<VehicleStatusViewHolder>() {

        private var data: List<VehicleStatus>? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleStatusViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.list_item_vehicle_result_card, parent, false)
            return VehicleStatusViewHolder(view)
        }

        override fun onBindViewHolder(holder: VehicleStatusViewHolder, position: Int) {
            data?.get(position)?.let {
                holder.bindView(it)
            }
        }

        override fun getItemCount(): Int {
            return data?.size ?: 0
        }

        fun setData(vehicleStatuses: List<VehicleStatus>) {
            data = vehicleStatuses
            notifyDataSetChanged()
        }
    }

    private inner class VehicleStatusViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val vehicleNumberTV: TextView = itemView.vehicleNumber
        private val timeTV: TextView = itemView.statusTime
        private val routeNumberTV: TextView = itemView.routeNumber
        private val dirDescTV: TextView = itemView.dirDesc
        private val statusLocationTV: TextView = itemView.statusLocation

        private val dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

        fun bindView(vehicleStatus: VehicleStatus) {
            vehicleNumberTV.text = getString(R.string.vehicle_number, vehicleStatus.id.toString())
            timeTV.text = dateFormat.format(Date(vehicleStatus.time))
            routeNumberTV.text = vehicleStatus.getRouteNumber(context)
            dirDescTV.apply {
                if (vehicleStatus.dirDescription.isNullOrEmpty()) {
                    visibility = View.GONE
                } else {
                    text = vehicleStatus.dirDescription
                }
            }
            statusLocationTV.formatLocation(context, vehicleStatus.lat, vehicleStatus.lon)
        }
    }
}