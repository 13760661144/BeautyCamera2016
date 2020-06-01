package cn.poco.business;

import android.content.Context;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.data.AbsAdRes;
import com.adnonstop.admasterlibs.data.AbsChannelAdRes;

import java.util.ArrayList;
import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.business.site.DownloadBusinessPageSite;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.framework.MyFramework2App;
import cn.poco.home.site.HomePageSite;
import cn.poco.resource.AbsDownloadMgr;
import cn.poco.resource.BaseRes;
import cn.poco.resource.DecorateRes;
import cn.poco.resource.DecorateResMgr2;
import cn.poco.resource.DownloadMgr;
import cn.poco.resource.FrameRes;
import cn.poco.resource.FrameResMgr2;
import cn.poco.resource.IDownload;
import cn.poco.resource.ResourceMgr;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

/**
 * 通用通道下载商业素材用
 */
public class DownloadBusinessPage extends IPage
{
	protected DownloadBusinessPageSite m_site;

	protected ImageView mBtnCancel;
	protected TextView mTitle;
	protected AbsAdRes m_res;
	protected WaitAnimDialog.WaitAnimView m_progressBar;
	protected MyDownloadCallback m_cb;

	public DownloadBusinessPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (DownloadBusinessPageSite)site;

		Init();
	}

	protected void Init()
	{
		this.setBackgroundColor(0xFFEDEDE9);
		FrameLayout.LayoutParams fl;

		FrameLayout topBar = new FrameLayout(getContext());
		/*BitmapDrawable bmpDraw = (BitmapDrawable)getResources().getDrawable(R.drawable.business_top_bk);
		if(bmpDraw != null)
		{
			bmpDraw.setTileModeX(Shader.TileMode.REPEAT);
		}
		topBar.setBackgroundDrawable(bmpDraw);*/
		topBar.setBackgroundColor(0xf4ffffff);
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
		this.addView(topBar, fl);
		{
			mBtnCancel = new ImageView(getContext());
			mBtnCancel.setImageResource(R.drawable.framework_back_btn);
			ImageUtils.AddSkin(getContext(), mBtnCancel);
			mBtnCancel.setOnClickListener(m_btnLst);
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			fl.leftMargin = ShareData.PxToDpi_xhdpi(5);
			topBar.addView(mBtnCancel, fl);

			mTitle = new TextView(getContext());
			mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			mTitle.setTextColor(0xff6e6e6e);
			/*fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			topBar.addView(mTitle, fl);*/
		}
		FrameLayout fr2 = new FrameLayout(getContext());
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		fl.topMargin = ShareData.PxToDpi_xhdpi(90);
		this.addView(fr2, fl);
		{
			m_progressBar = new WaitAnimDialog.WaitAnimView(getContext());
			fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			fr2.addView(m_progressBar, fl);
		}
	}

	protected View.OnClickListener m_btnLst = new View.OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == mBtnCancel)
			{
				m_site.OnBack(getContext());
			}
		}
	};

	/**
	 * @param params {@link HomePageSite#BUSINESS_KEY} : BusinessRes
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
		m_res = (AbsAdRes)params.get(HomePageSite.BUSINESS_KEY);

		if(m_res != null)
		{
			if(m_res.m_name != null)
			{
				mTitle.setText(m_res.m_name);
			}

			if(!DownloadRes(m_res))
			{
				//资源部完整,等待线程重新获取
				final Handler uiHandler = new Handler();
				new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						ResourceMgr.WaitBusinessRes();

						uiHandler.post(new Runnable()
						{
							@Override
							public void run()
							{
								if(!DownloadRes(m_res))
								{
									Toast.makeText(getContext(), R.string.business_download_res_error, Toast.LENGTH_LONG).show();
								}
							}
						});
					}
				}).start();
			}
		}
	}

	/**
	 * @param res
	 * @return 资源是否完整
	 */
	protected boolean DownloadRes(AbsAdRes res)
	{
		//下载商业资源
		boolean resOk = true;
		ArrayList<BaseRes> downloadArr = new ArrayList<>();
		if(res.m_type == BaseRes.TYPE_NETWORK_URL)
		{
			downloadArr.add(res);
		}

		int[] ids;
		if(res instanceof AbsChannelAdRes)
		{
			AbsChannelAdRes.FramePageData frame = ((AbsChannelAdRes)res).GetFramePageData();
			if(frame != null)
			{
				ids = frame.mIds;
			}
			else
			{
				ids = null;
			}
			if(resOk && ids != null && ids.length > 0)
			{
				int len;
				ArrayList<FrameRes> arr = FrameResMgr2.getInstance().GetResArr2(ids, false);
				if((len = arr.size()) == ids.length)
				{
					for(int i = 0; i < len; i++)
					{
						if(arr.get(i).m_type == BaseRes.TYPE_NETWORK_URL)
						{
							downloadArr.add(arr.get(i));
						}
					}
				}
				else
				{
					resOk = false;
				}
			}

			AbsChannelAdRes.DecoratePageData decorate = ((AbsChannelAdRes)res).GetDecoratePageData();
			if(decorate != null)
			{
				ids = decorate.mIds;
			}
			else
			{
				ids = null;
			}
			if(resOk && ids != null && ids.length > 0)
			{
				int len;
				ArrayList<DecorateRes> arr = DecorateResMgr2.getInstance().GetResArr2(ids, false);
				if((len = arr.size()) == ids.length)
				{
					for(int i = 0; i < len; i++)
					{
						if(arr.get(i).m_type == BaseRes.TYPE_NETWORK_URL)
						{
							downloadArr.add(arr.get(i));
						}
					}
				}
				else
				{
					resOk = false;
				}
			}
		}

		if(resOk)
		{
			if(downloadArr.size() > 0)
			{
				IDownload[] arr = new IDownload[downloadArr.size()];
				downloadArr.toArray(arr);
				m_cb = new MyDownloadCallback(m_site);
				DownloadMgr.getInstance().DownloadRes(arr, false, m_cb);
			}
			else
			{
				m_site.OnNext(getContext());
			}
		}

		return resOk;
	}

	protected static class MyDownloadCallback implements AbsDownloadMgr.Callback2
	{
		public DownloadBusinessPageSite m_site;

		public MyDownloadCallback(DownloadBusinessPageSite site)
		{
			m_site = site;
		}

		@Override
		public void OnGroupComplete(int downloadId, IDownload[] resArr)
		{
			if(m_site != null)
			{
				if(MyFramework2App.getInstance().getActivity() != null)
				{
					m_site.OnNext(MyFramework2App.getInstance().getActivity());
				}
			}
		}

		@Override
		public void OnGroupFail(int downloadId, IDownload[] resArr)
		{
			if(MyFramework2App.getInstance().getActivity() != null)
			{
				Toast.makeText(MyFramework2App.getInstance().getActivity(), "下载资源失败！", Toast.LENGTH_LONG).show();
			}
		}

		@Override
		public void OnGroupProgress(int downloadId, IDownload[] resArr, int progress)
		{

		}

		@Override
		public void OnProgress(int downloadId, IDownload res, int progress)
		{

		}

		@Override
		public void OnComplete(int downloadId, IDownload res)
		{

		}

		@Override
		public void OnFail(int downloadId, IDownload res)
		{

		}

		public void ClearAll()
		{
			m_site = null;
		}
	}

	@Override
	public void onClose()
	{
		if(m_cb != null)
		{
			m_cb.ClearAll();
			m_cb = null;
		}

		super.onClose();
	}

	@Override
	public void onBack()
	{
		m_site.OnBack(getContext());
	}
}
