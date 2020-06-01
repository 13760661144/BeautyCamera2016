package cn.poco.business;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adnonstop.admasterlibs.data.AbsChannelAdRes;
import com.adnonstop.admasterlibs.data.ActInputInfo;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.poco.advanced.ImageUtils;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyTextButton;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

public class ActSignUpView extends RelativeLayout
{
	//private static final String INFO_POST_URL = "http://www1.poco.cn/topic/interface/user_info_post.php";

	public ActSignUpView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public ActSignUpView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public ActSignUpView(Context context)
	{
		super(context);
		initialize(context);
	}

	private int mFrW;
	private LinearLayout mFrContent; //全部控件
	private LinearLayout mContainer; //动态控件
	private LinearLayout mBtnContainer; //下一步/提交信息+跳过
	private MyTextButton mOk;
	private TextView mBtnSkip;
	private AbsChannelAdRes mRes;
	private ImageView mBtnCancel;
	private TextView mTxHelpTitle;
	private TextView mTxHelpContent;
	private ScrollView mScrollView;
	protected ActSignUpDialog mDialog;
	private ArrayList<Control> mCtrls = new ArrayList<Control>();

	private void initialize(Context context)
	{
		LayoutParams rl;
		FrameLayout.LayoutParams fl;
		LinearLayout.LayoutParams ll;
		//mFrW = ShareData.PxToDpi_xhdpi(630);
		mFrW = ShareData.m_screenWidth;

		FrameLayout fr = new FrameLayout(getContext());
		//fr.setBackgroundResource(R.drawable.business_signup_bk);
		fr.setBackgroundColor(0xffffffff);
		rl = new LayoutParams(mFrW, LayoutParams.WRAP_CONTENT);
		rl.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		rl.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		fr.setLayoutParams(rl);
		this.addView(fr);
		{
			mFrContent = new LinearLayout(context);
			mFrContent.setOrientation(LinearLayout.VERTICAL);
			fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			mFrContent.setLayoutParams(fl);
			fr.addView(mFrContent);
			{
				FrameLayout topBar = new FrameLayout(getContext());
				topBar.setBackgroundColor(0xf4ffffff);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(80));
				ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
				mFrContent.addView(topBar, ll);
				{
					mBtnCancel = new ImageView(getContext());
					mBtnCancel.setImageResource(R.drawable.framework_back_btn);
					ImageUtils.AddSkin(getContext(), mBtnCancel);
					fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
					fl.leftMargin = ShareData.PxToDpi_xhdpi(2);
					fl.topMargin = ShareData.PxToDpi_xhdpi(5);
					topBar.addView(mBtnCancel, fl);
					mBtnCancel.setOnTouchListener(new OnAnimationClickListener()
					{
						@Override
						public void onAnimationClick(View v)
						{
							if(mDialog != null)
							{
								mDialog.cancel();
							}
						}

						@Override
						public void onTouch(View v)
						{

						}

						@Override
						public void onRelease(View v)
						{

						}
					});
				}

				TextView title = new TextView(context);
				title.setSingleLine();
				title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//				title.getPaint().setFakeBoldText(true);
				String str1 = "请填妥以下报名信息";
				String str2 = "（*必填）";
				SpannableString ss = new SpannableString(str1 + str2);
				ss.setSpan(new ForegroundColorSpan(0xffaaaaaa), 0, str1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				ss.setSpan(new ForegroundColorSpan(0xffaaaaaa), str1.length(), str1.length() + str2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				title.setText(ss);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = -ShareData.PxToDpi_xhdpi(6);
				title.setLayoutParams(ll);
				mFrContent.addView(title);

				mScrollView = new ScrollView(getContext())
				{
					@Override
					protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
					{
						super.onMeasure(widthMeasureSpec, heightMeasureSpec);

					}
				};
//				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)(ShareData.m_screenHeight * 0.45f));
				ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.LEFT | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(16);
				ll.leftMargin = ShareData.getRealPixel_720P(40);
				mScrollView.setLayoutParams(ll);
				mFrContent.addView(mScrollView);
				{
					mContainer = new LinearLayout(getContext());
					mContainer.setOrientation(LinearLayout.VERTICAL);
					fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
					fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
					mContainer.setLayoutParams(fl);
					mScrollView.addView(mContainer);
				}
				mTxHelpTitle = new TextView(context);
				mTxHelpTitle.setTextColor(0xff6e6e6e);
				mTxHelpTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
				mTxHelpTitle.setGravity(Gravity.LEFT | Gravity.TOP);
				mTxHelpTitle.getPaint().setFakeBoldText(true);
				ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(25);
				ll.bottomMargin = ShareData.PxToDpi_xhdpi(8);
				ll.leftMargin = ShareData.PxToDpi_xhdpi(40);
				mTxHelpTitle.setLayoutParams(ll);
				mTxHelpTitle.setVisibility(View.GONE);
				mFrContent.addView(mTxHelpTitle);

				mTxHelpContent = new TextView(context);
				mTxHelpContent.setTextColor(0xff878787);
				mTxHelpContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
				mTxHelpContent.setGravity(Gravity.LEFT | Gravity.TOP);
				ll = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.bottomMargin = ShareData.PxToDpi_xhdpi(25);
				ll.leftMargin = ShareData.PxToDpi_xhdpi(40);
				mTxHelpContent.setLayoutParams(ll);
				mTxHelpContent.setVisibility(View.GONE);
				mFrContent.addView(mTxHelpContent);

				mBtnContainer = new LinearLayout(context);
				mBtnContainer.setOrientation(LinearLayout.HORIZONTAL);
				mBtnContainer.setGravity(Gravity.CENTER);
				ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				ll.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
				ll.topMargin = ShareData.PxToDpi_xhdpi(20);
				ll.bottomMargin = ShareData.PxToDpi_xhdpi(45);
				mBtnContainer.setLayoutParams(ll);
				mFrContent.addView(mBtnContainer);
			}
		}
	}

	public void setBusinessRes(AbsChannelAdRes.GatePageData pageData)
	{
		mBtnContainer.removeAllViews();
		if(pageData != null && !pageData.mShowSkipBtn)
		{
			mOk = new MyTextButton(getContext());
			mOk.setBk(R.drawable.business_submit_btn_bk);
			mOk.setName(R.string.business_submit_btn_name, 14, 0xffffffff, false);
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER;
			mOk.setLayoutParams(ll);
			mBtnContainer.addView(mOk);
		}
		else
		{
			mBtnSkip = new TextView(getContext());
			mBtnSkip.setSingleLine();
			mBtnSkip.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			mBtnSkip.setTextColor(0xFF000000);
			mBtnSkip.setText("跳过填写");
			mBtnSkip.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.FILL_PARENT);
			ll.gravity = Gravity.CENTER;
			mBtnSkip.setLayoutParams(ll);
			mBtnContainer.addView(mBtnSkip);
			mBtnSkip.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					onSkip();
				}
			});

			mOk = new MyTextButton(getContext());
			mOk.setBk(R.drawable.business_submit_btn_bk);
			mOk.setName(R.string.business_submit_btn_name, 14, 0xffffffff, false);
			ll = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER;
			ll.leftMargin = ShareData.PxToDpi_xhdpi(65);
			mOk.setLayoutParams(ll);
			mBtnContainer.addView(mOk);
		}
		mOk.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				onOk();
			}
		});
		mCtrls.clear();
		if(pageData != null)
		{
			int count = pageData.mInputInfoArr.size();
			for(int i = 0; i < count; i++)
			{
				ActInputInfo info = pageData.mInputInfoArr.get(i);
				Control ctrl = makeControl(info);
				if(ctrl != null)
				{
					LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					if(ctrl instanceof SingleSelCtrl)
					{
						ll.topMargin = ShareData.PxToDpi_xhdpi(22);
					}
					else if(ctrl instanceof MultiSelCtrl)
					{
						ll.topMargin = ShareData.PxToDpi_xhdpi(22);
					}
					else if(ctrl instanceof ComboBoxCtrl)
					{
						ll.topMargin = ShareData.PxToDpi_xhdpi(22);
					}
					mContainer.addView(ctrl, ll);
					mCtrls.add(ctrl);
				}
			}
			if(pageData.mDlgTitle != null && pageData.mDlgTitle.length() > 0 && mTxHelpTitle != null)
			{
				mTxHelpTitle.setVisibility(View.VISIBLE);
				mTxHelpTitle.setText(pageData.mDlgTitle);
			}
			if(pageData.mDlgContent != null && pageData.mDlgContent.length() > 0 && mTxHelpContent != null)
			{
				mTxHelpContent.setVisibility(View.VISIBLE);
				mTxHelpContent.setText(pageData.mDlgContent);
			}
		}
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				if(mScrollView.getHeight() > ShareData.PxToDpi_xhdpi(600))
				{
					ViewGroup.LayoutParams params = mScrollView.getLayoutParams();
					if(params != null)
					{
						params.height = ShareData.PxToDpi_xhdpi(600);
						mScrollView.setLayoutParams(params);
					}
				}
			}
		}, 100);
	}

	public void setBusinessRes(AbsChannelAdRes res)
	{
		mRes = res;

		AbsChannelAdRes.GatePageData pageData = null;
		if(mRes != null)
		{
			pageData = (AbsChannelAdRes.GatePageData)mRes.GetPageData(AbsChannelAdRes.GatePageData.class);
		}
		setBusinessRes(pageData);
	}

	/**
	 * 检查邮箱地址是否合法;
	 *
	 * @param emailAddr 邮箱地址
	 * @return isValidate ture代表合法;
	 */
	boolean validateEmail(String emailAddr)
	{
		boolean isValidate = false;
		if(!TextUtils.isEmpty(emailAddr) && emailAddr.contains("@"))
		{
			isValidate = Pattern.compile("^[_\\.0-9a-zA-Z+-]+@([0-9a-zA-Z]+[0-9a-zA-Z-]*\\.)+[a-zA-Z]{2,4}$").matcher(emailAddr).find();
		}
		return isValidate;
	}

	public void onSkip()
	{
		if(mDialog != null)
		{
			mDialog.onOk(null);
		}
	}

	public void onOk()
	{
		JSONObject postVal = new JSONObject();
		try
		{
			int count = mCtrls.size();
			for(int i = 0; i < count; i++)
			{
				Control control = mCtrls.get(i);
				String value = control.getValue();
				ActInputInfo inputInfo = control.getInputInfo();
				if(value != null && value.length() == 0)
				{
					value = null;
				}
				if(inputInfo != null && value != null)
				{
					if(inputInfo.inputType == ActInputInfo.INPUTTYPE_NUMBER || inputInfo.inputType == ActInputInfo.INPUTTYPE_PASSWORD || inputInfo.inputType == ActInputInfo.INPUTTYPE_TEXT)
					{
						if(value.length() < inputInfo.minLength)
						{
							Toast toast = Toast.makeText(this.getContext().getApplicationContext(), "请正确填写" + inputInfo.name, Toast.LENGTH_SHORT);
							toast.show();
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							return;
						}
						if(inputInfo.type.equals("email") && validateEmail(value) == false)
						{
							Toast toast = Toast.makeText(this.getContext().getApplicationContext(), "请正确填写" + inputInfo.name, Toast.LENGTH_SHORT);
							toast.show();
							toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
							return;
						}
					}
					TagMgr.SetTagValue(getContext(), Tags.BUSINESS_INFO_FLAG + inputInfo.type, value);
					try
					{
						value = URLEncoder.encode(value, "UTF-8");
					}
					catch(UnsupportedEncodingException e)
					{
					}
					//postVal += "|" + inputInfo.type + ":" + value;
					postVal.put(inputInfo.type, value);
				}
				else if(inputInfo != null && value == null && inputInfo.important == true)
				{
					if(inputInfo.inputType != ActInputInfo.INPUTTYPE_MULTISEL && inputInfo.inputType != ActInputInfo.INPUTTYPE_SINGLESEL)
					{
						Toast toast = Toast.makeText(this.getContext().getApplicationContext(), "\"" + inputInfo.name + "\"未填写", Toast.LENGTH_SHORT);
						toast.show();
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					}
					else
					{
						Toast toast = Toast.makeText(this.getContext().getApplicationContext(), "请选择\"" + inputInfo.name + "\"", Toast.LENGTH_SHORT);
						toast.show();
						toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
					}
					return;
				}
			}
		}
		catch(Throwable e)
		{
			e.printStackTrace();
		}
		//postInfo(mRes.m_channelValue, postVal);
		if(mDialog != null)
		{
			mDialog.onOk(postVal);
		}
	}

	/*public static void postInfo(final String channelValue, final String postStr)
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				NetCore2 net = new NetCore2();
				HashMap<String, String> params = new HashMap<String, String>();
				if(channelValue != null)
				{
					params.put("channel_value", URLEncoder.encode(channelValue));
				}
				if(postStr != null)
				{
					params.put("post_str", URLEncoder.encode(postStr));
				}
				NetCore2.NetMsg msg = net.HttpPost(INFO_POST_URL, params, null, true);
				if(msg != null)
				{
					if(msg.m_data != null)
					{
						//System.out.println(new String(msg.m_data));
					}
				}
			}
		}).start();
	}*/

	private Control makeControl(ActInputInfo inputInfo)
	{
		Control control = null;
		if(inputInfo != null)
		{
			switch(inputInfo.inputType)
			{
				case ActInputInfo.INPUTTYPE_TEXT:
				case ActInputInfo.INPUTTYPE_PASSWORD:
				case ActInputInfo.INPUTTYPE_NUMBER:
					control = new InputCtrl(getContext());
					break;
				case ActInputInfo.INPUTTYPE_SINGLESEL:
					control = new SingleSelCtrl(getContext());
					break;
				case ActInputInfo.INPUTTYPE_MULTISEL:
					control = new MultiSelCtrl(getContext());
					break;
				case ActInputInfo.INPUTTYPE_COMBOBOX:
					control = new ComboBoxCtrl(getContext());
					break;
			}
		}
		if(control != null)
		{
			control.setInputInfo(inputInfo);
		}
		return control;
	}

	private class SingleSelCtrl extends Control
	{

		public SingleSelCtrl(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
		}

		public SingleSelCtrl(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		public SingleSelCtrl(Context context)
		{
			super(context);
		}

		private ArrayList<RadioButton> mRadioButtons = new ArrayList<RadioButton>();
		private ActInputInfo mInputInfo;

		@Override
		public void setInputInfo(ActInputInfo inputInfo)
		{
			mInputInfo = inputInfo;
			mRadioButtons.clear();
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			TextView textView = new TextView(getContext());
			textView.setTextColor(0xff6e6e6e);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textView.setGravity(Gravity.LEFT);
			textView.setText(inputInfo.name + ":");
			addView(textView, params);
			textView.setId(R.id.business_sign_up_single_sel_text);

			if(inputInfo.important)
			{
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.rightMargin = ShareData.PxToDpi_xhdpi(80);
				params.leftMargin = ShareData.PxToDpi_xhdpi(50);
				TextView important = new TextView(getContext());
				important.setTextColor(0xff6e6e6e);
				important.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0f);
				important.setGravity(Gravity.RIGHT);
				important.setPadding(0, ShareData.PxToDpi_xhdpi(10), 0, 0);
				important.setText("*");
				important.setId(R.id.business_sign_up_important);
				addView(important, params);
			}

			params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.business_sign_up_single_sel_text);
			params.addRule(RelativeLayout.LEFT_OF, R.id.business_sign_up_important);
			params.leftMargin = ShareData.PxToDpi_xhdpi(95);
			LinearLayout radioGroup = new LinearLayout(getContext());
			radioGroup.setOrientation(LinearLayout.VERTICAL);
			addView(radioGroup, params);
			String options = inputInfo.options;
			if(options != null)
			{
				String[] pos2 = options.split(",");
				int count = pos2.length;
				LinearLayout.LayoutParams ll = null;
				for(int i = 0; i < count; i++)
				{
					if(i % 2 == 0 && i + 1 < count)
					{
						LinearLayout r = new LinearLayout(getContext());
						r.setOrientation(LinearLayout.HORIZONTAL);
						// 第一个 radioBtn
						RadioButton radioBtn = new RadioButton(getContext());
						radioBtn.setSingleLine();
						radioBtn.setText(pos2[i]);
						radioBtn.setTextColor(0xff6e6e6e);
						radioBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
						radioBtn.setGravity(Gravity.CENTER_VERTICAL);
						radioBtn.setButtonDrawable(R.drawable.business_radiobutton);
						if(android.os.Build.VERSION.SDK_INT <= 16)
						{
							radioBtn.setPadding(ShareData.PxToDpi_xhdpi(55), 0, 0, 0);
						}
						else
						{
							radioBtn.setPadding(ShareData.PxToDpi_xhdpi(8), 0, 0, 0);
						}
						radioBtn.setBackgroundColor(0);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						radioBtn.setLayoutParams(ll);
						radioBtn.setOnClickListener(listener);
						r.addView(radioBtn);
						mRadioButtons.add(radioBtn);

						// 第二个 radioBtn
						RadioButton rb = new RadioButton(getContext());
						rb.setSingleLine();
						rb.setText(pos2[i + 1]);
						rb.setTextColor(0xff6e6e6e);
						rb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
						rb.setGravity(Gravity.CENTER_VERTICAL);
						rb.setButtonDrawable(R.drawable.business_radiobutton);
						if(android.os.Build.VERSION.SDK_INT <= 16)
						{
							rb.setPadding(ShareData.PxToDpi_xhdpi(55), 0, 0, 0);
						}
						else
						{
							rb.setPadding(ShareData.PxToDpi_xhdpi(8), 0, 0, 0);
						}
						rb.setBackgroundColor(0);
						ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						ll.leftMargin = ShareData.PxToDpi_xhdpi(50);
						rb.setLayoutParams(ll);
						rb.setOnClickListener(listener);
						r.addView(rb);
						ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						ll.bottomMargin = ShareData.PxToDpi_xhdpi(10);
						if(i + 2 >= count)
						{
							ll.bottomMargin = 0;
						}
						radioGroup.addView(r, ll);
						mRadioButtons.add(rb);
					}
				}
				radioGroup.setId(R.id.business_sign_up_single_sel_group);

				String defaultValue = TagMgr.GetTagValue(getContext(), Tags.BUSINESS_INFO_FLAG + inputInfo.type);
				if(defaultValue == null)
				{
					defaultValue = inputInfo.defaultValue;
				}
				if(defaultValue != null && defaultValue.length() > 0)
				{
					try
					{
						int index = Integer.parseInt(defaultValue.substring(0, 1)) - 1;
						if(index >= 0 && index < mRadioButtons.size())
						{
							mRadioButtons.get(index).setChecked(true);
						}
					}
					catch(Exception e)
					{
					}
				}
			}
		}

		@Override
		public String getValue()
		{
			String sel = "";
			int count = mRadioButtons.size();
			for(int i = 0; i < count; i++)
			{
				if(mRadioButtons.get(i).isChecked() == true)
				{
					sel += (i + 1) + "";
					break;
				}
			}
			return sel;
		}

		@Override
		public ActInputInfo getInputInfo()
		{
			return mInputInfo;
		}

		private OnClickListener listener = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int count = mRadioButtons.size();
				for(int i = 0; i < count; i++)
				{
					RadioButton radioBtn = mRadioButtons.get(i);
					radioBtn.setChecked(v == radioBtn);
				}
			}
		};

	}

	private class RadioGroup extends LinearLayout
	{

		public RadioGroup(Context context, AttributeSet attrs)
		{
			super(context, attrs);
			initialize(context);
		}

		public RadioGroup(Context context)
		{
			super(context);
			initialize(context);
		}

		private void initialize(Context context)
		{
			this.setOrientation(LinearLayout.HORIZONTAL);
		}

		public void addView(View child)
		{
			child.setOnClickListener(mOnClickListener);
			super.addView(child);
		}

		private OnClickListener mOnClickListener = new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				int count = getChildCount();
				RadioButton radioBtn;
				for(int i = 0; i < count; i++)
				{
					radioBtn = (RadioButton)getChildAt(i);
					radioBtn.setChecked(arg0 == radioBtn);
				}
			}
		};
	}

	private class MultiSelCtrl extends Control
	{

		public MultiSelCtrl(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
		}

		public MultiSelCtrl(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		public MultiSelCtrl(Context context)
		{
			super(context);
		}

		private ArrayList<CheckBox> mCheckBoxs = new ArrayList<CheckBox>();
		private ActInputInfo mInputInfo;

		@Override
		public void setInputInfo(ActInputInfo inputInfo)
		{
			mInputInfo = inputInfo;
			mCheckBoxs.clear();
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			TextView textView = new TextView(getContext());
			textView.setTextColor(0xff6e6e6e);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textView.setGravity(Gravity.LEFT);
			textView.setText(inputInfo.name + ":");
			addView(textView, params);
			textView.setId(R.id.business_sign_up_multi_sel_text);

			if(inputInfo.important)
			{
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.rightMargin = ShareData.PxToDpi_xhdpi(80);
				params.leftMargin = ShareData.PxToDpi_xhdpi(50);
				TextView important = new TextView(getContext());
				important.setTextColor(0xff6e6e6e);
				important.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0f);
				important.setGravity(Gravity.RIGHT);
				important.setPadding(0, ShareData.PxToDpi_xhdpi(8), 0, 0);
				important.setText("*");
				important.setId(R.id.business_sign_up_important);
				addView(important, params);
			}


			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.business_sign_up_multi_sel_text);
			params.addRule(RelativeLayout.LEFT_OF, R.id.business_sign_up_important);
			params.leftMargin = ShareData.PxToDpi_xhdpi(100);
			LinearLayout group = new LinearLayout(getContext());
			group.setOrientation(LinearLayout.VERTICAL);
			addView(group, params);

			LinearLayout g = null;
			LinearLayout.LayoutParams ll = null;
			String options = inputInfo.options;
			if(options != null)
			{
				String[] opts = options.split(",");
				int count = opts.length;
				for(int i = 0; i < count; i++)
				{
					if(i % 2 == 0)
					{
						g = new LinearLayout(getContext());
						ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
						ll.bottomMargin = ShareData.PxToDpi_xhdpi(10);
						if(i + 2 >= count)
						{
							ll.bottomMargin = 0;
						}
						group.addView(g, ll);
					}
					CheckBox checkBtn = new CheckBox(getContext());
					checkBtn.setSingleLine();
					checkBtn.setText(opts[i]);
					checkBtn.setTextColor(0xff787878);
					checkBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
					checkBtn.setButtonDrawable(R.drawable.business_checkbox);
					if(android.os.Build.VERSION.SDK_INT <= 16)
					{
						checkBtn.setPadding(ShareData.PxToDpi_xhdpi(55), 0, 0, 0);
					}
					else
					{
						checkBtn.setPadding(ShareData.PxToDpi_xhdpi(8), 0, 0, 0);
					}
					checkBtn.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
					ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					if(i > 0 && i % 2 != 0)
					{
						ll.leftMargin = ShareData.PxToDpi_xhdpi(60);
					}
					checkBtn.setLayoutParams(ll);
					g.addView(checkBtn);
					mCheckBoxs.add(checkBtn);
				}
			}
			group.setId(R.id.business_sign_up_multi_sel_group);

			String defaultValue = TagMgr.GetTagValue(getContext(), Tags.BUSINESS_INFO_FLAG + inputInfo.type);
			if(defaultValue == null)
			{
				defaultValue = inputInfo.defaultValue;
			}
			if(defaultValue != null && defaultValue.length() > 0)
			{
				String[] defaultOpts = defaultValue.split(",");
				int len = defaultOpts.length;
				for(int i = 0; i < len; i++)
				{
					try
					{
						int index = Integer.parseInt(defaultOpts[i]) - 1;
						if(index >= 0 && index < mCheckBoxs.size())
						{
							mCheckBoxs.get(index).setChecked(true);
						}
					}
					catch(Exception e)
					{
					}
				}
			}
		}

		@Override
		public String getValue()
		{
			String sel = "";
			int count = mCheckBoxs.size();
			for(int i = 0; i < count; i++)
			{
				if(mCheckBoxs.get(i).isChecked() == true)
				{
					if(i != count - 1)
					{
						sel += (i + 1) + ",";
					}
					else
					{
						sel += (i + 1) + "";
					}
				}
			}
			return sel;
		}

		@Override
		public ActInputInfo getInputInfo()
		{
			return mInputInfo;
		}

	}

	private class ComboBoxCtrl extends Control
	{

		public ComboBoxCtrl(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
		}

		public ComboBoxCtrl(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		public ComboBoxCtrl(Context context)
		{
			super(context);
		}

		private Spinner mComboBox;
		private ActInputInfo mInputInfo;

		@Override
		public String getValue()
		{
			if(mComboBox != null)
			{
				int sel = mComboBox.getSelectedItemPosition();
				if(sel >= 0)
				{
					return "" + (sel + 1);
				}
			}
			return null;
		}

		@Override
		public void setInputInfo(ActInputInfo inputInfo)
		{
			mInputInfo = inputInfo;
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			//params.leftMargin = ShareData.PxToDpi_xhdpi(20);
			TextView textView = new TextView(getContext());
			textView.setTextColor(0xff6e6e6e);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textView.setGravity(Gravity.LEFT);
			textView.setText(inputInfo.name + ":");
			addView(textView, params);
			textView.setId(R.id.business_sign_up_combo_text);
			params = new LayoutParams(ShareData.PxToDpi_xhdpi(200), ShareData.PxToDpi_xhdpi(60));
			params.addRule(RelativeLayout.RIGHT_OF, R.id.business_sign_up_combo_text);
			params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			params.leftMargin = ShareData.PxToDpi_xhdpi(20);
			mComboBox = new Spinner(getContext());
			mComboBox.setBackgroundResource(R.drawable.business_spinner);
			mComboBox.setGravity(Gravity.LEFT);
			mComboBox.setPadding(30, 0, 0, 0);
			addView(mComboBox, params);
			mComboBox.setId(R.id.business_sign_up_combo_box);

			String options = inputInfo.options;
			if(options != null)
			{
				String[] opts = options.split(",");
				int count = opts.length;
//				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item);
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.business_spinner_text);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				for(int i = 0; i < count; i++)
				{
					adapter.add(opts[i]);
				}
				mComboBox.setAdapter(adapter);
				String defaultValue = TagMgr.GetTagValue(getContext(), Tags.BUSINESS_INFO_FLAG + inputInfo.type);
				if(defaultValue == null)
				{
					defaultValue = inputInfo.defaultValue;
				}
				if(defaultValue != null)
				{
					try
					{
						int index = Integer.parseInt(defaultValue) - 1;
						if(index >= 0 && index < adapter.getCount())
						{
							mComboBox.setSelection(index);
						}
					}
					catch(Exception e)
					{
					}
				}
			}

			if(inputInfo.important)
			{
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.rightMargin = ShareData.PxToDpi_xhdpi(80);
				params.leftMargin = ShareData.PxToDpi_xhdpi(50);
				TextView important = new TextView(getContext());
				important.setTextColor(0xff6e6e6e);
				important.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0f);
				important.setGravity(Gravity.RIGHT);
				important.setPadding(0, ShareData.PxToDpi_xhdpi(10), 0, 0);
				important.setText("*");
				addView(important, params);
			}
		}

		@Override
		public ActInputInfo getInputInfo()
		{
			return mInputInfo;
		}
	}

	private class InputCtrl extends Control
	{

		public InputCtrl(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
		}

		public InputCtrl(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		public InputCtrl(Context context)
		{
			super(context);
		}

		private EditText mEditText;
		private ActInputInfo mInputInfo;

		@Override
		public String getValue()
		{
			if(mEditText != null)
			{
				return mEditText.getText().toString();
			}
			else
			{
				return null;
			}
		}

		@Override
		public void setInputInfo(ActInputInfo inputInfo)
		{
			mInputInfo = inputInfo;
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//			LayoutParams params = new LayoutParams(ShareData.PxToDpi_xhdpi(105), ShareData.PxToDpi_xhdpi(90));
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			TextView textView = new TextView(getContext());
			textView.setTextColor(0xff787878);
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			textView.setGravity(Gravity.LEFT);
			textView.setText(inputInfo.name + ":");
			addView(textView, params);
			textView.setId(R.id.business_sign_up_input_text);

			if(inputInfo.important)
			{
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_VERTICAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				params.rightMargin = ShareData.PxToDpi_xhdpi(80);
				params.leftMargin = ShareData.PxToDpi_xhdpi(50);
				TextView important = new TextView(getContext());
				important.setTextColor(0xff6e6e6e);
				important.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.0f);
				important.setGravity(Gravity.RIGHT);
				important.setPadding(0, ShareData.PxToDpi_xhdpi(10), 0, 0);
				important.setText("*");
				addView(important, params);
			}

			params = new LayoutParams(ShareData.PxToDpi_xhdpi(333), LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.RIGHT_OF, R.id.business_sign_up_input_text);
			params.addRule(RelativeLayout.CENTER_VERTICAL);
			params.leftMargin = ShareData.PxToDpi_xhdpi(80);
			mEditText = new EditText(getContext());
			mEditText.setBackgroundColor(Color.TRANSPARENT);
			mEditText.setSingleLine(true);
			mEditText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(inputInfo.maxLength)});
			mEditText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			mEditText.setTextColor(0xff000000);
			addView(mEditText, params);
			if(inputInfo.inputType == ActInputInfo.INPUTTYPE_NUMBER)
			{
				mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			}
			else if(inputInfo.inputType == ActInputInfo.INPUTTYPE_PASSWORD)
			{
				mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			}
			mEditText.setId(R.id.business_sign_up_input_edit);

			int lineH = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 1, getResources().getDisplayMetrics());
			params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, lineH);
			params.addRule(RelativeLayout.BELOW, R.id.business_sign_up_input_edit);
			params.topMargin = ShareData.PxToDpi_xhdpi(10);
			View line = new View(getContext());
			line.setBackgroundColor(0xffececec);
			addView(line, params);

			String defaultValue = TagMgr.GetTagValue(getContext(), Tags.BUSINESS_INFO_FLAG + inputInfo.type);
			if(defaultValue == null)
			{
				defaultValue = inputInfo.defaultValue;
			}
			if(defaultValue != null)
			{
				mEditText.setText(defaultValue);
			}
		}

		@Override
		public ActInputInfo getInputInfo()
		{
			return mInputInfo;
		}
	}

	private abstract class Control extends RelativeLayout
	{
		public Control(Context context, AttributeSet attrs, int defStyle)
		{
			super(context, attrs, defStyle);
		}

		public Control(Context context, AttributeSet attrs)
		{
			super(context, attrs);
		}

		public Control(Context context)
		{
			super(context);
		}

		public abstract ActInputInfo getInputInfo();

		public abstract void setInputInfo(ActInputInfo inputInfo);

		public abstract String getValue();
	}
}
