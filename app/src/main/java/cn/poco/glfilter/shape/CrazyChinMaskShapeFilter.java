package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;
import cn.poco.pgles.PGLNativeIpl;

public class CrazyChinMaskShapeFilter extends DefaultFilter {

    private FloatBuffer mSrcBuffer, mChangeBuffer;
    private int mIndexLength;
    private ByteBuffer drawIndex;
    private PointF[] faceData = new PointF[114];

    private float mChinRadius = 1.0f;  //调节力度 -1~1之间
    private float mChinStrength = 0.1f;

    private PocoFace mPocoFace;

    public CrazyChinMaskShapeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vert_chin, R.raw.frag_chin);
        return PGLNativeIpl.loadChinMaskProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;
        this.mChinRadius = shapeData.chinRadius;
        this.mChinStrength = shapeData.chinStrength;

        if (mChinRadius < 0.0f) {
            mChinRadius = 0.0f;
        } else if (mChinRadius > 100.0f) {
            mChinRadius = 100.0f;
        }
        mChinRadius = mChinRadius / 100.f;

        if (mChinStrength < 0.0f) {
            mChinStrength = 0.0f;
        } else if (mChinStrength > 100.0f) {
            mChinStrength = 100.0f;
        }
        mChinStrength = (mChinStrength - 50.0f) * 0.2f / 100.0f;
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
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (mPocoFace != null && mPocoFace.points_count > 0) {
            for (int i = 0; i < mPocoFace.points_count; i++) {
                faceData[i] = new PointF();
                faceData[i].x = mPocoFace.points_array[i].x;
                faceData[i].y = 1.0f - mPocoFace.points_array[i].y;
            }

            if (drawIndex == null) {
                short[] index = {
                        9, 10, 106,
                        10, 106, 107,
                        10, 11, 107,
                        11, 107, 108,
                        11, 12, 108,
                        12, 108, 109,
                        12, 13, 109,
                        13, 109, 110,
                        13, 14, 110,
                        14, 110, 111,
                        14, 15, 111,
                        15, 111, 112,
                        15, 16, 112,
                        16, 112, 113,
                        16, 17, 113,
                        17, 113, 114,
                        17, 18, 114,
                        18, 114, 115,
                        18, 19, 115,
                        19, 115, 116,
                        19, 20, 116,
                        20, 116, 117,
                        20, 21, 117,
                        21, 117, 118,
                        21, 22, 118,
                        22, 118, 119,
                        22, 23, 119,
                        23, 119, 120,

                        9, 10, 84,
                        84, 10, 11,
                        84, 95, 11,
                        11, 12, 95,
                        12, 13, 95,
                        95, 94, 13,
                        94, 13, 14,
                        94, 14, 15,

                        23, 22, 90,
                        90, 22, 21,
                        90, 91, 21,
                        21, 20, 91,
                        20, 19, 91,
                        91, 92, 19,
                        92, 19, 18,
                        92, 18, 17,

                        94, 93, 15,
                        15, 16, 93,
                        93, 16, 17,
                        93, 92, 17
                };
                mIndexLength = index.length;
                drawIndex = ByteBuffer.allocateDirect(index.length * 2)
                        .order(ByteOrder.nativeOrder());
                drawIndex.asShortBuffer().put(index);
            }
            drawIndex.position(0);

            float imageVertices1[] = getChinPoint(faceData, mChinRadius);
            if (mSrcBuffer == null || mSrcBuffer.capacity() != imageVertices1.length * 4) {
                ByteBuffer bb = ByteBuffer.allocateDirect(imageVertices1.length * 4);
                bb.order(ByteOrder.nativeOrder());
                mSrcBuffer = bb.asFloatBuffer();
            }
            mSrcBuffer.clear();
            mSrcBuffer.put(imageVertices1);
            mSrcBuffer.position(0);

            float[] imageVertices = getChinOffset(faceData, mChinRadius, mChinStrength);
            for (int i = 0; i < imageVertices.length / 2; i++) {
                imageVertices[2 * i] = imageVertices[2 * i] * 2 - 1.f;
                imageVertices[2 * i + 1] = imageVertices[2 * i + 1] * 2 - 1.f;
            }
            if (mChangeBuffer == null || mChangeBuffer.capacity() != imageVertices.length * 4) {
                ByteBuffer bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                bb2.order(ByteOrder.nativeOrder());
                mChangeBuffer = bb2.asFloatBuffer();
            }
            mChangeBuffer.clear();
            mChangeBuffer.put(imageVertices);
            mChangeBuffer.position(0);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, mChangeBuffer);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, mSrcBuffer);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexLength, GLES20.GL_UNSIGNED_SHORT, drawIndex);
        }

        mPocoFace = null;
    }

    private float[] getChinPoint(PointF[] points, float chinRadius) {
        float ret[] = new float[(106 + 15) * 2];

        PointF idxPoint[] = new PointF[106 + 15];
        for (int i = 0; i < 106; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[i].x * mWidth;
            idxPoint[i].y = points[i].y * mHeight;
        }

        PointF _t_98_p = idxPoint[98];
        PointF _t_102_p = idxPoint[102];
        PointF mouthCenter = new PointF((_t_98_p.x + _t_102_p.x) * 0.5f, (_t_98_p.y + _t_102_p.y) * 0.5f);

        PointF _t_offset_84_p = offsetPointF(mouthCenter, idxPoint[84], 1.1f);
        idxPoint[84].x = _t_offset_84_p.x;
        idxPoint[84].y = _t_offset_84_p.y;
        PointF _t_offset_90_p = offsetPointF(mouthCenter, idxPoint[90], 1.1f);
        idxPoint[90].x = _t_offset_90_p.x;
        idxPoint[90].y = _t_offset_90_p.y;
        for (int i = 91; i <= 95; i++) {
            PointF _t_offset_p = offsetPointF(mouthCenter, idxPoint[i], 1.1f);
            idxPoint[i].x = _t_offset_p.x;
            idxPoint[i].y = _t_offset_p.y;
        }

        //insert effect range
        PointF face_center = idxPoint[46];

        PointF _t_p = new PointF();
        for (int i = 9, j = 0; i <= 23; i++, j++) {
            _t_p = offsetPointF(face_center, idxPoint[i], chinRadius + 1.1f);

            idxPoint[106 + j] = new PointF();
            idxPoint[106 + j].x = _t_p.x;
            idxPoint[106 + j].y = _t_p.y;
        }

        for (int i = 0; i < 106 + 15; i++) {
            ret[i * 2] = idxPoint[i].x / mWidth;
            ret[i * 2 + 1] = idxPoint[i].y / mHeight;
        }
        return ret;
    }

    private float[] getChinOffset(PointF[] points, float chinRadius, float chinStrength) {
        float ret[] = new float[(106 + 15) * 2];

        PointF idxPoint[] = new PointF[106 + 15];
        for (int i = 0; i < 106; i++) {
            idxPoint[i] = new PointF();
            ret[2 * i] = idxPoint[i].x = points[i].x * mWidth;
            ret[2 * i + 1] = idxPoint[i].y = points[i].y * mHeight;
        }

        PointF _t_98_p = idxPoint[98];
        PointF _t_102_p = idxPoint[102];
        PointF mouthCenter = new PointF((_t_98_p.x + _t_102_p.x) * 0.5f, (_t_98_p.y + _t_102_p.y) * 0.5f);

        PointF _t_offset_84_p = offsetPointF(mouthCenter, idxPoint[84], 1.1f);
        ret[2 * 84] = _t_offset_84_p.x;
        ret[2 * 84 + 1] = _t_offset_84_p.y;
        PointF _t_offset_90_p = offsetPointF(mouthCenter, idxPoint[90], 1.1f);
        ret[2 * 90] = _t_offset_90_p.x;
        ret[2 * 90 + 1] = _t_offset_90_p.y;
        for (int i = 91; i <= 95; i++) {
            PointF _t_offset_p = offsetPointF(mouthCenter, idxPoint[i], 1.1f);
            ret[2 * i] = _t_offset_p.x;
            ret[2 * i + 1] = _t_offset_p.y;
        }

        //insert effect range
        PointF face_center = idxPoint[46];

        PointF _t_p = new PointF();
        for (int i = 9, j = 0; i <= 23; i++, j++) {
            _t_p = offsetPointF(face_center, idxPoint[i], chinRadius + 1.1f);

            ret[(106 + j) * 2] = _t_p.x;
            ret[(106 + j) * 2 + 1] = _t_p.y;
        }

        PointF _t_p_15 = idxPoint[15];
        PointF _t_p_16 = idxPoint[16];
        PointF _t_p_17 = idxPoint[17];
        PointF _t_p_86 = idxPoint[86];
        PointF _t_p_87 = idxPoint[87];
        PointF _t_p_88 = idxPoint[88];

        PointF chin_ref_p = new PointF((_t_p_15.x + _t_p_16.x + _t_p_17.x) / 3.0f, (_t_p_15.y + _t_p_16.y + _t_p_17.y) / 3.0f);
        PointF mouth_ref_p = new PointF((_t_p_86.x + _t_p_87.x + _t_p_88.x) / 3.0f, (_t_p_86.y + _t_p_87.y + _t_p_88.y) / 3.0f);

        PointF offset_vector = new PointF(chin_ref_p.x - mouth_ref_p.x, chin_ref_p.y - mouth_ref_p.y);

        PointF _t_p_9 = idxPoint[9];
        PointF _t_p_23 = idxPoint[23];
        float parabolic_reflect_x = distanceOfPoint(_t_p_9, chin_ref_p);

        //y=-x^4+1.0
        float _t_parabolic_array[] = new float[15];
        float _t_p_parabolic_v = 0;
        for (int i = 9, j = 0; i <= 15; i++, j++) {
            _t_p_parabolic_v = distanceOfPoint(idxPoint[i], chin_ref_p) / parabolic_reflect_x;
            _t_parabolic_array[j] = -(float) (Math.pow(_t_p_parabolic_v, 2.0)) + 1.0f;
        }
        _t_parabolic_array[7] = 1.0f;

        parabolic_reflect_x = distanceOfPoint(_t_p_23, chin_ref_p);
        for (int i = 17, j = 8; i <= 23; i++, j++) {
            _t_p_parabolic_v = distanceOfPoint(idxPoint[i], chin_ref_p) / parabolic_reflect_x;
            _t_parabolic_array[j] = -(float) (Math.pow(_t_p_parabolic_v, 2.0)) + 1.0f;
        }

        float _t_offset_v = 0;
        float _t_offst_p_x = 0;
        float _t_offst_p_y = 0;
        for (int i = 9, j = 0; i <= 23; i++, j++) {
            int _t_index_x = i * 2;
            int _t_index_y = _t_index_x + 1;

            _t_offset_v = chinStrength * _t_parabolic_array[j];
            _t_p = idxPoint[i];

            _t_offst_p_x = _t_p.x + offset_vector.x * _t_offset_v;
            _t_offst_p_y = _t_p.y + offset_vector.y * _t_offset_v;

            ret[_t_index_x] = _t_offst_p_x;
            ret[_t_index_y] = _t_offst_p_y;
        }

        for (int i = 0; i < 106 + 15; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }
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
        drawIndex = null;
        mSrcBuffer = null;
        mChangeBuffer = null;
    }

    private PointF offsetPointF(PointF src, PointF target, float percent) {
        return new PointF(src.x + (target.x - src.x) * percent, src.y + (target.y - src.y) * percent);
    }

    private float distanceOfPoint(PointF p1, PointF p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }
}
