package com.mna.gles32engine.gles32

import android.graphics.Bitmap
import android.opengl.GLES30
import android.opengl.GLES32
import android.opengl.GLUtils
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Sphere(position: Int, tex: Int) : GLES30() {

    // MARK: - Properties -

    private val mModel: SphereModel
    private val vertexVBO = IntArray(1)
    private val orderVBO = IntArray(1)
    private var texId = 0
    private var mSamplerHandle = 0
    private val mPositionHandle: Int
    private val mTexCoordHandle: Int

    // MARK: - Init -

    init {
        mModel = SphereModel(120, 0f, 0f, 0f, 1f, 1)
        val fbuffer: FloatBuffer = mModel.vertices
        glGenBuffers(1, vertexVBO, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0])
        glBufferData(GL_ARRAY_BUFFER, fbuffer.capacity() * 4, fbuffer, GL_STATIC_DRAW)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        val sbuffer: ShortBuffer? = mModel.indices[0]
        glGenBuffers(1, orderVBO, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0])
        if (sbuffer != null) {
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, sbuffer.capacity() * 2, sbuffer, GL_STATIC_DRAW)
        }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
        mPositionHandle = position
        mTexCoordHandle = tex
    }

    // MARK: - Functions -

    fun setTexture(handle: Int, bmp: Bitmap?) {
        mSamplerHandle = handle
        val textures = IntArray(1)
        glGenTextures(1, textures, 0)
        texId = textures[0]
        glBindTexture(GL_TEXTURE_2D, texId)
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bmp, 0)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR.toFloat())
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR.toFloat())
    }

    // MARK: - Draw -

    fun draw() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texId)
        glUniform1i(mSamplerHandle, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vertexVBO[0])
        glVertexAttribPointer(mPositionHandle, 3, GL_FLOAT, false, mModel.veticesStride, 0)
        glVertexAttribPointer(mTexCoordHandle, 2, GL_FLOAT, false, mModel.veticesStride, 3 * 4)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, orderVBO[0])
        mModel.indices[0]?.let { GLES32.glDrawElements(GLES32.GL_TRIANGLES, it.limit(), GLES32.GL_UNSIGNED_SHORT, 0) }
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }

}
