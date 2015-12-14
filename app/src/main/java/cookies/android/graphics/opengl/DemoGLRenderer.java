package cookies.android.graphics.opengl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import cookies.android.R;

public class DemoGLRenderer implements Renderer {

    private final String mVertexShader =
            "uniform mat4 uMVPMatrix;\n" +
            "attribute vec4 aPosition;\n" +
            "attribute vec4 aColor;\n" +
            "attribute vec2 aTextureCoord;\n" +
            "varying vec4 vColor;\n" +
            "varying vec2 vTextureCoord;\n" +
            "void main() {\n" +
            "    gl_Position = uMVPMatrix * aPosition;\n" +
            "    vColor = aColor;\n" +
            "    vTextureCoord = aTextureCoord;\n" +
            "}\n";

    private final String mFragmentShader=
            "precision mediump float;\n" +
            "varying vec4 vColor;\n" +
            "varying vec2 vTextureCoord;\n" +
            "uniform sampler2D uBaseTexture;\n" +
            "void main() {\n" +
            //"    gl_FragColor = vec4(0.0, 1.0, 1.0, 1.0);\n" +
            //"    gl_FragColor = vColor;\n" +
            "    gl_FragColor = texture2D(uBaseTexture, vTextureCoord);\n" +
            "}\n";

    private final int mCellsX = 4;
    private final float mSquareLen = 2.0f / mCellsX;
    // a square located at the left-bottom cornor
    private final float[] mVertexAttribData = {
            // x, y, z,
            // r, g, b,
            // u, v
            -1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 1.0f,
            0.0f, 1.0f,

            -1.0f + mSquareLen, -1.0f, 0.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 1.0f,

            -1.0f + mSquareLen, -1.0f + mSquareLen, 0.0f,
            1.0f, 1.0f, 1.0f,
            1.0f, 0.0f,

            -1.0f, -1.0f + mSquareLen, 0.0f,
            1.0f, 1.0f, 1.0f,
            0.0f, 0.0f
    };

    private final byte[] mPrimitiveIndicesData = {
            0, 1, 2, 3
    };

    private float[] mModelMatrix = new float[16];
    private float[] mViewMatrix = new float[16];
    private float[] mProjectionMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private FloatBuffer mVetexAttribBuffer;
    private ByteBuffer mPrimitiveIndicesBuffer;

    private int mProgram;
    private int mPositionLocation;
    private int mColorPosition;
    private int mMVPMatrixLocation;
    private int mTextureCoordLocation;
    private int mBaseTextureLocation;

    private int[] mVertexBufferObjects;

    private int mTextureId;

    private Context mContext;

    public DemoGLRenderer(Context context) {
        mContext = context;
        mVetexAttribBuffer = ByteBuffer
                .allocateDirect(mVertexAttribData.length * GLHelper.FLOAT_SIZE_IN_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVetexAttribBuffer.put(mVertexAttribData);

        mPrimitiveIndicesBuffer = ByteBuffer
                .allocateDirect(mPrimitiveIndicesData.length * GLHelper.BYTE_SIZE_IN_BYTES)
                .order(ByteOrder.nativeOrder());
        mPrimitiveIndicesBuffer.put(mPrimitiveIndicesData);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mProgram = GLHelper.createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }

        GLHelper.logActiveUniformInfo(mProgram);
        GLHelper.logActiveAttribInfo(mProgram);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLHelper.checkGLError("glGetAttribLocation vPosition");
        if (mPositionLocation == -1) {
            throw new RuntimeException("glGetAttribLocation() for aPosition failed");
        }

        mColorPosition = GLES20.glGetAttribLocation(mProgram, "aColor");
        GLHelper.checkGLError("glGetAttribLocation aColor");
        if (mColorPosition == -1) {
            throw new RuntimeException("glGetAttribLocation() for aColor failed");
        }

        mMVPMatrixLocation = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLHelper.checkGLError("glGetUniformLocation uMVPMatrix");
        if (mMVPMatrixLocation == -1) {
            throw new RuntimeException("glGetUniformLocation() for uMVPMatrix failed");
        }

        mTextureCoordLocation = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        GLHelper.checkGLError("glGetAttribLocation aTextureCoord");
        if (mTextureCoordLocation == -1) {
            throw new RuntimeException("glGetAttribLocation() for aTextureCoord failed");
        }

        mBaseTextureLocation = GLES20.glGetUniformLocation(mProgram, "uBaseTexture");
        GLHelper.checkGLError("glGetUniformLocation uBaseTexture");
        if (mBaseTextureLocation == -1) {
            throw new RuntimeException("glGetUniformLocation() for uBaseTexture failed");
        }

        // generate a texture
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        mTextureId = textures[0];

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_REPEAT);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_REPEAT);

        // upload the texture image
        InputStream textureData = mContext.getResources().openRawResource(R.raw.robot);
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(textureData);
        } finally {
            try {
                textureData.close();
            } catch (Exception e) {
                // ignore
            }
        }
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        // generate mipmap automatically
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        bmp.recycle();

        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0.0f, 5.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f);

        if (mVertexBufferObjects == null) {
            mVertexBufferObjects = new int[2];
            GLES20.glGenBuffers(2, mVertexBufferObjects, 0);

            // upload the vertex attribs
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferObjects[0]);
            mVetexAttribBuffer.position(0);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    mVertexAttribData.length * GLHelper.FLOAT_SIZE_IN_BYTES,
                    mVetexAttribBuffer, GLES20.GL_STATIC_DRAW);

            // upload the indices
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mVertexBufferObjects[1]);
            mPrimitiveIndicesBuffer.position(0);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                    mPrimitiveIndicesData.length * GLHelper.BYTE_SIZE_IN_BYTES,
                    mPrimitiveIndicesBuffer, GLES20.GL_STATIC_DRAW);

            // unbind buffer object
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float)height / (float)width;
        if (height >= width) {
//        Matrix.frustumM(mProjectionMatrix, 0,
//                -ratio, ratio, -1.0f, 1.0f, 3.0f, 7.0f);
            Matrix.orthoM(mProjectionMatrix, 0, -1.0f, 1.0f, -ratio, ratio, 1.0f, 10.0f);
        } else {
            ratio = (float)width / (float)height;
            Matrix.orthoM(mProjectionMatrix, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 10.0f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClearDepthf(1.0f);
        GLES20.glClearStencil(0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);

        //GLES20.glColorMask(false, false, true, false);

        // enable culling face
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);

        //GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        //GLES20.glScissor(360, 640, 100, 100);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendColor(0.6f, 0.6f, 0.6f, 0.0f);
        GLES20.glBlendFunc(GLES20.GL_CONSTANT_COLOR, GLES20.GL_ONE_MINUS_CONSTANT_COLOR);
        GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);

        GLES20.glEnable(GLES20.GL_STENCIL_TEST);

        updateStencilBuffer();

        // using stencil buffer to restrict the output staying inside a fixed area
        GLES20.glStencilFunc(GLES20.GL_EQUAL, 0x01, 0xff);
        GLES20.glStencilOp(GLES20.GL_KEEP, GLES20.GL_KEEP, GLES20.GL_KEEP);
        for (int i = 0; i < mCellsX; ++i)
            for (int j = 0; j < mCellsX; ++j) {
                drawSquare(i * mSquareLen, j * mSquareLen, 0.0f, 1.0f, 1.0f);
            }
    }

    private void drawSquare(float translateX, float translateY,
                            float rotateAngle, float scaleX, float scaleY) {
        GLES20.glUseProgram(mProgram);
        GLHelper.checkGLError("glUseProgram");

        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.scaleM(mModelMatrix, 0, scaleX, scaleY, 1.0f);
        Matrix.rotateM(mModelMatrix, 0, rotateAngle, 0.0f, 0.0f, 1.0f);
        Matrix.translateM(mModelMatrix, 0, translateX, translateY, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixLocation, 1, false, mMVPMatrix, 0);

        // bind buffer object
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mVertexBufferObjects[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mVertexBufferObjects[1]);

        // use vertex buffer object(VBO) to draw
        int stride = 8 * GLHelper.FLOAT_SIZE_IN_BYTES;
        GLES20.glEnableVertexAttribArray(mPositionLocation);
        GLES20.glVertexAttribPointer(mPositionLocation, 3, GLES20.GL_FLOAT,
                false, stride, 0);

        GLES20.glEnableVertexAttribArray(mColorPosition);
        GLES20.glVertexAttribPointer(mColorPosition, 3, GLES20.GL_FLOAT, false,
                stride, 3 * GLHelper.FLOAT_SIZE_IN_BYTES);

        GLES20.glEnableVertexAttribArray(mTextureCoordLocation);
        GLES20.glVertexAttribPointer(mTextureCoordLocation, 2, GLES20.GL_FLOAT,
                false, stride, 6 * GLHelper.FLOAT_SIZE_IN_BYTES);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mBaseTextureLocation, 0);

        //GLES20.glLineWidth(2);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, 4, GLES20.GL_UNSIGNED_BYTE, 0);

        // unbind buffer object
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    private void updateStencilBuffer() {
        // disable color buffer and depth buffer, only update stencil buffer
        GLES20.glColorMask(false, false, false, false);
        GLES20.glDepthMask(false);
        GLES20.glStencilFunc(GLES20.GL_NEVER, 0x01, 0x01);
        GLES20.glStencilOp(GLES20.GL_REPLACE, GLES20.GL_REPLACE, GLES20.GL_REPLACE);

        drawSquare(mSquareLen * 1.5f, mSquareLen * 1.5f, 45.0f, 1.6f, 1.6f);

        // enable color buffer and depth buffer
        GLES20.glColorMask(true, true, true, true);
        GLES20.glDepthMask(true);
    }
}
