package com.mna.gles32engine.gles32

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

class ObjLoader(var context: Context) {

    // MARK: - Properties -

    var strings: MutableList<String?>? = null
    lateinit var vertices: FloatArray
    lateinit var normal: FloatArray
    lateinit var tx: FloatArray

    // MARK: - Functions -

    private fun ObjRead(resId: Int) {

        val inputStream = context.resources.openRawResource(resId)
        val inputStreamReader: InputStreamReader
        val bufferedReader: BufferedReader
        var string: String? = null
        strings = ArrayList()

        try {

            inputStreamReader = InputStreamReader(inputStream)
            bufferedReader = BufferedReader(inputStreamReader)

            while (bufferedReader.readLine().also { string = it } != null) {
                strings?.add(string)
            }

        } catch (e: IOException) {

            Log.d(TAG, "Could not load obj file")
            e.printStackTrace()

        } finally {

            if (inputStream != null) try {

                inputStream.close()

            } catch (e: IOException) {
                Log.d(TAG, "Couldn't close obj file")
                e.printStackTrace()

            }

        }

    }

    fun load(resId: Int) {

        var vertexIndex = 0
        var numVertices = 0
        var normalIndex = 0
        var numNormals = 0
        var txIndex = 0
        var numTx = 0
        var fIndex = 0
        var numFace = 0
        var v = 0
        var vn = 0
        var vt = 0
        var vf = 0

        ObjRead(resId)

        for (i in strings!!.indices) {

            val string = strings!![i]
            if (string!!.startsWith("v ")) v++
            if (string.startsWith("vn ")) vn++
            if (string.startsWith("vt ")) vt++
            if (string.startsWith("f ")) {
                val F = string.split("[ ]+".toRegex()).toTypedArray()
                if (F.size == 5) {
                    vf++
                    vf++
                } else if (F.size == 4) {
                    vf++
                }
            }
        }

        val verticesAux = FloatArray(v * 3)
        val normalsAux = FloatArray(vn * 3)
        val txAux = FloatArray(vt * 2)
        val f = IntArray(vf * 9)

        for (i in strings!!.indices) {

            val string = strings!![i]

            if (string!!.startsWith("v ")) {

                val V = string.split("[ ]+".toRegex()).toTypedArray()

                if (V.count() > 3) {
                    for (j in 1..3) {
                        verticesAux[vertexIndex++] = V[j].toFloat()
                    }
                }

                numVertices++
                continue

            }

            if (string.startsWith("vn ")) {

                val N = string.split("[ ]+".toRegex()).toTypedArray()

                if (N.count() > 3) {
                    for (j in 1..3) {
                        normalsAux[normalIndex++] = N[j].toFloat()
                    }
                }

                numNormals++
                continue

            }

            if (string.startsWith("vt ")) {

                val T = string.split("[ ]+".toRegex()).toTypedArray()

                if (T.count() > 2) {
                    for (j in 1..2) {
                        txAux[txIndex++] = T[j].toFloat()
                    }
                }

                numTx++
                continue

            }

            if (string.startsWith("f ")) {

                val F = string.split("[ ]+".toRegex()).toTypedArray()

                if (F.size == 4) {

                    for (j in 1..3) {

                        val V = F[j].split("/").toTypedArray()

                        if (V.count() >= 3) {
                            for (k in 0..2) {
                                val str = "0${V[k]}"
                                f[fIndex++] = str.toInt()
                            }
                        }

                    }

                    numFace++

                } else if (F.size == 5) {

                    for (j in 1..3) {

                        val V = F[j].split("/").toTypedArray()

                        if (V.count() >= 3) {
                            for (k in 0..2) {
                                val str = "0${V[k]}"
                                f[fIndex++] = str.toInt()
                            }
                        }

                    }

                    numFace++

                    for (j in 2..4) {

                        val V = F[1 + j % 4].split("/").toTypedArray()

                        if (V.count() >= 3) {
                            for (k in 0..2) {
                                val str = "0${V[k]}"
                                f[fIndex++] = str.toInt()
                            }
                        }

                    }

                    numFace++

                }

            }

        }

        vertices = FloatArray(f.size / 3 * 3)
        tx = FloatArray(f.size / 3 * 2)
        normal = FloatArray(f.size / 3 * 3)

        var n = 0
        var nt = 0

        while (n < fIndex) {

            for (i in 0..2) {
                vertices[n + i] = verticesAux[3 * f[n] - 3 + i]
            }

            for (i in 0..1) {
                tx[nt + i] = if (vt > 0) txAux[2 * f[n + 1] - 2 + i] else 0f
            }

            for (i in 0..2) {
                normal[n + i] = normalsAux[3 * f[n + 2] - 3 + i]
            }

            n += 3
            nt += 2

        }

    }

    // MARK: - Companion -

    companion object {
        private const val TAG = "ObjLoader"
    }

}
