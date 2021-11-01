package com.mna.gles32engine.gles32

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.view.MotionEvent
import com.mna.gles32engine.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GLES32Activity : Activity(), GLSurfaceView.Renderer {

    // MARK: - Properties -

    private var mProgram0 = 0
    private var mMVPMatrixHandle0 = 0
    var mTriangle: Triangle? = null
    var mGrid: Grid? = null
    private var mProgram1 = 0
    private var mMVPMatrixHandle1 = 0
    var mSquare: Square? = null
    private var mSphere: Sphere? = null
    private var mProgram2 = 0
    private var mMVPMatrixHandle2 = 0
    var mObjLoader: ObjLoader? = null
    var mColumn: Column? = null
    private var mAngle = 0f
    private var mRatio = 1.0f

    private val TOUCH_SCALE_FACTOR = 1.0f / 5
    private var previousX = 0f
    private var previousY = 0f

    var camera : Camera? = null

    // MARK: - Init -

    init {

        camera = Camera(0f, -3f, -3f, 0f, 45f, 0f)

    }

    // MARK: - Lifecycle -

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // GLSurfaceView

        val view = GLSurfaceView(this)
        view.setEGLContextClientVersion(2)
        view.setRenderer(this)
        setContentView(view)
        //actionBar!!.hide()

        // .obj loader

        mObjLoader = ObjLoader(this)
        mObjLoader?.load(R.raw.column)

    }

    // MARK: - Surface -

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {

        // mProgram0

        mProgram0 = readShader(R.raw.color_vertex_shader)?.let {
            readShader(R.raw.color_fragment_shader)?.let { it1 ->
                GLToolbox.createProgram(
                    it,
                    it1
                )
            }
        }!!

        mMVPMatrixHandle0 = GLES32.glGetUniformLocation(mProgram0, "uMVPMatrix")

        var aPositionHandle = GLES32.glGetAttribLocation(mProgram0, "aPosition")
        val aColorHandle = GLES32.glGetAttribLocation(mProgram0, "aColor")

        GLES32.glEnableVertexAttribArray(aPositionHandle)
        GLES32.glEnableVertexAttribArray(aColorHandle)

        // grid

        mGrid = Grid(aPositionHandle, aColorHandle, mMVPMatrixHandle0)

        // triangle

        mTriangle = Triangle(aPositionHandle, aColorHandle)
        GLToolbox.checkGLError(TAG, "Program and Object for Grid/Triangle")

        // mProgram1

        mProgram1 = readShader(R.raw.texture_vertex_shader)?.let {
            readShader(R.raw.texture_fragment_shader)?.let { it1 ->
                GLToolbox.createProgram(
                    it,
                    it1
                )
            }
        }!!

        mMVPMatrixHandle1 = GLES32.glGetUniformLocation(mProgram1, "uMVPMatrix")
        aPositionHandle = GLES32.glGetAttribLocation(mProgram1, "aPosition")

        val aTexCoordHandle = GLES32.glGetAttribLocation(mProgram1, "aTexCoord")
        val uSamplerHandle = GLES32.glGetUniformLocation(mProgram1, "uSamplerTex")
        GLES32.glEnableVertexAttribArray(aPositionHandle)
        GLES32.glEnableVertexAttribArray(aTexCoordHandle)

        // square

        mSquare = Square(aPositionHandle, aTexCoordHandle)
        var bmp = BitmapFactory.decodeResource(resources, R.drawable.ground)
        mSquare!!.setTexture(uSamplerHandle, bmp)
        bmp.recycle()

        // sphere

        mSphere = Sphere(aPositionHandle, aTexCoordHandle)
        bmp = BitmapFactory.decodeResource(resources, R.drawable.globe)
        mSphere!!.setTexture(uSamplerHandle, bmp)
        bmp.recycle()
        GLToolbox.checkGLError(TAG, "Program and Object for Square/Sphere")

        // mProgram2

        mProgram2 = readShader(R.raw.solid_fragment_shader)?.let {
            readShader(R.raw.position_vertex_shader)?.let { it1 ->
                GLToolbox.createProgram(
                    it1,
                    it
                )
            }
        }!!

        mMVPMatrixHandle2 = GLES32.glGetUniformLocation(mProgram2, "uMVPMatrix")
        aPositionHandle = GLES32.glGetAttribLocation(mProgram2, "aPosition")
        val uColorHandle = GLES32.glGetUniformLocation(mProgram2, "uColor")
        GLES32.glEnableVertexAttribArray(aPositionHandle)

        // column

        mColumn = mObjLoader?.let { Column(it.vertices, aPositionHandle, uColorHandle) }
        GLToolbox.checkGLError(TAG, "Program and Object for Column")
        GLES32.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        GLES32.glEnable(GLES32.GL_CULL_FACE)

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {

        GLES32.glViewport(0, 0, width, height)

        mRatio = width.toFloat() / height

    }

    // MARK: - Draw -

    override fun onDrawFrame(gl: GL10) {

        // clear buffer

        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        // set sky

        GLES20.glClearColor(0.4f, 0.5f, 0.75f, 1.0f) //Sky blue

        // camera viewMatrix

        val viewMatrix = FloatArray(16)

        // moveable camera

        Matrix.setLookAtM(
            viewMatrix, 0, 6f, 6f, 6f,
            camera!!.location.x,
            camera!!.location.y,
            camera!!.location.z,
            0f, 1f, 0f
        )

        // static camera

        /*
        Matrix.setLookAtM(
            viewMatrix, 0, 6f, 6f, 6f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
        */

        // motion path camera

        /*
        Matrix.setLookAtM(
            viewMatrix, 0, 6f, 6f, 6f, 0f,
            (0.5 * Math.sin((mAngle / 40).toDouble())).toFloat(), 0f, 0f, 1f, 0f
        )
        */

        // projection

        val projectionMatrix = FloatArray(16)
        Matrix.perspectiveM(projectionMatrix, 0, 30f, mRatio, 1f, 1000f)

        // grid

        val mvpMatrix = FloatArray(16)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        GLES32.glUseProgram(mProgram0)
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle0, 1, false, mvpMatrix, 0)

        // square

        val mvpMatrixSquare = FloatArray(16)

        // set square position

        Matrix.translateM(mvpMatrixSquare, 0, mvpMatrix, 0, -4f, -9f, -4f)

        // scale square size

        Matrix.scaleM(mvpMatrixSquare, 0, 80f, 1f, 80f)

        GLES32.glUseProgram(mProgram1)
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, mvpMatrixSquare, 0)
        mSquare?.draw()

        // grid

        mGrid?.draw(mvpMatrix)

        // triangle

        val mvpMatrix1 = FloatArray(16)
        Matrix.translateM(mvpMatrix1, 0, mvpMatrix, 0, -4f, 0f, -4f)

        GLES32.glUseProgram(mProgram0)
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle0, 1, false, mvpMatrix1, 0)
        mTriangle?.draw()

        // sphere

        val mvpMatrix2 = FloatArray(16)
        Matrix.rotateM(mvpMatrix2, 0, mvpMatrix, 0, ++mAngle, 0f, 1f, 0f)
        GLES32.glUseProgram(mProgram1)
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle1, 1, false, mvpMatrix2, 0)

        mSphere?.draw()

        // column

        GLES32.glUseProgram(mProgram2)
        GLES32.glUniformMatrix4fv(mMVPMatrixHandle2, 1, false, mvpMatrix2, 0)

        mColumn!!.draw()

    }

    // MARK: - Touches -

    override fun onTouchEvent(event: MotionEvent): Boolean {

        println("onTouchEvent")

        val x = event.x
        val y = event.y

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {

                println("ACTION_DOWN")

                previousX = x
                previousY = y

            }

            MotionEvent.ACTION_MOVE -> {

                println("ACTION_MOVE")

                var dx = x - previousX
                var dy = y - previousY

                previousX = x
                previousY = y

                if (dx == 0f && dy == 0f) return true

                dx *= TOUCH_SCALE_FACTOR
                dy *= TOUCH_SCALE_FACTOR
                var yaw: Float
                var pitch: Float
                val l: Location = camera?.location!!
                yaw = l.yaw
                pitch = l.pitch
                yaw += dx
                pitch += dy
                pitch = GLToolbox.clamp(pitch, -90f, 90f)
                l.setRotation(yaw, pitch, 0f)
                yaw = Math.toRadians((yaw - 90).toDouble()).toFloat()
                pitch = Math.toRadians(pitch.toDouble()).toFloat()

                l.setPos(
                    (5 * Math.cos(yaw.toDouble()) * Math.cos(pitch.toDouble())).toFloat(),
                    (5 * Math.sin(-pitch.toDouble())).toFloat(),  //l.getY(),
                    (5 * Math.sin(yaw.toDouble()) * Math.cos(pitch.toDouble())).toFloat()
                )

                //print("l: x: ${l.x} y: ${l.y} z: ${l.z} yaw: ${l.yaw} pitch: ${l.pitch} roll: ${l.roll} matrix: ${l.matrix}")

            }

        }

        return true

    }

    // MARK: - Destroy -

    override fun onDestroy() {

        super.onDestroy()

        GLES32.glDeleteProgram(mProgram0)
        GLES32.glDeleteProgram(mProgram1)
        GLES32.glDeleteProgram(mProgram2)

    }

    // MARK: - Functions -

    private fun readShader(resId: Int): String? {

        val inputStream = resources.openRawResource(resId)

        try {

            val reader = BufferedReader(InputStreamReader(inputStream))
            val sb = StringBuilder()
            var line: String?

            while (reader.readLine().also { line = it } != null) {
                sb.append(line).append("\n")
            }

            reader.close()

            return sb.toString()

        } catch (e: IOException) {

            e.printStackTrace()

        }

        return null

    }

    // MARK: - Companion -

    companion object {
        private const val TAG = "GLES32Activity"
    }

}