package cn.poco.view.beauty;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.view.MotionEvent;

import cn.poco.tianutils.ImageUtils;
import cn.poco.transitions.TweenLite;
import cn.poco.view.BaseView;

/**
 * 基础view ，只提供内容随view大小缩放、回弹效果
 */
public class BeautyViewEx extends BaseView
{
	// 动画
	protected TweenLite mTween;// 计数器
	protected float mTweenScale;// 记录动画过程的缩放比例
	protected Matrix mTweenMatrix;// 记录动画的矩阵，只是中间矩阵
	protected Shape mTweenTarget; // 做动画的对象
	protected float[] mTweenPoints;
	protected boolean isAnimFinish = true;// 控制动画

	// view 改变大小时记录复位数据
	private float mRestoreDisX = 0;
	private float mRestoreDisY = 0;
	private int mAnimEndWidth = 0;
	private int mAnimEndHeight = 0;
	protected final int mAnimType = TweenLite.EASING_EXPO | TweenLite.EASE_OUT; // 默认动画

	protected float mCanvasX, mCanvasY;

	public BeautyViewEx(Context context)
	{
		super(context);
	}

	@Override
	protected void InitData()
	{
		super.InitData();
		mTween = new TweenLite();
		mTweenMatrix = new Matrix();
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();

		if(mTweenTarget != null && mTweenTarget.m_bmp != null && !mTweenTarget.m_bmp.isRecycled())
		{
			mTweenTarget.m_matrix = null;
			mTweenTarget.m_bmp.recycle();
			mTweenTarget.m_bmp = null;
			mTweenTarget = null;
		}
		mReplacementAnim = null;
		mKickBackAnim = null;
		mTweenPoints = null;
		mTween = null;
		mTweenMatrix = null;
	}

	protected void updateContent(int width, int height)
	{
        if (img == null || img.m_bmp == null) return;
		// 计算 view 宽高改变后与图片宽高的比例，取min
		float scaleA = getSpecificScale(img.m_bmp.getWidth(), img.m_bmp.getHeight(), width, height, false);

		mCanvasX = (width - img.m_bmp.getWidth() * scaleA) / 2f;
		mCanvasY = (height - img.m_bmp.getHeight() * scaleA) / 2f;

		// 保证全局矩阵开始是单元矩阵
		if(GlobalChange)// 触摸屏幕后
		{
			// 计算 view 宽高改变前与图片宽高的比例，取min
			float scaleB = getSpecificScale(img.m_bmp.getWidth(), img.m_bmp.getHeight(), view_w, view_h, false);
			// 对比两者之间放大或缩小的比例
			float scale = scaleA / scaleB;

			// 进行整体内容的比例缩放
			global.m_matrix.set(m_temp_global_matrix);
			global.m_matrix.postScale(scale, scale);

			float ratioX = 0;
			if(mAnimEndWidth != view_w)
			{
				ratioX = ((float)Math.abs(width - view_w)) / ((float)Math.abs(mAnimEndWidth - view_w));
			}
			float ratioY = ((float)Math.abs(height - view_h)) / ((float)Math.abs(mAnimEndHeight - view_h));
			float ratio = Math.max(ratioX, ratioY);

			global.m_matrix.postTranslate(mRestoreDisX * ratio, mRestoreDisY * ratio);

			if(ratio >= 1.0f)
			{
				ResetAnimDate();
				LockUI(false);
			}
		}
		else
		{
			// 没有触摸屏幕，只缩放图片
			img.m_matrix.reset();
			img.m_matrix.postScale(scaleA, scaleA);
		}
	}

	/**
	 * 改变view大小前调用<br/> 为了修复缩小产生的位置偏移
	 */
	public void InitAnimDate(int startW, int startH, int endW, int endH)
	{
		if(GlobalChange)
		{
			// 修复 view 大小改变时，手指滑动会影响图片位置的bug -- 原因出在 onTouch()
			LockUI(true);

			// 记录动画始末宽高，用于动画过程计算比例
			mAnimEndWidth = endW;
			mAnimEndHeight = endH;

			view_w = startW;
			view_h = startH;

			// 备份数据
			m_temp_global_matrix.set(global.m_matrix);

			float scale1 = getSpecificScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),endW,endH,false);

			mCanvasX = (endW - img.m_bmp.getWidth() * scale1) / 2f;
			mCanvasY = (endH - img.m_bmp.getHeight() * scale1) / 2f;

			// 计算view宽高改变之前与图片宽高的比例，取min
			float scale2 = getSpecificScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),startW,startH,false);
			float scale = scale1 / scale2;

			// 基层Matrix 进行整体内容的比例缩放
			global.m_matrix.postScale(scale,scale);

			RectF imgCurRectF = getCurImgShowRect(); // view 缩放后图片的位置矩形

			if(imgCurRectF.width() > endW) // 图片的 width > view 缩放后 width
			{
				if(imgCurRectF.left >= 0)
				{
					mRestoreDisX = 0 - imgCurRectF.left;
				}
				else if(imgCurRectF.right <= endW)
				{
					mRestoreDisX = endW - imgCurRectF.right;
				}
			}
			else // 图片 width <= view 缩放后 width
			{
				mRestoreDisX = (endW - imgCurRectF.width()) / 2f - imgCurRectF.left; // 垂直居中
			}

			if(imgCurRectF.height() > endH)// 图片的 height > view 缩放后 height
			{
				if(imgCurRectF.top >= 0)
				{
					mRestoreDisY = 0 - imgCurRectF.top;
				}
				else if(imgCurRectF.bottom <= endH)
				{
					mRestoreDisY = endH - imgCurRectF.bottom;
				}
			}
			else // 图片 height <= view 缩放后 height
			{
				mRestoreDisY = (endH - imgCurRectF.height()) / 2f - imgCurRectF.top; // 水平居中
			}

			// 还原数据
			global.m_matrix.set(m_temp_global_matrix);
		}
	}

	/**
	 * 改变view大小后调用
	 */
	protected void ResetAnimDate()
	{
		syncScaling();
		mRestoreDisX = 0;
		mRestoreDisY = 0;
		mAnimEndWidth = 0;
		mAnimEndHeight = 0;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// 背景色
		canvas.drawColor(Color.TRANSPARENT);

		//画图片
		if(img != null && img.m_bmp != null && !img.m_bmp.isRecycled())
		{
			canvas.save();
			canvas.translate(mCanvasX, mCanvasY);
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			canvas.drawBitmap(img.m_bmp, img.m_matrix, mPaint);
			canvas.restore();
		}
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		StopAnim();
		mTarget = getShowMatrix(mDownX,mDownY);
		Init_M_Data(mTarget, mDownX, mDownY);
		this.invalidate();
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		Run_M(mTarget, event.getX(), event.getY());
		this.invalidate();
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		Init_M_Data(mTarget, mUpX, mUpY);
		DoAnim();
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		StopAnim();
		mTarget = getShowMatrix(mDownX1, mDownY1, mDownX2, mDownY2);
		Init_MZ(mTarget, mDownX1, mDownY1, mDownX2, mDownY2);
		this.invalidate();
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
		Run_MZ(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
		this.invalidate();
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		int i;
		if(event.getActionIndex() == 0){
			i = 1;
		}else{
			i = 0;
		}
		mUpX = event.getX(i);
		mUpY = event.getY(i);

		Init_M_Data(mTarget, mUpX, mUpY);
		DoAnim();
	}

	protected void StopAnim()
	{
		mTween.M1End();
		isAnimFinish = true;
	}

	public void DoAnim()
	{
		mTarget = mInit;
		RectF rectF = getOrgImgShowRect();
		Replacement(img, rectF.width(), rectF.height(), mAnimType);
		KickBack(img);
	}

	protected void Run_MZ(Shape target, float x1, float y1, float x2, float y2)
	{
		target.m_matrix.set(mOldMatrix);
		Run_Z(target, x1, y1, x2, y2);
		Run_M(target.m_matrix, (x1 + x2) / 2f, (y1 + y2) / 2f);
	}

	protected void Init_MZ(Shape target, float x1, float y1, float x2, float y2)
	{
		mOldMatrix.set(target.m_matrix);
		Init_R_Data(x1, y1, x2, y2);
		Init_Z_Data(x1, y1, x2, y2);
		Init_M_Data((x1 + x2) / 2f, (y1 + y2) / 2f);
	}

	/**
	 * 缩放
	 */
	@Override
	protected void Run_Z(Shape target, float x1, float y1, float x2, float y2)
	{
		float scale = 1f;
		Matrix record = new Matrix();
		record.set(target.m_matrix);
		// 计算down时图片的缩放比例
		float sw1 = getScaleByW();
		float sh1 = getScaleByH();

		float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
		if(tempDist > 10)
		{
			scale = tempDist / mDelta;
		}

		Run_Z(target,scale,scale);

		// 计算move后图片的缩放比例
		float sw2 = getScaleByW();
		float sh2 = getScaleByH();
		float newScaleX = 1f, newScaleY = 1f;

		if(sw2 != -1 && sw1 != -1)
		{
			// 限制图片缩放比例
			if(sw2 <= def_img_min_scale)
			{
				sw2 = def_img_min_scale;
			}

			if(sw2 >= def_img_max_scale)
			{
				sw2 = def_img_max_scale;
			}
			newScaleX = sw2 / sw1;
		}

		if(sh2 != -1 && sh1 != -1)
		{
			// 限制图片缩放比例
			if(sh2 <= def_img_min_scale)
			{
				sh2 = def_img_min_scale;
			}

			if(sh2 >= def_img_max_scale)
			{
				sh2 = def_img_max_scale;
			}
			newScaleY = sh2 / sh1;
		}

		if(sw2 == def_img_min_scale || sw2 == def_img_max_scale)
		{
			newScaleY = newScaleX;
		}

		if(sh2 == def_img_min_scale || sh2 == def_img_max_scale)
		{
			newScaleX = newScaleY;
		}

		// 恢复缩放前的状态
		target.m_matrix.set(record);

		Run_Z(target,newScaleX,newScaleY);
	}

	private void Run_Z(Shape target, float scaleX, float scaleY)
	{
		if(target == global) // 整体缩放
		{
			PointF zoomCen = new PointF((mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
			getLogicPos(zoomCen, zoomCen);
			target.m_matrix.postScale(scaleX, scaleY, zoomCen.x, zoomCen.y);
		}
		else
		{
			PointF zoomCen = new PointF((mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
			getLogicPos(zoomCen, zoomCen);
			Matrix[] matrices = new Matrix[]{global.m_matrix};
			invMatrixCount(zoomCen, zoomCen, matrices);
			target.m_matrix.postScale(scaleX, scaleY, zoomCen.x, zoomCen.y);
		}
	}

	/**
	 * 计算底图 当前width 与 某宽度 的缩放值<br/>
	 * @return
	 */
	protected float getScaleByW()
	{
		RectF rectF = getCurImgShowRect();
		float curImgW = rectF.width();
		// 以当前view宽高为基础，缩放原图，得到原图缩放后的矩形
		rectF = getOrgImgShowRect();
		float orgImgW = rectF.width();
		return curImgW / orgImgW;
	}

	/**
	 * 计算底图 当前height 与 某高度 的缩放值<br/>
	 *
	 * @return
	 */
	protected float getScaleByH()
	{
		RectF rectF = getCurImgShowRect();
		float curImgH = rectF.height();
		// 以当前view宽高为基础，缩放原图，得到原图缩放后的矩形
		rectF = getOrgImgShowRect();
		float orgImgH = rectF.height();
		return curImgH / orgImgH;
	}

	public void ResetImage()
	{
		img.m_matrix.reset();
		float scaleX = view_w *1f / img.m_bmp.getWidth();
		float scaleY = view_h *1f / img.m_bmp.getHeight();
		float scale = Math.min(scaleX, scaleY);
		mCanvasX = (view_w - img.m_bmp.getWidth() * scale) / 2f;
		mCanvasY = (view_h - img.m_bmp.getHeight() * scale) / 2f;
		img.m_matrix.postScale(scale, scale);
		invalidate();
	}

	/**
	 * 计算当前屏幕上的底图位置
	 *
	 * @return 原图缩放后，长宽构成的矩形，位置基于屏幕坐标
	 */
	protected RectF getCurImgShowRect()
	{
		float[] src = new float[]{0,0,img.m_bmp.getWidth(),img.m_bmp.getHeight()};
		float[] dst = new float[src.length];
		Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
		mixMatrixCount(dst,src,matrices);
		RectF rectF = new RectF(dst[0],dst[1],dst[2],dst[3]);

		Matrix matrix = new Matrix();
		matrix.postTranslate(mCanvasX, mCanvasY);
		matrix.mapRect(rectF);

		return rectF;
	}

	/**
	 * 计算当前屏幕上的底图位置
	 *
	 * @return 原图缩放后，长宽构成的矩形，位置基于屏幕坐标
	 */
	protected RectF getCurImgLogicRect()
	{
		float[] src = new float[]{0,0,img.m_bmp.getWidth(),img.m_bmp.getHeight()};
		float[] dst = new float[src.length];
		Matrix[] matrices = new Matrix[]{global.m_matrix, img.m_matrix};
		mixMatrixCount(dst,src,matrices);

		return new RectF(dst[0],dst[1],dst[2],dst[3]);
	}

	/**
	 * 以当前view宽高为基础，缩放原图，原图中心与view中心重叠
	 *
	 * @return 原图缩放后的矩形，位置基于屏幕坐标
	 */
	protected RectF getOrgImgShowRect()
	{
		float scale = getSpecificScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),getWidth(),getHeight(),false);
		return getImgPositionByScale(scale);
	}

	/**
	 * 获得指定的缩放比例
	 *
	 * @param getMaxValue get 最大值 or 最小值
	 * @return 最大值 or 最小值
	 */
	protected float getSpecificScale(float inW,float inH,float outW,float outH, boolean getMaxValue)
	{
		float scale_x = outW/inW;
		float scale_y = outH/inH;
		if(getMaxValue){
			return Math.max(scale_x,scale_y);
		}else {
			return Math.min(scale_x,scale_y);
		}
	}

	/**
	 * @return 原图缩放后，图片中心与view 中心重叠时，图片矩形的位置 (基于屏幕坐标)
	 */
	protected RectF getImgPositionByScale(float scale)
	{
		float imgWidth = img.m_bmp.getWidth() * scale;
		float imgHeight = img.m_bmp.getHeight() * scale;
		float x = (getWidth() - imgWidth) / 2f;
		float y = (getHeight() - imgHeight) / 2f;

		return new RectF(x,y, x + imgWidth, y + imgHeight);
	}

	/**
	 * 融合多个矩阵计算点
	 */
	protected void mixMatrixCount(@NonNull float[] dst,@NonNull float[] src,@NonNull Matrix[] matrices)
	{
		int matrixCount = matrices.length;
		int count = src.length;
		if(matrixCount <= 0 || count <= 0)return;

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix mix = new Matrix();
		canvas.getMatrix(mix);
		mix.mapPoints(dst,src);
	}

	/**
	 * 融合多个矩阵计算矩形
	 */
	protected void mixMatrixCount(@NonNull RectF dst,@NonNull RectF src,@NonNull Matrix[] matrices)
	{
		int matrixCount = matrices.length;
		if(matrixCount <= 0)return;

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix mix = new Matrix();
		canvas.getMatrix(mix);
		mix.mapRect(dst,src);
	}

	protected void invMatrixCount(PointF dst, PointF src, Matrix[] matrices)
	{
		int matrixCount = matrices.length;
		if(matrixCount <=0 || dst == null || src == null)return;

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}

		float[] temp = new float[]{src.x, src.y};

		Matrix mix = new Matrix();
		canvas.getMatrix(mix);
		Matrix invert = new Matrix();
		mix.invert(invert);
		invert.mapPoints(temp);

		dst.set(temp[0], temp[1]);
	}

	/**
	 * 融合多个矩阵后，get到逆矩阵，计算点
	 */
	protected void invMatrixCount(@NonNull float[] dst,@NonNull float[] src,@NonNull Matrix[] matrices)
	{
		int matrixCount = matrices.length;
		int count = src.length;
		if(matrixCount <=0 || count <= 0)return;

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix mix = new Matrix();
		canvas.getMatrix(mix);
		Matrix invert = new Matrix();
		mix.invert(invert);
		invert.mapPoints(dst,src);
	}

	/**
	 * 融合多个矩阵后，get到逆矩阵，计算矩形
	 */
	protected void invMatrixCount(@NonNull RectF dst,@NonNull RectF src,@NonNull Matrix[] matrices)
	{
		int matrixCount = matrices.length;
		if(matrixCount <=0)return;

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix mix = new Matrix();
		canvas.getMatrix(mix);
		Matrix invert = new Matrix();
		invert.mapRect(dst,src);
	}

	/**
	 * 恢复到指定区域大小，带动画效果<br/> 图片不做旋转变换时适用
	 *
	 * @param target 复位的对象
	 * @param w 要恢复的宽度
	 * @param h 要恢复的高度
	 */
	protected void Replacement(Shape target, float w, float h, int animType)
	{
		RectF rectF = getCurImgShowRect();
		float scaleX = rectF.width() / w;
		// 比原图小 --> 直接复位
		if(isAnimFinish && scaleX < 1f){

			float scale_x = w / rectF.width();
			float scale_y = h / rectF.height();
			mTweenScale = Math.min(scale_x,scale_y);

			if(mTween == null) mTween = new TweenLite();

			InitTweenAnim(target, 0, 1, 400, animType);
			doReplacementAnim();
		}
	}

	protected void Replacement1(Shape target, float w, float h, int animType)
	{
		RectF rectF = getCurImgShowRect();
		if(isAnimFinish){
			isAnimFinish = false;

			float scale_x = w / rectF.width();
			float scale_y = h / rectF.height();
			mTweenScale = Math.min(scale_x,scale_y);

			if(mTween == null) mTween = new TweenLite();

			InitTweenAnim(target, 0, 1, 400, animType);
			doReplacementAnim();
		}
	}

	protected void doReplacementAnim()
	{
		if(mTweenTarget != null && mTweenTarget.m_bmp != null)
		{
			// 缩放复位
			mTweenTarget.m_matrix.set(mTweenMatrix);
			// 计算图片位置
			RectF rectF = new RectF(0, 0, img.m_bmp.getWidth(), img.m_bmp.getHeight());
			RectF imgRectF = new RectF();
			Matrix[] matrices = new Matrix[]{global.m_matrix, mTweenTarget.m_matrix};
			mixMatrixCount(imgRectF,rectF,matrices);

			// 图片中心点
			PointF imgCenter = new PointF(imgRectF.centerX(), imgRectF.centerY());
			// view中心点
			PointF viewCenter = new PointF(getWidth()/2f, getHeight()/2f);
			getLogicPos(viewCenter,viewCenter);
			Matrix[] globalMatrix = new Matrix[]{global.m_matrix};

			// 确保缩放中心点不变
			invMatrixCount(imgCenter,imgCenter,globalMatrix);
			mTweenTarget.m_matrix.postScale(1+((mTweenScale - 1) * mTween.M1GetPos()),1+((mTweenScale - 1) * mTween.M1GetPos()),imgCenter.x, imgCenter.y);

			// 平移复位 将当前图片的中心点移动到view中心点
			matrices[1] = mTweenTarget.m_matrix;
			mixMatrixCount(imgRectF,rectF,matrices);
			imgCenter.set(imgRectF.centerX(), imgRectF.centerY());

			invMatrixCount(viewCenter, viewCenter, globalMatrix);
			invMatrixCount(imgCenter, imgCenter, globalMatrix);
			mTweenTarget.m_matrix.postTranslate((viewCenter.x - imgCenter.x)*mTween.M1GetPos(),(viewCenter.y - imgCenter.y)*mTween.M1GetPos());

			invalidate();
			if(!mTween.M1IsFinish()){
				this.postDelayed(mReplacementAnim,1);
			}else {
				// 动画结束，清楚状态
				mTweenMatrix.reset();
				mTweenTarget = null;
				this.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						isAnimFinish = true;
					}
				},1);
			}
		}
	}

	protected Runnable mReplacementAnim = new Runnable()
	{
		@Override
		public void run()
		{
			doReplacementAnim();
		}
	};

	protected void getLogicPos(PointF dstPt, PointF srcPt)
	{
		float[] src = new float[]{srcPt.x, srcPt.y};
		Matrix matrix = new Matrix();
		matrix.postTranslate(-mCanvasX, -mCanvasY);
		matrix.mapPoints(src);
		dstPt.set(src[0], src[1]);
	}

	protected void getLogicPos(RectF dst, RectF src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(-mCanvasX, -mCanvasY);
		matrix.mapRect(dst, src);
	}

	protected void getShowPos(PointF dstPt, PointF srcPt)
	{
		float[] src = new float[]{srcPt.x, srcPt.y};
		Matrix matrix = new Matrix();
		matrix.postTranslate(mCanvasX, mCanvasY);
		matrix.mapPoints(src);
		dstPt.set(src[0], src[1]);
	}

	protected void getShowPos(RectF dst, RectF src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(mCanvasX, mCanvasY);
		matrix.mapRect(dst, src);
	}

	protected void getShowPos(float[] dst, float[] src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(mCanvasX, mCanvasY);
		matrix.mapPoints(dst, src);
	}

	/**
	 * 回弹到初始位置，带动画效果<br/> 图片不做旋转变换时适用
	 *
	 * @param item 回弹的对象
	 */
	protected void KickBack(Shape item)
	{
		RectF orgRectF = getOrgImgShowRect();
		RectF curRectF = getCurImgShowRect();
		float scaleX = curRectF.width() / orgRectF.width();

		if(isAnimFinish && scaleX >= 1f)
		{
			// 回弹范围
			RectF kbRectF = new RectF();

			// 修改回弹位置
			if(curRectF.width() >= getWidth() * 1f && curRectF.height() >= getHeight() * 1f)// 底图宽高都 >= view 宽高
			{
				kbRectF.set(0, 0, getWidth(), getHeight());
			}
			else if(Math.round(curRectF.width()) < getWidth() * 1f && Math.round(curRectF.height()) >= getHeight() * 1f)// 图宽 < view 宽，图高 >= view 高
			{
				float scale = curRectF.width() / img.m_bmp.getWidth();
				RectF tempRect = getImgPositionByScale(scale);
				kbRectF.set(tempRect.left, 0, tempRect.left + tempRect.width(), getHeight());
			}
			else if(Math.round(curRectF.width()) >= getWidth() * 1f && Math.round(curRectF.height()) < getHeight() * 1f)// 图宽 >= view 宽，图高 < view 高
			{
				float scale = curRectF.width() / img.m_bmp.getWidth();
				RectF tempRect = getImgPositionByScale(scale);
				kbRectF.set(0, tempRect.top, getWidth(), tempRect.top + tempRect.height());
			}

			// 设置回弹范围
			if(!kbRectF.isEmpty())
			{
				if(kbRectF.contains(curRectF.left, curRectF.top))
				{// 左上角在矩形内
					initKickBackAnim(item, kbRectF.left, kbRectF.top, curRectF.left, curRectF.top);
				}
				else if(kbRectF.contains(curRectF.left, curRectF.bottom))
				{// 左下角在矩形内
					initKickBackAnim(item, kbRectF.left, kbRectF.bottom, curRectF.left, curRectF.bottom);
				}
				else if(kbRectF.contains(curRectF.right, curRectF.top))
				{// 右上角在矩形内
					initKickBackAnim(item, kbRectF.right, kbRectF.top, curRectF.right, curRectF.top);
				}
				else if(kbRectF.contains(curRectF.right, curRectF.bottom))
				{// 右下角在矩形内
					initKickBackAnim(item, kbRectF.right, kbRectF.bottom, curRectF.right, curRectF.bottom);
				}
				else if(kbRectF.contains(curRectF.left, kbRectF.top))
				{
					if(kbRectF.bottom <= curRectF.top)
					{
						initKickBackAnim(item, kbRectF.left, kbRectF.top, curRectF.left, curRectF.top);
					}
					else if(kbRectF.top >= curRectF.bottom)
					{
						initKickBackAnim(item, kbRectF.left, kbRectF.bottom, curRectF.left, curRectF.bottom);
					}
					else
					{// 仅左边在矩形内
						initKickBackAnim(item, kbRectF.left, 0, curRectF.left, 0);
					}
				}
				else if(kbRectF.contains(curRectF.right, kbRectF.top))
				{
					if(kbRectF.bottom <= curRectF.top)
					{
						initKickBackAnim(item, kbRectF.right, kbRectF.top, curRectF.right, curRectF.top);
					}
					else if(kbRectF.top >= curRectF.bottom)
					{
						initKickBackAnim(item, kbRectF.right, kbRectF.bottom, curRectF.right, curRectF.bottom);
					}
					else
					{
						// 仅右边在矩形内
						initKickBackAnim(item,kbRectF.right, 0, curRectF.right, 0);
					}
				}
				else if(kbRectF.contains(kbRectF.left, curRectF.top))
				{
					if(kbRectF.right <= curRectF.left)
					{
						initKickBackAnim(item, kbRectF.left, kbRectF.top, curRectF.left, curRectF.top);
					}
					else if(kbRectF.left >= curRectF.right)
					{
						initKickBackAnim(item, kbRectF.right, kbRectF.top, curRectF.right, curRectF.top);
					}
					else
					{
						// 仅顶边在矩形内
						initKickBackAnim(item, 0, kbRectF.top, 0, curRectF.top);
					}
				}
				else if(kbRectF.contains(kbRectF.left, curRectF.bottom))
				{
					if(kbRectF.right <= curRectF.left)
					{
						initKickBackAnim(item, kbRectF.left, kbRectF.bottom, curRectF.left, curRectF.bottom);
					}
					else if(kbRectF.left >= curRectF.right)
					{
						initKickBackAnim(item, kbRectF.right, kbRectF.bottom, curRectF.right, curRectF.bottom);
					}
					else
					{
						// 仅底边在矩形内
						initKickBackAnim(item, 0, kbRectF.bottom, 0, curRectF.bottom);
					}
				}
			}
		}
	}

	/**
	 * @param x1 目标点x
	 * @param y1 目标点y
	 * @param x2 起始点x
	 * @param y2 起始点y
	 */
	private void initKickBackAnim(Shape item, float x1, float y1, float x2, float y2)
	{
		if(isAnimFinish)
		{
			float[] src = new float[]{x1,y1,x2,y2};
			mTweenPoints = new float[src.length];
			Matrix[] globalMatrix = new Matrix[]{global.m_matrix};
			invMatrixCount(mTweenPoints, src, globalMatrix);

			InitTweenAnim(item, 0, 1, 400, mAnimType);
			doKickBackAnim();
		}
	}

	protected void InitTweenAnim(Shape target, float start, float end, int duration, int animType)
	{
		isAnimFinish = false;
		mTween.Init(start, end, duration);
		mTweenTarget = target;
		mTweenMatrix.set(target.m_matrix);
		mTween.M1Start(animType);
	}

	protected void doKickBackAnim()
	{
		if(mTweenTarget != null && mTweenTarget.m_bmp != null)
		{
			mTweenTarget.m_matrix.set(mTweenMatrix);
			mTweenTarget.m_matrix.postTranslate((mTweenPoints[0] - mTweenPoints[2])*mTween.M1GetPos(),(mTweenPoints[1] - mTweenPoints[3])*mTween.M1GetPos());
			invalidate();
			if(!mTween.M1IsFinish()){
				this.postDelayed(mKickBackAnim,1);
			}else {
				// 动画结束，清楚状态
				mTweenMatrix.reset();
				mTweenTarget = null;
				this.postDelayed(new Runnable()
				{
					@Override
					public void run()
					{
						isAnimFinish = true;
					}
				},1);
			}
		}
	}

	protected Runnable mKickBackAnim = new Runnable()
	{
		@Override
		public void run() {
			doKickBackAnim();
		}
	};

	/**
	 * 将屏幕上的点，转换成底图上的比例
	 */
	protected void GetFaceLogicPos(float[] dst, float[] src)
	{
		RectF curImgShowRect = getCurImgShowRect();

		int count = src.length;
		for(int i=0;i<count;i+=2)
		{
			dst[i] = (src[i] - curImgShowRect.left) / curImgShowRect.width();
			dst[i+1] = (src[i+1] - curImgShowRect.top) / curImgShowRect.height();
		}
	}

	/**
	 * 将屏幕上的点，转换成底图上的比例
	 */
	protected void GetFaceLogicPos(PointF dst, PointF src)
	{
		if(src == null || dst == null) return;
		RectF curImgShowRect = getCurImgShowRect();
		float x = (src.x - curImgShowRect.left) / curImgShowRect.width();
		float y = (src.y - curImgShowRect.top) / curImgShowRect.height();
		dst.set(x,y);
	}
}