package com.sunshinemoose.test6

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.sunshinemoose.test6.databinding.FragmentDetailsBinding

/**
 * Simple dialog fragment to show text passed in as extras
 */
class DetailFragment : DialogFragment() {
    private var binding: FragmentDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentDetailsBinding.inflate(inflater, container, false).also {
        binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(DRIVER_KEY)?.let {
            binding?.driver?.text = it
        }
        arguments?.getString(SHIPMENT_KEY)?.let {
            binding?.shipment?.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        private const val DRIVER_KEY = "driver"
        private const val SHIPMENT_KEY = "shipment"

        fun getFrag(driver: String, shipment: String): DetailFragment {
            val bundle = Bundle().apply {
                putString(DRIVER_KEY, driver)
                putString(SHIPMENT_KEY, shipment)
            }
            return DetailFragment().apply {
                arguments = bundle
            }
        }
    }
}