package cn.poco.widget;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.MyTextButton;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/8/4.
 */

public class SdkOutDatedDialog {
    public interface SdkDialogCallback {
        void updateNow();
        void updateLater();
    }

    private FullScreenDlg mFullScreenDialog;
    private Context mContext;

    public SdkOutDatedDialog(Activity activity) {
        mFullScreenDialog = new FullScreenDlg(activity, R.style.dialog);
        mContext = activity;
        View mainView = getInitView();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), ShareData.PxToDpi_xhdpi(558), Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        mFullScreenDialog.AddView(mainView, params);
    }

    private Paint mPaint;
    private MyTextButton mConfirmBtn;
    private TextView mCancelBtn;
    private TextView mSubTitle;

    private View getInitView() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.WHITE);

        LinearLayout container = new LinearLayout(mContext) {
            @Override
            protected void onDraw(Canvas canvas) {
                RectF rect = new RectF(0, ShareData.PxToDpi_xhdpi(100), getWidth(), getHeight());
                canvas.drawRoundRect(rect, ShareData.PxToDpi_xhdpi(30), ShareData.PxToDpi_xhdpi(30), mPaint);
            }
        };
        container.setWillNotDraw(false);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);

        // 应用图标
        ImageView mAppIcon = new ImageView(mContext);
        mAppIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mAppIcon.setImageResource(R.drawable.web_update_version_icon);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mAppIcon.setLayoutParams(params);
        container.addView(mAppIcon);

        //主标题
        TextView mTitle = new TextView(mContext);
        mTitle.setText(R.string.importantUpdate);
        mTitle.setGravity(Gravity.CENTER);
        mTitle.setTypeface(mTitle.getTypeface(), Typeface.BOLD);
        mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        mTitle.setTextColor(0xff333333);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(20);
        mTitle.setLayoutParams(params);
        container.addView(mTitle);

        //副标题
        mSubTitle = new TextView(mContext);
        mSubTitle.setText(R.string.sdkUpdateText);
        mSubTitle.setGravity(Gravity.CENTER);
        mSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mSubTitle.setTextColor(0xe6000000);
        mSubTitle.setLineSpacing(10, 1.0f);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(16);
        mSubTitle.setLayoutParams(params);
        container.addView(mSubTitle);

        //确定按钮
        mConfirmBtn = new MyTextButton(mContext);
        mConfirmBtn.setBk(R.drawable.photofactory_noface_help_btn);
        mConfirmBtn.setName(R.string.updateRightNow, 14, 0xffffffff, true);
        mConfirmBtn.setOnTouchListener(animationListener);

        params = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(418), ShareData.PxToDpi_xhdpi(78));
        params.topMargin = ShareData.PxToDpi_xhdpi(32);
        mConfirmBtn.setLayoutParams(params);
        container.addView(mConfirmBtn);

        //取消按钮
        mCancelBtn = new TextView(mContext);
        mCancelBtn.setGravity(Gravity.CENTER);
        mCancelBtn.setText(R.string.updateLater);
        mCancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        mCancelBtn.setOnTouchListener(animationListener);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mCancelBtn.setLayoutParams(params);
        container.addView(mCancelBtn);
        return container;
    }

    public void SetSubText(String text)
    {
        if (mSubTitle != null)
        {
            mSubTitle.setText(text);
        }
    }

    private OnAnimationClickListener animationListener = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mConfirmBtn) {
                if (mCallback != null) {
                    mCallback.updateNow();
                }
            } else if (v == mCancelBtn) {
                dismiss();
                if (mCallback != null) {
                    mCallback.updateLater();
                }
            }
        }

        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {

        }
    };


    public void show() {
        mFullScreenDialog.show();
    }

    public void dismiss() {
        mFullScreenDialog.dismiss();
    }

    public boolean isShowingDialog() {
        return mFullScreenDialog.isShowing();
    }

    private SdkDialogCallback mCallback;
    public void setCallback(SdkDialogCallback callback) {
        mCallback = callback;
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener listener) {
        mFullScreenDialog.setOnCancelListener(listener);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener listener) {
        mFullScreenDialog.setOnDismissListener(listener);
    }

}
