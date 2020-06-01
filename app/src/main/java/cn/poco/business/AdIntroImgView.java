package cn.poco.business;

import android.content.Context;
import android.graphics.Bitmap;

//居顶
//public class AdIntroImgView extends View
//{
//	private Bitmap mBackBmp = null;
//
//	public AdIntroImgView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//	}
//
//	public AdIntroImgView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//	}
//
//	public AdIntroImgView(Context context) {
//		super(context);
//	}
//
//	public void clear()
//	{
//		if(mBackBmp != null && mBackBmp.isRecycled() == false)
//		{
//			mBackBmp.recycle();
//			mBackBmp = null;
//		}
//	}
//
//	public void setImageBitmap(Bitmap bmp)
//	{
//		mBackBmp = bmp;
//		invalidate();
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		if(mBackBmp == null)
//			return;
//		int w = getWidth();
//		int h = w*mBackBmp.getHeight()/mBackBmp.getWidth();
//		canvas.save();
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
//		canvas.drawBitmap(mBackBmp, new Rect(0, 0, mBackBmp.getWidth(), mBackBmp.getHeight()), new Rect(0, 0, w, h), null);
//		canvas.restore();
//	}
//}

//2018年3月5日改为居中
public class AdIntroImgView extends ADImageView
{

	public AdIntroImgView(Context context)
	{
		super(context);
	}

	public void setImageBitmap(Bitmap bmp)
	{
		setBitmap(bmp);
	}

	public void clear()
	{
		bitmap = null;
	}
}