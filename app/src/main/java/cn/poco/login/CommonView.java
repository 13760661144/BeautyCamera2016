package cn.poco.login;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.advanced.ImageUtils;
import cn.poco.home.home4.Home4Page;
import cn.poco.loginlibs.LoginUtils;
import cn.poco.loginlibs.info.CheckVerifyInfo;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.MakeBmpV2;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.WaitAnimDialog;
import my.beautyCamera.R;

import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;

/**
 * 填写验证码页面ui
 */
public class CommonView extends FrameLayout {

	public static final int STATE_REGISTER = 1;
	public static final int STATE_RESETPSW = 2;
	public static final int STATE_BINDPHONE = 3;
    private String tempUrl = "http://www.adnonstop.com/beauty_camera/wap/user_agreement.php?language=";
	public static String AGREE_URL = "";
	
    private ImageView m_cancelBtn;
    private LinearLayout centerLoginCon;

    //国家国际区域码
    private String  mAreaCodeNum="86";

    //输入手机号
    private RelativeLayout rlCenterTelLin;
    private RelativeLayout rlout1;
    private ImageView centerTelIcon;
    private TextView mAreaCode;
    private EditTextWithDel centerTelInput;
    private ImageView centerTelBottomLine;
    private ImageView mTocountryAreaCodeIcon;

    //输入验证码
    private RelativeLayout rlCenterVerificationcodeLin;
    private RelativeLayout rlout2;
    private ImageView centerVerificationcodeIcon;
    private EditTextWithDel centerVerificationcodeInput;
    private ImageView centerVerificationcodeDivide;
    public TextView centerVerificationcodeTx;
    private ImageView centerVerificationcodeBottomLine;

    //重设密码和注册按钮
    private TextView centerOkLogin;

    private TextView userAgress;

    private TextView mTitle;
    private boolean m_isHideTitle = false;
    
    private WaitAnimDialog mLoadingDlg = null;
    private ProgressDialog mProgress;
    private TextView mTipstext;
    private LinearLayout mTipsLinear;
    
    private onLoginLisener mOnLoginListener;
    private int mState = STATE_REGISTER;

    private String mTestCode;

    private CallBack mCallBack;
    
    private FullScreenDlg dialog;
    private Bitmap bkBmp;
    
    private String m_id;

    private boolean isCanClick = true;



	private String phoneNum, verityCode;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mControl = false;

    private LoginUtils.VerifyCodeType mType;

    private FrameLayout m_upFr;
    private FrameLayout m_downFr;
    private boolean m_once;
    private int mTopLayoutHeight;
    private FrameLayout m_upFr_1;

    public FrameLayout m_tipsFr;
    public ImageView m_tipsIcon;
    public TextView m_tipText;

    private ImageView loginBtn;
    private ImageView m_LoginLoading2;

    private LinearLayout m_tipsLayout;

    public CommonView(Context context)
    {
        super(context);
        initilize();
    }
    public void initilize(){
        ShareData.InitData(getContext());
        if(getResources().getConfiguration().locale.getCountry().equals("CN"))
        {
            AGREE_URL = tempUrl + "cn";
        }
        else
        {
            AGREE_URL = tempUrl + "en";
        }
        if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
        {
            this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath,null)));
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackground(new BitmapDrawable(getResources(), LoginOtherUtil.getTempBK(getContext())));
            } else {
                this.setBackgroundDrawable(new BitmapDrawable(getResources(), LoginOtherUtil.getTempBK(getContext())));
            }
        }
        mTopLayoutHeight = ShareData.PxToDpi_xhdpi(593);
    	String tag = TagMgr.GetTagValue(getContext(), Tags.NET_TAG_REG_TIP_ON_OFF);
    	if(tag != null && tag.equals("on"))
    	{
    		mControl = true;
    	}
    	else
    	{
    		mControl = false;
    	}
    	dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
        RelativeLayout.LayoutParams rlParams;
        LinearLayout.LayoutParams llParams;


        m_upFr = new FrameLayout(getContext());
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,mTopLayoutHeight);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_upFr.setLayoutParams(fl);
        this.addView(m_upFr);
        m_upFr.setBackgroundColor(Color.WHITE);
//        LoginAllAnim.SetBK(CommonView.this);

        m_upFr_1 = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,mTopLayoutHeight);
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        m_upFr_1.setLayoutParams(fl);
        m_upFr.addView(m_upFr_1);

        m_downFr = new FrameLayout(getContext())
        {
            @Override
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if(!m_once)
                {
                    m_once = true;
                    LoginAllAnim.getVCodePageAnim(m_upFr,m_downFr,m_upFr_1);
                }
            }
        };
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,ShareData.m_screenHeight - ShareData.PxToDpi_xhdpi(593));
        fl.gravity = Gravity.TOP | Gravity.LEFT;
        fl.topMargin = ShareData.PxToDpi_xhdpi(593);
        m_downFr.setLayoutParams(fl);
        this.addView(m_downFr);



        // 返回到上一层按钮
        FrameLayout topBar = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ShareData.PxToDpi_xhdpi(90));
        fl.gravity = Gravity.TOP;
        topBar.setLayoutParams(fl);
        m_upFr_1.addView(topBar);
        m_cancelBtn = new ImageView(getContext());
        m_cancelBtn.setImageResource(R.drawable.framework_back_btn);
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        m_cancelBtn.setLayoutParams(fl);
        m_cancelBtn.setOnClickListener(mOnClickListener);
        m_cancelBtn.setOnTouchListener(mOnTouchListener);
        topBar.addView(m_cancelBtn);
        ImageUtils.AddSkin(getContext(),m_cancelBtn);
        
        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = ShareData.PxToDpi_xhdpi(150);
        centerLoginCon = new LinearLayout(getContext());
        centerLoginCon.setGravity(Gravity.CENTER_HORIZONTAL);
        centerLoginCon.setOrientation(LinearLayout.VERTICAL);
        centerLoginCon.setId(R.id.login_commenview_centerlogincon);
        m_upFr_1.addView(centerLoginCon,fl);


        fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        fl.topMargin = ShareData.PxToDpi_xhdpi(78);
        mTitle = new TextView(this.getContext());
//        mTitle.setText("使用云相册需注册美人通行证");
        mTitle.setText(getContext().getResources().getString(R.string.commonview_bindphone));
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        mTitle.setTextColor(0xff333333);
        mTitle.setVisibility(GONE);
        m_upFr_1.addView(mTitle, fl);

        //手机号码
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, PxToDpi_xhdpi(92));
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.rightMargin = ShareData.PxToDpi_xhdpi(30);
        rlCenterTelLin = new RelativeLayout(getContext());
        centerLoginCon.addView(rlCenterTelLin, llParams);
        {
            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(190), LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rlout1=new RelativeLayout(getContext());
            rlCenterTelLin.addView(rlout1, rlParams);
            rlout1.setId(R.id.login_commenview_rlout1);
            {
                rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                centerTelIcon = new ImageView(getContext());
                centerTelIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                centerTelIcon.setImageResource(R.drawable.beauty_login_del_logo);
                rlout1.addView(centerTelIcon, rlParams);

                rlParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                mTocountryAreaCodeIcon = new ImageView(getContext());
                mTocountryAreaCodeIcon.setLayoutParams(rlParams);
                rlout1.addView(mTocountryAreaCodeIcon);
                mTocountryAreaCodeIcon.setImageResource(R.drawable.beautify_login_changecode_icon);
                mTocountryAreaCodeIcon.setId(R.id.login_loginpage_rlout1_changecode_icon);
                mTocountryAreaCodeIcon.setOnClickListener(mOnClickListener);

                rlParams = new RelativeLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                rlParams.addRule(RelativeLayout.LEFT_OF,R.id.login_loginpage_rlout1_changecode_icon);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                rlParams.rightMargin = ShareData.PxToDpi_xhdpi(4);
                mAreaCode = new TextView(getContext());
                rlout1.addView(mAreaCode, rlParams);
                mAreaCode.setText("+" + mAreaCodeNum);
                mAreaCode.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
                mAreaCode.setTextColor(0xff000000);
                mAreaCode.setOnClickListener(mOnClickListener);
            }

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            rlParams.leftMargin = PxToDpi_xhdpi(34);
            rlParams.addRule(RelativeLayout.RIGHT_OF,R.id.login_commenview_rlout1);
            centerTelInput = new EditTextWithDel(getContext(), -1, R.drawable.beauty_login_delete_logo);
            centerTelInput.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            centerTelInput.setBackgroundColor(0x00000000);
            centerTelInput.setPadding(0, 0, PxToDpi_xhdpi(5), 0);
            centerTelInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
            centerTelInput.setTextColor(0xff000000);
            centerTelInput.setHintTextColor(0xffb3b3b3);
            centerTelInput.setHint(getContext().getResources().getString(R.string.commonview_writephonenum));
            centerTelInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            centerTelInput.setSingleLine();
            centerTelInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            centerTelInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
            rlCenterTelLin.addView(centerTelInput, rlParams);
        }
        centerTelInput.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (v instanceof EditTextWithDel) {
                        EditTextWithDel editText = (EditTextWithDel) v;
                        editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                    }
                } else {
                    if (v instanceof EditTextWithDel) {
                        EditTextWithDel editText = (EditTextWithDel) v;
                        editText.setDrawable();
                    }

                }
            }
        });
        centerTelInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginBtn != null && centerVerificationcodeInput != null && centerOkLogin != null) {
                    if (s.toString().length() > 0 && centerVerificationcodeInput.getText().toString().length() == 6) {
                        loginBtn.setEnabled(true);
                        centerOkLogin.setTextColor(0xffffffff);
                    } else {
                        loginBtn.setEnabled(false);
                        centerOkLogin.setTextColor(0x7fffffff);
                    }
                }

                if((centerTelInput != null && centerTelInput.length() > 0) || (centerVerificationcodeInput != null && centerVerificationcodeInput.length() > 0))
                {
                    if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
                    {
                        m_tipsFr.setVisibility(INVISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (loginBtn != null && centerVerificationcodeInput != null && centerOkLogin != null) {
                    if (s.toString().length() > 0 && centerVerificationcodeInput.getText().toString().length() == 6) {
                        loginBtn.setEnabled(true);
                        centerOkLogin.setTextColor(0xffffffff);
                    } else {
                        loginBtn.setEnabled(false);
                        centerOkLogin.setTextColor(0x7fffffff);
                    }
                }
                if((centerTelInput != null && centerTelInput.length() > 0) || (centerVerificationcodeInput != null && centerVerificationcodeInput.length() > 0))
                {
                    if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
                    {
                        m_tipsFr.setVisibility(INVISIBLE);
                    }
                }
            }
        });

        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,1);
        centerTelBottomLine = new ImageView(getContext());
        centerTelBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
        centerTelBottomLine.setImageResource(R.drawable.beauty_login_line);
        centerLoginCon.addView(centerTelBottomLine, llParams);


        //输入验证码
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, PxToDpi_xhdpi(92));
        rlCenterVerificationcodeLin =  new RelativeLayout(getContext());
        centerLoginCon.addView(rlCenterVerificationcodeLin, llParams);
        {
            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(190), LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            rlParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
            rlout2=new RelativeLayout(getContext());
            rlCenterVerificationcodeLin.addView(rlout2, rlParams);
            rlout2.setId(R.id.login_commenview_rlout2);
            {
                rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
                rlParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                centerVerificationcodeIcon = new ImageView(getContext());
                centerVerificationcodeIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                centerVerificationcodeIcon.setImageResource(R.drawable.beauty_login_verificationcode);
                rlout2.addView(centerVerificationcodeIcon, rlParams);
            }

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_commenview_rlout2);
            rlParams.leftMargin = PxToDpi_xhdpi(34);
            centerVerificationcodeInput = new EditTextWithDel(getContext(), -1,-1);
            centerVerificationcodeInput.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            centerVerificationcodeInput.setBackgroundColor(0x00000000);
            centerVerificationcodeInput.setPadding(0, 0, PxToDpi_xhdpi(5), 0);
            centerVerificationcodeInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
            centerVerificationcodeInput.setTextColor(0xff000000);
            centerVerificationcodeInput.setHintTextColor(0xffb3b3b3);
            centerVerificationcodeInput.setHint(getContext().getResources().getString(R.string.commonview_verificationcode));
            centerVerificationcodeInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
            centerVerificationcodeInput.setSingleLine();
            centerVerificationcodeInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
            centerVerificationcodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
//            centerVerificationcodeInput.setTypeface(Typeface.MONOSPACE, 0);
            rlCenterVerificationcodeLin.addView(centerVerificationcodeInput, rlParams);
            centerVerificationcodeInput.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (v instanceof EditTextWithDel) {
                            EditTextWithDel editText = (EditTextWithDel) v;
                            editText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                        }
                    } else {
                        if (v instanceof EditTextWithDel) {
                            EditTextWithDel editText = (EditTextWithDel) v;
                            editText.setDrawable();
                        }

                    }
                }
            });
            centerVerificationcodeInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (loginBtn != null && centerTelInput != null && centerOkLogin != null) {
                        if (centerTelInput.getText().toString().length() > 0 && s.toString().length() == 6) {
                            loginBtn.setEnabled(true);
                            centerOkLogin.setTextColor(0xffffffff);
                        } else {
                            loginBtn.setEnabled(false);
                            centerOkLogin.setTextColor(0x7fffffff);
                        }
                    }

                    if((centerTelInput != null && centerTelInput.length() > 0) || (centerVerificationcodeInput != null && centerVerificationcodeInput.length() > 0))
                    {
                        if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
                        {
                            m_tipsFr.setVisibility(INVISIBLE);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (loginBtn != null && centerTelInput != null && centerOkLogin != null) {
                        if (centerTelInput.getText().toString().length() > 0 && s.toString().length() == 6) {
                            loginBtn.setEnabled(true);
                            centerOkLogin.setTextColor(0xffffffff);
                        } else {
                            loginBtn.setEnabled(false);
                            centerOkLogin.setTextColor(0x7fffffff);
                        }
                    }

                    if((centerTelInput != null && centerTelInput.length() > 0) || (centerVerificationcodeInput != null && centerVerificationcodeInput.length() > 0))
                    {
                        if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
                        {
                            m_tipsFr.setVisibility(INVISIBLE);
                        }
                    }
                }
            });

            rlParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rlParams.rightMargin = PxToDpi_xhdpi(205);
            centerVerificationcodeDivide = new ImageView(getContext());
            centerVerificationcodeDivide.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            centerVerificationcodeDivide.setImageResource(R.drawable.beauty_login_verificationcode_line);
            centerVerificationcodeDivide.setId(R.id.login_commenview_centerverificationcodedivide);
            rlCenterVerificationcodeLin.addView(centerVerificationcodeDivide, rlParams);




            rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(175), LayoutParams.WRAP_CONTENT);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
            rlParams.rightMargin = ShareData.PxToDpi_xhdpi(15);
            centerVerificationcodeTx = new TextView(getContext());
            centerVerificationcodeTx.setGravity(Gravity.CENTER);
            centerVerificationcodeTx.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
            centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.commonview_geting));
            centerVerificationcodeTx.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12);
            centerVerificationcodeTx.setId(R.id.login_commenview_centerverificationcodetx);
            centerVerificationcodeTx.setOnClickListener(mOnClickListener);
            centerVerificationcodeTx.setOnTouchListener(mOnTouchListener);
            rlCenterVerificationcodeLin.addView(centerVerificationcodeTx, rlParams);



            llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
            llParams.gravity = Gravity.CENTER_HORIZONTAL;
            centerVerificationcodeBottomLine = new ImageView(getContext());
            centerVerificationcodeBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
            centerVerificationcodeBottomLine.setImageResource(R.drawable.beauty_login_line);
            centerLoginCon.addView(centerVerificationcodeBottomLine, llParams);
        }


        m_tipsFr = new FrameLayout(getContext());
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.LEFT;
        ll.topMargin = ShareData.PxToDpi_xhdpi(14);
        ll.leftMargin = ShareData.PxToDpi_xhdpi(35);
        m_tipsFr.setLayoutParams(ll);
        m_tipsFr.setVisibility(INVISIBLE);
        centerLoginCon.addView(m_tipsFr);
        {
            m_tipsIcon = new ImageView(getContext());
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
            m_tipsIcon.setLayoutParams(fl);
            m_tipsFr.addView(m_tipsIcon);
            m_tipsIcon.setImageResource(R.drawable.beauify_login_tips_icon);

            m_tipText = new TextView(getContext());
            fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(35);
            m_tipText.setLayoutParams(fl);
            m_tipText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,12f);
            m_tipText.setTextColor(0xffff4b4b);
            m_tipText.setGravity(Gravity.CENTER);
            m_tipsFr.addView(m_tipText);
        }

//        llParams = new LinearLayout.LayoutParams(PxToDpi_xhdpi(541), ShareData.PxToDpi_xhdpi(80));
//        llParams.gravity = Gravity.CENTER_HORIZONTAL;
//        llParams.topMargin = ShareData.PxToDpi_xhdpi(30);
//        llParams.bottomMargin = PxToDpi_xhdpi(32);
//        centerOkLogin = new TextView(getContext());
//        centerOkLogin.setGravity(Gravity.CENTER);
//        centerOkLogin.setText("确定");
//        centerOkLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//        centerOkLogin.setTextColor(0x7fffffff);
//        centerOkLogin.setEnabled(false);
//        centerOkLogin.setBackgroundDrawable(LoginOtherUtil.makeDrawableForSkin(R.drawable.beauty_login_btn_disable_new,R.drawable.beauty_login_btn_normal1,R.drawable.beauty_login_btn_press1));
//        centerOkLogin.setOnClickListener(mOnClickListener);
//        centerLoginCon.addView(centerOkLogin,llParams);



        FrameLayout tempLoginBtn = new FrameLayout(getContext());
        llParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(541), ShareData.PxToDpi_xhdpi(80));
        llParams.topMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.bottomMargin = ShareData.PxToDpi_xhdpi(32);
//        loginBtn.setOnClickListener(mOnClickListener);
        centerLoginCon.addView(tempLoginBtn, llParams);

        loginBtn = new ImageView(getContext());
        loginBtn.setEnabled(false);
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        loginBtn.setLayoutParams(fl);
        tempLoginBtn.addView(loginBtn);
        loginBtn.setOnClickListener(mOnClickListener);
        loginBtn.setImageDrawable(LoginOtherUtil.makeDrawableForSkin(R.drawable.beauty_login_btn_disable_new,R.drawable.beauty_login_btn_normal1,R.drawable.beauty_login_btn_press1, getContext()));

        fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        centerOkLogin = new TextView(getContext());
        centerOkLogin.setGravity(Gravity.CENTER);
        centerOkLogin.setText(getContext().getResources().getString(R.string.commonview_sure));
        centerOkLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16f);
        centerOkLogin.setTextColor(0xffffffff);
        tempLoginBtn.addView(centerOkLogin, fl);

        m_LoginLoading2 = new ImageView(getContext());
        m_LoginLoading2.setScaleType(ImageView.ScaleType.CENTER);
        fl = new LayoutParams(ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(50));
        fl.gravity = Gravity.CENTER;
        m_LoginLoading2.setImageResource(R.drawable.beauty_login_loading_logo);
        m_LoginLoading2.setVisibility(GONE);
        tempLoginBtn.addView(m_LoginLoading2, fl);

        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llParams.gravity = Gravity.CENTER_HORIZONTAL;
        userAgress = new TextView(getContext());
        String str = getContext().getResources().getString(R.string.commonview_agreeprotocol1) + getContext().getResources().getString(R.string.commonview_agreeprotocol2);
        int str1Length = getContext().getResources().getString(R.string.commonview_agreeprotocol1).length();
        SpannableString span = new SpannableString(str);
        span.setSpan(new ForegroundColorSpan(0xffa4a0a0), 0, str1Length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(ImageUtils.GetSkinColor(0xffe75887)), str1Length, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new AbsoluteSizeSpan(10,true), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), str1Length, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        userAgress.setText(span);
//        userAgress.setPadding(PxToDpi_xhdpi(5), PxToDpi_xhdpi(5), PxToDpi_xhdpi(5), PxToDpi_xhdpi(5));
        userAgress.setOnClickListener(mOnClickListener);
        centerLoginCon.addView(userAgress,llParams);


        m_tipsLayout = new LinearLayout(getContext());
        m_tipsLayout.setOrientation(LinearLayout.VERTICAL);
        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        fl.bottomMargin = ShareData.PxToDpi_xhdpi(60);
        m_tipsLayout.setLayoutParams(fl);
        m_downFr.addView(m_tipsLayout);

        mTipstext = new TextView(getContext());
        mTipstext.setText(getContext().getResources().getString(R.string.commonview_tips));
        mTipstext.setTextColor(0xff666666);
        mTipstext.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11f);
        ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.gravity = Gravity.CENTER_HORIZONTAL;
        mTipstext.setLayoutParams(ll);
        m_tipsLayout.addView(mTipstext);
        
        mTipsLinear = new LinearLayout(getContext());
        ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.topMargin = ShareData.PxToDpi_xhdpi(18);
        ll.gravity = Gravity.CENTER_HORIZONTAL;
        mTipsLinear.setLayoutParams(ll);
        m_tipsLayout.addView(mTipsLinear);
        {
        	ImageView beauty = new ImageView(getContext());
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        	beauty.setImageResource(R.drawable.beauty_login_meiren_icon);
        	mTipsLinear.addView(beauty,ll);
        	
        	ImageView jianping = new ImageView(getContext());
        	ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        	ll.leftMargin = PxToDpi_xhdpi(20);
        	jianping.setImageResource(R.drawable.beauty_login_jianping_icon);
        	mTipsLinear.addView(jianping,ll);
        	
        	ImageView jianke = new ImageView(getContext());
        	ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        	ll.leftMargin = PxToDpi_xhdpi(20);
        	jianke.setImageResource(R.drawable.beauty_login_jianke_icon);
        	mTipsLinear.addView(jianke,ll);

            ImageView yinxiang = new ImageView(getContext());
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.leftMargin = PxToDpi_xhdpi(20);
            yinxiang.setImageResource(R.drawable.beauty_login_yinxiang_icon);
            mTipsLinear.addView(yinxiang,ll);

            ImageView zaiyiqi = new ImageView(getContext());
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.leftMargin = PxToDpi_xhdpi(20);
            zaiyiqi.setImageResource(R.drawable.beauty_login_zaiyiqi_icon);
            mTipsLinear.addView(zaiyiqi,ll);

            ImageView xingnan = new ImageView(getContext());
            ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.leftMargin = PxToDpi_xhdpi(20);
            xingnan.setImageResource(R.drawable.beauty_login_xingnan_icon);
            mTipsLinear.addView(xingnan,ll);
        	
        	ImageView hechengqi = new ImageView(getContext());
        	ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        	ll.leftMargin = PxToDpi_xhdpi(20);
        	hechengqi.setImageResource(R.drawable.beauty_login_hechengqi_icon);
        	mTipsLinear.addView(hechengqi,ll);
        	
        	ImageView poco = new ImageView(getContext());
        	ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        	ll.leftMargin = PxToDpi_xhdpi(20);
        	poco.setImageResource(R.drawable.beauty_login_poco_icon);
        	mTipsLinear.addView(poco,ll);
        }

        this.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideKeyboard();
			}
		});
    }
    
    public void SetBackgroundBmp(Bitmap bmp)
    {
    	if(bmp != null)
    	{
    		bkBmp = bmp;
            bkBmp = MakeBmpV2.CreateFixBitmapV2(bkBmp,0,MakeBmpV2.FLIP_NONE,0,ShareData.m_screenWidth,ShareData.m_screenHeight - mTopLayoutHeight, Bitmap.Config.ARGB_8888);
    		this.setBackgroundDrawable(new BitmapDrawable(bkBmp));
    	}
    	else
    	{
    		this.setBackgroundColor(Color.WHITE);
    	}
    }
    
    public void setState(int state){
    	this.mState = state;
    	setViewForState(state);
        if(state == STATE_REGISTER)
        {
            mType =  LoginUtils.VerifyCodeType.register;
        }
        else if(state == STATE_RESETPSW)
        {
            mType =  LoginUtils.VerifyCodeType.find;
        }
        else
        {
            mType =  LoginUtils.VerifyCodeType.bind_mobile;
        }
    }
    
    public void setState(int state,boolean isHide)
    {
    	this.mState = state;
    	m_isHideTitle = isHide;
    	setViewForState(state);
    }
    

    private void setViewForState(int state) {
		switch (state) {
		case STATE_REGISTER:
			 if(mControl)
		        {
//		        	mTipstext.setVisibility(VISIBLE);
//		        	mTipsLinear.setVisibility(VISIBLE);
                    if(m_tipsLayout != null)
                    {
                        m_tipsLayout.setVisibility(VISIBLE);
                    }
		        }
		        else
		        {
//		        	mTipstext.setVisibility(GONE);
//		        	mTipsLinear.setVisibility(GONE);
                    if(m_tipsLayout != null)
                    {
                        m_tipsLayout.setVisibility(GONE);
                    }
		        }
			break;
		case STATE_BINDPHONE:
            if(mTitle != null && !m_isHideTitle)
            {
                mTitle.setVisibility(VISIBLE);
            }
            if(userAgress != null)
            {
                userAgress.setVisibility(VISIBLE);
            }
            break;
		case STATE_RESETPSW:
			if(userAgress != null)
			{
				userAgress.setVisibility(GONE);
			}

            if(m_tipsLayout != null)
            {
                m_tipsLayout.setVisibility(GONE);
            }
			break;
		default:
			break;
		}

        if(m_tipsLayout != null && m_tipsLayout.getVisibility() == VISIBLE)
        {
            showAnim(m_tipsLayout,ShareData.PxToDpi_xhdpi(220),0);
        }
	}

    private void showAnim(View view,float startY,float endY)
    {
        if(view != null)
        {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"translationY",startY,endY);
            objectAnimator.setDuration(400);
            objectAnimator.start();
        }
    }

	@SuppressLint("NewApi")
	private OnTouchListener mOnTouchListener=new OnTouchListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@SuppressLint("NewApi")
		@Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v == m_cancelBtn) {
                    m_cancelBtn.setAlpha(0.5f);
                }else if (v == centerVerificationcodeTx) {
                    centerVerificationcodeTx.setAlpha(0.5f);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v == m_cancelBtn) {
                    m_cancelBtn.setAlpha(1.0f);
                } else if (v == centerVerificationcodeTx) {
                    centerVerificationcodeTx.setAlpha(1.0f);
                }
            }
            return false;
        }
    };
    
    public void SetVerifyCode(String verifyCode){
    	this.mTestCode = verifyCode;
    	this.verityCode = verifyCode;
    }
    
    public void SetCallback(CallBack cb){
    	this.mCallBack = cb;
    }
    
    public void setIsHideTitle(boolean isHide)
    {
    	m_isHideTitle = isHide;
        if(m_isHideTitle)
        {
            mTitle.setVisibility(GONE);
        }
    }


    public void setPhoneNum(String phoneNum) {
        centerTelInput.setText(phoneNum);
    }
    
    
    
    public void setOnLoginLisener(onLoginLisener onLoginlisener){
    	this.mOnLoginListener = onLoginlisener;
    }

    private NoDoubleClickListener mOnClickListener = new NoDoubleClickListener(){
        @Override
        public void onNoDoubleClick(View v) {
            if(v == m_cancelBtn){
                hideKeyboard();
                if(mCallBack != null)
                {
                    mCallBack.onBack();
                }
            }else if(v == mTocountryAreaCodeIcon || v == mAreaCode)
            {
            	hideKeyboard();
                if(mCallBack != null)
                {
                    mCallBack.onChooseCountry();
                }
            }
            else if (v == loginBtn) {
            	if(isCanClick)
            	{
                    if(mCallBack != null)
                    {
                        mCallBack.onOkCallBackForSendTongJi();
                    }
                    hideKeyboard();
                    String phone = centerTelInput.getText().toString();
                    phone = phone.trim();
                    phone.replace(" ","");
    //                      if(phone.length() != 11){
    ////                          TipsDialog tipCancelOkPage = new TipsDialog(getContext(),null , "请输入手机号！", null, "确定", listener);
    ////                          showDialog(tipCancelOkPage);
    //                          m_tipsFr.setVisibility(VISIBLE);
    //                          m_tipText.setText("请输入正确的11位手机号");
    //                          return;
    //                      }
                    String vcode = centerVerificationcodeInput.getText().toString();
                    if (vcode.length() == 0) {
                        Toast toast = Toast.makeText(getContext(),getContext().getResources().getString(R.string.commonview_writeverificationcode), Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CLIP_VERTICAL, 0, 0);
                        toast.show();
                        return;
                    }
                    checkVerityCode(phone, centerVerificationcodeInput.getText().toString(),mAreaCodeNum);
            	}
            }
            else if(v == centerVerificationcodeTx){
                hideKeyboard();
                 if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
                    {
                        m_tipsFr.setVisibility(INVISIBLE);
                    }
                if(isCanClick)
                {
                    if(isTimerDone){
                        String phone = centerTelInput.getText().toString();
                        phone = phone.trim();
                        phone.replace(" ","");
                        if(phone.length() == 0){
//                            TipsDialog tipCancelOkPage = new TipsDialog(getContext(),null , "请输入手机号！", null, "确定", listener);
//                            showDialog(tipCancelOkPage);
                            m_tipsFr.setVisibility(VISIBLE);
                            m_tipText.setText(getContext().getResources().getString(R.string.commonview_writephonenumtips));
                            return;
                        }
                        isTimerDone = false;
                        centerVerificationcodeTx.setFocusable(false);
                        if(mCallBack != null)
                        {
                        	mCallBack.onClickGetVerifyCode(mAreaCodeNum,phone);
                        }
//                        getVerityCode(phone,mAreaCodeNum);
                    }
                }
            }
            else if(v == userAgress)
            {
//                CommonUtils.OpenBrowser(getContext(), AGREE_URL);
                hideKeyboard();
                if(mCallBack != null)
                {
                    mCallBack.toAgreeWebView();
                }
            }
        }
    };
    
    public boolean isTimerDone = true;
    public CountDownTimer mtimer = new CountDownTimer(60000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            isTimerDone = false;
            centerVerificationcodeTx.setTextColor(0xff949494);
            centerVerificationcodeTx.setText(millisUntilFinished / 1000 + getContext().getResources().getString(R.string.commonview_regettime));
        }

        @Override
        public void onFinish() {
            isTimerDone = true;
            centerVerificationcodeTx.setEnabled(true);
            centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.commonview_reget));
            centerVerificationcodeTx.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
        }
    };

    public void SetVCodeText(String text)
    {
        centerVerificationcodeInput.setText(text);
    }

	/**
     * 校验验证码
     * @param phone
     * @param vcode
     */
    private void checkVerityCode(final String phone, final String vcode, final String areaCode){
        if(mLoadingDlg != null) {
            mLoadingDlg.dismiss();
            mLoadingDlg = null;
        }
        isCanClick = false;
        SetLoginBtnByState(true);
//        new Thread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                final CheckVerifyInfo verifyInfo =  LoginUtils.checkVerifyCode(mAreaCodeNum, phone, vcode, mType, AppInterface.GetInstance(getContext()));
//                ((Activity)getContext()).runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        isCanClick = true;
//                        if(verifyInfo !=null){
//                        if(verifyInfo.mCode==0 && verifyInfo.mProtocolCode == 200)
//                            {
//                                if(verifyInfo.mCheckResult)
//                                {
//                                    phoneNum = phone;
//                                    if(mCallBack != null)
//                                    {
//                                        mCallBack.onComfirmBtn(phoneNum, vcode, mAreaCodeNum);
//                                    }
//                                }else
//                                {
////                                    LoginOtherUtil.showToast("验证码不正确");
//                                    showErrorTips(getContext().getResources().getString(R.string.commonview_verificationcodeerror));
//                                    SetLoginBtnByState(false);
//                                }
//                            }
//                            else
//                            {
////                                LoginOtherUtil.showToast("验证失败");
//                                showErrorTips(getContext().getResources().getString(R.string.commonview_verificationcodeerror));
//                                SetLoginBtnByState(false);
//                            }
//
//                        }else{
////                            LoginOtherUtil.showToast("网络异常");
//                            showErrorTips(getContext().getResources().getString(R.string.commonview_networkerror));
//                            SetLoginBtnByState(false);
//                        }
//                    }
//                });
//            }
//        }).start();

        LoginUtils2.checkVerifyCode(mAreaCodeNum, phone, vcode, mType, new HttpResponseCallback()
        {
            @Override
            public void response(Object object)
            {
                isCanClick = true;
                if(object == null)
                {
                    showErrorTips(getContext().getResources().getString(R.string.commonview_networkerror));
                    SetLoginBtnByState(false);
                    return;
                }
                CheckVerifyInfo verifyInfo = (CheckVerifyInfo)object;
                if(verifyInfo.mCode == 0 && verifyInfo.mProtocolCode == 200)
                {
                    if(verifyInfo.mCheckResult)
                    {
                        phoneNum = phone;
                        if(mCallBack != null) mCallBack.onComfirmBtn(phoneNum, vcode, mAreaCodeNum);
                    }
                    else
                    {
                        showErrorTips(getContext().getResources().getString(R.string.commonview_verificationcodeerror));
                        SetLoginBtnByState(false);
                    }
                }
                else
                {
                    if(verifyInfo.mMsg != null && verifyInfo.mMsg.length() > 0) showErrorTips(verifyInfo.mMsg);
                    else showErrorTips(getContext().getResources().getString(R.string.commonview_verificationcodeerror));
                    SetLoginBtnByState(false);
                }
            }
        });
    }

    public void setCountryAndCodeNum(String country,String code)
    {
    	mAreaCode.setText("+" + code);
    	mAreaCodeNum = code;
    }
    
    public void reSetData()
    {
        if(mtimer != null)
    	mtimer.cancel();
		centerVerificationcodeTx.setText(getContext().getResources().getString(R.string.commonview_reget));
        centerVerificationcodeTx.setTextColor(ImageUtils.GetSkinColor(0xffe75887));
		isTimerDone = true;
		centerVerificationcodeInput.setText(null);
		centerOkLogin.setEnabled(false);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
    }

    @SuppressLint("NewApi")
    public void onClose() {
        if(mtimer != null){
            mtimer.cancel();
        }
        mtimer = null;
        this.setBackgroundDrawable(null);
        
        if(dialog != null)
        {
        	dialog.dismiss();
        	dialog = null;
        }
        if(bkBmp != null)
        {
        	bkBmp = null;
        }

        if(mLoadingDlg != null)
        {
            mLoadingDlg.dismiss();
            mLoadingDlg = null;
        }
    }


    public interface CallBack{
    	public void onClickGetVerifyCode(String zoneNum, String phoneNum);
    	public void onComfirmBtn(String phoneNum, String verityCode, String areaCodeNum);
        public void onChooseCountry();
        public void onBack();
        public void toAgreeWebView();
        public void onOkCallBackForSendTongJi();
    }

    public void showErrorTips(String errorTips)
    {
        if(errorTips != null && errorTips.length() > 0)
        {
            if(m_tipsFr != null && m_tipText != null)
            {
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(errorTips);
            }
        }
    }

    public void SetLoginBtnByState(boolean isAction)
    {
        if(isAction)
        {
            centerVerificationcodeInput.setEnabled(false);
            centerTelInput.setEnabled(false);
            centerTelInput.clearFocus();
            centerVerificationcodeInput.clearFocus();
            if (m_LoginLoading2 != null) {
                m_LoginLoading2.setVisibility(VISIBLE);
                final RotateAnimation animation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF,
                        0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(500);
                animation.setRepeatCount(-1);
                m_LoginLoading2.startAnimation(animation);
            }
            if (centerOkLogin != null) {
                centerOkLogin.setVisibility(GONE);
            }
        }
        else
        {
            centerVerificationcodeInput.setEnabled(true);
            centerTelInput.setEnabled(true);
            if (m_LoginLoading2 != null) {
                m_LoginLoading2.setVisibility(GONE);
                m_LoginLoading2.clearAnimation();
                m_LoginLoading2.setAnimation(null);
            }
            if (centerOkLogin != null) {
                centerOkLogin.setVisibility(VISIBLE);
            }
        }
    }
}
