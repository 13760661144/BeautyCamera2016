package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.glfilter.shape.ShapeInfoData;
import cn.poco.image.PocoFace;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;
import my.beautyCamera.R;

/**
 * Created by liujx on 2017/12/12. <p>
 * 瘦脸，削脸，小脸
 */
public class SuperCrazyShapeFilterV2 extends DefaultFilter {

    private int bufferSizeUniform;

    private int featuresUniform;
    private int faceOrientationUniform;
    private int face1RadiusUniform, face1StrengthUniform;
    private int face3RadiusUniform, face3StrengthUniform;
    private int face4RadiusUniform, face4StrengthUniform;

    private int faceNarrowRadiusUniform, faceNarrowStrengthUniform;

    private float mFaceRadius1, mFaceStrength1;   //瘦脸
    private float mFaceRadius3, mFaceStrength3;   //削脸
    private float mFaceRadius4, mFaceStrength4;   //小脸

    private float mFaceNarrowRadius, mFaceNarrowStrength;

    private float mFeatures[] = new float[90];
    private int currentFaceOrientation;

    private PocoFace mPocoFace;

    private int face_mask_program;
    private int face_mask_inputtextureUniform;
    private int mFaceMaskId;
    private int maPositionLoc1;
    private int maTextureCoordLoc1;
    private ByteBuffer drawIndex, bb, bb1;
    private FloatBuffer mFaceMaskVertexBuffer, mFaceVertexBuffer;
    private PointF[] faceData;
    private int muTextureLoc1;

    private GLFramebuffer mMaskBuffer;

    public SuperCrazyShapeFilterV2(Context context) {
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
    }

    @Override
    protected int createProgram(Context context) {
        face_mask_program = PGLNativeIpl.loadBasicProgram();
        return PGLNativeIpl.loadSuperCrazyShapeProgramV2();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        muTextureLoc1 = GLES20.glGetUniformLocation(mProgramHandle, "faceMaskTexture");

        bufferSizeUniform = GLES20.glGetUniformLocation(mProgramHandle, "bufferSize");
        featuresUniform = GLES20.glGetUniformLocation(mProgramHandle, "features");

        faceOrientationUniform = GLES20.glGetUniformLocation(mProgramHandle, "faceOrientation");

        face1RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r1");
        face1StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s1");
        face3RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r3");
        face3StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s3");
        face4RadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_r4");
        face4StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_s4");

        faceNarrowRadiusUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_narrow_r");
        faceNarrowStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "face_narrow_s");

        //===========facemask=============================
        maPositionLoc1 = GLES20.glGetAttribLocation(face_mask_program, "aPosition");
        maTextureCoordLoc1 = GLES20.glGetAttribLocation(face_mask_program, "aTextureCoord");

        face_mask_inputtextureUniform = GLES20.glGetUniformLocation(face_mask_program, "uTexture");
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (width == 0 || height == 0) {
            return;
        }
        if (mMaskBuffer != null) {
            mMaskBuffer.destroy();
            mMaskBuffer = null;
        }
        if (mMaskBuffer == null || width != mMaskBuffer.getWidth() || height != mMaskBuffer.getHeight()) {
            mMaskBuffer = new GLFramebuffer(1, width, height);
        }
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
        mFaceNarrowRadius = mFaceRadius4;
        mFaceNarrowStrength = mFaceStrength4;

    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {

        //mMaskBuffer.bind(true);
        drawFaceMask();

        if (mGLFramebuffer != null) {
            mGLFramebuffer.rebind(true);
        }
        useProgram();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    private void drawFaceMask() {
        if (mMaskBuffer != null) {
            mMaskBuffer.bind(true);
        }

        GLES20.glUseProgram(face_mask_program);

        if (mFaceMaskId <= 0) {
            mFaceMaskId = getBitmapTextureId(R.drawable.facemask);
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(getTextureTarget(), mFaceMaskId);
        GLES20.glUniform1i(face_mask_inputtextureUniform, 1);

        if (mPocoFace != null && mPocoFace.points_count > 0) {
            if (drawIndex == null) {
                drawIndex = ByteBuffer.allocateDirect(Faceindex.length * 2)
                        .order(ByteOrder.nativeOrder());
                drawIndex.asShortBuffer().put(Faceindex);
            }
            drawIndex.position(0);

            faceData = new PointF[mPocoFace.points_count];
            for (int i = 0; i < mPocoFace.points_count; i++) {
                faceData[i] = new PointF();
                faceData[i].x = mPocoFace.points_array[i].x;
                faceData[i].y = 1.0f - mPocoFace.points_array[i].y;
            }

            float imageVertices[] = getFacePoint(faceData);
            for (int i = 0; i < imageVertices.length / 2; i++) {
                imageVertices[2 * i] = imageVertices[2 * i] * 2 - 1.f;
                imageVertices[2 * i + 1] = imageVertices[2 * i + 1] * 2 - 1.f;
            }

            if (mFaceVertexBuffer == null) {
                bb1 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                bb1.order(ByteOrder.nativeOrder());
                mFaceVertexBuffer = bb1.asFloatBuffer();
            }
            mFaceVertexBuffer.put(imageVertices);
            mFaceVertexBuffer.position(0);

            if (mFaceMaskVertexBuffer == null) {
                bb = ByteBuffer.allocateDirect(mTextureVerticesData.length * 4);
                bb.order(ByteOrder.nativeOrder());
                mFaceMaskVertexBuffer = bb.asFloatBuffer();
                mFaceMaskVertexBuffer.put(mTextureVerticesData);
            }
            mFaceMaskVertexBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc1);
            GLES20.glVertexAttribPointer(maPositionLoc1, 2, GLES20.GL_FLOAT, false, 0, mFaceVertexBuffer);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc1);
            GLES20.glVertexAttribPointer(maTextureCoordLoc1, 2, GLES20.GL_FLOAT, false, 0, mFaceMaskVertexBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, Faceindex.length, GLES20.GL_UNSIGNED_SHORT, drawIndex);

            GLES20.glDisableVertexAttribArray(maPositionLoc1);
            GLES20.glDisableVertexAttribArray(maTextureCoordLoc1);
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        super.bindTexture(textureId);

        if (mMaskBuffer != null && mMaskBuffer.getTextureId() > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(getTextureTarget(), mMaskBuffer.getTextureId());
            GLES20.glUniform1i(muTextureLoc1, 1);
        }
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

        GLES20.glUniform1f(faceNarrowRadiusUniform, mFaceNarrowRadius);
        GLES20.glUniform1f(faceNarrowStrengthUniform, mFaceNarrowStrength);

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
        if (mMaskBuffer != null) {
            mMaskBuffer.destroy();
            mMaskBuffer = null;
        }
    }

    private short[] Faceindex = {
            //内环
            121,6,108, 121,108,109, 121,109,110, 121,110,111, 121,111,112,
            121,112,26, 121,6,9, 121,9,12, 121,12,16, 121,16,20,
            121,20,23, 121,23,26,
            //外环上半
            116,6,108,116,108,113,
            113,108,109, 113,114,109, 114,109,110, 114,110,111,
            114,115,111, 115,111,112, 115,112,117, 117,112,26,
            //外环下半
            116,6,118, 118,6,9, 118,9,12, 118,12,119,
            119,12,16, 119,16,20,
            117,26,120, 120,26,23, 120,23,20, 120,20,119
    };

    private float[] mTextureVerticesData = {0.2130682f, 0.4813008f, 0.2149621f, 0.5065041f, 0.219697f, 0.5308943f, 0.2253788f, 0.5569106f, 0.2310606f, 0.5829268f, 0.2405303f, 0.6138211f, 0.25f, 0.6430894f, 0.2613636f, 0.6731707f, 0.2727273f, 0.702439f, 0.2888258f, 0.7317073f, 0.3106061f, 0.7601626f, 0.3323864f, 0.7853659f, 0.3589015f, 0.8073171f, 0.3873106f, 0.8284553f, 0.4232955f, 0.8479675f, 0.4621212f, 0.8593496f, 0.5f, 0.8609756f, 0.5378788f, 0.8609756f, 0.5757576f, 0.8487805f, 0.6126894f, 0.8276423f, 0.6401515f, 0.8081301f, 0.6657197f, 0.7853659f, 0.6893939f, 0.7601626f, 0.7102273f, 0.7333333f, 0.7263258f, 0.704065f, 0.7395833f, 0.6747967f, 0.75f, 0.6447154f, 0.7604167f, 0.6130081f, 0.7698864f, 0.5829268f, 0.7755682f, 0.5569106f, 0.7793561f, 0.5317073f, 0.7850379f, 0.5065041f, 0.7878788f, 0.4804878f, 0.2547348f, 0.4333333f, 0.2926136f, 0.4081301f, 0.3494318f, 0.4065041f, 0.4015152f, 0.4105691f, 0.4441288f, 0.4252033f, 0.5568182f, 0.4252033f, 0.5984848f, 0.4113821f, 0.6515152f, 0.404878f, 0.7064394f, 0.4081301f, 0.7462121f, 0.4333333f, 0.500947f, 0.4723577f, 0.5f, 0.5276423f, 0.500947f, 0.5821138f, 0.5f, 0.6227642f, 0.4498106f, 0.6487805f, 0.4725379f, 0.6430894f, 0.499053f, 0.6552846f, 0.5255682f, 0.6414634f, 0.5501894f, 0.6479675f, 0.3011364f, 0.4902439f, 0.342803f, 0.4691057f, 0.3948864f, 0.4699187f, 0.4308712f, 0.501626f, 0.3929924f, 0.5081301f, 0.3418561f, 0.5081301f, 0.5681818f, 0.501626f, 0.6070076f, 0.4691057f, 0.6590909f, 0.4691057f, 0.6988636f, 0.4902439f, 0.6581439f, 0.5089431f, 0.6070076f, 0.5081301f, 0.2897727f, 0.4284553f, 0.34375f, 0.4300813f, 0.3996212f, 0.4349593f, 0.4479167f, 0.4422764f, 0.5530303f, 0.4422764f, 0.6003788f, 0.4349593f, 0.65625f, 0.4292683f, 0.7102273f, 0.4284553f, 0.3674242f, 0.4650407f, 0.3674242f, 0.5130081f, 0.3664773f, 0.4878049f, 0.6325758f, 0.4650407f, 0.6325758f, 0.5113821f, 0.6325758f, 0.4886179f, 0.4602273f, 0.4813008f, 0.5369318f, 0.4821138f, 0.4545455f, 0.598374f, 0.5464015f, 0.598374f, 0.4327652f, 0.6382114f, 0.5681818f, 0.6373984f, 0.3892045f, 0.7178862f, 0.4308712f, 0.7089431f, 0.46875f, 0.701626f, 0.5f, 0.7105691f, 0.532197f, 0.701626f, 0.5681818f, 0.7089431f, 0.6098485f, 0.7186992f, 0.5691288f, 0.7439024f, 0.5416667f, 0.7593496f, 0.5f, 0.7634146f, 0.4602273f, 0.7585366f, 0.4318182f, 0.7447154f, 0.4043561f, 0.7195122f, 0.4583333f, 0.7219512f, 0.5f, 0.7252033f, 0.5416667f, 0.7219512f, 0.5956439f, 0.7203252f, 0.5293561f, 0.7284553f, 0.5f, 0.7292683f, 0.469697f, 0.7276423f, 0.3721591f, 0.4886179f, 0.6268939f, 0.4886179f, 0.3030303f, 0.6536585f, 0.6988636f, 0.6544715f, 0.2992424f, 0.3495935f, 0.3712121f, 0.3430894f, 0.5f, 0.3430894f, 0.6297348f, 0.3447154f, 0.6979167f, 0.3504065f, 0.f, 0.001626f, 0.5f, 0.000813f, 0.9952652f, 0.000813f, 0.0018939f, 0.5821138f, 0.9943182f, 0.6138211f, 0.f, 0.996748f, 0.5f, 0.997561f, 0.9952652f, 0.996748f, 0.500000f, 0.643902f};

    private float[] getPoints(PointF[] points) {
        float[] vertices = new float[242];//121 * 2

        for (int i = 0; i < 106; ++i) {
            vertices[i * 2] = points[i].x;
            vertices[i * 2 + 1] = points[i].y;
        }

        float x1 = points[35].x;
        float y1 = points[35].y;
        float x2 = points[40].x;
        float y2 = points[40].y;
        float x4, y4, x5, y5, x6, y6;
        float x3 = points[72].x;
        float y3 = points[72].y;
        x4 = (x1 * x1 * x3 + x2 * x2 * x3 - x3 * (y1 - y2) * (y1 - y2) -
                2 * x1 * (x2 * x3 + (y1 - y2) * (y2 - y3)) +
                2 * x2 * (y1 - y2) * (y1 - y3)) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        y4 = (2 * (x1 - x2) * (-x2 * y1 + x3 * (y1 - y2) + x1 * y2) - (x1 - x2 + y1 -
                y2) * (x1 - x2 - y1 + y2) * y3) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        x3 = points[75].x;
        y3 = points[75].y;
        x5 = (x1 * x1 * x3 + x2 * x2 * x3 - x3 * (y1 - y2) * (y1 - y2) -
                2 * x1 * (x2 * x3 + (y1 - y2) * (y2 - y3)) +
                2 * x2 * (y1 - y2) * (y1 - y3)) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        y5 = (2 * (x1 - x2) * (-x2 * y1 + x3 * (y1 - y2) + x1 * y2) - (x1 - x2 + y1 -
                y2) * (x1 - x2 - y1 + y2) * y3) / ((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        x6 = (x4 + x5) / 2;
        y6 = (y4 + y5) / 2;
        vertices[212] = (points[4].x + points[84].x) / 2;
        vertices[213] = (points[4].y + points[84].y) / 2;
        vertices[214] = (points[29].x + points[90].x) / 2;
        vertices[215] = (points[29].y + points[90].y) / 2;
        vertices[216] = x6 + (x4 - x6) / 3 * 4;
        vertices[217] = y6 + (y4 - y6) / 3 * 4;
        vertices[218] = x6 + (x4 - x6) / 3 * 2;
        vertices[219] = y6 + (y4 - y6) / 3 * 2;
        vertices[220] = x6;
        vertices[221] = y6;
        vertices[222] = x6 + (x5 - x6) / 3 * 2;
        vertices[223] = y6 + (y5 - y6) / 3 * 2;
        vertices[224] = x6 + (x5 - x6) / 3 * 4;
        vertices[225] = y6 + (y5 - y6) / 3 * 4;
        float mid_x = (points[29].x + points[3].x) / 2;
        float mid_y = (points[29].y + points[3].y) / 2;
        float right_x = points[29].x + points[29].x - mid_x;
        float right_y = points[29].y + points[29].y - mid_y;
        float left_x = points[3].x + points[3].x - mid_x;
        float left_y = points[3].y + points[3].y - mid_y;
        float top_x = x6 * 2 - points[45].x;
        float top_y = y6 * 2 - points[45].y;
        float bottom_x = points[16].x * 2 - points[93].x;
        float bottom_y = points[16].y * 2 - points[93].y;
        vertices[226] = top_x + (left_x - mid_x);
        vertices[227] = top_y + (left_y - mid_y);
        vertices[228] = top_x;
        vertices[229] = top_y;
        vertices[230] = top_x + (right_x - mid_x);
        vertices[231] = top_y + (right_y - mid_y);
        vertices[232] = left_x;
        vertices[233] = left_y;
        vertices[234] = right_x;
        vertices[235] = right_y;
        vertices[236] = bottom_x + (left_x - mid_x);
        vertices[237] = bottom_y + (left_y - mid_y);
//        vertices[238] = bottom_x;
//        vertices[239] = bottom_y;
        PointF _t_16_p = points[16];
        PointF _t_74_p = points[74];
        PointF _t_77_p = points[77];
        PointF _t_sim_eye_center = new PointF((_t_74_p.x + _t_77_p.x) / 2, (_t_74_p.y + _t_77_p.y) / 2);

        float build_in_frb_scale = 1.5f;

        PointF _t_sim_119 = offsetPointF(_t_sim_eye_center, _t_16_p, build_in_frb_scale);

        vertices[238] = _t_sim_119.x;
        vertices[239] = _t_sim_119.y;

        vertices[240] = bottom_x + (right_x - mid_x);
        vertices[241] = bottom_y + (right_y - mid_y);

        return vertices;
    }

    private float distanceOfPoint(PointF p1, PointF p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    private PointF offsetPointF(PointF src, PointF target, float percent) {
        return new PointF(src.x + (target.x - src.x) * percent, src.y + (target.y - src.y) * percent);
    }

    private float[] getFacePoint(PointF[] points) {
        float[] face_landmarks_coor = new float[122 * 2];

        float[] face_landmarks_coor121 = getPoints(points);

        for (int i = 0; i < face_landmarks_coor121.length; i++) {
            face_landmarks_coor[i] = face_landmarks_coor121[i];
        }

        face_landmarks_coor[121 * 2] = (face_landmarks_coor[6 * 2] + face_landmarks_coor[26 * 2]) * 0.5f;
        face_landmarks_coor[121 * 2 + 1] = (face_landmarks_coor[6 * 2 + 1] + face_landmarks_coor[26 * 2 + 1]) * 0.5f;

//        //=====================  额外添加耳朵点   ==============================//
//        PointF _t_3_p = new PointF(face_landmarks_coor[6], face_landmarks_coor[7]);
//        PointF _3_p_pic = new PointF(_t_3_p.x*mWidth, _t_3_p.y*mHeight);
//        PointF _t_29_p = new PointF(face_landmarks_coor[58], face_landmarks_coor[59]);
//        PointF _29_p_pic = new PointF(_t_29_p.x*mWidth, _t_29_p.y*mHeight);
//        PointF left_eye_center = new PointF(face_landmarks_coor[208], face_landmarks_coor[209]);
//        PointF right_eye_center = new PointF(face_landmarks_coor[210], face_landmarks_coor[211]);
//        PointF _t_49_p = new PointF(face_landmarks_coor[98], face_landmarks_coor[99]);
//        PointF eye_center = new PointF((left_eye_center.x+right_eye_center.x)*0.5f,
//                (left_eye_center.y+right_eye_center.y)*0.5f);
//        PointF face_center = new PointF((_t_49_p.x+eye_center.x)*0.5f,
//                (_t_49_p.y+eye_center.y)*0.5f);
//        PointF _face_center_pic = new PointF(face_center.x*mWidth,
//                face_center.y*mHeight);
//
//        float left_right_distance =  distanceOfPoint(_3_p_pic,_29_p_pic);
//        float left_face_width = distanceOfPoint(_3_p_pic , _face_center_pic);
//        float right_face_width = distanceOfPoint(_29_p_pic, _face_center_pic);
//        float left_face_ratio = left_face_width/left_right_distance;
//        float right_face_ratio = right_face_width/left_right_distance;
//
//        float aligned_left_strength = 1.0f;
//        if(left_face_ratio > 0.5f){
//            aligned_left_strength = (left_face_ratio-0.5f)*10;
//            aligned_left_strength = aligned_left_strength < 0.0f ?  0.0f :(aligned_left_strength > 1.0f ? 1.0f : aligned_left_strength);
//            aligned_left_strength = (float)Math.pow(aligned_left_strength, 1.1);
//        }
//        float aligned_right_strength = 1.0f;
//        if(right_face_ratio > 0.5f){
//            aligned_right_strength = (right_face_ratio-0.5f)*10;
//            aligned_right_strength = aligned_right_strength < 0.0f ? 0.0f : (aligned_right_strength > 1.0f ? 1.0f : aligned_right_strength);
//            aligned_right_strength = (float)Math.pow(aligned_right_strength, 1.1);
//        }
//
//        float left_ear_offset_s = aligned_left_strength;
//        float right_ear_offset_s = aligned_right_strength;
//
//        PointF _t_116_p = new PointF(face_landmarks_coor[116*2],
//                face_landmarks_coor[116*2+1]);
//        PointF _t_117_p = new PointF(face_landmarks_coor[117*2],
//                face_landmarks_coor[117*2+1]);
//        PointF left_ear_p = offsetPointF(_t_3_p, _t_116_p, left_ear_offset_s);
//        PointF right_ear_p =  offsetPointF(_t_29_p , _t_117_p ,right_ear_offset_s);
//
//        face_landmarks_coor[122*2] = left_ear_p.x;
//        face_landmarks_coor[122*2+1] = left_ear_p.y;
//        face_landmarks_coor[123*2] = right_ear_p.x;
//        face_landmarks_coor[123*2+1] = right_ear_p.y;

        return face_landmarks_coor;
    }

}
