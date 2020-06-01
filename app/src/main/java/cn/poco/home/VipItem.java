package cn.poco.home;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VipItem extends LinearLayout
{
	protected FrameLayout m_imgFr;
	public ImageView m_img;
	protected ImageView m_flag;
	protected TextView m_name;

	public VipItem(Context context)
	{
		super(context);

		Init();
	}

	protected void Init()
	{
		this.setOrientation(LinearLayout.VERTICAL);
		this.setGravity(Gravity.CENTER);

		LayoutParams ll;
		FrameLayout.LayoutParams fl;

		m_imgFr = new FrameLayout(getContext());
		ll = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.addView(m_imgFr, ll);
		{
			m_img = new ImageView(getContext());
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			m_imgFr.addView(m_img, fl);
		}

		m_name = new TextView(getContext());
		m_name.setTextColor(0xFF32BEA0);
		m_name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		m_name.setGravity(Gravity.CENTER);
		ll = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		this.addView(m_name, ll);
	}

	public void SetImg(Drawable img)
	{
		m_img.setImageDrawable(img);
	}

	public void SetName(String name)
	{
		m_name.setText(name);
	}

	public void SetFlag(Drawable flag)
	{
		if(flag != null)
		{
			if(m_flag == null)
			{
				m_flag = new ImageView(getContext());
				FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				m_imgFr.addView(m_flag, fl);
			}
			m_flag.setImageDrawable(flag);
		}
		else
		{
			if(m_flag != null)
			{
				m_imgFr.removeView(m_flag);
				m_flag = null;
			}
		}
	}

	public void SetImgLst(OnClickListener l)
	{
		m_img.setOnClickListener(l);
	}
}
