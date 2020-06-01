package cn.poco.view.beauty;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;

import cn.poco.face.FaceDataV2;
import cn.poco.tianutils.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.transitions.TweenLite;

/**
 * 滤镜、美颜、大眼、祛眼袋、亮眼、微笑、高鼻梁 共用 <br/> 双击缩放、多人脸选择<br/>
 * Created by admin on 2017/2/5.
 */
public class BeautyCommonViewEx extends BeautyViewEx
{
	// 双击触发
	protected long m_doubleClickTime = 0;
	protected float m_doubleClickX = 0;
	protected float m_doubleClickY = 0;

	protected float def_magnify_scale = 2.0f;

	protected float mMagnifyPosX , mMagnifyPosY; // 记录双击放大时的缩放点
	protected float mOffsetX,mOffsetY; // 记录双击放大时的偏移量

	protected int def_click_size; //判断为click状态的最大size
	protected boolean m_isClick = true; // 判断是否单击事件

	public BeautyCommonViewEx(Context context)
	{
		super(context);
	}

	@Override
	protected void InitData()
	{
		super.InitData();
		def_click_size = ShareData.PxToDpi_xhdpi(20);
		def_img_max_scale = 15.0f;
		def_img_min_scale = 0.5f;
		mSkinColor = cn.poco.advanced.ImageUtils.GetSkinColor(0xffe75988);
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();
		mMagnifyAnim = null;
		mShrinkAnim = null;
		mResetAnimDate = null;
		mCallBack = null;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		//选择人脸模式
		if(m_operateMode == MODE_SEL_FACE && img != null)
		{
			DrawMultiFace(canvas);
		}
	}

	@Override
	protected void OddDown(MotionEvent event)
	{
		mTarget = mInit;
		super.OddDown(event);
		m_isClick = true;
	}

	@Override
	protected void OddMove(MotionEvent event)
	{
		super.OddMove(event);
		if(ImageUtils.Spacing(mDownX - event.getX(), mDownY - event.getY()) > def_click_size)
		{
			m_isClick = false;
		}
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		if (m_isClick && isAnimFinish)
		{
			if(m_operateMode == MODE_SEL_FACE)
			{
				int index = isClickFaceArea(mDownX,mDownY);
				if(index != -1)
				{
					m_operateMode = -1;
					if(mCallBack != null)
					{
						mCallBack.OnSelFaceIndex(index);
						return;
					}
				}
				else
				{
					isDoubleClick(event);
				}
			}
			else
			{
				isDoubleClick(event);
			}
		} else {
			m_doubleClickTime = 0;
		}

		super.OddUp(event);
	}

	@Override
	protected void EvenDown(MotionEvent event)
	{
		mTarget = mInit;
		super.EvenDown(event);
		m_isClick = false;
	}

	@Override
	protected void EvenMove(MotionEvent event)
	{
		super.EvenMove(event);
	}

	@Override
	protected void EvenUp(MotionEvent event)
	{
		super.EvenUp(event);
	}

	private void isDoubleClick(MotionEvent event)
	{
		long time = System.currentTimeMillis();

		if(Math.abs(time - m_doubleClickTime) < 800 && ImageUtils.Spacing(m_doubleClickX - event.getX(), m_doubleClickY - event.getY()) <= def_click_size)
		{
			m_doubleClickTime = 0;

			Matrix temp_matrix = new Matrix();
			temp_matrix.set(img.m_matrix);

			// 当前矩阵状态下，图片构造的矩形
			RectF curImgShowRect = getCurImgShowRect();
			// 当前 view 宽高前提下，图片适配 view 构造的矩形
			RectF orgImgShowRect = getOrgImgShowRect();
			// 当前缩放比例
			float scaleX = curImgShowRect.width() / orgImgShowRect.width();

			if(mTween == null) mTween = new TweenLite();

			if(scaleX >= 1f && scaleX < (def_img_max_scale + 1) * 0.5f)// 放大
			{
				// 放大比例 暂时写死
				mTweenScale = def_magnify_scale;
				// 模拟图片放大缩放
				temp_matrix.postScale(mTweenScale, mTweenScale);
				// 求出模拟缩放后，图片的位置 -- 逻辑坐标
				RectF tempRect = new RectF(0, 0, img.m_bmp.getWidth(), img.m_bmp.getHeight());
				Matrix[] matrices = new Matrix[]{global.m_matrix, temp_matrix};
				mixMatrixCount(tempRect, tempRect, matrices);

				// 求出缩放后，跟原始对比的比例
				float temp_scale_X = tempRect.width() / orgImgShowRect.width();

				if(temp_scale_X >= def_img_max_scale)
				{
					mTweenScale = def_img_max_scale / scaleX;
				}

				InitTweenAnim(img, 0, 1, 500, mAnimType);

				PointF pointF = new PointF(m_doubleClickX, m_doubleClickY);
				getLogicPos(pointF, pointF);

				// 记录放大时的缩放点
				mMagnifyPosX = pointF.x;
				mMagnifyPosY = pointF.y;
				// 修复放大后的位置偏移问题
				repairOffset(mMagnifyPosX, mMagnifyPosY);
				doMagnifyAnim(mMagnifyPosX,mMagnifyPosY);
			}
			else if(scaleX >= (def_img_max_scale + 1) * 0.5f)// 缩小
			{
				mTweenScale = 1f/scaleX;
				InitTweenAnim(img, 0, 1, 500, mAnimType);
				doShrinkAnim();
			}
		} else {
			m_doubleClickTime = time;
			m_doubleClickX = mDownX;
			m_doubleClickY = mDownY;
		}
	}

	/**
	 * 放大动画
	 */
	protected void doMagnifyAnim(float zoomCenX, float zoomCenY)
	{
		if(mTweenTarget != null)
		{
			// 还原图片缩放前的矩阵状态
			mTweenTarget.m_matrix.set(mTweenMatrix);

			PointF zoomPoint = new PointF(zoomCenX, zoomCenY);
			PointF temp = new PointF();
			Matrix[] globalMatrix = new Matrix[]{global.m_matrix};
			invMatrixCount(temp, zoomPoint, globalMatrix);

			// 根据缩放点缩放
			mTweenTarget.m_matrix.postScale(1 + ((mTweenScale - 1) * mTween.M1GetPos()), 1 + ((mTweenScale - 1) * mTween.M1GetPos()), temp.x, temp.y);

			// view 中心
			PointF viewCenPoint = new PointF(getWidth() / 2f, getHeight() / 2f);
			getLogicPos(viewCenPoint, viewCenPoint);

			invMatrixCount(viewCenPoint, viewCenPoint, globalMatrix);
			invMatrixCount(zoomPoint, zoomPoint, globalMatrix);
			// 求出缩放点与中心点距离
			float transX = viewCenPoint.x - zoomPoint.x;
			float transY = viewCenPoint.y - zoomPoint.y;

			// 将缩放点平移到中心
			mTweenTarget.m_matrix.postTranslate(transX * mTween.M1GetPos(), transY * mTween.M1GetPos());
			// 修复偏移
			mTweenTarget.m_matrix.postTranslate(mOffsetX * mTween.M1GetPos(), mOffsetY * mTween.M1GetPos());

			invalidate();
			if(!mTween.M1IsFinish())
			{
				this.postDelayed(mMagnifyAnim, 1);
			}
			else
			{
				// 动画结束，清楚状态
				mTweenMatrix.reset();
				if(mCallBack != null)
				{
					mCallBack.OnAnimFinish();
				}
				this.postDelayed(mResetAnimDate, 1);
			}
		}
	}

	/**
	 * 修复放大时的位置偏移
	 */
	protected void repairOffset(float zoomCenX, float zoomCenY)
	{
		PointF zoomPoint = new PointF(zoomCenX, zoomCenY);
		PointF temp = new PointF();
		Matrix[] matrices = new Matrix[]{global.m_matrix};
		invMatrixCount(temp, zoomPoint, matrices);

		// 根据缩放点缩放
		img.m_matrix.postScale(mTweenScale, mTweenScale, temp.x, temp.y);

		// view中心点
		PointF viewCenPoint = new PointF(getWidth()/2f, getHeight()/2f);
		getLogicPos(viewCenPoint, viewCenPoint);

		invMatrixCount(viewCenPoint, viewCenPoint, matrices);
		invMatrixCount(zoomPoint, zoomPoint, matrices);

		// 求出缩放点与中心点距离
		float transX = viewCenPoint.x - zoomPoint.x;
		float transY = viewCenPoint.y - zoomPoint.y;

		// 将缩放点平移到中心
		img.m_matrix.postTranslate(transX, transY);

		RectF curRectF = getCurImgLogicRect();
		RectF kbRectF = new RectF();
		// 修改回弹位置
		if(curRectF.width() >= getWidth() * 1f && curRectF.height() >= getHeight() * 1f)// 底图宽高都 >= view 宽高
		{
			kbRectF.set(0, 0, getWidth(), getHeight());
		}
		else if(curRectF.width() < getWidth() * 1f && curRectF.height() >= getHeight() * 1f)// 图宽 < view 宽，图高 >= view 高
		{
			float scale = curRectF.width() / img.m_bmp.getWidth();
			RectF tempRect = getImgPositionByScale(scale);
			kbRectF.set(tempRect.left, 0, tempRect.left + tempRect.width(), getHeight());
		}
		else if(curRectF.width() >= getWidth() * 1f && curRectF.height() < getHeight() * 1f)// 图宽 >= view 宽，图高 < view 高
		{
			float scale = curRectF.width() / img.m_bmp.getWidth();
			RectF tempRect = getImgPositionByScale(scale);
			kbRectF.set(0, tempRect.top, getWidth(), tempRect.top + tempRect.height());
		}

		getLogicPos(kbRectF,kbRectF);

		// 逆计算
		invMatrixCount(curRectF,curRectF,matrices);
		invMatrixCount(kbRectF,kbRectF,matrices);

		if(!kbRectF.isEmpty())
		{
			if(kbRectF.contains(curRectF.left, curRectF.top))
			{// 左上角
				mOffsetX = kbRectF.left - curRectF.left;
				mOffsetY = kbRectF.top - curRectF.top;
			}
			else if(kbRectF.contains(curRectF.left, curRectF.bottom))
			{// 左下角
				mOffsetX = kbRectF.left - curRectF.left;
				mOffsetY = kbRectF.bottom - curRectF.bottom;
			}
			else if(kbRectF.contains(curRectF.right, curRectF.top))
			{// 右上角在矩形内
				mOffsetX = kbRectF.right - curRectF.right;
				mOffsetY = kbRectF.top - curRectF.top;
			}
			else if(kbRectF.contains(curRectF.right, curRectF.bottom))
			{// 右下角在矩形内
				mOffsetX = kbRectF.right - curRectF.right;
				mOffsetY = kbRectF.bottom - curRectF.bottom;
			}
			else if(kbRectF.contains(curRectF.left, kbRectF.top))
			{// 仅左边在矩形内
				mOffsetX = kbRectF.left - curRectF.left;
				mOffsetY = 0;
			}
			else if(kbRectF.contains(curRectF.right, kbRectF.top))
			{// 仅右边在矩形内
				mOffsetX = kbRectF.right - curRectF.right;
				mOffsetY = 0;
			}
			else if(kbRectF.contains(kbRectF.left, curRectF.top))
			{// 仅顶边在矩形内
				mOffsetX = 0;
				mOffsetY = kbRectF.top - curRectF.top;
			}
			else if(kbRectF.contains(kbRectF.left, curRectF.bottom))
			{// 仅底边在矩形内
				mOffsetX = 0;
				mOffsetY = kbRectF.bottom - curRectF.bottom;
			}
		}
	}

	/**
	 * 缩小动画
	 */
	protected void doShrinkAnim()
	{
		if(mTweenTarget != null)
		{
			// 还原图片缩放前的矩阵状态
			mTweenTarget.m_matrix.set(mTweenMatrix);

			// 图片当前位置 -- 逻辑坐标
			RectF rectF = new RectF(0, 0, img.m_bmp.getWidth(), img.m_bmp.getHeight());
			RectF curImgShowRect = new RectF();
			Matrix[] matrices = new Matrix[]{global.m_matrix, mTweenTarget.m_matrix};
			mixMatrixCount(curImgShowRect, rectF, matrices);

			// 图片中心
			PointF imgCenPoint = new PointF(curImgShowRect.left + curImgShowRect.width() / 2f, curImgShowRect.top + curImgShowRect.height() / 2f);
			PointF temp = new PointF();

			// view 中心
			PointF viewCenPoint = new PointF(getWidth() / 2f, getHeight() / 2f);
			getLogicPos(viewCenPoint, viewCenPoint);

			// 逆计算缩放点位置
			Matrix[] globalMatrix = new Matrix[]{global.m_matrix};
			invMatrixCount(temp, imgCenPoint, globalMatrix);
			// 根据缩放点缩放
			mTweenTarget.m_matrix.postScale(1 + ((mTweenScale - 1) * mTween.M1GetPos()), 1 + ((mTweenScale - 1) * mTween.M1GetPos()), temp.x, temp.y);

			// 求出缩放后的图片位置
			matrices[1] = mTweenTarget.m_matrix;
			mixMatrixCount(curImgShowRect, rectF, matrices);
			// 将图片中心平移到 view 中心
			imgCenPoint.set(curImgShowRect.left + curImgShowRect.width() / 2f, curImgShowRect.top + curImgShowRect.height() / 2f);

			invMatrixCount(viewCenPoint, viewCenPoint, globalMatrix);
			invMatrixCount(imgCenPoint, imgCenPoint, globalMatrix);

			float transX = viewCenPoint.x - imgCenPoint.x;
			float transY = viewCenPoint.y - imgCenPoint.y;

			mTweenTarget.m_matrix.postTranslate(transX * mTween.M1GetPos(), transY * mTween.M1GetPos());

			invalidate();
			if(!mTween.M1IsFinish())
			{
				this.postDelayed(mShrinkAnim, 1);
			}
			else
			{
				// 动画结束，清楚状态
				mTweenMatrix.reset();
				if(mCallBack != null)
				{
					mCallBack.OnAnimFinish();
				}
				this.postDelayed(mResetAnimDate, 1);
			}
		}
	}

	protected Runnable mShrinkAnim = new Runnable()
	{
		@Override
		public void run()
		{
			doShrinkAnim();
		}
	};

	protected Runnable mMagnifyAnim = new Runnable()
	{
		@Override
		public void run()
		{
			doMagnifyAnim(mMagnifyPosX , mMagnifyPosY);
		}
	};

	protected Runnable mResetAnimDate = new Runnable()
	{
		@Override
		public void run()
		{
			isAnimFinish = true;
			mMagnifyPosY = 0;
			mMagnifyPosY = 0;
			mOffsetX = 0;
			mOffsetY = 0;
		}
	};

	/* ----------------------------------- 多人脸 ------------------------------------ */

	public static final int MODE_MAKEUP = 0x0004;
	public static final int MODE_SEL_FACE = 0x0008;
	public static final int MODE_FACE = 0x0002;
	public static final int MODE_NORMAL = -1;
	protected int m_operateMode = -1;
	public int m_faceIndex = -1; //多人脸选择哪个
	protected int mSkinColor;
	public boolean m_showSelFaceRect = true;

	protected Path temp_path = new Path();
	protected float[] temp_dst3; //选择人脸用
	protected float[] temp_src3;
	protected PathEffect temp_effect = new DashPathEffect(new float[]{12, 6}, 1);
	protected ArrayList<RectF> temp_rect_arr = new ArrayList<>(); //GPU加速画矩形
	protected ControlCallback mCallBack;

	private int isClickFaceArea(float x, float y)
	{
		if(temp_rect_arr != null && temp_rect_arr.size() >0)
		{
			int len = temp_rect_arr.size();
			for(int i=0; i<len; i++)
			{
				RectF rectF = temp_rect_arr.get(i);
				PointF pointF = new PointF(x,y);
				getLogicPos(pointF, pointF);
				if(rectF.contains(pointF.x, pointF.y)) return i;
			}
		}
		return -1;
	}

	public void SetOnControlListener(ControlCallback cb)
	{
		mCallBack = cb;
	}

	/**
	 * 放大人脸区域
	 */
	public void DoSelFaceAnim()
	{
		removeCallbacks(mMagnifyAnim);
		removeCallbacks(mShrinkAnim);
		if(mTween == null) mTween = new TweenLite();

		UpdateFaceDate();
		syncScaling();

		if(temp_rect_arr != null && m_faceIndex >= 0 && m_faceIndex < temp_rect_arr.size())
		{
			// 逻辑坐标
			RectF rectF = temp_rect_arr.get(m_faceIndex);

			Matrix tempMatrix = new Matrix();
			tempMatrix.set(img.m_matrix);

			float scaleX = getWidth() / rectF.width();
			float scaleY = getHeight() / rectF.height();
			float scale = Math.min(scaleX, scaleY);

			Matrix[] matrices = new Matrix[]{global.m_matrix};
			//放大
			PointF tempZoomPts = new PointF(rectF.left + rectF.width() / 2f, rectF.top + rectF.height() / 2f);
			invMatrixCount(tempZoomPts, tempZoomPts, matrices);

			float imgScale = getScaleByW();
			if (imgScale > scale) {
				scale = 1f / imgScale;
			}
			img.m_matrix.postScale(scale,scale, tempZoomPts.x, tempZoomPts.y);
			mTweenScale = getScaleByW();

			if(mTweenScale >= def_img_max_scale)
			{
				mTweenScale = def_img_max_scale / imgScale;
			}

			// 还原矩阵状态
			img.m_matrix.set(tempMatrix);

			mMagnifyPosX = rectF.left + rectF.width() / 2f;
			mMagnifyPosY = rectF.top + rectF.height() / 2f;

			InitTweenAnim(img, 0, 1, 500, mAnimType);
			repairOffset(mMagnifyPosX, mMagnifyPosY);
			post(mMagnifyAnim);
		}
	}

	/**
	 * 恢复选择人脸
	 */
	public void Restore()
	{
		removeCallbacks(mMagnifyAnim);
		removeCallbacks(mShrinkAnim);
		if(mTween == null) mTween = new TweenLite();

		m_operateMode = MODE_SEL_FACE;
		//缩小
		mTweenScale = 1f / getScaleByW();
		InitTweenAnim(img, 0, 1, 500, mAnimType);
		post(mShrinkAnim);
	}

	/**
	 * 判断当前是否已经放大了
	 * @return true: 放大了
	 */
	public boolean isMagnify() {
		return getScaleByW() > 1 || getScaleByH() > 1;
	}

	public void setMode(int mode)
	{
		m_operateMode = mode;
		invalidate();
	}

	public int getMode()
	{
		return m_operateMode;
	}

	public void UpdateFaceDate()
	{
		int len = 4;
		if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
		{
			len += FaceDataV2.FACE_POS_MULTI.length << 2;
		}

		if(img != null && img.m_bmp != null)
		{
			if(temp_src3 == null || temp_src3.length < len)
			{
				temp_src3 = new float[len];
				temp_dst3 = new float[len];
			}

			temp_src3[0] = 0;
			temp_src3[1] = 0;
			temp_src3[2] = img.m_bmp.getWidth();
			temp_src3[3] = img.m_bmp.getHeight();
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				int index;
				for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
				{
					index = 4 + (i << 2);
					temp_src3[index] = temp_src3[2] * FaceDataV2.FACE_POS_MULTI[i][6];
					temp_src3[index + 1] = temp_src3[3] * FaceDataV2.FACE_POS_MULTI[i][7];
					temp_src3[index + 2] = temp_src3[index] +temp_src3[2] * FaceDataV2.FACE_POS_MULTI[i][8];
					temp_src3[index + 3] = temp_src3[index + 1] + temp_src3[3] * FaceDataV2.FACE_POS_MULTI[i][9];
				}
			}

			// 逻辑坐标
			Matrix[] matrices = new Matrix[]{global.m_matrix,img.m_matrix};
			mixMatrixCount(temp_dst3, temp_src3, matrices);

			//画矩形填充
			temp_rect_arr.clear();
			if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
			{
				int index;
				for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
				{
					index = 4 + (i << 2);
					temp_rect_arr.add(new RectF(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3]));
				}
			}
		}
	}

	protected void DrawMultiFace(Canvas canvas)
	{
		//计算全部矩形
		UpdateFaceDate();

		ArrayList<RectF> arr = GetOutsideRect(new RectF(temp_dst3[0], temp_dst3[1], temp_dst3[2], temp_dst3[3]), temp_rect_arr);
		temp_path.reset();
		temp_path.setFillType(Path.FillType.WINDING);
		int rectLen = arr.size();
		for(int i = 0; i < rectLen; i++)
		{
			temp_path.addRect(arr.get(i), Path.Direction.CW);
		}

		canvas.save();
		canvas.translate(mCanvasX, mCanvasY);
		mPaint.reset();
		mPaint.setColor(0x8c000000);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		canvas.drawPath(temp_path, mPaint);

		//画矩形边框
		if(FaceDataV2.FACE_POS_MULTI != null && FaceDataV2.FACE_POS_MULTI.length > 0)
		{
			mPaint.reset();
			mPaint.setColor(0xFFFFFFFF);
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setStrokeWidth(3);
			mPaint.setAntiAlias(true);
			mPaint.setPathEffect(temp_effect);

			int index;
			for(int i = 0; i < FaceDataV2.FACE_POS_MULTI.length; i++)
			{
				index = 4 + (i << 2);
				if(i != m_faceIndex || !m_showSelFaceRect)
				{
					canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], mPaint);
				}
			}
			if(m_showSelFaceRect && m_faceIndex >= 0 && m_faceIndex < FaceDataV2.FACE_POS_MULTI.length)
			{
				index = 4 + (m_faceIndex << 2);
				mPaint.setColor(mSkinColor);
				canvas.drawRect(temp_dst3[index], temp_dst3[index + 1], temp_dst3[index + 2], temp_dst3[index + 3], mPaint);
			}
		}
		canvas.restore();
	}

	public static ArrayList<RectF> GetOutsideRect(RectF src, ArrayList<RectF> dst)
	{
		ArrayList<RectF> out = new ArrayList<>();

		if(src != null && dst != null)
		{
			FixRect(src);
			int len = dst.size();
			for(int i = 0; i < len; i++)
			{
				FixRect(dst.get(i));
			}

			ArrayList<RectF> arr = DivideRect(src, dst);
			int len2 = arr.size();

			RectF temp;
			CROSS: for(int i = 0; i < len2; i++)
			{
				temp = arr.get(i);
				for(int j = 0; j < len; j++)
				{
					if(RectCross(temp, dst.get(j)))
					{
						continue CROSS;
					}
				}
				out.add(temp);
			}
		}

		return out;
	}

	public static void FixRect(RectF rect)
	{
		if(rect != null)
		{
			float temp;
			if(rect.left > rect.right)
			{
				temp = rect.left;
				rect.left = rect.right;
				rect.right = temp;
			}
			if(rect.top > rect.bottom)
			{
				temp = rect.top;
				rect.top = rect.bottom;
				rect.bottom = temp;
			}
		}
	}

	public static ArrayList<RectF> DivideRect(RectF src, ArrayList<RectF> dst)
	{
		ArrayList<RectF> out = new ArrayList<>();

		if(src != null && dst != null)
		{
			ArrayList<Float> xs = new ArrayList<>();
			ArrayList<Float> ys = new ArrayList<>();
			InsertOrderItem(xs, src.left);
			InsertOrderItem(xs, src.right);
			InsertOrderItem(ys, src.top);
			InsertOrderItem(ys, src.bottom);

			int len = dst.size();
			RectF rect;
			for(int i = 0; i < len; i++)
			{
				rect = dst.get(i);
				if(src.left < rect.left && rect.left < src.right)
				{
					InsertOrderItem(xs, rect.left);
				}
				if(src.left < rect.right && rect.right < src.right)
				{
					InsertOrderItem(xs, rect.right);
				}
				if(src.top < rect.top && rect.top < src.bottom)
				{
					InsertOrderItem(ys, rect.top);
				}
				if(src.top < rect.bottom && rect.bottom < src.bottom)
				{
					InsertOrderItem(ys, rect.bottom);
				}
			}

			int xlen = xs.size() - 1;
			int ylen = ys.size() - 1;
			for(int i = 0; i < ylen; i++)
			{
				for(int j = 0; j < xlen; j++)
				{
					out.add(new RectF(xs.get(j), ys.get(i), xs.get(j + 1), ys.get(i + 1)));
				}
			}
		}

		return out;
	}

	public static boolean InsertOrderItem(ArrayList<Float> arr, float value)
	{
		boolean out = false;

		if(arr != null)
		{
			Float temp;
			int len = arr.size();
			boolean equal = false;
			for(int i = 0; i < len; i++)
			{
				equal = false;
				temp = arr.get(i);
				if(temp > value)
				{
					arr.add(i, value);
					out = true;
					break;
				}
				else if(temp == value)
				{
					equal = true;
					break;
				}
			}
			if(!out && !equal)
			{
				arr.add(value);
			}
		}

		return out;
	}

	public static boolean RectCross(RectF rect1, RectF rect2)
	{
		boolean out = false;

		if(rect1 != null && rect2 != null)
		{
			out = true;
			if(rect1.left >= rect2.left && rect1.left >= rect2.right || rect1.right <= rect2.left && rect1.right <= rect2.right || rect1.top >= rect2.top && rect1.top >= rect2.bottom || rect1.bottom <= rect2.top && rect1.bottom <= rect2.bottom)
			{
				out = false;
			}
		}
		return out;
	}

	public void ZoomRect(RectF rect, float scale)
	{
		float w2 = rect.width() * scale / 2f;
		float h2 = rect.height() * scale / 2f;
		float cx = (rect.left + rect.right) / 2f;
		float cy = (rect.top + rect.bottom) / 2f;
		rect.left = cx - w2;
		rect.right = cx + w2;
		rect.top = cy - h2;
		rect.bottom = cy + h2;
		if(rect.left < 0)
		{
			rect.left = 0;
		}
		else if(rect.left > 1)
		{
			rect.left = 1;
		}
		if(rect.right < 0)
		{
			rect.right = 0;
		}
		else if(rect.right > 1)
		{
			rect.right = 1;
		}
		if(rect.top < 0)
		{
			rect.top = 0;
		}
		else if(rect.top > 1)
		{
			rect.top = 1;
		}
		if(rect.bottom < 0)
		{
			rect.bottom = 0;
		}
		else if(rect.bottom > 1)
		{
			rect.bottom = 1;
		}
	}

	public interface ControlCallback
	{
		void OnSelFaceIndex(int index);

		void OnAnimFinish();
	}
}
