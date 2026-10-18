package com.gezimos.katapult.util

import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkInfoHelperTest {

    private fun info(city: String? = "Zürich", country: String? = "CH", vpn: Boolean = false) =
        NetworkInfoHelper.Info(ip = "185.1.2.3", city = city, country = country, vpn = vpn)

    @Test
    fun formatsPlaceIpAndVpnLabelWhenVpnIsUp() {
        assertEquals("Zürich, CH - 185.1.2.3 - VPN UP", NetworkInfoHelper.format(info(vpn = true), "VPN UP"))
    }

    @Test
    fun omitsVpnLabelWhenVpnIsDown() {
        assertEquals("Zürich, CH - 185.1.2.3", NetworkInfoHelper.format(info(), "VPN UP"))
    }

    @Test
    fun showsCountryAloneWhenCityIsUnknown() {
        assertEquals("CH - 185.1.2.3", NetworkInfoHelper.format(info(city = null), "VPN UP"))
    }

    @Test
    fun showsOnlyIpWhenLocationIsUnknown() {
        assertEquals("185.1.2.3", NetworkInfoHelper.format(info(city = null, country = null), "VPN UP"))
    }
}
