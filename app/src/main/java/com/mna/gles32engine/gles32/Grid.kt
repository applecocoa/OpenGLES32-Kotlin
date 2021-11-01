package com.mna.gles32engine.gles32

import android.opengl.GLES30
import android.opengl.Matrix
import java.nio.FloatBuffer

class Grid(position: Int, color: Int, mvp: Int) : GLES30() {

    // MARK: - Properties -

    private val vertexBuffer: FloatBuffer
    private val colorBuffer: FloatBuffer
    private val mPositionHandle: Int
    private val mColorHandle: Int
    private val mMVPMatrixHandle: Int
    private lateinit var mMVPMatrix: FloatArray

    // MARK: - Init -

    init {
        vertexBuffer = GLToolbox.loadBuffer(coords)
        colorBuffer = GLToolbox.loadBuffer(colors)
        mPositionHandle = position
        mColorHandle = color
        mMVPMatrixHandle = mvp
    }

    // MARK: - Functions -

    fun draw(mvpMatrix: FloatArray) {

        mMVPMatrix = mvpMatrix

        glVertexAttribPointer(
            mPositionHandle, 3, GL_FLOAT, false,
            3 * 4, vertexBuffer
        )

        glVertexAttribPointer(
            mColorHandle, 4, GL_FLOAT, false,
            4 * 4, colorBuffer
        )

        // line width

        glLineWidth(3f)

        // 8 x 8 grid

        for (f in -4..4) {
            translate(0f, 0f, f.toFloat())
            glDrawArrays(GL_LINES, 0, 2)
            translate(f.toFloat(), 0f, 0f)
            glDrawArrays(GL_LINES, 2, 2)
        }

    }

    private fun translate(x: Float, y: Float, z: Float) {

        val mvpMatrix = FloatArray(16)
        Matrix.translateM(mvpMatrix, 0, mMVPMatrix, 0, x, y, z)
        glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0)

    }

    // MARK: - Companion -

    companion object {
        var coords = floatArrayOf(-4f, 0f, 0f, 4f, 0f, 0f, 0f, 0f, -4f, 0f, 0f, 4f)
        var h = 0.5f
        var colors = floatArrayOf(
            h, h, h, 1f, h, h, h, 1f, h, h, h, 1f, h, h, h, 1f
        )
    }

}
