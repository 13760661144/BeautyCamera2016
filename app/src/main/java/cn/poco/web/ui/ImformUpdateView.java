package cn.poco.web.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.web.info.UpdateInfo;
import my.beautyCamera.R;

/**
 * Created by admin on 2016/11/3.
 */

public class ImformUpdateView extends FrameLayout {
    public interface OnUpdateDialogClickListener {
        void onClickIgnoreUpdate();

        void onClickUpdateNow(String url);

        void onClickCheckDetail(String url);
    }

    private static final int SHOW_UPDATE_TITLE = 1;
    private static final int SHOW_NEW_APPVERSION = 2;
    private static final int SHOW_UPDATE_CONTENT = 4;
    private static final int SHOW_UPDATE_NOW_BUTTON = 8;
    private static final int SHOW_CHECK_UPDATE_BUTTON = 16;
    private static final int SHOW_IGNORE_UPDATE_BUTTON = 32;

    private UpdateInfo mUpdateInfo;
    private int currentState;
    private Paint mPaint;

    private OnUpdateDialogClickListener mListener;

    public ImformUpdateView(Context context, UpdateInfo updateInfo) {
        super(context);
        this.mUpdateInfo = updateInfo;
        initData(mUpdateInfo);
        if (currentState != 0) {
            initView(context, currentState);
        }
    }


    private void initData(UpdateInfo info) {
        UpdateInfo.UpdateType type = info.getUpdateType();
        if (type == UpdateInfo.UpdateType.unnecessary) {
            this.setVisibility(View.GONE);
        } else {
            UpdateInfo.BaseInfo baseInfo = info.getTitle();
            if (baseInfo.isShow > 0 ) {
                currentState |= SHOW_UPDATE_TITLE;
            }
            UpdateInfo.BaseInfo baseInfo2 = info.getVersion();
            if (baseInfo2.isShow > 0) {
                currentState |= SHOW_NEW_APPVERSION;
            }

            UpdateInfo.DetailsInfo detailsInfo = info.getDetails();
            if (detailsInfo.isShow > 0) {
                currentState |= SHOW_UPDATE_CONTENT;
            }
            UpdateInfo.UrlInfo updateNowInfo = info.getDownloadUrlBtn();
            if (updateNowInfo.isShow > 0) {
                currentState |= SHOW_UPDATE_NOW_BUTTON;
            }

            UpdateInfo.UrlInfo checkDetailInfo = info.getDetailsUrlBtn();
            if (checkDetailInfo.isShow > 0) {
                currentState |= SHOW_CHECK_UPDATE_BUTTON;
            }

            if (type != UpdateInfo.UpdateType.mandatoryUpdate) {
                UpdateInfo.IgnoreInfo ignoreInfo = info.getIsIgnore();
                if (ignoreInfo.isShow > 0) {
                    currentState |= SHOW_IGNORE_UPDATE_BUTTON;
                }
            }
        }
    }


    private void initView(Context context, int state) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShadowLayer(ShareData.PxToDpi_xhdpi(2), 0, 0, 0x50888888);

        LinearLayout bottomLayer = new LinearLayout(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                RectF rectF = new RectF(ShareData.PxToDpi_xhdpi(1), ShareData.PxToDpi_xhdpi(1), this.getWidth() - ShareData.PxToDpi_xhdpi(1), this.getHeight() - ShareData.PxToDpi_xhdpi(1));
                canvas.drawRoundRect(rectF, ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(30), mPaint);
            }
        };

        bottomLayer.setClickable(true);
        bottomLayer.setWillNotDraw(false);
        bottomLayer.setGravity(Gravity.CENTER_HORIZONTAL);
        bottomLayer.setOrientation(LinearLayout.VERTICAL);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            bottomLayer.setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        }

        FrameLayout.LayoutParams bLayerParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
        bLayerParams.topMargin = ShareData.PxToDpi_xhdpi(87);
        this.addView(bottomLayer, bLayerParams);
        // 底层的linearlayout包含的view
        {
            //更新版本标题
            if ((state & SHOW_UPDATE_TITLE) > 0) {
                TextView headText = new TextView(context);
                headText.setText(mUpdateInfo.getTitle().val);
                headText.setGravity(Gravity.BOTTOM);
                // 这里在headText设置padding top为95是为了解决某些android系统在linearlayout中设置padding无效的bug....ex : color os
                headText.setPadding(0, ShareData.PxToDpi_xhdpi(111), 0, 0);
                headText.setLines(1);
                headText.setMaxLines(1);
                headText.setSingleLine(true);
                headText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                headText.setTypeface(Typeface.DEFAULT_BOLD);
                headText.setTextColor(Color.parseColor("#333333"));
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                bottomLayer.addView(headText, params3);
            }

            if ((state & SHOW_NEW_APPVERSION) > 0) {
                RelativeLayout versionContainer = new RelativeLayout(context);
                LinearLayout.LayoutParams paramsVersion = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsVersion.topMargin = ShareData.PxToDpi_xhdpi(12);
                bottomLayer.addView(versionContainer, paramsVersion);

                // 版本号界面
                {
                    View dividerLine = new View(context);
                    dividerLine.setBackgroundResource(R.drawable.web_update_version_divider);
                    RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ShareData.PxToDpi_xhdpi(500), 1);
                    params4.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                    versionContainer.addView(dividerLine, params4);

                    TextView updateAppVersionText = new TextView(context);
                    updateAppVersionText.setText(mUpdateInfo.getVersion().val);
                    updateAppVersionText.setLines(1);
                    updateAppVersionText.setMaxLines(1);
                    updateAppVersionText.setSingleLine(true);
                    updateAppVersionText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                    updateAppVersionText.setTextColor(Color.parseColor("#000000"));
                    updateAppVersionText.setGravity(Gravity.CENTER);
                    updateAppVersionText.setIncludeFontPadding(false);
                    updateAppVersionText.setBackgroundResource(R.drawable.web_update_versionnumber_bg);
                    RelativeLayout.LayoutParams params5 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params5.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    versionContainer.addView(updateAppVersionText, params5);
                }
            }

            ScrollView scrollView = new ScrollView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(256));
            layoutParams.topMargin = ShareData.PxToDpi_xhdpi(33);
            scrollView.setLayoutParams(layoutParams);
            bottomLayer.addView(scrollView);

            if ((state & SHOW_UPDATE_CONTENT) > 0) {
                // 更新内容描述界面
                ArrayList<String> infoList = mUpdateInfo.getDetails().vals;
                LinearLayout updateContentContainer = new LinearLayout(context);
                updateContentContainer.setPadding(ShareData.PxToDpi_xhdpi(26), 0, ShareData.PxToDpi_xhdpi(26), 0);
                updateContentContainer.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                scrollView.addView(updateContentContainer, layoutParams2);

                for (String item : infoList) {
                    MutipleLineTextLayout textLayout = new MutipleLineTextLayout(context);
                    textLayout.setUpText(item);
                    LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if (infoList.indexOf(item) != 0) {
                        params7.topMargin = ShareData.PxToDpi_xhdpi(16);
                    } else if (infoList.indexOf(item) == infoList.size() - 1) {
                        params7.topMargin = ShareData.PxToDpi_xhdpi(40);
                    }
                    updateContentContainer.addView(textLayout, params7);
                }
            }

            //选择按钮界面
            LinearLayout bottomContainer = new LinearLayout(context);
            bottomContainer.setOrientation(LinearLayout.HORIZONTAL);
            bottomContainer.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params8 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(146));
            params8.gravity = Gravity.CENTER_HORIZONTAL;
            bottomLayer.addView(bottomContainer, params8);
            {
                if ((state & SHOW_CHECK_UPDATE_BUTTON) > 0) {
                    TextView checkDetails = new TextView(context);
                    checkDetails.setLines(1);
                    checkDetails.setMaxLines(1);
                    checkDetails.setSingleLine(true);
                    checkDetails.setClickable(true);
                    checkDetails.setFocusable(true);
                    checkDetails.setGravity(Gravity.CENTER);
                    checkDetails.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    checkDetails.setText(mUpdateInfo.getDetailsUrlBtn().name);
                    checkDetails.setBackgroundResource(R.drawable.web_update_version_details);
                    checkDetails.setTextColor (getTextColorState (getResources()
                            .getColor (R.color.update_version_detail_default), getResources()
                            .getColor (R.color.update_version_detail_selected)));
                    LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(238), ShareData.PxToDpi_xhdpi(78));

                    bottomContainer.addView(checkDetails, params9);
                    checkDetails.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.onClickCheckDetail(mUpdateInfo.getDetailsUrlBtn().val);
                            }
                        }
                    });
                }

                if ((state & SHOW_UPDATE_NOW_BUTTON) > 0) {
                    TextView updateNow = new TextView(context);
                    updateNow.setLines(1);
                    updateNow.setMaxLines(1);
                    updateNow.setSingleLine(true);
                    updateNow.setGravity(Gravity.CENTER);
                    updateNow.setClickable(true);
                    updateNow.setFocusable(true);
                    updateNow.setText(mUpdateInfo.getDownloadUrlBtn().name);
                    updateNow.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                    updateNow.setBackgroundResource(R.drawable.web_update_button_bg);
                    updateNow.setTextColor (getTextColorState(getResources ()
                            .getColor (R.color.update_version_update_default), getResources ()
                            .getColor (R.color.update_version_update_selected)));
                    LinearLayout.LayoutParams params10 = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(238), ShareData.PxToDpi_xhdpi(78));

                    if (((state & SHOW_CHECK_UPDATE_BUTTON) > 0)) {
                        params10.leftMargin = ShareData.PxToDpi_xhdpi(34);
                    }

                    bottomContainer.addView(updateNow, params10);
                    updateNow.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mListener != null) {
                                mListener.onClickUpdateNow(mUpdateInfo.getDownloadUrlBtn().val);
                            }
                        }
                    });
                    ImageUtils.AddSkin(context, ((BitmapDrawable)updateNow.getBackground()).getBitmap());
                }
            }

            if ((state & SHOW_IGNORE_UPDATE_BUTTON) > 0) {
                TextView ignore = new TextView(context);
                ignore.setLines(1);
                ignore.setIncludeFontPadding(false);
                ignore.setMaxLines(1);
                ignore.setSingleLine(true);
                ignore.setGravity(Gravity.CENTER);
                ignore.setClickable(true);
                ignore.setText(mUpdateInfo.getIsIgnore().name);
                ignore.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                ignore.setTextColor (getTextColorState (getResources ()
                        .getColor (R.color.update_version_ignore_default), getResources ()
                        .getColor (R.color.update_version_ignore_selected)));

                LinearLayout.LayoutParams params13 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params13.topMargin = ShareData.PxToDpi_xhdpi(32);
                params13.bottomMargin = ShareData.PxToDpi_xhdpi(32);
                ignore.setLayoutParams(params13);
                ignore.setPadding(ShareData.PxToDpi_xhdpi(10), 0, ShareData.PxToDpi_xhdpi(10), 0);
                bottomLayer.addView(ignore);
                ignore.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onClickIgnoreUpdate();
                        }
                    }
                });
            }
        }

//         App的Logo
        ImageView appIcon = new ImageView(context);
        appIcon.setImageResource(R.drawable.web_update_version_icon);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        this.addView(appIcon, params2);
    }


    public void setOnUpdateDialogClickListener(OnUpdateDialogClickListener listener) {
        this.mListener = listener;
    }

    public void clear() {
        mListener = null;
    }


    private  ColorStateList getTextColorState(int normal,int pressed) {
        ColorStateList colorStates = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{
                        pressed,
                        normal});
        return colorStates;
    }
}
