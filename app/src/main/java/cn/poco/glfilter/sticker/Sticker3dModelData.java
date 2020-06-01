package cn.poco.glfilter.sticker;

import android.opengl.Matrix;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.GlUtil;

/**
 * Created by zwq on 2017/09/25 19:36.<br/><br/>
 */

public class Sticker3dModelData {

    private float[] mVertexVertices;
    private float[] mTextureVertices;
    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;

    // 视图矩阵
    private float[] mViewMatrix = new float[16];
    // 投影矩阵
    private float[] mProjectionMatrix = new float[16];
    // 模型矩阵
    private float[] mModelMatrix = new float[16];
    // 变换矩阵
    private float[] mMVPMatrix = new float[16];

    // 模型矩阵欧拉角的实际角度
    private float mPitchAngle = 0.0f; // x轴旋转
    private float mYawAngle = 0.0f; // y轴旋转
    private float mRollAngle = 0.0f; // z轴旋转

    // 位置
    private float mCenterX = 0.0f;
    private float mCenterY = 0.0f;
    private float mCenterZ = 0.0f;

    public Sticker3dModelData() {
        mVertexVertices = new float[]{1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f};
        mTextureVertices = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};

        mVertexBuffer = GlUtil.createFloatBuffer(mVertexVertices);
        mTextureBuffer = GlUtil.createFloatBuffer(mTextureVertices);

        initIdentityMatrix();
    }

    /**
     * 初始化单位矩阵
     */
    public void initIdentityMatrix() {
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.setIdentityM(mMVPMatrix, 0);
    }

    /**
     * 设置相机朝向
     */
    public void setLookAt() {
        // 设置相机角度
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        // 设置视图矩阵
        int rmOffset = 0;
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 6.0f;
        float centerX = 0.0f;
        float centerY = 0.0f;
        float centerZ = 0.0f;
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);

        // 设置透视投影矩阵
        float mRatio = 1.0f;
        int offset = 0;

        float left = -mRatio;

        float right = mRatio;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 3.0f;
        float far = 9.0f;
        Matrix.frustumM(mProjectionMatrix, offset, left, right, bottom, top, near, far);
    }

    /**
     * 计算视锥体变换矩阵(MVPMatrix)
     */
    public void calculateMVPMatrix() {
        // 1.重置模型矩阵为单位矩阵
        Matrix.setIdentityM(mModelMatrix, 0);
//        // 2.平移到贴纸对应的人脸关键点的中心位置
//        Matrix.translateM(mModelMatrix, 0, mCenterX, mCenterY,  mCenterZ);
        // 3.以当前关键点为中心，做姿态角的偏移(偏移的先后顺序对贴纸的显示是有影响的)
        Matrix.rotateM(mModelMatrix, 0, mYawAngle, 0, 1.0f, 0);
        Matrix.rotateM(mModelMatrix, 0, mPitchAngle, 1.0f, 0, 0);
        Matrix.rotateM(mModelMatrix, 0, mRollAngle, 0, 0, 1.0f);

        // 4.计算总的变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        // 5.计算完成后将姿态角重置，以防污染其他贴纸的姿态角
        mPitchAngle = 0.0f;
        mYawAngle = 0.0f;
        mRollAngle = 0.0f;
    }

    /**
     * @param pitchAngle X轴旋转角度(0 - 360)
     * @param yawAngle   Y轴旋转角度(0 - 360)
     * @param rollAngle  Z轴旋转角度(0 - 360)
     */
    public void setModelAngle(float pitchAngle, float yawAngle, float rollAngle) {
        mPitchAngle = pitchAngle;
        mYawAngle = yawAngle;
        mRollAngle = rollAngle;
    }

    /**
     * 设置贴纸中心点的位置
     *
     * @param x x轴的位置
     * @param y y轴的位置
     * @param z z轴的位置
     *          <p>
     *          备注：这里的中心点是屏幕的UV坐标系上的贴纸中心，而不是贴纸在投影空间中的中心点
     */
    public void setCenterPosition(float x, float y, float z) {
        mCenterX = x * 2.0f;
        mCenterY = y * 2.0f;
        mCenterZ = z * 2.0f;
    }

    /**
     * 设置vertex坐标
     *
     * @param vertices
     */
    public void setVertexVertices(float[] vertices) {
        if (vertices == null) {
            return;
        }
        for (int i = 0; i < vertices.length; i++) {
            mVertexVertices[i] = vertices[i] * 4.0f; // 如果将中心点放进glsl里面计算，这里还要多乘以2.0
        }
        if (mVertexBuffer == null || mVertexBuffer.capacity() != mVertexVertices.length * 4) {
            mVertexBuffer = GlUtil.createFloatBuffer(mVertexVertices);
        } else {
            mVertexBuffer.clear();
            mVertexBuffer.put(vertices);
        }
        mVertexBuffer.position(0);
    }

    /**
     * 设置texture坐标
     *
     * @param vertices
     */
    public void setTextureVertices(float[] vertices) {
        if (vertices == null) {
            return;
        }
        mTextureVertices = vertices;
        if (mTextureBuffer == null || mTextureBuffer.capacity() != mTextureVertices.length * 4) {
            mTextureBuffer = GlUtil.createFloatBuffer(mTextureVertices);
        } else {
            mTextureBuffer.clear();
            mTextureBuffer.put(vertices);
        }
        mTextureBuffer.position(0);
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getCenterZ() {
        return mCenterZ;
    }

    public float[] getMVPMatrix() {
        return mMVPMatrix;
    }

    public FloatBuffer getVertexBuffer() {
        return mVertexBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return mTextureBuffer;
    }

    public void release() {
        mVertexVertices = null;
        mTextureVertices = null;
        mVertexBuffer = null;
        mTextureBuffer = null;

        mViewMatrix = null;
        mProjectionMatrix = null;
        mModelMatrix = null;
        mMVPMatrix = null;
    }
}
