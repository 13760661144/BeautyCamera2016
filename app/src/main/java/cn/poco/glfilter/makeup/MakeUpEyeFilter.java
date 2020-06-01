package cn.poco.glfilter.makeup;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import cn.poco.image.PocoCompositeOperator;
import cn.poco.image.PocoFaceOrientation;
import cn.poco.image.Sslandmarks;
import cn.poco.pgles.PGLNativeIpl;
import cn.poco.resource.RealTimeMakeUpSubRes;

public class MakeUpEyeFilter extends MakeUpBaseFilter {

    private int maTextureUnitsId;
    private float facesFeaturesCoordinates[] = new float[114 * 2];

    private PointF meyePoints[] = new PointF[4]; //左眼顺时针，右眼逆时针 的四个点坐标，在图片坐标系,归一化
    private PointF meyePointCenter = new PointF();
    private PointF mBufferRenderClipCenterPoint;

    private PointF t_mEyePoints[] = new PointF[4];
    private PointF t_mEyePointCenter;

    private int /*cosmeticPositionAttribute, bgTextureCoordinateAttribute, */eyeMaterialTextureCoordinateAttribute;
    private int filterBGCosmeticTextureUniform;

    private int filterEyePositionLeftRightFlipUniform;//float
    private int filterLevelOrientCosSinUniform;      //vec2
    private int filterBufferSizeUniform;             //vec2

    private int filterEyeShadowTextureUniform, filterEyeLinerTextureUniform, filterEyeLashTextureUniform;//sampler2D

    private int filterFrameTemplateRemappingFactorUniform;   //float
    private int filterTargetEyeLowerLidLumaUniform;          //float
    private int filterOrientedUpperLidCenterUniform, filterOrientedLowerLidCenterUniform;//vec2
    private int filterSimilarityOriginUniform, filterSimilarityShiftUniform;          //vec2
    private int filterSimilarityScaleUniform;    //float

    //vec4
    private int filterTopSplineTransformSrcDstCenterUniform, filterTopLeftSplineTransformParabolicCoeffUniform, filterTopRightSplineTransformParabolicCoeffUniform;
    private int filterBottomSplineTransformSrcDstCenterUniform, filterBottomLeftSplineTransformParabolicCoeffUniform, filterBottomRightSplineTransformParabolicCoeffUniform;

    private int filterEyeLinerTemplateColorUniform, filterEyeLashTemplateColorUniform;//vec3

    private int filterEnableEyeShadowUniform, filterEnableEyeLinerUniform, filterEnableEyeLashUniform;//int
    private int filterEnvironmentLumaUniform;//vec2
    private int filterEyeROIUniform;//vec4
    private int filterMinColorUniform, filterMaxColorUniform;//vec3
    private int filterUpperLidEyeLashYScaleAdjusterUniform;//float
    private int filterShimmerModelScaleUniform;//float
    private int filterMaxLumaUniform;//float
    private int filterLumaRangeUniform;//float

    private int filterBrightTexture_0_Uniform, filterBrightTexture_1_Uniform;//sampler2D
    private int filterGlitterTexture_0_Uniform, filterGlitterTexture_1_Uniform;

    private int filterLeftRightCornerToTopCenterSquareUniform;
    private int filterActualTopLeftRightParabolicUniform;
    private int filterLeftRightCornerToBottomCenterSquareUniform;
    private int filterActualBottomLeftRightParabolicUniform;

    private int filterEyeShadowBlendTypeUniform;
    private int filterEyeShadowStrengthUniform;
    private int mEyeShadowBlendType = PocoCompositeOperator.MultiplyCompositeOp;   //眼影混合模式
    private float mEyeShadowStrength = 1.0f;

    private boolean mFaceDataIsChange = true;
    private int currentFaceOrientation = 0;
    private int mFaceOrientation = -1;
    private int size_width;
    private int size_height;

    private int mLeftEyeLuminance, mRightEyeLuminance;
    private RectF renderROI;
    private PointF standardParabolicPointsInPic[] = new PointF[4];
    private FloatBuffer clipBuffer, noRotationClipBuffer, noRotationBuffer;

    private PointF standard_eye_material_points[] = new PointF[]{new PointF(0.31555554f, 0.23916666f), new PointF(0.5088889f, 0.1775f), new PointF(0.7033333f, 0.23916666f), new PointF(0.5088889f, 0.30166668f)};

    private int mLashId, mShadowId, mLineId;
    private int mLashColor, mLineColor;

    private boolean mIsRightEye = true;   //判断左眼还是右眼 false为左眼

    public MakeUpEyeFilter(Context context) {
        super(context);
        mTextureIdCount = 3;
    }

    @Override
    protected int createProgram(Context context) {
        return PGLNativeIpl.loadEyefilterProgram();
    }

    @Override
    protected void getGLSLValues() {
        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "position");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "inputTextureCoordinate");
        maTextureUnitsId = GLES20.glGetUniformLocation(mProgramHandle, "vTextureId");

        eyeMaterialTextureCoordinateAttribute = GLES20.glGetAttribLocation(mProgramHandle, "inputTemplateTextureCoordinate");

        filterEyePositionLeftRightFlipUniform = GLES20.glGetUniformLocation(mProgramHandle, "left_right_flip");
        filterLevelOrientCosSinUniform = GLES20.glGetUniformLocation(mProgramHandle, "level_orient_cos_sin");
        filterBufferSizeUniform = GLES20.glGetUniformLocation(mProgramHandle, "analyzing_frame_width_height_in_pixel");

        //片元着色器
        filterBGCosmeticTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "inputImageTexture");
        filterEyeShadowTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyeshadow_texture");
        filterEyeLinerTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyeliner_texture");
        filterEyeLashTextureUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyelash_texture");

        filterFrameTemplateRemappingFactorUniform = GLES20.glGetUniformLocation(mProgramHandle, "frame_to_template_y_remapping_factor");
        filterTargetEyeLowerLidLumaUniform = GLES20.glGetUniformLocation(mProgramHandle, "target_eye_lower_lid_luma");
        filterOrientedUpperLidCenterUniform = GLES20.glGetUniformLocation(mProgramHandle, "oriented_upper_lid_center");
        filterOrientedLowerLidCenterUniform = GLES20.glGetUniformLocation(mProgramHandle, "oriented_lower_lid_center");
        filterSimilarityOriginUniform = GLES20.glGetUniformLocation(mProgramHandle, "similarity_origin");
        filterSimilarityShiftUniform = GLES20.glGetUniformLocation(mProgramHandle, "similarity_shift");
        filterSimilarityScaleUniform = GLES20.glGetUniformLocation(mProgramHandle, "similarity_scale");

        filterTopSplineTransformSrcDstCenterUniform = GLES20.glGetUniformLocation(mProgramHandle, "top_spline_transform_src_dst_center");
        filterTopLeftSplineTransformParabolicCoeffUniform = GLES20.glGetUniformLocation(mProgramHandle, "top_left_spline_transform_src_dst_aligned_parabolic_coeff");
        filterTopRightSplineTransformParabolicCoeffUniform = GLES20.glGetUniformLocation(mProgramHandle, "top_right_spline_transform_src_dst_aligned_parabolic_coeff");
        filterBottomSplineTransformSrcDstCenterUniform = GLES20.glGetUniformLocation(mProgramHandle, "bottom_spline_transform_src_dst_center");
        filterBottomLeftSplineTransformParabolicCoeffUniform = GLES20.glGetUniformLocation(mProgramHandle, "bottom_left_spline_transform_src_dst_aligned_parabolic_coeff");
        filterBottomRightSplineTransformParabolicCoeffUniform = GLES20.glGetUniformLocation(mProgramHandle, "bottom_right_spline_transform_src_dst_aligned_parabolic_coeff");

        filterEyeLinerTemplateColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyeliner_template_color");
        filterEyeLashTemplateColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyelash_template_color");
        filterEnableEyeShadowUniform = GLES20.glGetUniformLocation(mProgramHandle, "enable_eyeshadow");
        filterEnableEyeLinerUniform = GLES20.glGetUniformLocation(mProgramHandle, "enable_eyeliner");
        filterEnableEyeLashUniform = GLES20.glGetUniformLocation(mProgramHandle, "enable_eyelash");
        filterEnvironmentLumaUniform = GLES20.glGetUniformLocation(mProgramHandle, "environment_luma");
        filterEyeROIUniform = GLES20.glGetUniformLocation(mProgramHandle, "roi");
        filterMinColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "min_color");
        filterMaxColorUniform = GLES20.glGetUniformLocation(mProgramHandle, "max_color");

        filterUpperLidEyeLashYScaleAdjusterUniform = GLES20.glGetUniformLocation(mProgramHandle, "upper_lid_eyelash_y_scale_adjuster");
        filterShimmerModelScaleUniform = GLES20.glGetUniformLocation(mProgramHandle, "shimmer_model_scale");
        filterMaxLumaUniform = GLES20.glGetUniformLocation(mProgramHandle, "max_luma");
        filterLumaRangeUniform = GLES20.glGetUniformLocation(mProgramHandle, "luma_range");

        filterBrightTexture_0_Uniform = GLES20.glGetUniformLocation(mProgramHandle, "bright0_texture");
        filterBrightTexture_1_Uniform = GLES20.glGetUniformLocation(mProgramHandle, "glitter0_texture");
        filterGlitterTexture_0_Uniform = GLES20.glGetUniformLocation(mProgramHandle, "bright1_texture");
        filterGlitterTexture_1_Uniform = GLES20.glGetUniformLocation(mProgramHandle, "glitter1_texture");

        filterLeftRightCornerToTopCenterSquareUniform = GLES20.glGetUniformLocation(mProgramHandle, "oriented_target_eye_left_right_corner_to_top_center_square");
        filterLeftRightCornerToBottomCenterSquareUniform = GLES20.glGetUniformLocation(mProgramHandle, "oriented_target_eye_left_right_corner_to_bottom_center_square");
        filterActualTopLeftRightParabolicUniform = GLES20.glGetUniformLocation(mProgramHandle, "actual_top_left_right_parabolic");
        filterActualBottomLeftRightParabolicUniform = GLES20.glGetUniformLocation(mProgramHandle, "actual_bottom_left_right_parabolic");

        filterEyeShadowBlendTypeUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyeShadowBlendType");
        filterEyeShadowStrengthUniform = GLES20.glGetUniformLocation(mProgramHandle, "eyeShadowStrength");
    }

    public boolean setMakeUpRes(RealTimeMakeUpSubRes realTimeMakeUpSubRes) {
        boolean flag = super.setMakeUpRes(realTimeMakeUpSubRes);
        if (flag && mRealTimeMakeUpSubRes != null && mResIsChange) {
            if (mRealTimeMakeUpSubRes.mNeedReset) {
                mLashId = 0;
                mLineId = 0;
                mShadowId = 0;
            }
            //0 1 2
            int textureId = mRealTimeMakeUpSubRes.getTextureId(0);
            if (textureId == 0) {
                initTask(0, mRealTimeMakeUpSubRes.mEyeLash);//睫毛
            } else {
                mTempTextureId[0] = textureId;
            }
            textureId = mRealTimeMakeUpSubRes.getTextureId(1);
            if (textureId == 0) {
                initTask(1, mRealTimeMakeUpSubRes.mEyeLine);//眼线
            } else {
                mTempTextureId[1] = textureId;
            }
            textureId = mRealTimeMakeUpSubRes.getTextureId(2);
            if (textureId == 0) {
                initTask(2, mRealTimeMakeUpSubRes.mEyeShadow);//眼影
            } else {
                mTempTextureId[2] = textureId;
            }
        }
        return true;
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        loadTexture();
        bindTexture(textureId);

        onPreDraw();
        bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);

        if ((mLashId > 0 || mLineId > 0 || mShadowId > 0) &&
                mPocoFace != null && mPocoFace.points_count > 0 && mResWidth > 0 && mResHeight > 0) {
            mIsRightEye = false;//左
            drawEye(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

            mIsRightEye = true;//右
            int framebufferId = textureId;
            if (mGLFramebuffer != null) {
                mGLFramebuffer.bindNext(true);
                framebufferId = mGLFramebuffer.getPreviousTextureId();

                GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
                GLES20.glBindTexture(getTextureTarget(), framebufferId);
                GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);
            }
            bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
            drawEye(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, framebufferId, texStride);
        }
        mUseOtherFaceData = false;
        mPocoFace = null;

        onAfterDraw();
        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    public void loadTexture() {
        int size = getTaskSize();
        if (size > 0) {
            runTask();
        }
        if (size == 0 && mResIsChange && mTempTextureId != null && mRealTimeMakeUpSubRes != null) {
            mResIsChange = false;
            mLashId = mTempTextureId[0];
            mLineId = mTempTextureId[1];
            mShadowId = mTempTextureId[2];

            mRealTimeMakeUpSubRes.setTextureId(0, mLashId);
            mRealTimeMakeUpSubRes.setTextureId(1, mLineId);
            mRealTimeMakeUpSubRes.setTextureId(2, mShadowId);

            mLashColor = mRealTimeMakeUpSubRes.mEyeLashColor;
            mLineColor = mRealTimeMakeUpSubRes.mEyeLineColor;
            if (mRealTimeMakeUpSubRes.mEyeShadowBlendType > 0) {
                mEyeShadowBlendType = mRealTimeMakeUpSubRes.mEyeShadowBlendType;
            }
            mEyeShadowStrength = mRealTimeMakeUpSubRes.mEyeShadowOpaqueness;
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(getTextureTarget(), textureId);
        GLES20.glUniform1i(filterBGCosmeticTextureUniform, 0);

        if (mShadowId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mShadowId);
            GLES20.glUniform1i(filterEyeShadowTextureUniform, 1);

            GLES20.glUniform1i(filterEnableEyeShadowUniform, 1);
        } else {
            GLES20.glUniform1i(filterEnableEyeShadowUniform, 0);
        }

        if (mLashId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 2);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLashId);
            GLES20.glUniform1i(filterEyeLashTextureUniform, 2);

            GLES20.glUniform1i(filterEnableEyeLashUniform, 1);
        } else {
            GLES20.glUniform1i(filterEnableEyeLashUniform, 0);
        }

        if (mLineId > 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLineId);
            GLES20.glUniform1i(filterEyeLinerTextureUniform, 3);

            GLES20.glUniform1i(filterEnableEyeLinerUniform, 1);
        } else {
            GLES20.glUniform1i(filterEnableEyeLinerUniform, 0);
        }
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        //镜头画面
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, texStride, texBuffer);
        GLES20.glUniform1f(maTextureUnitsId, 0.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

    }

    private void drawEye(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        renderROI = new RectF(0, 0, mResWidth, mResHeight);

        float standard_template_y_scale_factor = 2.0f;//图片尺寸3/4的话是这个比例，所以下面也要求buffer要是3/4的大小
        standardParabolicPointsInPic[0] = new PointF(standard_eye_material_points[0].x * mResWidth, standard_eye_material_points[0].y * mResHeight * standard_template_y_scale_factor);
        standardParabolicPointsInPic[1] = new PointF(standard_eye_material_points[1].x * mResWidth, standard_eye_material_points[1].y * mResHeight * standard_template_y_scale_factor);
        standardParabolicPointsInPic[2] = new PointF(standard_eye_material_points[2].x * mResWidth, standard_eye_material_points[2].y * mResHeight * standard_template_y_scale_factor);
        standardParabolicPointsInPic[3] = new PointF(standard_eye_material_points[3].x * mResWidth, standard_eye_material_points[3].y * mResHeight * standard_template_y_scale_factor);

        for (int i = 0; i < mPocoFace.points_count; i++) {
            facesFeaturesCoordinates[2 * i] = mPocoFace.points_array[i].x;
            facesFeaturesCoordinates[2 * i + 1] = mPocoFace.points_array[i].y;
        }

        PointF left_eye_pic = new PointF(mPocoFace.points_array[Sslandmarks.lEyeCenter].x, mPocoFace.points_array[Sslandmarks.lEyeCenter].y);
        PointF right_eye_pic = new PointF(mPocoFace.points_array[Sslandmarks.rEyeCenter].x, mPocoFace.points_array[Sslandmarks.rEyeCenter].y);

        left_eye_pic.x = left_eye_pic.x * mWidth;
        left_eye_pic.y = (1.0f - left_eye_pic.y) * mHeight;
        right_eye_pic.x = right_eye_pic.x * mWidth;
        right_eye_pic.y = (1.0f - right_eye_pic.y) * mHeight;
        currentFaceOrientation = PocoFaceOrientation.enquirySimilarityFaceOrientation(left_eye_pic, right_eye_pic);

        if (mIsRightEye) {
            //右眼
            meyePoints[0] = new PointF(mPocoFace.points_array[Sslandmarks.rEyeOuter].x, mPocoFace.points_array[Sslandmarks.rEyeOuter].y);
            meyePoints[1] = new PointF(mPocoFace.points_array[Sslandmarks.rEyeTop].x, mPocoFace.points_array[Sslandmarks.rEyeTop].y);
            meyePoints[2] = new PointF(mPocoFace.points_array[Sslandmarks.rEyeInner].x, mPocoFace.points_array[Sslandmarks.rEyeInner].y);
            meyePoints[3] = new PointF(mPocoFace.points_array[Sslandmarks.rEyeBot].x, mPocoFace.points_array[Sslandmarks.rEyeBot].y);

            for (int m = 0; m < 4; m++) {
                meyePoints[m].x = (1.0f - meyePoints[m].x);
                meyePoints[m].y = (meyePoints[m].y);
            }

            meyePointCenter = new PointF(mPocoFace.points_array[Sslandmarks.rEyeCenter].x, mPocoFace.points_array[Sslandmarks.rEyeCenter].y);


            for (int i = 0; i < 4; i++) {

                t_mEyePoints[i] = rotateEyePointsToFitHeadCoordinate(new PointF(1.0f - meyePoints[i].x, meyePoints[i].y), currentFaceOrientation);
                t_mEyePoints[i].x = 1.0f - t_mEyePoints[i].x;
            }

            t_mEyePointCenter = rotateEyePointsToFitHeadCoordinate(new PointF(1.0f - meyePointCenter.x, meyePointCenter.y), currentFaceOrientation);
            t_mEyePointCenter.x = 1.0f - t_mEyePointCenter.x;


        } else {

            //左眼
            meyePoints[0] = new PointF(mPocoFace.points_array[Sslandmarks.lEyeOuter].x, mPocoFace.points_array[Sslandmarks.lEyeOuter].y);
            meyePoints[1] = new PointF(mPocoFace.points_array[Sslandmarks.lEyeTop].x, mPocoFace.points_array[Sslandmarks.lEyeTop].y);
            meyePoints[2] = new PointF(mPocoFace.points_array[Sslandmarks.lEyeInner].x, mPocoFace.points_array[Sslandmarks.lEyeInner].y);
            meyePoints[3] = new PointF(mPocoFace.points_array[Sslandmarks.lEyeBot].x, mPocoFace.points_array[Sslandmarks.lEyeBot].y);

            for (int m = 0; m < 4; m++) {
                meyePoints[m].x = (meyePoints[m].x);
                meyePoints[m].y = (meyePoints[m].y);
            }
            meyePointCenter = new PointF(mPocoFace.points_array[Sslandmarks.lEyeCenter].x, mPocoFace.points_array[Sslandmarks.lEyeCenter].y);

            for (int i = 0; i < 4; i++) {

                t_mEyePoints[i] = rotateEyePointsToFitHeadCoordinate(meyePoints[i], currentFaceOrientation);
            }
            t_mEyePointCenter = rotateEyePointsToFitHeadCoordinate(meyePointCenter, currentFaceOrientation);
        }

        meyePointCenter.x = (1.0f - meyePointCenter.x);

        size_width = mCameraWidth;
        size_height = mCameraHeight;
        if (PocoFaceOrientation.PORSFaceOrientationLeft == currentFaceOrientation || PocoFaceOrientation.PORSFaceOrientationRight == currentFaceOrientation) {
            size_width = mCameraHeight;
            size_height = mCameraWidth;
        }

        RectF clipBufferRect = new RectF();

        updateEyeFilterGLParams(clipBufferRect, t_mEyePoints, t_mEyePointCenter, mIsRightEye, mLineColor, mLashColor, size_width, size_height);


        RectF normal_buffer_clip_rect = new RectF(clipBufferRect.left / mCameraWidth, clipBufferRect.top / mCameraHeight,
                clipBufferRect.right / mCameraWidth, clipBufferRect.bottom / mCameraHeight);

        if (PocoFaceOrientation.PORSFaceOrientationLeft == currentFaceOrientation) {

            normal_buffer_clip_rect = new RectF(clipBufferRect.top / mCameraWidth, (mCameraHeight - clipBufferRect.right) / mCameraHeight,
                    clipBufferRect.bottom / mCameraWidth, (mCameraHeight - clipBufferRect.left) / mCameraHeight);
        } else if (PocoFaceOrientation.PORSFaceOrientationRight == currentFaceOrientation) {

            normal_buffer_clip_rect = new RectF((mCameraWidth - clipBufferRect.bottom) / mCameraWidth, clipBufferRect.left / mCameraHeight,
                    (mCameraWidth - clipBufferRect.top) / mCameraWidth, clipBufferRect.right / mCameraHeight);
        } else if (PocoFaceOrientation.PORSFaceOrientationDown == currentFaceOrientation) {

            normal_buffer_clip_rect = new RectF((mCameraWidth - clipBufferRect.right) / mCameraWidth, (mCameraHeight - clipBufferRect.bottom) / mCameraHeight,
                    (mCameraWidth - clipBufferRect.left) / mCameraWidth, (mCameraHeight - clipBufferRect.top) / mCameraHeight);
        }

        float[] noRotationClipBufferTextureCoordinates = {
                normal_buffer_clip_rect.left, 1.0f - normal_buffer_clip_rect.bottom,
                normal_buffer_clip_rect.right, 1.0f - normal_buffer_clip_rect.bottom,
                normal_buffer_clip_rect.left, 1.0f - normal_buffer_clip_rect.top,
                normal_buffer_clip_rect.right, 1.0f - normal_buffer_clip_rect.top,
        };

        float[] clipBufferimageVertices = new float[8];
        for (int m = 0; m < 8; m++) {
            clipBufferimageVertices[m] = (float) (noRotationClipBufferTextureCoordinates[m] * 2.0 - 1.0);
        }

        if (clipBuffer == null) {
            ByteBuffer bb2 = ByteBuffer.allocateDirect(clipBufferimageVertices.length * 4);
            bb2.order(ByteOrder.nativeOrder());
            clipBuffer = bb2.asFloatBuffer();
        }
        clipBuffer.clear();
        clipBuffer.put(clipBufferimageVertices);
        clipBuffer.position(0);

        if (noRotationClipBuffer == null) {
            ByteBuffer bb = ByteBuffer.allocateDirect(noRotationClipBufferTextureCoordinates.length * 4);
            bb.order(ByteOrder.nativeOrder());
            noRotationClipBuffer = bb.asFloatBuffer();
        }
        noRotationClipBuffer.clear();
        noRotationClipBuffer.put(noRotationClipBufferTextureCoordinates);
        noRotationClipBuffer.position(0);

        if (noRotationBuffer == null || currentFaceOrientation != mFaceOrientation) {
            float[] noRotationTextureCoordinates = {0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f};
            if (PocoFaceOrientation.PORSFaceOrientationLeft == currentFaceOrientation) {
                noRotationTextureCoordinates[0] = 0.f;
                noRotationTextureCoordinates[1] = 0.f;
                noRotationTextureCoordinates[2] = 0.f;
                noRotationTextureCoordinates[3] = 1.f;
                noRotationTextureCoordinates[4] = 1.f;
                noRotationTextureCoordinates[5] = 0.f;
                noRotationTextureCoordinates[6] = 1.f;
                noRotationTextureCoordinates[7] = 1.f;
            } else if (PocoFaceOrientation.PORSFaceOrientationRight == currentFaceOrientation) {
                noRotationTextureCoordinates[0] = 1.f;
                noRotationTextureCoordinates[1] = 1.f;
                noRotationTextureCoordinates[2] = 1.f;
                noRotationTextureCoordinates[3] = 0.f;
                noRotationTextureCoordinates[4] = 0.f;
                noRotationTextureCoordinates[5] = 1.f;
                noRotationTextureCoordinates[6] = 0.f;
                noRotationTextureCoordinates[7] = 0.f;
            } else if (PocoFaceOrientation.PORSFaceOrientationDown == currentFaceOrientation) {
                noRotationTextureCoordinates[0] = 1.f;
                noRotationTextureCoordinates[1] = 0.f;
                noRotationTextureCoordinates[2] = 0.f;
                noRotationTextureCoordinates[3] = 0.f;
                noRotationTextureCoordinates[4] = 1.f;
                noRotationTextureCoordinates[5] = 1.f;
                noRotationTextureCoordinates[6] = 0.f;
                noRotationTextureCoordinates[7] = 1.f;
            }

            if (noRotationBuffer == null) {
                ByteBuffer bb1 = ByteBuffer.allocateDirect(noRotationTextureCoordinates.length * 4);
                bb1.order(ByteOrder.nativeOrder());
                noRotationBuffer = bb1.asFloatBuffer();
            }
            noRotationBuffer.clear();
            noRotationBuffer.put(noRotationTextureCoordinates);
            mFaceOrientation = currentFaceOrientation;
        }
        noRotationBuffer.position(0);

        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glEnableVertexAttribArray(eyeMaterialTextureCoordinateAttribute);

        GLES20.glVertexAttribPointer(maPositionLoc, 2, GLES20.GL_FLOAT, false, 0, clipBuffer);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2, GLES20.GL_FLOAT, false, 0, noRotationClipBuffer);
        GLES20.glVertexAttribPointer(eyeMaterialTextureCoordinateAttribute, 2, GLES20.GL_FLOAT, false, 0, noRotationBuffer);
        GLES20.glUniform1f(maTextureUnitsId, 1.0f);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
//            }
//        } else {
//            Arrays.fill(facesFeaturesCoordinates, 0);
//        }
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {

    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }


    @Override
    public void releaseProgram() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;

        GLES20.glDeleteTextures(3, new int[]{mLashId, mLineId, mShadowId}, 0);
    }

    protected PointF calculateEyeOrientaCosSin(PointF src, PointF dst, float width, float height) {
        PointF ret = new PointF();
        PointF srcInBuffer = new PointF(src.x * width, src.y * height);
        PointF dstInBuffer = new PointF(dst.x * width, dst.y * height);

        double x_y_pow_add = (dstInBuffer.x - srcInBuffer.x) * (dstInBuffer.x - srcInBuffer.x) + (dstInBuffer.y - srcInBuffer.y) * (dstInBuffer.y - srcInBuffer.y);
//                Math.pow(dstInBuffer.x - srcInBuffer.x, 2.0) + Math.pow(dstInBuffer.y - srcInBuffer.y, 2.0);
        x_y_pow_add = Math.sqrt(x_y_pow_add);
        double Cos = Math.abs(dstInBuffer.x - srcInBuffer.x) / x_y_pow_add;
        double Sin = (srcInBuffer.y - dstInBuffer.y) / x_y_pow_add;

        ret.x = (float) Cos;
        ret.y = (float) Sin;
        return ret;
    }

    protected PointF calculateLevelOrientatedPoint(PointF src, PointF thetaCosSin, float width, float height) {
        PointF ret = new PointF();

        PointF srcInBuffer = new PointF(src.x * width, src.y * height);
        double orientatedX = thetaCosSin.x * srcInBuffer.x - thetaCosSin.y * srcInBuffer.y;
        double orientatedY = thetaCosSin.y * srcInBuffer.x + thetaCosSin.x * srcInBuffer.y;

        ret.x = (float) orientatedX / width;
        ret.y = (float) orientatedY / height;
        return ret;
    }

    protected PointF calculateSplineTransformAlignedCoeff(PointF src, PointF center, PointF polar) {
        float C = polar.y - center.y;
        float C_ = -C;

        float dis = src.x - center.x;
        float X_2 = dis * dis;

        float A = C_ / X_2;

        return new PointF(A, C);
    }

    protected float distanceOfPoint(PointF src, PointF dst) {
        return (float) Math.sqrt((src.x - dst.x) * (src.x - dst.x) + (src.y - dst.y) * (src.y - dst.y));
    }

    //RGBA
    /*
    *filpLeftRight    左眼 false  右眼 true
    * 眼线颜色
    * 睫毛颜色
    * */
    private void updateEyeFilterGLParams(RectF bufferClipRenderRect, PointF[] eyePoints, PointF eyecenter, boolean filpLeftRight, int eyeLineColor, int eyeLashColor, int width, int height) {
        int color_r = (eyeLineColor >> 24) & 0xff;
        int color_g = (eyeLineColor >> 16) & 0xff;
        int color_b = (eyeLineColor >> 8) & 0xff;

        GLES20.glUniform3f(filterEyeLinerTemplateColorUniform, color_r / 255.0f, color_g / 255.0f, color_b / 255.0f);
        color_r = (eyeLashColor >> 24) & 0xff;
        color_g = (eyeLashColor >> 16) & 0xff;
        color_b = (eyeLashColor >> 8) & 0xff;

        GLES20.glUniform3f(filterEyeLashTemplateColorUniform, color_r / 255.0f, color_g / 255.0f, color_b / 255.0f);

        if (filpLeftRight) {
            GLES20.glUniform1f(filterEyePositionLeftRightFlipUniform, 1.0f);
        } else {
            GLES20.glUniform1f(filterEyePositionLeftRightFlipUniform, 0.0f);
        }

        float _4_3_clip_scale_height = width * 4.0f / 3.0f;

        float clipBufferSize_width = width;
        float clipBufferSize_height = _4_3_clip_scale_height;

        GLES20.glUniform2f(filterBufferSizeUniform, clipBufferSize_width, clipBufferSize_height);

        mBufferRenderClipCenterPoint = new PointF(facesFeaturesCoordinates[43 * 2], facesFeaturesCoordinates[43 * 2 + 1]);

        //已43点为y轴中心裁4／3
        PointF _43_p = mBufferRenderClipCenterPoint;

        float clip_y_offset = _43_p.y * height - clipBufferSize_height / 2.0f;

//        RectF clipBufferRect = new RectF(0.0f, clip_y_offset, clipBufferSize_width, clipBufferSize_height);
        RectF clipBufferRect = new RectF(0.0f, clip_y_offset, clipBufferSize_width, clip_y_offset + clipBufferSize_height);

//        bufferClipRenderRect = new RectF(clipBufferRect);
        bufferClipRenderRect.left = clipBufferRect.left;
        bufferClipRenderRect.right = clipBufferRect.right;
        bufferClipRenderRect.top = clipBufferRect.top;
        bufferClipRenderRect.bottom = clipBufferRect.bottom;

        PointF[] adjust_y_eye_point = new PointF[4];
        for (int i = 0; i < 4; i++) {
            adjust_y_eye_point[i] = new PointF();
            adjust_y_eye_point[i].x = eyePoints[i].x;
            adjust_y_eye_point[i].y = (eyePoints[i].y * height - clip_y_offset) / clipBufferSize_height;
        }
//        System.out.println("clip_y_offset:"+clip_y_offset);

        PointF EyeCosSin = calculateEyeOrientaCosSin(adjust_y_eye_point[0], adjust_y_eye_point[2], clipBufferSize_width, clipBufferSize_height);

//        EyeCosSin.x = 1.f;
//        EyeCosSin.y = 0.f;


        float f1 = mResHeight * clipBufferSize_width / (mResWidth * clipBufferSize_height);

        GLES20.glUniform2f(filterLevelOrientCosSinUniform, EyeCosSin.x, EyeCosSin.y);

        float frame_to_template_y_facetor = 1.0f / f1;
        GLES20.glUniform1f(filterFrameTemplateRemappingFactorUniform, frame_to_template_y_facetor);

        if (filpLeftRight) {
            GLES20.glUniform1f(filterTargetEyeLowerLidLumaUniform, mRightEyeLuminance / 255.0f);
        } else {
            GLES20.glUniform1f(filterTargetEyeLowerLidLumaUniform, mLeftEyeLuminance / 255.0f);
        }

        switch (mEyeShadowBlendType) {
            case PocoCompositeOperator.NoCompositeOp: {
                GLES20.glUniform1f(filterEyeShadowBlendTypeUniform, 1.0f);
            }
            break;
            case PocoCompositeOperator.LinearLightCompositeOp: {
                GLES20.glUniform1f(filterEyeShadowBlendTypeUniform, 2.0f);
            }
            break;
            case PocoCompositeOperator.MultiplyCompositeOp: {
                GLES20.glUniform1f(filterEyeShadowBlendTypeUniform, 3.0f);
            }
            break;
        }

        GLES20.glUniform1f(filterEyeShadowStrengthUniform, mEyeShadowStrength);

        PointF EyeOrientatedPoints[] = new PointF[4];
        for (int i = 0; i < 4; i++) {
//            EyeOrientatedPoints[i] = [self calculateLevelOrientatedPoint:adjust_y_eye_point[i] theta:EyeCosSin bufferSize:clipBufferSize];
            EyeOrientatedPoints[i] = calculateLevelOrientatedPoint(adjust_y_eye_point[i], EyeCosSin, clipBufferSize_width, clipBufferSize_height);
        }

        PointF orientedEyeUpCenter = new PointF(EyeOrientatedPoints[1].x, EyeOrientatedPoints[0].y);
        PointF orientedEyeLowCenter = new PointF(EyeOrientatedPoints[3].x, EyeOrientatedPoints[2].y);

        if (EyeOrientatedPoints[3].y <= orientedEyeLowCenter.y) {
            double dis_lip = Math.abs(EyeOrientatedPoints[1].y - EyeOrientatedPoints[3].y);

            EyeOrientatedPoints[3].y = orientedEyeLowCenter.y + (float) dis_lip * 0.1f;
        }
        if (EyeOrientatedPoints[1].y >= orientedEyeUpCenter.y) {
            double dis_lip = Math.abs(EyeOrientatedPoints[1].y - EyeOrientatedPoints[3].y);

            EyeOrientatedPoints[1].y = orientedEyeUpCenter.y - (float) dis_lip * 0.1f;
        }

        GLES20.glUniform2f(filterOrientedUpperLidCenterUniform, orientedEyeUpCenter.x, orientedEyeUpCenter.y);
        GLES20.glUniform2f(filterOrientedLowerLidCenterUniform, orientedEyeLowCenter.x, orientedEyeLowCenter.y);

        GLES20.glUniform2f(filterSimilarityOriginUniform, EyeOrientatedPoints[0].x, EyeOrientatedPoints[0].y);
        PointF similarity_shift = new PointF(standard_eye_material_points[0].x - EyeOrientatedPoints[0].x,
                standard_eye_material_points[0].y - EyeOrientatedPoints[0].y);
        GLES20.glUniform2f(filterSimilarityShiftUniform, similarity_shift.x, similarity_shift.y);
        float similarity_scale = (standard_eye_material_points[2].x - standard_eye_material_points[0].x) /
                (EyeOrientatedPoints[2].x - EyeOrientatedPoints[0].x);
        GLES20.glUniform1f(filterSimilarityScaleUniform, similarity_scale);

        float w = 240.0f;//检测到的数据
        float shimmer_model_scale = (float) Math.max(0.0f, Math.min(1.0, (1.0f / similarity_scale * mResWidth / w - 0.35)) / 0.3f);
        GLES20.glUniform1f(filterShimmerModelScaleUniform, shimmer_model_scale);

        // eye top spline
        float parabolic_polar_transform_top_src_dst_center[] = {orientedEyeUpCenter.x, orientedEyeUpCenter.y,
                standard_eye_material_points[1].x, standard_eye_material_points[0].y};

        GLES20.glUniform4fv(filterTopSplineTransformSrcDstCenterUniform, 1, FloatBuffer.wrap(parabolic_polar_transform_top_src_dst_center));

        PointF standard_oriented_upcenter = new PointF(standard_eye_material_points[1].x, standard_eye_material_points[0].y);

        PointF top_left_spline_transform_src_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(EyeOrientatedPoints[0],
                orientedEyeUpCenter, EyeOrientatedPoints[1]);

        PointF top_left_spline_transform_dst_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(standard_eye_material_points[0],
                standard_oriented_upcenter, standard_eye_material_points[1]);

        GLES20.glUniform4f(filterTopLeftSplineTransformParabolicCoeffUniform, top_left_spline_transform_src_aligned_parabolic_coeff.x,
                top_left_spline_transform_src_aligned_parabolic_coeff.y, top_left_spline_transform_dst_aligned_parabolic_coeff.x,
                top_left_spline_transform_dst_aligned_parabolic_coeff.y);

        PointF top_right_spline_transform_src_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(EyeOrientatedPoints[2],
                orientedEyeUpCenter, EyeOrientatedPoints[1]);

        PointF top_right_spline_transform_dst_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(standard_eye_material_points[2],
                standard_oriented_upcenter, standard_eye_material_points[1]);

        GLES20.glUniform4f(filterTopRightSplineTransformParabolicCoeffUniform, top_right_spline_transform_src_aligned_parabolic_coeff.x,
                top_right_spline_transform_src_aligned_parabolic_coeff.y, top_right_spline_transform_dst_aligned_parabolic_coeff.x,
                top_right_spline_transform_dst_aligned_parabolic_coeff.y);

        // eye bottom spline
        float parabolic_polar_transform_bottom_src_dst_center[] = {orientedEyeLowCenter.x, orientedEyeLowCenter.y,
                standard_eye_material_points[3].x, standard_eye_material_points[2].y};
        GLES20.glUniform4fv(filterBottomSplineTransformSrcDstCenterUniform, 1, FloatBuffer.wrap(parabolic_polar_transform_bottom_src_dst_center));

        PointF standard_oriented_lowcenter = new PointF(standard_eye_material_points[3].x, standard_eye_material_points[2].y);

        PointF bottom_left_spline_transform_src_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(EyeOrientatedPoints[0],
                orientedEyeLowCenter, EyeOrientatedPoints[3]);
        PointF bottom_left_spline_transform_dst_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(standard_eye_material_points[0],
                standard_oriented_lowcenter, standard_eye_material_points[3]);
        GLES20.glUniform4f(filterBottomLeftSplineTransformParabolicCoeffUniform, bottom_left_spline_transform_src_aligned_parabolic_coeff.x,
                bottom_left_spline_transform_src_aligned_parabolic_coeff.y, bottom_left_spline_transform_dst_aligned_parabolic_coeff.x,
                bottom_left_spline_transform_dst_aligned_parabolic_coeff.y);

        PointF bottom_right_spline_transform_src_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(EyeOrientatedPoints[2],
                orientedEyeLowCenter, EyeOrientatedPoints[3]);
        PointF bottom_right_spline_transform_dst_aligned_parabolic_coeff = calculateSplineTransformAlignedCoeff(standard_eye_material_points[2],
                standard_oriented_lowcenter, standard_eye_material_points[3]);

        GLES20.glUniform4f(filterBottomRightSplineTransformParabolicCoeffUniform, bottom_right_spline_transform_src_aligned_parabolic_coeff.x,
                bottom_right_spline_transform_src_aligned_parabolic_coeff.y, bottom_right_spline_transform_dst_aligned_parabolic_coeff.x,
                bottom_right_spline_transform_dst_aligned_parabolic_coeff.y);

        float eyeLashYScaleAdjuster = (float) Math.max(0.2f, Math.min(1.0, top_left_spline_transform_src_aligned_parabolic_coeff.y * similarity_scale /
                top_left_spline_transform_dst_aligned_parabolic_coeff.y * 1.2f));

        GLES20.glUniform1f(filterUpperLidEyeLashYScaleAdjusterUniform, eyeLashYScaleAdjuster);

        //计算roi
        RectF localObject3 = new RectF(renderROI);
        float as[] = new float[4];
        PointF t_as_p = new PointF(standardParabolicPointsInPic[1].x, standardParabolicPointsInPic[0].y);
        as[0] = (t_as_p.x - localObject3.left) / (t_as_p.x - standardParabolicPointsInPic[0].x);
        as[1] = (t_as_p.y - localObject3.top) / (t_as_p.y - standardParabolicPointsInPic[1].y);
        as[2] = (localObject3.width() - t_as_p.x) / (standardParabolicPointsInPic[2].x - t_as_p.x);
        as[3] = (localObject3.height() - t_as_p.y) / (standardParabolicPointsInPic[3].y - t_as_p.y);

        float f2 = (standard_eye_material_points[2].x - standard_eye_material_points[0].x) /
                (EyeOrientatedPoints[2].x - EyeOrientatedPoints[0].x);

        float f3 = Math.min(Math.max(orientedEyeUpCenter.x, orientedEyeLowCenter.x) + as[0] * (EyeOrientatedPoints[0].x - Math.max(orientedEyeUpCenter.x, orientedEyeLowCenter.x)),
                orientedEyeUpCenter.x - (standard_eye_material_points[1].x - localObject3.left / mResWidth) / f2);
        float f4 = Math.max(Math.min(orientedEyeUpCenter.x, orientedEyeLowCenter.x) + as[2] * (EyeOrientatedPoints[2].x - Math.min(orientedEyeUpCenter.x, orientedEyeLowCenter.x)),
                orientedEyeUpCenter.x + (localObject3.width() / mResWidth - standard_eye_material_points[1].x) / f2);
        f1 = Math.min(orientedEyeUpCenter.y + as[1] * (EyeOrientatedPoints[1].y - orientedEyeUpCenter.y),
                orientedEyeUpCenter.y - (standard_eye_material_points[0].y - f1 * (localObject3.top / mResHeight)) / f2);
        float f5 = Math.max(EyeOrientatedPoints[3].y + (standard_eye_material_points[3].y - standard_eye_material_points[0].y) * (as[3] - 1.0f) / f2, EyeOrientatedPoints[3].y);

        GLES20.glUniform4f(filterEyeROIUniform, f3, f4, f1, f5);

        GLES20.glUniform3f(filterMinColorUniform, 0.18f, 0.18f, 0.18f);  //for test
        GLES20.glUniform3f(filterMaxColorUniform, 0.8f, 0.8f, 0.8f);  //for test

        //====================================   曲线矫正   ====================================//
        f3 = orientedEyeUpCenter.x - EyeOrientatedPoints[0].x;
        f2 = EyeOrientatedPoints[2].x - orientedEyeUpCenter.x;
        GLES20.glUniform2f(filterLeftRightCornerToTopCenterSquareUniform, f3 * f3, f2 * f2);

        PointF adjust_up_eye_center = new PointF(eyecenter.x, (eyecenter.y * height - clip_y_offset) / clipBufferSize_height);
        PointF adjust_low_eye_center = new PointF(adjust_up_eye_center.x, adjust_up_eye_center.y);

        PointF actual_eye_upCenter = calculateLevelOrientatedPoint(adjust_up_eye_center, EyeCosSin, clipBufferSize_width, clipBufferSize_height);
        PointF actual_eye_lowCenter = calculateLevelOrientatedPoint(adjust_low_eye_center, EyeCosSin, clipBufferSize_width, clipBufferSize_height);

        float actual_lid_eye_height = distanceOfPoint(actual_eye_upCenter, EyeOrientatedPoints[1]);
        float limitedHeight = distanceOfPoint(EyeOrientatedPoints[0], EyeOrientatedPoints[2]) * 0.12f;//测试值

        double ratio_of_actual_lid_height_to_limited_height = actual_lid_eye_height / limitedHeight;
        f1 = top_left_spline_transform_src_aligned_parabolic_coeff.y;// * (float) Math.min(1.0, ratio_of_actual_lid_height_to_limited_height);   ///for test
        f3 = -1.0f * f1 / (f3 * f3);
        f2 = -1.0f * f1 / (f2 * f2);
        GLES20.glUniform3f(filterActualTopLeftRightParabolicUniform, f3, f1, f2);

        //============================   下眼皮   ============================//
        f3 = orientedEyeLowCenter.x - EyeOrientatedPoints[0].x;
        f2 = EyeOrientatedPoints[2].x - orientedEyeLowCenter.x;
        GLES20.glUniform2f(filterLeftRightCornerToBottomCenterSquareUniform, f3 * f3, f2 * f2);

        actual_lid_eye_height = distanceOfPoint(actual_eye_lowCenter, EyeOrientatedPoints[3]);
        ratio_of_actual_lid_height_to_limited_height = actual_lid_eye_height / limitedHeight;
        f1 = bottom_left_spline_transform_src_aligned_parabolic_coeff.y;// * (float) Math.min(1.0, ratio_of_actual_lid_height_to_limited_height);///for test
        f3 = -1.0f * f1 / (f3 * f3);
        f2 = -1.0f * f1 / (f2 * f2);
        GLES20.glUniform3f(filterActualBottomLeftRightParabolicUniform, f3, f1, f2);
    }

    private PointF rotateEyePointsToFitHeadCoordinate(PointF src, int orientation) {
        PointF rotateP = new PointF(src.x, src.y);

        switch (orientation) {
            case PocoFaceOrientation.PORSFaceOrientationLeft: {
                rotateP.x = 1.0f - src.y;
                rotateP.y = src.x;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationRight: {
                rotateP.x = src.y;
                rotateP.y = 1.0f - src.x;
            }
            break;
            case PocoFaceOrientation.PORSFaceOrientationDown: {
                rotateP.x = 1.0f - src.x;
                rotateP.y = 1.0f - src.y;
            }
            break;
        }

        return rotateP;
    }
}
