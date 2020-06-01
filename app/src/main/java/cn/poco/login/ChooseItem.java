package cn.poco.login;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class ChooseItem extends FrameLayout
{
	public TextView m_text;
	public ImageView m_okIcon;
	protected ImageView m_nextArrow;
	protected boolean m_showArrow;
	public ChooseItem(Context context)
	{
		super(context);

		LayoutParams fl;
		m_text = new TextView(getContext());
		m_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, UserInfoPage.TEXT_SIZE);
		m_text.setTextColor(0xff333333);
		fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
		fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
		m_text.setLayoutParams(fl);
		this.addView(m_text);

		m_okIcon = new ImageView(getContext());
		m_okIcon.setVisibility(View.GONE);
		m_okIcon.setImageResource(R.drawable.userinfo_area_edit_ok);
		fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		fl.rightMargin = ShareData.PxToDpi_xhdpi(28);
		m_okIcon.setLayoutParams(fl);
		this.addView(m_okIcon);
		ImageUtils.AddSkin(getContext(),m_okIcon);

		m_nextArrow = new ImageView(getContext());
		m_nextArrow.setImageResource(R.drawable.setting_arrow);
		fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		fl.rightMargin = ShareData.PxToDpi_xhdpi(14);
		m_nextArrow.setLayoutParams(fl);
		this.addView(m_nextArrow);

		ImageView line = new ImageView(getContext());
		line.setBackgroundColor(0xfff6f6f5);
		fl = new LayoutParams(LayoutParams.MATCH_PARENT, 2);
		fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
		fl.leftMargin = ShareData.PxToDpi_xhdpi(30);
		line.setLayoutParams(fl);
		this.addView(line);
		this.setBackgroundColor(Color.WHITE);
	}

	public void onChoose(boolean choose)
	{
		if(choose)
		{
			m_okIcon.setVisibility(View.VISIBLE);
			m_nextArrow.setVisibility(View.GONE);
		}
		else
		{
			if(m_showArrow)
			{
				m_nextArrow.setVisibility(View.VISIBLE);
			}
			else
			{
				m_nextArrow.setVisibility(View.GONE);
			}
			m_okIcon.setVisibility(View.GONE);
		}
	}

	public void showArrow(boolean show)
	{
		m_showArrow = show;
		if(m_showArrow)
		{
			m_nextArrow.setVisibility(View.VISIBLE);
		}
		else
		{
			m_nextArrow.setVisibility(View.GONE);
		}
	}

	public void setText(String text)
	{
		m_text.setText(text);
	}


}
