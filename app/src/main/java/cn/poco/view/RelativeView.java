package cn.poco.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.poco.tianutils.ImageUtils;
import cn.poco.transitions.TweenLite;

/**
 * 相对temp做变换，view内容根据view大小缩放
 * 不同页面的需求，通过重写oddDown，oddMove，oddUp，EvenDown，EvenMove，EvenUp控制
 * Created by admin on 2017/1/11.
 */
public class RelativeView extends BaseView
{
	protected List<Shape> mPendantArr;
	protected float canvas_l, canvas_t, canvas_r, canvas_b; // 画布的剪裁坐标
	protected int viewBKColor = 0x66bfbfbf;
	protected int bkColor = 0;// canvas的颜色
	protected int mFilterAlpha = 100;
	protected boolean isAnimFinish = true; // 控制动画

	protected TweenLite mTween;// 计数器
	protected float[] points;
	protected float mTweenScale;// 用于回弹的缩放比例
	protected Matrix tween_matrix;// 用于回弹动画的矩阵，目前只是中间矩阵，不做变换

	public RelativeView(Context context)
	{
		super(context);
	}

	@Override
	protected void InitData()
	{
		super.InitData();
		mPendantArr = new ArrayList<>();
		tween_matrix = new Matrix();
		mTween = new TweenLite();
	}

	@Override
	public void ReleaseMem()
	{
		super.ReleaseMem();

		if(mPendantArr != null)
		{
			for(Shape item:mPendantArr)
			{
				if(item.m_bmp != null && !item.m_bmp.isRecycled())
				{
					item.m_matrix = null;
					item.m_bmp.recycle();
					item.m_bmp = null;
				}
				item.m_ex = null;
				item.m_info = null;
			}
			mPendantArr.clear();
			mPendantArr = null;
		}

		points = null;
		mViewAnimRunnable = null;
	}

	public void setImgMaxScale(float max)
	{
		def_img_max_scale = max;
	}

	public void setImgMinScale(float min)
	{
		def_img_min_scale = min;
	}

	public void setBKColor(int color)
	{
		bkColor = color;
		this.invalidate();
	}

	protected void updateContent(int width, int height)
	{
		float scale_x = 1,scale_y = 1;
		float scale1,scale2;
		float[] result;

		// 计算view宽高改变之前与图片宽高的比例，取min
		scale1 = countImgScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),view_w,view_h,false);
		// 计算当前view宽高与图片宽高的比例，取min
		scale2 = countImgScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),width,height,false);
		// 计算缩放后temp2.mBmp坐标
		result = countImgPos(img,width,height,scale2);
		// 更新画布显示区域
		updateCanvasSize(result);
		// view初始化时拿不到view_w,view_h
		if(scale1 != 0){
			scale_x = scale2 / scale1;
			scale_y = scale2 / scale1;
		}

		if(GlobalChange){// 触摸屏幕后
			// 基层Matrix 进行整体内容的比例缩放
			global.m_matrix.set(m_temp_global_matrix);
			global.m_matrix.postScale(scale_x,scale_y);
		}else{
			// 没有触摸屏幕，只缩放图片
			img.m_matrix.reset();
			img.m_matrix.postScale(scale2,scale2);
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// 背景色
		canvas.drawColor(Color.TRANSPARENT);

		// 限制图片显示区域
		canvas.clipRect(canvas_l,canvas_t,canvas_r,canvas_b);

		// 显示区域颜色
		if(bkColor != 0)
		{
			canvas.drawColor(bkColor);
		}

		//画图片
		if(img != null && img.m_bmp != null && !img.m_bmp.isRecycled())
		{
			canvas.save();
			canvas.translate(canvas_l,canvas_t);
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			canvas.drawBitmap(img.m_bmp, img.m_matrix, mPaint);
			canvas.restore();
		}
	}

	/**
	 * 更新画布显示的范围
	 * @param newSize
	 */
	protected void updateCanvasSize(float[] newSize)
	{
		if(newSize != null && newSize.length ==4){
			canvas_l = newSize[0];
			canvas_t = newSize[1];
			canvas_r = newSize[2];
			canvas_b = newSize[3];
		}
	}

	/**
	 * 根据 scale 计算原图当前位置 -- 屏幕坐标 <br/>
	 * 适用条件：图片没有经过旋转
	 * @return 长度为4的数组，0 - left_x, 1 - top_y, 2 - right_x, 3 - bottom_y
	 */
	protected float[] countImgPos(Shape target, float viewW, float viewH, float scale)
	{
		float left,top,right,bottom;
		float[] result = null;
		if(target != null && target.m_bmp != null){
			left = (viewW - target.m_bmp.getWidth() * scale)/2f;
			top = (viewH - target.m_bmp.getHeight() * scale)/2f;
//			right = left + target.m_bmp.getWidth() * scale;
//			bottom = top + target.m_bmp.getHeight() * scale;
			right = (viewW + target.m_bmp.getWidth() * scale)/2f;
			bottom = (viewH + target.m_bmp.getHeight() * scale)/2f;

			result = new float[4];
			result[0] = left;
			result[1] = top;
			result[2] = right;
			result[3] = bottom;
		}
		return result;
	}

	/**
	 * 计算图片与 指定宽高 之间的缩放比例
	 * @param getMaxValue 取最大值还是最小值
	 * @return 返回比例的最大值or最小值
	 */
	protected float countImgScale(float inW,float inH,float outW,float outH, boolean getMaxValue)
	{
		float scale_x,scale_y,scale;
		scale_x = outW/inW;
		scale_y = outH/inH;
		if(getMaxValue){
			scale = Math.max(scale_x,scale_y);
		}else {
			scale = Math.min(scale_x,scale_y);
		}
		return scale;
	}

	/**
	 * 逆矩阵换算点的坐标
	 * @param matrices 逆换算的矩阵数组
	 * @param src 坐标点数组
	 */
	protected void inverseCount(float[] src, Matrix[] matrices)
	{
		if(matrices == null || src == null){
			return ;
		}

		int count = matrices.length;
		if(count <=0)
		{
			return;
		}

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix temp = new Matrix();
		canvas.getMatrix(temp);
		Matrix invert = new Matrix();
		temp.invert(invert);
		invert.mapPoints(src);
	}

	protected void inverseCount(float[] dst, float[] src, Matrix[] matrices)
	{
		if(matrices == null || src == null){
			return;
		}

		int matrixCount = matrices.length;
		int count = src.length;
		if(matrixCount <=0 || count <= 0)
		{
			return;
		}

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix temp = new Matrix();
		canvas.getMatrix(temp);
		Matrix invert = new Matrix();
		temp.invert(invert);
		invert.mapPoints(dst,src);
	}

	/**
	 * 逆矩阵换算矩形
	 * @param matrices 逆换算的矩阵数组
	 * @param rectF 矩形
	 */
	protected void inverseCount(RectF rectF, Matrix[] matrices)
	{
		if(matrices == null || rectF == null){
			return ;
		}

		int count = matrices.length;
		if(count <=0)
		{
			return;
		}

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix temp = new Matrix();
		canvas.getMatrix(temp);
		Matrix invert = new Matrix();
		temp.invert(invert);
		invert.mapRect(rectF);
	}

	protected void inverseCount(RectF dst, RectF src, Matrix[] matrices)
	{
		if(matrices == null || src == null){
			return ;
		}

		int count = matrices.length;
		if(count <=0)
		{
			return;
		}

		Canvas canvas = new Canvas();
		for(Matrix temp : matrices)
		{
			canvas.concat(temp);
		}
		Matrix temp = new Matrix();
		canvas.getMatrix(temp);
		Matrix invert = new Matrix();
		temp.invert(invert);
		invert.mapRect(dst, src);
	}

	/**
	 * 平移
	 */
	@Override
	protected void Run_M(Matrix matrix, float x, float y)
	{
		if(mTarget == global)// 整体平移
		{
			matrix.postTranslate(x - mGammaX, y - mGammaY);
		}
		else
		{
			// 通过逆矩阵求点的坐标
			float[] src = new float[]{x,y,mGammaX,mGammaY};
			float[] dst = new float[src.length];
			Matrix[] matrices = new Matrix[]{global.m_matrix};
			inverseCount(dst,src,matrices);
			matrix.postTranslate(dst[0] - dst[2],dst[1] - dst[3]);
		}
	}

	/**
	 * 旋转
	 */
	@Override
	protected void Run_R(Shape target, float x1, float y1, float x2, float y2)
	{
		float tempAngle;
		if(x1 - x2 == 0)
		{
			if(y1 >= y2)
			{
				tempAngle = 90;
			}
			else
			{
				tempAngle = -90;
			}
		}
		else if(y1 - y2 != 0)
		{
			tempAngle = (float)Math.toDegrees(Math.atan(((double)(y1 - y2)) / (x1 - x2)));
			if(x1 < x2)
			{
				tempAngle += 180;
			}
		}
		else
		{
			if(x1 >= x2)
			{
				tempAngle = 0;
			}
			else
			{
				tempAngle = 180;
			}
		}

		float[] src = new float[]{(mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f};
		float[] dst = new float[src.length];
		Matrix[] matrices = new Matrix[]{global.m_matrix};
		inverseCount(dst, src, matrices);

		target.m_matrix.postRotate(tempAngle - mBeta, dst[0],dst[1]);
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
		float sw1 = getScaleByW(target);
		float sh1 = getScaleByH(target);

		float tempDist = ImageUtils.Spacing(x1 - x2, y1 - y2);
		if(tempDist > 10)
		{
			scale = tempDist / mDelta;
		}

		Run_Z(target,scale,scale);

		// 计算move后图片的缩放比例
		float sw2 = getScaleByW(target);
		float sh2 = getScaleByH(target);
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
			target.m_matrix.postScale(scaleX, scaleY, (mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f);
		}
		else
		{
			float[] src = new float[]{(mDownX1 + mDownX2) / 2f, (mDownY1 + mDownY2) / 2f};
			float[] dst = new float[src.length];
			Matrix[] matrices = new Matrix[]{global.m_matrix};
			inverseCount(dst, src, matrices);
			target.m_matrix.postScale(scaleX, scaleY, dst[0],dst[1]);
		}
	}

	/**
	 * 计算图片 target 当前width 与 某宽度 的缩放值<br/>
	 * 默认 与画布的宽作比较
	 * @param target
	 * @return
	 */
	protected float getScaleByW(Shape target)
	{
		if(target == global){
			target = img;
		}
		float[] curPos = getImgLogicPos(target);
		if(curPos != null)
		{
			float curImgW = ImageUtils.Spacing(curPos[0] - curPos[2], curPos[1] - curPos[3]);
			float orgImgW = (canvas_r - canvas_l);
			return curImgW / orgImgW;
		}
		return -1;
	}

	/**
	 * 计算图片 target 当前height 与 某高度 的缩放值<br/>
	 * 默认 与画布的宽作比较
	 * @param target
	 * @return
	 */
	protected float getScaleByH(Shape target)
	{
		if(target == global){
			target = img;
		}
		float[] curPos = getImgLogicPos(target);
		if(curPos != null)
		{
			float curImgW = ImageUtils.Spacing(curPos[0] - curPos[6], curPos[1] - curPos[7]);
			float orgImgW = (canvas_b - canvas_t);
			return curImgW / orgImgW;
		}

		return -1;
	}

	/**
	 * 单指down
	 */
	@Override
	protected void OddDown(MotionEvent event)
	{
		mTween.M1End();
		mTarget = getShowMatrix(mDownX,mDownY);
		Init_M_Data(mTarget, mDownX, mDownY);
		this.invalidate();
	}

	/**
	 * 单指move
	 */
	@Override
	protected void OddMove(MotionEvent event)
	{
		Run_M(mTarget, event.getX(), event.getY());
		this.invalidate();
	}

	/**
	 * 单指up
	 */
	@Override
	protected void OddUp(MotionEvent event)
	{
		mTween.M1End();
		Init_M_Data(mTarget, mUpX, mUpY);
	}

	/**
	 * 双指down
	 */
	@Override
	protected void EvenDown(MotionEvent event)
	{
		mTween.M1End();
		mTarget = getShowMatrix(mDownX1, mDownY1, mDownX2, mDownY2);
		Init_MRZ_Data(mTarget, mDownX1, mDownY1, mDownX2, mDownY2);
		this.invalidate();
	}

	/**
	 * 双指move
	 */
	@Override
	protected void EvenMove(MotionEvent event)
	{
		Run_MRZ(mTarget, event.getX(0), event.getY(0), event.getX(1), event.getY(1));
		this.invalidate();
	}

	/**
	 * 双指up
	 */
	@Override
	protected void EvenUp(MotionEvent event)
	{
		mTween.M1End();
		int i;
		if(event.getActionIndex() == 0){
			i = 1;
		}else{
			i = 0;
		}
		mUpX = event.getX(i);
		mUpY = event.getY(i);
		Init_M_Data(mTarget, mUpX, mUpY);
	}

	/**
	 * 基于屏幕左上角为原点，换算图片位置 --->> 屏幕坐标
	 * @return 返回长度8的数组 0->lt_x 1->lt_y 顺时针如此类推
	 */
	protected float[] getImgShowPos(Shape target)
	{
		if(target.m_bmp == null){
			return null;
		}

		float[] src = new float[8];
		float[] dst = new float[8];

		// left-top
		src[0] = 0;
		src[1] = 0;
		// right-top
		src[2] = target.m_bmp.getWidth();
		src[3] = 0;
		// right-bottom
		src[4] = target.m_bmp.getWidth();
		src[5] = target.m_bmp.getHeight();
		// left-bottom
		src[6] = 0;
		src[7] = target.m_bmp.getHeight();

		// 自身变换
		target.m_matrix.mapPoints(src);
		// 由于改变了原点位置，需要将图片先移动到当前原点
		global.m_matrix.postTranslate(canvas_l,canvas_t);
		// 相对temp的变换
		global.m_matrix.mapPoints(dst,src);
		// 还原矩阵状态
		global.m_matrix.postTranslate(-canvas_l,-canvas_t);

		return dst;
	}

	/**
	 * 基于屏幕左上角为原点 --->> 屏幕坐标<br/>
	 * 换算出target的图片当前外切矩形的坐标
	 * @return 返回长度4的数组 0->left 1->top 2->right 3->bottom
	 */
	protected float[] getImgShowRect(Shape target)
	{
		if(target.m_bmp == null){
			return null;
		}

		float[] src = new float[8];
		float[] dst = new float[8];
		float[] matrixX = new float[4];
		float[] matrixY = new float[4];
		float[] result = new float[4];

		// left-top
		src[0] = 0;
		src[1] = 0;
		// right-top
		src[2] = target.m_bmp.getWidth();
		src[3] = 0;
		// right-bottom
		src[4] = target.m_bmp.getWidth();
		src[5] = target.m_bmp.getHeight();
		// left-bottom
		src[6] = 0;
		src[7] = target.m_bmp.getHeight();

		// 自身变换
		target.m_matrix.mapPoints(src);
		// 由于改变了原点位置，需要将图片先移动到当前原点
		global.m_matrix.postTranslate(canvas_l,canvas_t);
		// 相对temp的变换
		global.m_matrix.mapPoints(dst,src);
		// 还原矩阵状态
		global.m_matrix.postTranslate(-canvas_l,-canvas_t);

		// 求变换后矩形的外切矩形
		// 从小到大排序
		matrixX[0] = dst[0];
		matrixX[1] = dst[2];
		matrixX[2] = dst[4];
		matrixX[3] = dst[6];
		Arrays.sort(matrixX);

		matrixY[0] = dst[1];
		matrixY[1] = dst[3];
		matrixY[2] = dst[5];
		matrixY[3] = dst[7];
		Arrays.sort(matrixY);

		// 变换后的矩形四个点
		result[0] = matrixX[0];// left
		result[1] = matrixY[0];// top
		result[2] = matrixX[3];// right
		result[3] = matrixY[3];// bottom
		return result;
	}

	/**
	 * 基于画布left，top为原点的 Matrix变换 --->> 逻辑坐标<br/>
	 * 换算出target的图片对应的坐标
	 * @return 返回长度8的数组 0->lt_x 1->lt_y 顺时针如此类推
	 */
	protected float[] getImgLogicPos(Shape target)
	{
		if(target.m_bmp == null)
		{
			return null;
		}

		float[] src = new float[8];
		float[] dst = new float[8];

		// left-top
		src[0] = 0;
		src[1] = 0;
		// right-top
		src[2] = target.m_bmp.getWidth();
		src[3] = 0;
		// right-bottom
		src[4] = target.m_bmp.getWidth();
		src[5] = target.m_bmp.getHeight();
		// left-bottom
		src[6] = 0;
		src[7] = target.m_bmp.getHeight();

		// 自身变换
		target.m_matrix.mapPoints(src);
		// 相对global的变换
		global.m_matrix.mapPoints(dst, src);

		return dst;
	}

	/**
	 * 基于画布left，top为原点的 Matrix变换 --->> 逻辑坐标<br/>
	 * 换算出target的图片当前外切矩形的坐标
	 * @return 返回长度4的数组 0->left 1->top 2->right 3->bottom
	 */
	protected float[] getImgLogicRect(Shape target)
	{
		if(target.m_bmp == null){
			return null;
		}

		float[] src = new float[8];
		float[] dst = new float[8];
		float[] matrixX = new float[4];
		float[] matrixY = new float[4];
		float[] result = new float[4];

		// left-top
		src[0] = 0;
		src[1] = 0;
		// right-top
		src[2] = target.m_bmp.getWidth();
		src[3] = 0;
		// right-bottom
		src[4] = target.m_bmp.getWidth();
		src[5] = target.m_bmp.getHeight();
		// left-bottom
		src[6] = 0;
		src[7] = target.m_bmp.getHeight();

		// 自身变换
		target.m_matrix.mapPoints(src);
		// 相对底层矩阵变换
		global.m_matrix.mapPoints(dst,src);

		// 求变换后矩形的外切矩形
		// 从小到大排序
		matrixX[0] = dst[0];
		matrixX[1] = dst[2];
		matrixX[2] = dst[4];
		matrixX[3] = dst[6];
		Arrays.sort(matrixX);

		matrixY[0] = dst[1];
		matrixY[1] = dst[3];
		matrixY[2] = dst[5];
		matrixY[3] = dst[7];
		Arrays.sort(matrixY);

		// 变换后的矩形四个点
		result[0] = matrixX[0];// left
		result[1] = matrixY[0];// top
		result[2] = matrixX[3];// right
		result[3] = matrixY[3];// bottom
		return result;
	}

	/**
	 * 逻辑坐标转换为屏幕显示坐标
	 * 逻辑坐标：以canvas原点为零点的坐标系
	 */
	protected float[] getShowPos(float[] dst, float[] src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(canvas_l,canvas_t);
		matrix.mapPoints(dst,src);
		return dst;
	}

	protected float[] getShowPos(float[] src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(canvas_l,canvas_t);
		matrix.mapPoints(src);
		return src;
	}

	/**
	 * 屏幕显示坐标转换为逻辑坐标<br/>
	 * 逻辑坐标：以canvas原点为零点的坐标系
	 */
	protected float[] getLogicPos(float[] dst, float[] src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(-canvas_l,-canvas_t);
		matrix.mapPoints(dst,src);
		return dst;
	}

	protected float[] getLogicPos(float[] src)
	{
		Matrix matrix = new Matrix();
		matrix.postTranslate(-canvas_l,-canvas_t);
		matrix.mapPoints(src);
		return src;
	}

	/**
	 * 瞬间将图片恢复到最初状态
	 */
	public void reset()
	{
		mPendantArr.clear();
		if(frame.m_bmp != null && !frame.m_bmp.isRecycled()){
			frame.m_bmp.recycle();
			frame.m_bmp = null;
		}

		img.m_matrix.reset();
		float scale = countImgScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),getWidth(),getHeight(),false);
		float[] canvasPos = countImgPos(img,getWidth(),getHeight(),scale);
		updateCanvasSize(canvasPos);
		img.m_matrix.postScale(scale,scale);
		invalidate();
	}

	/**
	 * @param x1 起始点x
	 * @param y1 起始点y
	 * @param x2 结束点x
	 * @param y2 结束点y
	 */
	private void updatePosition(Shape target, float x1, float y1, float x2, float y2)
	{
		if(target == global)// 整体
		{
			points = new float[]{x1,y1,x2,y2};
		}
		else
		{
			float[] src = new float[]{x1,y1,x2,y2};
			points = new float[src.length];
			Matrix[] matrices = new Matrix[]{global.m_matrix};
			inverseCount(points,src,matrices);
		}
		if(points!=null && isAnimFinish){
			isAnimFinish = false;
			mTween.Init(0,1,400);
			mTween.M1Start(TweenLite.EASING_BACK | TweenLite.EASE_OUT);
			tween_matrix.set(target.m_matrix);
			updatePosition();
		}
	}

	private void updatePosition()
	{
		if(mTarget == global)
		{
			mTarget.m_matrix.set(tween_matrix);
			mTarget.m_matrix.postTranslate((points[0] - points[2])*mTween.M1GetPos(),(points[1] - points[3])*mTween.M1GetPos());
		}
		else
		{
			img.m_matrix.set(tween_matrix);
			img.m_matrix.postTranslate((points[0] - points[2])*mTween.M1GetPos(),(points[1] - points[3])*mTween.M1GetPos());
		}

		invalidate();
		if(!mTween.M1IsFinish()){
			this.postDelayed(mViewAnimRunnable,1);
		}else {
			// 动画结束，清楚状态
			tween_matrix.reset();
			if(mTarget == global)
			{
				m_temp_global_matrix.set(global.m_matrix);
			}
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

	private Runnable mViewAnimRunnable = new Runnable()
	{
		@Override
		public void run() {
			updatePosition();
		}
	};

	/**
	 * 单指up时图片回弹，图片变换没有限制 <br/>
	 * 保留图片部分区域
	 * @param target 回弹矩阵
	 */
	protected void kickBack1(Shape target)
	{
		float imgL, imgT, imgR, imgB;
		// 计算当前图片所在的位置
		float[] result = getImgShowRect(target);
		imgL = result[0];
		imgT = result[1];
		imgR = result[2];
		imgB = result[3];

		float left = canvas_l;
		float top = canvas_t;
		float right = canvas_r;
		float bottom = canvas_b;

		// 当前canvas显示区域中心点
		float CenterX = (left + right)/2f;
		float CenterY = (top + bottom)/2f;

		// 变换后的图片中心点
		float imgCenterX = (imgR + imgL)/2f;
		float imgCenterY = (imgB + imgT)/2f;

		float orgArea = (right - left)*(bottom - top);
		float curArea = (imgR - imgL)*(imgB - imgT);

		// 放大
		if(curArea > orgArea){
			if(imgL > CenterX){
				if(imgT > CenterY){
					// 图片左上角超过canvas中心点
					updatePosition(target,CenterX,CenterY,imgL,imgT);
				}else if(imgB < CenterY){
					// 图片左下角超过canvas中心点
					updatePosition(target,CenterX,CenterY,imgL,imgB);
				}else {
					// 图片左边超过中心点
					updatePosition(target,CenterX,0,imgL,0);
				}
			}else if(imgR < CenterX){
				if(imgB < CenterY){
					// 图片右下角超过中心点
					updatePosition(target,CenterX,CenterY,imgR,imgB);
				}else if(imgT > CenterY){
					// 图片右上角超过中心点
					updatePosition(target,CenterX,CenterY,imgR,imgT);
				}else {
					// 图片右边超过中心点
					updatePosition(target,CenterX,0,imgR,0);
				}
			}else if(imgT > CenterY){
				// 图片顶边超过中心点
				updatePosition(target,0,CenterY,0,imgT);
			}else if(imgB < CenterY){
				// 图片底边超过中心点
				updatePosition(target,0,CenterY,0,imgB);
			}
		}else {// 缩小
			if(imgCenterX < left){
				if(imgCenterY > bottom){
					// 图片右上角准备超出屏幕
					updatePosition(target,left,bottom,imgCenterX,imgCenterY);
				}else if(imgCenterY < top){
					// 图片右下角准备超出屏幕
					updatePosition(target,left,top,imgCenterX,imgCenterY);
				}else{
					// 图片右边准备超出屏幕
					updatePosition(target,left,0,imgCenterX,0);
				}
			}else if(imgCenterX > right){
				if(imgCenterY > bottom){
					// 图片左上角准备超出屏幕
					updatePosition(target,right,bottom,imgCenterX,imgCenterY);
				}else if(imgCenterY < top){
					// 图片左下角准备超出屏幕
					updatePosition(target,right,top,imgCenterX,imgCenterY);
				}else{
					// 图片左边准备超出屏幕
					updatePosition(target,right,0,imgCenterX,0);
				}
			}else if(imgCenterY < top){
				// 图片底边准备超出屏幕
				updatePosition(target,0,top,0,imgCenterY);
			}else if(imgCenterY > bottom){
				// 图片顶边准备超出屏幕
				updatePosition(target,0,bottom,0,imgCenterY);
			}
		}
	}

	/**
	 * @return 以底图原始尺寸输出当前bitmap
	 */
	public Bitmap getOutPutBmp()
	{
		int size = img.m_bmp.getWidth() > img.m_bmp.getHeight() ? img.m_bmp.getWidth() : img.m_bmp.getHeight();

		float whscale = (canvas_r - canvas_l) / (canvas_b - canvas_t);
		float outW = size;
		float outH = outW / whscale;
		if(outH > size)
		{
			outH = size;
			outW = outH * whscale;
		}

		float scaleX = outW / (canvas_r - canvas_l);
		float scaleY = outH / (canvas_b - canvas_t);
		float scale = Math.min(scaleX,scaleY);
		global.m_matrix.postScale(scale,scale);

		Bitmap outBmp = Bitmap.createBitmap((int)outW, (int)outH, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(outBmp);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));

		Bitmap tempBmp;

		if(bkColor != 0){
			canvas.save();
			canvas.drawColor(bkColor);
			canvas.restore();
		}

		if(img.m_bmp != null)
		{
			tempBmp = img.m_bmp;
			if(tempBmp != null)
			{
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setFilterBitmap(true);
				canvas.save();
				canvas.concat(global.m_matrix);
				canvas.drawBitmap(tempBmp, img.m_matrix, mPaint);
				canvas.restore();
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		if(frame.m_bmp != null)
		{
			tempBmp = frame.m_bmp;
			if(tempBmp != null)
			{
				mPaint.reset();
				mPaint.setAntiAlias(true);
				mPaint.setFilterBitmap(true);
				canvas.save();
				canvas.concat(global.m_matrix);
				canvas.drawBitmap(tempBmp, frame.m_matrix, mPaint);
				canvas.restore();
				tempBmp.recycle();
				tempBmp = null;
			}
		}

		if(mPendantArr != null && mPendantArr.size()>0)
		{
			for(Shape pendant : mPendantArr){
				tempBmp = pendant.m_bmp;
				if(tempBmp != null)
				{
					mPaint.reset();
					mPaint.setAntiAlias(true);
					mPaint.setFilterBitmap(true);
					canvas.save();
					canvas.concat(global.m_matrix);
					canvas.drawBitmap(tempBmp, pendant.m_matrix, mPaint);
					canvas.restore();
					tempBmp.recycle();
					tempBmp = null;
				}
			}
		}
		return outBmp;
	}
}