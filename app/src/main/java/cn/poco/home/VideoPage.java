//package cn.poco.home;
//
//import android.app.Activity;
//import android.content.ContentResolver;
//import android.content.Context;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.view.Gravity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.VideoView;
//
//import com.baidu.mobstat.StatService;
//
//import java.util.HashMap;
//
//import cn.poco.framework.BaseSite;
//import cn.poco.framework.IPage;
//import cn.poco.home.site.VideoPageSite;
//import cn.poco.tianutils.FullScreenDlg;
//import cn.poco.tianutils.ShareData;
//import my.beautyCamera.R;
//
//public class VideoPage extends IPage
//{
//	private static final String TAG = "视频预览";
//
//	protected VideoPageSite m_site;
//
//	private VideoView m_video;
//	private ImageView m_closeBtn;
//	private Runnable mCompleteListener;
//	private FullScreenDlg m_fullDlg;
//
//	private int m_pos;
//
//	public VideoPage(Context context)
//	{
//		super(context, null);
//
//		Init();
//	}
//
//	public VideoPage(Context context, BaseSite site)
//	{
//		super(context, site);
//
//		m_site = (VideoPageSite)site;
//
//		Init();
//	}
//
//	private void Init()
//	{
//		ShareData.InitData((Activity)getContext());
//		//CommonUtils.CancelViewGPU(this);
//		m_video = new VideoView(getContext())
//		{
//			@Override
//			protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//			{
//				setMeasuredDimension(ShareData.m_screenWidth, ShareData.m_screenHeight);
//			}
//		};
//		LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		fl.gravity = Gravity.CENTER;
//		m_video.setLayoutParams(fl);
//		this.addView(m_video);
//		m_video.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
//		{
//			@Override
//			public void onCompletion(MediaPlayer mp)
//			{
//				if(mCompleteListener != null)
//				{
//					mCompleteListener.run();
//					mCompleteListener = null;
//				}
//				if(m_site != null)
//				{
//					m_site.OnSkip();
//				}
//				if(m_fullDlg != null)
//				{
//					m_fullDlg.dismiss();
//					m_fullDlg = null;
//				}
//			}
//		});
//		Uri.Builder builder = new Uri.Builder();
//		builder.scheme(ContentResolver.SCHEME_ANDROID_RESOURCE);
//		builder.encodedAuthority(getContext().getPackageName());
//		builder.appendEncodedPath(R.raw.start_video + "");
//		//System.out.println(builder.build().toString());
//		m_video.setVideoURI(builder.build());
//		m_video.start();
//		m_video.setZOrderOnTop(true);
//
//		m_closeBtn = new ImageView(getContext());
//		m_closeBtn.setImageResource(R.drawable.homepage_video_skip_btn_out);
//		fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		fl.gravity = Gravity.TOP | Gravity.RIGHT;
//		m_closeBtn.setLayoutParams(fl);
////		this.addView(m_closeBtn);
//		m_closeBtn.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				if(mCompleteListener != null)
//				{
//					mCompleteListener.run();
//					mCompleteListener = null;
//				}
//				if(m_site != null)
//				{
//					m_site.OnSkip();
//				}
//				if(m_fullDlg != null)
//				{
//					m_fullDlg.dismiss();
//					m_fullDlg = null;
//				}
//			}
//		});
//		m_fullDlg = new FullScreenDlg((Activity) getContext(),R.style.MyTheme_Dialog_Transparent_Fullscreen_NO_ANIM);
//		m_fullDlg.m_fr.addView(m_closeBtn);
//		m_fullDlg.show();
//
//		StatService.onPageStart(getContext(), TAG);
//	}
//
//	public void setCompleteListener(Runnable listener)
//	{
//		mCompleteListener = listener;
//	}
//
//	@Override
//	public void onPause()
//	{
//		//System.out.println("onPause");
//		if(m_video != null)
//		{
//			m_pos = m_video.getCurrentPosition();
//			if(m_video.isPlaying())
//			{
//				m_video.pause();
//			}
//		}
//	}
//
//	@Override
//	public void onResume()
//	{
//		//System.out.println("onResume");
//		if(m_video != null)
//		{
//			m_video.seekTo(m_pos);
//			if(!m_video.isPlaying())
//			{
//				m_video.start();
//			}
//		}
//	}
//
//	@Override
//	public void SetData(HashMap<String, Object> params)
//	{
//	}
//
//	@Override
//	public void onBack()
//	{
//	}
//
//	@Override
//	public void onClose()
//	{
//		if(m_video != null)
//		{
//			//m_video.stopPlayback();
//			m_video = null;
//		}
//		if(m_fullDlg != null)
//		{
//			m_fullDlg.dismiss();
//			m_fullDlg = null;
//		}
//
//		StatService.onPageEnd(getContext(), TAG);
//	}
//
//}
