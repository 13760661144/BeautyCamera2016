package cn.poco.pgles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.poco.camera.ICameraFilter;
import cn.poco.image.PocoFace;
import cn.poco.utils.CpuUtils;
import my.beautyCamera.R;

public class PFBeautyWrapV2 extends PFFilter implements ICameraFilter {

    private static final String TAG = "PFBeautyWrapV2";

    private Context mContext;
    private PGLFramebuffer[] mPGLFramebuffers;

//    private PFSrc mCameraFilter = null;
//    private PFBeauty mBeautyFilter;
    private PFBeautyV2 mBeautyFilter = null;
    private PFFilter mColorFilter = null;
//    private PFRGBBlendFilter mPFRGBBlendFilter;
    private HashMap<Integer, PFFilter> mColorFilterCache;//滤镜缓存

    protected final FloatBuffer mGLCubeBuffer;
    protected FloatBuffer mGLTextureBuffer;

    protected FloatBuffer mGLTextureFlipBuffer;
//    protected FloatBuffer mGLTextureFlipBuffer_NoBeauty_Front;

    private int mCameraId = 0;
    private int mPreviewDegree;
    private int mScreenW = 1080, mScreenH = 1440;
    private float narrow = 1.0f;

    private int mColorFilterId;
    private boolean mBeautyFilterEnable = true;
    private boolean mColorFilterEnable;

    private int QUALCOMMFLAG = 0;
    private boolean isExit;
    private boolean isSwitchCamera;
    private boolean isPatchMode;
    private int mPatchDegree;

    public PFBeautyWrapV2(Context context) {
        super(context);
        mContext = context;
        isExit = false;

        Log.e(TAG, "mBeautyFilterEnable:"+mBeautyFilterEnable);

//        if (mBeautyFilterEnable){
            mBeautyFilter = new PFBeautyV2(context);
//        }else{
//            mCameraFilter = new PFSrc(context);
//        }

        mGLCubeBuffer = ByteBuffer.allocateDirect(PGLTextureRotationUtil.CUBE.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLCubeBuffer.put(PGLTextureRotationUtil.CUBE).position(0);

        float[] textureBuffer = PGLTextureRotationUtil.getRotation(PGLRotation.NORMAL, false, true);
        mGLTextureBuffer = ByteBuffer.allocateDirect(textureBuffer.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mGLTextureBuffer.put(textureBuffer).position(0);

        //获取cpu信息
        CpuUtils.CpuInfo cpuInfo = CpuUtils.getCpuInfo();
        if (cpuInfo != null && cpuInfo.mHardware != null && cpuInfo.mHardware.indexOf("msm") != -1) {
            QUALCOMMFLAG = 1;
        } else {
            QUALCOMMFLAG = 0;
        }

//        setRotation(mCameraId);
    }

    @Override
    public void onInit() {
//        if (mCameraFilter != null) {
//            mCameraFilter.init();
//        }

        if (mBeautyFilter != null) {
            mBeautyFilter.init();
        }
    }

    @Override
    public void setBeautyEnable(boolean enable) {
        mBeautyFilterEnable = enable;
    }

    @Override
    public float[] getTextureCube() {
        return PGLTextureRotationUtil.CUBE;
    }

    @Override
    public float[] getTextureRotation(int rotate) {
        rotate = rotate % 360;
        if (rotate == 0) {
            return PGLTextureRotationUtil.TEXTURE_NO_ROTATION;
        } else if (rotate == 90) {
            return PGLTextureRotationUtil.TEXTURE_ROTATED_90;
        } else if (rotate == 180) {
            return PGLTextureRotationUtil.TEXTURE_ROTATED_180;
        } else if (rotate == 270) {
            return PGLTextureRotationUtil.TEXTURE_ROTATED_270;
        }
        return null;
    }

    @Override
    public void setFilterEnable(boolean enable) {
        mColorFilterEnable = enable;
    }

    public void setFilterId(int filterId) {
        mColorFilterId = filterId;
    }

    public void setPatchMode(boolean patchMode) {
        isPatchMode = patchMode;
    }

    public void setPatchDegree(int patchDegree) {
        mPatchDegree = patchDegree;
////        setPreviewDegree(patchDegree);
//        if (patchDegree != mPreviewDegree) {
//            mPreviewDegree = patchDegree;
//        }
    }

    public void setSwitchCamera(boolean switchCamera) {
        isSwitchCamera = switchCamera;
    }

    public void setExit(boolean exit) {
        this.isExit = exit;
    }

    public void setPreviewDegree(int previewDegree) {
        if (isPatchMode) {
            mPatchDegree = previewDegree;
        }
        if (previewDegree != mPreviewDegree) {
            mPreviewDegree = previewDegree;
            setRotation(mCameraId);
        }
    }

    public float[] getRotation(int degree, int cameraId) {
        degree = 180;
        boolean flipVertical = false;
//        if (cameraId == 0) {
//            degree = (degree + 180) % 360;
//            flipVertical = true;
//        } else if (cameraId == 1) {
//            flipVertical = false;
//        }
//        Log.i("bbb", "degree:"+degree+", cameraId:"+cameraId);
        float[] flipTexture = null;
        if (degree == 0) {
            flipTexture = PGLTextureRotationUtil.getRotation(PGLRotation.NORMAL, true, flipVertical);
        } else if (degree == 90) {
            flipTexture = PGLTextureRotationUtil.getRotation(PGLRotation.ROTATION_90, true, flipVertical);
        } else if (degree == 180) {
            flipTexture = PGLTextureRotationUtil.getRotation(PGLRotation.ROTATION_180, true, flipVertical);
        } else if (degree == 270) {
            flipTexture = PGLTextureRotationUtil.getRotation(PGLRotation.ROTATION_270, true, flipVertical);
        } else {
            flipTexture = PGLTextureRotationUtil.getRotation(PGLRotation.NORMAL, true, flipVertical);
        }
        return flipTexture;
    }

    @Override
    public void setRotation(int cameraId) {
        mCameraId = cameraId;
        if (isPatchMode) {
            if (mPreviewDegree != mPatchDegree) {
                mPreviewDegree = mPatchDegree;
            } else {
                return;
            }
        }
        if (mGLTextureFlipBuffer == null) {
            float[] flipTexture = getRotation(mPreviewDegree, mCameraId);

            mGLTextureFlipBuffer = ByteBuffer.allocateDirect(flipTexture.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mGLTextureFlipBuffer.put(flipTexture).position(0);
        }
    }

    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        mScreenW = width;
        mScreenH = height;
        autoGetNarrow();
//        if (mCameraFilter != null) {
//            mCameraFilter.onOutputSizeChanged(width, height);
//        }
        if (mBeautyFilter != null) {
            mBeautyFilter.onOutputSizeChanged(width, height);
        }
        if (mPGLFramebuffers == null) {
            updateBufferSize();
        }
    }

    @Override
    public void setCameraSize(int width, int height) {
        if (mBeautyFilter != null) {
            if (mPreviewDegree == 90 || mPreviewDegree == 270) {
                mBeautyFilter.setFBOSize(height, width);
            } else {
                mBeautyFilter.setFBOSize(width, height);
            }
        }
    }

    public void autoGetNarrow() {
        if (mBeautyFilter != null) {
            narrow = 1.0f;
        }
    }

//    private int extHeight;
    private void updateBufferSize() {
//        if ("Xiaomi".equals(Build.MANUFACTURER) && "MI 5".equals(Build.MODEL.toUpperCase(Locale.CHINA))) {
//            extHeight = ShareData.getRealPixel_720P(53);//80
//        } else {
//            extHeight = 0;
//        }

        if (mPGLFramebuffers != null) {
            for (int i = 0; i < mPGLFramebuffers.length; ++i) {
                mPGLFramebuffers[i].destroy();
            }
        }
        mPGLFramebuffers = new PGLFramebuffer[1];
        for (int i = 0; i < mPGLFramebuffers.length; ++i) {
            mPGLFramebuffers[i] = new PGLFramebuffer((int) (mScreenW * narrow), (int) (mScreenH * narrow)/* - extHeight*/);
        }
        if (mBeautyFilter != null) {
            mBeautyFilter.setSize((int) (mScreenW * narrow), (int) (mScreenH * narrow));
        }
    }

    @Override
    public void setFaceData(Object... objects) {
        if (objects != null && objects.length > 0) {
            PocoFace pocoFace = (PocoFace) objects[0];
        }
    }

    @Override
    public void draw(int textureId, float[] transformMatrix, float[] viewProjectionMatrix) {
        if (isExit) {
            if (isSwitchCamera) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//                if (mPFRGBBlendFilter != null) {
//                    //有些手机切换镜头预览会抖动 ，清掉预览
//                    mPFRGBBlendFilter.setColor(new float[]{0.0f, 0.0f, 0.0f});
//                    mPFRGBBlendFilter.setPercent(1.0f);
//                    mPFRGBBlendFilter.draw(textureId, mGLCubeBuffer, mCameraId == 0 ? mGLTextureFlipBuffer : mGLTextureFlipBuffer_NoBeauty_Front);
//                }
            }
            return;
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        if (narrow != 1.0f) {
            GLES20.glViewport(0, 0, (int) (mScreenW * narrow), (int) (mScreenH * narrow));
        }

        if (mColorFilterId < 0) {
            mColorFilterId = 0;
        }
        mColorFilter = getColorFilter(mScreenW, mScreenH, mColorFilterId);
        boolean hasBindFramebuffer = false;

        if (mColorFilter != null && mColorFilterEnable && mColorFilterId > 0 && mPGLFramebuffers != null && mPGLFramebuffers.length > 0) {
            mPGLFramebuffers[0].bind(true);
            hasBindFramebuffer = true;
        }
        if (!hasBindFramebuffer && narrow != 1.0f) {
            GLES20.glViewport(0, 0, mScreenW, mScreenH);
        }
        mBeautyFilter.draw(textureId, transformMatrix, viewProjectionMatrix, mGLCubeBuffer, mGLTextureFlipBuffer, mBeautyFilterEnable);
        if (hasBindFramebuffer) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        }

        if (mColorFilter != null && mColorFilterEnable && hasBindFramebuffer && mPGLFramebuffers != null && mPGLFramebuffers.length > 0) {
//            Log.e(TAG, "mColor draw");
            if (narrow != 1.0f) {
                GLES20.glViewport(0, 0, mScreenW, mScreenH);
            }
            mColorFilter.draw(mPGLFramebuffers[0].get_textureid(), mGLCubeBuffer, mGLTextureBuffer);
        }
    }

    private PFFilter getColorFilter(int width, int height, Integer filterType) {
        if (mColorFilterCache == null) {
            mColorFilterCache = new HashMap<Integer, PFFilter>();
        }
        PFFilter mColorFilter = null;
        if (mColorFilterCache.containsKey(filterType)) {
            mColorFilter = mColorFilterCache.get(filterType);
        }
        if (mColorFilter != null && mColorFilter.Isinit()) {
            return mColorFilter;
        }
        switch (filterType) {
            //   0         1          2         3         4            5          6        7           8          9       10
            // "None", "Jasmine", "Camellia", "Rosa", "Lavender", "Sunflower", "Clover", "Peach", "Dandelion", "Lilac", "Tulip"
            case 0:
                break;
            case 1:
                PFColorFilterJasmine jasmineFilter = new PFColorFilterJasmine(mContext);
                if (!jasmineFilter.Isinit()) {
                    jasmineFilter.init();
                    jasmineFilter.onOutputSizeChanged(width, height);
                    mColorFilter = jasmineFilter;
                }
                break;
            case 2:
                PFColorFilterCmillia cmilliaFilter = new PFColorFilterCmillia(mContext);
                if (!cmilliaFilter.Isinit()) {
                    cmilliaFilter.init();
                    cmilliaFilter.onOutputSizeChanged(width, height);
                    cmilliaFilter.setTexture1(getMaskTextureId(mContext, R.drawable.crazy01_mask1));
                    mColorFilter = cmilliaFilter;
                }
                break;
            case 3:
                PFColorFilterRosa rosaFilter = new PFColorFilterRosa(mContext);
                if (!rosaFilter.Isinit()) {
                    rosaFilter.init();
                    rosaFilter.onOutputSizeChanged(width, height);
                    mColorFilter = rosaFilter;
                }
                break;
            case 4:
                PFColorFilterLavender lavenderFilter = new PFColorFilterLavender(mContext);
                if (!lavenderFilter.Isinit()) {
                    lavenderFilter.init();
                    lavenderFilter.onOutputSizeChanged(width, height);
                    lavenderFilter.setTexture1(getMaskTextureId(mContext, R.drawable.crazy05_mask1));
                    lavenderFilter.setTexture2(getMaskTextureId(mContext, R.drawable.crazy05_mask2));
                    mColorFilter = lavenderFilter;
                }
                break;
            case 5:
                PFColorFilterSunflower sunflowerFilter = new PFColorFilterSunflower(mContext);
                if (!sunflowerFilter.Isinit()) {
                    sunflowerFilter.init();
                    sunflowerFilter.onOutputSizeChanged(width, height);
                    sunflowerFilter.setTexture1(getMaskTextureId(mContext, R.drawable.crazy07_mask1));
                    sunflowerFilter.setTexture2(getMaskTextureId(mContext, R.drawable.crazy07_mask2));
                    mColorFilter = sunflowerFilter;
                }
                break;
            case 6:
                PFColorFilterClover cloverFilter = new PFColorFilterClover(mContext);
                if (!cloverFilter.Isinit()) {
                    cloverFilter.init();
                    cloverFilter.onOutputSizeChanged(width, height);
                    cloverFilter.setTexture1(getMaskTextureId(mContext, R.drawable.crazy10_mask1));
                    mColorFilter = cloverFilter;
                }
                break;
            case 7:
                PFColorFilterPeach peachFilter = new PFColorFilterPeach(mContext);
                if (!peachFilter.Isinit()) {
                    peachFilter.init();
                    peachFilter.onOutputSizeChanged(width, height);
                    peachFilter.setTexture1(getMaskTextureId(mContext, R.drawable.crazy02_mask1));
                    mColorFilter = peachFilter;
                }
                break;
            case 8:
                PFColorFilterDandelion dandelionFilter = new PFColorFilterDandelion(mContext);
                if (!dandelionFilter.Isinit()) {
                    dandelionFilter.init();
                    dandelionFilter.onOutputSizeChanged(width, height);
                    mColorFilter = dandelionFilter;
                }
                break;
            case 9:
                PFColorFilterLilac lilacFilter = new PFColorFilterLilac(mContext);
                if (!lilacFilter.Isinit()) {
                    lilacFilter.init();
                    lilacFilter.onOutputSizeChanged(width, height);
                    mColorFilter = lilacFilter;
                }
                break;
            case 10:
                PFColorFilterTulip tulipFilter = new PFColorFilterTulip(mContext);
                if (!tulipFilter.Isinit()) {
                    tulipFilter.init();
                    tulipFilter.onOutputSizeChanged(width, height);
                    mColorFilter = tulipFilter;
                }
                break;
        }
        if (mColorFilter != null) {
            mColorFilterCache.put(filterType, mColorFilter);
        }
        return mColorFilter;
    }

    public int getMaskTextureId(Context context, int masks) {
        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), masks);
        PGLTexture texture = new PGLTexture();
        texture.bind(bmp, true);
        return texture.getId();
    }

    @Override
    public void onDestroy() {
        if (mPGLFramebuffers != null) {
            for (int i = 0; i < mPGLFramebuffers.length; ++i) {
                if (mPGLFramebuffers[i] != null) {
                    mPGLFramebuffers[i].destroy();
                }
            }
            mPGLFramebuffers = null;
        }
        if (mBeautyFilter != null) {
            mBeautyFilter.destroy();
        }
        if (mColorFilter != null) {
            mColorFilter.destroy();
        }

        if (mColorFilterCache != null) {
//            Log.i(TAG, "mColorFilterCache size:"+ mColorFilterCache.size());
            for (Map.Entry<Integer, PFFilter> entry : mColorFilterCache.entrySet()) {
                PFFilter filter = entry.getValue();
                if (filter != null) {
                    filter.destroy();
                    filter = null;
                }
            }
        }
        super.onDestroy();
    }
}
