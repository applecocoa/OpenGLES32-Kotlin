package com.mna.gles32engine.gles32

import android.opengl.GLES30

class Column(coords: FloatArray, private val mPositionHandle: Int, private val mColorHandle: Int) :
    GLES30() {

    // MARK: - Properties -

    private val vertexVBO = IntArray(1)
    private val mNumverts: Int

    // MARK: - Init -

    init {
        mNumverts = coords.size / 3
        GLToolbox.loadFloatVBO(vertexVBO, coords)
    }

    // MARK: - Draw -

    fun draw() {
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0])
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, 3 * 4, 0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glUniform4fv(mColorHandle, 1, color, 0)
        glDrawArrays(GL_TRIANGLES, 0, mNumverts)
    }

    // MARK: - Companion -

    companion object {
        var color = floatArrayOf(
            0.5f, 0f, 0.5f, 1f
        )
    }

}