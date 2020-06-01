package cn.poco.share;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class BindPocoDialog extends Dialog
{	
	public static final int ID_DETERMINE = 0;
	public static final int ID_OTHER = 1;
	
	private Context context;
	private Bitmap icon;
	private Bitmap background;
	private String nickname;
	private String tips;
	
	private TextView exit;
	private FrameLayout determine;
	private FrameLayout other;
	private SharePage.DialogListener listener;
	
	public BindPocoDialog(Context context, int theme, String tips, Bitmap bmp, Bitmap bg, String nickname, SharePage.DialogListener listener)
	{
		super(context, theme);
		this.context = context;
		this.icon = ShareFrame.makeCircle(makeBmp(bmp));
		this.nickname = nickname;
		this.tips = tips;
		this.listener = listener;
		this.background = bg;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setCanceledOnTouchOutside(false);

		LinearLayout.LayoutParams ll;
		FrameLayout.LayoutParams fl;
		
		FrameLayout bg_frame = new FrameLayout(context);
		if(background != null && !background.isRecycled()) bg_frame.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), background));
		else bg_frame.setBackgroundColor(0xb2ffffff);
		fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth, ShareData.m_screenHeight);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.setContentView(bg_frame, fl);
		{
			LinearLayout main_frame = new LinearLayout(context);
			main_frame.setOrientation(LinearLayout.VERTICAL);
			main_frame.setBackgroundResource(R.drawable.share_bindpoco_dialog_bg);
			fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(598), LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			bg_frame.addView(main_frame, fl);
			{
				TextView poco_text = new TextView(context);
				poco_text.setTextColor(0xff333333);
				poco_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				poco_text.setText(getContext().getResources().getString(R.string.pocologin_login_title));
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(54);
				main_frame.addView(poco_text, ll);

				ImageView thumb = new ImageView(context);
				thumb.setScaleType(ScaleType.CENTER_INSIDE);
				thumb.setImageBitmap(icon);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(32);
				main_frame.addView(thumb, ll);

				TextView name = new TextView(context);
				name.setTextColor(0xff333333);
				name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				name.setText(nickname);
				TextPaint tp = name.getPaint();
				tp.setFakeBoldText(true);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(6);
				main_frame.addView(name, ll);

				TextView info = new TextView(context);
				info.setTextColor(0xff333333);
				info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				if(tips != null && tips.length() > 0) info.setText(tips);
				else info.setText(getContext().getResources().getString(R.string.pocologin_check_other_weibo));
				info.setLineSpacing(ShareData.PxToDpi_xhdpi(18), 1f);
				info.setGravity(Gravity.CENTER);
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(380), LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(20);
				main_frame.addView(info, ll);

				LinearLayout bindLayout = new LinearLayout(context);
				bindLayout.setOrientation(LinearLayout.HORIZONTAL);
				ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				ll.topMargin = ShareData.PxToDpi_xhdpi(46);
				main_frame.addView(bindLayout, ll);
				{
					other = new FrameLayout(context);
					other.setBackgroundResource(R.drawable.share_bindpoco_other);
					ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(230), ShareData.PxToDpi_xhdpi(78));
					ll.gravity = Gravity.LEFT | Gravity.TOP;
					bindLayout.addView(other, ll);
					other.setOnClickListener(mClickListener);
					{
						TextView text = new TextView(context);
						text.setTextColor(Color.BLACK);
						text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
						text.setText(getContext().getResources().getString(R.string.pocologin_other_weibo_login));
						TextPaint paint = text.getPaint();
						paint.setFakeBoldText(true);
						fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						fl.gravity = Gravity.CENTER;
						other.addView(text, fl);
					}

					determine = new FrameLayout(context);
					Bitmap determine_bg = ImageUtils.AddSkin(getContext(), BitmapFactory.decodeResource(getContext().getResources(), R.drawable.share_bindpoco_ensure));
					determine.setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), determine_bg));
					ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(230), ShareData.PxToDpi_xhdpi(78));
					ll.gravity = Gravity.LEFT | Gravity.TOP;
					ll.leftMargin = ShareData.PxToDpi_xhdpi(30);
					bindLayout.addView(determine, ll);
					determine.setOnClickListener(mClickListener);
					{
						TextView text = new TextView(context);
						text.setTextColor(Color.WHITE);
						text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
						text.setText(getContext().getResources().getString(R.string.pocologin_ensure));
						TextPaint paint = text.getPaint();
						paint.setFakeBoldText(true);
						fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
						fl.gravity = Gravity.CENTER;
						determine.addView(text, fl);
					}
				}

				FrameLayout exitFrame = new FrameLayout(context);
				ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.bottomMargin = ShareData.PxToDpi_xhdpi(20);
				main_frame.addView(exitFrame, ll);
				{
					exit = new TextView(context);
					exit.setTextColor(0xff999999);
					exit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
					exit.setText(getContext().getResources().getString(R.string.pocologin_exit));
					fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.CENTER;
					exitFrame.addView(exit, fl);
					exit.setOnClickListener(mClickListener);
				}
			}
		}
	}
	
	private View.OnClickListener mClickListener = new View.OnClickListener()
	{		
		@Override
		public void onClick(View v) 
		{
			if(v == exit)
			{
				dismiss();
			}
			else if(v == determine)
			{
				if(listener != null)
				{
					listener.onClick(ID_DETERMINE);
				}
				dismiss();
			}
			else if(v == other)
			{
				if(listener != null)
				{
					listener.onClick(ID_OTHER);
				}
				dismiss();
			}
		}
	};
	
	@Override
	public void dismiss() 
	{
		super.dismiss();
		icon = null;
		determine.setOnTouchListener(null);
		other.setOnTouchListener(null);
		exit.setOnTouchListener(null);
		listener = null;
		System.gc();
	}
	
	private Bitmap makeBmp(Bitmap bmp)
	{
		int w = bmp.getWidth();
		int h = bmp.getHeight();
		float scale = (float)ShareData.PxToDpi_xhdpi(120) / w;
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap icon = Bitmap.createBitmap((int)(w * scale), (int)(h * scale), Config.ARGB_8888);
		Canvas canvas = new Canvas(icon);
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		canvas.drawBitmap(bmp, 0, 0, null);
		bmp.recycle();
		return icon;
	}
}