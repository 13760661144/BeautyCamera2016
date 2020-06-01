package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.image.filter;
import cn.poco.pgles.PGLNativeIpl;

/**
 * 萌犬/法斗变形
 */
public class CrazyShapeFilter extends FaceShapeFilter {

    /**
     * 3 种萌犬变形
     */
    private int mShapeType;

    private int filterBGCosmeticTextureUniform;

    private int bufferSizeUniform;

    private int featuresUniform;
    private int face1RadiusUniform, face1StrengthUniform;
    private int face2RadiusUniform, face2StrengthUniform;
    private int chinRadiusUniform, chinStrengthUniform;
    private int eyeStrengthUniform, eyeOffsetthUniform;
    private int smileStrengthUniform;
    private int noseOffsetUniform;
    private int mouthRadiusUniform, mouthStrengthUniform;

    private float mFaceRadius1, mFaceStrength1;
    private float mFaceRadius2, mFaceStrength2;
    private float mChinRadius, mChinStrength;

    private float mEyeStrength;
    private float mEyelac;

    private float mSmileStrength;
    private float mNoseStrength;
    private float mMouthRadius, mMouthStrength;
    private float mFeatures[] = new float[86];

    private float[] mTextureMatrix;

    public CrazyShapeFilter(Context context) {
        this(context, 0);
    }

    public CrazyShapeFilter(Context context, int type) {
        super(context);
        mShapeType = type;
        initShapeData();
    }

    private void initShapeData() {
        mFaceRadius1 = 0.0f;
        mFaceStrength1 = 0.0f;
        mFaceRadius2 = 0.0f;
        mFaceStrength2 = 0.0f;
        mChinRadius = 0.0f;
        mChinStrength = 0.0f;

        mEyeStrength = 0.0f;
        mSmileStrength = 0.0f;
        mNoseStrength = 100.0f;
        mEyelac = 0.0f;
        mMouthStrength = 0.0f;
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vert_shrinknose, R.raw.frag_crazyshape);
        return PGLNativeIpl.loadCrazyShapeProgram();
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");

        bufferSizeUniform = GLES20.glGetUniformLocation(mProgramHandle, "bufferSize");
        featuresUniform = GLES20.glGetUniformLocation(mProgramHandle, "features");

        face1RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r1");
        face1StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s1");
        face2RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r2");
        face2StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s2");
        chinRadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "chin_r");
        chinStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "chin_s");

        eyeStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "eye_s");
        eyeOffsetthUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyelac_s");
        smileStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "smile_s");
        noseOffsetUniform = GLES20.glGetUniformLocation(mProgramHandle, "nose_s");
        mouthRadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "mouth_r");
        mouthStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "mouth_s");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return false;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
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
                if (mTextureMatrix == null) {
                    mTextureMatrix = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f};
                }
                bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, mTextureMatrix, texBuffer, texStride);
                drawArrays(firstVertex, vertexCount);

                if (i > 0 && i == faceSize - 1 && mGLFramebuffer != null) {
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
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);

        int[] dogData = null;
        if (mShapeType == 1) {
            dogData = filter.getRealCuteDogParam2();
        } else if (mShapeType == 2) {
            dogData = filter.getRealCuteDogParam3();
        } else {
            dogData = filter.getRealCuteDogParam();
        }
        setShapeData(dogData);

        GLES20.glUniform1f(face1RadiusUniform, mFaceRadius1);
        GLES20.glUniform1f(face1StrengthUniform, mFaceStrength1);
        GLES20.glUniform1f(face2RadiusUniform, mFaceRadius2);
        GLES20.glUniform1f(face2StrengthUniform, mFaceStrength2);
        GLES20.glUniform1f(chinRadiusUniform, mChinRadius);
        GLES20.glUniform1f(chinStrengthUniform, mChinStrength);
        GLES20.glUniform1f(eyeStrengthUniform, mEyeStrength);
        GLES20.glUniform1f(eyeOffsetthUniform, mEyelac);
        GLES20.glUniform1f(smileStrengthUniform, mSmileStrength);
        GLES20.glUniform1f(noseOffsetUniform, mNoseStrength);
        GLES20.glUniform1f(mouthRadiusUniform, mMouthRadius);
        GLES20.glUniform1f(mouthStrengthUniform, mMouthStrength);
        GLES20.glUniform2f(bufferSizeUniform, mWidth, mHeight);

        if (mPocoFace != null && mPocoFace.points_count > 0) {
            updateFaceFeaturesPosition(mPocoFace.points_array, mWidth, mHeight);
            GLES20.glUniform1fv(featuresUniform, 86, FloatBuffer.wrap(mFeatures));
        }
        mUseOtherFaceData = false;
        mPocoFace = null;
    }

    //获取变形数据
    private void setShapeData(int[] shapeData) {
        if (shapeData == null) {
            return;
        }
        mFaceRadius1 = shapeData[filter.PocoShapeInfo.faceRadius1];
        mFaceStrength1 = shapeData[filter.PocoShapeInfo.faceStrength1];

        mFaceRadius2 = shapeData[filter.PocoShapeInfo.faceRadius2];
        mFaceStrength2 = shapeData[filter.PocoShapeInfo.faceStrength2];

        mChinRadius = shapeData[filter.PocoShapeInfo.chinRadius];
        mChinStrength = shapeData[filter.PocoShapeInfo.chinStrength];

        mEyeStrength = shapeData[filter.PocoShapeInfo.eyeStrength];

        mSmileStrength = shapeData[filter.PocoShapeInfo.smileStrength];

        mNoseStrength = shapeData[filter.PocoShapeInfo.noseStrength];

        mEyelac = shapeData[filter.PocoShapeInfo.eyelac];

        mMouthRadius = shapeData[filter.PocoShapeInfo.MouthRadius];
        mMouthStrength = shapeData[filter.PocoShapeInfo.MouthStrength];
    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
        mTextureMatrix = null;
    }

    private final int index_poco_feature[] = {7, 16, 25, 33, 35, 67, 65, 42, 40, 68, 70, 52, 72, 55, 73, 61, 75, 58, 76, 46, 82, 49, 83,
            84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 99, 101, 102, 103, 104, 105};

    void updateFaceFeaturesPosition(PointF[] index_face, int width, int height) {
        if (index_face != null && index_face.length > 0) {
            for (int i = 0; i < index_poco_feature.length; i++) {
                mFeatures[2 * i] = index_face[index_poco_feature[i]].x * width;
                mFeatures[2 * i + 1] = index_face[index_poco_feature[i]].y * height;
            }
        }
    }

}
