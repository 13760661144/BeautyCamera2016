package cn.poco.gldraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.support.annotation.FloatRange;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.poco.glfilter.sticker.Sticker3dFilter;
import cn.poco.glfilter.DisplayFilter;
import cn.poco.glfilter.VideoTextureFilter;
import cn.poco.glfilter.base.AbstractFilter;
import cn.poco.glfilter.base.Drawable2d;
import cn.poco.glfilter.base.GLFramebuffer;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.base.TextureRotationUtils;
import cn.poco.glfilter.beauty.BeautyFilterGroup;
import cn.poco.glfilter.camera.CameraFilterGroup;
import cn.poco.glfilter.color.ColorFilterGroup;
import cn.poco.glfilter.composite.CompositeFilterGroup;
import cn.poco.glfilter.makeup.MakeUpFilterGroup;
import cn.poco.glfilter.shape.NarrowFaceBigEyeFilter;
import cn.poco.glfilter.shape.ShapeFilterGroup;
import cn.poco.glfilter.shape.ShrinkNoseFilter;
import cn.poco.glfilter.shape.WhitenTeethFilter;
import cn.poco.glfilter.sticker.OnDrawStickerResListener;
import cn.poco.resource.FilterRes;

/**
 * Created by zwq on 2017/02/22 12:08.<br/><br/>
 * 在 FullFrameRectV2 的基础上进行优化：
 * 1.根据FilterDrawOrder的顺序进行绘制；
 * 2.使用Framebuffer减少录制时的绘制次数
 */
public class FullFrameRectV3 {

    private static final String TAG = "bbb";
    private final Drawable2d mRectDrawable = new Drawable2d();
    private float[] IDENTITY_MATRIX = new float[16];
    private Context mContext;
    private int VIDEO_WIDTH;
    private int VIDEO_HEIGHT;
    private int mViewportWidth, mViewportHeight;
    private int mViewportXoff, mViewportYoff;

    private HashMap<Integer, AbstractFilter> mAllFilterCache;
    private CameraFilterGroup mCameraFilterGroup;
    private BeautyFilterGroup mBeautyFilterGroup;
    private ColorFilterGroup mColorFilterGroup;//颜色滤镜组
    private CompositeFilterGroup mCompositeFilterGroup;//混合模式
    private ShapeFilterGroup mShapeFilterGroup;//变形滤镜组
    private MakeUpFilterGroup mMakeUpFilterGroup;//彩妆

    private boolean useCameraYUV;//使用镜头YUV数据渲染画面
    private boolean mBeautyEnable;
    private boolean mColorEnable;
    private boolean mFaceAdjustEnable = true;
    private final boolean mColorFilterEnable = true;
    private final boolean mShapeFilterEnable = true;
    private boolean mStickerEnable;
    private boolean mShapeEnable = true;
    private boolean mVideoTextureEnable = false;

    private boolean mIsStickerMode;

    private int mVideoTextureId;
    private float[] mVideoSTMatrix;

    /**
     * 画面旋转
     */
    private FloatBuffer mGLTextureFlipBuffer;
    private FloatBuffer mGLTextureFlipBuffer2;
    private FloatBuffer mYUVGLTextureFlipBuffer;

    private boolean mBlendEnable;
    private Integer mCurrentLayer = 0;
    private Integer mNextLayer = 0;
    private AbstractFilter mCurrentFilter;
    private AbstractFilter mNextFilter;

    private int mWidth, mHeight;
    private float mViewHWRatio;
    private float mLastViewHWRatio;
    private int InvalidFrameCount;
    private int mCameraWidth, mCameraHeight;
    private boolean mIsInitFramebuffer;
    private GLFramebuffer mGLFramebuffer;
    private boolean hasBindFramebuffer;

    private boolean mIsRecord;
    private boolean mIsEnding;
    private int mEndingFrameCount;
    private Bitmap mLastFrame;
    private boolean mIsDrawCache;

    private boolean mBindRecordFramebufferEnable = true;
    private GLFramebuffer mRecordGLFramebuffer;
    private boolean mBindRecordFramebuffer;

    public FullFrameRectV3(Context context) {
        super();
        mContext = context;
        mAllFilterCache = new HashMap<Integer, AbstractFilter>();
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    protected void addToCache(Integer layer, AbstractFilter filter) {
        if (mAllFilterCache != null) {
            mAllFilterCache.put(layer, filter);
        }
    }

    protected AbstractFilter getFilterByLayer(Integer layer) {
        if (mAllFilterCache == null) {
            return null;
        }
        return mAllFilterCache.get(layer);
    }

    protected boolean hasCache(Integer layer) {
        return getFilterByLayer(layer) == null ? false : true;
    }

    protected boolean setFilterEnableByLayer(Integer layer, boolean enable) {
        AbstractFilter filter = getFilterByLayer(layer);
        if (filter != null) {
            filter.setFilterEnable(enable);
            return true;
        }
        return false;
    }

    public void initFilter() {
        AbstractFilter filter = null;
        if (mCameraFilterGroup == null) {
            try {
                mCameraFilterGroup = new CameraFilterGroup(mContext);
                if (mCameraFilterGroup != null) {
                    mCameraFilterGroup.changeFilterById(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mCameraFilterGroup = null;
            }
        }
        if (mBeautyFilterGroup == null) {
            try {
                mBeautyFilterGroup = new BeautyFilterGroup(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mBeautyFilterGroup = null;
            }
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE);
        if (filter == null) {
            try {
                filter = new NarrowFaceBigEyeFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_SHRINK_NOSE);
        if (filter == null) {
            try {
                filter = new ShrinkNoseFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_SHRINK_NOSE, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH);
        if (filter == null) {
            try {
                filter = new WhitenTeethFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_WHITEN_TEETH, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mColorFilterEnable && mColorFilterGroup == null) {
            try {
                mColorFilterGroup = new ColorFilterGroup(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mColorFilterGroup = null;
            }
        }
        if (mCompositeFilterGroup == null) {
            try {
                mCompositeFilterGroup = new CompositeFilterGroup(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mCompositeFilterGroup = null;
            }
        }
        if (mMakeUpFilterGroup == null) {
            try {
                mMakeUpFilterGroup = new MakeUpFilterGroup(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mMakeUpFilterGroup = null;
            }
        }
//        initStickerFilter();

        if (mShapeFilterEnable && mShapeFilterGroup == null) {
            try {
                mShapeFilterGroup = new ShapeFilterGroup(mContext);
            } catch (Exception e) {
                e.printStackTrace();
                mShapeFilterGroup = null;
            }
        }
//        filter = getFilterByLayer(FilterDrawOrder.FILTER_VIDEO_TEXTURE);
//        if (filter == null) {
//            try {//视频纹理
//                filter = new VideoTextureFilter(mContext);
//                addToCache(FilterDrawOrder.FILTER_VIDEO_TEXTURE, filter);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

//        filter = getFilterByLayer(FilterDrawOrder.FILTER_SHOW_FACE_POINTS);
//        if (filter == null) {
//            try {//显示人脸的点用于调试
//                filter = new FacePointFilter(mContext);
//                addToCache(FilterDrawOrder.FILTER_SHOW_FACE_POINTS, filter);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        filter = getFilterByLayer(FilterDrawOrder.FILTER_DISPLAY);
        if (filter == null) {
            try {//左下角水印
                filter = new DisplayFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_DISPLAY, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        try {
//            filter = new EndingFilter(mContext);
//            addToCache(FilterDrawOrder.FILTER_ENDING, filter);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        filter = null;
    }

    private void initStickerFilter() {
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_STICKER);
        if (filter == null) {
            try {
                filter = new Sticker3dFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_STICKER, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (filter != null) {
                filter.setViewSize(mWidth, mHeight);
            }
        }
    }

    /**
     * Creates a texture object suitable for use with drawFrame().
     */
    public int createTexture() {
        return GlUtil.createTexture(getTextureTarget());
    }

    public int createTexture(Bitmap bitmap) {
        return GlUtil.createTexture(getTextureTarget(), bitmap);
    }

    private int getTextureTarget() {
        return GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
    }

    public void scaleMVPMatrix(float x, float y) {
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
        Matrix.scaleM(IDENTITY_MATRIX, 0, x, y, 1f);
    }

    public void setMVPMatrix(float[] mvpMatrix) {
        IDENTITY_MATRIX = mvpMatrix;
    }

    /**
     * width < height
     *
     * @param width
     * @param height
     */
    public void setVideoSize(int width, int height, int topPadding) {
        if (width > 0 && height > 0) {
            if (width != VIDEO_WIDTH || height != VIDEO_HEIGHT) {
                VIDEO_WIDTH = width;
                VIDEO_HEIGHT = height;
                calculateViewport(1, topPadding, mWidth, mHeight, VIDEO_WIDTH, VIDEO_HEIGHT);
            }
        }
    }

    public void setVideoSize(int width, int height) {
        setVideoSize(width, height, 0);
    }

    /**
     * @param type        当视频比例与UI比例不同时的处理方式，0:缩放处理，1:裁剪处理
     * @param top         顶部偏移量，当视频比例小于UI比例时有效
     * @param viewWidth
     * @param viewHeight
     * @param videoWidth
     * @param videoHeight
     */
    private void calculateViewport(int type, float top, int viewWidth, int viewHeight, int videoWidth, int videoHeight) {
        if (viewWidth <= 0) {
            mViewportXoff = 0;
            mViewportYoff = 0;
            mViewportWidth = videoWidth;
            mViewportHeight = videoHeight;
            return;
        }
        float viewRatio = viewHeight * 1.0f / viewWidth;
        int topOffset = 0;
        if (type == 1) {
            //裁剪处理
            if (videoHeight > (int) (videoWidth * viewRatio)) {//videoRatio > viewRatio
                mViewportWidth = (int) (videoWidth * viewRatio);
                mViewportHeight = videoHeight;
            } else {
                mViewportWidth = videoWidth;
                mViewportHeight = (int) (videoWidth * viewRatio);
                float maxTopOffset = viewHeight - (viewWidth * (videoHeight * 1.0f / videoWidth));
                if (top > -1 && top <= maxTopOffset) {
                    topOffset = (int) ((-maxTopOffset / 2 + top) / viewHeight * mViewportHeight);
//                    Log.i(TAG, "calculateViewport: " + top + ", " + topOffset);
                }
            }
        } else {
            //缩放处理
            if (videoHeight > (int) (videoWidth * viewRatio)) {
                mViewportWidth = videoWidth;
                mViewportHeight = (int) (videoWidth * viewRatio);
            } else {
                mViewportWidth = (int) (videoHeight / viewRatio);
                mViewportHeight = videoHeight;
            }
        }
        mViewportXoff = (videoWidth - mViewportWidth) / 2;
        mViewportYoff = (videoHeight - mViewportHeight) / 2 + topOffset;
//        Log.i(TAG, "calculateViewport: " + mViewportXoff + ", " + mViewportYoff + ", " + mViewportWidth + ", " + mViewportHeight);//0, -90, 540, 720
    }

    public void setViewSize(int width, int height) {
//        Log.i(TAG, "setViewSize width:" + width + ", " + height);
        mWidth = width;
        mHeight = height;
        if (mWidth > 0 && mHeight > 0) {
            mViewHWRatio = mHeight * 1.0f / mWidth;
            InvalidFrameCount = 0;
            GLES20.glViewport(0, 0, mWidth, mHeight);
        }
        if (mGLFramebuffer != null) {
            mGLFramebuffer.destroy();
            mGLFramebuffer = null;
        }
        if (mAllFilterCache != null) {
            for (Map.Entry<Integer, AbstractFilter> entry : mAllFilterCache.entrySet()) {
                AbstractFilter filter = entry.getValue();
                if (filter != null) {
                    filter.setViewSize(mWidth, mHeight);
                }
            }
        }
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mColorFilterGroup != null) {
            mColorFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mShapeFilterGroup != null) {
            mShapeFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mMakeUpFilterGroup != null) {
            mMakeUpFilterGroup.setViewSize(mWidth, mHeight);
        }
    }

    public void setCameraSize(int width, int height) {
//        Log.i(TAG, "setCameraSize width:" + width + ", " + height);
        mCameraWidth = width;
        mCameraHeight = height;
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.setCameraSize(mCameraWidth, mCameraHeight);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.setCameraSize(mCameraWidth, mCameraHeight);
        }
        if (mMakeUpFilterGroup != null) {
            mMakeUpFilterGroup.setCameraSize(mCameraWidth, mCameraHeight);
        }
    }

    public void runOnGLThread() {
//        Log.i(TAG, "runOnGLThread");
        if (mGLFramebuffer != null) {
            mGLFramebuffer.destroy();
            mGLFramebuffer = null;
        }
        if (mWidth > 0 && mHeight > 0) {
            mGLFramebuffer = new GLFramebuffer(5, mWidth, mHeight);
            mIsInitFramebuffer = true;
        }
        /*某些系统不支持重复使用同一个buffer，只能新建一个buffer*/
        if (mRecordGLFramebuffer != null) {
            mRecordGLFramebuffer.destroy();
        }
        if (mWidth > 0 && mHeight > 0) {
            mRecordGLFramebuffer = new GLFramebuffer(mWidth, mHeight);
        }
    }

    public void setDrawType(boolean isRecord) {
//        Log.i(TAG, "setDrawType isRecord:" + isRecord);
        if (mAllFilterCache != null) {
            for (Map.Entry<Integer, AbstractFilter> entry : mAllFilterCache.entrySet()) {
                AbstractFilter filter = entry.getValue();
                if (filter != null) {
                    filter.setDrawType(isRecord);
                }
            }
        }
        if (isRecord != mIsRecord) {
            if (isRecord) {
                GLES20.glViewport(mViewportXoff, mViewportYoff, mViewportWidth, mViewportHeight);
            } else {
                GLES20.glViewport(0, 0, mWidth, mHeight);
            }
        }
        mIsRecord = isRecord;
    }

    public void resetFilterData() {
        if (mAllFilterCache != null) {
            for (Map.Entry<Integer, AbstractFilter> entry : mAllFilterCache.entrySet()) {
                AbstractFilter filter = entry.getValue();
                if (filter != null) {
                    filter.resetFilterData();
                }
            }
        }
    }

    public void setPreviewDegree(int degree, boolean isFront) {
//        Log.i(TAG, "setRotation degree:" + degree + ", isFront:" + isFront);
        boolean flipVertical = true;
        int degree2 = degree;
        /*计算旋转角度，与镜头角度同步*/
        if (isFront) {
            degree = ((360 - degree) + 360) % 360;
            flipVertical = false;
        } else {
            degree = (degree + 180) % 360;
        }
        float[] flipTexture = TextureRotationUtils.getRotation(degree, true, flipVertical);
        FloatBuffer temp_mGLTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        temp_mGLTextureFlipBuffer.put(flipTexture).position(0);

        //-----------------------------------------------
        int yuvDegree = 0;
        if (isFront) {
            yuvDegree = ((180 - degree2) + 360) % 360;
            flipVertical = false;
        } else {
            yuvDegree = (360 - degree2) % 360;
        }
        //0   90  180 270
        //270 180 90  0
        degree2 = (degree2 + ((360 - degree2) + 270) % 360) % 360;

        float[] yuvFlipTexture = TextureRotationUtils.getRotation(yuvDegree, true, flipVertical);
        FloatBuffer temp_mYUVGLTextureFlipBuffer = ByteBuffer.allocateDirect(yuvFlipTexture.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        temp_mYUVGLTextureFlipBuffer.put(yuvFlipTexture).position(0);

        float[] flipTexture2 = TextureRotationUtils.getRotation(degree2, true, flipVertical);
        FloatBuffer temp_mGLTextureFlipBuffer2 = ByteBuffer.allocateDirect(flipTexture2.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        temp_mGLTextureFlipBuffer2.put(flipTexture2).position(0);

        //初始化完再赋值 防止闪屏
        mGLTextureFlipBuffer = temp_mGLTextureFlipBuffer;
        mYUVGLTextureFlipBuffer = temp_mYUVGLTextureFlipBuffer;
        mGLTextureFlipBuffer2 = temp_mGLTextureFlipBuffer2;
    }

    public void setBeautyEnable(boolean enable) {
        mBeautyEnable = enable;
    }

    public void setColorEnable(boolean enable) {
        mColorEnable = enable;
    }

    public void setFaceAdjustEnable(boolean enable) {
        mFaceAdjustEnable = enable;
    }

    public void setStickerEnable(boolean enable) {
        mStickerEnable = enable;
    }

    public void setShapeEnable(boolean enable) {
        mShapeEnable = enable;
    }

    public void setVideoTextureEnable(boolean enable) {
        mVideoTextureEnable = enable;
        if (mVideoTextureEnable && !hasCache(FilterDrawOrder.FILTER_VIDEO_TEXTURE)) {
            try {
                AbstractFilter filter = new VideoTextureFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_VIDEO_TEXTURE, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setVideoTextureSize(int width, int height) {
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_VIDEO_TEXTURE);
        if (filter != null) {
            ((VideoTextureFilter) filter).setVideoTextureSize(width, height);
        }
    }

    public void setDrawEnding(boolean ending) {
        mIsEnding = ending;
    }

    public void setEndingFrameCount(int endingFrameCount, Bitmap lastFrame) {
        mEndingFrameCount = endingFrameCount;
        mLastFrame = lastFrame;
    }

    public void updatePreviewFrame(byte[] previewData, int width, int height) {
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.updatePreviewFrame(previewData, width, height);
        }
    }

    public void setIsDrawCache(boolean drawCache) {
        mIsDrawCache = drawCache;
    }

    /**
     * Draws a viewport-filling rect, texturing it with the specified texture object.
     */
    public void drawFrame(int textureId, float[] texMatrix) {
        // Use the identity matrix for MVP so our 2x2 FULL_RECTANGLE covers the viewport.
        onDraw(IDENTITY_MATRIX, mRectDrawable.getVertexArray(), 0, mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
                mRectDrawable.getVertexStride(), texMatrix, mRectDrawable.getTexCoordArray(), textureId, mRectDrawable.getTexCoordStride());
    }

    public void drawFrame(int textureId, float[] texMatrix, int videoTextureId, float[] videoTextureMatrix) {
        mVideoTextureId = videoTextureId;
        mVideoSTMatrix = videoTextureMatrix;
        drawFrame(textureId, texMatrix);
    }

    private void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                        int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useCameraYUV = false;
        boolean beautyEnable = mBeautyEnable;
        boolean stickerEnable = mStickerEnable;
//        boolean videoTextureEnable = mVideoTextureEnable;
        FloatBuffer mFlipBuffer = mGLTextureFlipBuffer;

        if (mCameraFilterGroup != null) {
            useCameraYUV = mCameraFilterGroup.isYuvFilter();
            if (mCameraFilterGroup.hasBeauty()) {
                mCameraFilterGroup.setBeautyEnable(beautyEnable);
                beautyEnable = false;
            }
            if (mCameraFilterGroup.canSwitchToYuvFilter() && mCameraFilterGroup.filterIsChange()) {
                if (useCameraYUV) {
                    mFlipBuffer = mGLTextureFlipBuffer2;
                }
                addToCache(FilterDrawOrder.FILTER_CAMERA, mCameraFilterGroup.getFilter());
            }
            mIsStickerMode = useCameraYUV;
        }
        if (mBeautyFilterGroup != null && mBeautyFilterGroup.filterIsChange()) {
            addToCache(FilterDrawOrder.FILTER_BEAUTY, mBeautyFilterGroup.getFilter());
        }
        if (mColorFilterEnable && mColorFilterGroup != null && mColorFilterGroup.filterIsChange()) {
            addToCache(FilterDrawOrder.FILTER_COLOR, mColorFilterGroup.getFilter());
        }
        if (stickerEnable) {
            initStickerFilter();
        }
        if (mShapeFilterEnable && mShapeFilterGroup != null && mShapeFilterGroup.filterIsChange()) {
            addToCache(FilterDrawOrder.FILTER_SHAPE, mShapeFilterGroup.getFilter());
        }

        setFilterEnableByLayer(FilterDrawOrder.FILTER_BEAUTY, beautyEnable);
//        setFilterEnableByLayer(FilterDrawOrder.FILTER_COLOR, mColorEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_STICKER, mIsStickerMode && stickerEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_SHAPE, mIsStickerMode && mShapeEnable);

        boolean beautifyEnable = mFaceAdjustEnable || (!mShapeEnable && !stickerEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE, beautifyEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_SHRINK_NOSE, beautifyEnable);
//        setFilterEnableByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH, beautifyEnable);
//        setFilterEnableByLayer(FilterDrawOrder.FILTER_VIDEO_TEXTURE, videoTextureEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_SHOW_FACE_POINTS, !mIsStickerMode);

        if (!mIsStickerMode && mViewHWRatio != 0 && mLastViewHWRatio != 0 && mViewHWRatio != mLastViewHWRatio) {
            InvalidFrameCount++;
            if (InvalidFrameCount >= 2) {
                mLastViewHWRatio = mViewHWRatio;
                InvalidFrameCount = 0;
            }
            return;
        }
        mLastViewHWRatio = mViewHWRatio;

////******************************根据层级顺序进行绘制************************************************
        if (mAllFilterCache == null || mAllFilterCache.isEmpty()) {
            return;
        }
        if (mIsDrawCache && mCurrentFilter != null) {
            blendEnable(mCurrentFilter.isNeedBlend());

            FloatBuffer mTextureBuffer = getTextureBuffer(texBuffer, mFlipBuffer, mCurrentFilter.isNeedFlipTexture());
            int mTextureId = textureId;
            if (mBindRecordFramebuffer && mRecordGLFramebuffer != null) {
                mTextureId = mRecordGLFramebuffer.getTextureId();
            } else {
                mTextureId = getTextureId(textureId);
            }
            mCurrentFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, mTextureBuffer, mTextureId, texStride);

            blendEnable(false);
            return;
        }

        mCurrentLayer = 0;
        mNextLayer = 0;
        mCurrentFilter = null;
        mNextFilter = null;
        hasBindFramebuffer = false;
        mBindRecordFramebuffer = false;

        if (mGLFramebuffer != null) {
            mGLFramebuffer.reset();
        }
//        long t = System.currentTimeMillis();
        for (int i = 0; i < FilterDrawOrder.FILTER_MAX_LAYER; i++) {
            if (mCurrentLayer.intValue() > FilterDrawOrder.FILTER_MAX_LAYER) {
                break;
            }
            mCurrentFilter = mAllFilterCache.get(mCurrentLayer);
            if (mCurrentFilter != null) {
                mNextLayer = mCurrentLayer + 1;
                hasBindFramebuffer = false;

                for (int j = mNextLayer.intValue(); mIsInitFramebuffer && j < FilterDrawOrder.FILTER_MAX_LAYER; j++) {
                    if (mNextLayer.intValue() > FilterDrawOrder.FILTER_MAX_LAYER) {
                        break;
                    }
                    mNextFilter = mAllFilterCache.get(mNextLayer);
                    if (mNextFilter != null && mNextFilter.isFilterEnable() && mGLFramebuffer != null) {
                        /*if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE) {
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);
                        } else */
                        if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_COLOR) {
                            mCurrentFilter.setFilterGroups(mCompositeFilterGroup);
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);//光效使用图层混合
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_STICKER) {
                            mCurrentFilter.setFilterGroups(mCompositeFilterGroup, mShapeFilterGroup, mMakeUpFilterGroup);
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);//贴纸使用图层混合
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_SHAPE) {
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);
                        }
                        hasBindFramebuffer = mGLFramebuffer.bindNext(true);
                        break;
                    } else {
                        mNextLayer++;
                    }
                }
                blendEnable(mCurrentFilter.isNeedBlend());

                FloatBuffer mTextureBuffer = getTextureBuffer(texBuffer, mFlipBuffer, mCurrentFilter.isNeedFlipTexture());
                if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_CAMERA) {
                    mTextureBuffer = getTextureBuffer(texBuffer, mYUVGLTextureFlipBuffer, mCurrentFilter.isNeedFlipTexture());
                }
                int mTextureId = getTextureId(textureId);

                if (hasBindFramebuffer) {
                    mCurrentFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, mTextureBuffer, mTextureId, texStride);
                    //swap
                    mCurrentLayer = mNextLayer;
                    mCurrentFilter = mNextFilter;
                    mNextFilter = null;
                } else {
                    if (mGLFramebuffer != null) {
                        mGLFramebuffer.setHasBind(false);
                    }
                    if (stickerEnable && mBindRecordFramebufferEnable && mRecordGLFramebuffer != null) {
                        mBindRecordFramebuffer = mRecordGLFramebuffer.bind(true);
                        if (mBindRecordFramebuffer) {
                            mCurrentFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, mTextureBuffer, mTextureId, texStride);
                            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                        }
                    }
                    if (!mBindRecordFramebuffer && mCurrentLayer.intValue() != FilterDrawOrder.FILTER_CAMERA) {
                        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                    }
                    mCurrentFilter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, mTextureBuffer, mTextureId, texStride);
                    blendEnable(false);
                    break;
                }
            } else {
                mCurrentLayer++;
            }
        }
        blendEnable(false);
//        long t2 = System.currentTimeMillis();
//        Log.i(TAG, "onDraw: " + (t2 - t));
    }

    public FloatBuffer getScaleTextureCoords(float scale) {
        float textureCoords[] = {
                0.0f * scale, 0.0f * scale,
                1.0f * scale, 0.0f * scale,
                0.0f * scale, 1.0f * scale,
                1.0f * scale, 1.0f * scale};
        return GlUtil.createFloatBuffer(textureCoords);
    }

    private void blendEnable(boolean enable) {
        if (enable == mBlendEnable) {
            return;
        }
        mBlendEnable = enable;
        if (enable) {
            GLES20.glDepthMask(false);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendEquation(GLES20.GL_FUNC_ADD);
            GLES20.glBlendFuncSeparate(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        } else {
            GLES20.glDisable(GLES20.GL_BLEND);
            GLES20.glDepthMask(true);
        }
    }

    private FloatBuffer getTextureBuffer(FloatBuffer defaultBuffer, FloatBuffer flipBuffer, boolean useFlip) {
        if (useFlip && flipBuffer != null) {
            return flipBuffer;
        }
        return defaultBuffer;
    }

    private int getTextureId(int defaultTextureId) {
        int textureId = defaultTextureId;
        if (mGLFramebuffer != null) {
            textureId = mGLFramebuffer.getPreviousTextureId();
            if (textureId < 1) {
                textureId = defaultTextureId;
            }
        }
        return textureId;
    }

    public void loadNextTexture(boolean load) {
        if (mAllFilterCache != null) {
            for (Map.Entry<Integer, AbstractFilter> entry : mAllFilterCache.entrySet()) {
                AbstractFilter filter = entry.getValue();
                if (filter != null) {
                    filter.loadNextTexture(load);
                }
            }
        }
    }

    public void changeCameraFilter(int filterType) {
//        Log.i(TAG, "changeCameraFilter filterType:" + filterType);
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.changeFilterById(filterType);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.changeFilterById(2);
        }
        mIsStickerMode = (filterType == 2);
    }

    /**
     * 美形参数设置
     *
     * @param beauty      美颜
     * @param thinFace    瘦脸
     * @param bigEye      大眼
     * @param shrinkNose  瘦鼻
     * @param whitenTeeth 美牙
     */
    public void setBeautifyParams(@FloatRange(from = 0.0f, to = 1.0f) float beauty, @FloatRange(from = 0.0f, to = 1.0f) float thinFace,
                                  @FloatRange(from = 0.0f, to = 1.0f) float bigEye, @FloatRange(from = 0.0f, to = 1.0f) float shrinkNose,
                                  @FloatRange(from = 0.0f, to = 1.0f) float whitenTeeth) {
//        Log.i(TAG, "setBeautifyParams :" + beauty + ", " + thinFace + ", " + bigEye + ", " + shrinkNose + ", " + whitenTeeth);
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.setBeautyParams(beauty);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.setBeautyParams(beauty);
        }
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE);
        if (filter != null && filter instanceof NarrowFaceBigEyeFilter) {
            ((NarrowFaceBigEyeFilter) filter).setFaceEyeScale(thinFace, bigEye);
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_SHRINK_NOSE);
        if (filter != null && filter instanceof ShrinkNoseFilter) {
            ((ShrinkNoseFilter) filter).setStrengthScale(shrinkNose);
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH);
        if (filter != null && filter instanceof WhitenTeethFilter) {
            ((WhitenTeethFilter) filter).setWhitenTeethScale(whitenTeeth);
        }
    }

    public void changeColorFilter(int filterType) {
//        Log.i(TAG, "changeColorFilter filterType:" + filterType);
        if (mColorFilterGroup != null && filterType >= 0 && filterType <= 10) {
            mColorFilterGroup.changeFilterById(filterType);
        }
    }

    private int getColorFilterTypeById(int id) {
        if (id == 0) {
            return 0;
        } else if (id >= 1066300 && id <= 1066309) {
            return (id - 1066299);//1-10
        }
        return 11;
    }

    public void changeColorFilter(FilterRes filterRes) {
        if (mColorFilterGroup != null && filterRes != null) {
            int filterType = getColorFilterTypeById(filterRes.m_id);
            if (filterType == 11 && (filterRes.m_datas == null || filterRes.m_datas.length < 1
                    || filterRes.m_datas[0] == null || filterRes.m_datas[0].m_res == null)) {
                filterType = 0;
            }
//            Log.i(TAG, "changeColorFilter filterType:" + filterType);
            mColorFilterGroup.changeFilterById(filterType);
            mColorFilterGroup.setFilterData(filterRes);
        }
    }

    public void setRatioAndOrientation(float width_height_ratio, int flip, int upCut) {
        if (mColorFilterGroup != null) {
            mColorFilterGroup.setRatioAndOrientation(width_height_ratio, flip, upCut);
        }
    }

    public void changeShapeFilter(int filterType) {
//        Log.i(TAG, "changeShapeFilter filterType:" + filterType);
        if (mShapeFilterGroup != null) {
            mShapeFilterGroup.changeFilterById(filterType);
        }
    }

    public void setOnDrawStickerResListener(OnDrawStickerResListener listener) {
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_STICKER);
        if (filter != null && filter instanceof Sticker3dFilter) {
            ((Sticker3dFilter) filter).setOnDrawStickerResListener(listener);
        }
    }

    /**
     * Releases resources.
     * <p/>
     * This must be called with the appropriate EGL context current (i.e. the one that was
     * current when the constructor was called).  If we're about to destroy the EGL context,
     * there's no value in having the caller make it current just to do this cleanup, so you
     * can pass a flag that will tell this function to skip any EGL-context-specific cleanup.
     */
    public void release(boolean doEglCleanup) {
//        if (mAllFilterCache != null) {
//            Log.i(TAG, "mAllFilterCache size:"+ mAllFilterCache.size());
//        }
        mContext = null;
        mVideoSTMatrix = null;
        mGLTextureFlipBuffer = null;
        mGLTextureFlipBuffer2 = null;
        mYUVGLTextureFlipBuffer = null;
        mCurrentLayer = null;
        mNextLayer = null;
        mCurrentFilter = null;
        mNextFilter = null;

        if (mAllFilterCache != null) {
            for (Map.Entry<Integer, AbstractFilter> entry : mAllFilterCache.entrySet()) {
                AbstractFilter filter = entry.getValue();
                if (filter != null) {
                    filter.releaseProgram();
                    filter = null;
                }
            }
            mAllFilterCache.clear();
            mAllFilterCache = null;
        }
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.release(doEglCleanup);
            mCameraFilterGroup = null;
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.release(doEglCleanup);
            mBeautyFilterGroup = null;
        }
        if (mColorFilterGroup != null) {
            mColorFilterGroup.release(doEglCleanup);
            mColorFilterGroup = null;
        }
        if (mCompositeFilterGroup != null) {
            mCompositeFilterGroup.release(doEglCleanup);
            mCompositeFilterGroup = null;
        }
        if (mShapeFilterGroup != null) {
            mShapeFilterGroup.release(doEglCleanup);
            mShapeFilterGroup = null;
        }
        if (mMakeUpFilterGroup != null) {
            mMakeUpFilterGroup.release(doEglCleanup);
            mMakeUpFilterGroup = null;
        }
        if (mGLFramebuffer != null) {
            mGLFramebuffer.destroy();
            mGLFramebuffer = null;
        }
        if (mRecordGLFramebuffer != null) {
            mRecordGLFramebuffer.destroy();
            mRecordGLFramebuffer = null;
        }
        mIsInitFramebuffer = false;
    }
}
