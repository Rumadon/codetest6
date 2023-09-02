package com.sunshinemoose.test6

import androidx.core.text.isDigitsOnly
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class MainPresenter(private val actionListener: Contract.ViewListener) : Contract.Presenter {
    private var orderedPairs: Map<String, String>? = null

    override fun viewCreated() {
        orderedPairs = processJson(actionListener.getInputStream())?.let {
            calculateOrder(it.drivers, it.shipments)
        }
        orderedPairs?.let {
            actionListener.updateList(it.keys.toList())
        }
    }

    override fun onItemClicked(shipment: String) {
        orderedPairs?.let { map ->
            map[shipment]?.let { driver ->
                actionListener.showDetailView(driver, shipment)
            }
        }
    }

    private fun processJson(inputStream: InputStream): DataHolder? {
        val json = loadJSONFile(inputStream)?.let {
            JSONObject(it)
        } ?: return null
        val shipments = parseJSONShipments(json)
        val drivers = parseJSONDrivers(json)
        return DataHolder(shipments, drivers)
    }

    private fun loadJSONFile(inputStream: InputStream): String? {
        return try {
            val size = inputStream.available()
            val byteArray = ByteArray(size)
            inputStream.read(byteArray)
            inputStream.close()
            byteArray.toString(Charsets.UTF_8)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun parseJSONShipments(json: JSONObject): List<String> {
        return json.getJSONArray("shipments").toList()
    }

    private fun parseJSONDrivers(json: JSONObject): List<String> {
        return json.getJSONArray("drivers").toList()
    }

    private fun <T> JSONArray.toList(): List<T> {
        val out = mutableListOf<T>()
        for (i in 0 until this.length()) {
            (this.get(i) as? T)?.let {
                out.add(it)
            }
        }
        return out
    }

    private fun calculateOrder(drivers: List<String>, shipments: List<String>): Map<String, String> {
        val driverSortings = drivers.map { mapDrivers(it) }
        val shipmentSortings = shipments.map { mapShipments(it) }
        return sortItems(driverSortings, shipmentSortings)
    }

    private fun mapDrivers(driver: String): DriverSorting {
        var vowelCount = 0
        var consonantCount = 0
        driver.forEach { it: Char ->
            if (vowels.contains(it)) {
                vowelCount++
            } else {
                consonantCount++
            }

        }
        return DriverSorting(driver, vowelCount.times(1.5).toInt(), consonantCount, driver.length)
    }

    private fun mapShipments(shipment: String): ShipmentSorting {
        return ShipmentSorting(shipment, getStreetNameCharCount(shipment))
    }

    private fun getStreetNameCharCount(address: String): Int {
        var split = address.split(" ")
        split.takeLast(split.size - 1) //remove street number
        if (split.last().isDigitsOnly()) {
            split = split.dropLast(2)
        }
        return split.sumOf { it.length }
    }

    private fun sortItems(
        drivers: List<DriverSorting>,
        shipments: List<ShipmentSorting>
    ): Map<String, String> { //map of drivers to shipments
        val possibilitiesMap = mutableMapOf<Pair<String, String>, Int>()
        drivers.forEach { driver ->
            shipments.forEach { shipment ->
                possibilitiesMap[Pair(driver.driver, shipment.shipment)] = calculateScore(shipment, driver)
            }
        }
        val sortedmap = possibilitiesMap.toList().sortedBy { it.second }

        val outMap = mutableMapOf<String, String>()

        sortedmap.forEach { it: Pair<Pair<String, String>, Int> ->
            val pairing = it.first
            if (!outMap.containsKey(pairing.first) && !outMap.containsValue(pairing.second)) {
                outMap[pairing.first] = pairing.second
            }
        }
        return outMap

    }

    /**
    If the length of the shipment's destination street name is even, the base suitability score (SS)
    is the number of vowels in the driver’s name multiplied by 1.5.
    If the length of the shipment's destination street name is odd, the base SS is the number of
    consonants in the driver’s name multiplied by 1.
    If the length of the shipment's destination street name shares any common factors (besides 1)
    with the length of the driver’s name, the SS is increased by 50% above the base SS.
     */

    private fun calculateScore(
        shipmentSorting: ShipmentSorting, driverSorting: DriverSorting
    ): Int {
        val baseSS = if (shipmentSorting.lenStreetName % 2 == 0) {
            driverSorting.vowelsDriversName1_5
        } else {
            driverSorting.consonantsDriversName
        }
        return if (shipmentSorting.lenStreetName.factors().containsAny(
                driverSorting.consonantsDriversName.factors()
            )
        ) {
            (baseSS * 1.5).toInt()
        } else {
            baseSS
        }
    }

    private fun Int.factors(): List<Int> {
        val output = mutableListOf<Int>()
        for (i in this until 1) {
            if (this % i == 0) {
                output.add(i)
            }
        }
        return output
    }

    private fun List<Int>.containsAny(otherList: List<Int>): Boolean = this.any {
        it in otherList
    }

    private companion object {
        val vowels = listOf('a', 'e', 'i', 'o', 'u')
    }
}
