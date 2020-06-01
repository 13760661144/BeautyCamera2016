package cn.poco.gldraw2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.poco.camera3.beauty.data.BeautyData;
import cn.poco.camera3.beauty.data.ShapeData;
import cn.poco.gldraw.FilterDrawOrder;
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
import cn.poco.glfilter.shape.BeautifyFilter;
import cn.poco.glfilter.shape.NarrowFaceBigEyeFilter;
import cn.poco.glfilter.shape.ShapeFilterGroup;
import cn.poco.glfilter.shape.ShrinkNoseFilter;
import cn.poco.glfilter.shape.WhitenTeethFilter;
import cn.poco.glfilter.sticker.OnDrawStickerResListener;
import cn.poco.glfilter.sticker.SplitScreenFilter;
import cn.poco.glfilter.sticker.Sticker3dFilter;
import cn.poco.resource.FilterRes;

/**
 * Created by zwq on 2017/02/22 12:08.<br/><br/>
 * 1.根据FilterDrawOrder的顺序进行绘制；
 * 2.使用Framebuffer减少录制时的绘制次数
 */
public class RenderFilterManager {

    private final String TAG = "vvv RenderFilterManager";
    private final Drawable2d mRectDrawable = new Drawable2d();
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mViewProjectionMatrix = new float[16];

    private Context mContext;
    private int mSurfaceWidth, mSurfaceHeight;

    private Rect mDisplayRect;
    private Rect mFrameBufferRect;
    private Rect mRecordRect;

    private float mViewHWRatio;
    private float mRenderScale = 1.0f;
    private int mWidth, mHeight;
    private int mCameraWidth, mCameraHeight;
    private int mVideoWidth, mVideoHeight;
    private int mVideoTopOffset;

    private HashMap<Integer, AbstractFilter> mAllFilterCache;
    private CameraFilterGroup mCameraFilterGroup;
    private BeautyFilterGroup mBeautyFilterGroup;
    private ColorFilterGroup mColorFilterGroup;//颜色滤镜组
    private CompositeFilterGroup mCompositeFilterGroup;//混合模式
    private ShapeFilterGroup mShapeFilterGroup;//变形滤镜组
    private MakeUpFilterGroup mMakeUpFilterGroup;//彩妆

    private BeautyData mBeautyData;//美形定制 - 美颜
    private ShapeData mShapeData;//美形定制 - 脸型

    private boolean useCameraYUV;//使用镜头YUV数据渲染画面
    private boolean mBeautyEnable;
    private boolean mBusinessBeautyEnable;//控制商业Filter的处理
    private boolean mColorEnable;
    private boolean mFaceAdjustEnable = true;
    private final boolean mColorFilterEnable = true;
    private final boolean mShapeFilterEnable = true;
    private boolean mStickerEnable;
    private boolean mShapeEnable = true;
    private boolean mVideoTextureEnable = false;
    private boolean mSplitScreenEnable = true;

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

    public RenderFilterManager(Context context) {
        super();
        mContext = context;
        mAllFilterCache = new HashMap<Integer, AbstractFilter>();
        Matrix.setIdentityM(mViewProjectionMatrix, 0);

        mDisplayRect = new Rect();
        mFrameBufferRect = new Rect();
        mRecordRect = new Rect();
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

    protected AbstractFilter removeFilterByLayer(Integer layer) {
        if (mAllFilterCache == null) {
            return null;
        }
        return mAllFilterCache.remove(layer);
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
                    mCameraFilterGroup.changeFilterById(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mCameraFilterGroup = null;
            }
        }
        if (mBeautyFilterGroup == null) {
            try {
                mBeautyFilterGroup = new BeautyFilterGroup(mContext);
                filter = mBeautyFilterGroup.preInitFilter(3);//羽西商业
                mBeautyFilterGroup.changeFilterById(4);
            } catch (Exception e) {
                e.printStackTrace();
                mBeautyFilterGroup = null;
            }
            if (filter != null) {
                addToCache(FilterDrawOrder.FILTER_BUSINESS_CUSTOM, filter);
            }
        }
        //美形定制
        initBeautifyFilter();

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

        //贴纸
        filter = getFilterByLayer(FilterDrawOrder.FILTER_STICKER);
        if (filter == null) {
            try {
                filter = new Sticker3dFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_STICKER, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (filter != null) {
                filter.setRenderScale(mRenderScale);
                filter.setViewSize(mWidth, mHeight);

                //-------分屏---------
                try {
                    SplitScreenFilter splitScreenFilter = new SplitScreenFilter(mContext);
                    addToCache(FilterDrawOrder.FILTER_SPLIT_SCREEN, splitScreenFilter);
                    ((Sticker3dFilter) filter).setSplitScreenFilter(splitScreenFilter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (mShapeFilterEnable && mShapeFilterGroup == null) {
            try {
                mShapeFilterGroup = new ShapeFilterGroup(mContext);
                mShapeFilterGroup.preInitFilter(24);
            } catch (Exception e) {
                e.printStackTrace();
                mShapeFilterGroup = null;
            }
        }

        /*filter = getFilterByLayer(FilterDrawOrder.FILTER_SHOW_FACE_POINTS);
        if (filter == null) {
            try {//显示人脸的点用于调试
                filter = new FacePointFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_SHOW_FACE_POINTS, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/

        filter = getFilterByLayer(FilterDrawOrder.FILTER_DISPLAY);
        if (filter == null) {
            try {//左下角水印
                filter = new DisplayFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_DISPLAY, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filter = null;
    }

    private void initBeautifyFilter() {
        AbstractFilter filter = null;
        /*filter = getFilterByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE);
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
        }*/
        filter = getFilterByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH);
        if (filter == null) {
            try {
                filter = new WhitenTeethFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_WHITEN_TEETH, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_BEAUTIFY_SHAPE);
        if (filter == null) {
            try {
                filter = new BeautifyFilter(mContext);
                addToCache(FilterDrawOrder.FILTER_BEAUTIFY_SHAPE, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AbstractFilter getDisplayFilter() {
        return getFilterByLayer(FilterDrawOrder.FILTER_DISPLAY);
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

    public void initMVPMatrix(int width, int height) {
        float mRatio = width * 1.0f / height;
        int offset = 0;
        float left = -mRatio;
        float right = mRatio;
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 3.0f;// near <= eyeZ
        float far = 7.0f;
        Matrix.frustumM(mProjectionMatrix, offset, left, right, bottom, top, near, far);//透视投影矩阵

        // Set the camera position (View matrix)
        int rmOffset = 0;
        float eyeX = 0.0f;
        float eyeY = 0.0f;
        float eyeZ = 3.0f;
        float centerX = 0.0f;
        float centerY = 0.0f;
        float centerZ = 0.0f;
        float upX = 0.0f;
        float upY = 1.0f;
        float upZ = 0.0f;
        Matrix.setLookAtM(mViewMatrix, rmOffset, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);//视觉矩阵
        Matrix.multiplyMM(mViewProjectionMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
    }

    public void scaleMVPMatrix(float x, float y) {
        Matrix.setIdentityM(mViewProjectionMatrix, 0);
        Matrix.scaleM(mViewProjectionMatrix, 0, x, y, 1f);
    }

    public void setSurfaceSize(int width, int height) {
        setSurfaceSize(width, height, 1.0f);
    }

    public void setSurfaceSize(int width, int height, float renderScale) {
//        Log.i(TAG, "setSurfaceSize width:" + width + ", " + height);
        if (width <= 0 || height <= 0 || renderScale <= 0.0f) {
            return;
        }
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        mRenderScale = renderScale;

        mViewHWRatio = mSurfaceHeight * 1.0f / mSurfaceWidth;
        mWidth = Math.round(width * renderScale);
        mHeight = Math.round(height * renderScale);

        mDisplayRect.set(0, 0, mSurfaceWidth, mSurfaceHeight);
        mFrameBufferRect.set(0, 0, mWidth, mHeight);
        GLES20.glViewport(mFrameBufferRect.left, mFrameBufferRect.top, mFrameBufferRect.right, mFrameBufferRect.bottom);

        if (mGLFramebuffer != null) {
            mGLFramebuffer.destroy();
            mGLFramebuffer = null;
        }
        if (mAllFilterCache != null) {
            for (Map.Entry<Integer, AbstractFilter> entry : mAllFilterCache.entrySet()) {
                AbstractFilter filter = entry.getValue();
                if (filter != null) {
                    filter.setRenderScale(mRenderScale);
                    filter.setViewSize(mWidth, mHeight);
                }
            }
        }
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.setRenderScale(mRenderScale);
            mCameraFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.setRenderScale(mRenderScale);
            mBeautyFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mColorFilterGroup != null) {
            mColorFilterGroup.setRenderScale(mRenderScale);
            mColorFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mShapeFilterGroup != null) {
            mShapeFilterGroup.setRenderScale(mRenderScale);
            mShapeFilterGroup.setViewSize(mWidth, mHeight);
        }
        if (mMakeUpFilterGroup != null) {
            mMakeUpFilterGroup.setRenderScale(mRenderScale);
            mMakeUpFilterGroup.setViewSize(mWidth, mHeight);
        }
    }

    public void initGLFramebuffer() {
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

        //创建 AR3D 的 DepthFrameBuffer 放最后，否则可能会有问题
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_STICKER);
        if (filter != null) {
            try {
                ((Sticker3dFilter) filter).setDepthFrameBuffer(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public void setVideoSize(int width, int height) {
        setVideoSize(width, height, 0);
    }

    /**
     * width < height
     *
     * @param width
     * @param height
     */
    public void setVideoSize(int width, int height, int topOffset) {
        if (width > 0 && height > 0) {
            if (width != mVideoWidth || height != mVideoHeight || topOffset != mVideoTopOffset) {
                mVideoWidth = width;
                mVideoHeight = height;
                mVideoTopOffset = topOffset;
                calculateViewport(1, topOffset, mSurfaceWidth, mSurfaceHeight, mVideoWidth, mVideoHeight);

                AbstractFilter filter = getDisplayFilter();
                if (filter != null) {
                    float videoRatio = mVideoHeight * 1.0f / mVideoWidth;
                    float bottomOffset = 0.0f;
                    if (topOffset > 0) {
                        bottomOffset = mSurfaceHeight - topOffset - mSurfaceWidth * videoRatio;
                    }
                    ((DisplayFilter) filter).setWaterMarkYOffset(videoRatio, topOffset, bottomOffset);
                }
            }
        }
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
            mRecordRect.set(0, 0, videoWidth, videoHeight);
            return;
        }
        float viewRatio = viewHeight * 1.0f / viewWidth;
        int topOffset = 0;
        int width = 0, height = 0, xOff = 0, yOff = 0;
        if (type == 1) {
            //裁剪处理
            height = Math.round(videoWidth * viewRatio);
            if (videoHeight > height) {//videoRatio > viewRatio
                width = height;//Math.round(videoWidth * viewRatio);
                height = videoHeight;

            } else {
                width = videoWidth;
                //height = Math.round(videoWidth * viewRatio);

                float maxTopOffset = viewHeight - (viewWidth * (videoHeight * 1.0f / videoWidth));
                if (top > -1 && top <= maxTopOffset) {
                    topOffset = Math.round((-maxTopOffset / 2 + top) / viewHeight * height);
//                    Log.i(TAG, "calculateViewport: " + top + ", " + topOffset);
                }
            }
        } else {
            //缩放处理
            height = Math.round(videoWidth * viewRatio);
            if (videoHeight > height) {
                width = videoWidth;
                //height = Math.round(videoWidth * viewRatio);
            } else {
                width = Math.round(videoHeight / viewRatio);
                height = videoHeight;
            }
        }
        xOff = (videoWidth - width) / 2;
        yOff = (videoHeight - height) / 2 + topOffset;
        mRecordRect.set(xOff, yOff, width, height);
//        Log.i(TAG, "calculateViewport: " + xOff + ", " + yOff + ", " + width + ", " + height);//0, -90, 540, 720
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
                GLES20.glViewport(mRecordRect.left, mRecordRect.top, mRecordRect.right, mRecordRect.bottom);
            } else {
                GLES20.glViewport(mFrameBufferRect.left, mFrameBufferRect.top, mFrameBufferRect.right, mFrameBufferRect.bottom);
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

    public void setBusinessBeautyEnable(boolean enable) {
        mBusinessBeautyEnable = enable;
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
                if (filter != null) {
                    addToCache(FilterDrawOrder.FILTER_VIDEO_TEXTURE, filter);
                    filter.setRenderScale(mRenderScale);
                    filter.setViewSize(mWidth, mHeight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setSplitScreenEnable(boolean splitScreenEnable) {
        mSplitScreenEnable = splitScreenEnable;
    }

    public void setVideoTextureSize(int width, int height) {
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_VIDEO_TEXTURE);
        if (filter != null) {
            ((VideoTextureFilter) filter).setVideoTextureSize(width, height);
        }
    }

    public void setFaceRectEnable(boolean enable) {
        AbstractFilter filter = getDisplayFilter();
        if (filter != null) {
            ((DisplayFilter) filter).setFaceRectEnable(enable);
        }
    }

    public void setWaterMarkEnable(boolean enable) {
        AbstractFilter filter = getDisplayFilter();
        if (filter != null) {
            ((DisplayFilter) filter).setWaterMarkEnable(enable);
        }
    }

    public void prepareWaterMark(boolean hasDate) {
        AbstractFilter filter = getDisplayFilter();
        if (filter != null) {
            ((DisplayFilter) filter).prepareWaterMark(hasDate);
        }
    }

    public void setWaterMarkOrientation(int degree) {
        AbstractFilter filter = getDisplayFilter();
        if (filter != null) {
            ((DisplayFilter) filter).setWaterMarkOrientation(degree);
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
        onDraw(mViewProjectionMatrix, mRectDrawable.getVertexArray(), 0, mRectDrawable.getVertexCount(), mRectDrawable.getCoordsPerVertex(),
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
        boolean videoTextureEnable = mVideoTextureEnable && mVideoTextureId > 0;
        FloatBuffer mFlipBuffer = mGLTextureFlipBuffer;

        mIsStickerMode = stickerEnable;

        if (mBeautyData != null && mBeautyData.skinBeauty <= 0.0f && mBeautyData.skinType <= 0.0f) {
            beautyEnable = false;
        }
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
        }
        if (mBeautyFilterGroup != null && mBeautyFilterGroup.filterIsChange()) {
            addToCache(FilterDrawOrder.FILTER_BEAUTY, mBeautyFilterGroup.getFilter());

            if (mBeautyData != null) {
                mBeautyFilterGroup.setBeautyParams(mBeautyData.skinBeauty, mBeautyData.skinType);
            }
        }
        if (mColorFilterEnable && mColorFilterGroup != null && mColorFilterGroup.filterIsChange()) {
            addToCache(FilterDrawOrder.FILTER_COLOR, mColorFilterGroup.getFilter());
        }
        if (mShapeFilterEnable && mShapeFilterGroup != null && mShapeFilterGroup.filterIsChange()) {
            addToCache(FilterDrawOrder.FILTER_SHAPE, mShapeFilterGroup.getFilter());
        }

        //setFilterEnableByLayer(FilterDrawOrder.FILTER_BEAUTY, beautyEnable && mBeautyData != null && (mBeautyData.skinBeauty > 0.0f || mBeautyData.skinType > 0.0f));
        setFilterEnableByLayer(FilterDrawOrder.FILTER_BEAUTY, beautyEnable && mBeautyData != null && mBeautyData.skinBeauty > 0.0f);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_BUSINESS_CUSTOM, mBusinessBeautyEnable);
        //setFilterEnableByLayer(FilterDrawOrder.FILTER_COLOR, mColorEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_STICKER, mIsStickerMode && stickerEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_SHAPE, mIsStickerMode && mShapeEnable);

        boolean beautifyEnable = mFaceAdjustEnable || (!mShapeEnable && !stickerEnable);

        //setFilterEnableByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE, beautifyEnable && mShapeData != null && (mShapeData.thinFace > 0.0f || mShapeData.bigEye > 0.0f));
        //setFilterEnableByLayer(FilterDrawOrder.FILTER_SHRINK_NOSE, beautifyEnable && mShapeData != null && mShapeData.shrinkNose > 0.0f);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH, mBeautyData != null && mBeautyData.whitenTeeth > 0.0f);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_BEAUTIFY_SHAPE, beautifyEnable && mShapeData != null/* && mShapeData.thinFace > 0.0f*/);

        if (!(mIsStickerMode && stickerEnable)) {
            setFilterEnableByLayer(FilterDrawOrder.FILTER_SPLIT_SCREEN, false/* && mSplitScreenEnable*/);
        }
        setFilterEnableByLayer(FilterDrawOrder.FILTER_VIDEO_TEXTURE, videoTextureEnable);
        setFilterEnableByLayer(FilterDrawOrder.FILTER_SHOW_FACE_POINTS, !mIsStickerMode);

////******************************根据层级顺序进行绘制************************************************
        if (mAllFilterCache == null || mAllFilterCache.isEmpty()) {
            return;
        }
        if (mIsDrawCache && mCurrentFilter != null) {
            /*GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
            GLES20.glScissor(mViewportXoff, mViewportYoff, mViewportWidth, mViewportHeight);*/

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

            /*GLES20.glDisable(GLES20.GL_SCISSOR_TEST);*/
            return;
        }

        GLES20.glViewport(mFrameBufferRect.left, mFrameBufferRect.top, mFrameBufferRect.right, mFrameBufferRect.bottom);//scale

        FaceDataHelper.getInstance().checkAndConvertData();//判断是否要更新人脸数据

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
                        if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_BEAUTY) {
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_COLOR) {
                            mCurrentFilter.setFilterGroups(mCompositeFilterGroup);
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);//光效使用图层混合
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_STICKER) {
                            mCurrentFilter.setFilterGroups(mCompositeFilterGroup, mShapeFilterGroup, mMakeUpFilterGroup);
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);//贴纸使用图层混合
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_SHAPE) {
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_WHITEN_TEETH) {
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_BEAUTIFY_SHAPE) {
                            mCurrentFilter.setFramebuffer(mGLFramebuffer);
                        } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_SPLIT_SCREEN) {
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
                } else if (mCurrentLayer.intValue() == FilterDrawOrder.FILTER_VIDEO_TEXTURE) {
                    ((VideoTextureFilter) mCurrentFilter).setVideoTextureData(mVideoTextureId, mVideoSTMatrix);
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
                    GLES20.glViewport(mDisplayRect.left, mDisplayRect.top, mDisplayRect.right, mDisplayRect.bottom);
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

    /**
     * 美形定制 - 旧版接口
     *
     * @param params
     */
    public void setBeautifyParams(float[] params) {
        if (params == null || params.length < 6) {
            return;
        }
        if (mBeautyData == null) {
            mBeautyData = new BeautyData();
        }
        mBeautyData.skinBeauty = params[0];
        mBeautyData.skinType = params[5];
        mBeautyData.whitenTeeth = params[4];

        if (mShapeData == null) {
            mShapeData = new ShapeData();
        }
        mShapeData.thinFace = params[1];
        mShapeData.bigEye = params[2];
        mShapeData.shrinkNose = params[3];

        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.setBeautyParams(mBeautyData.skinBeauty);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.setBeautyParams(mBeautyData.skinBeauty, mBeautyData.skinType);
        }
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH);
        if (filter != null && filter instanceof WhitenTeethFilter) {
            ((WhitenTeethFilter) filter).setWhitenTeethScale(mBeautyData.whitenTeeth);
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE);
        if (filter != null && filter instanceof NarrowFaceBigEyeFilter) {
            ((NarrowFaceBigEyeFilter) filter).setFaceEyeScale(mShapeData.thinFace, mShapeData.bigEye);
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_SHRINK_NOSE);
        if (filter != null && filter instanceof ShrinkNoseFilter) {
            ((ShrinkNoseFilter) filter).setStrengthScale(mShapeData.shrinkNose);
        }
    }

    /**
     * 美形定制 - 美颜
     */
    public void setBeautyData(BeautyData beautyData) {
        if (beautyData == null) {
            return;
        }
        mBeautyData = beautyData;
        if (mCameraFilterGroup != null) {
            mCameraFilterGroup.setBeautyParams(mBeautyData.skinBeauty);
        }
        if (mBeautyFilterGroup != null) {
            mBeautyFilterGroup.setBeautyParams(mBeautyData.skinBeauty, mBeautyData.skinType);
        }
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_WHITEN_TEETH);
        if (filter != null && filter instanceof WhitenTeethFilter) {
            ((WhitenTeethFilter) filter).setWhitenTeethScale(mBeautyData.whitenTeeth);
        }
    }

    /**
     * 美形定制 - 脸型
     */
    public void setShapeData(ShapeData shapeData) {
        if (shapeData == null) {
            return;
        }
        if (shapeData.isNone) {
            mShapeData = null;
        } else {
            mShapeData = shapeData;
        }
        AbstractFilter filter; /*= getFilterByLayer(FilterDrawOrder.FILTER_THIN_FACE_BIG_EYE);
        if (filter != null && filter instanceof NarrowFaceBigEyeFilter) {
            ((NarrowFaceBigEyeFilter) filter).setFaceEyeScale(mShapeData.thinFace, mShapeData.bigEye);
        }
        filter = getFilterByLayer(FilterDrawOrder.FILTER_SHRINK_NOSE);
        if (filter != null && filter instanceof ShrinkNoseFilter) {
            ((ShrinkNoseFilter) filter).setStrengthScale(mShapeData.shrinkNose);
        }*/
        filter = getFilterByLayer(FilterDrawOrder.FILTER_BEAUTIFY_SHAPE);
        if (filter != null && filter instanceof BeautifyFilter) {
            ((BeautifyFilter) filter).setShapeData(mShapeData);
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
            return id;

        } else if (id >= 1066300 && id <= 1066309) {
            return (id - 1066299);//1-10

        } else if (id == 2000000) {//阿玛尼定制
            return id;
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
            if (filterType == 0) {
                mColorFilterGroup.setFilterData(filterRes);
                mColorFilterGroup.changeFilterById(filterType);
            } else {
                mColorFilterGroup.changeFilterById(filterType);
                mColorFilterGroup.setFilterData(filterRes);
            }
        }
    }

    public void changeColorFilterRenderStyle(boolean reset) {
        if (mColorFilterGroup != null) {
            mColorFilterGroup.changeColorFilterRenderStyle(reset);
        }
    }

    public void setRatioAndOrientation(float width_height_ratio, int flip, int upCut) {
        if (mColorFilterGroup != null) {
            mColorFilterGroup.setRatioAndOrientation(width_height_ratio, flip, upCut);
        }
        AbstractFilter filter = getFilterByLayer(FilterDrawOrder.FILTER_SPLIT_SCREEN);
        if (filter != null) {
            ((SplitScreenFilter) filter).setRatio(width_height_ratio, upCut);
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
        mBeautyData = null;
        mShapeData = null;

        FaceDataHelper.getInstance().clearAll();
    }
}
