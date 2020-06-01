package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.image.PocoFace;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.image.PocoNativeFilter;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;
import my.beautyCamera.R;

public class WhitenTeethFilter extends DefaultFilter {

    private float[] facesFeaturesCoordinates = new float[114 * 2];

    private int lipTextureCoordinateAttribute;
    private int filterBGCosmeticTextureUniform, filterLipTextureUniform, filterLookupTextureUniform;
    private int lipCosmeticColorStrength;

    private ArrayList<Integer> mLipMaskIds;
    private int[] mLipMaskIdArr;
    private int mTableId, mLipMaskId;
    private PointF lipMaskOriginal = new PointF();
    private float _lipCosmeticColorStrength = 1.0f;

    private int currentFaceOrientation = 0;

    private PocoFace mPocoFace;
    private FloatBuffer clipBuffer, noRotationClipBuffer, noRotationBuffer;
    private int lipMaskWidth, lipMaskHeight;

    private int maTextureUnitsId;

    public WhitenTeethFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vert_whiteteeth, R.raw.frag_whiteteeth);
        return PGLNativeIpl.loadWhitenTeethProgram();
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");
        lipTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgramHandle, "teethTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");

        filterLipTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "teethMaskTexture");
        filterLookupTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "lookupTexture");

        lipCosmeticColorStrength = GLES20.glGetUniformLocation(mProgramHandle, "whiten_strength");

        maTextureUnitsId = GLES20.glGetUniformLocation(mProgramHandle, "vTextureId");
    }

    public void setWhitenTeethScale(float scale) {
        _lipCosmeticColorStrength = scale / 100.f;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();

        int faceSize = FaceDataHelper.getInstance().getFaceSize();
        if (faceSize <= 0) {
            faceSize = 1;
        }
        if (faceSize >= 1) {
            for (int i = 0; i < faceSize; i++) {
                mPocoFace = FaceDataHelper.getInstance().changeFace(i).getFace();

                loadTexture();
                if (i > 0) {
                    mGLFramebuffer.bindNext(true);
                    textureId = mGLFramebuffer.getPreviousTextureId();
                }
                bindTexture(textureId);
                if (i == 0) {
                    bindLipTableTexture();
                }
                bindLipMaskTexture();
                bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
            }
        }
        drawArrays(firstVertex, vertexCount);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();

        mPocoFace = null;
    }

    private void loadTexture() {
        if (mPocoFace != null && mPocoFace.points_count > 0) {
            if (mTableId <= 0) {
                mTableId = getBitmapTextureId(R.drawable.teethwhite);
            }
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = mPocoFace.points_array[i].y;
            }

            PointF left_eye_pic = new PointF(mPocoFace.points_array[Sslandmarks.lEyeCenter].x, mPocoFace.points_array[Sslandmarks.lEyeCenter].y);
            PointF right_eye_pic = new PointF(mPocoFace.points_array[Sslandmarks.rEyeCenter].x, mPocoFace.points_array[Sslandmarks.rEyeCenter].y);

            left_eye_pic.x = left_eye_pic.x * mWidth;
            left_eye_pic.y = (1.0f - left_eye_pic.y) * mHeight;
            right_eye_pic.x = right_eye_pic.x * mWidth;
            right_eye_pic.y = (1.0f - right_eye_pic.y) * mHeight;
            currentFaceOrientation = PocoFaceOrientation.enquirySimilarityFaceOrientation(left_eye_pic, right_eye_pic);

            //计算获取牙齿蒙版
            if (mLipMaskId > 0) {
                if (mLipMaskIds == null) {
                    mLipMaskIds = new ArrayList<>();
                }
                mLipMaskIds.add(mLipMaskId);
//                if (mLipMaskIdArr == null) {
//                    mLipMaskIdArr = new int[1];
//                }
//                mLipMaskIdArr[0] = mLipMaskId;
//                GLES20.glDeleteTextures(1, mLipMaskIdArr, 0);
            }
            mLipMaskId = 0;
            Bitmap mouth = getTeethMask(facesFeaturesCoordinates, mWidth, mHeight, currentFaceOrientation);
            if (mouth != null) {
                lipMaskWidth = mouth.getWidth();
                lipMaskHeight = mouth.getHeight();

                try {
                    mLipMaskId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, mouth);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (!mouth.isRecycled()) {
                    mouth.recycle();
                }
            }
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);
    }

    private void bindLipTableTexture() {
        if (mTableId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTableId);
            GLES20.glUniform1i(filterLookupTextureUniform, 1);
        }
    }

    private void bindLipMaskTexture() {
        if (mLipMaskId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLipMaskId);
            GLES20.glUniform1i(filterLipTextureUniform, 2);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glUniform1f(maTextureUnitsId, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (mTableId > 0 && mLipMaskId > 0 && mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
                facesFeaturesCoordinates[2 * i + 1] = /*1.0f -*/ mPocoFace.points_array[i].y;
            }

            GLES20.glUniform1f(lipCosmeticColorStrength, _lipCosmeticColorStrength);

            float fbo_size_width = mWidth;
            float fbo_size_height = mHeight;

            if (PocoFaceOrientation.PORSFaceOrientationLeft == currentFaceOrientation || PocoFaceOrientation.PORSFaceOrientationRight == currentFaceOrientation) {
                int temp = lipMaskHeight;
                lipMaskHeight = lipMaskWidth;
                lipMaskWidth = temp;
            }

            float lip_mask_coor_l = lipMaskOriginal.x / fbo_size_width;
            float lip_mask_coor_r = (lipMaskOriginal.x + lipMaskWidth) / fbo_size_width;
            float lip_mask_coor_t = lipMaskOriginal.y / fbo_size_height;
            float lip_mask_coor_b = (lipMaskOriginal.y + lipMaskHeight) / fbo_size_height;

            float noRotationTextureCoordinates[] = {
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
            };

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

            float lip_area_coor[] = {
                    lip_mask_coor_l, lip_mask_coor_b,
                    lip_mask_coor_r, lip_mask_coor_b,
                    lip_mask_coor_l, lip_mask_coor_t,
                    lip_mask_coor_r, lip_mask_coor_t,
            };
            float lip_area_position[] = {
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

            if (noRotationBuffer == null) {
                ByteBuffer bb1 = ByteBuffer.allocateDirect(noRotationTextureCoordinates.length * 4);
                bb1.order(ByteOrder.nativeOrder());
                noRotationBuffer = bb1.asFloatBuffer();
            }
            noRotationBuffer.clear();
            noRotationBuffer.put(noRotationTextureCoordinates);
            noRotationBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glEnableVertexAttribArray(lipTextureCoordinateAttribute);

            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
            GLES20.glVertexAttribPointer(lipTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, noRotationBuffer);

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

        if (mLipMaskIds == null) {
            mLipMaskIds = new ArrayList<>();
        }
        if (mLipMaskIds != null) {
            mLipMaskIds.add(mLipMaskId);
            mLipMaskIdArr = new int[mLipMaskIds.size()];
            for (int i = 0; i < mLipMaskIdArr.length; i++) {
                mLipMaskIdArr[i] = mLipMaskIds.get(i);
            }
            mLipMaskIds.clear();
        }

        if (mLipMaskIdArr != null) {
//            mLipMaskIdArr[0] = mLipMaskId;
            GLES20.glDeleteTextures(mLipMaskIdArr.length, mLipMaskIdArr, 0);
            mLipMaskIdArr = null;
        }
        mLipMaskId = 0;
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

    public Bitmap getTeethMask(float[] feature, int width, int height, int orientation) {
        float[] lipPos_f = GetLipPos(feature);
        int[] lipPos_i = new int[lipPos_f.length];

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

        int mWidth = maxX - minX + 1;
        int mHeight = maxY - minY + 1;

        if (mWidth <= 0 || mHeight <= 0) {
            return null;
        }

        int lip_mask_width = mWidth;
        int lip_mask_height = mHeight;

        switch (orientation) {
            case PocoFaceOrientation.PORSFaceOrientationRight: {
                for (int i = 0; i < 16; i++) {
                    int t_x = lipPos_i[2 * i];
                    lipPos_i[2 * i] = lipPos_i[2 * i + 1];
                    lipPos_i[2 * i + 1] = mWidth - t_x;
                }
                lip_mask_width = mHeight;
                lip_mask_height = mWidth;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationLeft: {
                for (int i = 0; i < 16; i++) {
                    int t_x = lipPos_i[2 * i];
                    lipPos_i[2 * i] = mHeight - lipPos_i[2 * i + 1];
                    lipPos_i[2 * i + 1] = t_x;
                }

                lip_mask_width = mHeight;
                lip_mask_height = mWidth;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationDown: {
                for (int i = 0; i < 16; i++) {
                    lipPos_i[2 * i] = mWidth - lipPos_i[2 * i];
                    lipPos_i[2 * i + 1] = mHeight - lipPos_i[2 * i + 1];
                }
            }
            break;
        }
        Bitmap dest = null;
        if (lip_mask_width > 0 && lip_mask_height > 0) {
            dest = Bitmap.createBitmap(lip_mask_width, lip_mask_height, Bitmap.Config.ARGB_8888);

            PocoNativeFilter.liveGetTeethMask(dest, lipPos_i);
        }

        lipMaskOriginal.x = (float) minX;
        lipMaskOriginal.y = (float) height - maxY;

        return dest;
    }
}
