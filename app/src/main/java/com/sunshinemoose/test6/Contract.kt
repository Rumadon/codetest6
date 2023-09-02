package com.sunshinemoose.test6

import java.io.InputStream

interface Contract {

    interface ViewListener {
        fun updateList(items: List<String>)
        fun showDetailView(driver: String, shipment: String)
        fun getInputStream(): InputStream
    }

    interface Presenter {
        fun viewCreated()
        fun onItemClicked(shipment: String)
    }
}