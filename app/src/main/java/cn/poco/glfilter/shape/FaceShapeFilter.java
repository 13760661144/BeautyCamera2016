package cn.poco.glfilter.shape;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.RawRes;

import java.nio.FloatBuffer;
import java.util.Arrays;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;

/**
 * Created by zwq on 2016/09/05 11:50.<br/><br/>
 * 脸部变形
 */
public class FaceShapeFilter extends DefaultFilter {

    private int mPositionLocation;
    private int mStrengthLocation;
    private int mWidthLocation;
    private int mHeightLocation;

    private int pointIndexes[];

    protected boolean mUseOtherFaceData;
    protected PocoFace mPocoFace;
    private float[] mPoints/* = new float[37 * 2]*/;//脸部需要的点
    private boolean mHasFaceData;

    public FaceShapeFilter(Context context) {
        super(context);
    }

    public FaceShapeFilter(Context context, int programHandle, int[] indexesArray) {
        super(context, programHandle);

        pointIndexes = indexesArray;
        if (pointIndexes != null) {
            mPoints = new float[pointIndexes.length * 2];
        } else {
            mPoints = new float[37 * 2];
        }
    }

    public FaceShapeFilter(Context context, @RawRes int vertexSourceRawId, @RawRes int fragmentSourceRawId, int[] indexesArray) {
        super(context, vertexSourceRawId, fragmentSourceRawId);

        pointIndexes = indexesArray;
        if (pointIndexes != null) {
            mPoints = new float[pointIndexes.length * 2];
        }
    }

    public FaceShapeFilter(Context context, String vertexSource, String fragmentSource) {
        super(context, vertexSource, fragmentSource);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        mPositionLocation = GLES20.glGetUniformLocation(mProgramHandle, "position");
        mStrengthLocation = GLES20.glGetUniformLocation(mProgramHandle, "strength");

        mWidthLocation = GLES20.glGetUniformLocation(mProgramHandle, "mwidth");
        mHeightLocation = GLES20.glGetUniformLocation(mProgramHandle, "mheight");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return true;
    }

    @Override
    public boolean isNeedBlend() {
        return true;
    }

    public void setShapeFilterId(int filterId) {

    }

    public void setUseOtherFaceData(boolean useOtherFaceData) {
        mUseOtherFaceData = useOtherFaceData;
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
//        super.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        useProgram();

        if (!mUseOtherFaceData && mPocoFace == null) {
            int faceSize = FaceDataHelper.getInstance().getFaceSize();
            if (faceSize < 1) {
                faceSize = 1;
            }
            for (int i = 0; i < faceSize; i++) {
                mPocoFace = FaceDataHelper.getInstance().changeFace(i).getFace();

                int frameBufferId = textureId;
                if (i > 0 && mGLFramebuffer != null) {
                    mGLFramebuffer.bindNext(true);
                    frameBufferId = mGLFramebuffer.getPreviousTextureId();
                }

                bindTexture(frameBufferId);
                bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
                drawArrays(firstVertex, vertexCount);

                if (i > 0 && i == faceSize -1 && mGLFramebuffer != null) {
                    mGLFramebuffer.setHasBind(false);
                }
            }
            mDefaultTextureId = textureId;
            mGLFramebuffer = null;

        } else {
            bindTexture(textureId);
            bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
            drawArrays(firstVertex, vertexCount);
        }
        mUseOtherFaceData = false;
        mPocoFace = null;

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        GLES20.glUniform1f(mStrengthLocation, 0.7f);
        GLES20.glUniform1f(mWidthLocation, mWidth);
        GLES20.glUniform1f(mHeightLocation, mHeight);

        if (mPocoFace != null && mPocoFace.points_count > 0) {
            float[] faceData = new float[2 * mPocoFace.points_count];
            for (int i = 0; i < mPocoFace.points_count; i++) {
                faceData[2 * i] = mPocoFace.points_array[i].x;
                faceData[2 * i + 1] = 1.0f - mPocoFace.points_array[i].y;
            }
            setPosition(faceData);
        } else {
            setPosition(null);
        }
        GLES20.glUniform2fv(mPositionLocation, mPoints.length / 2, FloatBuffer.wrap(mPoints));

        mUseOtherFaceData = false;
        mPocoFace = null;
    }

    public void setPosition(float[] points) {
        if (points == null) {
            if (mHasFaceData) {
                Arrays.fill(mPoints, 0);
            }
            mHasFaceData = false;
            return;
        }
        mHasFaceData = true;

        for (int i = 0; i < pointIndexes.length; i++) {
            mPoints[2 * i] = points[2 * pointIndexes[i]];
            mPoints[2 * i + 1] = points[2 * pointIndexes[i] + 1];
        }
    }
}
