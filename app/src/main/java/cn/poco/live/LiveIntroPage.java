package cn.poco.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.Build;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.circle.ctrls.RoundedImageView;
import com.circle.utils.Utils;

import java.util.HashMap;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.framework.BaseSite;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.framework.IPage;
import cn.poco.framework.SiteID;
import cn.poco.live.site.LiveIntroPageSite;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.system.TagMgr;
import cn.poco.system.Tags;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by zwq on 2018/01/30 11:30.<br/><br/>
 */

public class LiveIntroPage extends IPage {

    private LiveIntroPageSite mSite;
    private EventCenter.OnEventListener mEventListener;

    public LiveIntroPage(Context context, BaseSite site) {
        super(context, site);
        mSite = (LiveIntroPageSite) site;

        initView(context);

        initListener();

        MyBeautyStat.onPageStartByRes(R.string.直播助手_直播入口_入口中转页);
    }

    private void initListener() {
        mEventListener = new EventCenter.OnEventListener() {

            @Override
            public void onEvent(int eventId, Object[] params) {
                if (eventId == EventID.HOMEPAGE_UPDATE_MENU_AVATAR) {
                    boolean isUserLogin = UserMgr.IsLogin(getContext(), null);
                    if (!isUserLogin) {
                        if (mainContain != null) {
                            mainContain.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (mSite != null) {
                                        MyBeautyStat.onClickByRes(R.string.直播助手_直播入口_入口中转页_退出);
                                        mSite.onBack(getContext());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        };
        EventCenter.addListener(mEventListener);
    }

    private ImageView mBackBtn;
    private TextView mTitle;
    private RoundedImageView mIconView;
    private TextView mUserName;
    private RelativeLayout mLiveBtn, mClassBtn;
    private Button mOpenClassBtn;
    private TextView mOkBtn;
    private RelativeLayout mainContain;


    private void initView(Context context) {
        LayoutParams fPrams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mainContain = new RelativeLayout(context);
        mainContain.setBackgroundColor(0xffffffff);
        addView(mainContain, fPrams);

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        RelativeLayout topBar = new RelativeLayout(context);
        topBar.setBackgroundColor(0xffffffff);
        mainContain.addView(topBar, rParams);
        topBar.setId(Utils.generateViewId());

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rParams.topMargin = ShareData.PxToDpi_xhdpi(5);
        rParams.leftMargin = ShareData.PxToDpi_xhdpi(2);
        mBackBtn = new ImageView(context);
        mBackBtn.setScaleType(ScaleType.CENTER_CROP);
        mBackBtn.setImageResource(R.drawable.framework_back_btn);
        topBar.addView(mBackBtn, rParams);
        ImageUtils.AddSkin(getContext(), mBackBtn);
        mBackBtn.setOnTouchListener(mOnAnimationClickListener);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTitle = new TextView(getContext());
        mTitle.setTextColor(0xff333333);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
        mTitle.setText(R.string.live_intro_title);
        topBar.addView(mTitle, rParams);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.addRule(RelativeLayout.BELOW, topBar.getId());
        RelativeLayout containLay = new RelativeLayout(context);
        mainContain.addView(containLay, rParams);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.topMargin = ShareData.PxToDpi_xhdpi(92);
        rParams.leftMargin = ShareData.PxToDpi_xhdpi(36);
        RelativeLayout userLay = new RelativeLayout(context);
        containLay.addView(userLay, rParams);
        userLay.setId(Utils.generateViewId());

        rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(120), ShareData.PxToDpi_xhdpi(120));
        mIconView = new RoundedImageView(context);
        mIconView.setOval(true);
        mIconView.setScaleType(ScaleType.CENTER_CROP);
//        mIconView.setImageResource(R.drawable.defaultpic);
        mIconView.setImageResource(R.drawable.featuremenu_default_avatar);
        userLay.addView(mIconView, rParams);
        mIconView.setId(Utils.generateViewId());

        rParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_VERTICAL);
        rParams.addRule(RelativeLayout.RIGHT_OF, mIconView.getId());
        rParams.leftMargin = ShareData.PxToDpi_xhdpi(28);
        rParams.rightMargin = ShareData.PxToDpi_xhdpi(20);
        mUserName = new TextView(context);
        mUserName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        mUserName.setTextColor(0xff000000);
        mUserName.setSingleLine();
        mUserName.setEllipsize(TruncateAt.END);
        userLay.addView(mUserName, rParams);


        rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(424));
        rParams.addRule(RelativeLayout.BELOW, userLay.getId());
        rParams.topMargin = ShareData.PxToDpi_xhdpi(24);
        rParams.leftMargin = rParams.rightMargin = ShareData.PxToDpi_xhdpi(10);
        ItemView itemView2 = new ItemView(context);
        itemView2.setId(Utils.generateViewId());
        itemView2.setBgColors(new int[]{0xffcc93ee, 0xfff49dc8});
        itemView2.setTitle(R.string.live_start_class);
        itemView2.setDesc(R.string.live_class_description);
        itemView2.setIcon(R.drawable.live_gotoclass_icon);
        containLay.addView(itemView2, rParams);
        mClassBtn = itemView2;
        mClassBtn.setId(Utils.generateViewId());
        mClassBtn.setOnTouchListener(mOnAnimationClickListener);

        rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(424));
        rParams.addRule(RelativeLayout.BELOW, mClassBtn.getId());
        rParams.topMargin = ShareData.PxToDpi_xhdpi(-14);
        rParams.leftMargin = rParams.rightMargin = ShareData.PxToDpi_xhdpi(10);
        ItemView itemView = new ItemView(context);
        itemView.setId(Utils.generateViewId());
        itemView.setBgColors(new int[]{0xfffd9c8f, 0xfffdcba7});
        itemView.setTitle(R.string.live_start_live);
        itemView.setDesc(R.string.live_live_description);
        itemView.setIcon(R.drawable.live_golive_icon);
        containLay.addView(itemView, rParams);
        mLiveBtn = itemView;
        mLiveBtn.setOnTouchListener(mOnAnimationClickListener);
    }

    private class ItemView extends RelativeLayout {

        public ItemView(Context context) {
            super(context);

            initView(context);
        }

        public RelativeLayout mItemView;
        private TextView mTitle;
        private TextView mDesc;
        private ImageView mIcon;

        private void initView(Context context) {
            //绘制背景阴影，如果效果样式不对，可换imageview加设计素材
            RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            MaskView maskView = new MaskView(context);
            addView(maskView, rParams);

            rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(372));
            rParams.topMargin = ShareData.PxToDpi_xhdpi(26);
            rParams.leftMargin = rParams.rightMargin = ShareData.PxToDpi_xhdpi(26);
            mItemView = new RelativeLayout(context);
            mItemView.setId(Utils.generateViewId());
            addView(mItemView, rParams);

            rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rParams.leftMargin = ShareData.PxToDpi_xhdpi(44);
            rParams.topMargin = ShareData.PxToDpi_xhdpi(33);
            mTitle = new TextView(context);
            mTitle.setTextColor(0xffffffff);
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
            TextPaint paint = mTitle.getPaint();
            paint.setFakeBoldText(true);
            mTitle.setId(Utils.generateViewId());
            mItemView.addView(mTitle, rParams);

            rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(56), ShareData.PxToDpi_xhdpi(4));
            rParams.addRule(RelativeLayout.BELOW, mTitle.getId());
            rParams.leftMargin = ShareData.PxToDpi_xhdpi(44);
            rParams.topMargin = ShareData.PxToDpi_xhdpi(20);
            View line = new View(context);
            line.setBackgroundColor(0xffffffff);
            line.setId(Utils.generateViewId());
            mItemView.addView(line, rParams);

            rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rParams.addRule(RelativeLayout.BELOW, line.getId());
            rParams.leftMargin = ShareData.PxToDpi_xhdpi(44);
            rParams.topMargin = ShareData.PxToDpi_xhdpi(24);
            mDesc = new TextView(context);
            mDesc.setTextColor(0xffffffff);
            mDesc.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            mItemView.addView(mDesc, rParams);

            rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(110));
            rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            ImageView arrView = new ImageView(context);
            arrView.setScaleType(ScaleType.CENTER_CROP);
            arrView.setImageResource(R.drawable.live_goto_icon);
            mItemView.addView(arrView, rParams);

            rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(280), ShareData.PxToDpi_xhdpi(190));
            rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mIcon = new ImageView(context);
            mIcon.setScaleType(ScaleType.CENTER_CROP);
            mItemView.addView(mIcon, rParams);
        }

        public void setBgColors(int[] colors) {
            if (mItemView != null && colors != null) {
                GradientDrawable bg = new GradientDrawable();
                bg.setCornerRadius(ShareData.PxToDpi_xhdpi(30));
                bg.setColors(colors);
                bg.setOrientation(Orientation.LEFT_RIGHT);
                mItemView.setBackground(bg);
            }
        }

        public void setTitle(int resId) {
            if (mTitle != null) {
                mTitle.setText(resId);
            }
        }

        public void setDesc(int resId) {
            if (mDesc != null) {
                mDesc.setText(resId);
            }
        }

        public void setIcon(int resId) {
            if (mIcon != null) {
                mIcon.setImageResource(resId);
            }
        }
    }

    //弹框
    private View getPopView() {
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RelativeLayout popView = new RelativeLayout(getContext());
        popView.setBackgroundColor(0x80000000);
        popView.setLayoutParams(rParams);
        popView.setClickable(true);

        // rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(690 + 25));
        rParams = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), RelativeLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        LinearLayout containView = new LinearLayout(getContext());
        containView.setOrientation(LinearLayout.VERTICAL);
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(ShareData.PxToDpi_xhdpi(30));
        bg.setColor(0xffffffff);
        containView.setBackground(bg);
        popView.addView(containView, rParams);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.gravity = Gravity.CENTER_HORIZONTAL;
        lParams.topMargin = ShareData.PxToDpi_xhdpi(52);
        TextView title = new TextView(getContext());
        TextPaint paint = title.getPaint();
        paint.setFakeBoldText(true);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        title.setTextColor(0xff333333);
        title.setText(R.string.live_tips_title);
        containView.addView(title, lParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.topMargin = ShareData.PxToDpi_xhdpi(32);
        lParams.leftMargin = lParams.rightMargin = ShareData.PxToDpi_xhdpi(60);
        TextView tips1 = new TextView(getContext());
        tips1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tips1.setTextColor(0xff333333);
        tips1.setText(R.string.live_tips_text1);
        tips1.setLineSpacing(ShareData.PxToDpi_xhdpi(13), 1);
        containView.addView(tips1, lParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.topMargin = ShareData.PxToDpi_xhdpi(40);
        lParams.leftMargin = lParams.rightMargin = ShareData.PxToDpi_xhdpi(60);
        TextView tips2 = new TextView(getContext());
        tips2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tips2.setTextColor(0xff333333);
        tips2.setText(R.string.live_tips_text2);
        tips2.setLineSpacing(ShareData.PxToDpi_xhdpi(13), 1);
        containView.addView(tips2, lParams);

        lParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lParams.topMargin = ShareData.PxToDpi_xhdpi(40);
        lParams.leftMargin = lParams.rightMargin = ShareData.PxToDpi_xhdpi(60);
        TextView tips3 = new TextView(getContext());
        tips3.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tips3.setTextColor(0xff333333);
        tips3.setText(R.string.live_tips_text3);
        tips3.setLineSpacing(ShareData.PxToDpi_xhdpi(13), 1);
        containView.addView(tips3, lParams);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(ImageUtils.GetSkinColor());
        btnBg.setCornerRadius(ShareData.PxToDpi_xhdpi(39));

        lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418), ShareData.PxToDpi_xhdpi(78));
        lParams.gravity = Gravity.CENTER_HORIZONTAL;
        lParams.topMargin = ShareData.PxToDpi_xhdpi(38);
        mOpenClassBtn = new Button(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mOpenClassBtn.setStateListAnimator(null);//去掉自带的阴影效果
        }
        mOpenClassBtn.setBackground(btnBg);
        mOpenClassBtn.setTextColor(0xffffffff);
        mOpenClassBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        paint = mOpenClassBtn.getPaint();
        paint.setFakeBoldText(true);
        mOpenClassBtn.setText(R.string.live_tips_btngo);
        mOpenClassBtn.setOnTouchListener(mOnAnimationClickListener);
        containView.addView(mOpenClassBtn, lParams);

        lParams = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(300), ShareData.PxToDpi_xhdpi(120));
        lParams.gravity = Gravity.CENTER_HORIZONTAL;
        // lParams.topMargin = ShareData.PxToDpi_xhdpi(22);
        // lParams.bottomMargin = ShareData.PxToDpi_xhdpi(30);
        mOkBtn = new TextView(getContext());
        // mOkBtn.setPadding(ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(50), ShareData.PxToDpi_xhdpi(10));
        mOkBtn.setText(R.string.live_tips_btnok);
        mOkBtn.setGravity(Gravity.CENTER);
        mOkBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mOkBtn.setTextColor(0xff999999);
        containView.addView(mOkBtn, lParams);
        mOkBtn.setOnTouchListener(mOnAnimationClickListener);
        return popView;
    }

    private void setPageInfo() {
        UserInfo userInfo = UserMgr.ReadCache(getContext());
        if (userInfo != null) {
            String nick = userInfo.mNickname;
            mUserName.setText(nick);
            ImageLoaderUtil.getBitmapByUrl(getContext(), userInfo.mUserIcon, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void loadImageSuccessfully(Object object) {
                    if (object != null) {
                        Bitmap headBmp = (Bitmap) object;
                        mIconView.setImageBitmap(headBmp);
                    }
                }

                @Override
                public void failToLoadImage() {

                }
            });
        }

    }

    private View popView = null;

    private OnAnimationClickListener mOnAnimationClickListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mBackBtn) {
                onBack();

            } else if (v == mLiveBtn) {
                MyBeautyStat.onClickByRes(R.string.直播助手_直播入口_入口中转页_开始直播);
                mSite.openLiveCamera(getContext(), null);

            } else if (v == mClassBtn) {
                MyBeautyStat.onClickByRes(R.string.直播助手_直播入口_入口中转页_查看教程);
                //弹框
                popView = getPopView();
                mainContain.addView(popView);

            } else if (v == mOpenClassBtn) {
                //doOutAnimation(); 加动画后弹框不能移除，因为动画执行完毕前当前页面已经被释放
                mainContain.removeView(popView);
                popView = null;

                HashMap<String, Object> params = new HashMap<>();
                params.put("url", "http://www.adnonstop.com/beauty_camera/wap/live.php?type=android");
                mSite.openLiveHelper(getContext(), params);

                MyBeautyStat.onPageStartByRes(R.string.直播助手_直播入口_教程页);

            } else if (v == mOkBtn) {
                doOutAnimation();
            }
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mLiveBtn) {
                MyBeautyStat.onClickByRes(R.string.直播助手_直播入口_入口中转页_开始直播);
                mSite.openLiveCamera(getContext(), null);
            } else if (v == mClassBtn) {
                //弹框
                popView = getPopView();
                mainContain.addView(popView);
            } else if (v == mOkBtn) {
                doOutAnimation();
            }
        }
    };

    public void doOutAnimation() {
        AlphaAnimation animation = new AlphaAnimation(1.0f, 0f);
        animation.setDuration(300);
        animation.setInterpolator(new LinearInterpolator());
        animation.setFillAfter(true);
        animation.setFillBefore(false);
        animation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (popView != null) {
                    popView.clearAnimation();
                    mainContain.removeView(popView);
                    popView = null;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        popView.startAnimation(animation);
    }


    private class MaskView extends View {

        public MaskView(Context context) {
            super(context);
        }

        Paint temp_paint = new Paint();
        int def_shadow_color = 0x24000000;
        Path temp_path = new Path();

        {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            temp_path.addRect(ShareData.PxToDpi_xhdpi(26), ShareData.PxToDpi_xhdpi(26),
                    ShareData.PxToDpi_xhdpi(674), ShareData.PxToDpi_xhdpi(398), Direction.CW);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            temp_paint.reset();
            temp_paint.setStyle(Style.FILL);
            temp_paint.setColor(def_shadow_color);
            temp_paint.setStrokeCap(Paint.Cap.SQUARE);
            temp_paint.setStrokeJoin(Paint.Join.MITER);
            temp_paint.setMaskFilter(new BlurMaskFilter(ShareData.PxToDpi_xhdpi(26), Blur.NORMAL));

            canvas.drawPath(temp_path, temp_paint);
        }
    }

    @Override
    public void SetData(HashMap<String, Object> params) {
        setPageInfo();

        if (mClassBtn != null) {
            boolean show = TagMgr.CheckTag(getContext(), Tags.LIVE_HELP_TIP);
            if (show) {
                mClassBtn.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mOnAnimationClickListener != null && mClassBtn != null) {
                            mOnAnimationClickListener.onAnimationClick(mClassBtn);
                        }
                    }
                });
                TagMgr.SetTag(getContext(), Tags.LIVE_HELP_TIP);
            }
        }
    }

    @Override
    public void onBack() {
        if (popView != null) {
            doOutAnimation();
            return;
        }
        MyBeautyStat.onClickByRes(R.string.直播助手_直播入口_入口中转页_退出);
        if (mSite != null) {
            mSite.onBack(getContext());
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (mEventListener != null) {
            EventCenter.removeListener(mEventListener);
            mEventListener = null;
        }
        MyBeautyStat.onPageEndByRes(R.string.直播助手_直播入口_入口中转页);
    }

    @Override
    public void onPageResult(int siteID, HashMap<String, Object> params) {
        super.onPageResult(siteID, params);
        if (siteID == SiteID.WEBVIEW) {
            MyBeautyStat.onPageEndByRes(R.string.直播助手_直播入口_教程页);
        }
    }
}
