package cn.poco.ad65;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.ArrayList;

import cn.poco.pocolibs.R;
import cn.poco.tianutils.AnimationView;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;

public class AnimationDialog2 extends FullScreenDlg
{
	protected AnimationDialog2.Callback m_cb;
	protected AnimationView2 m_view;

	public AnimationDialog2(Activity activity, AnimationDialog2.Callback cb)
	{
		super(activity, R.style.MyTheme_Dialog_Transparent_Fullscreen);

		m_cb = cb;
		this.setCancelable(false);
		ShareData.InitData(activity);
		m_view = new AnimationView2(activity);
		AddView(m_view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
	}

	public void SetData_xhdpi(ArrayList<AnimationDialog2.AnimFrameData> datas)
	{
		m_view.SetData_xhdpi(datas, new AnimationView.Callback()
		{
			@Override
			public void OnClick()
			{
				if(m_cb != null)
				{
					m_cb.OnClick();
				}
			}

			@Override
			public void OnAnimationEnd()
			{
				dismiss();
				if(m_cb != null)
				{
					m_cb.OnAnimationEnd();
				}
			}
		});
	}

	public void SetGravity(int gravity)
	{
		if(m_view != null)
		{
			m_view.SetGravity(gravity);
		}
	}

	public void SetLeftMargin(int value)
	{
		if(m_view != null)
		{
			ViewGroup.LayoutParams lp = m_view.getLayoutParams();
			if(lp instanceof FrameLayout.LayoutParams)
			{
				((FrameLayout.LayoutParams)lp).leftMargin = value;
				m_view.setLayoutParams(lp);
			}
		}
	}

	public void SetTopMargin(int value)
	{
		if(m_view != null)
		{
			ViewGroup.LayoutParams lp = m_view.getLayoutParams();
			if(lp instanceof FrameLayout.LayoutParams)
			{
				((FrameLayout.LayoutParams)lp).topMargin = value;
				m_view.setLayoutParams(lp);
			}
		}
	}

	public void SetRightMargin(int value)
	{
		if(m_view != null)
		{
			ViewGroup.LayoutParams lp = m_view.getLayoutParams();
			if(lp instanceof FrameLayout.LayoutParams)
			{
				((FrameLayout.LayoutParams)lp).rightMargin = value;
				m_view.setLayoutParams(lp);
			}
		}
	}

	public void SetbottomMargin(int value)
	{
		if(m_view != null)
		{
			ViewGroup.LayoutParams lp = m_view.getLayoutParams();
			if(lp instanceof FrameLayout.LayoutParams)
			{
				((FrameLayout.LayoutParams)lp).bottomMargin = value;
				m_view.setLayoutParams(lp);
			}
		}
	}

	@Override
	public void show()
	{
		super.show();

		Start();
	}

	protected void Start()
	{
		if(m_view != null)
		{
			m_view.Start();
		}
	}

	@Override
	public void dismiss()
	{
		ClearAll();

		super.dismiss();
	}

	protected void ClearAll()
	{
		if(m_view != null)
		{
			m_view.ClearAll();
		}
	}

	public static class AnimFrameData extends AnimationView.AnimFrameData
	{
		public AnimFrameData()
		{
			super();
		}

		public AnimFrameData(Object res, long duration, boolean isStop)
		{
			super(res, duration, isStop);
		}
	}

	public interface Callback extends AnimationView.Callback
	{
	}
}
