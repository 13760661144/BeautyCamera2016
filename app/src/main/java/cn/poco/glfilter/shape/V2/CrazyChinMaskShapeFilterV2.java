package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.glfilter.shape.ShapeInfoData;
import cn.poco.image.PocoFace;
import cn.poco.pgles.PGLNativeIpl;

/**
 * 下巴变形V2版，创建下巴变形蒙版 +额头变形
 */
public class CrazyChinMaskShapeFilterV2 extends DefaultFilter {

    private FloatBuffer mSrcBuffer, mChangeBuffer;
    private int mIndexLength;
    private ByteBuffer drawIndex;
    private PointF[] faceData = new PointF[114];

    private float mChinRadius = 1.0f;  //调节力度 -1~1之间
    private float mChinStrength = 0.1f;

    private float mHeadStrength = 0f;
    private PocoFace mPocoFace;

    private int faceNum = 0;

    private GLFramebuffer mFaceBuffer;
    private int shapeOffstCombineProgram;
    private int offsetCombineInputTextureUniform;
    private int offsetCombineFaceCountUniform;

    private int maPositionLoc1;
    private int maTextureCoordLoc1;

    public CrazyChinMaskShapeFilterV2(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vert_chin, R.raw.frag_chin);
//        shapeOffstCombineProgram = GlUtil.createProgram(context, R.raw.vert_combine, R.raw.frag_combine);
        shapeOffstCombineProgram = PGLNativeIpl.loadChinCombineProgram();
        return PGLNativeIpl.loadChinMaskProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");

        maPositionLoc1 = GLES20.glGetAttribLocation(shapeOffstCombineProgram, "position");
        maTextureCoordLoc1 = GLES20.glGetAttribLocation(shapeOffstCombineProgram, "inputTextureCoordinate");

        offsetCombineInputTextureUniform = GLES20.glGetUniformLocation(shapeOffstCombineProgram, "inputImageTextures");
        offsetCombineFaceCountUniform = GLES20.glGetUniformLocation(shapeOffstCombineProgram, "face_count");
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (width == 0 || height == 0) {
            return;
        }
        if (mFaceBuffer != null) {
            mFaceBuffer.destroy();
            mFaceBuffer = null;
        }
        if (mFaceBuffer == null) {
            mFaceBuffer = new GLFramebuffer(6, mWidth / 2, mHeight / 2);
        }
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
        mChinStrength = (mChinStrength - 50.0f) * 0.35f / 100.0f;

        float faceScale = shapeData.headStrength / 100f;   //for test

        mHeadStrength = (faceScale - 0.5f) * 0.16f;

        if (mHeadStrength < 0.0f) {
            mHeadStrength *= 1.2f;
        }
    }

    public int getMaskTextureId() {
        if (mFaceBuffer != null) {
            return mFaceBuffer.getTextureIdByIndex(5);
        }
        return 0;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        ArrayList<PocoFace> faces = FaceDataHelper.getInstance().getFaceList();

        if (faces != null && !faces.isEmpty()) {
            GLES20.glViewport(0, 0, mWidth / 2, mHeight / 2);

            useProgram();
            bindTexture(textureId);

            faceNum = faces.size();
            for (int i = 0; i < faceNum; i++) {
                if (i == 0) {
                    mFaceBuffer.bindByIndex(0, true);
                } else {
                    mFaceBuffer.bindNext(true);
                }
                mPocoFace = faces.get(i);
                bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
            }

            unbindGLSLValues();
            unbindTexture();
            disuseProgram();

            //##############################
            //############## Mask 合并 ################

            mFaceBuffer.bindByIndex(5, true);

            GLES20.glUseProgram(shapeOffstCombineProgram);

            for (int i = 0; i < faceNum; i++) {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + i);
                GLES20.glBindTexture(getTextureTarget(), mFaceBuffer.getTextureIdByIndex(i));
                GLES20.glUniform1i(offsetCombineInputTextureUniform + i, 1 + i);
            }

            GLES20.glUniform1i(offsetCombineFaceCountUniform, faceNum);

            GLES20.glEnableVertexAttribArray(maPositionLoc1);
            GLES20.glVertexAttribPointer(maPositionLoc1, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
            GLES20.glEnableVertexAttribArray(maTextureCoordLoc1);
            GLES20.glVertexAttribPointer(maTextureCoordLoc1, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glDisableVertexAttribArray(maPositionLoc1);
            GLES20.glDisableVertexAttribArray(maTextureCoordLoc1);
            GLES20.glUseProgram(0);

            GLES20.glViewport(0, 0, mWidth, mHeight);
        }
    }

    @Override
    protected void bindTexture(int textureId) {
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
//        GLES20.glEnableVertexAttribArray(maPositionLoc);
//        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
//        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
//        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glClearColor(0.5f + 0.5f / 256.0f, 0.5f + 0.5f / 256.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        /*if (mPocoFace == null) {
            StickerDrawHelper.getInstance().getFaceSize();
            StickerDrawHelper.getInstance().changeFace(0);
            mPocoFace = StickerDrawHelper.getInstance().mOriPocoFace;
        }*/
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
                        93, 92, 17,

                        121,128,124,128,131,124,131,125,124,131,130,125,130,123,125,130,127,123,
                        130,132,127,132,126,127,132,129,126,129,122,126,133,128,121,133,134,
                        128,134,135,128,135,131,128,135,130,131,135,132,130,135,129,
                        132,135,136,129,136,137,129,137,122,129,

                        //补充连接额头与下巴
                        133,121,138, 133,138,106, 138,9,106, 138,9,84,
                        137,139,120,
                        139,23,120, 139,23,90, 139,122,137,

                        138,140,84, 139,140,90, 140,122,126, 140,122,139,
                        140,84,95, 140,95,94, 140,94,93, 140,93,92, 140,92,91, 140,91,90,
                        140,138,121, 140,121,124, 140,124,125, 140,125,123, 140,123,127,
                        140,127,126
                };
                mIndexLength = index.length;
                drawIndex = ByteBuffer.allocateDirect(index.length * 2)
                        .order(ByteOrder.nativeOrder());
                drawIndex.asShortBuffer().put(index);
            }
            drawIndex.position(0);

            PointF _t_5 = faceData[5];
            PointF _t_27 = faceData[27];
            PointF _sim_face_center = new PointF((_t_5.x + _t_27.x) * 0.5f,
                    (_t_5.y + _t_27.y) * 0.5f);
            float build_in_face_protect_scale = 1.3f;
            PointF _offset_5_p = offsetPointF(_sim_face_center, _t_5, build_in_face_protect_scale);
            PointF _offset_27_p = offsetPointF(_sim_face_center, _t_27, build_in_face_protect_scale);


            float dd[] = getChinPoint(faceData, mChinRadius);
            float ss[] = getHeadPoint(faceData);
            float imageVertices1[] = new float[dd.length + ss.length + 3 * 2];

            for (int i = 0; i < dd.length; i++) {
                imageVertices1[i] = dd[i];
            }

            for (int i = 0; i < ss.length; i++) {
                imageVertices1[dd.length + i] = ss[i];
            }
            imageVertices1[ss.length + dd.length] = _offset_5_p.x;
            imageVertices1[ss.length + dd.length + 1] = _offset_5_p.y;
            imageVertices1[ss.length + dd.length + 2] = _offset_27_p.x;
            imageVertices1[ss.length + dd.length + 3] = _offset_27_p.y;
            imageVertices1[ss.length + dd.length + 4] = _sim_face_center.x;
            imageVertices1[ss.length + dd.length + 5] = _sim_face_center.y;

            if (mSrcBuffer == null || mSrcBuffer.capacity() != imageVertices1.length * 4) {
                ByteBuffer bb = ByteBuffer.allocateDirect(imageVertices1.length * 4);
                bb.order(ByteOrder.nativeOrder());
                mSrcBuffer = bb.asFloatBuffer();
            }
            mSrcBuffer.clear();
            mSrcBuffer.put(imageVertices1);
            mSrcBuffer.position(0);

            float[] aa = getChinOffset(faceData, mChinRadius, mChinStrength);
            float[] bb = getHeadOffset(faceData);

            float imageVertices[] = new float[(aa.length / 2 + bb.length / 2 + 3) * 3];
            for (int i = 0; i < aa.length / 2; i++) {
                imageVertices[3 * i] = aa[2 * i];
                imageVertices[3 * i + 1] = aa[2 * i + 1];
            }

            for (int i = 0; i < bb.length / 2; i++) {
                imageVertices[3 * (aa.length / 2 + i)] = bb[2 * i];
                imageVertices[3 * (aa.length / 2 + i) + 1] = bb[2 * i + 1];
            }
            imageVertices[3 * (aa.length / 2 + bb.length / 2)] = _offset_5_p.x;
            imageVertices[3 * (aa.length / 2 + bb.length / 2) + 1] = _offset_5_p.y;
            imageVertices[3 * (aa.length / 2 + bb.length / 2 + 1)] = _offset_27_p.x;
            imageVertices[3 * (aa.length / 2 + bb.length / 2 + 1) + 1] = _offset_27_p.y;
            imageVertices[3 * (aa.length / 2 + bb.length / 2 + 2)] = _sim_face_center.x;
            imageVertices[3 * (aa.length / 2 + bb.length / 2 + 2) + 1] = _sim_face_center.y;

            //先全部置0
            for (int i = 0; i < 141; i++) {
                imageVertices[3 * i + 2] = 0.0f;
            }
            //描述脸部边界
            for (int i = 106; i <= 120; i++) {//下巴
                imageVertices[i * 3 + 2] = 1.0f;
            }
            for (int i = 133; i <= 137; i++) {//额头
                imageVertices[i * 3 + 2] = 1.0f;
            }

            for (int i = 0; i < imageVertices.length / 3; i++) {
                imageVertices[3 * i] = imageVertices[3 * i] * 2 - 1.f;
                imageVertices[3 * i + 1] = imageVertices[3 * i + 1] * 2 - 1.f;
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
            GLES20.glVertexAttribPointer(maPositionLoc, 3, GLES20.GL_FLOAT, false, 0, mChangeBuffer);
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
            idxPoint[i].x = points[i].x;
            idxPoint[i].y = points[i].y;
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
            _t_p = offsetPointF(face_center, idxPoint[i], chinRadius + 2.0f);

            idxPoint[106 + j] = new PointF();
            idxPoint[106 + j].x = _t_p.x;
            idxPoint[106 + j].y = _t_p.y;
        }

        for (int i = 0; i < 106 + 15; i++) {
            ret[i * 2] = idxPoint[i].x;
            ret[i * 2 + 1] = idxPoint[i].y;
        }
        return ret;
    }

    private float[] getChinOffset(PointF[] points, float chinRadius, float chinStrength) {
        float ret[] = getChinPoint(points, chinRadius);

        float[] ori = new float[ret.length];
        for (int i = 0; i < ret.length; i++) {
            ori[i] = ret[i];
        }

        PointF idxPoint[] = new PointF[106 + 15];
        for (int i = 0; i < 106; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[i].x;
            idxPoint[i].y = points[i].y;
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

        float curve_pow = 0.9f;
        //y=-x^4+1.0
        float _t_parabolic_array[] = new float[15];
        float _t_p_parabolic_v = 0;
        for (int i = 9, j = 0; i <= 15; i++, j++) {
            _t_p_parabolic_v = distanceOfPoint(idxPoint[i], chin_ref_p) / parabolic_reflect_x;
            _t_p_parabolic_v = _t_p_parabolic_v < 0.0f ? 0.0f : (_t_p_parabolic_v > 1.0f ? 1.0f : _t_p_parabolic_v);

            _t_parabolic_array[j] = -(float) (Math.pow(_t_p_parabolic_v, curve_pow)) + 1.0f;
            _t_parabolic_array[j] = _t_parabolic_array[j] < 0.0f ? 0.0f : (_t_parabolic_array[j] > 1.0f ? 1.0f : _t_parabolic_array[j]);
        }
        _t_parabolic_array[7] = 1.0f;

        parabolic_reflect_x = distanceOfPoint(_t_p_23, chin_ref_p);
        for (int i = 17, j = 8; i <= 23; i++, j++) {
            _t_p_parabolic_v = distanceOfPoint(idxPoint[i], chin_ref_p) / parabolic_reflect_x;
            _t_p_parabolic_v = _t_p_parabolic_v < 0.0f ? 0.0f : (_t_p_parabolic_v > 1.0f ? 1.0f : _t_p_parabolic_v);

            _t_parabolic_array[j] = -(float) (Math.pow(_t_p_parabolic_v, curve_pow)) + 1.0f;
            _t_parabolic_array[j] = _t_parabolic_array[j] < 0.0f ? 0.0f : (_t_parabolic_array[j] > 1.0f ? 1.0f : _t_parabolic_array[j]);
        }

        float _t_offset_v = 0;
        float _t_offst_p_x = 0;
        float _t_offst_p_y = 0;
        PointF _t_p = new PointF();
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
            ret[i * 2] = ret[i * 2];
            ret[i * 2 + 1] = ret[i * 2 + 1];
        }

        float rectify_strength = calculateRectifyChinStrength(ret, mWidth, mHeight);

        if (rectify_strength <= 0.0f) {
            for (int i = 0; i < ret.length; i++) {
                ret[i] = ori[i];
            }
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
        if (mFaceBuffer != null) {
            mFaceBuffer.destroy();
            mFaceBuffer = null;
        }
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

    private float[] getHeadPoint(PointF[] points) {
        int idx[] = {46, 16, 0, 32, 52, 55, 58, 61, 33, 34, 35, 36, 39, 40, 41, 42, 37, 38};

        PointF idxPoint[] = new PointF[idx.length];
        for (int i = 0; i < idx.length; i++) {
            idxPoint[i] = new PointF();
            idxPoint[i].x = points[idx[i]].x * mWidth;
            idxPoint[i].y = points[idx[i]].y * mHeight;
        }

        PointF noseT = idxPoint[0];      //鼻子
        PointF chin = idxPoint[1];      //下巴
        PointF Lface = idxPoint[2];       //左边点
        PointF Rface = idxPoint[3];     //右边点

        PointF Leye_L = idxPoint[4];    //左眼左眼角
        PointF Leye_R = idxPoint[5];    //左眼右眼角
        PointF Leye_M = new PointF((Leye_L.x + Leye_R.x) / 2, (Leye_L.y + Leye_R.y) / 2);    //左眼中心

        PointF Reye_L = idxPoint[6];    //右眼左眼角
        PointF Reye_R = idxPoint[7];    //右眼右眼角
        PointF Reye_M = new PointF((Reye_L.x + Reye_R.x) / 2, (Reye_L.y + Reye_R.y) / 2);    //右眼中心

        PointF LbrowL = idxPoint[8];    //眉毛
        PointF RbrowR = idxPoint[15];    //眉毛

        float ret[] = new float[17 * 2];

        PointF MidEye = new PointF(points[43].x * mWidth, points[43].y * mHeight);
        PointF BrowCenter = new PointF((points[37].x + points[38].x) / 2 * mWidth, (points[37].y + points[38].y) / 2 * mHeight);

        float eyeDist = (float) Math.sqrt((Leye_M.x - Reye_M.x) * (Leye_M.x - Reye_M.x) + (Leye_M.y - Reye_M.y) * (Leye_M.y - Reye_M.y));
        float browEye = (float) Math.sqrt((BrowCenter.x - MidEye.x) * (BrowCenter.x - MidEye.x) + (BrowCenter.y - MidEye.y) * (BrowCenter.y - MidEye.y));

        ret[0] = LbrowL.x + (LbrowL.x - MidEye.x) * 0.2f;
        ret[1] = LbrowL.y + (LbrowL.y - MidEye.y) * 0.2f;

        ret[2] = RbrowR.x + (RbrowR.x - MidEye.x) * 0.2f;
        ret[3] = RbrowR.y + (RbrowR.y - MidEye.y) * 0.2f;

        ret[4] = BrowCenter.x + (BrowCenter.x - MidEye.x) / browEye * eyeDist * 0.15f;
        ret[5] = BrowCenter.y + (BrowCenter.y - MidEye.y) / browEye * eyeDist * 0.15f;

        PointF left1 = new PointF(ret[4] / 3.0f + ret[0] * 2.0f / 3.0f, ret[5] / 3.0f + ret[1] * 2.0f / 3.0f);
        PointF left2 = new PointF(ret[0] / 3.0f + ret[4] * 2.0f / 3.0f, ret[1] / 3.0f + ret[5] * 2.0f / 3.0f);
        ret[6] = left1.x + (left1.x - MidEye.x) * 0.15f;
        ret[7] = left1.y + (left1.y - MidEye.y) * 0.15f;

        ret[8] = left2.x + (left2.x - MidEye.x) * 0.15f;
        ret[9] = left2.y + (left2.y - MidEye.y) * 0.15f;

        PointF right1 = new PointF(ret[4] / 3.0f + ret[2] * 2.0f / 3.0f, ret[5] / 3.0f + ret[3] * 2.0f / 3.0f);
        PointF right2 = new PointF(ret[2] / 3.0f + ret[4] * 2.0f / 3.0f, ret[3] / 3.0f + ret[5] * 2.0f / 3.0f);

        ret[10] = right1.x + (right1.x - MidEye.x) * 0.15f;
        ret[11] = right1.y + (right1.y - MidEye.y) * 0.15f;

        ret[12] = right2.x + (right2.x - MidEye.x) * 0.15f;
        ret[13] = right2.y + (right2.y - MidEye.y) * 0.15f;


//        ret[14] = 1.5f * ret[6] - 0.5f * chin.x;
//        ret[15] = 1.5f * ret[7] - 0.5f * chin.y;
//        //点9
//        ret[16] = 1.5f * ret[10] - 0.5f * chin.x;
//        ret[17] = 1.5f * ret[11] - 0.5f * chin.y;
//        //点10
//        ret[18] = 1.5f * ret[4] - 0.5f * chin.x;
//        ret[19] = 1.5f * ret[5] - 0.5f * chin.y;

        float point3Dist = distanceOfPoint(new PointF(ret[6], ret[7]), chin);

        ret[14] = ret[6] + 0.85f * eyeDist * (ret[6] - chin.x) / point3Dist;
        ret[15] = ret[7] + 0.85f * eyeDist * (ret[7] - chin.y) / point3Dist;

        float point5Dist = distanceOfPoint(new PointF(ret[10], ret[11]), chin);
        //点8
        ret[16] = ret[10] + 0.85f * eyeDist * (ret[10] - chin.x) / point5Dist;
        ret[17] = ret[11] + 0.85f * eyeDist * (ret[11] - chin.y) / point5Dist;

        float point2Dist = distanceOfPoint(new PointF(ret[4], ret[5]), chin);
        //点9
        ret[18] = ret[4] + 1.2f * eyeDist * (ret[4] - chin.x) / point2Dist;
        ret[19] = ret[5] + 1.2f * eyeDist * (ret[5] - chin.y) / point2Dist;

        //点10
        ret[20] = (ret[18] + ret[14]) / 2.0f;
        ret[21] = (ret[19] + ret[15]) / 2.0f;

        //点11
        ret[22] = (ret[18] + ret[16]) / 2.0f;
        ret[23] = (ret[19] + ret[17]) / 2.0f;

        float faceDist = distanceOfPoint(Lface, Rface) * 1.5f;

        PointF brow_center = new PointF((idxPoint[16].x + idxPoint[17].x) / 2, (idxPoint[16].y + idxPoint[17].y) / 2);

        PointF p_0_7 = new PointF((ret[0] + ret[14]) / 2, (ret[1] + ret[15]) / 2);
        float p_12_offset = distanceOfPoint(p_0_7, brow_center) * 1.3f;
        PointF p_12 = new PointF((p_0_7.x - brow_center.x) / p_12_offset * faceDist + p_0_7.x,
                (p_0_7.y - brow_center.y) / p_12_offset * faceDist + p_0_7.y);

        PointF p_7_10 = new PointF((ret[14] + ret[20]) / 2, (ret[15] + ret[21]) / 2);
        float p_13_offset = distanceOfPoint(p_7_10, brow_center);
        PointF p_13 = new PointF((p_7_10.x - brow_center.x) / p_13_offset * faceDist + p_7_10.x,
                (p_7_10.y - brow_center.y) / p_13_offset * faceDist + p_7_10.y);

        float p_14_offset = distanceOfPoint(new PointF(ret[18], ret[19]), brow_center) * 1.45f;
        PointF p_14 = new PointF((ret[18] - brow_center.x) / p_14_offset * faceDist + ret[18],
                (ret[19] - brow_center.y) / p_14_offset * faceDist + ret[19]);

        PointF p_8_11 = new PointF((ret[16] + ret[22]) / 2, (ret[17] + ret[23]) / 2);
        float p_15_offset = distanceOfPoint(p_8_11, brow_center);
        PointF p_15 = new PointF((p_8_11.x - brow_center.x) / p_15_offset * faceDist + p_8_11.x,
                (p_8_11.y - brow_center.y) / p_15_offset * faceDist + p_8_11.y);

        PointF p_1_8 = new PointF((ret[2] + ret[16]) / 2, (ret[3] + ret[17]) / 2);
        float p_16_offset = distanceOfPoint(p_1_8, brow_center) * 1.3f;
        PointF p_16 = new PointF((p_1_8.x - brow_center.x) / p_16_offset * faceDist + p_1_8.x,
                (p_1_8.y - brow_center.y) / p_16_offset * faceDist + p_1_8.y);

        ret[24] = p_12.x;
        ret[25] = p_12.y;

        ret[26] = p_13.x;
        ret[27] = p_13.y;

        ret[28] = p_14.x;
        ret[29] = p_14.y;

        ret[30] = p_15.x;
        ret[31] = p_15.y;

        ret[32] = p_16.x;
        ret[33] = p_16.y;

        for (int i = 0; i < ret.length / 2; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }

    protected float[] getHeadOffset(PointF[] points) {

        float srcPoints[] = getHeadPoint(points);
        float ret[] = new float[srcPoints.length];
        for (int i = 0; i < srcPoints.length / 2; i++) {
            ret[2 * i] = srcPoints[2 * i] * mWidth;
            ret[2 * i + 1] = srcPoints[2 * i + 1] * mHeight;
        }

        PointF offsetSrc = new PointF(ret[4], ret[5]);
        PointF offsetDst = new PointF(ret[18], ret[19]);
        float offsetDistance = distanceOfPoint(offsetSrc, offsetDst);
        float offset_x = (offsetDst.x - offsetSrc.x);
        float offset_y = (offsetDst.y - offsetSrc.y);

        if (mHeadStrength < 0.0) {
            float offset_leftx = (ret[14] - ret[8]);
            float offset_lefty = (ret[15] - ret[9]);

            float offset_rightx = (ret[16] - ret[12]);
            float offset_righty = (ret[17] - ret[13]);

            ret[14] += offset_leftx * mHeadStrength;
            ret[15] += offset_lefty * mHeadStrength;

            ret[16] += offset_rightx * mHeadStrength;
            ret[17] += offset_righty * mHeadStrength;

            offset_leftx = (ret[20] - ret[4]);
            offset_lefty = (ret[21] - ret[5]);

            offset_rightx = (ret[22] - ret[4]);
            offset_righty = (ret[23] - ret[5]);

            ret[20] += offset_leftx * mHeadStrength * 0.98;
            ret[21] += offset_lefty * mHeadStrength * 0.98;

            ret[22] += offset_rightx * mHeadStrength * 0.98;
            ret[23] += offset_righty * mHeadStrength * 0.98;

            ret[18] += offset_x * mHeadStrength * 0.9;
            ret[19] += offset_y * mHeadStrength * 0.9;

        } else {
            for (int i = 7; i < 12; i++) {
                ret[2 * i] += offset_x * mHeadStrength;
                ret[2 * i + 1] += offset_y * mHeadStrength;
            }
        }


        for (int i = 0; i < ret.length / 2; i++) {
            ret[i * 2] = ret[i * 2] / mWidth;
            ret[i * 2 + 1] = ret[i * 2 + 1] / mHeight;
        }

        return ret;
    }


    private CrazyTriShapeFilter.PointInsidePolygonResult PORS_C_judge_p_inside_polygon_ray(float xarr[], float yarr[], int verticesCount,
                                                                                           float px, float py) {
        if (null == xarr || null == yarr) {
            return CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultParamsIllegal;
        }

        int l = verticesCount;
        if (0 >= l) {

            return CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultParamsIllegal;
        }

        int inside = 0;

        int i = 0;
        int j = 0;
        float sx = 0, sy = 0, tx = 0, ty = 0;
        float x = 0;

        for (i = 0, j = l - 1; i < l; j = i, i++) {

            sx = xarr[i];
            sy = yarr[i];
            tx = xarr[j];
            ty = yarr[j];
            // 点与多边形顶点重合
            if ((sx == px && sy == py) || (tx == px && ty == py)) {
                return CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultCrossEdge;
            }
            // 判断线段两端点是否在射线两侧
            if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
                // 线段上与射线 Y 坐标相同的点的 X 坐标
                x = sx + (py - sy) * (tx - sx) / (ty - sy);

                // 点在多边形的边上
                if (x == px) {
                    return CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultCrossEdge;
                }

                // 射线穿过多边形的边界
                if (x > px) {
                    if (inside > 0) {
                        inside = 0;
                    } else {
                        inside = 1;
                    }
                }
            }
        }

        // 射线穿过多边形边界的次数为奇数时点在多边形内
        return (inside > 0) ? CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultInside : CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultOutside;
    }


    private boolean isPointInsidePolygon(PointF polygon[], PointF p) {
        if (polygon == null || 0 >= polygon.length) {
            return false;
        }

        int vertices_count = (int) polygon.length;

        float x_array[] = new float[vertices_count];
        float y_array[] = new float[vertices_count];

        PointF index_v_p = new PointF();
        for (int i = 0; i < vertices_count; i++) {
            index_v_p = polygon[i];
            x_array[i] = index_v_p.x;
            y_array[i] = index_v_p.y;
        }

        CrazyTriShapeFilter.PointInsidePolygonResult r = PORS_C_judge_p_inside_polygon_ray(x_array, y_array,
                vertices_count, p.x, p.y);

        return (CrazyTriShapeFilter.PointInsidePolygonResult.PointInsidePolygonResultInside == r);
    }


    private float calculateRectifyChinStrength(float ioffset_coordinate[], int width, int height) {
        float area_outside_strength = 1.0f;//用于保护越界的情况下，图像断层问题
        ArrayList<PointF> test_array = new ArrayList<PointF>();

        PointF mouth_0 = new PointF(ioffset_coordinate[84 * 2] * width,
                ioffset_coordinate[84 * 2 + 1] * height);
        PointF mouth_1 = new PointF(ioffset_coordinate[95 * 2] * width,
                ioffset_coordinate[95 * 2 + 1] * height);
        PointF mouth_2 = new PointF(ioffset_coordinate[94 * 2] * width,
                ioffset_coordinate[94 * 2 + 1] * height);
        PointF mouth_3 = new PointF(ioffset_coordinate[93 * 2] * width,
                ioffset_coordinate[93 * 2 + 1] * height);
        PointF mouth_4 = new PointF(ioffset_coordinate[92 * 2] * width,
                ioffset_coordinate[92 * 2 + 1] * height);
        PointF mouth_5 = new PointF(ioffset_coordinate[91 * 2] * width,
                ioffset_coordinate[91 * 2 + 1] * height);
        PointF mouth_6 = new PointF(ioffset_coordinate[90 * 2] * width,
                ioffset_coordinate[90 * 2 + 1] * height);

        test_array.add(mouth_0);
        test_array.add(mouth_1);
        test_array.add(mouth_2);
        test_array.add(mouth_3);
        test_array.add(mouth_4);
        test_array.add(mouth_5);
        test_array.add(mouth_6);


        PointF face_23_p = new PointF(ioffset_coordinate[23 * 2] * width,
                ioffset_coordinate[23 * 2 + 1] * height);
        test_array.add(face_23_p);

        for (int i = 120; i >= 106; i--) {
            PointF face_p = new PointF(ioffset_coordinate[i * 2] * width,
                    ioffset_coordinate[i * 2 + 1] * height);
            test_array.add(face_p);
        }

        PointF face_9_p = new PointF(ioffset_coordinate[9 * 2] * width,
                ioffset_coordinate[9 * 2 + 1] * height);
        test_array.add(face_9_p);

        ArrayList<PointF> hit_array = new ArrayList<PointF>();
        for (int i = 10; i <= 22; i++) {
            PointF face_p = new PointF(ioffset_coordinate[i * 2] * width,
                    ioffset_coordinate[i * 2 + 1] * height);
            hit_array.add(face_p);
        }

        PointF[] tArray = new PointF[test_array.size()];
        for (int i = 0; i < test_array.size(); i++) {
            tArray[i] = new PointF(test_array.get(i).x, test_array.get(i).y);
        }

        for (int i = 0; i < hit_array.size(); i++) {
            PointF hit_p = hit_array.get(i);
            boolean p_inside = isPointInsidePolygon(tArray, hit_p);
            if (!p_inside) {
                area_outside_strength = 0.0f;
                break;
            }
        }

        return area_outside_strength;
    }
}
