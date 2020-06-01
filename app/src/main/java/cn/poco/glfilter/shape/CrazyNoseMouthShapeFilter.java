package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;

public class CrazyNoseMouthShapeFilter extends DefaultFilter {

    private PointF[] mFaceData = new PointF[114];

    private boolean mIsNose;

    private float mNoseRadius;
    private float mNoseStrength;
    private int mNoseIndexLength;
    private ByteBuffer mNoseDrawIndex;
    private FloatBuffer mNoseSrcBuffer, mNoseChangeBuffer;

    private float mMouthRadius;
    private float mMouthStrength;
    private int mMouthIndexLength;
    private ByteBuffer mMouthDrawIndex;
    private FloatBuffer mMouthSrcBuffer, mMouthChangeBuffer;

    private PocoFace mPocoFace;

    public CrazyNoseMouthShapeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadBasicProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexture");
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public void setFilterType(boolean isNose) {
        mIsNose = isNose;
    }

    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;
        mNoseRadius = shapeData.noseRadius;
        mNoseStrength = shapeData.noseStrength;
        mMouthRadius = shapeData.mouthRadius;
        mMouthStrength = shapeData.mouthStrength;

        if (mIsNose) {
            if (mNoseStrength < 0f) {
                mNoseStrength = 0f;
            } else if (mNoseStrength > 100.f) {
                mNoseStrength = 100.f;
            }
            mNoseStrength = (mNoseStrength * 0.2f) / 100.0f;

        } else {
            //范围0-100   50的时候无效果
            if (mMouthStrength < 0f) {
                mMouthStrength = 0f;
            } else if (mMouthStrength > 100.f) {
                mMouthStrength = 100.f;
            }
            mMouthStrength = ((mMouthStrength - 50.f) * 0.25f) / 100.0f;
        }
    }


    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (mPocoFace == null) {
            mPocoFace = FaceDataHelper.getInstance().changeFace(0).getFace();
        }
        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                mFaceData[i] = new PointF();
                mFaceData[i].x = mPocoFace.points_array[i].x;
                mFaceData[i].y = 1.0f - mPocoFace.points_array[i].y;
            }

            if (mIsNose) {
                noseShape(texStride);
            } else {
                mouthShape(texStride);
            }
        }
    }

    private void noseShape(int texStride) {
        if (mNoseDrawIndex == null) {
            short[] index = {
                    0, 1, 3,
                    1, 2, 3,
                    2, 3, 4,
                    4, 2, 7,
                    4, 7, 9,
                    4, 8, 9,
                    14, 4, 8,
                    14, 3, 4,
                    16, 12, 3,
                    16, 14, 3,
                    3, 12, 0,

                    0, 1, 5,
                    1, 2, 5,
                    2, 5, 6,
                    2, 6, 7,
                    6, 7, 10,
                    6, 11, 10,
                    15, 6, 11,
                    15, 5, 6,
                    17, 13, 5,
                    17, 15, 5,
                    5, 13, 0
            };
            mNoseIndexLength = index.length;
            mNoseDrawIndex = ByteBuffer.allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder());
            mNoseDrawIndex.asShortBuffer().put(index);
        }
        mNoseDrawIndex.position(0);

        float imageVertices1[] = getNose(mFaceData);
        if (mNoseSrcBuffer == null || mNoseSrcBuffer.capacity() != imageVertices1.length * 4) {
            ByteBuffer bb = ByteBuffer.allocateDirect(imageVertices1.length * 4);
            bb.order(ByteOrder.nativeOrder());
            mNoseSrcBuffer = bb.asFloatBuffer();
        }
        mNoseSrcBuffer.clear();
        mNoseSrcBuffer.put(imageVertices1);
        mNoseSrcBuffer.position(0);

        float imageVertices[] = getNoseOffset(mFaceData, mNoseStrength);
        for (int i = 0; i < imageVertices.length / 2; i++) {
            imageVertices[2 * i] = imageVertices[2 * i] * 2 - 1.f;
            imageVertices[2 * i + 1] = imageVertices[2 * i + 1] * 2 - 1.f;
        }
        if (mNoseChangeBuffer == null || mNoseChangeBuffer.capacity() != imageVertices.length * 4) {
            ByteBuffer bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            mNoseChangeBuffer = bb2.asFloatBuffer();
        }
        mNoseChangeBuffer.clear();
        mNoseChangeBuffer.put(imageVertices);
        mNoseChangeBuffer.position(0);

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, mNoseChangeBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, mNoseSrcBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mNoseIndexLength, GLES20.GL_UNSIGNED_SHORT, mNoseDrawIndex);
    }

    private void mouthShape(int texStride) {
        if (mMouthDrawIndex == null) {
            short[] index = {
                    0, 7, 8, 0, 1, 8, 1, 8, 9, 1, 2, 9, 2, 3, 9, 3, 9, 10, 3, 10, 11, 3, 11, 4, 4, 11, 12, 4, 12, 5, 5, 12, 6, 6, 12, 13,
                    12, 14, 13, 11, 14, 12, 11, 15, 14, 11, 16, 15, 10, 16, 11, 9, 16, 10, 9, 17, 16, 9, 18, 17, 8, 18, 9, 7, 18, 8,
                    38, 7, 0, 38, 19, 7, 19, 20, 7, 20, 21, 7, 21, 22, 7, 22, 23, 7, 23, 24, 7, 24, 18, 7, 24, 25, 18, 25, 26, 18, 26, 27, 18,
                    18, 27, 17, 17, 27, 16, 27, 28, 16, 28, 29, 16, 29, 30, 16, 16, 30, 15, 15, 30, 31, 15, 31, 14,
                    31, 32, 14, 32, 33, 14, 33, 34, 14, 34, 35, 14, 35, 13, 14, 35, 36, 13, 36, 37, 13, 37, 39, 13, 39, 6, 13
            };
            mMouthIndexLength = index.length;
            mMouthDrawIndex = ByteBuffer.allocateDirect(index.length * 2)
                    .order(ByteOrder.nativeOrder());
            mMouthDrawIndex.asShortBuffer().put(index);
        }
        mMouthDrawIndex.position(0);

        float imageVertices1[] = getMouth(mFaceData);
        if (mMouthSrcBuffer == null || mMouthSrcBuffer.capacity() != imageVertices1.length * 4) {
            ByteBuffer bb = ByteBuffer.allocateDirect(imageVertices1.length * 4);
            bb.order(ByteOrder.nativeOrder());
            mMouthSrcBuffer = bb.asFloatBuffer();
        }
        mMouthSrcBuffer.clear();
        mMouthSrcBuffer.put(imageVertices1);
        mMouthSrcBuffer.position(0);

        float imageVertices[] = getMouthOffset(mFaceData, mMouthStrength);
        for (int i = 0; i < imageVertices.length / 2; i++) {
            imageVertices[2 * i] = imageVertices[2 * i] * 2 - 1.f;
            imageVertices[2 * i + 1] = imageVertices[2 * i + 1] * 2 - 1.f;
        }
        if (mMouthChangeBuffer == null || mMouthChangeBuffer.capacity() != imageVertices.length * 4) {
            ByteBuffer bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            mMouthChangeBuffer = bb2.asFloatBuffer();
        }
        mMouthChangeBuffer.clear();
        mMouthChangeBuffer.put(imageVertices);
        mMouthChangeBuffer.position(0);

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, mMouthChangeBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, mMouthSrcBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, mMouthIndexLength, GLES20.GL_UNSIGNED_SHORT, mMouthDrawIndex);
    }

    private float[] getNose(PointF[] points) {
        int idx[] = {43, 45, 46, 80, 81, 82, 83, 49, 73, 76, 6, 7, 8, 9, 26, 25, 24, 23, 74, 77, 87, 84, 85, 89, 90};
        float ret[] = new float[18 * 2];

        PointF idxPoint[] = new PointF[idx.length];
        for (int i = 0; i < idx.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[idx[i]].x * mWidth;
            idxPoint[i].y = points[idx[i]].y * mHeight;
        }

        PointF _t_43_p = idxPoint[0];
        PointF _t_45_p = idxPoint[1];
        PointF _t_46_p = idxPoint[2];

        PointF _t_80_p = idxPoint[3];
        PointF _t_81_p = idxPoint[4];
        PointF _t_82_p = idxPoint[5];
        PointF _t_83_p = idxPoint[6];
        PointF _t_49_p = idxPoint[7];

        PointF _t_73_p = idxPoint[8];
        PointF _t_76_p = idxPoint[9];

        PointF _t_6_p = idxPoint[10];
        PointF _t_7_p = idxPoint[11];
        PointF _t_8_p = idxPoint[12];
        PointF _t_9_p = idxPoint[13];

        PointF _t_26_p = idxPoint[14];
        PointF _t_25_p = idxPoint[15];
        PointF _t_24_p = idxPoint[16];
        PointF _t_23_p = idxPoint[17];

        float _t_left_face_ref_p_x = (_t_6_p.x + _t_7_p.x + _t_8_p.x + _t_9_p.x) * 0.25f;
        float _t_left_face_ref_p_y = (_t_6_p.y + _t_7_p.y + _t_8_p.y + _t_9_p.y) * 0.25f;
        PointF _t_left_face_ref_p = new PointF(_t_left_face_ref_p_x, _t_left_face_ref_p_y);

        float _t_right_face_ref_p_x = (_t_26_p.x + _t_25_p.x + _t_24_p.x + _t_23_p.x) * 0.25f;
        float _t_right_face_ref_p_y = (_t_26_p.y + _t_25_p.y + _t_24_p.y + _t_23_p.y) * 0.25f;
        PointF _t_right_face_ref_p = new PointF(_t_right_face_ref_p_x, _t_right_face_ref_p_y);

        float _t_left_face_center_p_x = _t_73_p.x + (_t_left_face_ref_p.x - _t_73_p.x) * 0.7f;
        float _t_left_face_center_p_y = _t_73_p.y + (_t_left_face_ref_p.y - _t_73_p.y) * 0.7f;
        float _t_right_face_center_p_x = _t_76_p.x + (_t_right_face_ref_p.x - _t_76_p.x) * 0.7f;
        float _t_right_face_center_p_y = _t_76_p.y + (_t_right_face_ref_p.y - _t_76_p.y) * 0.7f;

        PointF _t_left_face_center_p = new PointF(_t_left_face_center_p_x, _t_left_face_center_p_y);
        PointF _t_right_face_center_p = new PointF(_t_right_face_center_p_x, _t_right_face_center_p_y);

        //---------------------------------------------     --------------------------------------------------------//
//        PointF _t_74_p = idxPoint[18];
//        PointF _t_77_p = idxPoint[19];
        PointF _t_52_p = new PointF(points[52].x * mWidth, points[52].y * mHeight);
        PointF _t_55_p = new PointF(points[55].x * mWidth, points[55].y * mHeight);
        PointF _t_72_p = new PointF(points[72].x * mWidth, points[72].y * mHeight);
        PointF _t_74_p = new PointF((_t_52_p.x+_t_55_p.x+_t_72_p.x+_t_73_p.x)*0.25f, (_t_52_p.y+_t_55_p.y+_t_72_p.y+_t_73_p.y)*0.25f);
        PointF _t_58_p = new PointF(points[58].x * mWidth, points[58].y * mHeight);
        PointF _t_61_p = new PointF(points[61].x * mWidth, points[61].y * mHeight);
        PointF _t_75_p = new PointF(points[75].x * mWidth, points[75].y * mHeight);
        PointF _t_77_p = new PointF((_t_58_p.x+_t_61_p.x+_t_75_p.x+_t_76_p.x)*0.25f, (_t_58_p.y+_t_61_p.y+_t_75_p.y+_t_76_p.y)*0.25f);

        PointF _t_87_p = idxPoint[20];
        float _t_left_face_center_x = _t_87_p.x + (_t_74_p.x - _t_77_p.x) * 0.7f;
        float _t_left_face_center_y = _t_87_p.y + (_t_74_p.y - _t_77_p.y) * 0.7f;
        float _t_right_face_center_x = _t_87_p.x + (_t_77_p.x - _t_74_p.x) * 0.7f;
        float _t_right_face_center_y = _t_87_p.y + (_t_77_p.y - _t_74_p.y) * 0.7f;

        float _t_left_face_cheek_offset_y = (_t_6_p.y - _t_87_p.y) * 0.5f;
        float _t_right_face_cheek_offset_y = (_t_26_p.y - _t_87_p.y) * 0.5f;

        _t_left_face_center_p = new PointF(_t_left_face_center_x, _t_left_face_center_y + _t_left_face_cheek_offset_y);
        _t_right_face_center_p = new PointF(_t_right_face_center_x, _t_right_face_center_y + _t_right_face_cheek_offset_y);

        PointF _t_offset_nose_ref_left_0 = new PointF((_t_80_p.x + _t_82_p.x) * 0.5f, (_t_80_p.y + _t_82_p.y) * 0.5f);
        PointF _t_offset_nose_ref_right_0 = new PointF((_t_81_p.x + _t_83_p.x) * 0.5f, (_t_81_p.y + _t_83_p.y) * 0.5f);

        PointF _t_offset_nose_ref_left_1 = new PointF(_t_49_p.x + (_t_offset_nose_ref_left_0.x - _t_46_p.x), _t_49_p.y + (_t_offset_nose_ref_left_0.y - _t_46_p.y));
        PointF _t_offset_nose_ref_right_1 = new PointF(_t_49_p.x + (_t_offset_nose_ref_right_0.x - _t_46_p.x), _t_49_p.y + (_t_offset_nose_ref_right_0.y - _t_46_p.y));

        float _t_offset_nose_left_0_x = _t_46_p.x + (_t_offset_nose_ref_left_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_left_0_y = _t_46_p.y + (_t_offset_nose_ref_left_0.y - _t_46_p.y) * 1.0f;
        float _t_offset_nose_right_0_x = _t_46_p.x + (_t_offset_nose_ref_right_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_right_0_y = _t_46_p.y + (_t_offset_nose_ref_right_0.y - _t_46_p.y) * 1.0f;

        float _t_offset_nose_left_1_x = _t_49_p.x + (_t_offset_nose_ref_left_1.x - _t_49_p.x) * 1.0f;
        float _t_offset_nose_left_1_y = _t_49_p.y + (_t_offset_nose_ref_left_1.y - _t_49_p.y) * 1.0f;
        float _t_offset_nose_right_1_x = _t_49_p.x + (_t_offset_nose_ref_right_1.x - _t_49_p.x) * 1.0f;
        float _t_offset_nose_right_1_y = _t_49_p.y + (_t_offset_nose_ref_right_1.y - _t_49_p.y) * 1.0f;

        PointF _t_84_p = idxPoint[21];
        PointF _t_85_p = idxPoint[22];
        PointF _t_89_p = idxPoint[23];
        PointF _t_90_p = idxPoint[24];

        float offset_left_eyelid_p_x = _t_73_p.x + (_t_left_face_center_p.x - _t_73_p.x) * 0.1f;
        float offset_left_eyelid_p_y = _t_73_p.y + (_t_left_face_center_p.y - _t_73_p.y) * 0.1f;
        float offset_right_eyelid_p_x = _t_76_p.x + (_t_right_face_center_p.x - _t_76_p.x) * 0.1f;
        float offset_right_eyelid_p_y = _t_76_p.y + (_t_right_face_center_p.y - _t_76_p.y) * 0.1f;

        ret[0] = _t_43_p.x;
        ret[1] = _t_43_p.y;
        ret[2] = _t_45_p.x;
        ret[3] = _t_45_p.y;
        ret[4] = _t_46_p.x;
        ret[5] = _t_46_p.y;

        ret[6] = _t_offset_nose_left_0_x;
        ret[7] = _t_offset_nose_left_0_y;
        ret[8] = _t_offset_nose_left_1_x;
        ret[9] = _t_offset_nose_left_1_y;
        ret[10] = _t_offset_nose_right_0_x;
        ret[11] = _t_offset_nose_right_0_y;
        ret[12] = _t_offset_nose_right_1_x;
        ret[13] = _t_offset_nose_right_1_y;

        ret[14] = _t_49_p.x;
        ret[15] = _t_49_p.y;

        ret[16] = _t_84_p.x;
        ret[17] = _t_84_p.y;
        ret[18] = _t_85_p.x;
        ret[19] = _t_85_p.y;
        ret[20] = _t_89_p.x;
        ret[21] = _t_89_p.y;
        ret[22] = _t_90_p.x;
        ret[23] = _t_90_p.y;

        ret[24] = offset_left_eyelid_p_x;
        ret[25] = offset_left_eyelid_p_y;
        ret[26] = offset_right_eyelid_p_x;
        ret[27] = offset_right_eyelid_p_y;

        ret[28] = _t_left_face_center_p.x;
        ret[29] = _t_left_face_center_p.y;
        ret[30] = _t_right_face_center_p.x;
        ret[31] = _t_right_face_center_p.y;

        PointF face_polar_left = new PointF(_t_74_p.x + (_t_74_p.x - _t_77_p.x) * 0.7f, _t_74_p.y + (_t_74_p.y - _t_77_p.y) * 0.7f);
        PointF face_polar_right = new PointF(_t_77_p.x + (_t_77_p.x - _t_74_p.x) * 0.7f, _t_77_p.y + (_t_77_p.y - _t_74_p.y) * 0.7f);
        ret[32] = face_polar_left.x;
        ret[33] = face_polar_left.y;
        ret[34] = face_polar_right.x;
        ret[35] = face_polar_right.y;


        for (int i = 0; i < 18; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }

    private float[] getNoseOffset(PointF[] points, float mNoseStrength) {
        int idx[] = {43, 45, 46, 80, 81, 82, 83, 49, 73, 76, 6, 7, 8, 9, 26, 25, 24, 23, 74, 77, 87, 84, 85, 89, 90};
        float ret[] = new float[18 * 2];

        PointF idxPoint[] = new PointF[idx.length];
        for (int i = 0; i < idx.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[idx[i]].x * mWidth;
            idxPoint[i].y = points[idx[i]].y * mHeight;
        }

        PointF _t_43_p = idxPoint[0];
        PointF _t_45_p = idxPoint[1];
        PointF _t_46_p = idxPoint[2];

        PointF _t_80_p = idxPoint[3];
        PointF _t_81_p = idxPoint[4];
        PointF _t_82_p = idxPoint[5];
        PointF _t_83_p = idxPoint[6];
        PointF _t_49_p = idxPoint[7];

        PointF _t_73_p = idxPoint[8];
        PointF _t_76_p = idxPoint[9];

        PointF _t_6_p = idxPoint[10];
        PointF _t_7_p = idxPoint[11];
        PointF _t_8_p = idxPoint[12];
        PointF _t_9_p = idxPoint[13];

        PointF _t_26_p = idxPoint[14];
        PointF _t_25_p = idxPoint[15];
        PointF _t_24_p = idxPoint[16];
        PointF _t_23_p = idxPoint[17];

        float _t_left_face_ref_p_x = (_t_6_p.x + _t_7_p.x + _t_8_p.x + _t_9_p.x) * 0.25f;
        float _t_left_face_ref_p_y = (_t_6_p.y + _t_7_p.y + _t_8_p.y + _t_9_p.y) * 0.25f;
        PointF _t_left_face_ref_p = new PointF(_t_left_face_ref_p_x, _t_left_face_ref_p_y);

        float _t_right_face_ref_p_x = (_t_26_p.x + _t_25_p.x + _t_24_p.x + _t_23_p.x) * 0.25f;
        float _t_right_face_ref_p_y = (_t_26_p.y + _t_25_p.y + _t_24_p.y + _t_23_p.y) * 0.25f;
        PointF _t_right_face_ref_p = new PointF(_t_right_face_ref_p_x, _t_right_face_ref_p_y);

        float _t_left_face_center_p_x = _t_73_p.x + (_t_left_face_ref_p.x - _t_73_p.x) * 0.7f;
        float _t_left_face_center_p_y = _t_73_p.y + (_t_left_face_ref_p.y - _t_73_p.y) * 0.7f;
        float _t_right_face_center_p_x = _t_76_p.x + (_t_right_face_ref_p.x - _t_76_p.x) * 0.7f;
        float _t_right_face_center_p_y = _t_76_p.y + (_t_right_face_ref_p.y - _t_76_p.y) * 0.7f;

        PointF _t_left_face_center_p = new PointF(_t_left_face_center_p_x, _t_left_face_center_p_y);
        PointF _t_right_face_center_p = new PointF(_t_right_face_center_p_x, _t_right_face_center_p_y);

        //---------------------------------------------     --------------------------------------------------------//
//        PointF _t_74_p = idxPoint[18];
//        PointF _t_77_p = idxPoint[19];
        PointF _t_52_p = new PointF(points[52].x * mWidth, points[52].y * mHeight);
        PointF _t_55_p = new PointF(points[55].x * mWidth, points[55].y * mHeight);
        PointF _t_72_p = new PointF(points[72].x * mWidth, points[72].y * mHeight);
        PointF _t_74_p = new PointF((_t_52_p.x+_t_55_p.x+_t_72_p.x+_t_73_p.x)*0.25f, (_t_52_p.y+_t_55_p.y+_t_72_p.y+_t_73_p.y)*0.25f);
        PointF _t_58_p = new PointF(points[58].x * mWidth, points[58].y * mHeight);
        PointF _t_61_p = new PointF(points[61].x * mWidth, points[61].y * mHeight);
        PointF _t_75_p = new PointF(points[75].x * mWidth, points[75].y * mHeight);
        PointF _t_77_p = new PointF((_t_58_p.x+_t_61_p.x+_t_75_p.x+_t_76_p.x)*0.25f, (_t_58_p.y+_t_61_p.y+_t_75_p.y+_t_76_p.y)*0.25f);

        PointF _t_87_p = idxPoint[20];
        float _t_left_face_center_x = _t_87_p.x + (_t_74_p.x - _t_77_p.x) * 0.7f;
        float _t_left_face_center_y = _t_87_p.y + (_t_74_p.y - _t_77_p.y) * 0.7f;
        float _t_right_face_center_x = _t_87_p.x + (_t_77_p.x - _t_74_p.x) * 0.7f;
        float _t_right_face_center_y = _t_87_p.y + (_t_77_p.y - _t_74_p.y) * 0.7f;

        float _t_left_face_cheek_offset_y = (_t_6_p.y - _t_87_p.y) * 0.5f;
        float _t_right_face_cheek_offset_y = (_t_26_p.y - _t_87_p.y) * 0.5f;

        _t_left_face_center_p = new PointF(_t_left_face_center_x, _t_left_face_center_y + _t_left_face_cheek_offset_y);
        _t_right_face_center_p = new PointF(_t_right_face_center_x, _t_right_face_center_y + _t_right_face_cheek_offset_y);

        PointF _t_offset_nose_ref_left_0 = new PointF((_t_80_p.x + _t_82_p.x) * 0.5f, (_t_80_p.y + _t_82_p.y) * 0.5f);
        PointF _t_offset_nose_ref_right_0 = new PointF((_t_81_p.x + _t_83_p.x) * 0.5f, (_t_81_p.y + _t_83_p.y) * 0.5f);

        PointF _t_offset_nose_ref_left_1 = new PointF(_t_49_p.x + (_t_offset_nose_ref_left_0.x - _t_46_p.x), _t_49_p.y + (_t_offset_nose_ref_left_0.y - _t_46_p.y));
        PointF _t_offset_nose_ref_right_1 = new PointF(_t_49_p.x + (_t_offset_nose_ref_right_0.x - _t_46_p.x), _t_49_p.y + (_t_offset_nose_ref_right_0.y - _t_46_p.y));

        float _t_offset_nose_left_0_x = _t_46_p.x + (_t_offset_nose_ref_left_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_left_0_y = _t_46_p.y + (_t_offset_nose_ref_left_0.y - _t_46_p.y) * 1.0f;
        float _t_offset_nose_right_0_x = _t_46_p.x + (_t_offset_nose_ref_right_0.x - _t_46_p.x) * 1.0f;
        float _t_offset_nose_right_0_y = _t_46_p.y + (_t_offset_nose_ref_right_0.y - _t_46_p.y) * 1.0f;

        float _t_offset_nose_left_1_x = _t_49_p.x + (_t_offset_nose_ref_left_1.x - _t_49_p.x) * 1.0f;
        float _t_offset_nose_left_1_y = _t_49_p.y + (_t_offset_nose_ref_left_1.y - _t_49_p.y) * 1.0f;
        float _t_offset_nose_right_1_x = _t_49_p.x + (_t_offset_nose_ref_right_1.x - _t_49_p.x) * 1.0f;
        float _t_offset_nose_right_1_y = _t_49_p.y + (_t_offset_nose_ref_right_1.y - _t_49_p.y) * 1.0f;

        float _t_offset_adjust_nose_left_0_x = _t_offset_nose_left_0_x * (1.0f - mNoseStrength) + _t_46_p.x * mNoseStrength;
        float _t_offset_adjust_nose_left_0_y = _t_offset_nose_left_0_y * (1.0f - mNoseStrength) + _t_46_p.y * mNoseStrength;
        float _t_offset_adjust_nose_right_0_x = _t_offset_nose_right_0_x * (1.0f - mNoseStrength) + _t_46_p.x * mNoseStrength;
        float _t_offset_adjust_nose_right_0_y = _t_offset_nose_right_0_y * (1.0f - mNoseStrength) + _t_46_p.y * mNoseStrength;

        float _t_offset_adjust_nose_left_1_x = _t_offset_nose_left_1_x * (1.0f - mNoseStrength) + _t_49_p.x * mNoseStrength;
        float _t_offset_adjust_nose_left_1_y = _t_offset_nose_left_1_y * (1.0f - mNoseStrength) + _t_49_p.y * mNoseStrength;
        float _t_offset_adjust_nose_right_1_x = _t_offset_nose_right_1_x * (1.0f - mNoseStrength) + _t_49_p.x * mNoseStrength;
        float _t_offset_adjust_nose_right_1_y = _t_offset_nose_right_1_y * (1.0f - mNoseStrength) + _t_49_p.y * mNoseStrength;

        PointF _t_84_p = idxPoint[21];
        PointF _t_85_p = idxPoint[22];
        PointF _t_89_p = idxPoint[23];
        PointF _t_90_p = idxPoint[24];

        float offset_left_eyelid_p_x = _t_73_p.x + (_t_left_face_center_p.x - _t_73_p.x) * 0.1f;
        float offset_left_eyelid_p_y = _t_73_p.y + (_t_left_face_center_p.y - _t_73_p.y) * 0.1f;
        float offset_right_eyelid_p_x = _t_76_p.x + (_t_right_face_center_p.x - _t_76_p.x) * 0.1f;
        float offset_right_eyelid_p_y = _t_76_p.y + (_t_right_face_center_p.y - _t_76_p.y) * 0.1f;


        ret[0] = _t_43_p.x;
        ret[1] = _t_43_p.y;
        ret[2] = _t_45_p.x;
        ret[3] = _t_45_p.y;
        ret[4] = _t_46_p.x;
        ret[5] = _t_46_p.y;

        ret[6] = _t_offset_adjust_nose_left_0_x;
        ret[7] = _t_offset_adjust_nose_left_0_y;
        ret[8] = _t_offset_adjust_nose_left_1_x;
        ret[9] = _t_offset_adjust_nose_left_1_y;
        ret[10] = _t_offset_adjust_nose_right_0_x;
        ret[11] = _t_offset_adjust_nose_right_0_y;
        ret[12] = _t_offset_adjust_nose_right_1_x;
        ret[13] = _t_offset_adjust_nose_right_1_y;

        ret[14] = _t_49_p.x;
        ret[15] = _t_49_p.y;

        ret[16] = _t_84_p.x;
        ret[17] = _t_84_p.y;
        ret[18] = _t_85_p.x;
        ret[19] = _t_85_p.y;
        ret[20] = _t_89_p.x;
        ret[21] = _t_89_p.y;
        ret[22] = _t_90_p.x;
        ret[23] = _t_90_p.y;

        ret[24] = offset_left_eyelid_p_x;
        ret[25] = offset_left_eyelid_p_y;
        ret[26] = offset_right_eyelid_p_x;
        ret[27] = offset_right_eyelid_p_y;

        ret[28] = _t_left_face_center_p.x;
        ret[29] = _t_left_face_center_p.y;
        ret[30] = _t_right_face_center_p.x;
        ret[31] = _t_right_face_center_p.y;

        PointF face_polar_left = new PointF(_t_74_p.x + (_t_74_p.x - _t_77_p.x) * 0.7f, _t_74_p.y + (_t_74_p.y - _t_77_p.y) * 0.7f);
        PointF face_polar_right = new PointF(_t_77_p.x + (_t_77_p.x - _t_74_p.x) * 0.7f, _t_77_p.y + (_t_77_p.y - _t_74_p.y) * 0.7f);

        ret[32] = face_polar_left.x;
        ret[33] = face_polar_left.y;
        ret[34] = face_polar_right.x;
        ret[35] = face_polar_right.y;

        for (int i = 0; i < 18; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }


    private float[] getMouth(PointF[] points) {
        int idx[] = {82, 47, 48, 49, 50, 51, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
        float ret[] = new float[(idx.length + 2) * 2];

        for (int i = 0; i < idx.length; i++) {
            ret[2 * i] = points[idx[i]].x;
            ret[2 * i + 1] = points[idx[i]].y;
        }

        ret[idx.length * 2] = (points[4].x + points[84].x) / 2;
        ret[idx.length * 2 + 1] = (points[4].y + points[84].y) / 2;
        ret[idx.length * 2 + 2] = (points[29].x + points[90].x) / 2;
        ret[idx.length * 2 + 3] = (points[29].y + points[90].y) / 2;

        return ret;
    }

    private float[] getMouthOffset(PointF[] points, float scale) {
        int idx[] = {82, 47, 48, 49, 50, 51, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25};
        float ret[] = new float[(idx.length + 2) * 2];

        PointF realPoint[] = new PointF[points.length];
        for (int i = 0; i < points.length; i++) {
            realPoint[i] = new PointF();
            realPoint[i].x = (int) (points[i].x * mWidth);
            realPoint[i].y = (int) (points[i].y * mHeight);
        }

        PointF mouthCenter = new PointF((realPoint[Sslandmarks.BotOfTopLip].x + realPoint[Sslandmarks.TopOfBotLip].x) / 2,
                (realPoint[Sslandmarks.BotOfTopLip].y + realPoint[Sslandmarks.TopOfBotLip].y) / 2);

        scale = 1.25f * scale;  //限制缩小区域

        for (int i = 84; i <= 95; i++) {
            realPoint[i].x = (realPoint[i].x * (1 - scale) + mouthCenter.x * scale);
            realPoint[i].y = (realPoint[i].y * (1 - scale) + mouthCenter.y * scale);
        }

        for (int i = 0; i < idx.length; i++) {
            ret[2 * i] = realPoint[idx[i]].x / mWidth;
            ret[2 * i + 1] = realPoint[idx[i]].y / mHeight;
        }

        ret[idx.length * 2] = (points[4].x + points[84].x) / 2;
        ret[idx.length * 2 + 1] = (points[4].y + points[84].y) / 2;
        ret[idx.length * 2 + 2] = (points[29].x + points[90].x) / 2;
        ret[idx.length * 2 + 3] = (points[29].y + points[90].y) / 2;

        return ret;
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindTexture() {
        super.unbindTexture();
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        mPocoFace = null;

        mNoseDrawIndex = null;
        mNoseSrcBuffer = null;
        mNoseChangeBuffer = null;

        mMouthDrawIndex = null;
        mMouthSrcBuffer = null;
        mMouthChangeBuffer = null;
    }
}
