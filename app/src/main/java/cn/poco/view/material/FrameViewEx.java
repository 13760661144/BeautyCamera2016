package cn.poco.view.material;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.MotionEvent;

import cn.poco.view.RelativeView;

/**
 * 相框
 * Created by admin on 2017/1/12.
 */

public class FrameViewEx extends RelativeView
{
	// 相框界面的类型
	public static final int TYPE_THEME = 1;
	public static final int TYPE_SAMPLE = 2;
	protected int mType = -1;

	public FrameViewEx(Context context)
	{
		super(context);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// 画遮罩之类
		if(frame != null && frame.m_bmp != null && !frame.m_bmp.isRecycled()){
			canvas.save();
			canvas.translate(canvas_l,canvas_t);
			canvas.concat(global.m_matrix);
			mPaint.reset();
			mPaint.setAntiAlias(true);
			mPaint.setFilterBitmap(true);
			mPaint.setAlpha((int) ((float) mFilterAlpha / 100f * 255));
			canvas.drawBitmap(frame.m_bmp,frame.m_matrix,mPaint);
			canvas.restore();
		}
	}

	@Override
	protected void OddUp(MotionEvent event)
	{
		super.OddUp(event);
		kickBack1(img);
	}

	@Override
	protected void updateContent(int width, int height)
	{
		super.updateContent(width, height);

		if(frame.m_bmp != null)
		{
			float scale_x = 1,scale_y = 1;
			float scale1,scale2;

			// 原始frame缩放的比例
			scale1 = countImgScale(frame.m_bmp.getWidth(),frame.m_bmp.getHeight(),view_w,view_h,false);

			// 当前frame缩放的比例
			scale2 = countImgScale(frame.m_bmp.getWidth(),frame.m_bmp.getHeight(),width,height,false);

			// 计算当前frame各点坐标
			float[] cur = countImgPos(frame,width,height,scale2);

			// 更新画布区域
			updateCanvasSize(cur);

			if(scale1 != 0)
			{
				scale_x = scale2 / scale1;
				scale_y = scale2 / scale1;
			}

			global.m_matrix.set(m_temp_global_matrix);
			global.m_matrix.postScale(scale_x,scale_y);
		}
	}

	/**
	 * 添加相框
	 * @param bitmap 图片
	 * @param rect 图片显示的矩形区域，可以为null
	 */
	public void setFrame(Bitmap bitmap, RectF rect)
	{
		float scale2,scale3;
		float frameL =0,frameT =0,frameR =0,frameB =0;
		float imgL=0,imgT=0,imgR=0,imgB=0;
		float[] result;

		if(rect == null)
		{
			mType = TYPE_THEME;
		}
		else
		{
			mType = TYPE_SAMPLE;
		}

		// 为了与图片相对temp的缩放比例一致
		syncScaling();

		frame.m_bmp = bitmap;

		// 当前相框应该显示的区域 --> 屏幕坐标
		scale3 = countImgScale(frame.m_bmp.getWidth(),frame.m_bmp.getHeight(),view_w,view_h,false);
		result = countImgPos(frame,view_w,view_h,scale3);
		if(result != null)
		{
			frameL = result[0];
			frameT = result[1];
			frameR = result[2];
			frameB = result[3];
		}
		updateCanvasSize(result);
		frame.m_matrix.reset();
		frame.m_matrix.postScale(scale3,scale3);
		img.m_matrix.reset();

		if(mType == TYPE_THEME)// 主题
		{
			// 1.计算相框与图片之间的比例
			scale2 = countImgScale(img.m_bmp.getWidth(), img.m_bmp.getHeight(), frameR - frameL, frameB - frameT, true);
			img.m_matrix.postScale(scale2,scale2);

			// 2.基于屏幕左上角为原点，判断现在图片的坐标位置
			result = getImgShowPos(img); // --> 屏幕坐标
			if(result != null && result.length == 8)
			{
				imgL = result[0];
				imgT = result[1];
				imgR = result[2];
				imgB = result[5];
			}
			float transX = (frameR+frameL)/2f-(imgR+imgL)/2f;
			float transY = (frameB+frameT)/2f-(imgB+imgT)/2f;
			// 3.将图片中心点平移到矩形中心
			img.m_matrix.postTranslate(transX,transY);
		}
		else if(mType == TYPE_SAMPLE)// 简约
		{

			// 1.当前缩放比例下，矩形宽高
			float rectL = frameL + (frameR - frameL)* rect.left;
			float rectT = frameT + (frameB - frameT)* rect.top;
			float rectR = frameL + (frameR - frameL)* rect.right;
			float rectB = frameT + (frameB - frameT)* rect.bottom;

			// 2.计算当前矩形区域与初始图片之间的变换比例s2
			float s2 = countImgScale(img.m_bmp.getWidth(),img.m_bmp.getHeight(),rectR - rectL,rectB - rectT,true);
			img.m_matrix.postScale(s2,s2);
			// 3.基于屏幕左上角为原点，判断现在图片的坐标位置
			result = getImgShowPos(img);// --> 屏幕坐标
			if(result != null && result.length == 8)
			{
				imgL = result[0];
				imgT = result[1];
				imgR = result[2];
				imgB = result[5];
			}
			float transX = (rectR+rectL)/2f - (imgR+imgL)/2f;
			float transY = (rectB+rectT)/2f - (imgB+imgT)/2f;
			// 4.将图片中心点平移到矩形中心
			img.m_matrix.postTranslate(transX,transY);
		}
		this.invalidate();
	}
}