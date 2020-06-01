package cn.poco.glfilter.makeup;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.pgles.PGLNativeIpl;
import cn.poco.resource.RealTimeMakeUpSubRes;

public class MakeUpBlushFilter extends MakeUpBaseFilter {

    private float facesFeaturesCoordinates[] = new float[114 * 2];

    private int cosmeticPositionAttribute, bgTextureCoordinateAttribute, filterBlushTextureCoordinateAttribute;
    private int filterBGCosmeticTextureUniform, filterLeftBlushTextureUniform, filterRightBlushTextureUniform;

    private int filterLeftBlushRoiUniform, filterRightBlushRoiUniform;
    private int filterLeftBlushStretchUniform, filterRightBlushStretchUniform;
    private int filterCosSinUniform, filterNegSinCosUniform;
    private int filterFaceRotateCenterUniform;

    private int filterBlushStrengthUniform;
    private int filterBlushColorUniform;
    private int filterStepLeftRightXValueUniform;

    private int mBlushLeftId, mBlushRightId;
    private int mBlushColor = 0xeb5f2100;    //腮红颜色  rgba
    private float mBlushStrength = 1.0f;            //腮红不透明度  0 -1.0f

    private boolean isUpdatingBlushMask;
    private FloatBuffer clipBuffer, noRotationClipBuffer, noRotationBuffer;
    private int maTextureUnitsId;

    public MakeUpBlushFilter(Context context) {
        super(context);
        mTextureIdCount = 2;
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vertex_blushfilter, R.raw.fragment_blushfilter);
        return PGLNativeIpl.loadBlushProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");
        maTextureUnitsId = GLES20.glGetUniformLocation(mProgramHandle, "vTextureId");

        filterBlushTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgramHandle, "inputTemplateTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        filterLeftBlushTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "left_blush_texture");
        filterRightBlushTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "right_blush_texture");

        filterLeftBlushRoiUniform = GLES20.glGetUniformLocation(mProgramHandle, "left_blush_roi");
        filterRightBlushRoiUniform = GLES20.glGetUniformLocation(mProgramHandle, "right_blush_roi");
        filterLeftBlushStretchUniform = GLES20.glGetUniformLocation(mProgramHandle, "left_blush_stretch");
        filterRightBlushStretchUniform = GLES20.glGetUniformLocation(mProgramHandle, "right_blush_stretch");

        filterCosSinUniform = GLES20.glGetUniformLocation(mProgramHandle, "Cos_Sin");
        filterNegSinCosUniform = GLES20.glGetUniformLocation(mProgramHandle, "negSin_Cos");
        filterFaceRotateCenterUniform = GLES20.glGetUniformLocation(mProgramHandle, "RotateCenter");

        filterBlushStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "blush_strength");
        filterBlushColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "blush_color");
        filterStepLeftRightXValueUniform = GLES20.glGetUniformLocation(mProgramHandle, "Mid_X_of_left_right");
    }

    public boolean setMakeUpRes(RealTimeMakeUpSubRes realTimeMakeUpSubRes) {
        boolean flag = super.setMakeUpRes(realTimeMakeUpSubRes);
        if (flag && mRealTimeMakeUpSubRes != null && mResIsChange) {
            if (mRealTimeMakeUpSubRes.mNeedReset) {
                mBlushLeftId = 0;
                mBlushRightId = 0;
            }
            //3 4
            int textureId = mRealTimeMakeUpSubRes.getTextureId(3);
            if (textureId == 0) {
                initTask(0, mRealTimeMakeUpSubRes.mBlushLeft);
            } else {
                mTempTextureId[0] = textureId;
            }
            textureId = mRealTimeMakeUpSubRes.getTextureId(4);
            if (textureId == 0) {
                initTask(1, mRealTimeMakeUpSubRes.mBlushRight);
            } else {
                mTempTextureId[1] = textureId;
            }
        }
        return true;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        loadTexture();
        bindTexture(textureId);

//        mPocoFace = FaceDataHelper.getInstance().changeFace(0).getFace();

        onPreDraw();
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        if (mBlushLeftId > 0 && mBlushRightId > 0) {
            drawBlush(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }
        mPocoFace = null;

        onAfterDraw();
        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    public void loadTexture() {
        int size = getTaskSize();
        if (size > 0) {
            runTask();
        }
        if (size == 0 && mResIsChange && mTempTextureId != null && mRealTimeMakeUpSubRes != null) {
            mResIsChange = false;
            mBlushLeftId = mTempTextureId[0];
            mBlushRightId = mTempTextureId[1];

            mRealTimeMakeUpSubRes.setTextureId(3, mBlushLeftId);
            mRealTimeMakeUpSubRes.setTextureId(4, mBlushRightId);

            mBlushColor = mRealTimeMakeUpSubRes.mBlushColor;    //腮红颜色  rgba
            mBlushStrength = mRealTimeMakeUpSubRes.mBlushOpaqueness;
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);

        if (mBlushLeftId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlushLeftId);
            GLES20.glUniform1i(filterLeftBlushTextureUniform, 1);
        }
        if (mBlushRightId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBlushRightId);
            GLES20.glUniform1i(filterRightBlushTextureUniform, 2);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        //镜头画面
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glUniform1f(maTextureUnitsId, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

    private void drawBlush(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = /*1.0f -*/ mPocoFace.points_array[i].y;
            }

            int blush_r = (mBlushColor >> 24) & 0xff;
            int blush_g = (mBlushColor >> 16) & 0xff;
            int blush_b = (mBlushColor >> 8) & 0xff;
            GLES20.glUniform3f(filterBlushColorUniform, blush_r / 255.0f, blush_g / 255.0f, blush_b / 255.0f);

            PointF p1, p2, p3, p4, p5; //82, 73, 3, 6, (95+12)/2
            PointF p6, p7, p8, p9, p10;

            p1 = new PointF(facesFeaturesCoordinates[82 * 2], 1.0f - facesFeaturesCoordinates[82 * 2 + 1]);
            p2 = new PointF(facesFeaturesCoordinates[73 * 2], 1.0f - facesFeaturesCoordinates[73 * 2 + 1]);
            p3 = new PointF(facesFeaturesCoordinates[3 * 2], 1.0f - facesFeaturesCoordinates[3 * 2 + 1]);
            p4 = new PointF(facesFeaturesCoordinates[6 * 2], 1.0f - facesFeaturesCoordinates[6 * 2 + 1]);
            p5 = new PointF((facesFeaturesCoordinates[12 * 2] + facesFeaturesCoordinates[95 * 2]) * 0.5f,
                    (1.0f - facesFeaturesCoordinates[12 * 2 + 1] + 1.0f - facesFeaturesCoordinates[95 * 2 + 1]) * 0.5f);

            p6 = new PointF(facesFeaturesCoordinates[83 * 2], 1.0f - facesFeaturesCoordinates[83 * 2 + 1]);
            p7 = new PointF(facesFeaturesCoordinates[76 * 2], 1.0f - facesFeaturesCoordinates[76 * 2 + 1]);
            p8 = new PointF(facesFeaturesCoordinates[29 * 2], 1.0f - facesFeaturesCoordinates[29 * 2 + 1]);
            p9 = new PointF(facesFeaturesCoordinates[26 * 2], 1.0f - facesFeaturesCoordinates[26 * 2 + 1]);
            p10 = new PointF((facesFeaturesCoordinates[20 * 2] + facesFeaturesCoordinates[91 * 2]) * 0.5f,
                    (1.0f - facesFeaturesCoordinates[20 * 2 + 1] + 1.0f - facesFeaturesCoordinates[91 * 2 + 1]) * 0.5f);

            PointF eyePointLeft = new PointF(facesFeaturesCoordinates[52 * 2], facesFeaturesCoordinates[52 * 2 + 1]);
            PointF eyePointRight = new PointF(facesFeaturesCoordinates[61 * 2], facesFeaturesCoordinates[61 * 2 + 1]);

            float atan = (float) Math.atan2(eyePointLeft.y - eyePointRight.y, eyePointLeft.x - eyePointRight.x);
            float roll = (float) (atan / Math.PI * 180.0 - 180.0);

            float mcos = (float) Math.cos(roll / 180.0 * Math.PI);
            float msin = (float) Math.sin(roll / 180.0 * Math.PI);


            GLES20.glUniform2f(filterCosSinUniform, mcos, msin);
            GLES20.glUniform2f(filterNegSinCosUniform, -msin, mcos);

            PointF faceRotateCenter = new PointF((p2.x + p7.x) / 2.0f, (p2.y + p7.y) / 2.0f);
            GLES20.glUniform2f(filterFaceRotateCenterUniform, faceRotateCenter.x, faceRotateCenter.y);

            GLES20.glUniform1f(filterStepLeftRightXValueUniform, faceRotateCenter.x);

            RectF leftFaceRoi = new RectF(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            leftFaceRoi = calculateRotateCoverRect(leftFaceRoi, mcos, msin, faceRotateCenter, p1);
            leftFaceRoi = calculateRotateCoverRect(leftFaceRoi, mcos, msin, faceRotateCenter, p2);
            leftFaceRoi = calculateRotateCoverRect(leftFaceRoi, mcos, msin, faceRotateCenter, p3);
            leftFaceRoi = calculateRotateCoverRect(leftFaceRoi, mcos, msin, faceRotateCenter, p4);
            leftFaceRoi = calculateRotateCoverRect(leftFaceRoi, mcos, msin, faceRotateCenter, p5);

            RectF rightFaceRoi = new RectF(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            rightFaceRoi = calculateRotateCoverRect(rightFaceRoi, mcos, msin, faceRotateCenter, p6);
            rightFaceRoi = calculateRotateCoverRect(rightFaceRoi, mcos, msin, faceRotateCenter, p7);
            rightFaceRoi = calculateRotateCoverRect(rightFaceRoi, mcos, msin, faceRotateCenter, p8);
            rightFaceRoi = calculateRotateCoverRect(rightFaceRoi, mcos, msin, faceRotateCenter, p9);
            rightFaceRoi = calculateRotateCoverRect(rightFaceRoi, mcos, msin, faceRotateCenter, p10);

            GLES20.glUniform2f(filterLeftBlushRoiUniform, leftFaceRoi.left, leftFaceRoi.top);
            GLES20.glUniform2f(filterRightBlushRoiUniform, rightFaceRoi.left, rightFaceRoi.top);

            GLES20.glUniform2f(filterLeftBlushStretchUniform, 1.0f / leftFaceRoi.width(), 1.0f / leftFaceRoi.height());
            GLES20.glUniform2f(filterRightBlushStretchUniform, 1.0f / rightFaceRoi.width(), 1.0f / rightFaceRoi.height());

            GLES20.glUniform1f(filterBlushStrengthUniform, mBlushStrength);  //for test

            if (clipBuffer == null) {
                float[] imageVertices = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
                ByteBuffer bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
                clipBuffer.put(imageVertices);
            }
            clipBuffer.position(0);

            if (noRotationClipBuffer == null) {
                float[] noRotationTextureCoordinates = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                ByteBuffer bb = ByteBuffer.allocateDirect(noRotationTextureCoordinates.length * 4);
                bb.order(ByteOrder.nativeOrder());
                noRotationClipBuffer = bb.asFloatBuffer();
                noRotationClipBuffer.put(noRotationTextureCoordinates);
            }
            noRotationClipBuffer.position(0);

            if (noRotationBuffer == null) {
                float[] flipVerticalTextureCoordinates = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                ByteBuffer bb1 = ByteBuffer.allocateDirect(flipVerticalTextureCoordinates.length * 4);
                bb1.order(ByteOrder.nativeOrder());
                noRotationBuffer = bb1.asFloatBuffer();
                noRotationBuffer.put(flipVerticalTextureCoordinates);
            }
            noRotationBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glEnableVertexAttribArray(filterBlushTextureCoordinateAttribute);

            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
            GLES20.glVertexAttribPointer(filterBlushTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, noRotationBuffer);

            GLES20.glUniform1f(maTextureUnitsId, 1.0f);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        GLES20.glDeleteTextures(2, new int[]{mBlushLeftId, mBlushLeftId}, 0);
    }

    public RectF calculateRotateCoverRect(RectF original, float cos, float sin, PointF center, PointF p) {
        PointF localPointF = new PointF();
        localPointF.x = (center.x + (p.x - center.x) * cos + (p.y - center.y) * (-sin));
        localPointF.y = (center.y + (p.x - center.x) * sin + (p.y - center.y) * cos);

        RectF r_rect = new RectF(0, 0, 0, 0);
        r_rect.left = Math.min(localPointF.x, original.left);
        r_rect.top = Math.min(localPointF.y, original.top);
        r_rect.right = Math.max(localPointF.x, original.right);
        r_rect.bottom = Math.max(localPointF.y, original.bottom);

        return r_rect;
    }

}
