package cn.poco.glfilter.shape.V2;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.shape.ShapeInfoData;
import cn.poco.image.PocoFace;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by liujx on 2018/1/24.
 */

public class CrazyTriShapeFilter extends DefaultFilter {

    public enum PointInsidePolygonResult {
        PointInsidePolygonResultInside,         //在内部
        PointInsidePolygonResultCrossEdge,        //在边上、定点
        PointInsidePolygonResultOutside,     //在外部
        PointInsidePolygonResultParamsIllegal;  //参数非法
    }
    protected PointF[] faceData = new PointF[114];
    protected FloatBuffer mSrcTextureBuffer, mChangeVertexBuffer;
    protected int mIndexLength;
    protected ByteBuffer drawIndex;

    protected PocoFace mPocoFace;

    public CrazyTriShapeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadBasicProgram();
    }

    public void setFaceData(PocoFace pocoFace) {
        mPocoFace = pocoFace;
    }


    public void setShapeData(ShapeInfoData shapeData) {}

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        if (mPocoFace != null && mPocoFace.points_count > 0) {
            if(setIndexBuffer())
            {
                for (int i = 0; i < mPocoFace.points_count; i++) {
                    faceData[i] = new PointF();
                    faceData[i].x = mPocoFace.points_array[i].x;
                    faceData[i].y = 1.0f - mPocoFace.points_array[i].y;
                }

                float imageVertices1[] = getSrcPoint(faceData);
                if(imageVertices1 == null)
                    return;

                if (mSrcTextureBuffer == null || mSrcTextureBuffer.capacity() != imageVertices1.length * 4)
                {
                    ByteBuffer bb = ByteBuffer.allocateDirect(imageVertices1.length * 4);
                    bb.order(ByteOrder.nativeOrder());
                    mSrcTextureBuffer = bb.asFloatBuffer();
                }

                mSrcTextureBuffer.clear();
                mSrcTextureBuffer.put(imageVertices1);
                mSrcTextureBuffer.position(0);

                float imageVertices[] = getOffsetPoint(faceData);
                for (int i = 0; i < imageVertices.length / 2; i++)
                {
                    imageVertices[2 * i] = imageVertices[2 * i] * 2 - 1.f;
                    imageVertices[2 * i + 1] = imageVertices[2 * i + 1] * 2 - 1.f;
                }

                if (mChangeVertexBuffer == null || mChangeVertexBuffer.capacity() != imageVertices.length * 4)
                {
                    ByteBuffer bb2 = ByteBuffer.allocateDirect(imageVertices.length * 4);
                    bb2.order(ByteOrder.nativeOrder());
                    mChangeVertexBuffer = bb2.asFloatBuffer();
                }

                mChangeVertexBuffer.clear();
                mChangeVertexBuffer.put(imageVertices);
                mChangeVertexBuffer.position(0);

                GLES20.glEnableVertexAttribArray(maPositionLoc);
                GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, mChangeVertexBuffer);
                GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
                GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, mSrcTextureBuffer);

                GLES20.glDrawElements(GLES20.GL_TRIANGLES, mIndexLength, GLES20.GL_UNSIGNED_SHORT, drawIndex);
            }

        }

        mPocoFace = null;
    }

    public boolean setIndexBuffer()
    {
        return false;
    }

    protected float[] getSrcPoint(PointF []points)
    {
        return null;
    }

    protected float[] getOffsetPoint(PointF []points)
    {
        return null;
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        mPocoFace = null;
        drawIndex = null;
        mSrcTextureBuffer = null;
        mChangeVertexBuffer = null;
    }

    protected boolean isPointInsidePolygon(PointF polygon[], PointF p) {
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

        PointInsidePolygonResult r = PORS_C_judge_p_inside_polygon_ray(x_array, y_array,
                vertices_count, p.x, p.y);

        return (PointInsidePolygonResult.PointInsidePolygonResultInside == r);
    }

    protected PointInsidePolygonResult PORS_C_judge_p_inside_polygon_ray(float xarr[], float yarr[], int verticesCount,
                                                                         float px, float py) {
        if (null == xarr || null == yarr) {
            return PointInsidePolygonResult.PointInsidePolygonResultParamsIllegal;
        }

        int l = verticesCount;
        if (0 >= l) {

            return PointInsidePolygonResult.PointInsidePolygonResultParamsIllegal;
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
                return PointInsidePolygonResult.PointInsidePolygonResultCrossEdge;
            }
            // 判断线段两端点是否在射线两侧
            if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
                // 线段上与射线 Y 坐标相同的点的 X 坐标
                x = sx + (py - sy) * (tx - sx) / (ty - sy);

                // 点在多边形的边上
                if (x == px) {
                    return PointInsidePolygonResult.PointInsidePolygonResultCrossEdge;
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
        return (inside > 0) ? PointInsidePolygonResult.PointInsidePolygonResultInside : PointInsidePolygonResult.PointInsidePolygonResultOutside;
    }


    protected float distanceOfPoint(PointF p1, PointF p2)
    {
        return (float)Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    protected PointF offsetPointF(PointF src, PointF target, float percent)
    {
        return new PointF(src.x+(target.x-src.x)*percent, src.y+(target.y-src.y)*percent);
    }
}
