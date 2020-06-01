package cn.poco.glfilter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.poco.gldraw2.FaceDataHelper;
import cn.poco.glfilter.base.AbstractFilter;
import cn.poco.glfilter.base.GlUtil;
import cn.poco.glfilter.base.TextureRotationUtils;
import cn.poco.glfilter.base.VertexArray;
import cn.poco.image.PocoFace;
import my.beautyCamera.R;

/**
 * Created by zwq on 2016/07/28 13:49.<br/><br/>
 * 此 filter 用来显示最终图像、人脸框、水印<br/>
 * 16:9 裁剪适配到17:9、17.25:9、18:9、18.5:9
 */
public class DisplayFilter extends AbstractFilter {

    private static final String TAG = "vvv DisplayFilter";

    private static final String vertexShaderCode =
            "attribute vec4 aPosition;\n" +
                    "attribute vec2 aTextureCoord;\n" +
                    "attribute float aTextureId;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = aPosition;\n" +
                    "    vTextureCoord = aTextureCoord;\n" +
                    "}";

    private static final String fragmentShaderCode =
            "precision mediump float;\n" +
                    "uniform sampler2D uTexture0;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(uTexture0, vTextureCoord);\n" +
                    "}";

    private int muTexture0;

    private int mFaceRectTextureId;
    private int mWaterMarkTextureId;

    private float mViewHWRatio;
    private VertexArray mDisplayRect;

    private boolean mFaceRectEnable;
    private VertexArray mFaceRectVertexArray;

    private Object mWaterMarkRes = R.drawable.sticker_video_watermark;
    private boolean mIs1080P;

    private boolean mWaterMarkEnable;
    private VertexArray mWaterMarkVertexArray;
    private float mTextSize = 40.0f;
    private Rect mTextBound;
    private SimpleDateFormat mSimpleDateFormat;
    private String mDate;
    private Bitmap mWaterMarkBmp;
    private boolean mWaterMarkHasDate;
    private int mOrientation;
    private float mVideoRatio;
    private float mTopOffsetRatio;
    private float mBottomOffsetRatio;

    public DisplayFilter(Context context) {
        super(context);

        mDisplayRect = new VertexArray();

        //左下角水印位置
        float[] mWaterMarkVertexData = new float[]{-1.0f, -0.85972226f, 0.0f, -0.85972226f, -1.0f, -1.0f, 0.0f, -1.0f};//4.0f / 3;
        mWaterMarkVertexArray = new VertexArray(mWaterMarkVertexData);

        //test
//        mWaterMarkRes = R.drawable.__wat__06_res;
//        mIs1080P = true;
    }

    @Override
    public int getTextureTarget() {
        return GLES20.GL_TEXTURE_2D;
    }

    @Override
    protected int createProgram(Context context) {
        return GlUtil.createProgram(vertexShaderCode, fragmentShaderCode);
    }

    @Override
    protected void getGLSLValues() {
        super.getGLSLValues();
        muTexture0 = GLES20.glGetUniformLocation(mProgramHandle, "uTexture0");
    }

    @Override
    public void setViewSize(int width, int height) {
        super.setViewSize(width, height);
        if (width > 0 && height > 0) {
            mViewHWRatio = height * 1.0f / width;
        }
        float[] textureVertices = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (mViewHWRatio > 16.0f / 9) {
            //镜头输出16:9 裁剪适配到17:9、17.25:9、18:9、18.5:9
            //int realW = Math.round(mWidth * 16.0f / 9 / mHeight * mWidth);
            //int xOffset = (mWidth - realW) / 2;

            float xOffset = (mWidth - Math.round((mWidth * 16.0f / 9) / mHeight * mWidth)) / 2.0f / mWidth;
            textureVertices = new float[]{xOffset, 0.0f, 1.0f - xOffset, 0.0f, xOffset, 1.0f, 1.0f - xOffset, 1.0f};
            //textureVertices = new float[]{0.055555f, 0.0f, 0.944444f, 0.0f, 0.055555f, 1.0f, 0.944444f, 1.0f};

        } else if (mViewHWRatio > 4.0f / 3 && mViewHWRatio < 16.0f / 9) {
            //镜头输出4:3(12:9) 裁剪适配到13.5:9、14.4:9、15:9
            float xOffset = (mWidth - Math.round((mWidth * 4.0f / 3) / mHeight * mWidth)) / 2.0f / mWidth;
            textureVertices = new float[]{xOffset, 0.0f, 1.0f - xOffset, 0.0f, xOffset, 1.0f, 1.0f - xOffset, 1.0f};
        }
        if (mDisplayRect == null) {
            mDisplayRect = new VertexArray();
        }
        float[] vertexData = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f};
        mDisplayRect.updateBuffer(vertexData, textureVertices);

        calculateTextBounds();
        setWaterMarkYOffset(mViewHWRatio, 0.0f, 0.0f);
    }

    @Override
    public boolean isNeedBlend() {
        return true;
    }

    public void setFaceRectEnable(boolean enable) {
        mFaceRectEnable = enable;
        if (mFaceRectEnable && mFaceRectVertexArray == null) {
            mFaceRectVertexArray = new VertexArray();
        }
    }

    public void setWaterMarkEnable(boolean enable) {
        mWaterMarkEnable = enable;
    }

    public void setWaterMarkRes(Object res) {
        if (res != null) {
            mWaterMarkRes = res;
        }
    }

    private void calculateTextBounds() {
        float sr = Math.round(mWidth / mRenderScale) / 1080.0f;
        String text = "0000-00-00";
        mTextSize = Math.round(30.0f * sr);
        Paint paint = new Paint();
        paint.setTextSize(mTextSize);
        mTextBound = new Rect();
        paint.getTextBounds(text, 0, text.length(), mTextBound);

        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        mTextBound.bottom = Math.round(-fontMetrics.top);
        mTextBound.top = Math.round(fontMetrics.bottom);
        //Log.i(TAG, "getTextBounds: "+mTextBound.height()+", "+(fontMetrics.descent - fontMetrics.ascent)+", "+fontMetrics.top+", "+fontMetrics.bottom+", "+fontMetrics.ascent+", "+fontMetrics.descent+", "+fontMetrics.leading);
    }

    public void prepareWaterMark(boolean hasDate) {
        mWaterMarkHasDate = hasDate;
        if (mSimpleDateFormat == null) {
            mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        }
        String date = mSimpleDateFormat.format(new Date());
        if (mDate == null || !date.equals(mDate)) {
            mDate = date;
            if (mWaterMarkBmp != null) {
                mWaterMarkBmp.recycle();
                mWaterMarkBmp = null;
            }
            //计算图片高度
            if (mTextBound == null) {
                calculateTextBounds();
            }
            Rect textWH = mTextBound;
            if (textWH != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                if (mWaterMarkRes instanceof Integer) {
                    BitmapFactory.decodeResource(mContext.getResources(), (Integer) mWaterMarkRes, options);
                } else if (mWaterMarkRes instanceof String) {
                    BitmapFactory.decodeFile((String) mWaterMarkRes, options);
                }
                options.inJustDecodeBounds = false;
                float watermarkSrcW = options.outWidth;
                float watermarkSrcH = options.outHeight;
                float dpiScale = 1.0f;
                if (mIs1080P) {
                    dpiScale = 720.f / 1080;
                }

                //watermark w:119 h:32  720p
                //          w:178 h:48  960p
                float sr = Math.round(mWidth / mRenderScale) / 720.0f;
                float watermarkW = Math.round(options.outWidth * dpiScale) * sr;
                float watermarkH = Math.round(options.outHeight * dpiScale) * sr;
                float scale = watermarkW / watermarkSrcW;// watermarkSrcW->real width

                float left = 22.0f * sr;
                float top = 0.0f;
                float bottom = 24.0f * sr;

                int destW = Math.round(360 * sr);
                int destH = Math.round(watermarkH + 12 * sr + textWH.height() + bottom);
                if (!mWaterMarkHasDate) {
                    top = 12 * sr + textWH.height();
                }
                //Log.i(TAG, "prepareWaterMark: "+sr+", "+destW+", "+destH+", "+left+", "+top+", "+bottom+", "+textWH.height());

                mWaterMarkBmp = Bitmap.createBitmap(destW, destH, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mWaterMarkBmp);
                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
                //canvas.drawColor(0xffff0000);//test

                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setFilterBitmap(true);

                Bitmap srcBmp = getBitmap(mWaterMarkRes, 1);
                if (srcBmp != null && !srcBmp.isRecycled()) {
                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);
                    matrix.postTranslate(left, top);
                    canvas.drawBitmap(srcBmp, matrix, paint);
                    srcBmp.recycle();
                    srcBmp = null;
                }

                if (mWaterMarkHasDate) {
                    float xOffset = -1.33333f * sr;

                    paint.reset();
                    paint.setAntiAlias(true);
                    paint.setTextSize(mTextSize);
                    paint.setColor(0xffffffff);
                    paint.setShadowLayer(2, 1, 1, 0x26000000);
                    canvas.drawText(mDate, left + (watermarkW - textWH.width()) / 2 + xOffset, destH - bottom, paint);
                }
            }
        }
    }

    /**
     * 水印方向
     * @param orientation 逆时针方向 0，90，180，270
     */
    public void setWaterMarkOrientation(int orientation) {
        if (orientation != mOrientation) {
            mOrientation = orientation;
            if (mWaterMarkEnable && mWidth > 0 && mHeight > 0 && mVideoRatio > 0) {
                calculateVertexPoint(mHeight * 1.0f / mWidth, mVideoRatio, mTopOffsetRatio, mBottomOffsetRatio);
            }
        }
    }

    public void setWaterMarkYOffset(float videoRatio, float topOffset, float bottomOffset) {
        if (mWidth > 0 && mHeight > 0) {
            mVideoRatio = videoRatio;
            mTopOffsetRatio = topOffset / Math.round(mHeight / mRenderScale) * 2;
            mBottomOffsetRatio = bottomOffset / Math.round(mHeight / mRenderScale) * 2;
            calculateVertexPoint(mHeight * 1.0f / mWidth, mVideoRatio, mTopOffsetRatio, mBottomOffsetRatio);
        }
    }

    private void calculateVertexPoint(float hwRatio, float videoRatio, float topOffsetRatio, float bottomOffsetRatio) {
        float sr = Math.round(mWidth / mRenderScale) / 720.0f;
        float destW = 360 * sr;// 720 / 2
        float destH = 100 * sr;
        if (mWaterMarkBmp != null && !mWaterMarkBmp.isRecycled()) {
            destW = mWaterMarkBmp.getWidth();
            destH = mWaterMarkBmp.getHeight();
        }

        float scale = 1.0f;
        if (videoRatio < 1.0f) {//9.0f / 16
            scale = 0.75f;//0.8f;
        }
        float ratio = destH * 1.0f / destW;
        float realW = 1.0f * scale;//0.5f * 1.0f
        float realH = realW * ratio / hwRatio;
        if (mOrientation == 90 || mOrientation == 270) {
            realW = 1.0f / hwRatio * scale;
            if (videoRatio < 1.0f) {
                 //realW = realW * (mWidth * videoRatio / mWidth);
                realW *= videoRatio;
            }
            realH = realW * ratio * hwRatio;
        }

        float x1 = 0, x2 = 0, x3 = 0, x4 = 0;
        float y1 = 0, y2 = 0, y3 = 0, y4 = 0;

        float[] texture = {0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f};
        if (mOrientation == 90) {
            x2 = 1.0f;
            x1 = x2 - realH;
            x3 = x1;
            x4 = x2;

            y1 = -1.0f + bottomOffsetRatio;
            y3 = y1 + realW;
            y2 = y1;
            y4 = y3;

            texture = TextureRotationUtils.getRotation(mOrientation, texture);

        } else if (mOrientation == 180) {
            x2 = 1.0f;
            x1 = x2 - realW;
            x3 = x1;
            x4 = x2;

            y3 = 1.0f - topOffsetRatio;
            y1 = y3 - realH;
            y2 = y1;
            y4 = y3;

            texture = TextureRotationUtils.getRotation(mOrientation, texture);

        } else if (mOrientation == 270) {
            x1 = -1.0f;
            x2 = x1 + realH;
            x3 = x1;
            x4 = x2;

            y3 = 1.0f - topOffsetRatio;
            y1 = y3 - realW;
            y2 = y1;
            y4 = y3;

            texture = TextureRotationUtils.getRotation(mOrientation, texture);

        } else {
            x1 = -1.0f;
            x2 = x1 + realW;
            x3 = x1;
            x4 = x2;

            y1 = -1.0f + bottomOffsetRatio;
            y3 = y1 + realH;
            y2 = y1;
            y4 = y3;
        }

        float[] vertex = new float[]{x3, y3, x4, y4, x1, y1, x2, y2};

        if (mWaterMarkVertexArray == null) {
            mWaterMarkVertexArray = new VertexArray();
        }
        mWaterMarkVertexArray.updateBuffer(vertex, texture);
    }

    @Override
    public void onDraw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex, int vertexCount, int coordsPerVertex,
                       int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        useProgram();
        loadTexture();

        //原始图像
        bindTexture(textureId);
        if (!mIsRecord && mDisplayRect != null) {
            bindGLSLValues(mvpMatrix, mDisplayRect.vertexBuffer, coordsPerVertex, vertexStride, texMatrix, mDisplayRect.textureVerticesBuffer, texStride);
        } else {
            bindGLSLValues(mvpMatrix, vertexBuffer, coordsPerVertex, vertexStride, texMatrix, texBuffer, texStride);
        }
        drawArrays(firstVertex, vertexCount);

        //人脸框
        drawFaceRect(mvpMatrix, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texStride);

        //水印
        drawWaterMark(mvpMatrix, firstVertex, vertexCount, coordsPerVertex, vertexStride, texMatrix, texStride);

        unbindGLSLValues();
        unbindTexture();
        disuseProgram();
    }

    private void drawFaceRect(float[] mvpMatrix, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, int texStride) {
        if (!mIsRecord && mFaceRectEnable && mFaceRectTextureId > 0 && mFaceRectVertexArray != null) {
            bindTexture(mFaceRectTextureId);

            int size = FaceDataHelper.getInstance().getFaceSize();
            PocoFace face = null;
            for (int i = 0; i < size; i++) {
                face = FaceDataHelper.getInstance().changeFace(i).getFace();
                if (face != null && face.mGLRect != null) {
                    float[] vertexPoint = {
                            face.mGLRect.left, face.mGLRect.top,
                            face.mGLRect.right, face.mGLRect.top,
                            face.mGLRect.left, face.mGLRect.bottom,
                            face.mGLRect.right, face.mGLRect.bottom
                    };
                    mFaceRectVertexArray.updateBuffer(vertexPoint, 0, vertexPoint.length);
                    bindGLSLValues(mvpMatrix, mFaceRectVertexArray.vertexBuffer, coordsPerVertex, vertexStride, texMatrix, mFaceRectVertexArray.textureVerticesBuffer, texStride);
                    drawArrays(firstVertex, vertexCount);
                }
            }
        }
    }

    private void drawWaterMark(float[] mvpMatrix, int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride, float[] texMatrix, int texStride) {
        if (mIsRecord && mWaterMarkEnable && mWaterMarkTextureId > 0 && mWaterMarkVertexArray != null) {
            bindTexture(mWaterMarkTextureId);

            mWaterMarkVertexArray.resetPosition();
            bindGLSLValues(mvpMatrix, mWaterMarkVertexArray.vertexBuffer, coordsPerVertex, vertexStride, texMatrix, mWaterMarkVertexArray.textureVerticesBuffer, texStride);
            drawArrays(firstVertex, vertexCount);
        }
    }

    private void loadTexture() {
        if (!mIsRecord && mFaceRectEnable && mFaceRectTextureId <= 0) {
            mFaceRectTextureId = getBitmapTextureId(R.drawable.camera_face_rect);
        }

        if (mIsRecord && mWaterMarkEnable && mWaterMarkTextureId <= 0) {
            if (mWaterMarkBmp != null && !mWaterMarkBmp.isRecycled()) {
                mWaterMarkTextureId = GlUtil.createTexture(GLES20.GL_TEXTURE_2D, mWaterMarkBmp);
            } else {
                mWaterMarkTextureId = getBitmapTextureId(mWaterMarkRes);
            }
        }
    }

    @Override
    protected void bindTexture(int textureId) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(muTexture0, 0);
    }

    @Override
    protected void bindGLSLValues(float[] mvpMatrix, FloatBuffer vertexBuffer, int coordsPerVertex, int vertexStride, float[] texMatrix, FloatBuffer texBuffer, int texStride) {
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GLES20.glVertexAttribPointer(maTextureCoordLoc, coordsPerVertex, GLES20.GL_FLOAT, false, texStride, texBuffer);
    }

    @Override
    protected void drawArrays(int firstVertex, int vertexCount) {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
    }

    @Override
    protected void unbindGLSLValues() {
        super.unbindGLSLValues();
    }

    @Override
    protected void unbindTexture() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
    }

    @Override
    public void loadNextTexture(boolean load) {

    }

    @Override
    public void releaseProgram() {
        super.releaseProgram();
        GLES20.glDeleteProgram(mProgramHandle);
        if (mFaceRectTextureId > 0 || mWaterMarkTextureId > 0) {
            GLES20.glDeleteTextures(2, new int[]{mFaceRectTextureId, mWaterMarkTextureId}, 0);
        }
        mProgramHandle = -1;
        mWaterMarkVertexArray = null;
        mFaceRectVertexArray = null;
    }
}