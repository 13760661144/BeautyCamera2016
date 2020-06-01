package cn.poco.glfilter;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.AbstractFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.base.IFilter;
import cn.poco.glfilter.base.VertexArray;
import cn.poco.image.PocoFace;

/**
 * 显示脸部的点，用于调试
 */
public class FacePointFilter extends AbstractFilter implements IFilter {

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "attribute float aTextureId;\n" +
                    "attribute float aPointSize;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "varying float vTextureId;\n" +
                    "void main() {\n" +
                    "    gl_Position = aPosition;\n" +
                    "    vTextureCoord = aTextureCoord;\n" +
                    "    vTextureId = aTextureId;\n" +
                    "    gl_PointSize = aPointSize;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D uTexture0;\n" +
                    "uniform sampler2D uTexture1;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "varying float vTextureId;\n" +
                    "void main() {\n" +
                    "   if (vTextureId < 1.0) {\n" +
                    "       gl_FragColor = texture2D(uTexture0, vTextureCoord);\n" +
                    "   } else if (vTextureId < 2.0) {\n" +
                    "       gl_FragColor = texture2D(uTexture1, vTextureCoord);\n" +
                    "   } else {\n" +
                    "       gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" +
                    "   }\n" +
                    "}";

    private int maTextureUnitsId;
    private int maPointSize;
    private int muTexture0;
    private int muTexture1;

    private float mPointSize = 5.0f;//点的大小
    private int mPointCount;//点的数量
    private VertexArray mVertexArray;
    private float[] mPoints = new float[]{
            0.0f, 0.5f,
            -0.5f, -0.5f,
            0.5f, -0.5f
    };

    private ByteBuffer mPointIndexBuffer;
    private short[] mPointIndexArr;

    private int textureId1;
    private PocoFace mPocoFace;

    public FacePointFilter(Context context) {
        super(context);
        mPointCount = 114;//106
        mPoints = new float[mPointCount * 2];

        float[] vertexTexture = new float[]{0.5f, 0.5f, 0.45f, 0.7f, 0.55f, 0.7f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f};

        mVertexArray = new VertexArray(mPoints, vertexTexture);

        mPointIndexArr = new short[]{
                33, 34, 64, 34, 35, 64, 35, 64, 65, 35, 36, 65, 36, 65, 66, 36, 37, 66, 37, 67, 66,
                38, 39, 68, 39, 69, 68, 39, 40, 69, 40, 70, 69, 40, 41, 70, 41, 71, 70, 41, 42, 71,
                52, 53, 74, 53, 72, 74, 72, 54, 74, 54, 55, 74, 55, 56, 74, 56, 73, 74, 73, 57, 74, 57, 52, 74,
                58, 59, 77, 59, 75, 77, 75, 60, 77, 60, 61, 77, 61, 62, 77, 62, 76, 77, 76, 63, 77, 63, 58, 77,
                82, 47, 46, 47, 48, 46, 48, 49, 46, 49, 50, 46, 50, 51, 46, 51, 83, 46, 82, 80, 46, 80, 45, 46, 45, 81, 46, 81, 83, 46, 80, 44, 45, 78, 44, 80, 44, 45, 81, 79, 44, 81, 78, 43, 44, 43, 79, 44,
                84, 85, 96, 85, 86, 96, 86, 97, 96, 86, 87, 97, 87, 98, 97, 87, 88, 98, 88, 99, 98, 88, 89, 99, 89, 100, 99, 89, 90, 100, 84, 103, 95, 103, 94, 95, 103, 102, 94, 102, 93, 94, 102, 101, 93, 101, 92, 93, 101, 91, 92, 101, 90, 91,
                108, 0, 116, 0, 1, 116, 1, 2, 116, 2, 3, 116, 3, 4, 116, 4, 5, 116, 5, 6, 116, 6, 7, 116, 116, 7, 118, 7, 8, 118, 8, 9, 118, 9, 10, 118, 10, 11, 118, 11, 12, 118, 118, 12, 119, 12, 13, 119, 13, 14, 119, 14, 15, 119, 15, 16, 119, 16, 17, 119, 17, 18, 119, 18, 19, 119, 19, 20, 119, 119, 20, 120, 20, 21, 120, 21, 22, 120, 22, 23, 120, 23, 24, 120, 24, 25, 120, 120, 25, 117, 25, 26, 117, 26, 27, 117, 27, 28, 117, 28, 29, 117, 29, 30, 117, 30, 31, 117, 31, 32, 117, 112, 32, 117, 117, 112, 110, 111, 112, 110, 110, 111, 114, 110, 111, 114, 110, 109, 114, 114, 109, 113, 109, 108, 113, 113, 108, 116, 0, 33, 108, 33, 34, 108, 108, 34, 109, 34, 35, 109, 35, 36, 109, 109, 110, 36, 36, 37, 110, 37, 38, 110, 38, 39, 110, 110, 39, 111, 39, 40, 111, 40, 41, 111, 111, 41, 112, 41, 42, 112, 42, 32, 112, 0, 33, 52, 0, 1, 52, 1, 2, 52, 52, 106, 2, 2, 3, 106, 3, 4, 106, 52, 57, 106, 4, 5, 106, 5, 6, 106, 6, 7, 106, 7, 8, 84, 8, 9, 84, 84, 95, 9, 9, 10, 95, 10, 11, 95, 11, 12, 95, 12, 13, 95, 95, 94, 13, 13, 14, 94, 94, 93, 14, 93, 15, 14, 93, 16, 15, 93, 16, 17, 93, 92, 17, 92, 91, 17, 91, 18, 17, 91, 19, 18, 91, 20, 19, 91, 90, 20, 90, 21, 20, 90, 22, 21, 90, 23, 22, 90, 24, 23, 90, 25, 24, 25, 107, 26, 107, 27, 26, 107, 28, 27, 107, 29, 28, 107, 30, 29, 61, 107, 30, 61, 31, 30, 32, 31, 61, 32, 42, 61, 33, 64, 52, 64, 53, 52, 64, 65, 53, 65, 66, 53, 66, 72, 53, 66, 54, 72, 66, 67, 54, 67, 55, 54, 37, 38, 67, 67, 38, 68, 67, 78, 55, 67, 43, 78, 67, 68, 43, 43, 79, 68, 79, 58, 81, 79, 58, 68, 58, 59, 68, 68, 69, 59, 59, 75, 69, 69, 70, 75, 75, 60, 70, 70, 71, 60, 71, 42, 61, 71, 61, 60, 61, 62, 107, 76, 62, 107, 76, 63, 83, 58, 63, 81, 63, 81, 83, 57, 73, 106, 55, 78, 80, 56, 55, 80, 56, 80, 82, 56, 73, 82, 73, 106, 82, 76, 83, 107, 106, 7, 84, 106, 82, 84, 84, 82, 85, 82, 47, 85, 107, 25, 90, 107, 90, 83, 47, 48, 85, 48, 85, 86, 48, 49, 86, 49, 86, 87, 49, 87, 88, 49, 50, 88, 50, 51, 88, 51, 89, 88, 51, 83, 89, 83, 89, 90
        };
//        mPointIndexArr = new short[]{
////                0, 3, 1, 1, 3, 6, 6, 1, 0, 0, 6, 5, 5, 0, 2, 2, 5, 4, 4, 2, 0, 0, 4, 3,
//                0, 3, 1, 3, 1, 6, 1, 6, 0, 6, 0, 5, 0, 5, 2, 5, 2, 4, 2, 4, 0, 4, 0, 3
//        };

        mPointIndexBuffer = ByteBuffer
                .allocateDirect(mPointIndexArr.length * 2)
                .order(ByteOrder.nativeOrder());
        mPointIndexBuffer.asShortBuffer().put(mPointIndexArr);
        mPointIndexBuffer.position(0);
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    protected int createProgram(Context context) {
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        maTextureUnitsId = GLES20.glGetAttribLocation(mProgramHandle, "aTextureId");
        maPointSize = GLES20.glGetAttribLocation(mProgramHandle, "aPointSize");
        muTexture0 = GLES20.glGetUniformLocation(mProgramHandle, "uTexture0");
        muTexture1 = GLES20.glGetUniformLocation(mProgramHandle, "uTexture1");
    }

    @Override
    public boolean isNeedBlend() {
        return true;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                       int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        loadTexture();
        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        drawArrays(firstVertex, vertexCount);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    private void loadTexture() {
//        if (textureId1 <= 0) {
//            textureId1 = getBitmapTextureId(R.drawable.mn81010005);
//        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(muTexture0, 0);

        if (textureId1 > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId1);
            GLES20.glUniform1i(muTexture1, 1);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glVertexAttrib1f(maTextureUnitsId, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        mPoints = getFacePoints();
        if (mPoints != null) { //draw point
            mPointCount = mPoints.length / 2;
            mVertexArray.updateBuffer(mPoints, 0, mPoints.length);

            GLES20.glEnableVertexAttribArray(maPositionLoc);
            GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, mVertexArray.vertexStride, mVertexArray.vertexBuffer);

            GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
            GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, mVertexArray.textureVerticesBuffer);

            GLES20.glVertexAttrib1f(maTextureUnitsId, textureId1 > 0 ? 1 : 2);
            GLES20.glVertexAttrib1f(maPointSize, mPointSize);
            //非索引法
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, mPointCount);
//            GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, mPointCount);
//            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mPointCount);

            //索引法
//            GLES20.glDrawElements(GLES20.GL_LINE_STRIP, mPointIndexArr.length, GLES20.GL_UNSIGNED_SHORT, mPointIndexBuffer);
//            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mPointIndexArr.length, GLES20.GL_UNSIGNED_SHORT, mPointIndexBuffer);
        }
    }

    private float[] getFacePoints() {
        mPocoFace = FaceDataHelper.getInstance().changeFace(0).getFace();
        if (mPocoFace == null || mPocoFace.mGLPoints == null) {
            return null;
        }
        PointF[] points = mPocoFace.mGLPoints;
        if (points.length < 2) {
            return null;
        }
        float[] allPoints = new float[mPointCount * 2];
        for (int i = 0; i < points.length && i < mPointCount; ++i) {
            allPoints[i * 2] = points[i].x;
            allPoints[i * 2 + 1] = points[i].y;
        }
//        allPoints[0] = points[110].x;
//        allPoints[1] = points[110].y;
//        allPoints[2] = points[109].x;
//        allPoints[3] = points[109].y;
//        allPoints[4] = points[111].x;
//        allPoints[5] = points[111].y;
//
//        float lx = points[109].x - (Math.abs(points[109].x) + Math.abs(points[111].x)) / 2;
//        float rx = points[111].x + (Math.abs(points[109].x) + Math.abs(points[111].x)) / 2;
//        float t = (points[43].y - points[110].y);
//
//        allPoints[6] = lx;
//        allPoints[7] = points[35].y - t;
//        allPoints[8] = rx;
//        allPoints[9] = points[40].y - t;
//        allPoints[10] = rx;
//        allPoints[11] = points[40].y;
//        allPoints[12] = lx;
//        allPoints[13] = points[35].y;

//        float[] allPoints = new float[]{0.2130682f, 0.4813008f, 0.2149621f, 0.5065041f, 0.219697f, 0.5308943f, 0.2253788f, 0.5569106f, 0.2310606f, 0.5829268f, 0.2405303f, 0.6138211f, 0.25f, 0.6430894f, 0.2613636f, 0.6731707f, 0.2727273f, 0.702439f, 0.2888258f, 0.7317073f, 0.3106061f, 0.7601626f, 0.3323864f, 0.7853659f, 0.3589015f, 0.8073171f, 0.3873106f, 0.8284553f, 0.4232955f, 0.8479675f, 0.4621212f, 0.8593496f, 0.5f, 0.8609756f, 0.5378788f, 0.8609756f, 0.5757576f, 0.8487805f, 0.6126894f, 0.8276423f, 0.6401515f, 0.8081301f, 0.6657197f, 0.7853659f, 0.6893939f, 0.7601626f, 0.7102273f, 0.7333333f, 0.7263258f, 0.704065f, 0.7395833f, 0.6747967f, 0.75f, 0.6447154f, 0.7604167f, 0.6130081f, 0.7698864f, 0.5829268f, 0.7755682f, 0.5569106f, 0.7793561f, 0.5317073f, 0.7850379f, 0.5065041f, 0.7878788f, 0.4804878f, 0.2547348f, 0.4333333f, 0.2926136f, 0.4081301f, 0.3494318f, 0.4065041f, 0.4015152f, 0.4105691f, 0.4441288f, 0.4252033f, 0.5568182f, 0.4252033f, 0.5984848f, 0.4113821f, 0.6515152f, 0.404878f, 0.7064394f, 0.4081301f, 0.7462121f, 0.4333333f, 0.500947f, 0.4723577f, 0.5f, 0.5276423f, 0.500947f, 0.5821138f, 0.5f, 0.6227642f, 0.4498106f, 0.6487805f, 0.4725379f, 0.6430894f, 0.499053f, 0.6552846f, 0.5255682f, 0.6414634f, 0.5501894f, 0.6479675f, 0.3011364f, 0.4902439f, 0.342803f, 0.4691057f, 0.3948864f, 0.4699187f, 0.4308712f, 0.501626f, 0.3929924f, 0.5081301f, 0.3418561f, 0.5081301f, 0.5681818f, 0.501626f, 0.6070076f, 0.4691057f, 0.6590909f, 0.4691057f, 0.6988636f, 0.4902439f, 0.6581439f, 0.5089431f, 0.6070076f, 0.5081301f, 0.2897727f, 0.4284553f, 0.34375f, 0.4300813f, 0.3996212f, 0.4349593f, 0.4479167f, 0.4422764f, 0.5530303f, 0.4422764f, 0.6003788f, 0.4349593f, 0.65625f, 0.4292683f, 0.7102273f, 0.4284553f, 0.3674242f, 0.4650407f, 0.3674242f, 0.5130081f, 0.3664773f, 0.4878049f, 0.6325758f, 0.4650407f, 0.6325758f, 0.5113821f, 0.6325758f, 0.4886179f, 0.4602273f, 0.4813008f, 0.5369318f, 0.4821138f, 0.4545455f, 0.598374f, 0.5464015f, 0.598374f, 0.4327652f, 0.6382114f, 0.5681818f, 0.6373984f, 0.3892045f, 0.7178862f, 0.4308712f, 0.7089431f, 0.46875f, 0.701626f, 0.5f, 0.7105691f, 0.532197f, 0.701626f, 0.5681818f, 0.7089431f, 0.6098485f, 0.7186992f, 0.5691288f, 0.7439024f, 0.5416667f, 0.7593496f, 0.5f, 0.7634146f, 0.4602273f, 0.7585366f, 0.4318182f, 0.7447154f, 0.4043561f, 0.7195122f, 0.4583333f, 0.7219512f, 0.5f, 0.7252033f, 0.5416667f, 0.7219512f, 0.5956439f, 0.7203252f, 0.5293561f, 0.7284553f, 0.5f, 0.7292683f, 0.469697f, 0.7276423f, 0.3721591f, 0.4886179f, 0.6268939f, 0.4886179f, 0.3030303f, 0.6536585f, 0.6988636f, 0.6544715f, 0.2992424f, 0.3495935f, 0.3712121f, 0.3430894f, 0.5f, 0.3430894f, 0.6297348f, 0.3447154f, 0.6979167f, 0.3504065f, 0.f, 0.001626f};
//        for (int i = 0; i < allPoints.length / 2; i++) {
//            allPoints[i * 2] = (allPoints[i * 2] - 0.5f) * 2;
//            allPoints[i * 2 + 1] = -(allPoints[i * 2 + 1] - 0.5f) * 2;
//        }

        return allPoints;
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindGLSLValues() {
        super.unbindGLSLValues();
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void loadNextTexture(boolean load) {

    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
        mPocoFace = null;
    }
}
