package cn.poco.glfilter.shape;

import android.content.Context;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.DefaultFilter;

/**
 * Created by zwq on 2017/09/11 16:47.<br/><br/>
 * 旧版贴纸变形
 * 17 自然美
 * 18 气场女王
 * 19 网红脸
 * 20 卡通萌神
 * 21 嘟嘟脸
 * 22 激萌少女
 * 23 呆萌甜心
 * 24 气质女神
 */
public class SuperShapeFilter extends FaceShapeFilter {

    private final int Nose = 0;
    private final int Mouth = 1;
    private final int SuperCrazy = 2;
    private final int ChinMask = 3;
    private final int Chin = 4;

    protected HashMap<Object, DefaultFilter> mFilterCache;
    private int mFilterId;
    private ShapeInfoData mShapeInfoData;

    public SuperShapeFilter(Context context) {
        super(context);
    }

    @Override
    protected int createProgram(Context context) {
        if (mFilterCache == null) {
            mFilterCache = new HashMap<>();
        }
        DefaultFilter filter = null;
        try {
            filter = new CrazyNoseShapeFilter(context);
            mFilterCache.put(Integer.valueOf(Nose), filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyMouthShapeFilter(context);
            mFilterCache.put(Integer.valueOf(Mouth), filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            filter = new SuperCrazyShapeFilter(context);
            mFilterCache.put(Integer.valueOf(SuperCrazy), filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyChinMaskShapeFilter(context);
            mFilterCache.put(Integer.valueOf(ChinMask), filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            filter = new CrazyChinShapeFilter(context);
            mFilterCache.put(Integer.valueOf(Chin), filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mFilterCache.size();
    }

    @Override
    protected void checkProgram() {
        if (mProgramHandle == 0) {
            throw new RuntimeException("Unable to create program");
        }
    }

    @Override
    public void setRenderScale(float renderScale) {
        super.setRenderScale(renderScale);
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().setRenderScale(renderScale);
                }
            }
        }
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().setViewSize(width, height);
                }
            }
        }
    }

    @Override
    public boolean isNeedFlipTexture() {
        return false;
    }

    public void setShapeFilterId(int filterId) {
        if (filterId != mFilterId || mShapeInfoData == null) {
            mFilterId = filterId;
            mShapeInfoData = ShapeInfoData.getShapeInfoById(mFilterId);
        }
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        if (mFilterCache == null || mFilterCache.isEmpty() || mGLFramebuffer == null) {
            return;
        }

        if (!mUseOtherFaceData && mPocoFace == null) {
            int faceSize = FaceDataHelper.getInstance().getFaceSize();
            if (faceSize < 1) {
                faceSize = 1;
            }
            int drawCount = 0;
            for (int i = 0; i < faceSize; i++) {
                mPocoFace = FaceDataHelper.getInstance().changeFace(i).getFace();

                drawCount = drawSubShape(i, false, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);

                /*if (drawCount > 0 && i > 0 && i == faceSize - 1 && mGLFramebuffer != null) {
                    mGLFramebuffer.setHasBind(false);
                }*/
            }
            if (((faceSize == 1 && drawCount > 1) || (faceSize > 1 && drawCount > 0)) && mGLFramebuffer != null) {
                mGLFramebuffer.setHasBind(false);
            }

            mDefaultTextureId = textureId;
            mGLFramebuffer = null;

        } else {

            drawSubShape(0, true, mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, textureId, texStride);
        }

        mUseOtherFaceData = false;
        mPocoFace = null;
    }

    public int drawSubShape(int faceIndex, boolean hasBind, float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        DefaultFilter filter = null;
        int subIndex = 0;
        int frameBufferId = textureId;
        int chinMaskId = 0;

        for (int i = 0; i < 5; i++) {
            if (subIndex > 0 && mPocoFace == null) {
                continue;
            }
            filter = mFilterCache.get(Integer.valueOf(i));
            if (filter == null) {
                continue;
            }
            if ((faceIndex == 0 && subIndex > 0) || faceIndex > 0) {
                mGLFramebuffer.bindNext(true);
                if (i == Chin) {
                    // frameBufferId is before mask
                    chinMaskId = mGLFramebuffer.getPreviousTextureId();
                } else {
                    frameBufferId = mGLFramebuffer.getPreviousTextureId();
                }
            }
            subIndex++;

            if (filter instanceof CrazyNoseShapeFilter) {
                ((CrazyNoseShapeFilter) filter).setFaceData(mPocoFace);
                ((CrazyNoseShapeFilter) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyMouthShapeFilter) {
                ((CrazyMouthShapeFilter) filter).setFaceData(mPocoFace);
                ((CrazyMouthShapeFilter) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof SuperCrazyShapeFilter) {
                ((SuperCrazyShapeFilter) filter).setFaceData(mPocoFace);
                ((SuperCrazyShapeFilter) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyChinMaskShapeFilter) {
                ((CrazyChinMaskShapeFilter) filter).setFaceData(mPocoFace);
                ((CrazyChinMaskShapeFilter) filter).setShapeData(mShapeInfoData);

            } else if (filter instanceof CrazyChinShapeFilter) {
                ((CrazyChinShapeFilter) filter).setMaskTextureId(chinMaskId);
            }

            filter.onDraw(mvpMatrix, vertexBuffer, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texBuffer, frameBufferId, texStride);

        }
        if (hasBind && subIndex > 1 /*&& i == size - 1*/ && mGLFramebuffer != null) {
            mGLFramebuffer.setHasBind(false);
        }
        return subIndex;
    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        if (mFilterCache != null) {
            for (Map.Entry<Object, DefaultFilter> entry : mFilterCache.entrySet()) {
                if (entry.getValue() != null) {
                    entry.getValue().releaseProgram();
                }
            }
            mFilterCache.clear();
            mFilterCache = null;
        }
    }
}
