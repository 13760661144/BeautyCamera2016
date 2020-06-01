package cn.poco.glfilter.makeup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.glfilter.base.GlUtil;
import cn.poco.image.PocoCompositeOperator;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.image.PocoNativeFilter;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;
import cn.poco.resource.RealTimeMakeUpSubRes;

public class MakeUpLipFilter extends MakeUpBaseFilter {

    private float faceData[] = new float[121 * 2];
    private float facesFeaturesCoordinates[] = new float[114 * 2];

    private int cosmeticPositionAttribute, bgTextureCoordinateAttribute, lipTextureCoordinateAttribute;
    private int filterBGCosmeticTextureUniform, filterLipTextureUniform, filterLookupTextureUniform;

    private int lipCosmeticColorUniform;
    private int lipCosmeticColorStrength;
    private int lipCosmeticColorBlendTypeUniform;

    private int mTableId, mLipMaskId;
    private PointF lipMaskOriginal = new PointF();
    private int mLipColor = 0xc4001200;
    private float mLipCosmeticColorStrength = 1.0f;
    private int mLipBlendType = PocoCompositeOperator.SoftLightCompositeOp;

    private int lipCColorUniform, lipAColorUniform;
    private int lipCstrength = 255;    //C值 0-255
    private int lipAstrength = 255;   //A值 0-255
    private int currentFaceOrientation = 0;
    private int mFaceOrientation = -1;

    private FloatBuffer clipBuffer, noRotationClipBuffer, noRotationBuffer;
    private int lipMaskWidth, lipMaskHeight;

    private int maTextureUnitsId;

    public MakeUpLipFilter(Context context) {
        super(context);
        mTextureIdCount = 1;
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vertex_lipstick, R.raw.fragment_lipdtick);
        return PGLNativeIpl.loadMouthProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");
        maTextureUnitsId = GLES20.glGetUniformLocation(mProgramHandle, "vTextureId");

        lipTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgramHandle, "lipTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        filterLipTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "lipMaskTexture");
        filterLookupTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "lookupTexture");

        lipCosmeticColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "lipCosmectic_color_u");
        lipCosmeticColorStrength = GLES20.glGetUniformLocation(mProgramHandle, "lipColorStrength");
        lipCosmeticColorBlendTypeUniform = GLES20.glGetUniformLocation(mProgramHandle, "lipStickBlendType");

        lipCColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "lip_color_blend_strength");
        lipAColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "lip_color_mix_strength");
    }

    @Override
    public boolean setMakeUpRes(RealTimeMakeUpSubRes realTimeMakeUpSubRes) {
        boolean flag = super.setMakeUpRes(realTimeMakeUpSubRes);
        if (flag && mRealTimeMakeUpSubRes != null && mResIsChange) {
            if (mRealTimeMakeUpSubRes.mNeedReset) {
                mTableId = 0;
            }
            //5
            int textureId = mRealTimeMakeUpSubRes.getTextureId(5);
            if (textureId == 0) {
                initTask(0, mRealTimeMakeUpSubRes.mLip);
            } else {
                mTempTextureId[0] = textureId;
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

        if (mTableId > 0 && mLipMaskId > 0) {
            drawLips(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
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
            mTableId = mTempTextureId[0];
            mRealTimeMakeUpSubRes.setTextureId(5, mTableId);

            mLipColor = mRealTimeMakeUpSubRes.mLipColor;
            mLipCosmeticColorStrength = mRealTimeMakeUpSubRes.mLipOpaqueness;
            lipCstrength = mRealTimeMakeUpSubRes.mLipCValue;
            lipAstrength = mRealTimeMakeUpSubRes.mLipAValue;
            if (mRealTimeMakeUpSubRes.mLipBlendType > 0) {
                mLipBlendType = mRealTimeMakeUpSubRes.mLipBlendType;
            }
        }

        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = mPocoFace.points_array[i].y;
            }

            PointF left_eye_pic = new PointF(mPocoFace.points_array[Sslandmarks.lEyeCenter].x, mPocoFace.points_array[Sslandmarks.lEyeCenter].y);
            PointF right_eye_pic = new PointF(mPocoFace.points_array[Sslandmarks.rEyeCenter].x, mPocoFace.points_array[Sslandmarks.rEyeCenter].y);

            left_eye_pic.x = left_eye_pic.x * mHeight;
            left_eye_pic.y = (1.0f - left_eye_pic.y) * mWidth;
            right_eye_pic.x = right_eye_pic.x * mHeight;
            right_eye_pic.y = (1.0f - right_eye_pic.y) * mWidth;
            currentFaceOrientation = PocoFaceOrientation.enquirySimilarityFaceOrientation(left_eye_pic, right_eye_pic);

            //计算获取唇部蒙版
            Bitmap mouth = getMouthMask(facesFeaturesCoordinates, mCameraWidth, mCameraHeight, currentFaceOrientation);
            if (mouth != null) {
                lipMaskWidth = mouth.getWidth();
                lipMaskHeight = mouth.getHeight();

                mLipMaskId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, mouth);
                if (!mouth.isRecycled()) {
                    mouth.recycle();
                }
            }
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);

        if (mTableId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTableId);
            GLES20.glUniform1i(filterLookupTextureUniform, 1);
        }

        if (mLipMaskId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLipMaskId);
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

    private void drawLips(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = /*1.0f -*/ mPocoFace.points_array[i].y;
            }

            if (PocoCompositeOperator.SoftLightCompositeOp == mLipBlendType) {
                GLES20.glUniform1i(lipCosmeticColorBlendTypeUniform, 0);
            } else if (PocoCompositeOperator.OverlayCompositeOp == mLipBlendType) {
                GLES20.glUniform1i(lipCosmeticColorBlendTypeUniform, 1);
            }

            int lip_r = (mLipColor >> 24) & 0xff;
            int lip_g = (mLipColor >> 16) & 0xff;
            int lip_b = (mLipColor >> 8) & 0xff;
            GLES20.glUniform3f(lipCosmeticColorUniform, lip_r, lip_g, lip_b);

            GLES20.glUniform1f(lipCosmeticColorStrength, mLipCosmeticColorStrength);

            float fbo_size_width = mCameraWidth;
            float fbo_size_height = mCameraHeight;

            if (PocoFaceOrientation.PORSFaceOrientationLeft == currentFaceOrientation || PocoFaceOrientation.PORSFaceOrientationRight == currentFaceOrientation) {
                int temp = lipMaskHeight;
                lipMaskHeight = lipMaskWidth;
                lipMaskWidth = temp;
            }

            float lip_mask_coor_l = lipMaskOriginal.x / fbo_size_width;
            float lip_mask_coor_r = (lipMaskOriginal.x + lipMaskWidth) / fbo_size_width;
            float lip_mask_coor_t = lipMaskOriginal.y / fbo_size_height;
            float lip_mask_coor_b = (lipMaskOriginal.y + lipMaskHeight) / fbo_size_height;


            float[] lip_area_coor = {
                    lip_mask_coor_l, lip_mask_coor_b,
                    lip_mask_coor_r, lip_mask_coor_b,
                    lip_mask_coor_l, lip_mask_coor_t,
                    lip_mask_coor_r, lip_mask_coor_t,
            };
            float[] lip_area_position = {
                    lip_mask_coor_l * 2.0f - 1, lip_mask_coor_b * 2.0f - 1,
                    lip_mask_coor_r * 2.0f - 1, lip_mask_coor_b * 2.0f - 1,
                    lip_mask_coor_l * 2.0f - 1, lip_mask_coor_t * 2.0f - 1,
                    lip_mask_coor_r * 2.0f - 1, lip_mask_coor_t * 2.0f - 1,
            };

            if (clipBuffer == null) {
                ByteBuffer bb2 = ByteBuffer.allocateDirect(lip_area_position.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                clipBuffer = bb2.asFloatBuffer();
            }
            clipBuffer.clear();
            clipBuffer.put(lip_area_position);
            clipBuffer.position(0);

            if (noRotationClipBuffer == null) {
                ByteBuffer bb = ByteBuffer.allocateDirect(lip_area_coor.length * 4);
                bb.order(ByteOrder.nativeOrder());
                noRotationClipBuffer = bb.asFloatBuffer();
            }
            noRotationClipBuffer.clear();
            noRotationClipBuffer.put(lip_area_coor);
            noRotationClipBuffer.position(0);

            if (noRotationBuffer == null || currentFaceOrientation != mFaceOrientation) {
                float[] noRotationTextureCoordinates = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
                if (PocoFaceOrientation.PORSFaceOrientationLeft == currentFaceOrientation) {
                    noRotationTextureCoordinates[0] = 1.f;
                    noRotationTextureCoordinates[1] = 0.f;
                    noRotationTextureCoordinates[2] = 1.f;
                    noRotationTextureCoordinates[3] = 1.f;
                    noRotationTextureCoordinates[4] = 0.f;
                    noRotationTextureCoordinates[5] = 0.f;
                    noRotationTextureCoordinates[6] = 0.f;
                    noRotationTextureCoordinates[7] = 1.f;
                } else if (PocoFaceOrientation.PORSFaceOrientationRight == currentFaceOrientation) {
                    noRotationTextureCoordinates[0] = 0.f;
                    noRotationTextureCoordinates[1] = 1.f;
                    noRotationTextureCoordinates[2] = 0.f;
                    noRotationTextureCoordinates[3] = 0.f;
                    noRotationTextureCoordinates[4] = 1.f;
                    noRotationTextureCoordinates[5] = 1.f;
                    noRotationTextureCoordinates[6] = 1.f;
                    noRotationTextureCoordinates[7] = 0.f;
                } else if (PocoFaceOrientation.PORSFaceOrientationDown == currentFaceOrientation) {
                    noRotationTextureCoordinates[0] = 1.f;
                    noRotationTextureCoordinates[1] = 1.f;
                    noRotationTextureCoordinates[2] = 0.f;
                    noRotationTextureCoordinates[3] = 1.f;
                    noRotationTextureCoordinates[4] = 1.f;
                    noRotationTextureCoordinates[5] = 0.f;
                    noRotationTextureCoordinates[6] = 0.f;
                    noRotationTextureCoordinates[7] = 0.f;
                }
                if (noRotationBuffer == null) {
                    ByteBuffer bb1 = ByteBuffer.allocateDirect(noRotationTextureCoordinates.length * 4);
                    bb1.order(ByteOrder.nativeOrder());
                    noRotationBuffer = bb1.asFloatBuffer();
                }
                noRotationBuffer.clear();
                noRotationBuffer.put(noRotationTextureCoordinates);

                mFaceOrientation = currentFaceOrientation;
            }
            noRotationBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glEnableVertexAttribArray(lipTextureCoordinateAttribute);

            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
            GLES20.glVertexAttribPointer(lipTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, noRotationBuffer);

            GLES20.glUniform1f(lipCColorUniform, lipCstrength / 255.f);
            GLES20.glUniform1f(lipAColorUniform, lipAstrength / 255.f);
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
        GLES20.glDeleteTextures(1, new int[]{mLipMaskId}, 0);
    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        GLES20.glDeleteTextures(2, new int[]{mTableId, mLipMaskId}, 0);
    }

    public static float[] GetLipPos(float[] features) {
        float[] lippos = new float[16 * 2];

        lippos[0] = features[2 * 84];        //左
        lippos[1] = features[2 * 84 + 1];
        lippos[2] = features[2 * 85];
        lippos[3] = features[2 * 85 + 1];
        lippos[4] = features[2 * 86];
        lippos[5] = features[2 * 86 + 1];
        lippos[6] = features[2 * 87];
        lippos[7] = features[2 * 87 + 1];
        lippos[8] = features[2 * 88];
        lippos[9] = features[2 * 88 + 1];
        lippos[10] = features[2 * 89];
        lippos[11] = features[2 * 89 + 1];

        lippos[12] = features[2 * 90];        //右
        lippos[13] = features[2 * 90 + 1];
        lippos[14] = features[2 * 92];
        lippos[15] = features[2 * 92 + 1];
        lippos[16] = features[2 * 93];
        lippos[17] = features[2 * 93 + 1];
        lippos[18] = features[2 * 94];
        lippos[19] = features[2 * 94 + 1];

        lippos[20] = features[2 * 97];
        lippos[21] = features[2 * 97 + 1];
        lippos[22] = features[2 * 98];
        lippos[23] = features[2 * 98 + 1];
        lippos[24] = features[2 * 99];
        lippos[25] = features[2 * 99 + 1];
        lippos[26] = features[2 * 101];
        lippos[27] = features[2 * 101 + 1];
        lippos[28] = features[2 * 102];
        lippos[29] = features[2 * 102 + 1];
        lippos[30] = features[2 * 103];
        lippos[31] = features[2 * 103 + 1];

        return lippos;
    }

    public Bitmap getMouthMask(float[] feature, int width, int height, int orientation) {
//        double t = System.currentTimeMillis();
        float lipPos_f[] = GetLipPos(feature);
        int lipPos_i[] = new int[lipPos_f.length];

        int minX = width, maxX = 0, minY = height, maxY = 0;
        for (int i = 0; i < lipPos_f.length / 2; i++) {
            lipPos_i[2 * i] = (int) (lipPos_f[2 * i] * width);
            lipPos_i[2 * i + 1] = (int) (lipPos_f[2 * i + 1] * height);

            minX = Math.min(minX, lipPos_i[2 * i]);
            maxX = Math.max(maxX, lipPos_i[2 * i]);
            minY = Math.min(minY, lipPos_i[2 * i + 1]);
            maxY = Math.max(maxY, lipPos_i[2 * i + 1]);
        }

        minX = Math.max(0, minX - 3);
        maxX = Math.min(width - 1, maxX + 3);
        minY = Math.max(0, minY - 3);
        maxY = Math.min(height - 1, maxY + 3);

        for (int i = 0; i < lipPos_f.length / 2; i++) {
            lipPos_i[2 * i] -= minX;
            lipPos_i[2 * i + 1] -= minY;
        }

        int destWidth = maxX - minX + 1;
        int destHeight = maxY - minY + 1;

        int lip_mask_width = destWidth;
        int lip_mask_height = destHeight;

        switch (orientation) {
            case PocoFaceOrientation.PORSFaceOrientationRight: {
                for (int i = 0; i < 16; i++) {
                    int t_x = lipPos_i[2 * i];
                    lipPos_i[2 * i] = lipPos_i[2 * i + 1];
                    lipPos_i[2 * i + 1] = destWidth - t_x;
                }
                lip_mask_width = destHeight;
                lip_mask_height = destWidth;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationLeft: {
                for (int i = 0; i < 16; i++) {
                    int t_x = lipPos_i[2 * i];
                    lipPos_i[2 * i] = destHeight - lipPos_i[2 * i + 1];
                    lipPos_i[2 * i + 1] = t_x;
                }

                lip_mask_width = destHeight;
                lip_mask_height = destWidth;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationDown: {
                for (int i = 0; i < 16; i++) {
                    lipPos_i[2 * i] = destWidth - lipPos_i[2 * i];
                    lipPos_i[2 * i + 1] = destHeight - lipPos_i[2 * i + 1];
                }
            }
            break;
        }


        Bitmap dest = null;
        if (destWidth > 0 && destHeight > 0) {
            dest = Bitmap.createBitmap(lip_mask_width, lip_mask_height, Bitmap.Config.ARGB_8888);

            PocoNativeFilter.liveGetMouthMask(dest, lipPos_i);

//            System.out.println("getmouthmask time:" + (System.currentTimeMillis() - t));
        }

        lipMaskOriginal.x = (float) minX;
        lipMaskOriginal.y = (float) height - maxY;

        return dest;
    }
}
