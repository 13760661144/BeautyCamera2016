package cn.poco.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by zwq on 2016/07/26 10:27.<br/><br/>
 */
public class MyProgressDialog extends AlertDialogV1 {

    private static final String TAG = MyProgressDialog.class.getName();
    private LinearLayout.LayoutParams lParams;
    private LinearLayout container;
    private ProgressBar mProgressBar;
    private TextView mMsgView;

    public MyProgressDialog(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        container = new LinearLayout(getContext());
        container.setGravity(Gravity.CENTER_VERTICAL);
        container.setPadding(ShareData.getRealPixel_720P(20), ShareData.getRealPixel_720P(20), ShareData.getRealPixel_720P(20), ShareData.getRealPixel_720P(20));
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        addContentView(container, lParams);

        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setIndeterminateDrawable(getContext().getResources().getDrawable(R.drawable.video_progress_bar_bg));
        lParams = new LinearLayout.LayoutParams(ShareData.getRealPixel_720P(100), ShareData.getRealPixel_720P(100));
        lParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(mProgressBar, lParams);

        mMsgView = new TextView(getContext());
        mMsgView.setGravity(Gravity.CENTER_VERTICAL);
        mMsgView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mMsgView.setTextColor(Color.BLACK);//0xff71b4e4
        mMsgView.setPadding(ShareData.getRealPixel_720P(10), 0, 0, 0);
        lParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(mMsgView, lParams);
    }

    public void setMessage(String msg) {
        mMsgView.setText(msg);
    }

}
