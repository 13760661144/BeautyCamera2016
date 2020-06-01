package cn.poco.glfilter.beauty;

import android.content.Context;
import android.opengl.GLES20;
import android.support.annotation.FloatRange;

import java.nio.FloatBuffer;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by Jdlin on 2016/7/27.
 */
public class BeautyFilter extends DefaultFilter {

    private int muTexelWidthLoc;
    private int muTexelHeightLoc;
    private int muDeltaLoc;
    private int muPercentLoc;
    private int muQflagLoc;
    private int muStep1Loc;
    private int muStep2Loc;

    private float mTexelWidth;
    private float mTexelHeight;
    private float mDelta = 0.0005f;
    private float mPercent = 0.9f;
    private float mStep1 = 8.5f;
    private float mStep2 = 4.5f;
    private float mQualCommFlag = 0.0f;

    protected int mIndexTexture0;
    protected int mIndexTexture1;
    protected int mIndexTexture2;

    private int muIndexTextureLoc0;
    private int muIndexTextureLoc1;
    private int muIndexTextureLoc2;

    protected byte[] mRedTable = null;
    protected byte[] mGreenTable = null;
    protected byte[] mBlueTable = null;

    public BeautyFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mRedTable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1};
        mGreenTable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1};
        mBlueTable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1};

        mIndexTexture0 = GlUtil.createIndexTexture(mRedTable);
        mIndexTexture1 = GlUtil.createIndexTexture(mGreenTable);
        mIndexTexture2 = GlUtil.createIndexTexture(mBlueTable);

        return PGLNativeIpl.loadStikerBeautyProgram();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        muIndexTextureLoc0 = GLES20.glGetUniformLocation(mProgramHandle, "redTable");
        muIndexTextureLoc1 = GLES20.glGetUniformLocation(mProgramHandle, "greenTable");
        muIndexTextureLoc2 = GLES20.glGetUniformLocation(mProgramHandle, "blueTable");

        muTexelWidthLoc = GLES20.glGetUniformLocation(mProgramHandle, "texelWidth");
        muTexelHeightLoc = GLES20.glGetUniformLocation(mProgramHandle, "texelHeight");
        muDeltaLoc = GLES20.glGetUniformLocation(mProgramHandle, "delta");
        muPercentLoc = GLES20.glGetUniformLocation(mProgramHandle, "percent");
        muStep1Loc = GLES20.glGetUniformLocation(mProgramHandle, "step1");
        muStep2Loc = GLES20.glGetUniformLocation(mProgramHandle, "step2");
        muQflagLoc = GLES20.glGetUniformLocation(mProgramHandle, "qflag");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return true;
    }

    public void setCameraSize(int width, int height) {
//        Log.i("bbb", "setCameraSize width:"+width+", height:"+height);
        mTexelWidth = 1.f / (float) width;
        mTexelHeight = 1.f / (float) height;
        int minVal = Math.min(width, height);
        if (minVal <= 640) {
            mDelta = 0.0003f;
            mStep1 = 3.5f;
            mStep2 = 1.5f;

        } else if (minVal <= 1000) {
            mDelta = 0.0005f;
            mStep1 = 5.5f;
            mStep2 = 2.5f;

        } else if (minVal <= 1100) {
            mDelta = 0.0007f;
            mStep1 = 11.5f;
            mStep2 = 5.5f;

        } else {
            mDelta = 0.0009f;
            mStep1 = 18.5f;
            mStep2 = 8.5f;
        }
    }

    /**
     * @param percent 0-1.0
     */
    public void setBeautyParams(@FloatRange(from = 0.0f, to = 1.0f) float percent) {
        if (percent < 0) {
            percent = 0;
        } else if (percent > 1.0f) {
            percent = 1.0f;
        }
        mPercent = percent;
    }

    @Override
    protected void bindTexture(int textureId) {
        super.bindTexture(textureId);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture0);
        GLES20.glUniform1i(muIndexTextureLoc0, 1);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture1);
        GLES20.glUniform1i(muIndexTextureLoc1, 2);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture2);
        GLES20.glUniform1i(muIndexTextureLoc2, 3);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        GLES20.glUniform1f(muTexelWidthLoc, mTexelWidth);
        GLES20.glUniform1f(muTexelHeightLoc, mTexelHeight);
        GLES20.glUniform1f(muPercentLoc, mPercent);
        GLES20.glUniform1f(muDeltaLoc, mDelta);
        GLES20.glUniform1f(muStep1Loc, mStep1);
        GLES20.glUniform1f(muStep2Loc, mStep2);
        GLES20.glUniform1f(muQflagLoc, mQualCommFlag);
    }

    @Override
    protected void unbindTexture() {
        super.unbindTexture();
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteTextures(3, new int[]{mIndexTexture0, mIndexTexture1, mIndexTexture2}, 0);
    }
}
