package cn.poco.glfilter.color;

import android.content.Context;
import android.opengl.GLES20;

import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.pgles.PGLNativeIpl;

/**
 * Created by Jdlin on 2016/7/8.
 */
public class JasmineFilter extends DefaultFilter {

    protected int mIndexTexture0;
    protected int mIndexTexture1;
    protected int mIndexTexture2;

    private int muIndexTextureLoc0;
    private int muIndexTextureLoc1;
    private int muIndexTextureLoc2;

    protected byte[] mRedtable = null;
    protected byte[] mGreentable = null;
    protected byte[] mBluetable = null;

    public JasmineFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mRedtable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 6, 8, 8, 10, 11, 13, 15, 16, 17, 19, 19, 21, 22, 24, 25, 26, 28, 29, 30, 34, 35, 36, 37, 39, 40, 41, 42, 44, 45, 47, 48, 50, 51, 53, 55, 56, 57, 58, 60, 62, 63, 64, 65, 67, 68, 69, 70, 71, 75, 76, 78, 79, 80, 82, 83, 84, 86, 87, 88, 89, 91, 92, 93, 94, 95, 96, 98, 100, 102, 103, 104, 106, 107, 108, 109, 110, 111, 113, 114, 117, 118, 119, 120, 121, 122, 123, 124, 126, 127, -128, -127, -126, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -111, -110, -109, -108, -107, -107, -106, -105, -105, -104, -103, -102, -100, -99, -98, -97, -96, -96, -95, -94, -93, -92, -92, -90, -89, -88, -87, -86, -85, -85, -84, -84, -83, -82, -80, -79, -78, -77, -76, -76, -76, -75, -74, -73, -73, -71, -70, -70, -69, -68, -67, -66, -65, -64, -64, -63, -62, -61, -60, -59, -59, -58, -57, -56, -56, -55, -55, -54, -52, -51, -51, -50, -50, -49, -48, -47, -46, -46, -44, -43, -42, -41, -41, -41, -40, -40, -39, -39, -38, -36, -35, -34, -34, -33, -32, -31, -30, -30, -29, -29, -28, -27, -27, -26, -25, -24, -23, -23, -22, -21, -19, -19, -18, -18, -18, -17, -17, -16, -15, -14, -12, -12, -11, -10, -9, -9, -8, -7, -7, -7, -7, -6, -4, -3, -3, -2, -1};
        mGreentable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 4, 7, 8, 8, 10, 11, 12, 13, 15, 17, 18, 20, 20, 21, 22, 24, 25, 29, 30, 31, 32, 34, 35, 36, 38, 40, 41, 42, 43, 45, 46, 48, 49, 51, 52, 53, 54, 56, 58, 59, 61, 63, 64, 65, 66, 69, 70, 71, 72, 73, 76, 77, 79, 80, 81, 82, 84, 85, 86, 87, 88, 90, 91, 93, 94, 95, 96, 97, 98, 101, 104, 105, 106, 107, 108, 109, 111, 112, 113, 114, 115, 116, 119, 121, 122, 123, 124, 125, 126, 127, -128, -127, -126, -124, -123, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -108, -107, -107, -106, -105, -104, -103, -102, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -90, -90, -88, -87, -86, -85, -84, -83, -82, -81, -81, -81, -80, -78, -77, -76, -75, -74, -74, -73, -73, -72, -71, -71, -69, -68, -68, -67, -66, -65, -64, -64, -63, -63, -62, -60, -59, -58, -57, -57, -56, -56, -55, -54, -54, -53, -51, -50, -49, -48, -48, -47, -47, -46, -45, -45, -45, -43, -42, -41, -40, -40, -39, -39, -38, -38, -37, -35, -34, -33, -33, -32, -31, -31, -30, -30, -29, -27, -26, -26, -26, -25, -24, -23, -22, -22, -22, -21, -19, -19, -18, -17, -16, -16, -15, -14, -14, -12, -12, -11, -10, -9, -9, -9, -8, -7, -6, -6, -5, -4, -3, -3, -2, -1};
        mBluetable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 7, 8, 8, 11, 12, 13, 16, 18, 20, 20, 22, 24, 25, 29, 30, 31, 34, 35, 36, 37, 41, 42, 43, 46, 47, 48, 51, 53, 54, 56, 57, 59, 60, 63, 64, 66, 68, 69, 70, 72, 76, 77, 78, 80, 81, 83, 85, 86, 88, 89, 90, 92, 94, 96, 97, 98, 100, 101, 104, 105, 107, 108, 109, 110, 112, 113, 114, 115, 119, 120, 121, 123, 124, 125, 126, 127, -128, -126, -125, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -109, -108, -107, -106, -105, -104, -103, -103, -102, -101, -100, -98, -98, -97, -96, -95, -94, -93, -92, -91, -90, -90, -88, -88, -87, -86, -85, -84, -83, -82, -82, -81, -80, -79, -78, -77, -76, -76, -75, -75, -74, -73, -72, -71, -70, -69, -69, -68, -67, -66, -65, -64, -64, -63, -63, -62, -61, -60, -59, -58, -58, -57, -56, -56, -55, -54, -54, -53, -52, -51, -50, -49, -49, -49, -48, -47, -46, -45, -45, -43, -43, -42, -42, -41, -41, -40, -39, -39, -38, -38, -37, -35, -34, -33, -33, -33, -33, -32, -31, -30, -30, -30, -29, -27, -26, -26, -25, -25, -24, -24, -23, -23, -22, -22, -21, -19, -19, -18, -17, -17, -16, -16, -15, -15, -14, -14, -12, -12, -11, -10, -10, -9, -9, -8, -7, -6, -6, -6, -6, -4, -3, -3, -3, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        mIndexTexture0 = GlUtil.createIndexTexture(mRedtable);
        mIndexTexture1 = GlUtil.createIndexTexture(mGreentable);
        mIndexTexture2 = GlUtil.createIndexTexture(mBluetable);

        return PGLNativeIpl.loadStikerJasmineProgram();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        muIndexTextureLoc0 = GLES20.glGetUniformLocation(mProgramHandle, "redTable");
        muIndexTextureLoc1 = GLES20.glGetUniformLocation(mProgramHandle, "greenTable");
        muIndexTextureLoc2 = GLES20.glGetUniformLocation(mProgramHandle, "blueTable");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return true;
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
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteTextures(3, new int[]{mIndexTexture0, mIndexTexture1, mIndexTexture2}, 0);
    }
}
