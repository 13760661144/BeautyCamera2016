package cn.poco.camera2;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import cn.poco.widget.AlertDialogV1;
import my.beautyCamera.R;

/**
 * Created by zwq on 2016/06/23 17:31.<br/><br/>
 */
public class CameraErrorTipsDialog extends AlertDialogV1 implements View.OnClickListener {

    private static final String TAG = CameraErrorTipsDialog.class.getName();
    private LinearLayout tipLayout;
    private TextView tipText;
    private Button okBtn;
    private boolean closePage = true;

    public CameraErrorTipsDialog(Context context) {
        super(context);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
    }

    private void initView() {
        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams((int) (ShareData.m_screenWidth * 0.82f), LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout container = new LinearLayout(getContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);
        container.setBackgroundColor(0xff404040);
        addContentView(container, lParams);

        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        TextView title = new TextView(getContext());
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        title.setTextColor(Color.WHITE);
        title.getPaint().setFakeBoldText(true);
        title.setGravity(Gravity.CENTER_HORIZONTAL);
        title.setPadding(0, ShareData.PxToDpi_xhdpi(50), 0, ShareData.PxToDpi_xhdpi(50));
        title.setText(getContext().getString(R.string.camerapage_camera_permission_limit));
        container.addView(title, lParams);

        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView contentView = new TextView(getContext());
        contentView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        contentView.setTextColor(0xffdddddd);
        contentView.setPadding(ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(40), ShareData.PxToDpi_xhdpi(50));
        contentView.setText(getContext().getResources().getString(R.string.camerapage_camera_open_fail));
        container.addView(contentView, lParams);

        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipLayout = new LinearLayout(getContext());
        tipLayout.setOnClickListener(this);
        tipLayout.setGravity(Gravity.CENTER_VERTICAL);
        tipLayout.setPadding(ShareData.PxToDpi_xhdpi(40), 0, ShareData.PxToDpi_xhdpi(40), ShareData.PxToDpi_xhdpi(60));
        container.addView(tipLayout, lParams);

        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView tipIcon = new ImageView(getContext());
        tipIcon.setImageResource(R.drawable.camera_open_permissions_tip);
        tipLayout.addView(tipIcon, lParams);

        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        tipText = new TextView(getContext());
        tipText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        tipText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tipText.setTextColor(0xffffc433);
        tipText.setPadding(ShareData.PxToDpi_xhdpi(12), 0, 0, 0);
        tipText.setText(getContext().getResources().getString(R.string.camerapage_camera_open_permission_method));
        tipLayout.addView(tipText, lParams);

        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(86));
        okBtn = new Button(getContext());
        okBtn.setText(getContext().getResources().getString(R.string.ok));
        okBtn.setTextColor(0xffffc433);
        okBtn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        okBtn.setOnClickListener(this);
        okBtn.setBackgroundDrawable(getPressedDrawable(0xff262626, 0x7f262626));
        container.addView(okBtn, lParams);
    }

    @Override
    public void onClick(View v) {
        if (v == tipLayout || v == tipText) {
            closePage = false;
            if (mListener != null) {
                mListener.onClick(this, 0);
            }
        } else if (v == okBtn) {
            closePage = true;
            if (mListener != null) {
                mListener.onClick(this, 1);
            }
        }
    }

    public void setCanClosePage(boolean close) {
        closePage = close;
    }

    public boolean canClosePage() {
        return closePage;
    }
}
