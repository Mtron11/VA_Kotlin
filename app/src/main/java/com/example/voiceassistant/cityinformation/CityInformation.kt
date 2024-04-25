package com.example.voiceassistent.cityinformation

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "message", strict = false)
class CityInformation {
    @field:Element(name = "city")
    var city: City? = null
}

@Root(name = "city", strict = false)
class City {
    @field:ElementList(inline = true, name = "msg")
    var cityMsgs: List<Town>? = null
}

@Root(name = "msg", strict = false)
class Town {
    @field:Element(name = "name")
    var name: String? = null

    @field:Element(name = "english")
    var english: String? = null

    @field:Element(name = "country")
    var country: String? = null

    @field:Element(name = "full_name")
    var fullName: String? = null

    @field:Element(name = "url")
    var url: String? = null
}