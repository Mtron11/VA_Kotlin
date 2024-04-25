package com.example.voiceassistent.forecast

import org.simpleframework.xml.Attribute
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/*#region Для json
class Forecast : Serializable {
    @SerializedName("main")
    @Expose
    var main: Main? = null

    @SerializedName("weather")
    @Expose
    var weather: ArrayList<Weather?> = ArrayList()
}

class Main {
    @SerializedName("temp")
    @Expose
    var temp: Double? = null
}

class Weather {
    @SerializedName("description")
    @Expose
    var description: String? = null
}
#endregion*/

@Root(name = "current", strict = false)
class Forecast {
    @field:Element(name = "temperature")
    var temperature: Temperature? = null

    @field:Element(name = "weather")
    var weather: Weather? = null
}

@Root(name = "temperature", strict = false)
class Temperature {
    @field:Attribute(name = "value")
    var value: String? = null
}

@Root(name = "weather", strict = false)
class Weather {
    @field:Attribute(name = "value")
    var value: String? = null
}