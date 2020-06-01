package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.Arrays;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;

public class NarrowFaceBigEyeFilter extends DefaultFilter {

    private int mPositionLocation;
    private int mStrengthLocation;
    private int mWidthLocation;
    private int mHeightLocation;
    private int mEyeStrengthLocation;

    private float mSmallFaceScale = 0.f;
    private float mBigEyeScale = 0.f;

    private float[] mPoints = new float[33 * 2];
    private boolean mHasFaceData;
    private boolean mFaceDataIsChange = true;
    private PocoFace mPocoFace;
    private FloatBuffer mFacePointsBuffer;

    public NarrowFaceBigEyeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadSmallFaceBigEyeProgram();
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();

        muTextureLoc = GLES20.glGetUniformLocation(mProgramHandle, "sourceImage");

        mPositionLocation = GLES20.glGetUniformLocation(mProgramHandle, "position");
        mStrengthLocation = GLES20.glGetUniformLocation(mProgramHandle, "strength");

        mWidthLocation = GLES20.glGetUniformLocation(mProgramHandle, "mwidth");
        mHeightLocation = GLES20.glGetUniformLocation(mProgramHandle, "mheight");

        mEyeStrengthLocation = GLES20.glGetUniformLocation(mProgramHandle, "eyeStretchStrength_u");
    }

    @Override
    public boolean isNeedFlipTexture() {
        return false;
    }

    /**
     * faceScale:  瘦脸参数范围（0 - 1.0）
     * eyeScale:   眼睛参数范围（0 - 1.0）
     */
    public void setFaceEyeScale(float faceScale, float eyeScale) {
        if (faceScale < 0) {
            faceScale = 0;
        } else if (faceScale > 1.0f) {
            faceScale = 1.0f;
        }
        if (eyeScale < 0) {
            eyeScale = 0;
        } else if (eyeScale > 1.0f) {
            eyeScale = 1.0f;
        }
        mSmallFaceScale = faceScale;
        mBigEyeScale = eyeScale;
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        super.bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        GLES20.glUniform1f(mWidthLocation, mWidth);
        GLES20.glUniform1f(mHeightLocation, mHeight);
        GLES20.glUniform1f(mEyeStrengthLocation, mBigEyeScale);
        GLES20.glUniform1f(mStrengthLocation, mSmallFaceScale);

        PocoFace pocoFace = FaceDataHelper.getInstance().changeFace(0).getFace();
        if ((pocoFace != null && mPocoFace == null) || (pocoFace == null && mPocoFace != null) || (pocoFace != null && mPocoFace != null && pocoFace != mPocoFace)) {
            mFaceDataIsChange = true;
            mPocoFace = pocoFace;
        }

        if (mFacePointsBuffer == null) {
            mFacePointsBuffer = FloatBuffer.wrap(mPoints);
        }
        if (mFaceDataIsChange) {
            mFaceDataIsChange = false;
            if (mPocoFace != null && mPocoFace.points_count > 0) {
                setPosition(mPocoFace.points_array);
            } else {
                setPosition(null);
            }
            if (mFacePointsBuffer != null) {
                mFacePointsBuffer.clear();
                mFacePointsBuffer.put(mPoints);
                mFacePointsBuffer.position(0);
            }
        }
        GLES20.glUniform1fv(mPositionLocation, mPoints.length, mFacePointsBuffer);
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        mPocoFace = null;
        mFacePointsBuffer = null;
    }

    private void setPosition(PointF[] points) {
        if (points == null) {
            if (mHasFaceData) {
                Arrays.fill(mPoints, 0);
            }
            mHasFaceData = false;
            return;
        }
        mHasFaceData = true;

        // p_eyeRight
        mPoints[0] = points[Sslandmarks.rEyeCenter].x;
        mPoints[1] = 1.0f - points[Sslandmarks.rEyeCenter].y;

        // p_eyeLeft
        mPoints[2] = points[Sslandmarks.lEyeCenter].x;
        mPoints[3] = 1.0f - points[Sslandmarks.lEyeCenter].y;

        // p_browRight
        mPoints[4] = points[Sslandmarks.rEyebrowTop].x;
        mPoints[5] = 1.0f - points[Sslandmarks.rEyebrowTop].y;

        // p_browLeft
        mPoints[6] = points[Sslandmarks.lEyebrowTop].x;
        mPoints[7] = 1.0f - points[Sslandmarks.lEyebrowTop].y;

        //p_noseTip
        mPoints[8] = points[Sslandmarks.NoseTop].x;
        mPoints[9] = 1.0f - points[Sslandmarks.NoseTop].y;

        //p_noseLeg
        mPoints[10] = points[Sslandmarks.NoseBottom].x;
        mPoints[11] = 1.0f - points[Sslandmarks.NoseBottom].y;

        //p_mouthRight
        mPoints[14] = points[Sslandmarks.rMouthCorner].x;
        mPoints[15] = 1.0f - points[Sslandmarks.rMouthCorner].y;

        //p_mouthLeft
        mPoints[12] = points[Sslandmarks.lMouthCorner].x;
        mPoints[13] = 1.0f - points[Sslandmarks.lMouthCorner].y;

        //p_mouthTop
        mPoints[16] = points[Sslandmarks.TopOfTopLip].x;
        mPoints[17] = 1.0f - points[Sslandmarks.TopOfTopLip].y;

        //p_mouthBottom
        mPoints[18] = points[Sslandmarks.BotOfBotLip].x;
        mPoints[19] = 1.0f - points[Sslandmarks.BotOfBotLip].y;

        //p_chin
        mPoints[20] = points[16].x;
        mPoints[21] = 1.0f - points[16].y;

        //p_faceRight1
        mPoints[22] = points[1].x;
        mPoints[23] = 1.0f - points[1].y;

        //p_faceLeft1
        mPoints[24] = points[31].x;
        mPoints[25] = 1.0f - points[31].y;

        //p_faceRight2
        mPoints[26] = points[4].x;
        mPoints[27] = 1.0f - points[4].y;

        //p_faceLeft2
        mPoints[28] = points[28].x;
        mPoints[29] = 1.0f - points[28].y;

        //p_faceRight3
        mPoints[30] = points[8].x;
        mPoints[31] = 1.0f - points[8].y;

        //p_faceLeft3
        mPoints[32] = points[24].x;
        mPoints[33] = 1.0f - points[24].y;

        //p_faceRight4
        mPoints[34] = points[10].x;
        mPoints[35] = 1.0f - points[10].y;

        //p_faceLeft4
        mPoints[36] = points[22].x;
        mPoints[37] = 1.0f - points[22].y;

        //p_faceRight5
        mPoints[38] = points[12].x;
        mPoints[39] = 1.0f - points[12].y;

        //p_faceLeft5
        mPoints[40] = points[20].x;
        mPoints[41] = 1.0f - points[20].y;

        //p_faceRight6
        mPoints[42] = points[14].x;
        mPoints[43] = 1.0f - points[14].y;

        //p_faceLeft6
        mPoints[44] = points[18].x;
        mPoints[45] = 1.0f - points[18].y;

        //p_faceRight7
        mPoints[46] = points[15].x;
        mPoints[47] = 1.0f - points[15].y;

        //p_faceLeft7
        mPoints[48] = points[17].x;
        mPoints[49] = 1.0f - points[17].y;

        //左眼四周
        mPoints[50] = points[52].x;
        mPoints[51] = 1.0f - points[52].y;

        mPoints[52] = points[72].x;
        mPoints[53] = 1.0f - points[72].y;

        mPoints[54] = points[55].x;
        mPoints[55] = 1.0f - points[55].y;

        mPoints[56] = points[73].x;
        mPoints[57] = 1.0f - points[73].y;

        //右眼四周
        mPoints[58] = points[61].x;
        mPoints[59] = 1.0f - points[61].y;

        mPoints[60] = points[75].x;
        mPoints[61] = 1.0f - points[75].y;

        mPoints[62] = points[58].x;
        mPoints[63] = 1.0f - points[58].y;

        mPoints[64] = points[76].x;
        mPoints[65] = 1.0f - points[76].y;

    }
}
