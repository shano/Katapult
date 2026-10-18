package com.gezimos.katapult.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object NetworkInfoHelper {

    data class Info(
        val ip: String,
        val city: String?,
        val country: String?,
        val vpn: Boolean,
    )

    private const val LOOKUP_URL = "https://ipinfo.io/json"

    // Bound to [network] so the IP shown is the one the VPN flag describes.
    fun read(context: Context, network: Network): Info? {
        val vpn = context.getSystemService(ConnectivityManager::class.java)
            ?.getNetworkCapabilities(network)
            ?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
        val connection = try {
            network.openConnection(URL(LOOKUP_URL)) as HttpURLConnection
        } catch (_: Exception) {
            return null
        }
        return try {
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("Accept", "application/json")
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            val ip = json.optString("ip").ifBlank { return null }
            Info(
                ip = ip,
                city = json.optString("city").ifBlank { null },
                country = json.optString("country").ifBlank { null },
                vpn = vpn,
            )
        } catch (_: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    fun format(info: Info, vpnLabel: String): String {
        val place = listOfNotNull(info.city, info.country).joinToString(", ")
        return listOfNotNull(
            place.ifEmpty { null },
            info.ip,
            if (info.vpn) vpnLabel else null,
        ).joinToString(" - ")
    }
}
