package cn.poco.glfilter.shape;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.FloatBuffer;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;
import cn.poco.image.PocoFace;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.pgles.PGLNativeIpl;

public class ShrinkNoseFilter extends DefaultFilter {

    private int filterBGCosmeticTextureUniform;
    private int StrengthUniform;
    private int maTextureUnitsId;

    private int bufferSizeUniform;
    private int controlPUniform;
    private int heartPUniform;
    private int midPUniform;
    private int midleftPUniform;
    private int midlefrPUniform;
    private int setNumUniform;
    private int xyPUniform;
    private int yc1PUniform;
    private int cosSinUniform;
    private int sqrtxyUniform;
    private int sqrtxy1Uniform;
    private int dissetUniform;
    private int dissetlowUniform;
    private int faceOrientationUniform;

    private float Strength = 1.0f;  //调节力度 -1~1之间

    private float imgPoint[] = new float[6];

    private float heartx;
    private float hearty;
    private float midx;
    private float midy;
    private float midleftx;
    private float midlefty;
    private float midrightx;
    private float midrighty;
    private float setnum;

    private float yc;
    private float xc;
    private float yc1;
    private float xc1;
    private float lcos;
    private float lsin;
    private float sqrtxy;
    private float sqrtxy1;
    private float disset;
    private float dissetlow;
    private int currentFaceOrientation = 0;

    private PocoFace mPocoFace;
    private FloatBuffer mFacePointsBuffer;

    public ShrinkNoseFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
//        return GlUtil.createProgram(context, R.raw.vert_shrinknose, R.raw.frag_shrinknose);
        return PGLNativeIpl.loadShrinkNoseProgram();
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");

        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");

        bufferSizeUniform = GLES20.glGetUniformLocation(mProgramHandle, "bufferSize");
        controlPUniform = GLES20.glGetUniformLocation(mProgramHandle, "imgPoint");
        heartPUniform = GLES20.glGetUniformLocation(mProgramHandle, "heartP");
        midPUniform = GLES20.glGetUniformLocation(mProgramHandle, "midP");
        midleftPUniform = GLES20.glGetUniformLocation(mProgramHandle, "midleftP");
        midlefrPUniform = GLES20.glGetUniformLocation(mProgramHandle, "midlefrP");
        setNumUniform = GLES20.glGetUniformLocation(mProgramHandle, "setnum");
        xyPUniform = GLES20.glGetUniformLocation(mProgramHandle, "xycP");
        yc1PUniform = GLES20.glGetUniformLocation(mProgramHandle, "xyc1P");

        cosSinUniform = GLES20.glGetUniformLocation(mProgramHandle, "lCosSin");
        sqrtxyUniform = GLES20.glGetUniformLocation(mProgramHandle, "sqrtxy");
        sqrtxy1Uniform = GLES20.glGetUniformLocation(mProgramHandle, "sqrtxy1");
        dissetUniform = GLES20.glGetUniformLocation(mProgramHandle, "disset");
        dissetlowUniform = GLES20.glGetUniformLocation(mProgramHandle, "dissetlow");

        StrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "flex");
        maTextureUnitsId = GLES20.glGetUniformLocation(mProgramHandle, "vTextureId");
        faceOrientationUniform = GLES20.glGetUniformLocation(mProgramHandle,"faceOrientation");
    }

    //缩小鼻子力度范围 0.0-1.0
    public void setStrengthScale(float Scale) {
        Strength = Scale < 0.0f ? 0.0f : (Scale > 1.f ? 1.f : Scale);
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();

        PocoFace pocoFace = FaceDataHelper.getInstance().changeFace(0).getFace();
        if (pocoFace != null) {
            mPocoFace = pocoFace;
        }

        bindTexture(textureId);
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        drawArrays(firstVertex, vertexCount);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();

        mPocoFace = null;
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glUniform1f(StrengthUniform, Strength);

        GLES20.glUniform2f(bufferSizeUniform, mWidth, mHeight);

        if (mPocoFace == null) {
            GLES20.glUniform1f(maTextureUnitsId, 0.0f);
        } else {
            GLES20.glUniform1f(maTextureUnitsId, 1.0f);

            updateFaceFeaturesPosition(mPocoFace.points_array, mWidth, mHeight);
        }
        if (mFacePointsBuffer == null) {
            mFacePointsBuffer = FloatBuffer.wrap(imgPoint);
        } else {
            mFacePointsBuffer.clear();
            mFacePointsBuffer.put(imgPoint);
            mFacePointsBuffer.position(0);
        }

        int face_ori = 0;
        switch (currentFaceOrientation) {
            case PocoFaceOrientation.PORSFaceOrientationLeft:
            {
                face_ori = 1;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationRight:
            {
                face_ori = 2;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationDown:
            {
                face_ori = 3;
            }
            break;

            case PocoFaceOrientation.PORSFaceOrientationUp:
            default:
            {
                face_ori = 0;
            }
            break;
        }

        GLES20.glUniform1i(faceOrientationUniform, face_ori);

        GLES20.glUniform1fv(controlPUniform, 6, mFacePointsBuffer);
        GLES20.glUniform2f(heartPUniform, heartx, hearty);
        GLES20.glUniform2f(midPUniform, midx, midy);
        GLES20.glUniform2f(midleftPUniform, midleftx, midlefty);
        GLES20.glUniform2f(midlefrPUniform, midrightx, midrighty);
        GLES20.glUniform1f(setNumUniform, setnum);
        GLES20.glUniform2f(xyPUniform, xc, yc);
        GLES20.glUniform2f(yc1PUniform, xc1, yc1);
        GLES20.glUniform2f(cosSinUniform, lcos, lsin);
        GLES20.glUniform1f(sqrtxyUniform, sqrtxy);
        GLES20.glUniform1f(sqrtxy1Uniform, sqrtxy1);
        GLES20.glUniform1f(dissetUniform, disset);
        GLES20.glUniform1f(dissetlowUniform, dissetlow);
    }

    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
        mFacePointsBuffer = null;
    }

    private final int porc_nose_slight_refrence_feature_table[] = {59, 60, 62, 63, 53, 54, 56, 57, 98, 102};

    private void updateFaceFeaturesPosition(PointF[] index_face, int width, int height) {
        if (index_face != null && index_face.length > 0) {
            PointF right_eye_inner_0 = index_face[porc_nose_slight_refrence_feature_table[0]];
            PointF right_eye_inner_1 = index_face[porc_nose_slight_refrence_feature_table[1]];
            PointF right_eye_inner_2 = index_face[porc_nose_slight_refrence_feature_table[2]];
            PointF right_eye_inner_3 = index_face[porc_nose_slight_refrence_feature_table[3]];
            PointF right_eye_center = new PointF((float) ((right_eye_inner_0.x + right_eye_inner_1.x + right_eye_inner_2.x + right_eye_inner_3.x) * 0.25),
                    (float) ((right_eye_inner_0.y + right_eye_inner_1.y + right_eye_inner_2.y + right_eye_inner_3.y) * 0.25));

            PointF left_eye_inner_0 = index_face[porc_nose_slight_refrence_feature_table[4]];
            PointF left_eye_inner_1 = index_face[porc_nose_slight_refrence_feature_table[5]];
            PointF left_eye_inner_2 = index_face[porc_nose_slight_refrence_feature_table[6]];
            PointF left_eye_inner_3 = index_face[porc_nose_slight_refrence_feature_table[7]];
            PointF left_eye_center = new PointF((left_eye_inner_0.x + left_eye_inner_1.x + left_eye_inner_2.x + left_eye_inner_3.x) * 0.25f, (left_eye_inner_0.y + left_eye_inner_1.y + left_eye_inner_2.y + left_eye_inner_3.y) * 0.25f);

            PointF top_lip_bot = index_face[porc_nose_slight_refrence_feature_table[8]];
            PointF bot_lip_top = index_face[porc_nose_slight_refrence_feature_table[9]];

            PointF right_eye_center_pic = new PointF(right_eye_center.x * width, (right_eye_center.y) * height);
            PointF left_eye_center_pic = new PointF(left_eye_center.x * width, (left_eye_center.y) * height);
            PointF mouth_center_pic = new PointF((top_lip_bot.x + bot_lip_top.x) * 0.5f * width, (top_lip_bot.y + bot_lip_top.y) * 0.5f * height);


            currentFaceOrientation = PocoFaceOrientation.enquirySimilarityFaceOrientation(
            new PointF(left_eye_center_pic.x, height - left_eye_center_pic.y),
            new PointF(right_eye_center_pic.x, height - right_eye_center_pic.y));

            right_eye_center_pic = PocoFaceOrientation.rotatePointToFitFaceOrientation(right_eye_center_pic, width, height ,currentFaceOrientation);
            left_eye_center_pic = PocoFaceOrientation.rotatePointToFitFaceOrientation(left_eye_center_pic, width, height ,currentFaceOrientation);
            mouth_center_pic = PocoFaceOrientation.rotatePointToFitFaceOrientation(mouth_center_pic, width, height ,currentFaceOrientation);

            imgPoint[0] = right_eye_center_pic.x;
            imgPoint[1] = right_eye_center_pic.y;
            imgPoint[2] = left_eye_center_pic.x;
            imgPoint[3] = left_eye_center_pic.y;
            imgPoint[4] = mouth_center_pic.x;
            imgPoint[5] = mouth_center_pic.y;

            heartx = (imgPoint[0] + imgPoint[2] + 12 * imgPoint[4]) / 14;
            hearty = (imgPoint[1] + imgPoint[3] + 12 * imgPoint[5]) / 14;
            midx = (imgPoint[0] + imgPoint[2]) / 2;
            midy = (imgPoint[1] + imgPoint[3]) / 2;

            midleftx = imgPoint[2] + (imgPoint[0] + imgPoint[2]) / 3;
            midlefty = imgPoint[3] + (imgPoint[1] + imgPoint[3]) / 3;
            midrightx = imgPoint[2] + 2 * (imgPoint[0] + imgPoint[2]) / 3;
            midrighty = imgPoint[3] + 2 * (imgPoint[1] + imgPoint[3]) / 3;

            setnum = (float) (Math.sqrt((imgPoint[0] - imgPoint[2]) * (imgPoint[0] - imgPoint[2]) + (imgPoint[1] - imgPoint[3]) * (imgPoint[1] - imgPoint[1])) / 2.2);

            yc = imgPoint[5] - midy;
            xc = imgPoint[4] - midx;
            yc1 = imgPoint[1] - imgPoint[3];
            xc1 = imgPoint[0] - imgPoint[2];
            lcos = (imgPoint[0] - imgPoint[2]) / setnum / 4;
            lsin = (imgPoint[1] - imgPoint[3]) / setnum / 4;
            sqrtxy = (float) Math.sqrt(yc * yc + xc * xc);
            sqrtxy1 = (float) Math.sqrt(yc1 * yc1 + xc1 * xc1);
            disset = (float) (Math.abs(yc1 * heartx - xc1 * hearty + imgPoint[3] * xc1 - imgPoint[2] * yc1) / sqrtxy1 / 3.5);
            dissetlow = (float) (Math.abs(xc1 * midy - yc1 * midx + heartx * yc1 - hearty * xc1) / sqrtxy1 / 4);
        }
    }

}
