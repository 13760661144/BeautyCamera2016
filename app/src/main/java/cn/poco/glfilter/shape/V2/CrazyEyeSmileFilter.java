package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.shape.ShapeInfoData;
import cn.poco.image.PocoFace;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by liujx on 2018/1/26.
 * 眼睛、微笑
 */
public class CrazyEyeSmileFilter extends DefaultFilter {
    private int bufferSizeUniform;

    private int featuresUniform;
    private int faceOrientationUniform;
    private int eyeStretchRadiusUniform, eyeStrengthUniform, eyeAspectUniform;
    private int smileStrengthUniform, smileAngleUniform;

    private float mEyeStretchRadius, mEyeStrength, mAspect;  //眼睛

    private float mSmileAngle, mSmileStrength;     //嘴角

    private float mFeatures[] = new float[90];
    private int currentFaceOrientation;

    private PocoFace mPocoFace;

    public CrazyEyeSmileFilter(Context context) {
        super(context);

        initShapeData();
    }

    private void initShapeData() {

        mEyeStrength = 0.0f;
        mEyeStretchRadius = 0.0f;
        mAspect = 0.0f;

        mSmileAngle = 50.0f;
        mSmileStrength = 0.0f;
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadEyeSmileShapeProgramV2();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");

        bufferSizeUniform = GLES20.glGetUniformLocation(mProgramHandle, "bufferSize");
        featuresUniform = GLES20.glGetUniformLocation(mProgramHandle, "features");

        faceOrientationUniform = GLES20.glGetUniformLocation(mProgramHandle, "faceOrientation");

        eyeStretchRadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "eye_r");
        eyeStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "eye_s");
        eyeAspectUniform = GLES20.glGetUniformLocation(mProgramHandle, "eye_ratio");
        smileAngleUniform = GLES20.glGetUniformLocation(mProgramHandle, "smile_angle");
        smileStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "smile_s");
    }


    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;

        //眼睛
        if (shapeData.isOvalEye) {
            updateEyeOval(shapeData.eyeRadius, shapeData.eyeStrength);
        } else {
            updateEyeCircle(shapeData.eyeRadius, shapeData.eyeStrength);
        }

        //微笑
        this.mSmileAngle = 180 * ((50 - shapeData.smileRadius) * 0.4f) / 50.0f;
        this.mSmileStrength = shapeData.smileStrength;
    }

    /**
     * 椭圆眼
     *
     * @param eyeStretchRadius 范围[0.0-100.0]
     * @param eyeStrength      强度[0.0-100.0]
     */
    public void updateEyeOval(float eyeStretchRadius, float eyeStrength) {
        this.mEyeStretchRadius = (25 + 0.15f * eyeStretchRadius) / 100.f;
        this.mEyeStrength = eyeStrength * 0.35f;
        this.mAspect = 45.f;
    }

    /**
     * 圆眼
     *
     * @param eyeStretchRadius 范围[0.0-100.0]
     * @param eyeStrength      强度[0.0-100.0]
     */
    public void updateEyeCircle(float eyeStretchRadius, float eyeStrength) {
        this.mEyeStretchRadius = (15 + 0.2f * eyeStretchRadius) / 100.f;
        this.mEyeStrength = eyeStrength * 0.35f;
        this.mAspect = 0.0f;
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
        super.bindTexture(textureId);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);

        GLES20.glUniform1f(eyeStretchRadiusUniform, mEyeStretchRadius);
        GLES20.glUniform1f(eyeStrengthUniform, mEyeStrength);
        GLES20.glUniform1f(eyeAspectUniform, mAspect);

        GLES20.glUniform1f(smileAngleUniform, mSmileAngle);
        GLES20.glUniform1f(smileStrengthUniform, mSmileStrength);

        GLES20.glUniform2f(bufferSizeUniform, mWidth / (float) mWidth, mHeight / (float) mWidth);

        if (mPocoFace != null) {
            updateFaceFeaturesPosition(mPocoFace.points_array, mWidth / (float) mWidth, mHeight / (float) mWidth);

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
            GLES20.glUniform1fv(featuresUniform, mFeatures.length, FloatBuffer.wrap(mFeatures));
        }
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        mPocoFace = null;
    }

    private final int index_poco_feature[] = {7, 16, 25, 33, 35, 67, 65, 42, 40, 68, 70, 52, 72, 55, 73, 61, 75, 58, 76, 46, 82, 49, 83,
            84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 99, 101, 102, 103, 104, 105};

    private void updateFaceFeaturesPosition(PointF[] index_face, float width, float height) {
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

            PointF face_3_p = new PointF(index_face[1].x, index_face[1].y);
            face_3_p.x = face_3_p.x * width;
            face_3_p.y = face_3_p.y * height;

            PointF face_29_p = new PointF(index_face[31].x, index_face[31].y);
            face_29_p.x = face_29_p.x * width;
            face_29_p.y = face_29_p.y * height;

            mFeatures[86] = face_3_p.x;
            mFeatures[87] = face_3_p.y;
            mFeatures[88] = face_29_p.x;
            mFeatures[89] = face_29_p.y;
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
