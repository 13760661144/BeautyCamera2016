package cn.poco.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cn.poco.advanced.ImageUtils;
import cn.poco.statistics.TongJi2;
import cn.poco.tianutils.MakeBmp;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class SendBlogPage extends FrameLayout
{
	public static final int ID_BACK = 0;
	public static final int ID_SEND = 1;
	
	//默认话题
	private static final String TOPIC1 = "美人相机";
	private static final String TOPIC2 = "我的自拍";
	private static final String TOPIC3 = "姐妹合照";
	private static final String TOPIC4 = "嘟嘴";
	private static final String TOPIC5 = "眼镜控";
	private static final String TOPIC6 = "狗狗";
	private static final String TOPIC7 = "猫咪";
	private static final String TOPIC8 = "职业装";
	
	private ImageView mBack;
	private TextView mSend;
	private EditText mTxInput;
	private LinearLayout mTopicFrame;
	private TopicButton mMoreJing;
	private TopicButton mAddJing;
	
	private final String mDefaultText = getContext().getResources().getString(R.string.share_sendblogpage_default_text);
	private SharePage.DialogListener listener;
	private boolean topic_status = false;
	
	public SendBlogPage(Context context)
	{
		super(context);
	}

	public void setDialogListener(SharePage.DialogListener listener)
	{
		this.listener = listener;
	}
	
	@SuppressWarnings("deprecation")
	public void init(Bitmap bg, Bitmap thumb, int blog_type, final String activities)
	{
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;
		
		LinearLayout mainFrame = new LinearLayout(getContext());
		mainFrame.setOrientation(LinearLayout.VERTICAL);
		if(bg != null && !bg.isRecycled()) 
		{
			BitmapDrawable bd = new BitmapDrawable(getResources(), bg);
			mainFrame.setBackgroundDrawable(bd);
		}
		else
		{
			mainFrame.setBackgroundColor(Color.WHITE);
		}
		fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		this.addView(mainFrame, fl);
		{
			FrameLayout top_bar = new FrameLayout(getContext());
			top_bar.setBackgroundColor(0xf6ffffff);
			ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.weight = 0;
			mainFrame.addView(top_bar, ll);
			{	
				String theme = null;
				TextView text = new TextView(getContext());
				switch(blog_type)
				{
				case SharePage.POCO:
					theme = getContext().getResources().getString(R.string.share_sendblogpage_title_poco);
					break;
					
				case SharePage.SINA:
					theme = getContext().getResources().getString(R.string.share_sendblogpage_title_sina);
					break;
					
				case SharePage.QQ:
					theme = getContext().getResources().getString(R.string.share_sendblogpage_title_qq);
					break;
					
				case SharePage.QZONE:
					theme = getContext().getResources().getString(R.string.share_sendblogpage_title_qzone);
					break;
				}
				text.setText(theme);
				text.setTextColor(0xe6000000);
				text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
				fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.CENTER;
				top_bar.addView(text, fl);
				
				mBack = new ImageView(getContext());
				mBack.setScaleType(ScaleType.CENTER_INSIDE);
				mBack.setImageResource(R.drawable.framework_back_btn);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
				top_bar.addView(mBack, fl);
				ImageUtils.AddSkin(getContext(), mBack);
				mBack.setOnTouchListener(new OnAnimationClickListener()
				{
					@Override
					public void onAnimationClick(View v)
					{
						if(listener != null) listener.onClick(ID_BACK);
					}

					@Override
					public void onTouch(View v){}

					@Override
					public void onRelease(View v){}
				});
							
				mSend = new TextView(getContext());
				mSend.setText(getContext().getResources().getString(R.string.share_sendblogpage_send));
				int color = ImageUtils.GetSkinColor();
				if(color != 0) mSend.setTextColor(color);
				else mSend.setTextColor(0xffe85b8a);
				mSend.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
				fl.rightMargin = ShareData.PxToDpi_xhdpi(27);
				top_bar.addView(mSend, fl);
				mSend.setOnClickListener(mClickListener);
				
				ImageView line = new ImageView(getContext());
				line.setBackgroundColor(0xffeeeeee);
				fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1);
				fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
				top_bar.addView(line, fl);
			}		
					
			LinearLayout top_frame = new LinearLayout(getContext());
			top_frame.setOrientation(LinearLayout.HORIZONTAL);
			top_frame.setBackgroundColor(Color.WHITE);
			ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(450));
			ll.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			ll.weight = 0;
			mainFrame.addView(top_frame, ll);
			{	
				FrameLayout text_frame = new FrameLayout(getContext());
				ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.weight = 1;
				top_frame.addView(text_frame, ll);
				{
//					LayoutInflater m_inflater = LayoutInflater.from(getContext());
//					mTxInput = (EditText) m_inflater.inflate(R.layout.sendblogpage_edittext, null);
					mTxInput = new EditText(getContext());
					if(activities != null && activities.length() > 0) mTxInput.setText(activities);
					else mTxInput.setHint(mDefaultText);
					mTxInput.setTextColor(0xff333333);
					mTxInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
					mTxInput.setHintTextColor(0xff8c8c8c);
					mTxInput.setGravity(Gravity.TOP);
					mTxInput.setBackgroundDrawable(null);
					mTxInput.setScrollContainer(true);
					mTxInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(140)});
					fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					fl.gravity = Gravity.TOP | Gravity.LEFT;
					fl.leftMargin = ShareData.PxToDpi_xhdpi(19);
					text_frame.addView(mTxInput, fl);
					mTxInput.setOnFocusChangeListener(mFocusChangeListener);
					mTxInput.addTextChangedListener(mTextWatcher);
					mTxInput.setOnTouchListener(new OnTouchListener() 
					{	
						@Override
						public boolean onTouch(View arg0, MotionEvent arg1) 
						{
							if(topic_status) showPartTopics();
							return false;
						}
					});
				}
					
				FrameLayout thumb_frame = new FrameLayout(getContext());
				ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(178), LayoutParams.MATCH_PARENT);
				ll.gravity = Gravity.TOP | Gravity.LEFT;
				ll.weight = 0;
				top_frame.addView(thumb_frame, ll);
				{
					ImageView mImageHolder = new ImageView(getContext());
					mImageHolder.setScaleType(ScaleType.CENTER_CROP);
					if(thumb != null && !thumb.isRecycled())
					{
						Bitmap send_thumb = MakeBmp.CreateFixBitmap(thumb, ShareData.PxToDpi_xhdpi(150), ShareData.PxToDpi_xhdpi(150), MakeBmp.POS_CENTER, 0, Config.ARGB_8888);
						mImageHolder.setImageBitmap(send_thumb);
					}
					fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
					fl.bottomMargin = ShareData.PxToDpi_xhdpi(28);
					thumb_frame.addView(mImageHolder, fl);
					
					ImageView mSuccess = new ImageView(getContext());
					mSuccess.setScaleType(ScaleType.CENTER_CROP);
					mSuccess.setImageResource(R.drawable.share_sendpage_success);
					fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.BOTTOM | Gravity.LEFT;
					fl.bottomMargin = ShareData.PxToDpi_xhdpi(28);
					fl.leftMargin = ShareData.PxToDpi_xhdpi(104);
					thumb_frame.addView(mSuccess, fl);
				}
			}
			
			TextView topic_text = new TextView(getContext());
			topic_text.setText(getContext().getResources().getString(R.string.share_sendblogpage_hot_topic));
			topic_text.setTextColor(0xe6000000);
			topic_text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.LEFT | Gravity.TOP;
			ll.weight = 0;
			ll.topMargin = ShareData.PxToDpi_xhdpi(34);
			ll.leftMargin = ShareData.PxToDpi_xhdpi(28);
			mainFrame.addView(topic_text, ll);
			
			ScrollView scroller = new ScrollView(getContext());
			ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			ll.weight = 1;
			mainFrame.addView(scroller, ll);
			{
				mTopicFrame = new LinearLayout(getContext());
				mTopicFrame.setOrientation(LinearLayout.VERTICAL);
				fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				fl.gravity = Gravity.TOP | Gravity.LEFT;
				fl.topMargin = ShareData.PxToDpi_xhdpi(2);
				fl.leftMargin = ShareData.PxToDpi_xhdpi(28);
				scroller.addView(mTopicFrame, fl);
				
				showPartTopics();
			}	
		}
		
		//弹出键盘
		this.post(new Runnable() 
		{	
			@Override
			public void run() 
			{
				if(mTxInput != null)
	        	{	
	        		InputMethodManager inputManager = (InputMethodManager)mTxInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
	        		inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	        		mTxInput.requestFocus();
	        		if(activities != null && activities.length() > 0) mTxInput.setSelection(activities.length());
	        	}	
			}
		});     
	}
	
	/**
	 * 隐藏键盘
	 */
	private void hideKeyboard()
	{
		this.post(new Runnable()
		{
			@Override
			public void run()
			{
				InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindowToken(), 0);
			}
		});
	}
	
	protected OnClickListener mClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v) 
		{
			if(v == mBack)
			{
				if(listener != null) listener.onClick(ID_BACK);
			}
			else if(v == mSend)
			{
				if(listener != null) listener.onClick(ID_SEND);
			}
			else if(v == mMoreJing)
			{
				showAllTopics();	
				hideKeyboard();
			}
			else if(v == mAddJing)
			{
				addJingString("##");
			}
		}
	};
	
	public String getText()
	{
		if(mTxInput != null) return mTxInput.getText().toString();
		return null;
	}
	
	public void addJingString(String strInsert)
    {
		if(strInsert != null && strInsert.length() > 0)
		{
			int index = mTxInput.getSelectionEnd();
			mTxInput.getText().insert(index, strInsert);
			if(strInsert.equals("##")) mTxInput.setSelection(mTxInput.getSelectionEnd() - 1);
			mTxInput.requestFocus();
		}
    }
	
	/**
	 * 输入框输入文字监听;
	 */
	private TextWatcher mTextWatcher = new TextWatcher()
	{
		@Override
		public void afterTextChanged(Editable s) {}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) 
		{
			int length = mTxInput.getText().length();			
			if(length >= 140) Toast.makeText(((Activity)getContext()), getContext().getResources().getString(R.string.share_sendblogpage_content_limit), Toast.LENGTH_LONG).show();
		}
	};
	
	private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener()
	{
		@Override
		public void onFocusChange(View v, boolean hasFocus) 
		{
			String c = mTxInput.getText().toString();	
			if(c == null || c.length() <= 0) mTxInput.setHint(mDefaultText);
			else mTxInput.setHint("");
		}
	};
	
	//读取记录的话题
	private ArrayList<TopicItem> readTopicsXML()
	{
		try 
		{
			File sdcard = Environment.getExternalStorageDirectory();
	        String strDir = sdcard.getPath()+"/beautyCamera/appdata/share/Topics.xml";
	        InputStream inputStream = new FileInputStream(new File(strDir));
	        SAXParserFactory spf = SAXParserFactory.newInstance(); 
			SAXParser sp = spf.newSAXParser();
			XMLTopicHandler handler = new XMLTopicHandler();
			sp.parse(inputStream, handler);
			return handler.getArrayList();
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private void showPartTopics()
	{
		topic_status = false;
		LinearLayout.LayoutParams ll;
		
		mTopicFrame.removeAllViews();
		ArrayList<TopicItem> topics;
		if(ShareFrame.mTopics != null && ShareFrame.mTopics.size() > 0)
		{
			topics = ShareFrame.mTopics;
		}
		else
		{
			topics = readTopicsXML();
			if(topics == null || topics.size() <= 0)
			{
				topics = new ArrayList<TopicItem>();
				for(int i=0; i<8; i++)
				{
					TopicItem topic = null;
					switch(i)
					{
					case 7:
						topic = new TopicItem(TOPIC8, null, "#"+TOPIC8+"#");
						break;
					
					case 6:
						topic = new TopicItem(TOPIC7, null, "#"+TOPIC7+"#");
						break;
					
					case 5:
						topic = new TopicItem(TOPIC6, null, "#"+TOPIC6+"#");
						break;
						
					case 4:
						topic = new TopicItem(TOPIC5, null, "#"+TOPIC5+"#");
						break;
						
					case 3:
						topic = new TopicItem(TOPIC4, null, "#"+TOPIC4+"#");
						break;
						
					case 2:
						topic = new TopicItem(TOPIC3, null, "#"+TOPIC3+"#");
						break;
						
					case 1:
						topic = new TopicItem(TOPIC2, null, "#"+TOPIC2+"#");
						break;
						
					case 0:
						topic = new TopicItem(TOPIC1, null, "#"+TOPIC1+"#");
						break;
						
					default:
						break;
					}
					topics.add(topic);
				}			
			}
		}
							
		int buttonNum = topics.size();			
		float screenW = 0;
		boolean full = true;
		LinearLayout buttonFrame = null;
		int layout_num = 0;

		for(int i=0; i<buttonNum; i++)
		{
			if(full)
			{	
				buttonFrame = new LinearLayout(getContext());
				buttonFrame.setOrientation(LinearLayout.HORIZONTAL);
				ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				ll.topMargin = ShareData.PxToDpi_xhdpi(22);
				mTopicFrame.addView(buttonFrame, ll);
				screenW = ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(28) * 2;
				layout_num++;
			}
			
			String theme = topics.get(i).getTopic();
			if(theme != null && theme.length() > 0)
			{					
				//汉字和英文、数字大小不一样需要区分计算
				int englishNum = ShareFrame.getEnglishNumber(theme);
				int chinese = theme.length() - englishNum;

				if(screenW - (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum) * ShareData.m_resScale >= 0)
				{	
					full = false;
					if(layout_num == 2 && screenW - (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum) * ShareData.m_resScale <= ShareData.PxToDpi_xhdpi(180)) break;
						
					boolean margin = false;
					if(screenW - (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum) * ShareData.m_resScale > ShareData.PxToDpi_xhdpi(22))
					{
						margin = true;
					}	
					screenW -= (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum + 11) * ShareData.m_resScale;
				
					final TopicButton button = new TopicButton(getContext());
					button.setButtonBG(R.drawable.share_topic_bg, false);
					button.setText(topics.get(i));
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					ll.gravity = Gravity.LEFT | Gravity.TOP;
					if(margin) ll.rightMargin = ShareData.PxToDpi_xhdpi(22);
					buttonFrame.addView(button, ll);
					button.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) 
						{
							TongJi2.AddCountByRes(getContext(), R.integer.分享_点击某个热门标签);
							addJingString(button.getText().toString());	
						}
					});
				}
				else
				{
					if(layout_num == 2) break;
					full = true;
					i--;
				}
			}
		}
							
		mMoreJing = new TopicButton(getContext());
		mMoreJing.setButtonBG(R.drawable.share_topic_bg, false);
		mMoreJing.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.share_sendpage_more));
		ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		ll.rightMargin = ShareData.PxToDpi_xhdpi(22);
		buttonFrame.addView(mMoreJing, ll);
		mMoreJing.setOnClickListener(mClickListener);
	
		mAddJing = new TopicButton(getContext());
		mAddJing.setButtonBG(R.drawable.share_topic_bg, false);
		mAddJing.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.share_sendpage_jing));
		ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		buttonFrame.addView(mAddJing, ll);
		mAddJing.setOnClickListener(mClickListener);
	}
	
	private void showAllTopics()
	{
		topic_status = true;
		LinearLayout.LayoutParams ll;
		
		mTopicFrame.removeAllViews();
		ArrayList<TopicItem> topics;
		if(ShareFrame.mTopics != null && ShareFrame.mTopics.size() > 0)
		{
			topics = ShareFrame.mTopics;
		}
		else
		{
			topics = readTopicsXML();
			if(topics == null || topics.size() <= 0)
			{
				topics = new ArrayList<TopicItem>();
				for(int i=0; i<8; i++)
				{
					TopicItem topic = null;
					switch(i)
					{
					case 7:
						topic = new TopicItem(TOPIC8, null, "#"+TOPIC8+"#");
						break;
					
					case 6:
						topic = new TopicItem(TOPIC7, null, "#"+TOPIC7+"#");
						break;
					
					case 5:
						topic = new TopicItem(TOPIC6, null, "#"+TOPIC6+"#");
						break;
						
					case 4:
						topic = new TopicItem(TOPIC5, null, "#"+TOPIC5+"#");
						break;
						
					case 3:
						topic = new TopicItem(TOPIC4, null, "#"+TOPIC4+"#");
						break;
						
					case 2:
						topic = new TopicItem(TOPIC3, null, "#"+TOPIC3+"#");
						break;
						
					case 1:
						topic = new TopicItem(TOPIC2, null, "#"+TOPIC2+"#");
						break;
						
					case 0:
						topic = new TopicItem(TOPIC1, null, "#"+TOPIC1+"#");
						break;
						
					default:
						break;
					}
					topics.add(topic);
				}			
			}
		}
							
		int buttonNum = topics.size();			
		float screenW = 0;
		boolean full = true;
		LinearLayout buttonFrame = null;
		
		for(int i=0; i<buttonNum; i++)
		{
			if(full)
			{
				buttonFrame = new LinearLayout(getContext());
				buttonFrame.setOrientation(LinearLayout.HORIZONTAL);
				ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				ll.topMargin = ShareData.PxToDpi_xhdpi(22);
				mTopicFrame.addView(buttonFrame, ll);
				screenW = ShareData.m_screenWidth - ShareData.PxToDpi_xhdpi(28) * 2;
			}
			
			String theme = topics.get(i).getTopic();
			if(theme != null && theme.length() > 0)
			{					
				//汉字和英文、数字大小不一样需要区分计算
				int englishNum = ShareFrame.getEnglishNumber(theme);
				int chinese = theme.length() - englishNum;
				
				if(screenW - (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum) * ShareData.m_resScale >= 0)
				{	
					full = false;					
					boolean margin = false;
					if(screenW - (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum) * ShareData.m_resScale > ShareData.PxToDpi_xhdpi(22))
					{
						margin = true;
					}	
					screenW -= (TopicButton.TOPIC_BUTTON_MARGIN + ShareFrame.CHINESE_LENGTH * chinese + ShareFrame.ENGLISH_NUM_LENGTH * englishNum + 11) * ShareData.m_resScale;
				
					final TopicButton button = new TopicButton(getContext());
					button.setButtonBG(R.drawable.share_topic_bg, false);
					button.setText(topics.get(i));
					ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					if(margin) ll.rightMargin = ShareData.PxToDpi_xhdpi(22);
					buttonFrame.addView(button, ll);
					button.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View v) 
						{
							TongJi2.AddCountByRes(getContext(), R.integer.分享_点击某个热门标签);
							addJingString(button.getText().toString());	
						}
					});
				}
				else
				{
					full = true;
					i--;
				}
			}
		}
		
		if(screenW < ShareData.PxToDpi_xhdpi(70))
		{
			buttonFrame = new LinearLayout(getContext());
			buttonFrame.setOrientation(LinearLayout.HORIZONTAL);
			ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			ll.topMargin = ShareData.PxToDpi_xhdpi(22);
			mTopicFrame.addView(buttonFrame, ll);
		}
		
		mAddJing = new TopicButton(getContext());
		mAddJing.setButtonBG(R.drawable.share_topic_bg, false);
		mAddJing.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.share_sendpage_jing));
		ll = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		ll.gravity = Gravity.TOP | Gravity.LEFT;
		buttonFrame.addView(mAddJing, ll);
		mAddJing.setOnClickListener(mClickListener);
	}
	
	private class XMLTopicHandler extends DefaultHandler
	{
		private static final String TOPIC = "topic";
		private static final String THEME = "theme";
		private static final String CONTENT = "content";
		
		private ArrayList<TopicItem> mTopic;
		private TopicItem topic;
		private String theme;
		private String content;
		private String tempString;
		
		@Override
		public void startDocument() throws SAXException 
		{
			super.startDocument();
			mTopic = new ArrayList<TopicItem>();
		}
		
		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException 
		{
			super.startElement(uri, localName, qName, attributes);
			if(TOPIC.equals(localName))
			{
				topic = new TopicItem();
			}
			tempString = localName;
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException 
		{
			super.endElement(uri, localName, qName);
			if(TOPIC.equals(localName)) 
			{
				topic.setTopic(theme);
				topic.setDisTopic(content);
				mTopic.add(topic);
				topic = null;
				theme = null;
				content = null;
			}
			tempString = null;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException 
		{
			super.characters(ch, start, length);
			String valueString = new String(ch, start, length);
			if(THEME.equals(tempString)) 
			{
				theme = valueString;
			}
			else if(CONTENT.equals(tempString))
			{
				content = valueString;
			}
		}
		
		public ArrayList<TopicItem> getArrayList()
		{
			return mTopic;
		}
	}
	
	public void clean()
	{
		if(mTxInput != null)
		{	
			mTxInput.setOnFocusChangeListener(null);
			mTxInput.clearFocus();
			mTxInput.setOnClickListener(null);
			mTxInput.removeTextChangedListener(mTextWatcher);
			mTxInput.addTextChangedListener(null);
			mTxInput = null;
		}
		listener = null;
		hideKeyboard();
	}
}
