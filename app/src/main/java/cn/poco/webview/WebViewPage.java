package cn.poco.webview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adnonstop.admasterlibs.AdUtils;

import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;

import cn.poco.advanced.ImageUtils;
import cn.poco.banner.BannerCore3;
import cn.poco.camera.RotationImg2;
import cn.poco.cloudalbumlibs.utils.NetWorkUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.SiteID;
import cn.poco.home.site.HomePageSite;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.CommonUtils;
import cn.poco.tianutils.MyWebView;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyNetCore;
import cn.poco.utils.OnAnimationClickListener;
import cn.poco.webview.site.WebViewPageSite;
import my.beautyCamera.R;

public class WebViewPage extends MyWebView
{
	//public static final int REQUEST_CODE_SELECT_PIC = 0x9001;
	//public static final int REQUEST_CODE_SELECT_CAMERA = 0x9002;

	protected WebViewPageSite m_site;

	protected String m_url;

	protected ValueCallback<Uri> m_filePathCallback1;
	protected ValueCallback<Uri[]> m_filePathCallback2;
	//protected String m_photoPath;

	protected ImageView m_backBtn;
	//protected ImageView m_closeBtn;
	protected TextView m_title;
	protected ProgressBar m_progressBar;

	private HomePageSite.ShareBlogData mShareData;

    private LinearLayout mErrorTipLayout;

	public WebViewPage(Context context, BaseSite site)
	{
		super(context, site);

		m_site = (WebViewPageSite)site;
		mShareData = new HomePageSite.ShareBlogData(context);
		m_site.m_cmdProc.SetShareData(mShareData);
	}


	@Override
	protected void Init()
	{
		ShareData.InitData((Activity)getContext());

		OnAnimationClickListener btnLst = new OnAnimationClickListener()
		{
			@Override
			public void onAnimationClick(View v)
			{
				if(v == m_backBtn)
				{
					onBack();
				}
//				else if(v == m_closeBtn)
//				{
//					m_site.OnClose();
//				}
			}

			@Override
			public void onTouch(View v)
			{

			}

			@Override
			public void onRelease(View v)
			{

			}
		};

		FrameLayout.LayoutParams fl;
		this.setBackgroundColor(0xFFFFFFFF);

		int topBarH;

		topBarH = ShareData.PxToDpi_xhdpi(90);
		FrameLayout topBar = new FrameLayout(getContext());
		topBar.setBackgroundColor(0xf4ffffff);
		//topBar.setBackgroundResource(R.drawable.frame_topbar_bk);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, topBarH);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		this.addView(topBar, fl);
		{
			m_backBtn = new ImageView(getContext());
			m_backBtn.setImageResource(R.drawable.framework_back_btn);
			ImageUtils.AddSkin(getContext(), m_backBtn);
			m_backBtn.setOnTouchListener(btnLst);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
			topBar.addView(m_backBtn, fl);

//			m_closeBtn = new ImageView(getContext());
//			m_closeBtn.setImageResource(R.drawable.framework_close_btn);
//			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
//			fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
//			topBar.addView(m_closeBtn, fl);
//			m_closeBtn.setOnTouchListener(btnLst);

			m_title = new TextView(getContext());
			m_title.setTextColor(0xff333333);
			m_title.setSingleLine();
			m_title.setEllipsize(TextUtils.TruncateAt.END);
			m_title.setGravity(Gravity.CENTER);
			m_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
			fl = new FrameLayout.LayoutParams(ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(300), FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.CENTER;
			topBar.addView(m_title, fl);
		}

		m_webView = new WebView(getContext());
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		fl.topMargin = topBarH;
		this.addView(m_webView, fl);

		int pbarH = ShareData.PxToDpi_xhdpi(10);
		m_progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
		m_progressBar.getProgressDrawable().setColorFilter(ImageUtils.GetSkinColor(), PorterDuff.Mode.SRC_IN);
		m_progressBar.setMax(100);
		m_progressBar.setMinimumHeight(pbarH);
		fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, pbarH);
		fl.gravity = Gravity.LEFT | Gravity.TOP;
		fl.topMargin = topBarH;
		this.addView(m_progressBar, fl);
		m_progressBar.setVisibility(View.GONE);

		InitWebViewSetting(m_webView.getSettings());
		m_webView.getSettings().setUserAgentString(m_webView.getSettings().getUserAgentString() + " beautyCamera/" + SysConfig.GetAppVer(getContext()));

		m_webView.setWebViewClient(new MyWebView.MyWebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url)
			{
				if(url.startsWith("userOtherBrowser://url=") || url.startsWith("userotherbrowser://url="))
				{
					url = url.toLowerCase(Locale.getDefault());
					String _url = url.substring("userotherbrowser://url=".length());
					CommonUtils.OpenBrowser(getContext(), _url);
					return true;
				}
				String temp = url.toLowerCase(Locale.ENGLISH);
				if(!temp.startsWith("http") && !temp.startsWith("ftp"))
				{
					BannerCore3.ExecuteCommand(getContext(), url, m_site.m_cmdProc);
					return true;
				}

				String newUrl = AdUtils.AdDecodeUrl(getContext(), url);
				if(newUrl == null || url == null || newUrl.equals(url))
				{
					return super.shouldOverrideUrlLoading(view, url);
				}
				else
				{
					view.loadUrl(newUrl);
					return true;
				}
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon)
			{
				if(url.startsWith("BeautyCamera://") || url.startsWith("beautycamera://"))
				{
					BannerCore3.ExecuteCommand(getContext(), url, m_site.m_cmdProc);
				}
				else if(url.startsWith("userOtherBrowser://url=") || url.startsWith("userotherbrowser://url="))
				{
					url = url.toLowerCase(Locale.getDefault());
					String _url = url.substring("userotherbrowser://url=".length());
					CommonUtils.OpenBrowser(getContext(), _url);
				}
				else
				{
					super.onPageStarted(view, url, favicon);
				}
			}
		});
		m_webView.setWebChromeClient(new MyWebView.MyWebChromeClient()
		{
			//5.0+
			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams)
			{
				ShowFileChooser(null, filePathCallback);
				return true;
			}

			//4.1.1
			public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture)
			{
				ShowFileChooser(filePathCallback, null);
			}

			//3.0+
			public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType)
			{
				ShowFileChooser(filePathCallback, null);
			}

			//3.0-
			public void openFileChooser(ValueCallback<Uri> filePathCallback)
			{
				ShowFileChooser(filePathCallback, null);
			}

			@Override
			public void onProgressChanged(WebView view, int newProgress)
			{
				if(newProgress < 100)
				{
					m_progressBar.setVisibility(View.VISIBLE);
					m_progressBar.setProgress(newProgress);
				}
				else
				{
					m_progressBar.setVisibility(View.GONE);
				}

				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title)
			{
				if(title != null && !title.contains(".com"))
				{
					m_title.setText(title);
				}

				super.onReceivedTitle(view, title);
			}
		});
		m_webView.setDownloadListener(new DownloadListener()
		{
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength)
			{
				CommonUtils.OpenBrowser(getContext(), url);
			}
		});

        mErrorTipLayout = new LinearLayout(getContext());
        mErrorTipLayout.setOrientation(LinearLayout.VERTICAL);
        mErrorTipLayout.setVisibility(View.GONE);
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        addView(mErrorTipLayout, fl);
        {
            ImageView icon = new ImageView(getContext());
            icon.setImageResource(R.drawable.campaigncenter_network_warn_big);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            mErrorTipLayout.addView(icon, lp);

            TextView txt = new TextView(getContext());
            txt.setText(getContext().getString(R.string.poor_network));
            txt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            txt.setTextColor(0xffcccccc);
            lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mErrorTipLayout.addView(txt, lp);
        }
	}

	protected void ShowFileChooser(final ValueCallback<Uri> cb1, final ValueCallback<Uri[]> cb2)
	{
		m_filePathCallback1 = cb1;
		m_filePathCallback2 = cb2;
		CharSequence[] items = {getResources().getString(R.string.webviewpage_album), getResources().getString(R.string.webviewpage_camera)};
		AlertDialog dlg = new AlertDialog.Builder(getContext()).setTitle(getResources().getString(R.string.webviewpage_select_image_source)).setItems(items, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				switch(which)
				{
					case 0:
					{
						/*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						intent.setType("image*//*");
						((Activity)getContext()).startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.webviewpage_select_image)), REQUEST_CODE_SELECT_PIC);*/
						m_site.OnSelPhoto(getContext());
						break;
					}

					case 1:
					{
						/*Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						m_photoPath = Utils.MakeSavePhotoPath(getContext(), 1);
						if(m_photoPath != null)
						{
							intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(m_photoPath)));
						}
						((Activity)getContext()).startActivityForResult(intent, REQUEST_CODE_SELECT_CAMERA);*/
						m_site.OnCamera(getContext());
						break;
					}

					default:
						break;
				}
			}
		}).create();
		dlg.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				if(m_filePathCallback1 != null)
				{
					m_filePathCallback1.onReceiveValue(null);
					m_filePathCallback1 = null;
				}
				if(m_filePathCallback2 != null)
				{
					m_filePathCallback2.onReceiveValue(null);
					m_filePathCallback2 = null;
				}
				//m_photoPath = null;
			}
		});
		dlg.show();
	}

	@Override
	public void loadUrl(String url)
	{
		m_url = MyNetCore.GetPocoUrl(getContext(), url);
		m_url = AdUtils.AdDecodeUrl(getContext(), m_url);
		m_url = AddMyParams(getContext(), m_url);
		//System.out.println("WebViewPage : " + m_url);

		super.loadUrl(m_url);
	}

	public static String AddMyParams(Context context, String url)
	{
		String out = url;

		if(out != null && out.contains("is_ime=1"))
		{
			String imei = CommonUtils.GetIMEI(context);
			if(imei != null && imei.length() > 0)
			{
				if(out.contains("?"))
				{
					out += "&";
				}
				else
				{
					out += "?";
				}
				out += "en_str=" + new String(MyEncode(imei, "beautycamera"));
				out += "&ime_str=" + imei;
			}
		}

		return out;
	}

	public static byte[] MyEncode(String key, String data)
	{
		byte[] out = null;

		byte[] keyArr = MD5(key).getBytes();
		byte[] dataArr = data.getBytes();

		int len = dataArr.length;
		int l = keyArr.length;
		int x = 0;
		for(int i = 0; i < len; i++)
		{
			if(x == l)
			{
				x = 0;
			}
			dataArr[i] += keyArr[x];
			x++;
		}

		out = Base64.encode(dataArr, Base64.DEFAULT | Base64.NO_WRAP);

		return out;
	}

	public static byte[] MyDecode(String key, String data)
	{
		byte[] out = null;

		byte[] keyArr = MD5(key).getBytes();
		byte[] dataArr = Base64.decode(data.getBytes(), Base64.DEFAULT | Base64.NO_WRAP);

		int len = dataArr.length;
		int l = keyArr.length;
		int x = 0;
		for(int i = 0; i < len; i++)
		{
			if(x == l)
			{
				x = 0;
			}
			dataArr[i] -= keyArr[x];
			x++;
		}

		out = dataArr;

		return out;
	}

	public static String MD5(String data)
	{
		String out = null;

		try
		{
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(data.getBytes("UTF-8"));
			byte[] encryption = md5.digest();

			StringBuffer buf = new StringBuffer();
			String temp;
			for(int i = 0; i < encryption.length; i++)
			{
				temp = Integer.toHexString(0xff & encryption[i]);
				if(temp.length() == 1)
				{
					buf.append("0").append(temp);
				}
				else
				{
					buf.append(temp);
				}
			}

			out = buf.toString();
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}

		return out;
	}

	public void reloadUrl()
	{
		if(m_url != null)
		{
			loadUrl(m_url);
		}
	}

	@Override
	public void onBack()
	{
		if(m_webView != null && m_webView.getVisibility() == View.GONE)
		{
			if(m_webChromeClient != null)
			{
				m_webChromeClient.onHideCustomView();
				return;
			}
		}

		if(m_webView != null)
		{
			if(m_webView.canGoBack())
			{
				m_webView.goBack();
				return;
			}
		}

		m_site.OnBack(getContext());
	}

	@Override
	public void onPageResult(int siteID, HashMap<String, Object> params)
	{
		switch(siteID)
		{
			case SiteID.ALBUM:
			case SiteID.CAMERA:
			{
				if(params != null)
				{
					RotationImg2[] imgs = (RotationImg2[])params.get("imgs");
					if(imgs != null && imgs.length > 0 && imgs[0].m_orgPath != null)
					{
						Uri uri = Uri.fromFile(new File(imgs[0].m_orgPath));
						if(uri != null)
						{
							if(m_filePathCallback2 != null)
							{
								m_filePathCallback2.onReceiveValue(new Uri[]{uri});
								m_filePathCallback2 = null;
							}
							else if(m_filePathCallback1 != null)
							{
								m_filePathCallback1.onReceiveValue(uri);
								m_filePathCallback1 = null;
							}
						}
					}
				}

				if(m_filePathCallback1 != null)
				{
					m_filePathCallback1.onReceiveValue(null);
					m_filePathCallback1 = null;
				}
				if(m_filePathCallback2 != null)
				{
					m_filePathCallback2.onReceiveValue(null);
					m_filePathCallback2 = null;
				}
				break;
			}
		}
		super.onPageResult(siteID, params);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data)
	{
		/*if(resultCode == Activity.RESULT_OK)
		{
			try
			{
				switch(requestCode)
				{
					case REQUEST_CODE_SELECT_PIC:
					{
						Uri uri = data.getData();
						if(uri != null)
						{
							if(m_filePathCallback2 != null)
							{
								m_filePathCallback2.onReceiveValue(new Uri[]{uri});
								m_filePathCallback2 = null;
							}
							else if(m_filePathCallback1 != null)
							{
								m_filePathCallback1.onReceiveValue(uri);
								m_filePathCallback1 = null;
							}
						}

						if(m_filePathCallback1 != null)
						{
							m_filePathCallback1.onReceiveValue(null);
							m_filePathCallback1 = null;
						}
						if(m_filePathCallback2 != null)
						{
							m_filePathCallback2.onReceiveValue(null);
							m_filePathCallback2 = null;
						}
						return true;
					}

					case REQUEST_CODE_SELECT_CAMERA:
					{
						String path = null;
						if(m_photoPath != null)
						{
							path = m_photoPath;
						}
						else
						{
							Bundle bundle = data.getExtras();
							if(bundle != null)
							{
								Bitmap bitmap = (Bitmap)bundle.get("data");
								if(bitmap != null)
								{
									path = Utils.SaveImg(getContext(), bitmap, null, 90, false);
								}
							}
						}
						if(path != null)
						{
							Uri uri = Uri.fromFile(new File(path));
							if(uri != null)
							{
								if(m_filePathCallback2 != null)
								{
									m_filePathCallback2.onReceiveValue(new Uri[]{uri});
									m_filePathCallback2 = null;
								}
								else if(m_filePathCallback1 != null)
								{
									m_filePathCallback1.onReceiveValue(uri);
									m_filePathCallback1 = null;
								}
							}
						}

						if(m_filePathCallback1 != null)
						{
							m_filePathCallback1.onReceiveValue(null);
							m_filePathCallback1 = null;
						}
						if(m_filePathCallback2 != null)
						{
							m_filePathCallback2.onReceiveValue(null);
							m_filePathCallback2 = null;
						}
						m_photoPath = null;
						return true;
					}

					default:
						break;
				}
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			switch(requestCode)
			{
				case REQUEST_CODE_SELECT_PIC:
				case REQUEST_CODE_SELECT_CAMERA:
				{
					if(m_filePathCallback1 != null)
					{
						m_filePathCallback1.onReceiveValue(null);
						m_filePathCallback1 = null;
					}
					if(m_filePathCallback2 != null)
					{
						m_filePathCallback2.onReceiveValue(null);
						m_filePathCallback2 = null;
					}
					m_photoPath = null;
					return true;
				}
				default:
					break;
			}
		}*/

		mShareData.onActivityResult(requestCode, resultCode, data);

		return super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * url:String,打开的URL
	 */
	@Override
	public void SetData(HashMap<String, Object> params)
	{
        if (!NetWorkUtils.isNetworkConnected(getContext())) {
            if (mErrorTipLayout != null) {
                mErrorTipLayout.setVisibility(View.VISIBLE);
            }
            return;
        }
		if(params != null)
		{
			Object obj = params.get("url");
			if(obj instanceof String)
			{
				loadUrl((String)obj);
			}
		}
	}

	@Override
	public void onClose()
	{
		mShareData.ClearAll();
		super.onClose();
	}
}
