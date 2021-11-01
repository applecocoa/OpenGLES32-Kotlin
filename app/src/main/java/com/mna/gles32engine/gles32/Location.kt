package com.mna.gles32engine.gles32

import android.opengl.Matrix

open class Location @JvmOverloads constructor(

    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var roll: Float = 0f

) {

    var matrix : FloatArray? = null
        get() = field
        set(value) {
            Matrix.setIdentityM(value, 0)
            Matrix.translateM(value, 0, x, y, z)
            Matrix.rotateM(value, 0, roll, 0f, 0f, 1f)
            Matrix.rotateM(value, 0, pitch, 1f, 0f, 0f)
            Matrix.rotateM(value, 0, yaw, 0f, 1f, 0f)
            field = value
        }

    fun move(dx: Float, dy: Float, dz: Float) {
        x += dx
        y += dy
        z += dz
    }

    fun rotate(dYaw: Float, dPitch: Float, dRoll: Float) {
        yaw += dYaw
        pitch += dPitch
        roll += dRoll
    }

    fun setPos(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    fun setRotation(yaw: Float, pitch: Float, roll: Float) {
        this.yaw = yaw
        this.pitch = pitch
        this.roll = roll
    }

    // copy xyz data into given array

    fun putPos(pos: FloatArray) {
        pos[0] = x
        pos[1] = y
        pos[2] = z
        if (pos.size > 3) pos[3] = 1f
    }

    private fun applyToMatrix(matrix: FloatArray?) {
        Matrix.translateM(matrix, 0, x, y, z)
        Matrix.rotateM(matrix, 0, roll, 0f, 0f, 1f)
        Matrix.rotateM(matrix, 0, pitch, 0f, 1f, 0f)
        Matrix.rotateM(matrix, 0, yaw, 1f, 0f, 0f)
    }

    private fun interpolate(a: Float, b: Float, f: Float): Float {
        return a * (1 - f) + b * f
    }

    fun interpolate(result: Location, a: Location, b: Location, factor: Float) {
        result.x = interpolate(a.x, b.x, factor)
        result.y = interpolate(a.y, b.y, factor)
        result.z = interpolate(a.z, b.z, factor)
        result.yaw = interpolate(a.yaw, b.yaw, factor)
        result.pitch = interpolate(a.pitch, b.pitch, factor)
        result.roll = interpolate(a.roll, b.roll, factor)
    }

    fun clone(): Location {
        return Location(x, y, z, yaw, pitch, roll)
    }

    fun cloneTo(location: Location) {
        location.x = x
        location.y = y
        location.z = z
        location.yaw = yaw
        location.pitch = pitch
        location.roll = roll
    }

    init {
        matrix = FloatArray(16)
    }

}
