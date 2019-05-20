package com.voxelpushing.transittrack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment


class FullScreenDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_full_screen_layout, container, false)

        // Set title bar
        view.findViewById<Toolbar>(R.id.appBar)?.apply {
            setTitle(R.string.pastResults)
            setNavigationIcon(R.drawable.ic_baseline_arrow_back_24px)
            setNavigationOnClickListener { this@FullScreenDialogFragment.dismiss() }
        }

        loadListFragment()

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    private fun loadListFragment() {
        // Create new fragment and transaction
        val newFragment = PastResultsListFragment()
        val transaction = childFragmentManager.beginTransaction()

        transaction.apply {
            replace(R.id.fragmentContainer, newFragment)
            addToBackStack(null)
            commit()
        }
    }
}