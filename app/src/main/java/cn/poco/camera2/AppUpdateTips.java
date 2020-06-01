package cn.poco.camera2;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.DrawableUtils;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * Created by zwq on 2016/12/28 15:08.<br/><br/>
 */
public class AppUpdateTips extends AlertDialogV1 implements View.OnClickListener {

    private LinearLayout container;
    private LinearLayout.LayoutParams lParams;
    private TextView confirmBtn;
    private TextView cancelBtn;

    public AppUpdateTips(Context context) {
        super(context);

        initView();
    }

    private void initView() {
        container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);
        lParams = new LinearLayout.LayoutParams((int) (ShareData.getScreenW() * 0.8f), LinearLayout.LayoutParams.WRAP_CONTENT);
        addContentView(container, lParams);

        setRadius(ShareData.getRealPixel_720P(45));

        TextView title = new TextView(getContext());
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);
        title.setTextColor(Color.BLACK);
        title.getPaint().setFakeBoldText(true);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(20));
        title.setText(R.string.camerapage_sdk_out_of_date_update_tip);
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(title, lParams);

        TextView msg = new TextView(getContext());
        msg.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);
        msg.setTextColor(Color.BLACK);
        msg.setGravity(Gravity.CENTER_HORIZONTAL);
        msg.setPadding(ShareData.PxToDpi_xhdpi(15), 0, ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15));
        msg.setText(R.string.camerapage_sdk_out_of_date_tip_msg);
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        container.addView(msg, lParams);

        confirmBtn = new TextView(getContext());
        confirmBtn.setClickable(true);
        confirmBtn.setOnClickListener(this);
        confirmBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
        confirmBtn.setTextColor(DrawableUtils.colorPressedDrawable2(0xffffffff, 0x99ffffff));
        confirmBtn.setGravity(Gravity.CENTER);
        confirmBtn.setPadding(ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15));
        confirmBtn.setText(R.string.camerapage_sdk_out_of_date_tip_download);
        confirmBtn.setBackgroundDrawable(getShapeDrawable(ImageUtils.GetSkinColor(0xffe75886)));
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.leftMargin = ShareData.PxToDpi_xhdpi(50);
        lParams.rightMargin = ShareData.PxToDpi_xhdpi(50);
        lParams.topMargin = ShareData.PxToDpi_xhdpi(30);
        container.addView(confirmBtn, lParams);

        cancelBtn = new TextView(getContext());
        cancelBtn.setClickable(true);
        cancelBtn.setOnClickListener(this);
        cancelBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15.0f);
        cancelBtn.setTextColor(DrawableUtils.colorPressedDrawable2(0xffa0a0a0, 0x99a0a0a0));
        cancelBtn.setGravity(Gravity.CENTER);
        cancelBtn.setPadding(ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15), ShareData.PxToDpi_xhdpi(15));
        cancelBtn.setText(R.string.camerapage_sdk_out_of_date_tip_cancel);
        cancelBtn.setBackgroundDrawable(getShapeDrawable(0x00000000));
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.leftMargin = ShareData.PxToDpi_xhdpi(50);
        lParams.rightMargin = ShareData.PxToDpi_xhdpi(50);
        lParams.bottomMargin = ShareData.PxToDpi_xhdpi(10);
        container.addView(cancelBtn, lParams);
    }

    @Override
    public void show() {
        setCanceledOnTouchOutside(true);
        super.show();
    }

    @Override
    public void onClick(View v) {
        if (v == confirmBtn) {
            if (mListener != null) {
                mListener.onClick(this, 0);
            }
            this.dismiss();
        } else if (v == cancelBtn) {
            this.dismiss();
        }
    }
}
