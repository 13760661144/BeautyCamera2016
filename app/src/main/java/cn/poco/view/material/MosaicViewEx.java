package cn.poco.view.material;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;

import cn.poco.image.PocoOilMask;
import cn.poco.mosaic.MosicBeautifyTools;
import cn.poco.resource.MosaicRes;
import cn.poco.tianutils.ShareData;
import cn.poco.view.beauty.BeautyViewEx;

/**
 * Created by admin on 2017/3/13.
 */

public class MosaicViewEx extends BeautyViewEx
{
	public static final int MODE_MOSAIC = 0;
	public static final int MODE_DOODLE = 1;
	private int MODE = MODE_MOSAIC;
	private int mMosaicType = -1;

	private boolean isRubberMode;

	private Bitmap mMosaicOrgBmp;//马赛克原图，用于合成

	private Bitmap mDoodleBmp;
	private Path mPath;
	private Shader mBitmapShader;
	private Matrix mShaderMatrix;
	private Bitmap mOrgBmp;//最原始的图片，用于橡皮擦还原

	public int def_color = 0xffffffff;
	private int m_sonWinRadius;
	private int m_sonWinOffset;
	private int m_sonWinBorder;
	private int m_sonWinX = 0;
	private int m_sonWinY = 0;
	private int mCircleStroke;
	private Bitmap m_SonWinBmp;
	private PointF mMagnifyPoint; // 用于平移放大镜的视野
	private PointF mLastPoint;

	protected boolean isDrawMagnify = false;
	protected boolean isOddTouch = false;
	protected boolean saveCache = false;
	protected boolean isCanvasChanged = false;

	private Bitmap mActionDownBeforeBmp;//保存down事件之前的bitmap
	private boolean isSingleMove = false;
	private PathMeasure mPathMeasure;

	private int mPaintSize;// 纹理和橡皮擦的笔触大小
	private int mMosaicPaintSize = 50;// 马赛克的笔触大小，范围0~100;
	private boolean isPaintSizeChanging = false;

	PaintFlagsDrawFilter temp_filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

	private ControlCallback mCallBack;

	public MosaicViewEx(Context context)
	{
		super(context);
	}

	@Override
	protected void InitData()
	{
		super.InitData();

		mLastPoint = new PointF();
		mMagnifyPoint = new PointF();
		mPathMeasure = new PathMeasure();

		mCircleStroke = ShareData.PxToDpi_xhdpi(3);

		m_sonWinRadius = (int) (ShareData.m_screenWidth * 0.145f);
		m_sonWinOffset = ShareData.PxToDpi_xhdpi(10);
		m_sonWinBorder = ShareData.PxToDpi_xhdpi(5);
	}

	public void setOnControlCallback(ControlCallback cb)
	{
		mCallBack = cb;
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();
		mBitmapShader = null;
		mLastPoint = null;
		mMagnifyPoint = null;
		mPathMeasure = null;
		mPath.reset();
		mPath = null;

		if (mOrgBmp != null && mOrgBmp.isRecycled()) {
			mOrgBmp.recycle();
			mOrgBmp = null;
		}
		if (mDoodleBmp != null && !mDoodleBmp.isRecycled()) {
			mDoodleBmp.recycle();
			mDoodleBmp = null;
		}
		if (mMosaicOrgBmp != null && !mMosaicOrgBmp.isRecycled()) {
			mMosaicOrgBmp.recycle();
			mMosaicOrgBmp = null;
		}
		if (m_SonWinBmp != null && !m_SonWinBmp.isRecycled()) {
			m_SonWinBmp.recycle();
			m_SonWinBmp = null;
		}
		releaseActionDownBmp();
	}

	private void releaseActionDownBmp() {
		if (mActionDownBeforeBmp != null && !mActionDownBeforeBmp.isRecycled()) {
			mActionDownBeforeBmp.recycle();
			mActionDownBeforeBmp = null;
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.save();
		canvas.setDrawFilter(temp_filter);
		super.onDraw(canvas);

		if(isDrawMagnify)
		{
			drawMagnify();
		}

		if(isPaintSizeChanging && !isOddTouch)
		{
			canvas.save();
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(def_color);
			mPaint.setStrokeWidth(mCircleStroke);
			canvas.drawCircle(getWidth()/2f, getHeight()/2f, ShareData.PxToDpi_xhdpi(mPaintSize / 2), mPaint);
			canvas.restore();
		}
		canvas.restore();
	}

	private void drawMagnify()
	{
		int size = m_sonWinRadius * 2;

		if (m_SonWinBmp == null)
		{
			m_SonWinBmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		}
		Canvas canvas = new Canvas(m_SonWinBmp);
		canvas.setDrawFilter(temp_filter);

		//清理
		canvas.drawColor(0, PorterDuff.Mode.CLEAR);

		//draw mask
		mPaint.reset();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(0xffffffff);
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		canvas.drawRoundRect(new RectF(m_sonWinOffset, m_sonWinOffset, size - m_sonWinOffset, size - m_sonWinOffset), m_sonWinBorder << 1, m_sonWinBorder << 1, mPaint);

		// draw img
		canvas.save();
		canvas.translate(mCanvasX, mCanvasY);
		canvas.concat(global.m_matrix);
		mPaint.reset();
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		Matrix temp_matrix = new Matrix();

		float[] src = new float[]{size/2f, size/2f, mMagnifyPoint.x, mMagnifyPoint.y};
		float[] dst = new float[src.length];
		Matrix[] matrices = new Matrix[]{global.m_matrix};
		invMatrixCount(dst,src,matrices);
		temp_matrix.set(img.m_matrix);
		temp_matrix.postTranslate(dst[0] - dst[2],dst[1] - dst[3]);
		canvas.drawBitmap(img.m_bmp,temp_matrix,mPaint);
		canvas.restore();

		// draw 圆
		canvas.save();
		mPaint.reset();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(def_color);
		mPaint.setStrokeWidth(mCircleStroke);
		canvas.drawCircle(size / 2f, size / 2f, ShareData.PxToDpi_xhdpi(mPaintSize / 2), mPaint);
		canvas.restore();

		if(mCallBack != null)
		{
			mCallBack.updateSonWin(m_SonWinBmp,m_sonWinX,m_sonWinY);
		}

		canvas.setBitmap(null);
	}

	protected void RefreshSonWinPos(float x, float y)
	{
		int size = m_sonWinRadius * 2;
		if (x < size && y < size) {
			m_sonWinX = getWidth() - size;
			m_sonWinY = 0;
		} else if (x > getWidth() - size && y < size) {
			m_sonWinX = 0;
			m_sonWinY = 0;
		}
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		isDrawMagnify = true;
		isOddTouch = true;
		isSingleMove = false;

		if (MODE == MODE_MOSAIC) {
			PocoOilMask.initOilMaskBitmap(getContext(), mMosaicType);
		}

		if(isOddTouch)
		{
			releaseActionDownBmp();
			mActionDownBeforeBmp = img.m_bmp.copy(Bitmap.Config.ARGB_8888, true);

			PointF point = new PointF(mDownX, mDownY);
			mMagnifyPoint.set(mDownX, mDownY);
			getLogicPos(point, point);
			Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
			invMatrixCount(point, point, matrices);

			mLastPoint = point;
			mPath.reset();
			mPath.moveTo(mLastPoint.x, mLastPoint.y);

			if (isRubberMode || MODE == MODE_DOODLE) {
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setFilterBitmap(true);
				mPaint.setStyle(Paint.Style.STROKE);
				mPaint.setStrokeCap(Paint.Cap.ROUND);
				mPaint.setStrokeJoin(Paint.Join.ROUND);
				mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(mPaintSize) / getPaintScale());

				if (isRubberMode) {
					// 擦除
					erasePoint();
				} else {
					// 涂鸦
					doodlePoint();
				}
			} else {
				MosicBeautifyTools.getMosicBmp(img.m_bmp, mMosaicOrgBmp, (int)mLastPoint.x, (int)mLastPoint.y, (int) (ShareData.PxToDpi_xhdpi(mMosaicPaintSize) / getPaintScale()), mMosaicType);
			}
			saveCache = true;

			RefreshSonWinPos(mDownX, mDownY);

			invalidate();
		}

		if(mCallBack != null)
		{
			mCallBack.fingerDown();
		}
	}

	private void erasePoint()
	{
		Canvas canvas = new Canvas(img.m_bmp);
		canvas.setDrawFilter(temp_filter);
		Matrix mMatrix = new Matrix();
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawPoint(mLastPoint.x, mLastPoint.y, mPaint);

		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		canvas.drawBitmap(mOrgBmp, mMatrix, mPaint);

		canvas.setBitmap(null);
	}

	private void doodlePoint()
	{
		Canvas canvas = new Canvas(img.m_bmp);
		canvas.setDrawFilter(temp_filter);
		mPaint.setShader(mBitmapShader);
		canvas.drawPoint(mLastPoint.x, mLastPoint.y, mPaint);
		canvas.setBitmap(null);
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		if(isOddTouch)
		{
			PointF newPoint = new PointF(event.getX(), event.getY());
			mMagnifyPoint.set(event.getX(), event.getY());
			getLogicPos(newPoint,newPoint);
			Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
			invMatrixCount(newPoint, newPoint, matrices);

			float dx = Math.abs(newPoint.x - mLastPoint.x);
			float dy = Math.abs(newPoint.x - mLastPoint.y);

			if (dx >= 4 || dy >= 4) {
				isSingleMove = true;
				mPath.quadTo(mLastPoint.x,mLastPoint.y, (newPoint.x + mLastPoint.x)/2, (newPoint.y + mLastPoint.y)/2);
				if (isRubberMode || MODE == MODE_DOODLE) {
					mPaint.reset();
					mPaint.setAntiAlias(true);
					mPaint.setFilterBitmap(true);
					mPaint.setStyle(Paint.Style.STROKE);
					mPaint.setStrokeCap(Paint.Cap.ROUND);
					mPaint.setStrokeJoin(Paint.Join.ROUND);
					mPaint.setStrokeWidth(ShareData.PxToDpi_xhdpi(mPaintSize) / getPaintScale());
					if (isRubberMode) {
						// 擦除
						erase();
					} else {
						// 涂鸦
						doodle();
					}
				} else {
					MosicBeautifyTools.getMosicBmp(img.m_bmp, mMosaicOrgBmp, (int) newPoint.x, (int) newPoint.y, (int) (ShareData.PxToDpi_xhdpi(mMosaicPaintSize) / getPaintScale()), mMosaicType);
				}
				mLastPoint = newPoint;
				saveCache = true;

				RefreshSonWinPos(event.getX(), event.getY());

				this.invalidate();
			}
		}
	}

	private void erase()
	{
		Canvas canvas = new Canvas(img.m_bmp);
		canvas.setDrawFilter(temp_filter);
		Matrix mMatrix = new Matrix();
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		canvas.drawPath(mPath, mPaint);

		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		canvas.drawBitmap(mOrgBmp, mMatrix, mPaint);
		canvas.setBitmap(null);
	}

	private void doodle()
	{
		Canvas canvas = new Canvas(img.m_bmp);
		canvas.setDrawFilter(temp_filter);
		mPaint.setShader(mBitmapShader);
		canvas.drawPath(mPath, mPaint);
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		isDrawMagnify = false;
		isOddTouch = false;
		if (MODE == MODE_MOSAIC) {
			PocoOilMask.releaseOilMaskBitmap();
		}
		if(mCallBack != null)
		{
			if (saveCache) {
				saveCache = false;
				mCallBack.canvasChanged(img.m_bmp);
			}
			mCallBack.fingerUp();
			mCallBack.updateSonWin(null,0,0);
		}
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		rollBackBmp();
		isDrawMagnify = false;
		isOddTouch = false;
		super.EvenDown(event);
		if(mCallBack != null)
		{
			mCallBack.updateSonWin(null,0,0);
		}
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		super.EvenUp(event);
		if(mCancelTouch)
		{
			isOddTouch = false;
			if(mCallBack != null)
			{
				if (saveCache) {
					saveCache = false;
					mCallBack.canvasChanged(img.m_bmp);
				}
				mCallBack.fingerUp();
			}
		}
	}

	public boolean isSaveCache()
	{
		return isCanvasChanged;
	}

	private void rollBackBmp() {
		boolean isRecycle = false;
		if (!isSingleMove) {
			isRecycle = true;
		} else if(mPathMeasure != null){
			mPathMeasure.setPath(mPath,false);
			float pathLen = mPathMeasure.getLength();
			if (pathLen < 50) isRecycle = true;
		}

		if (isRecycle && mActionDownBeforeBmp != null && !mActionDownBeforeBmp.isRecycled()) {
			if (img.m_bmp != null && !img.m_bmp.isRecycled()) {
				img.m_bmp.recycle();
				img.m_bmp = null;
			}
			img.m_bmp = mActionDownBeforeBmp;
			mActionDownBeforeBmp = null;
			saveCache = false;
		}
		releaseActionDownBmp();
	}

	/**
	 * 设置马赛克类型
	 *
	 * @param type
	 */
	public void setMosaicType(int type) {
		if (MODE != MODE_MOSAIC || isRubberMode) {
			if (mMosaicOrgBmp != null) {
				mMosaicOrgBmp.recycle();
				mMosaicOrgBmp = null;
			}
			mMosaicOrgBmp = img.m_bmp.copy(Bitmap.Config.ARGB_8888, true);
		}

		MODE = MODE_MOSAIC;
		mMosaicType = type;
	}

	public void updateMosaicOrgBmp()
	{
		if (mMosaicOrgBmp != null) {
			mMosaicOrgBmp.recycle();
			mMosaicOrgBmp = null;
			mMosaicOrgBmp = img.m_bmp.copy(Bitmap.Config.ARGB_8888, true);
		}
	}

	protected float getPaintScale()
	{
		RectF curImgLogicRect = getCurImgLogicRect();
		return curImgLogicRect.width() / img.m_bmp.getWidth();
	}

	/**
	 * 设置涂鸦效果
	 *
	 * @param doodleBmp
	 * @param doodleRes
	 */
	public void setDoodleType(Bitmap doodleBmp, MosaicRes doodleRes) {
		MODE = MODE_DOODLE;
		if (mDoodleBmp != null && !mDoodleBmp.isRecycled()) {
			mDoodleBmp.recycle();
			mDoodleBmp = null;
		}
		mDoodleBmp = doodleBmp;

		mBitmapShader = new BitmapShader(doodleBmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		if (doodleRes.m_paintType != MosaicRes.PAINT_TYPE_TILE) {
			Matrix tempMatrix = new Matrix();
			int maskWidth = mOrgBmp.getWidth();
			int maskHeight = mOrgBmp.getHeight();
			int paintWidth = mDoodleBmp.getWidth();
			int paintHeight = mDoodleBmp.getHeight();
			boolean horizontalOrVertical = maskWidth > maskHeight;
			float scale = horizontalOrVertical ? 1f * maskWidth / paintWidth : 1f * maskHeight / paintHeight;
			int type = horizontalOrVertical ? doodleRes.horizontal_fill : doodleRes.vertical_fill;
			switch (type) {
				case MosaicRes.POS_START: {
					if (horizontalOrVertical) {
						tempMatrix.postTranslate((maskWidth - paintWidth) / 2, 0);
						tempMatrix.postScale(scale, scale, maskWidth / 2, 0);
					} else {
						tempMatrix.postTranslate(0, (maskHeight - paintHeight) / 2);
						tempMatrix.postScale(scale, scale, 0, maskHeight / 2);
					}
					break;
				}

				case MosaicRes.POS_CENTER: {
					tempMatrix.postTranslate((maskWidth - paintWidth) / 2, (maskHeight - paintHeight) / 2);
					tempMatrix.postScale(scale, scale, maskWidth / 2, maskHeight / 2);
					break;
				}

				case MosaicRes.POS_END: {
					if (horizontalOrVertical) {
						tempMatrix.postTranslate((maskWidth - paintWidth) / 2, maskHeight - paintHeight);
						tempMatrix.postScale(scale, scale, maskWidth / 2, maskHeight);
					} else {
						tempMatrix.postTranslate(maskWidth - paintWidth, (maskHeight - paintHeight) / 2);
						tempMatrix.postScale(scale, scale, maskWidth, maskHeight / 2);
					}
					break;
				}
				default:
					break;
			}
			mBitmapShader.setLocalMatrix(tempMatrix);
		} else if (mShaderMatrix != null) {
			mBitmapShader.setLocalMatrix(mShaderMatrix);
		}
	}

	public void setImageViewBitmap(Bitmap bmp)
	{
		if (bmp != null)
		{
			mOrgBmp = bmp;
			setImage(mOrgBmp.copy(Bitmap.Config.ARGB_8888, true));

			mPath = new Path();
			mShaderMatrix = new Matrix();
			// 缩小素材
			this.post(new Runnable()
			{
				@Override
				public void run()
				{
					Matrix matrix = new Matrix();
					Canvas canvas = new Canvas();
					canvas.concat(global.m_matrix);
					canvas.concat(img.m_matrix);
					canvas.getMatrix(matrix);
					matrix.invert(mShaderMatrix);
				}
			});
		}
	}

	public void updateImageBitmap(Bitmap bmp) {
		if (bmp != null) {
			if (img.m_bmp != null) {
				img.m_bmp.recycle();
				img.m_bmp = null;
			}
			img.m_bmp = bmp;
			invalidate();
		}
	}

	public void setUiEnabled(boolean enabled) {
		LockUI(!enabled);
	}

	/**
	 * 设置纹理和橡皮擦笔触大小
	 *
	 * @param size
	 */
	public void setPaintSize(int size) {
		mPaintSize = size;
	}

	/**
	 * 设置马赛克笔触大小
	 *
	 * @param size
	 */
	public void setMosaicPaintSize(int size) {
		mMosaicPaintSize = size;
	}

	public void changingPaintSize(boolean isChanging)
	{
		isPaintSizeChanging = isChanging;
		this.invalidate();
	}

	/**
	 * 设置橡皮擦模式
	 *
	 * @param rubberMode
	 */
	public void setRubberMode(boolean rubberMode) {
		isRubberMode = rubberMode;
	}

	public Bitmap getOutBmp() {
		return img.m_bmp.copy(Bitmap.Config.ARGB_8888, true);
	}

	/**
	 * 会清理当初显示的Bitmap,只适用于保存
	 *
	 * @return
	 */
	public Bitmap getOutSaveBmp() {
		Bitmap out = img.m_bmp;
		img.m_bmp = null;
		return out;
	}

	public interface ControlCallback
	{
		void updateSonWin(Bitmap bmp, int x, int y);

		void canvasChanged(Bitmap bmp);

		void fingerDown();

		void fingerUp();
	}
}
