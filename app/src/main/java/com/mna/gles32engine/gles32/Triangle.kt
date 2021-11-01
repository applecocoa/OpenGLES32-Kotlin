package com.mna.gles32engine.gles32

import android.opengl.GLES30

class Triangle(position: Int, color: Int) : GLES30() {

    // MARK: - Properties -

    private val vertexVBO = IntArray(1)
    private val colorVBO = IntArray(1)
    private val mPositionHandle: Int
    private val mColorHandle: Int

    // MARK: - Init -

    init {
        GLToolbox.loadFloatVBO(vertexVBO, coords)
        GLToolbox.loadFloatVBO(colorVBO, colors)
        mPositionHandle = position
        mColorHandle = color
    }

    // MARK: - Functions -

    fun draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0])
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3 * 4, 0)
        glBindBuffer(GL_ARRAY_BUFFER, colorVBO[0])
        glVertexAttribPointer(mColorHandle, 4, GL_FLOAT, false, 4 * 4, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glDrawArrays(GL_TRIANGLES, 0, 3)
    }

    // MARK: - Companion -

    companion object {
        var coords = floatArrayOf(0f, 1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f)
        var colors = floatArrayOf(0f, 1f, 0f, 1f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 1f)
    }

}
