package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;

public class CrazyMouthShapeFilter extends DefaultFilter {

    private FloatBuffer mSrcBuffer, mChangeBuffer;
    private int mIndexLength;
    private ByteBuffer drawIndex;
    private PointF[] faceData = new PointF[114];

    private float MouthStrength = 50f;  //调节力度 0-100之间 50的时候无效果
    private float MouthRadius = 0f;   //暂时没用到;

    private PocoFace mPocoFace;

    public CrazyMouthShapeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadBasicProgram();
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }

    public void setShapeData(ShapeInfoData shapeData) {
        if (shapeData == null) return;
        this.MouthRadius = shapeData.mouthRadius;
        this.MouthStrength = shapeData.mouthStrength;

        //范围0-100   50的时候无效果
        if (MouthStrength < 0f) {
            MouthStrength = 0f;
        } else if (MouthStrength > 100.f) {
            MouthStrength = 100.f;
        }
        MouthStrength = ((MouthStrength - 50.f) * 0.25f) / 100.0f;
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
                        0, 7, 8, 0, 1, 8, 1, 8, 9, 1, 2, 9, 2, 3, 9, 3, 9, 10, 3, 10, 11, 3, 11, 4, 4, 11, 12, 4, 12, 5, 5, 12, 6, 6, 12, 13,
                        12, 14, 13, 11, 14, 12, 11, 15, 14, 11, 16, 15, 10, 16, 11, 9, 16, 10, 9, 17, 16, 9, 18, 17, 8, 18, 9, 7, 18, 8,
                        38, 7, 0, 38, 19, 7, 19, 20, 7, 20, 21, 7, 21, 22, 7, 22, 23, 7, 23, 24, 7, 24, 18, 7, 24, 25, 18, 25, 26, 18, 26, 27, 18,
                        18, 27, 17, 17, 27, 16, 27, 28, 16, 28, 29, 16, 29, 30, 16, 16, 30, 15, 15, 30, 31, 15, 31, 14,
                        31, 32, 14, 32, 33, 14, 33, 34, 14, 34, 35, 14, 35, 13, 14, 35, 36, 13, 36, 37, 13, 37, 39, 13, 39, 6, 13
                };
                mIndexLength = index.length;
                drawIndex = ByteBuffer.allocateDirect(index.length * 2)
                        .order(ByteOrder.nativeOrder());
                drawIndex.asShortBuffer().put(index);
            }
            drawIndex.position(0);

            float imageVertices1[] = getMouth(faceData);
            if (mSrcBuffer == null || mSrcBuffer.capacity() != imageVertices1.length * 4) {
                ByteBuffer bb = ByteBuffer.allocateDirect(imageVertices1.length * 4);
                bb.order(ByteOrder.nativeOrder());
                mSrcBuffer = bb.asFloatBuffer();
            }
            mSrcBuffer.clear();
            mSrcBuffer.put(imageVertices1);
            mSrcBuffer.position(0);

            float imageVertices[] = getMouthOffset(faceData, MouthStrength);
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
    public void releaseProgram() {
        super.releaseProgram();
        mPocoFace = null;
        drawIndex = null;
        mSrcBuffer = null;
        mChangeBuffer = null;
    }
}
