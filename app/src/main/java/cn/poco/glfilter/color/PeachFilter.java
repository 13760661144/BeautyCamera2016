package cn.poco.glfilter.color;

import android.content.Context;
import android.opengl.GLES20;

import cn.poco.glfilter.base.GlUtil;
import cn.poco.pgles.PGLNativeIpl;
import my.beautyCamera.R;

/**
 * Created by Jdlin on 2016/7/8.
 */
public class PeachFilter extends CamelliaFilter {

    protected int mIndexTexture0;
    protected int mIndexTexture1;
    protected int mIndexTexture2;

    private int muIndexTextureLoc0;
    private int muIndexTextureLoc1;
    private int muIndexTextureLoc2;

    protected byte[] mRedtable = null;
    protected byte[] mGreentable = null;
    protected byte[] mBluetable = null;

    public PeachFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        mRedtable = new byte[]{44, 44, 44, 45, 46, 49, 49, 50, 51, 52, 54, 55, 57, 57, 59, 60, 61, 62, 65, 66, 66, 67, 68, 70, 71, 72, 74, 75, 75, 77, 78, 79, 80, 82, 83, 85, 85, 86, 87, 89, 90, 91, 92, 93, 93, 94, 96, 97, 99, 100, 101, 102, 102, 104, 104, 105, 106, 107, 108, 109, 110, 110, 112, 113, 114, 116, 117, 118, 119, 120, 121, 122, 122, 123, 124, 125, 127, -128, -127, -126, -125, -124, -123, -122, -121, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -107, -105, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -95, -94, -93, -92, -91, -91, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -82, -81, -80, -79, -78, -77, -76, -75, -74, -74, -73, -73, -72, -72, -71, -70, -69, -68, -68, -67, -66, -65, -64, -63, -62, -61, -61, -60, -59, -58, -57, -57, -56, -55, -55, -54, -54, -53, -52, -51, -50, -50, -49, -48, -47, -46, -45, -44, -44, -44, -43, -42, -41, -40, -39, -38, -38, -37, -36, -35, -34, -33, -33, -33, -33, -32, -31, -30, -29, -28, -27, -27, -26, -25, -24, -24, -23, -22, -21, -21, -20, -19, -18, -18, -17, -17, -16, -16, -15, -14, -13, -12, -11, -11, -11, -10, -9, -8, -7, -7, -6, -6, -5, -4, -3, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        mGreentable = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 6, 9, 10, 13, 14, 16, 20, 21, 22, 25, 26, 27, 28, 31, 32, 33, 37, 38, 39, 40, 43, 44, 48, 49, 50, 51, 53, 54, 56, 58, 59, 60, 61, 64, 65, 66, 67, 69, 71, 73, 74, 76, 77, 78, 79, 81, 82, 84, 85, 86, 88, 89, 90, 91, 93, 95, 96, 98, 99, 100, 101, 103, 104, 105, 106, 107, 108, 109, 110, 111, 113, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 126, 127, -128, -127, -126, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -106, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -82, -81, -80, -79, -78, -77, -76, -75, -75, -74, -73, -73, -72, -71, -70, -69, -69, -68, -67, -66, -65, -64, -62, -62, -61, -60, -59, -58, -57, -56, -56, -55, -54, -53, -52, -51, -50, -50, -48, -47, -46, -45, -44, -44, -43, -42, -41, -40, -39, -38, -38, -37, -36, -35, -33, -33, -33, -32, -31, -30, -29, -28, -27, -27, -26, -25, -24, -24, -23, -22, -21, -20, -19, -18, -17, -17, -16, -15, -14, -13, -12, -11, -11, -11, -10, -9, -7, -6, -6, -5, -4, -3, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
        mBluetable = new byte[]{0, 3, 7, 9, 10, 13, 15, 17, 18, 21, 22, 23, 26, 27, 28, 29, 32, 33, 34, 37, 38, 40, 41, 44, 45, 46, 49, 50, 51, 52, 54, 55, 57, 59, 60, 61, 62, 65, 66, 67, 68, 70, 71, 72, 74, 75, 77, 78, 79, 80, 82, 83, 85, 86, 87, 89, 90, 91, 92, 93, 94, 96, 97, 99, 100, 101, 102, 104, 104, 105, 106, 107, 108, 109, 110, 112, 113, 114, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 127, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -105, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -95, -94, -93, -92, -91, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -82, -81, -80, -79, -78, -77, -76, -75, -74, -74, -73, -72, -72, -71, -70, -69, -68, -68, -67, -66, -65, -64, -63, -62, -61, -61, -60, -59, -58, -57, -56, -55, -55, -54, -54, -53, -52, -51, -50, -50, -49, -48, -47, -46, -45, -44, -44, -43, -42, -41, -40, -39, -38, -38, -37, -36, -35, -34, -33, -33, -33, -32, -31, -30, -29, -28, -27, -27, -26, -25, -24, -23, -22, -21, -21, -20, -19, -18, -18, -17, -17, -16, -15, -14, -13, -12, -11, -11, -11, -10, -9, -8, -7, -6, -6, -5, -4, -3, -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};

        mIndexTexture0 = GlUtil.createIndexTexture(mRedtable);
        mIndexTexture1 = GlUtil.createIndexTexture(mGreentable);
        mIndexTexture2 = GlUtil.createIndexTexture(mBluetable);

        mTexture1 = getBitmapTextureId(R.drawable.crazy02_mask1);
        return PGLNativeIpl.loadStikerPeachProgram();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        muIndexTextureLoc0 = GLES20.glGetUniformLocation(mProgramHandle, "redTable");
        muIndexTextureLoc1 = GLES20.glGetUniformLocation(mProgramHandle, "greenTable");
        muIndexTextureLoc2 = GLES20.glGetUniformLocation(mProgramHandle, "blueTable");
    }

    @Override
    protected void bindTexture(int textureId) {
        super.bindTexture(textureId);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture0);
        GLES20.glUniform1i(muIndexTextureLoc0, 2);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 3);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture1);
        GLES20.glUniform1i(muIndexTextureLoc1, 3);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mIndexTexture2);
        GLES20.glUniform1i(muIndexTextureLoc2, 4);
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteTextures(3, new int[]{mIndexTexture0, mIndexTexture1, mIndexTexture2}, 0);
    }
}
