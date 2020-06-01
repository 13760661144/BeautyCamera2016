package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by liujx on 2017/9/8.
 */

public class SuperCrazyShapeFilter extends DefaultFilter {

    private int filterBGCosmeticTextureUniform;

    private int bufferSizeUniform;

    private int featuresUniform;
    private int face1RadiusUniform, face1StrengthUniform;
    private int face3RadiusUniform, face3StrengthUniform;
    private int face4RadiusUniform, face4StrengthUniform;
    private int eyeStrengthUniform, eyeOffsetthUniform, eyeStretchRadiusUniform;
    private int smileStrengthUniform, smileAngleUniform;
    private int faceOrientationUniform;
    private int faceNarrowRadiusUniform, faceNarrowStrengthUniform;

    private float mFaceRadius1, mFaceStrength1;
    private float mFaceRadius3, mFaceStrength3;
    private float mFaceRadius4, mFaceStrength4;

    private float mEyeStretchRadius, mEyeStrength;

    private float mSmileAngle, mSmileStrength;
    private float mFaceNarrowRadius, mFaceNarrowStrength;

    private float mFeatures[] = new float[86];

    private int currentFaceOrientation;

    private PocoFace mPocoFace;

    public SuperCrazyShapeFilter(Context context) {
        super(context);

        initShapeData();
    }

    private void initShapeData() {
        mFaceRadius1 = 0.0f;
        mFaceStrength1 = 0.0f;
        mFaceRadius3 = 0.0f;
        mFaceStrength3 = 0.0f;
        mFaceRadius4 = 0.0f;
        mFaceStrength4 = 0.0f;

        mEyeStrength = 0.0f;
        mEyeStretchRadius = 0.0f;
        mSmileAngle = 50.0f;
        mSmileStrength = 0.0f;
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadSuperCrazyShapeProgram();
//        return GlUtil.createProgram(context, R.raw.vert_shrinknose, R.raw.frag_supercrazy);
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");

        bufferSizeUniform = GLES20.glGetUniformLocation(mProgramHandle, "bufferSize");
        featuresUniform = GLES20.glGetUniformLocation(mProgramHandle, "features");

        faceOrientationUniform = GLES20.glGetUniformLocation(mProgramHandle, "faceOrientation");

        face1RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r1");
        face1StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s1");
        face3RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r3");
        face3StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s3");
        face4RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r4");
        face4StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s4");

        eyeStretchRadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "eye_r");
        eyeStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "eye_s");
        eyeOffsetthUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyelac_s");
        smileAngleUniform = GLES20.glGetUniformLocation(mProgramHandle, "smile_angle");
        smileStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "smile_s");

        faceNarrowRadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_narrow_r");
        faceNarrowStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_narrow_s");
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;
        this.mFaceRadius1 = 40 + shapeData.faceRadius1 * 0.6f;
        this.mFaceStrength1 = shapeData.faceStrength1;
        this.mFaceRadius3 = 25 + shapeData.faceRadius2 * 0.2f;
        this.mFaceStrength3 = shapeData.faceStrength2;
        this.mFaceRadius4 = 60 + shapeData.faceRadius3 * 0.4f;
        this.mFaceStrength4 = shapeData.faceStrength3 * 0.7f;

        this.mEyeStretchRadius = (15 + 0.2f * shapeData.eyeRadius) / 100.f;
        this.mEyeStrength = shapeData.eyeStrength;
        this.mSmileAngle = 180 * ((50 - shapeData.smileRadius) * 0.4f) / 50.0f;
        this.mSmileStrength = shapeData.smileStrength;

        mFaceNarrowRadius = mFaceRadius4;
        mFaceNarrowStrength = mFaceStrength4;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

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

        GLES20.glUniform1f(face1RadiusUniform, mFaceRadius1);
        GLES20.glUniform1f(face1StrengthUniform, mFaceStrength1);
        GLES20.glUniform1f(face3RadiusUniform, mFaceRadius3);
        GLES20.glUniform1f(face3StrengthUniform, mFaceStrength3);
        GLES20.glUniform1f(face4RadiusUniform, mFaceRadius4);
        GLES20.glUniform1f(face4StrengthUniform, mFaceStrength4);

        GLES20.glUniform1f(eyeStretchRadiusUniform, mEyeStretchRadius);
        GLES20.glUniform1f(eyeStrengthUniform, mEyeStrength);

        GLES20.glUniform1f(smileAngleUniform, mSmileAngle);
        GLES20.glUniform1f(smileStrengthUniform, mSmileStrength);
        GLES20.glUniform1f(faceNarrowRadiusUniform, mFaceNarrowRadius);
        GLES20.glUniform1f(faceNarrowStrengthUniform, mFaceNarrowStrength);

        GLES20.glUniform2f(bufferSizeUniform, mWidth / (float)mWidth, mHeight / (float)mWidth);

        if (mPocoFace != null) {
            updateFaceFeaturesPosition(mPocoFace.points_array, mWidth / (float)mWidth, mHeight / (float)mWidth);

            int face_ori = 0;
            switch (currentFaceOrientation) {
                case PocoFaceOrientation.PORSFaceOrientationLeft: {
                    face_ori = 1;
                }
                break;
                case PocoFaceOrientation.PORSFaceOrientationRight: {
                    face_ori = 2;
                }
                break;
                case PocoFaceOrientation.PORSFaceOrientationDown: {
                    face_ori = 3;
                }
                break;
                case PocoFaceOrientation.PORSFaceOrientationUp:
                default: {
                    face_ori = 0;
                }
                break;
            }
            GLES20.glUniform1i(faceOrientationUniform, face_ori);
            GLES20.glUniform1fv(featuresUniform, 86, FloatBuffer.wrap(mFeatures));
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        mPocoFace = null;
    }

    private final int index_poco_feature[] = {7, 16, 25, 33, 35, 67, 65, 42, 40, 68, 70, 52, 72, 55, 73, 61, 75, 58, 76, 46, 82, 49, 83,
            84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 99, 101, 102, 103, 104, 105};

    void updateFaceFeaturesPosition(PointF[] index_face, float width, float height) {
        if (index_face != null && index_face.length > 0) {

            PointF left_eye_pic = new PointF(index_face[Sslandmarks.lEyeCenter].x, index_face[Sslandmarks.lEyeCenter].y);
            PointF right_eye_pic = new PointF(index_face[Sslandmarks.rEyeCenter].x, index_face[Sslandmarks.rEyeCenter].y);

            left_eye_pic.x = left_eye_pic.x * mWidth;
            left_eye_pic.y = (1.0f - left_eye_pic.y) * mHeight;
            right_eye_pic.x = right_eye_pic.x * mWidth;
            right_eye_pic.y = (1.0f - right_eye_pic.y) * mHeight;

            currentFaceOrientation = PocoFaceOrientation.enquirySimilarityFaceOrientation(left_eye_pic, right_eye_pic);

            for (int i = 0; i < index_poco_feature.length; i++) {

                PointF index_poco_feature_p = new PointF();
                index_poco_feature_p.x = index_face[index_poco_feature[i]].x * width;
                index_poco_feature_p.y = index_face[index_poco_feature[i]].y * height;

                index_poco_feature_p = PocoFaceOrientation.rotatePointToFitFaceOrientation(index_poco_feature_p, width, height, currentFaceOrientation);

                mFeatures[2 * i] = index_poco_feature_p.x;
                mFeatures[2 * i + 1] = index_poco_feature_p.y;
            }
        }
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        mPocoFace = null;
    }
}
