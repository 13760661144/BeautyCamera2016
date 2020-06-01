package cn.poco.about;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import cn.poco.about.site.AboutPageSite;
import cn.poco.advanced.ImageUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class AboutPage extends IPage
{
	private static final int TAG = R.string.关于我们;

	private AboutPageSite m_site;
	private int mLogoClickCount = 0;

	public AboutPage(Context context, BaseSite site)
	{
		super(context, site);
		m_site = (AboutPageSite)site;
		initUI();

		TongJiUtils.onPageStart(getContext(), TAG);
	}

	@Override
	public void onClose()
	{
		TongJiUtils.onPageEnd(getContext(), TAG);
		super.onClose();
	}

	@Override
	public void onResume()
	{
		TongJiUtils.onPageResume(getContext(), TAG);
		super.onResume();
	}

	@Override
	public void onPause()
	{
		TongJiUtils.onPagePause(getContext(), TAG);
		super.onPause();
	}

	@Override
	public void SetData(HashMap<String, Object> params)
	{
	}

	private void initUI()
	{
		TongJi2.AddCountByRes(getContext(), R.integer.关于);

		LayoutInflater m_inflater = LayoutInflater.from(getContext());
		FrameLayout about_frame = (FrameLayout)m_inflater.inflate(R.layout.about_ex, null);
		addView(about_frame);

		//界面调整
		/*ShareData.InitData(this);
		int screenW = ShareData.m_screenWidth;
		int screenH = ShareData.m_screenHeight;
		int m_padding = (int)(screenW * 0.1);
		int m_MarginTop_1 = (int)(screenH * 0.075);
		int m_MarginTop_2 = (int)(screenH * 0.035);
		int m_MarginBottom_1 = (int)(screenH * 0.13);

		RelativeLayout about_bar_center = (RelativeLayout)this.findViewById(R.id.about_bar_center);
		about_bar_center.setPadding(m_padding, 0, m_padding, 0);

		RelativeLayout about_t_1 = (RelativeLayout)this.findViewById(R.id.about_t_1);
		RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams)about_t_1.getLayoutParams();
		rl.setMargins(0, m_MarginTop_1, 0, 0);
		about_t_1.setLayoutParams(rl);

		FrameLayout about_t_2 = (FrameLayout)this.findViewById(R.id.about_t_2);
		RelativeLayout.LayoutParams rl2 = (RelativeLayout.LayoutParams)about_t_2.getLayoutParams();
		rl2.setMargins(0, m_MarginTop_2, 0, 0);
		about_t_2.setLayoutParams(rl2);

		RelativeLayout about_items_con = (RelativeLayout)this.findViewById(R.id.about_items_icon);
		RelativeLayout.LayoutParams rl3 = (RelativeLayout.LayoutParams)about_items_con.getLayoutParams();
		rl3.setMargins(0, 0, 0, m_MarginBottom_1);
		about_items_con.setLayoutParams(rl3);*/

		ImageView logo = (ImageView)this.findViewById(R.id.about_app_icon);
		ImageUtils.AddSkin(getContext(), logo);
		logo.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				mLogoClickCount++;
				if(mLogoClickCount >= 5)
				{
					mLogoClickCount = 0;

					final EditText editText = new EditText(getContext());
					FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(240), LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.CENTER;
					editText.setLayoutParams(fl);

					AlertDialog dlg = new AlertDialog.Builder(getContext()).create();
					dlg.setTitle("输入");
					dlg.setView(editText);
					dlg.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							String keyword = editText.getText().toString();
							if(keyword.equals("52012345"))
							{
								SysConfig.SetDebug(!SysConfig.IsDebug());
								EventCenter.sendEvent(EventID.COMMUNITY_UPDATE_ENVIRONMENT);
								if(SysConfig.IsDebug())
									Toast.makeText(getContext(), "已启动调试模式!", Toast.LENGTH_LONG).show();
								else Toast.makeText(getContext(), "已关闭调试模式!", Toast.LENGTH_LONG).show();
							}
						}
					});
					dlg.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which){}
					});
					dlg.show();
				}
			}
		});

		ImageView cancel = (ImageView)findViewById(R.id.cancel_button);
		ImageUtils.AddSkin(getContext(), cancel);
		cancel.setOnTouchListener(new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				onBack();
			}

			@Override
			public void onTouch(View v){}

			@Override
			public void onRelease(View v){}
		});

		ShareData.InitData(getContext());
//		RelativeLayout mainLayout = (RelativeLayout)this.findViewById(R.id.content_view);
//		Bitmap tempBitmap = Utils.DecodeImage(getContext(), R.drawable.about_background, 0, -1, -1, -1);
//		mainLayout.setBackgroundDrawable(new BitmapDrawable(MakeBmpV2.CreateFixBitmapV2(tempBitmap, 0, 0, MakeBmpV2.POS_BOTTOM, ShareData.m_screenWidth, ShareData.m_screenHeight, Bitmap.Config.ARGB_8888)));
//		tempBitmap.recycle();
//		tempBitmap = null;

		TextView ver = (TextView)this.findViewById(R.id.ver);
		int color = ImageUtils.GetSkinColor();
		if(color != 0) ver.setTextColor(color);
		else ver.setTextColor(0xffe75988);
		ver.setText("v" + SysConfig.GetAppVer(getContext()));
		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		fl.topMargin = ShareData.PxToDpi_xhdpi(334);
		fl.leftMargin = ShareData.m_screenWidth / 2 + ShareData.PxToDpi_xhdpi(193);
		ver.setLayoutParams(fl);
	}

	@Override
	public void onBack()
	{
		if(m_site != null) m_site.onBack();
	}
}
