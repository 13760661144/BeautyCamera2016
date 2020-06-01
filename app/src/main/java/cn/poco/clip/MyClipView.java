package cn.poco.clip;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

import cn.poco.display.ClipView;
import cn.poco.tianutils.ShareData;

public class MyClipView extends ClipView
{
	private float def_btn_size; //按钮的大小
	private float def_btn_h2; //四侧按钮的宽度
	private int def_btn_color = 0xffffffff; //按钮颜色
	private float def_over_scale = 1.25f; //按下时按钮的放大倍数

	private Paint temp_paint = new Paint();

	public MyClipView(Activity ac, int frW, int frH, Callback cb)
	{
		super(ac, frW, frH, cb);

		def_btn_size = (ShareData.PxToDpi_xhdpi(94) + 1) / 2 * 2;
		def_btn_h2 = (ShareData.PxToDpi_xhdpi(2) + 1) / 2 * 2;

		def_rect_color = 0xffffffff;
		def_mask_color = 0x8c000000;
	}

	@Override
	public void DrawBtnLT(Canvas canvas, boolean isTouch)
	{
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setFilterBitmap(true);
		temp_paint.setStyle(Style.FILL);
		temp_paint.setColor(def_btn_color);

		float r = def_btn_size / 2f;
		if(isTouch)
		{
			r *= def_over_scale;
		}
		r = (int)(r + 0.5f);

		float w2 = def_btn_h2;
		if(isTouch)
		{
			w2 *= def_over_scale;
		}
		w2 = (int)(w2 + 0.5f);

		canvas.drawRect(-w2, -w2, r, w2, temp_paint);
		canvas.drawRect(-w2, -w2, w2, r, temp_paint);
	}

	@Override
	public void DrawBtnCT(Canvas canvas, boolean isTouch)
	{
		/*temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setFilterBitmap(true);
		temp_paint.setStyle(Style.FILL);
		temp_paint.setColor(def_btn_color);

		float r = def_btn_size / 2f;
		if(isTouch)
		{
			r *= def_over_scale;
		}
		r = (int)(r + 0.5f);

		float w2 = def_btn_h2;
		if(isTouch)
		{
			w2 *= def_over_scale;
		}
		w2 = (int)(w2 + 0.5f);

		canvas.drawRect(-r, -w2, r, w2, temp_paint);*/
	}

	@Override
	public void DrawBtnRT(Canvas canvas, boolean isTouch)
	{
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setFilterBitmap(true);
		temp_paint.setStyle(Style.FILL);
		temp_paint.setColor(def_btn_color);

		float r = def_btn_size / 2f;
		if(isTouch)
		{
			r *= def_over_scale;
		}
		r = (int)(r + 0.5f);

		float w2 = def_btn_h2;
		if(isTouch)
		{
			w2 *= def_over_scale;
		}
		w2 = (int)(w2 + 0.5f);

		canvas.drawRect(-r, -w2, w2, w2, temp_paint);
		canvas.drawRect(-w2, -w2, w2, r, temp_paint);
	}

	@Override
	public void DrawBtnRC(Canvas canvas, boolean isTouch)
	{
		/*temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setFilterBitmap(true);
		temp_paint.setStyle(Style.FILL);
		temp_paint.setColor(def_btn_color);

		float r = def_btn_size / 2f;
		if(isTouch)
		{
			r *= def_over_scale;
		}
		r = (int)(r + 0.5f);

		float w2 = def_btn_h2;
		if(isTouch)
		{
			w2 *= def_over_scale;
		}
		w2 = (int)(w2 + 0.5f);

		canvas.drawRect(-w2, -r, w2, r, temp_paint);*/
	}

	@Override
	public void DrawBtnRB(Canvas canvas, boolean isTouch)
	{
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setFilterBitmap(true);
		temp_paint.setStyle(Style.FILL);
		temp_paint.setColor(def_btn_color);

		float r = def_btn_size / 2f;
		if(isTouch)
		{
			r *= def_over_scale;
		}
		r = (int)(r + 0.5f);

		float w2 = def_btn_h2;
		if(isTouch)
		{
			w2 *= def_over_scale;
		}
		w2 = (int)(w2 + 0.5f);

		canvas.drawRect(-r, -w2, w2, w2, temp_paint);
		canvas.drawRect(-w2, -r, w2, w2, temp_paint);
	}

	@Override
	public void DrawBtnCB(Canvas canvas, boolean isTouch)
	{
		/*DrawBtnCT(canvas, isTouch);*/
	}

	@Override
	public void DrawBtnLB(Canvas canvas, boolean isTouch)
	{
		temp_paint.reset();
		temp_paint.setAntiAlias(true);
		temp_paint.setFilterBitmap(true);
		temp_paint.setStyle(Style.FILL);
		temp_paint.setColor(def_btn_color);

		float r = def_btn_size / 2f;
		if(isTouch)
		{
			r *= def_over_scale;
		}
		r = (int)(r + 0.5f);

		float w2 = def_btn_h2;
		if(isTouch)
		{
			w2 *= def_over_scale;
		}
		w2 = (int)(w2 + 0.5f);

		canvas.drawRect(-w2, -w2, r, w2, temp_paint);
		canvas.drawRect(-w2, -r, w2, w2, temp_paint);
	}

	@Override
	public void DrawBtnLC(Canvas canvas, boolean isTouch)
	{
		/*DrawBtnRC(canvas, isTouch);*/
	}
}
