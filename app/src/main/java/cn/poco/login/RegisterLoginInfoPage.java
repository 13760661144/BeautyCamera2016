package cn.poco.login;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adnonstop.beautyaccount.RequestParam;

import java.io.File;
import java.util.HashMap;
import java.util.regex.Pattern;

import cn.poco.advanced.ImageUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.FileCacheMgr;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.home.home4.Home4Page;
import cn.poco.login.site.RegisterLoginInfoPageSite;
import cn.poco.loginlibs.info.LoginInfo;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.share.ImageButton;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.Utils;
import my.beautyCamera.R;

/**
 * 注册填写详细信息页面
 */
public class RegisterLoginInfoPage extends IPage {
    //private static final String TAG = "注册填写资料";

    private int userHeadIconWidth = ShareData.PxToDpi_xhdpi(160);

    private ImageView m_cancelBtn;
    private Bitmap bkBmp;


    //圆形头像
    private LinearLayout userHeadCon;
    private FrameLayout userHeadFrame;
    private ImageView userHeadLoadImageView;
    private TextView userHeadTv;

    private ImageView userHeadImg;

    private LinearLayout centerLoginCon;
    //输入昵称
    private RelativeLayout centerNickCon;
    private ImageView centerNickIcon;
    private EditTextWithDel centerNickInput;
    private ImageView centerNickBottomLine;
    //输入密码
    private RelativeLayout centerPswCon;
    private ImageView centerPswIcon;
    private EditTextWithDel centerPswInput;

    private ImageView centerCommitPswBottomLine;

    private TextView centerOkLogin;

    private ImageButton mPswShowIcon;
    private boolean isHidePsw = true;

    public String phoneNum, verityCode, nickName;
    public String mAreaCodeNum = "86";

    private String iconUrl = null;
    private boolean iconUpload = false;
    private String userId = "";

    private ProgressDialog mProgress;
    private LoginInfo loginInfo;

    private FullScreenDlg dialog;
    private int curCursor = 0;

    //匹配非表情符号的正则表达式
    private final String reg = "^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";

    private Pattern pattern = Pattern.compile(reg);

    protected RegisterLoginInfoPageSite mSite;

    protected LoginPageInfo mLoginPageInfo;

    protected FrameLayout m_upFr;
    protected FrameLayout m_downFr;
    protected int m_upFrHeight;
    protected boolean m_once;
    protected FrameLayout m_upFr_1;


    private FrameLayout m_tipsFr;
    private ImageView m_tipsIcon;
    private TextView m_tipText;
    private final String m_pswtips = getContext().getResources().getString(R.string.resgisterpage_pswtips);
    private final String m_pswEmptytips = getContext().getResources().getString(R.string.resgisterpage_pswemptytips);
    private final String m_pswtips2 = getContext().getResources().getString(R.string.loginpage_passwordtips3);
    private final String m_uploadHeadbmptips = getContext().getResources().getString(R.string.resgisterpage_uploadhead);
    private final String m_nameEmptytips = getContext().getResources().getString(R.string.resgisterpage_nicknametips);

    public RegisterLoginInfoPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (RegisterLoginInfoPageSite) site;
        initilize();
          MyBeautyStat.onPageStartByRes(R.string.个人中心_登录注册_创建账号_步骤二);
                TongJiUtils.onPageStart(getContext(), R.string.注册);
    }

    /**
     * @param params img：背景图片路径
     *               info:LoginPageInfo
     *               mode:REGISTER OR THIRDBINDPHONE
     */
    @Override
    public void SetData(HashMap<String, Object> params) {
        if (params != null) {
//            if(params.get("img") != null)
//            {
//                SetBk(Utils.DecodeFile((String) params.get("img"),null));
//            }
//            else
//            {
//                SetBk(null);
//            }
            if(params.get("info") != null)
            {
                setPhoneNumAndVerityCode(((LoginPageInfo) params.get("info")).m_phoneNum, ((LoginPageInfo) params.get("info")).m_verityCode, ((LoginPageInfo) params.get("info")).m_areaCodeNum);
            }
        }

        if(!TextUtils.isEmpty(Home4Page.s_maskBmpPath))
        {
            this.setBackgroundDrawable(new BitmapDrawable(cn.poco.imagecore.Utils.DecodeFile(Home4Page.s_maskBmpPath,null)));
        }
        else
        {
            this.setBackground(new BitmapDrawable(LoginOtherUtil.getTempBK(getContext())));
        }

    }

    @Override
    public void onBack() {
        mSite.onBack(getContext());
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public void initilize() {
        m_upFrHeight = ShareData.PxToDpi_xhdpi(736);
        dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
        RelativeLayout.LayoutParams rlParams;
        LinearLayout.LayoutParams llParams;
        LayoutParams fParams;

        FrameLayout.LayoutParams fl;

        m_upFr = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,m_upFrHeight);
        fl.gravity = Gravity.TOP;
        m_upFr.setLayoutParams(fl);
        this.addView(m_upFr);
        m_upFr.setBackgroundColor(Color.WHITE);
//        LoginAllAnim.SetBK(RegisterLoginInfoPage.this);

        m_upFr_1 = new FrameLayout(getContext());
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,m_upFrHeight);
        fl.gravity = Gravity.TOP;
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
                    LoginAllAnim.registerInfoPageAnim(m_upFr,m_downFr,m_upFr_1);
                }
            }
        };
        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,ShareData.m_screenHeight - m_upFrHeight);
        fl.gravity = Gravity.TOP;
        fl.topMargin = m_upFrHeight;
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

        fl = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(100);
        userHeadCon = new LinearLayout(getContext());
        userHeadCon.setId(R.id.login_registerinfopage_userheadcon);
        userHeadCon.setOrientation(LinearLayout.VERTICAL);
        userHeadCon.setGravity(Gravity.CENTER_HORIZONTAL);
        m_upFr_1.addView(userHeadCon, fl);

        llParams = new LinearLayout.LayoutParams(userHeadIconWidth, userHeadIconWidth);
        userHeadFrame = new FrameLayout(getContext());
        userHeadCon.addView(userHeadFrame, llParams);


        fParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        userHeadImg = new ImageView(getContext());
        userHeadImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Bitmap bg = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_register_headicon);
        userHeadImg.setImageBitmap(ImageUtils.MakeHeadBmp(bg,bg.getWidth(),0,0));
        userHeadImg.setOnClickListener(mOnClickListener);
        userHeadImg.setOnTouchListener(mOnTouchListener);
        userHeadFrame.addView(userHeadImg, fParams);

//        fParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//        fParams.gravity = Gravity.CENTER;
//        userHeadImg1 = new ImageView(getContext());
//        userHeadImg1.setScaleType(ImageView.ScaleType.FIT_CENTER);
////        userHeadImg1.setImageResource(R.drawable.beauty_login_head_logo1);
//        userHeadImg1.setImageResource(R.drawable.beautify_register_headicon);
//        userHeadFrame.addView(userHeadImg1, fParams);


        fParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        userHeadLoadImageView = new ImageView(getContext());
        userHeadLoadImageView.setVisibility(View.GONE);
        userHeadLoadImageView.setImageResource(R.drawable.beauty_login_loading_logo);
        userHeadFrame.addView(userHeadLoadImageView, fParams);

        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llParams.topMargin = ShareData.PxToDpi_xhdpi(14);
        userHeadTv = new TextView(getContext());
        userHeadTv.setGravity(Gravity.CENTER);
        userHeadTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        userHeadTv.setTextColor(0xff9C9C9C);
        userHeadTv.setText(getContext().getResources().getString(R.string.resgisterpage_setheadicon));
        userHeadCon.addView(userHeadTv, llParams);

        fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        fl.topMargin = ShareData.PxToDpi_xhdpi(328);

        centerLoginCon = new LinearLayout(getContext());
        centerLoginCon.setGravity(Gravity.CENTER_HORIZONTAL);
        centerLoginCon.setOrientation(LinearLayout.VERTICAL);
        m_upFr_1.addView(centerLoginCon, fl);

        //昵称
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(92));
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.rightMargin = ShareData.PxToDpi_xhdpi(27);
        centerNickCon = new RelativeLayout(getContext());
        centerLoginCon.addView(centerNickCon, llParams);

        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        centerNickIcon = new ImageView(getContext());
        centerNickIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        centerNickIcon.setImageResource(R.drawable.beauty_login_name_logo);
        centerNickCon.addView(centerNickIcon, rlParams);
        centerNickIcon.setId(R.id.login_registerinfopage_centernickicon);

        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_registerinfopage_centernickicon);
        rlParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        centerNickInput = new EditTextWithDel(getContext(), -1, R.drawable.beauty_login_delete_logo);
        centerNickInput.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        centerNickInput.setBackgroundColor(0x00000000);
        centerNickInput.setPadding(0, 0, ShareData.PxToDpi_xhdpi(5), 0);
        centerNickInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
        centerNickInput.setTextColor(0xff000000);
        centerNickInput.setHintTextColor(0xffb2b2b2);
        centerNickInput.setHint(getContext().getResources().getString(R.string.resgisterpage_nickname));
        centerNickInput.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        centerNickInput.setSingleLine();
        MyTextUtils.setupLengthFilter(centerNickInput, getContext(), 16, true);
        centerNickInput.setInputType(InputType.TYPE_CLASS_TEXT);
//        centerNickInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        centerNickCon.addView(centerNickInput, rlParams);
        centerNickInput.setOnFocusChangeListener(new OnFocusChangeListener() {
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
        centerNickInput.addTextChangedListener(textWatcher);

        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        centerNickBottomLine = new ImageView(getContext());
        centerNickBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
        centerNickBottomLine.setImageResource(R.drawable.beauty_login_line);
        centerLoginCon.addView(centerNickBottomLine, rlParams);

        //输入密码框
        llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(92));
        llParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        llParams.rightMargin = ShareData.PxToDpi_xhdpi(27);
        centerPswCon = new RelativeLayout(getContext());
        centerLoginCon.addView(centerPswCon, llParams);

        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        centerPswIcon = new ImageView(getContext());
        centerPswIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        centerPswIcon.setImageResource(R.drawable.beauty_login_setpsw_logo);
        centerPswCon.addView(centerPswIcon, rlParams);
        centerPswIcon.setId(R.id.login_registerinfopage_centerpswicon);

        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.login_registerinfopage_centerpswicon);
        rlParams.leftMargin = ShareData.PxToDpi_xhdpi(30);
        rlParams.rightMargin = ShareData.PxToDpi_xhdpi(48);
        centerPswInput = new EditTextWithDel(getContext(), -1, R.drawable.beauty_login_delete_logo);
        centerPswInput.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        centerPswInput.setBackgroundColor(0x00000000);
        centerPswInput.setPadding(0, 0, ShareData.PxToDpi_xhdpi(5), 0);
        centerPswInput.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15f);
        centerPswInput.setTextColor(0xff000000);
        centerPswInput.setHintTextColor(0xffb2b2b2);
        centerPswInput.setHint(getContext().getResources().getString(R.string.resgisterpage_setpsw));
        centerPswInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        centerPswInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        centerPswInput.setSingleLine();
        centerPswInput.setKeyListener(new NumberKeyListener() {
            @Override
            protected char[] getAcceptedChars() {
                String passwordRuleStr = getContext().getString(R.string.rule_password);
                return passwordRuleStr.toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_PASSWORD;
            }
        });
        centerPswInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        centerPswInput.setTypeface(Typeface.MONOSPACE, 0);
//        centerPswInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        centerPswCon.addView(centerPswInput, rlParams);
        centerPswInput.setOnFocusChangeListener(new OnFocusChangeListener() {
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
        centerPswInput.addTextChangedListener(textWatcher);

        mPswShowIcon = new ImageButton(getContext());
        rlParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(50));
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rlParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rlParams.rightMargin = ShareData.PxToDpi_xhdpi(5);
        mPswShowIcon.setButtonImage(R.drawable.beauty_login_hidepsw, R.drawable.beauty_login_hidepsw);
        centerPswCon.addView(mPswShowIcon, rlParams);
        mPswShowIcon.setOnClickListener(mOnClickListener);


        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        centerCommitPswBottomLine = new ImageView(getContext());
        centerCommitPswBottomLine.setScaleType(ImageView.ScaleType.FIT_XY);
        centerCommitPswBottomLine.setImageResource(R.drawable.beauty_login_line);
        centerLoginCon.addView(centerCommitPswBottomLine, rlParams);


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

        //完成按钮
        llParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(540), ShareData.PxToDpi_xhdpi(80));
        llParams.topMargin = ShareData.PxToDpi_xhdpi(35);
        centerOkLogin = new TextView(getContext());
        centerOkLogin.setGravity(Gravity.CENTER);
        centerOkLogin.setText(getContext().getResources().getString(R.string.resgisterpage_finish));
        centerOkLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        centerOkLogin.setTextColor(0xffffffff);
        centerOkLogin.setBackgroundDrawable(LoginOtherUtil.makeDrawableForSkin(R.drawable.beauty_login_btn_disable_new,R.drawable.beauty_login_btn_normal1,R.drawable.beauty_login_btn_press1,getContext()));
        centerOkLogin.setOnClickListener(mOnClickListener);
        centerLoginCon.addView(centerOkLogin, llParams);
        centerOkLogin.setEnabled(false);

        RegisterLoginInfoPage.this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                hideKeyboard();
            }
        });
    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(centerOkLogin != null)
            {
                if(centerNickInput != null && centerNickInput.getText() != null && centerNickInput.getText().toString().length() > 0 && centerPswInput != null && centerPswInput.getText() != null && centerPswInput.getText().toString().length() > 0)
                {
                    centerOkLogin.setEnabled(true);
                }
                else
                {
                    centerOkLogin.setEnabled(false);
                }
            }

            if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
            {
                if(centerPswInput != null && centerPswInput.getText().toString().length() > 0 && m_tipText.getText().toString().equals(m_pswEmptytips))
                {
                    m_tipsFr.setVisibility(INVISIBLE);
                    return;
                }

                if(centerPswInput != null && centerPswInput.getText().toString().length() >= 8 && centerPswInput.getText().toString().length() <= 20 && m_tipText.getText().toString().equals(m_pswtips))
                {
                    m_tipsFr.setVisibility(INVISIBLE);
                    return;
                }

                if(centerNickInput != null && centerNickInput.getText().toString().length() > 0 && m_tipText.getText().toString().equals(m_nameEmptytips))
                {
                    m_tipsFr.setVisibility(INVISIBLE);
                    return;
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(centerOkLogin != null)
            {
                if(centerNickInput != null && centerNickInput.getText() != null && centerNickInput.getText().toString().length() > 0 && centerPswInput != null && centerPswInput.getText() != null && centerPswInput.getText().toString().length() > 0)
                {
                    centerOkLogin.setEnabled(true);
                }
                else
                {
                    centerOkLogin.setEnabled(false);
                }
            }

            if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE)
            {
                if(centerPswInput != null && centerPswInput.getText().toString().length() > 0 && m_tipText.getText().toString().equals(m_pswEmptytips))
                {
                    m_tipsFr.setVisibility(INVISIBLE);
                    return;
                }

                if(centerPswInput != null && centerPswInput.getText().toString().length() >= 8 && centerPswInput.getText().toString().length() <= 20 && m_tipText.getText().toString().equals(m_pswtips))
                {
                    m_tipsFr.setVisibility(INVISIBLE);
                    return;
                }

                if(centerNickInput != null && centerNickInput.getText().toString().length() > 0 && m_tipText.getText().toString().equals(m_nameEmptytips))
                {
                    m_tipsFr.setVisibility(INVISIBLE);
                    return;
                }
            }

        }
    };

    protected void setPhoneNumAndVerityCode(String phoneNum, String verityCode, String mAreaCode) {
        this.phoneNum = phoneNum;
        this.verityCode = verityCode;
        if(mLoginPageInfo == null)
        {
            mLoginPageInfo = new LoginPageInfo();
        }
        mLoginPageInfo.m_phoneNum = phoneNum;
        mLoginPageInfo.m_verityCode = verityCode;
        if (!TextUtils.isEmpty(mAreaCode)) {
            this.mAreaCodeNum = mAreaCode;
            mLoginPageInfo.m_areaCodeNum = mAreaCode;
        }
    }

    protected void SetBk(Bitmap bmp) {
        if (bmp != null) {
            bkBmp = bmp;
            m_downFr.setBackgroundDrawable(new BitmapDrawable(bmp));
        } else {
            m_downFr.setBackgroundColor(Color.WHITE);
        }
    }

    //截图路径
    private String getScreenBmp()
    {
        RegisterLoginInfoPage.this.invalidate();
        Bitmap out = Bitmap.createBitmap(ShareData.m_screenWidth, ShareData.m_screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        RegisterLoginInfoPage.this.draw(canvas);
        out = LoginOtherUtil.MakeBkBmp2(out,ShareData.m_screenWidth,ShareData.m_screenHeight,0x4cffffff);
        String path = FileCacheMgr.GetLinePath();
        if(Utils.SaveTempImg(out, path))
        {
            return path;
        }
        else
        {
            return null;
        }

    }

    private NoDoubleClickListener mOnClickListener = new NoDoubleClickListener() {
        @Override
        public void onNoDoubleClick(View v) {
            if (v == userHeadImg) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤二_设置信息_设置头像);
                hideKeyboard();
                HashMap<String, Object> params = new HashMap<>();
                    if(loginInfo != null)
                    {
                        params.put("info",mLoginPageInfo);
                        params.put("userId",loginInfo.mUserId);
                        params.put("tocken",loginInfo.mAccessToken);
                    }
                    else
                    {
                        params.put("info",mLoginPageInfo);
                    }
                params.put(EditHeadIconImgPage.BGPATH,getScreenBmp());
                mSite.uploadHeadImg(params,getContext());
            } else if (v == mPswShowIcon) {
                if (isHidePsw) {
                    MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤二_设置信息_密码明文);
                    centerPswInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    centerPswInput.setSelection(centerPswInput.length());
                    isHidePsw = !isHidePsw;
                    mPswShowIcon.setButtonImage(R.drawable.beauty_login_show_psw, R.drawable.beauty_login_show_psw);
                } else {
                    MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤二_设置信息_密码加密);
                    centerPswInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    centerPswInput.setSelection(centerPswInput.length());
                    isHidePsw = !isHidePsw;
                    mPswShowIcon.setButtonImage(R.drawable.beauty_login_hidepsw, R.drawable.beauty_login_hidepsw);
                }
            } else if (v == centerOkLogin) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤二_设置信息_完成);
                isFitRule();
            } else if (v == m_cancelBtn) {
                MyBeautyStat.onClickByRes(R.string.个人中心_登录注册_创建账号_步骤二_返回);
                hideKeyboard();
                mSite.backtoLastPage(getContext());
            }
        }
    };

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getApplicationWindowToken(), 0);
    }

    /**
     * 规则判断
     */
    private boolean isFitRule() {
        String nickName = centerNickInput.getText().toString();
        nickName = nickName.trim();
        nickName = nickName.replace(" ", "");
        //昵称为空
        if (nickName.length() == 0) {
            m_tipsFr.setVisibility(VISIBLE);
            m_tipText.setText(m_nameEmptytips);
            return false;
        }
        String pwdStr = centerPswInput.getText().toString();

        switch(RequestParam.isRightPassword(pwdStr))
        {
            case -1:
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(m_pswEmptytips);
                return false;

            case -2:
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(m_pswtips2);
                return false;

            case -3:
                m_tipsFr.setVisibility(VISIBLE);
                m_tipText.setText(m_pswtips);
                return false;
        }

        if (!iconUpload) {
            m_tipsFr.setVisibility(VISIBLE);
            m_tipText.setText(m_uploadHeadbmptips);
            return false;
        }
        if (iconUpload && pwdStr.length() >= 8 && pwdStr.length() <= 20 && nickName.length() > 0) {
            hideKeyboard();
            UserInfo userInfo = new UserInfo();
            userInfo.mUserId = userId;
            userInfo.mNickname = nickName;
            userInfo.mUserIcon = iconUrl;
            register1(userId, loginInfo.mAccessToken, iconUrl, nickName, pwdStr);
            return true;
        }
        return false;
    }

    protected void register1(String id, final String token, final String iconUrl, final String nickName, final String pwd) {
        LoginOtherUtil.RegisterAction.RegisterBaseInfo baseInfo = new LoginOtherUtil.RegisterAction.RegisterBaseInfo();
        baseInfo.m_site = mSite;
        baseInfo.loginInfo = loginInfo;
        baseInfo.m_userId = userId;
        baseInfo.m_userId = id;
        baseInfo.m_token = token;
        baseInfo.m_iconUrl = iconUrl;
        baseInfo.m_nickName = nickName;
        baseInfo.m_pwd = pwd;
        LoginOtherUtil.RegisterAction registerAction = new LoginOtherUtil.RegisterAction(getContext(),baseInfo);
        registerAction.action();
    }

    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (v == m_cancelBtn) {
                    m_cancelBtn.setAlpha(0.5f);
                } else if (v == userHeadImg) {
                    userHeadImg.setAlpha(0.5f);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if (v == m_cancelBtn)
                {
                    m_cancelBtn.setAlpha(1.0f);
                }
                else if (v == userHeadImg)
                {
                    userHeadImg.setAlpha(1.0f);
                }
            }
            return false;
        }
    };

    @Override
    public void onClose() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }

        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
        if (bkBmp != null) {
            bkBmp = null;
        }

            MyBeautyStat.onPageEndByRes(R.string.个人中心_登录注册_创建账号_步骤二);
            TongJiUtils.onPageEnd(getContext(), R.string.注册);
    }

    @Override
    public void onResume() {
        super.onResume();
            TongJiUtils.onPageResume(getContext(), R.string.注册);
    }

    @Override
    public void onPause() {
        super.onPause();
            TongJiUtils.onPagePause(getContext(), R.string.注册);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if (siteID == SiteID.REGISTER_HEAD) {
            if(params != null)
            {
             if(params.get("isBack") != null && (boolean)params.get("isBack") == true)
             {

             }
                else
             {
                 File file = new File(UserMgr.HEAD_PATH);
                 if (file.exists()) {
                     Bitmap temp = BitmapFactory.decodeFile(UserMgr.HEAD_PATH);
                     Bitmap out = ImageUtils.MakeHeadBmp(temp, ShareData.PxToDpi_xhdpi(160), 0, 0);
                     if (temp != null) {
                         temp.recycle();
                         temp = null;
                     }
                     userHeadImg.setImageBitmap(out);
                     ImageUtils.RemoveSkin(getContext(),userHeadImg);
                     if(m_tipsFr != null && m_tipsFr.getVisibility() == VISIBLE && m_tipText.getText().toString().equals(m_uploadHeadbmptips))
                     {
                         m_tipsFr.setVisibility(INVISIBLE);
                     }
                 }
                 if (params != null) {
                     if (params.get("headUrl") != null && ((String) params.get("headUrl")).length() > 0) {
                         iconUrl = (String) params.get("headUrl");
                         iconUpload = true;
                     }
                     if (params.get("id") != null) {
                         userId = (String) params.get("id");
                     }
                     if (params.get("info") != null) {
                         loginInfo = (LoginInfo) params.get("info");
                     }
                 }
             }
            }


        }
    }
}
