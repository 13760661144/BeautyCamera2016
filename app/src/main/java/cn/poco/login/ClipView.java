package cn.poco.login;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;

import cn.poco.display.SimplePreviewV2;

/**
 * 上传头像剪裁图片view
 */
public class ClipView extends SimplePreviewV2 {

	private int mWidth;
	private int mHeight;
	public int def_max_size = 1;
	public static final int MODE_CENTER_WRAP = 3;
	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ClipView(Context context) {
		super(context);
	}
	
	public void setImage(Object bmp){
		def_space_size = 0;
		SetMode(MODE_CENTER_WRAP);
		super.SetImage(bmp);
		m_img.MAX_SCALE = 4.0f;
	}
	
	public Bitmap getClipBmp(){
		float scale1 = (float)(mWidth - (def_space_size << 1)) / (float)m_img.m_w;
		float scale2 = (float)(mHeight - (def_space_size << 1)) / (float)m_img.m_h;
		float initScale = scale1 > scale2 ? scale2 : scale1;
		float targetWH = m_img.m_w;
		if(scale1 > scale2)
		{
			targetWH = m_img.m_h;
		}
		float finalScale = scale1/m_img.m_scaleX;
		int finalWidth = (int) (targetWH*finalScale);
		int finalHeight = finalWidth;
		Bitmap bmp = Bitmap.createBitmap(finalWidth,finalHeight,Config.ARGB_8888);
		float scaleX = 1f/(mWidth*1.0f/finalWidth*1.0f);
		float scaleY = 1f/(mHeight*1.0f/finalHeight*1.0f);
		Canvas canvas = new Canvas(bmp);
		canvas.save();
		canvas.scale(scaleX,scaleY);
		if(m_img != null && m_img.m_bmp != null){
			canvas.drawColor(Color.WHITE);
			//防锯齿
			PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);  
			canvas.setDrawFilter(pfd);
			canvas.drawBitmap(m_img.m_bmp, temp_matrix, temp_paint);
		}
		canvas.restore();
		return bmp;
	}
	
	@Override
	protected void InitImgShowPos(int w, int h)
	{
		if(w > 0 && h > 0)
		{
			if(m_img != null)
			{
				m_img.m_x = w / 2f - m_img.m_centerX;
				m_img.m_y = h / 2f - m_img.m_centerY;
				float scale;
				{
					float scale1 = (float)(w) / (float)m_img.m_w;
					float scale2 = (float)(h) / (float)m_img.m_h;
					if(m_mode == MODE_CENTER_WRAP){
						scale = scale1 > scale2 ? scale1 : scale2;
					}else{
						scale = scale1 > scale2 ? scale2 : scale1;
					}

				}
				if(m_mode == MODE_CENTER)
				{
					if(scale > 1)
					{
						scale = 1;
					}
				}
				m_img.DEF_SCALE = scale;
				m_img.m_scaleX = scale;
				m_img.m_scaleY = scale;
				m_img.MIN_SCALE = scale;
				{
					float scale1 = (float)(w << def_max_size) / (float)m_img.m_w;
					float scale2 = (float)(h << def_max_size) / (float)m_img.m_h;
					m_img.MAX_SCALE = scale1 > scale2 ? scale1 : scale2;
					if(m_img.MAX_SCALE < 1)
					{
						m_img.MAX_SCALE = 1;
					}
				}
				

				if(m_mode == MODE_CENTER_WRAP && scale > m_img.MAX_SCALE){
					m_img.DEF_SCALE = m_img.MAX_SCALE;
					m_img.m_scaleX = m_img.MAX_SCALE;
					m_img.m_scaleY = m_img.MAX_SCALE;
					m_img.MIN_SCALE = m_img.MAX_SCALE;
				}
				
			}
		}
	}
	@Override
	public void SetMode(int mode)
	{
		switch(mode)
		{
			case MODE_CENTER:
			case MODE_CENTER_CROP:
			case MODE_CENTER_WRAP:
				m_mode = mode;
				break;
			default:
				break;
		}
	}
	
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    mWidth = measureWidthHeight(widthMeasureSpec);
	    mHeight = measureWidthHeight(heightMeasureSpec);
	}
	
	private int measureWidthHeight(int measureSpec){
		int result = 0;
		int mode = MeasureSpec.getMode(measureSpec);
		int size = MeasureSpec.getSize(measureSpec);
		switch (mode) {
		/**
		 * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
		 * MeasureSpec.AT_MOST。
		 *
		 * MeasureSpec.EXACTLY是精确尺寸，
		 * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
		 * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
		 *
		 * MeasureSpec.AT_MOST是最大尺寸，
		 * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
		 * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
		 * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
		 *
		 * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
		 * 通过measure方法传入的模式。
		 */
		case MeasureSpec.AT_MOST:
		case MeasureSpec.EXACTLY:
			result = size;
			break;
	   }
		return result;
	}

	

}
