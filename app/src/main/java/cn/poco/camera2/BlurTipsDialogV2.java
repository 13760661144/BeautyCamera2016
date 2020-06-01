package cn.poco.camera2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import my.beautyCamera.R;

/**
 * @author lmx
 *         Created by lmx on 2016/9/22.
 */
public class BlurTipsDialogV2 extends RelativeLayout {
    private Context mContext;
    private String mTitleStr;
    private String mContentStr;
    private String mCancelStr;
    private String mOkStr;
    private boolean isShowCancel;
    private boolean isShowOk;
    private BlurTipsClickCallback mClickCallback;

    private int mWidth = ShareData.PxToDpi_xhdpi(568);
    private int mHeight = ShareData.PxToDpi_xhdpi(404);

    //    private RelativeLayout.LayoutParams rlParams;
    private LinearLayout.LayoutParams lParams;
    private LinearLayout mContainer;
    private Button cancelBtn;
    private Button okBtn;

    private FullScreenDlg dialog;

    public BlurTipsDialogV2(Context mContext,
                            @NonNull String mTitleStr, @NonNull String mContentStr,
                            @NonNull String mCancelStr, @NonNull String mOkStr,
                            boolean isShowCancel, boolean isShowOk, BlurTipsClickCallback mClickCallback) {
        super(mContext);
        this.mContext = mContext;
        this.mTitleStr = mTitleStr;
        this.mContentStr = mContentStr;
        this.mCancelStr = mCancelStr;
        this.mOkStr = mOkStr;
        this.isShowCancel = isShowCancel;
        this.isShowOk = isShowOk;
        this.mClickCallback = mClickCallback;
        init();
    }

    private void init() {
        ShareData.InitData(mContext);
        setLayoutParams(new LayoutParams(mWidth, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);
        setVisibility(VISIBLE);
        setBackgroundColor(0x00000000);
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        mContainer = new LinearLayout(mContext);
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mContainer.setGravity(Gravity.CENTER);
        mContainer.setBackgroundDrawable(DrawableUtils.shapeDrawable(0xffffffff, ShareData.getRealPixel_720P(32)));

        LayoutParams rlParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rlParams.addRule(CENTER_IN_PARENT);// 居中
        rlParams.addRule(CENTER_HORIZONTAL);

        this.addView(mContainer, rlParams);
        {
            lParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lParams.topMargin = ShareData.PxToDpi_xhdpi(20);
            TextView titleTv, contentTv;
            if (!TextUtils.isEmpty(mTitleStr)) {
                titleTv = new TextView(mContext);
                titleTv.setPadding(ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(10));
                titleTv.setText(mTitleStr);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17.0f);
                titleTv.setGravity(Gravity.CENTER);
                titleTv.setTextColor(0xff000000);
                titleTv.setBackgroundColor(Color.TRANSPARENT);
                mContainer.addView(titleTv, lParams);
            }

            lParams = new LinearLayout.LayoutParams(mWidth, LayoutParams.WRAP_CONTENT);
            if (!TextUtils.isEmpty(mContentStr)) {
                contentTv = new TextView(mContext);
                contentTv.setPadding(ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(10), ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(10));
                contentTv.setText(mContentStr);
                contentTv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                contentTv.setGravity(Gravity.CENTER);
                contentTv.setTextColor(0xff000000);
                contentTv.setBackgroundColor(Color.TRANSPARENT);
                mContainer.addView(contentTv, lParams);
            }

            LinearLayout clickLayout = new LinearLayout(mContext);
            clickLayout.setOrientation(LinearLayout.HORIZONTAL);
            clickLayout.setBackgroundColor(Color.TRANSPARENT);
            clickLayout.setPadding(ShareData.PxToDpi_xhdpi(20), 0, ShareData.PxToDpi_xhdpi(20), 0);
            lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(80));
            lParams.gravity = Gravity.CENTER_VERTICAL;
            lParams.topMargin = ShareData.PxToDpi_xhdpi(60);
            lParams.bottomMargin = ShareData.PxToDpi_xhdpi(50);
            mContainer.addView(clickLayout, lParams);

            if (isShowCancel) {
                cancelBtn = new Button(mContext);
                cancelBtn.setText(mCancelStr);
                cancelBtn.setBackgroundDrawable(DrawableUtils.shapeDrawable(ImageUtils.GetSkinColor(0xffe75887), ShareData.PxToDpi_xhdpi(40)));
                cancelBtn.setTextColor(0xffffffff);
                cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                cancelBtn.getPaint().setFakeBoldText(true);
                cancelBtn.setGravity(Gravity.CENTER);
                cancelBtn.setOnClickListener(mOnClickListener);
                lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                if (isShowCancel && isShowOk) {
                    lParams.leftMargin = ShareData.PxToDpi_xhdpi(15);
                    lParams.rightMargin = ShareData.PxToDpi_xhdpi(15);
                } else {
                    lParams.leftMargin = ShareData.PxToDpi_xhdpi(40);
                    lParams.rightMargin = ShareData.PxToDpi_xhdpi(40);
                }
                lParams.weight = 1.0f;
//                cancelBtn.setPadding(0, ShareData.PxToDpi_xhdpi(10), 0, ShareData.PxToDpi_xhdpi(10));
                clickLayout.addView(cancelBtn, lParams);
            }

            if (isShowOk) {
                okBtn = new Button(mContext);
                okBtn.setText(mOkStr);
                okBtn.setBackgroundDrawable(DrawableUtils.shapeDrawable(ImageUtils.GetSkinColor(0xffe75887), ShareData.PxToDpi_xhdpi(40)));
                okBtn.setTextColor(0xffffffff);
                okBtn.getPaint().setFakeBoldText(true);
                okBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
                okBtn.setGravity(Gravity.CENTER);
                okBtn.setOnClickListener(mOnClickListener);
//                okBtn.setPadding(0, ShareData.PxToDpi_xhdpi(10), 0, ShareData.PxToDpi_xhdpi(10));
                lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                if (isShowCancel && isShowOk) {
                    lParams.leftMargin = ShareData.PxToDpi_xhdpi(15);
                    lParams.rightMargin = ShareData.PxToDpi_xhdpi(15);
                } else {
                    lParams.leftMargin = ShareData.PxToDpi_xhdpi(40);
                    lParams.rightMargin = ShareData.PxToDpi_xhdpi(40);
                }
                lParams.weight = 1.0f;
                clickLayout.addView(okBtn, lParams);
            }

        }

    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == okBtn) {
                if (mClickCallback != null) {
                    mClickCallback.onClickOk();
                }
                dissmissDialog();
            } else if (v == cancelBtn) {
                if (mClickCallback != null) {
                    mClickCallback.onClickCancel();
                }
                dissmissDialog();
            } else if (v == BlurTipsDialogV2.this) {
                dissmissDialog();
            }
        }
    };

    public void showDialog() {
        if (dialog == null) {
            dialog = new FullScreenDlg((Activity) getContext(), R.style.dialog);
        }
        dialog.m_fr.removeAllViews();
        dialog.m_fr.addView(this);
        dialog.show();
    }

    public void dissmissDialog() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public interface BlurTipsClickCallback {
        void onClickOk();

        void onClickCancel();
    }
}
