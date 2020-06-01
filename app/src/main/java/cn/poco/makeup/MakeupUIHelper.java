package cn.poco.makeup;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.beautify.SonWindow;
import cn.poco.tianutils.ShareData;
import cn.poco.tianutils.Switch;
import my.beautyCamera.R;

import static my.beautyCamera.R.drawable.beautify_check_logo_eye;
import static my.beautyCamera.R.drawable.beautify_check_logo_eyebrow;

public class MakeupUIHelper {

    /**parent参数是传要显示ChangePointFr的父view，ChangePointFrCallBack是点击改变定点的布局的按钮的回调，
     后面的可变参数是要隐藏的选项，例如要隐藏鼻子的选项传ChangePointFr.CHECK_INDEX_NOSE进来
    **/
    public static ChangePointFr showChangePointFr(FrameLayout parent,ChangePointFr.ChangePointFrCallBack callBack,int... gones)
    {
        ChangePointFr out = new ChangePointFr(parent.getContext(),callBack);
        FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(320));
        fl.gravity = Gravity.BOTTOM;
        out.setLayoutParams(fl);
        parent.addView(out);

        if(gones != null && gones.length > 0)
        {
            for(int i = 0; i < gones.length; i++)
            {
                out.SetGONEITEM(gones[i]);
            }
        }
        return out;
    }

    //定点ui
    public static class ChangePointFr extends FrameLayout
    {
        public static final int CHECK_INDEX_LIP = 0; //嘴唇
        public static final int CHECK_INDEX_EYE_L = 1;//眼睛
        //    public static final int CHECK_INDEX_EYE_R = 2;//右眼
        public static final int CHECK_INDEX_EYEBROW = 3;//眉毛
        public static final int CHECK_INDEX_CHEEK = 4;//一键萌妆脸颊

        public static final int CHECK_INDEX_NOSE = 5;//鼻子
        public static final int CHECK_INDEX_CHEEK1 = 6;//彩妆脸颊

        public static final int CHECK_TRHEE = 7;

        private int m_curSelIndex = -1;
        public FrameLayout m_classListBar;
        public LinearLayout m_checkClassListlin;
        public ChangePointPage.TitleItem m_checkLipBtn;//嘴唇
        public ChangePointPage.TitleItem m_checkEyeBtnL;//眼睛
        public ChangePointPage.TitleItem m_checkEyebrowBtn;//眉毛
        public ChangePointPage.TitleItem m_checkCheekBtn;//脸颊

        public ChangePointPage.TitleItem m_checkNoseBtn;//左右鼻翼

        public ImageView m_checkBackBtn;//多点返回
        public ImageView m_changePointOkBtn;//多点确定
        public FrameLayout m_checkBar;
        public ImageView m_line;
        public TextView m_checkContent;
        public TextView m_checkTitle;
        public ImageView m_checkLogo;
        public LinearLayout m_selAllFr;
        public Switch m_selAllCtrl;
        public ImageView m_checkThreeOkBtn;//三点返回
        public ImageView m_checkThreeBackBtn;//三点确定
        public SonWindow m_sonWin;
        private ChangePointFrCallBack m_cb;
        private int m_ItemW = ShareData.PxToDpi_xhdpi(108);
        public ChangePointFr(@NonNull Context context,ChangePointFrCallBack cb) {
            super(context);
            m_cb = cb;
            int temp = (ShareData.m_screenWidth - ShareData.PxToDpi_xxhdpi(75)*2 + ShareData.PxToDpi_xhdpi(46));
            if(temp < m_ItemW*5)
            {
                m_ItemW = (int) (temp/5f);
            }
            initUI();
        }


        private void initUI()
        {
            m_classListBar = new FrameLayout(getContext());
            LayoutParams fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(94));
            fl.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            fl.bottomMargin = ShareData.PxToDpi_xhdpi(226);
            m_classListBar.setLayoutParams(fl);
            m_classListBar.setBackgroundColor(0xf4ffffff);
            this.addView(m_classListBar);

            m_checkClassListlin = new LinearLayout(getContext());
            m_checkClassListlin.setOrientation(LinearLayout.HORIZONTAL);
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            fl.gravity = Gravity.CENTER;
            m_checkClassListlin.setLayoutParams(fl);
            m_classListBar.addView(m_checkClassListlin);
            {
                m_checkLipBtn = new ChangePointPage.TitleItem(getContext());
                m_checkLipBtn.SetData(R.drawable.beautify_makeup_point_zuichun_icon,R.drawable.beautify_makeup_point_zuichun_icon_hover,getResources().getString(R.string.makeup_changepoint_zuichun_name));
                LinearLayout.LayoutParams lLayoutParams = new LinearLayout.LayoutParams(m_ItemW,LinearLayout.LayoutParams.MATCH_PARENT);
                m_checkLipBtn.setLayoutParams(lLayoutParams);
                m_checkClassListlin.addView(m_checkLipBtn);
                m_checkLipBtn.setOnClickListener(m_cb);

                m_checkEyeBtnL = new ChangePointPage.TitleItem(getContext());
                m_checkEyeBtnL.SetData(R.drawable.beautify_makeup_point_zuoyan_icon,R.drawable.beautify_makeup_point_zuoyan_icon_hover,getResources().getString(R.string.makeup_changepoint_zuoyan_name));
                lLayoutParams = new LinearLayout.LayoutParams(m_ItemW, LinearLayout.LayoutParams.MATCH_PARENT);
                lLayoutParams.leftMargin = ShareData.PxToDpi_xhdpi(5);
                m_checkEyeBtnL.setLayoutParams(lLayoutParams);
                m_checkClassListlin.addView(m_checkEyeBtnL);
                m_checkEyeBtnL.setOnClickListener(m_cb);

                m_checkNoseBtn = new ChangePointPage.TitleItem(getContext());
                m_checkNoseBtn.SetData(R.drawable.beautify_makeup_point_nose_icon,R.drawable.beautify_makeup_point_nose_icon_hover,getResources().getString(R.string.makeup_chnagepoint_nose_name));
                lLayoutParams = new LinearLayout.LayoutParams(m_ItemW, LinearLayout.LayoutParams.MATCH_PARENT);
                lLayoutParams.leftMargin = ShareData.PxToDpi_xhdpi(5);
                m_checkNoseBtn.setLayoutParams(lLayoutParams);
                m_checkClassListlin.addView(m_checkNoseBtn);
                m_checkNoseBtn.setOnClickListener(m_cb);

                m_checkEyebrowBtn = new ChangePointPage.TitleItem(getContext());
                m_checkEyebrowBtn.SetData(R.drawable.beautify_makeup_point_meimao_icon,R.drawable.beautify_makeup_point_meimao_icon_hover,getResources().getString(R.string.makeup_changepoint_meimao_name));
                lLayoutParams = new LinearLayout.LayoutParams(m_ItemW, LinearLayout.LayoutParams.MATCH_PARENT);
                lLayoutParams.leftMargin = ShareData.PxToDpi_xhdpi(5);
                m_checkEyebrowBtn.setLayoutParams(lLayoutParams);
                m_checkClassListlin.addView(m_checkEyebrowBtn);
                m_checkEyebrowBtn.setOnClickListener(m_cb);

                m_checkCheekBtn = new ChangePointPage.TitleItem(getContext());
                m_checkCheekBtn.SetData(R.drawable.beautify_makeup_point_lianjia_icon,R.drawable.beautify_makeup_point_lianjia_icon_hover,getResources().getString(R.string.makeup_chnagepoint_lianjia_name));
                lLayoutParams = new LinearLayout.LayoutParams(m_ItemW, LinearLayout.LayoutParams.MATCH_PARENT);
                lLayoutParams.leftMargin = ShareData.PxToDpi_xhdpi(5);
                m_checkCheekBtn.setLayoutParams(lLayoutParams);
                m_checkClassListlin.addView(m_checkCheekBtn);
                m_checkCheekBtn.setOnClickListener(m_cb);
            }

            m_checkBackBtn = new ImageView(getContext());
            m_checkBackBtn.setImageResource(R.drawable.beautify_cancel);
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
            fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
            m_checkBackBtn.setLayoutParams(fl);
            m_classListBar.addView(m_checkBackBtn);
            m_checkBackBtn.setOnClickListener(m_cb);

            m_changePointOkBtn = new ImageView(getContext());
            fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            fl.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
            m_changePointOkBtn.setLayoutParams(fl);
            m_classListBar.addView(m_changePointOkBtn);
        m_changePointOkBtn.setOnClickListener(m_cb);
            m_changePointOkBtn.setImageResource(R.drawable.beautify_ok);
            ImageUtils.AddSkin(getContext(),m_changePointOkBtn);

            m_checkBar = new FrameLayout(getContext());
            m_checkBar.setBackgroundColor(0xf4ffffff);
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(226));
            fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
            m_checkBar.setLayoutParams(fl);
            this.addView(m_checkBar);

            m_line = new ImageView(getContext());
            fl = new LayoutParams(ShareData.m_screenWidth,1);
            fl.gravity = Gravity.BOTTOM;
            fl.bottomMargin = ShareData.PxToDpi_xhdpi(226);
            m_line.setLayoutParams(fl);
            this.addView(m_line);
            m_line.setBackgroundColor(0xffcccccc);

            {
                m_checkContent = new TextView(getContext());
                m_checkContent.setTextColor(0xb2000000);
                m_checkContent.setGravity(Gravity.TOP | Gravity.LEFT);
                m_checkContent.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                m_checkContent.setLayoutParams(fl);
                m_checkBar.addView(m_checkContent);

                m_checkTitle = new TextView(getContext());
                m_checkTitle.setTextColor(0xff000000);
                m_checkTitle.setGravity(Gravity.TOP | Gravity.LEFT);
                m_checkTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                m_checkTitle.setSingleLine();
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                m_checkTitle.setLayoutParams(fl);
                m_checkBar.addView(m_checkTitle);

                m_checkLogo = new ImageView(getContext());
                m_checkLogo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                m_checkLogo.setLayoutParams(fl);
                m_checkBar.addView(m_checkLogo);

                m_selAllFr = new LinearLayout(getContext());
                m_selAllFr.setOrientation(LinearLayout.HORIZONTAL);
                //m_selAllFr.setPadding(ShareData.PxToDpi_xhdpi(30), 0, 0, 0);
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
                fl.leftMargin = ShareData.PxToDpi_xhdpi(224);
                fl.bottomMargin = ShareData.PxToDpi_xhdpi(27);
                m_selAllFr.setLayoutParams(fl);
                m_checkBar.addView(m_selAllFr);
                {
                    m_selAllCtrl = new Switch(getContext());

                    //thumbOnBk 选中状态Bitmap
                    Bitmap temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_locked_bk_icon);
                    Bitmap thumbOnBk = temp.copy(Bitmap.Config.ARGB_8888,true);
                    if(temp != null)
                    {
                        temp.recycle();
                        temp = null;
                    }
                    Canvas thumbOnCanvas = new Canvas(thumbOnBk);
                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_locked_icon);
                    Bitmap thumbOn = ImageUtils.AddSkin(getContext(),temp);
                    if(temp != null)
                    {
                        int startX = (int) ((thumbOnBk.getWidth() - thumbOn.getWidth())/2f);
                        int startY = (int) ((thumbOnBk.getHeight() - thumbOn.getHeight())/2f);
                        thumbOnCanvas.drawBitmap(thumbOn,startX,startY,null);
                    }
                    BitmapDrawable thumbOnDrawable = new BitmapDrawable(getResources(),thumbOnBk);
//                thumbOnDrawable.setTargetDensity(getResources().getDisplayMetrics());

                    // thumbOffBk 没选中状态Bitmap
                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_lock_bk_icon);
                    Bitmap thumbOffBk = temp.copy(Bitmap.Config.ARGB_8888,true);
                    if(temp != null)
                    {
                        temp.recycle();
                        temp = null;
                    }
                    Canvas thumbOffCanvas = new Canvas(thumbOffBk);
                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_changepoint_lock_icon);
                    Bitmap thumbOff = temp;
                    if(temp != null)
                    {
                        int startX = (int) ((thumbOffBk.getWidth() - thumbOff.getWidth())/2f);
                        int startY = (int) ((thumbOffBk.getHeight() - thumbOff.getHeight())/2f);
                        thumbOffCanvas.drawBitmap(thumbOff,startX,startY,null);
                    }
                    BitmapDrawable thumbOffDrawable = new BitmapDrawable(getResources(),thumbOffBk);
//                thumbOffDrawable.setTargetDensity(getResources().getDisplayMetrics());

                    StateListDrawable drawable = new StateListDrawable();
                    drawable.addState(new int[]{android.R.attr.state_checked},
                            thumbOnDrawable);
                    drawable.addState(new int[]{-android.R.attr.state_pressed},
                            thumbOffDrawable);
                    drawable.addState(new int[]{android.R.attr.state_pressed},thumbOnDrawable);

                    m_selAllCtrl.setThumbDrawable(drawable);

//                m_selAllCtrl.setThumbResource(R.drawable.beautify_allsel_switch_thumb);
//                m_selAllCtrl.setTrackOffResource(R.drawable.beautify_allsel_switch_track_off);
//                m_selAllCtrl.setTrackOnResource(R.drawable.beautify_allsel_switch_track_on);

                    Bitmap track_off = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_allsel_switch_track_off_new);
                    BitmapDrawable track_off_drawable = new BitmapDrawable(getResources(),track_off);
                    m_selAllCtrl.setTrackOffDrawable(track_off_drawable);

                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_allsel_switch_track_on_new);
                    Bitmap track_on = ImageUtils.AddSkin(getContext(),temp,0xffe75988);
                    if(temp != null)
                    {
                        temp.recycle();
                        temp = null;
                    }
                    BitmapDrawable track_on_drawable = new BitmapDrawable(getResources(),track_on);
                    m_selAllCtrl.setTrackOnDrawable(track_on_drawable);

                    m_selAllCtrl.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked, boolean fromUser)
                        {
                            if(m_cb != null)
                            {
                                m_cb.onCheckedChanged(isChecked,fromUser);
                            }
                        }
                    });
                    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(80), ShareData.PxToDpi_xhdpi(48));
                    ll.gravity = Gravity.CENTER;
                    m_selAllCtrl.setLayoutParams(ll);
                    m_selAllFr.addView(m_selAllCtrl);

                    TextView tex = new TextView(getContext());
                    tex.setTextColor(0xFF999999);
                    tex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                    tex.setText(getResources().getString(R.string.makeup_changepoint_help_tips1));
                    tex.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    tex.setSingleLine();
                    ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    ll.leftMargin = ShareData.PxToDpi_xhdpi(10);
                    tex.setLayoutParams(ll);
                    m_selAllFr.addView(tex);
                }

                m_checkThreeOkBtn = new ImageView(getContext());
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.RIGHT | Gravity.TOP;
                fl.topMargin = ShareData.PxToDpi_xhdpi(20);
                fl.rightMargin = ShareData.PxToDpi_xhdpi(24);
                m_checkThreeOkBtn.setLayoutParams(fl);
                m_checkBar.addView(m_checkThreeOkBtn);
                m_checkThreeOkBtn.setImageResource(R.drawable.beautify_ok);
            m_checkThreeOkBtn.setOnClickListener(m_cb);
                ImageUtils.AddSkin(getContext(),m_checkThreeOkBtn);


                m_checkThreeBackBtn = new ImageView(getContext());
                fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                fl.gravity = Gravity.LEFT | Gravity.TOP;
                fl.topMargin = ShareData.PxToDpi_xhdpi(20);
                fl.leftMargin = ShareData.PxToDpi_xhdpi(22);
                m_checkThreeBackBtn.setLayoutParams(fl);
                m_checkBar.addView(m_checkThreeBackBtn);
                m_checkThreeBackBtn.setImageResource(R.drawable.beautify_cancel);
            m_checkThreeBackBtn.setOnClickListener(m_cb);
            }

            InitCheckBar(1);

            m_sonWin = new SonWindow(getContext());
            fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.m_screenWidth/3);
            fl.gravity = Gravity.TOP | Gravity.LEFT;
            m_sonWin.setLayoutParams(fl);
            this.addView(m_sonWin);
        }

        public void SetGONEITEM(int flag)
        {
            switch (flag)
            {
                case CHECK_INDEX_CHEEK:
                case CHECK_INDEX_CHEEK1:
                {
                    setGONE(m_checkCheekBtn);
                    break;
                }
                case CHECK_INDEX_EYE_L:
                {
                    setGONE(m_checkEyeBtnL);
                    break;
                }
                case CHECK_INDEX_EYEBROW:
                {
                    setGONE(m_checkEyebrowBtn);
                    break;
                }
                case CHECK_INDEX_LIP:
                {
                    setGONE(m_checkLipBtn);
                    break;
                }
                case CHECK_INDEX_NOSE:
                {
                    setGONE(m_checkNoseBtn);
                    break;
                }
            }
        }


        public void setGONES(int... gones)
        {
            if(gones != null && gones.length > 0)
            {
                for(int i = 0 ; i < gones.length; i++)
                {
                    int flag = gones[i];
                    SetGONEITEM(flag);
                }
            }
        }

        private void setGONE(View view)
        {
            if(view != null)
            {
                view.setVisibility(GONE);
            }
        }

        public void SetSelCheckBarClass(int index)
        {
            Bitmap temp = null;
            switch(index)
            {
                case CHECK_INDEX_LIP:
                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_check_logo_lip);
                    m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content1));
                    break;
                case CHECK_INDEX_EYE_L:
//            case CHECK_INDEX_EYE_R:
                    temp = BitmapFactory.decodeResource(getResources(), beautify_check_logo_eye);
                    m_checkContent.setText(getResources().getString(R.string.makeup_chnagepoint_content2));
                    break;
                case CHECK_INDEX_EYEBROW:
                    temp = BitmapFactory.decodeResource(getResources(), beautify_check_logo_eyebrow);
                    m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content3));
                    break;
                case CHECK_INDEX_CHEEK:
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_check_logo2_cheek);
                    m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content6));
                    break;
                case CHECK_INDEX_CHEEK1:
                    temp = BitmapFactory.decodeResource(getResources(),R.drawable.beautify_check_logo_cheek);
                    m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content4));
                    break;
                case CHECK_INDEX_NOSE:
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.beautify_check_logo_nose);
                    m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_content5));
                    break;
                default:
                    break;
            }

            if(temp != null)
            {
                ImageUtils.MakeHeadBmp(temp, temp.getWidth(), 0, 0);
                m_checkLogo.setImageBitmap(ImageUtils.MakeHeadBmp(temp, temp.getWidth(), 0, 0));
                if(temp != null)
                {
                    temp = null;
                }
            }
        }

        /**
         * @param index CHECK_INDEX_LIP唇彩,CHECK_INDEX_EYE眼睛,CHECK_INDEX_EYEBROW眉毛,
         *              CHECK_INDEX_CHEEK脸颊
         */
        public void SetSelCheckClassList(int index)
        {
            if(m_curSelIndex == index)
            {
                return;
            }
            switch(m_curSelIndex)
            {
                case CHECK_INDEX_LIP:
                    m_checkLipBtn.ClearChoose();
                    break;
                case CHECK_INDEX_EYE_L:
                    m_checkEyeBtnL.ClearChoose();
                    break;
                case CHECK_INDEX_EYEBROW:
                    m_checkEyebrowBtn.ClearChoose();
                    break;
                case CHECK_INDEX_CHEEK:
                case CHECK_INDEX_CHEEK1:
                    m_checkCheekBtn.ClearChoose();
                    break;
                case CHECK_INDEX_NOSE:
                    m_checkNoseBtn.ClearChoose();
                    break;
                default:
                    break;
            }

            switch(index)
            {
                case CHECK_INDEX_LIP:
                    m_checkLipBtn.SetChoose();
                    break;
                case CHECK_INDEX_EYE_L:
                    m_checkEyeBtnL.SetChoose();
                    break;
                case CHECK_INDEX_EYEBROW:
                    m_checkEyebrowBtn.SetChoose();
                    break;
                case CHECK_INDEX_CHEEK:
                case CHECK_INDEX_CHEEK1:
                    m_checkCheekBtn.SetChoose();
                    break;
                case CHECK_INDEX_NOSE:
                    m_checkNoseBtn.SetChoose();
                    break;
                default:
                    break;
            }
            m_curSelIndex = index;
        }

        public void InitCheckBar(int flag)
        {
            LayoutParams fl;
            switch(flag) {
                case 0:
                    fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(234));
                    fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
                    m_checkBar.setLayoutParams(fl);

                    m_checkLogo.setVisibility(GONE);
                    m_selAllFr.setVisibility(GONE);
                    m_classListBar.setVisibility(GONE);
                    m_line.setVisibility(GONE);
                    m_checkThreeBackBtn.setVisibility(VISIBLE);
                    m_checkThreeOkBtn.setVisibility(VISIBLE);

                    m_checkTitle.setText(getResources().getString(R.string.makeup_changepoint_title1));
                    fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    fl.topMargin = ShareData.PxToDpi_xhdpi(108);
                    fl.leftMargin = ShareData.PxToDpi_xhdpi(32);
                    m_checkTitle.setLayoutParams(fl);

                    m_checkContent.setText(getResources().getString(R.string.makeup_changepoint_title2));
                    fl = new LayoutParams(ShareData.PxToDpi_xhdpi(600), ShareData.PxToDpi_xhdpi(70));
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    fl.topMargin = ShareData.PxToDpi_xhdpi(148);
                    fl.leftMargin = ShareData.PxToDpi_xhdpi(32);
                    m_checkContent.setLayoutParams(fl);
                    break;
                case 1:
                    m_checkThreeBackBtn.setVisibility(GONE);
                    m_checkThreeOkBtn.setVisibility(GONE);

                    fl = new LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(226));
                    fl.gravity = Gravity.LEFT | Gravity.BOTTOM;
                    m_checkBar.setLayoutParams(fl);

                    m_checkLogo.setVisibility(View.VISIBLE);
                    m_classListBar.setVisibility(VISIBLE);
                    m_line.setVisibility(VISIBLE);
                    m_selAllFr.setVisibility(View.VISIBLE);

                    fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                    fl.leftMargin = ShareData.PxToDpi_xhdpi(28);
                    m_checkLogo.setLayoutParams(fl);

                    m_selAllFr.setVisibility(View.VISIBLE);

                    m_checkTitle.setText(getResources().getString(R.string.makeup_changepoint_title3));
                    fl = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    fl.topMargin = ShareData.PxToDpi_xhdpi(30);
                    fl.leftMargin = ShareData.PxToDpi_xhdpi(224);
                    m_checkTitle.setLayoutParams(fl);

                    fl = new LayoutParams(ShareData.PxToDpi_xhdpi(380), LayoutParams.WRAP_CONTENT);
                    fl.gravity = Gravity.LEFT | Gravity.TOP;
                    fl.topMargin = ShareData.PxToDpi_xhdpi(72);
                    fl.leftMargin = ShareData.PxToDpi_xhdpi(224);
                    m_checkContent.setLayoutParams(fl);

                    break;
                default:
                    break;
            }
        }

        public interface ChangePointFrCallBack extends View.OnClickListener
        {
            public void onCheckedChanged(boolean isChecked, boolean fromUser);
        }

        //根据不同的flag，显示不同的提示的信息
        public void setUIFlag(int flag)
        {
//            if(flag == 0)
//            {
//                InitCheckBar(0);
//            }
//            else if(flag == 1)
//            {
//                InitCheckBar(1);
//            }
            if(flag == CHECK_TRHEE)
            {
                InitCheckBar(0);
            }
            else
            {
                InitCheckBar(1);
                SetSelCheckBarClass(flag);
                SetSelCheckClassList(flag);
            }
        }

    }
}
