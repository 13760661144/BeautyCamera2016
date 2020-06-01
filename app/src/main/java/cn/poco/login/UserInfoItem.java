package cn.poco.login;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class UserInfoItem extends RelativeLayout
{
	protected TextView m_title;
	protected LinearLayout m_infoFr;
	protected TextView m_info;
	protected ImageView m_nextArrow;
	public UserInfoItem(Context context)
	{
		super(context);
		initUI();
	}

	protected void initUI()
	{
		this.setBackgroundColor(Color.WHITE);
		LayoutParams rl;
		m_title = new TextView(getContext());
		m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, UserInfoPage.TEXT_SIZE);
		m_title.setTextColor(0xff333333);
		rl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rl.leftMargin = ShareData.PxToDpi_xhdpi(28);
		m_title.setLayoutParams(rl);
		this.addView(m_title);

		m_infoFr = new LinearLayout(getContext());
		rl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rl.rightMargin = ShareData.PxToDpi_xhdpi(10);
		m_infoFr.setLayoutParams(rl);
		this.addView(m_infoFr);
		{
			m_info = new TextView(getContext());
			m_info.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			m_info.setTextColor(0xff999999);
			m_info.setGravity(Gravity.RIGHT);
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams
					(ShareData.PxToDpi_xhdpi(500), LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER_VERTICAL;
			m_info.setLayoutParams(ll);
			m_infoFr.addView(m_info);

			m_nextArrow = new ImageView(getContext());
			m_nextArrow.setImageResource(R.drawable.setting_arrow);
			ll = new LinearLayout.LayoutParams
					(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER_VERTICAL;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(8);
			m_nextArrow.setLayoutParams(ll);
			m_infoFr.addView(m_nextArrow);
		}

		ImageView line = new ImageView(getContext());
		line.setBackgroundColor(0xffedede9);
		rl = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rl.leftMargin = ShareData.PxToDpi_xhdpi(30);
		line.setLayoutParams(rl);
		this.addView(line);
	}

	public void setTitle(String title)
	{
		m_title.setText(title);
	}

	public void setInfo(String info)
	{
		m_info.setText(info);
	}

	public void showArrow(boolean show)
	{
		if(show)
		{
			m_nextArrow.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) m_info.getLayoutParams();
			ll.rightMargin = 0;
			m_info.setLayoutParams(ll);
		}
		else
		{
			m_nextArrow.setVisibility(View.GONE);
			LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) m_info.getLayoutParams();
			ll.rightMargin = ShareData.PxToDpi_xxhdpi(75) + ShareData.PxToDpi_xhdpi(8);
			m_info.setLayoutParams(ll);
		}
	}

}
