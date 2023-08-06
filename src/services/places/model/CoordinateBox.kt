package de.moritzbruder.services.places.model

import com.grum.geocalc.Coordinate
import com.grum.geocalc.EarthCalc
import com.grum.geocalc.Point
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class CoordinateBox {

    val minLat: Double
    val maxLat: Double
    val minLon: Double
    val maxLon: Double


    constructor(lat: Double, lon: Double, meterRadius: Double) {
        val latitude = Coordinate.fromDegrees(lat)
        val longitude = Coordinate.fromDegrees(lon)
        val point = Point.at(latitude, longitude)

        val pa = EarthCalc.pointAt(point, 135.0, sqrt(meterRadius.pow(2)))
        val pb = EarthCalc.pointAt(point, 315.0, sqrt(meterRadius.pow(2)))

        this.minLat = min(pa.latitude, pb.latitude)
        this.maxLat = max(pa.latitude, pb.latitude)
        this.minLon = min(pa.longitude, pb.longitude)
        this.maxLon = max(pa.longitude, pb.longitude)

    }

    override fun toString(): String {
        return "CoordinateBox(minLat=$minLat, maxLat=$maxLat, minLon=$minLon, maxLon=$maxLon)"
    }

}