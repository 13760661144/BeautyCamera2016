package cn.poco.glfilter.makeup;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.pgles.PGLNativeIpl;
import cn.poco.resource.RealTimeMakeUpSubRes;

public class MakeUpLipHighLightFilter extends MakeUpBaseFilter {

    public static class MakeUpLipsHighLightBlendType {
        public final static int BlendTypeScreen = 0;//滤色
        public final static int BlendTypeExpose = 1;//高光
    }

    private float facesFeaturesCoordinates[] = new float[114 * 2];

    private int cosmeticPositionAttribute, bgTextureCoordinateAttribute, lipTextureCoordinateAttribute;
    private int filterBGCosmeticTextureUniform, filterLipTextureUniform;
    private int filterHighLightBlendTypeUniform;

    private int mLipsHighLightId;
    private int mHighLightStrengthUniform;
    private float HighlightStrength = 1.0f;   //不透明度

    private FloatBuffer clipBuffer, noRotationClipBuffer, noRotationBuffer;
    private int maTextureUnitsId;
    private int mLipHighLightBlendType = MakeUpLipsHighLightBlendType.BlendTypeExpose;

//    private float porc_mouthParamD[] = {104, 62, 163, 72, 153, 134, 201, 86, 202, 138, 245, 73, 236, 135, 306, 70};
//    private int porc_lipstick_highlight_filter_refrence_feature_table[] = {84, 103, 94, 102, 93, 101, 92, 90};

    private float porc_mouthParamD[] = {266, 158, 152, 157, 37, 133, 149, 224, 191, 159, 98, 149, 106, 215, 188, 223};//下唇8个点
    private float porc_mouthParamUp[] = {266, 158, 159, 116, 37, 133, 155, 144, 225, 132, 194, 107, 121, 101, 79, 118, 99, 140, 191, 149};//上唇10个点

    private short PorclipstickHightLightDrawIndex[] = {
            0, 1, 2,
            5, 7, 6,
            2, 1, 4,
            1, 3, 4,
            4, 3, 6,
            3, 5, 6};

    private int upLipstick_width = 300;             //素材的宽高
    private int upLipstick_height = 300;
    private int downLipstick_width = 300;
    private int downLipstick_height = 300;
    //新增上唇
    private int mLipsHighLightUpId;
    private float HighlightUpStrength = 1.0f;
    private int mLipHighLightUpBlendType = MakeUpLipsHighLightBlendType.BlendTypeExpose;

    private int porc_lipstick_up_highlight_filter_refrence_feature_table[] = {90, 87, 84, 98, 89, 88, 86, 85, 97, 99};
    private int porc_lipstick_down_highlight_filter_refrence_feature_table[] = {90, 102, 84, 93, 101, 103, 94, 92};

    private short PorclipstickHightLightUpDrawIndex[] = {
            0, 4, 9,
            4, 5, 9,
            1, 5, 9,
            1, 3, 9,
            1, 3, 8,
            1, 6, 8,
            6, 7, 8,
            2, 7, 8};

    private ByteBuffer mLipsIndex;
    private ByteBuffer mLipsUpIndex;

    public MakeUpLipHighLightFilter(Context context) {
        super(context);
        mTextureIdCount = 2;
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vertext_lipshighlight, R.raw.fragment_lipshighlight);
        return PGLNativeIpl.loadLipsHighLightProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");
        maTextureUnitsId = GLES20.glGetUniformLocation(mProgramHandle, "vTextureId");

        lipTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgramHandle, "lipTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        filterLipTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "highLightTexture");

        filterHighLightBlendTypeUniform = GLES20.glGetUniformLocation(mProgramHandle, "highLightBlendType");
        mHighLightStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "strength");
    }

    public boolean setMakeUpRes(RealTimeMakeUpSubRes realTimeMakeUpSubRes) {
        boolean flag = super.setMakeUpRes(realTimeMakeUpSubRes);
        if (flag && mRealTimeMakeUpSubRes != null && mResIsChange) {
            if (mRealTimeMakeUpSubRes.mNeedReset) {
                mLipsHighLightId = 0;
            }
            //6
            int textureId = mRealTimeMakeUpSubRes.getTextureId(6);
            if (textureId == 0) {
                initTask(0, mRealTimeMakeUpSubRes.mLipHighLight);
            } else {
                mTempTextureId[0] = textureId;
            }
            textureId = mRealTimeMakeUpSubRes.getTextureId(7);
            if (textureId == 0) {
                initTask(1, mRealTimeMakeUpSubRes.mLipHighLightUp);
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

        onPreDraw();
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        if (mLipsHighLightId > 0) {
            drawLipsHighLight(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }

        if (mLipsHighLightUpId > 0) {  //上唇
            if (mLipsHighLightId > 0) {
                int framebufferId = textureId;
                if (mGLFramebuffer != null) {
                    mGLFramebuffer.bindNext(true);
                    framebufferId = mGLFramebuffer.getPreviousTextureId();
                }
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(getTextureTarget(), framebufferId);
                GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);
                bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
            }
            drawLipsHighLightUp(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }
        mPocoFace = null;

        onAfterDraw();
        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    @Override
    public void loadTexture() {
        int size = getTaskSize();
        if (size > 0) {
            runTask();
        }
        if (size == 0 && mResIsChange && mTempTextureId != null && mRealTimeMakeUpSubRes != null) {
            mResIsChange = false;
            mLipsHighLightId = mTempTextureId[0];
            mLipsHighLightUpId = mTempTextureId[1];

            mRealTimeMakeUpSubRes.setTextureId(6, mLipsHighLightId);
            mRealTimeMakeUpSubRes.setTextureId(7, mLipsHighLightUpId);

            HighlightStrength = mRealTimeMakeUpSubRes.mLipHighLightOpaqueness;
            mLipHighLightBlendType = mRealTimeMakeUpSubRes.mLipHighLightBlendType;

            HighlightUpStrength = mRealTimeMakeUpSubRes.mLipHighLightUpOpaqueness;
            mLipHighLightUpBlendType = mRealTimeMakeUpSubRes.mLipHighLightUpBlendType;
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);

        if (mLipsHighLightId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLipsHighLightId);
            GLES20.glUniform1i(filterLipTextureUniform, 1);
        }

        if (mLipsHighLightUpId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLipsHighLightUpId);
            GLES20.glUniform1i(filterLipTextureUniform, 2);
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

    private void drawLipsHighLight(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = 1.0f - mPocoFace.points_array[i].y;
            }

            if (MakeUpLipsHighLightBlendType.BlendTypeScreen == mLipHighLightBlendType) {
                GLES20.glUniform1i(filterHighLightBlendTypeUniform, 0);

            } else if (MakeUpLipsHighLightBlendType.BlendTypeExpose == mLipHighLightBlendType) {
                GLES20.glUniform1i(filterHighLightBlendTypeUniform, 1);
            }

            float highLightCoodinate[] = new float[16];
            for (int i = 0; i < 8; i++) {
                highLightCoodinate[2 * i] = porc_mouthParamD[2 * i] / downLipstick_width;
                highLightCoodinate[2 * i + 1] = porc_mouthParamD[2 * i + 1] / downLipstick_height;
            }

            float detected_lip_coordinates[] = new float[16];
            getCurrentDetectedLipCoordinates(detected_lip_coordinates);
            float detected_lip_positions[] = new float[16];

            for (int j = 0; j < 16; j++) {
                detected_lip_positions[j] = detected_lip_coordinates[j] * 2.0f - 1.0f;
            }

            if (clipBuffer == null || clipBuffer.capacity() != detected_lip_positions.length * 4) {
                ByteBuffer bb2 = ByteBuffer.allocateDirect(detected_lip_positions.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
            } else {
                clipBuffer.clear();
            }
            clipBuffer.put(detected_lip_positions);
            clipBuffer.position(0);

            if (noRotationClipBuffer == null || noRotationClipBuffer.capacity() != detected_lip_coordinates.length * 4) {
                ByteBuffer bb = ByteBuffer.allocateDirect(detected_lip_coordinates.length * 4);
                bb.order(ByteOrder.nativeOrder());
                noRotationClipBuffer = bb.asFloatBuffer();
            } else {
                noRotationClipBuffer.clear();
            }
            noRotationClipBuffer.put(detected_lip_coordinates);
            noRotationClipBuffer.position(0);

            if (noRotationBuffer == null || noRotationBuffer.capacity() != highLightCoodinate.length * 4) {
                ByteBuffer bb1 = ByteBuffer.allocateDirect(highLightCoodinate.length * 4);
                bb1.order(ByteOrder.nativeOrder());
                noRotationBuffer = bb1.asFloatBuffer();
            } else {
                noRotationBuffer.clear();
            }
            noRotationBuffer.put(highLightCoodinate);
            noRotationBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glEnableVertexAttribArray(lipTextureCoordinateAttribute);

            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
            GLES20.glVertexAttribPointer(lipTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, noRotationBuffer);

            if (mLipsIndex == null) {
                mLipsIndex = ByteBuffer.allocateDirect(PorclipstickHightLightDrawIndex.length * 2);
                mLipsIndex.order(ByteOrder.nativeOrder());
                mLipsIndex.asShortBuffer().put(PorclipstickHightLightDrawIndex);
            }
            mLipsIndex.position(0);

            GLES20.glUniform1f(mHighLightStrengthUniform, HighlightStrength);
            GLES20.glUniform1f(maTextureUnitsId, 1.0f);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, PorclipstickHightLightDrawIndex.length, GLES20.GL_UNSIGNED_SHORT, mLipsIndex);
        }
    }

    private void drawLipsHighLightUp(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = 1.0f - mPocoFace.points_array[i].y;
            }

            if (MakeUpLipsHighLightBlendType.BlendTypeScreen == mLipHighLightUpBlendType) {
                GLES20.glUniform1i(filterHighLightBlendTypeUniform, 0);

            } else if (MakeUpLipsHighLightBlendType.BlendTypeExpose == mLipHighLightUpBlendType) {
                GLES20.glUniform1i(filterHighLightBlendTypeUniform, 1);
            }

            float highLightCoodinate[] = new float[20];
            for (int i = 0; i < 10; i++) {
                highLightCoodinate[2 * i] = porc_mouthParamUp[2 * i] / upLipstick_width;
                highLightCoodinate[2 * i + 1] = porc_mouthParamUp[2 * i + 1] / upLipstick_height;
            }

            float detected_lip_coordinates[] = new float[20];
            getCurrentDetectedLipCoordinatesUp(detected_lip_coordinates);
            float detected_lip_positions[] = new float[20];

            for (int j = 0; j < 20; j++) {
                detected_lip_positions[j] = detected_lip_coordinates[j] * 2.0f - 1.0f;
            }

            if (clipBuffer == null || clipBuffer.capacity() != detected_lip_positions.length * 4) {
                ByteBuffer bb2 = ByteBuffer.allocateDirect(detected_lip_positions.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
            } else {
                clipBuffer.clear();
            }
            clipBuffer.put(detected_lip_positions);
            clipBuffer.position(0);

            if (noRotationClipBuffer == null || noRotationClipBuffer.capacity() != detected_lip_coordinates.length * 4) {
                ByteBuffer bb = ByteBuffer.allocateDirect(detected_lip_coordinates.length * 4);
                bb.order(ByteOrder.nativeOrder());
                noRotationClipBuffer = bb.asFloatBuffer();
            } else {
                noRotationClipBuffer.clear();
            }
            noRotationClipBuffer.put(detected_lip_coordinates);
            noRotationClipBuffer.position(0);

            if (noRotationBuffer == null || noRotationBuffer.capacity() != highLightCoodinate.length * 4) {
                ByteBuffer bb1 = ByteBuffer.allocateDirect(highLightCoodinate.length * 4);
                bb1.order(ByteOrder.nativeOrder());
                noRotationBuffer = bb1.asFloatBuffer();
            } else {
                noRotationBuffer.clear();
            }
            noRotationBuffer.put(highLightCoodinate);
            noRotationBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glEnableVertexAttribArray(lipTextureCoordinateAttribute);

            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
            GLES20.glVertexAttribPointer(lipTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, noRotationBuffer);

            if (mLipsUpIndex == null) {
                mLipsUpIndex = ByteBuffer.allocateDirect(PorclipstickHightLightUpDrawIndex.length * 2);
                mLipsUpIndex.order(ByteOrder.nativeOrder());
                mLipsUpIndex.asShortBuffer().put(PorclipstickHightLightUpDrawIndex);
            }
            mLipsUpIndex.position(0);

            GLES20.glUniform1f(mHighLightStrengthUniform, HighlightUpStrength);
            GLES20.glUniform1f(maTextureUnitsId, 1.0f);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, PorclipstickHightLightUpDrawIndex.length, GLES20.GL_UNSIGNED_SHORT, mLipsUpIndex);
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

        GLES20.glDeleteTextures(2, new int[]{mLipsHighLightId, mLipsHighLightUpId}, 0);

        mLipsIndex = null;
        mLipsUpIndex = null;
    }

    private void getCurrentDetectedLipCoordinates(float coor[]) {
//        coor[0] = facesFeaturesCoordinates[84 * 2];
//        coor[1] = facesFeaturesCoordinates[84 * 2 + 1];
//        coor[2] = facesFeaturesCoordinates[103 * 2];
//        coor[3] = facesFeaturesCoordinates[103 * 2 + 1];
//        coor[4] = facesFeaturesCoordinates[94 * 2];
//        coor[5] = facesFeaturesCoordinates[94 * 2 + 1];
//        coor[6] = facesFeaturesCoordinates[102 * 2];
//        coor[7] = facesFeaturesCoordinates[102 * 2 + 1];
//        coor[8] = facesFeaturesCoordinates[93 * 2];
//        coor[9] = facesFeaturesCoordinates[93 * 2 + 1];
//        coor[10] = facesFeaturesCoordinates[101 * 2];
//        coor[11] = facesFeaturesCoordinates[101 * 2 + 1];
//        coor[12] = facesFeaturesCoordinates[92 * 2];
//        coor[13] = facesFeaturesCoordinates[92 * 2 + 1];
//        coor[14] = facesFeaturesCoordinates[90 * 2];
//        coor[15] = facesFeaturesCoordinates[90 * 2 + 1];
        if (coor.length != porc_lipstick_down_highlight_filter_refrence_feature_table.length * 2)
            return;

        for (int i = 0; i < porc_lipstick_down_highlight_filter_refrence_feature_table.length; i++) {
            coor[2 * i] = facesFeaturesCoordinates[2 * porc_lipstick_down_highlight_filter_refrence_feature_table[i]];
            coor[2 * i + 1] = facesFeaturesCoordinates[2 * porc_lipstick_down_highlight_filter_refrence_feature_table[i] + 1];
        }

    }

    //上唇
    private void getCurrentDetectedLipCoordinatesUp(float coor[]) {
        if (coor.length != porc_lipstick_up_highlight_filter_refrence_feature_table.length * 2)
            return;

        for (int i = 0; i < porc_lipstick_up_highlight_filter_refrence_feature_table.length; i++) {
            coor[2 * i] = facesFeaturesCoordinates[2 * porc_lipstick_up_highlight_filter_refrence_feature_table[i]];
            coor[2 * i + 1] = facesFeaturesCoordinates[2 * porc_lipstick_up_highlight_filter_refrence_feature_table[i] + 1];
        }
    }
}
