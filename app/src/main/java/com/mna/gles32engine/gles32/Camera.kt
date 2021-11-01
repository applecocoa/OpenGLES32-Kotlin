package com.mna.gles32engine.gles32

import android.opengl.Matrix

class Camera constructor(
    x: Float = 0f,
    y: Float = 0f,
    z: Float = 0f,
    yaw: Float = 0f,
    pitch: Float = -45f,
    roll: Float = 0f
) {

    var location : Location

    var matrix = FloatArray(16)
        get() = field
        set(value) {
            Matrix.setIdentityM(value, 0)
            Matrix.rotateM(value, 0, location.roll, 0f, 0f, 1f)
            Matrix.rotateM(value, 0, location.pitch, 1f, 0f, 0f)
            Matrix.rotateM(value, 0, location.yaw, 0f, 1f, 0f)
            Matrix.translateM(value, 0, location.x, location.y, location.z)
            field = value
        }

    var pos = FloatArray(3)
        get() = field
        set(value) {
            location.putPos(value)
            field = value
        }

    init {
        println("Camera Init")
        location = Location(x, y, z, yaw, pitch, roll)
    }

}
