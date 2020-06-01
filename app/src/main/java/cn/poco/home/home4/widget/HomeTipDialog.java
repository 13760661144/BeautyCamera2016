package cn.poco.home.home4.widget;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.poco.home.home4.introAnimation.Config;
import cn.poco.home.home4.utils.PercentUtil;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

import static cn.poco.home.home4.utils.PercentUtil.HeightPxxToPercent;
import static cn.poco.home.home4.utils.PercentUtil.WidthPxxToPercent;

/**
 * Created by lgd on 2017/9/13.
 */

public class HomeTipDialog extends FullScreenDlg
{
    private ImageView mOkBtn;
    private ImageView mArrows;
    private ImageView mCircleMask;
    private Callback mCallback;
    private TextView mTipText;

    public HomeTipDialog(Activity activity)
    {
        super(activity, R.style.homeDialog);
        getWindow().setWindowAnimations(R.style.homeTipAnimation);
        initUi();
    }

    private void initUi()
    {
        FrameLayout.LayoutParams fl;
        mCircleMask = new ImageView(getContext());
        mCircleMask.setImageResource(R.drawable.home4_frist_tip_circle_mask);
        fl = new FrameLayout.LayoutParams(WidthPxxToPercent(465), HeightPxxToPercent(465));
        fl.gravity = Gravity.CENTER;
        fl.bottomMargin = Config.AD_CENTER_BOTTOM_MARGIN;
        m_fr.addView(mCircleMask, fl);

        View mask = new View(getContext());
        mask.setBackgroundColor(Color.BLACK);
        mask.setAlpha(0.76f);
        fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        m_fr.addView(mask, fl);

        mArrows = new ImageView(getContext());
        mArrows.setImageResource(R.drawable.home4_frist_tip_arrows);
        fl = new FrameLayout.LayoutParams(WidthPxxToPercent(728), HeightPxxToPercent(576));
        fl.gravity = Gravity.CENTER;
        fl.bottomMargin = Config.AD_CENTER_BOTTOM_MARGIN;
        m_fr.addView(mArrows, fl);

        mTipText = new TextView(getContext());
        mTipText.setText("上下左右滑动\n有惊喜");
        mTipText.setTextSize(23);
        mTipText.getPaint().setFakeBoldText(true);
        mTipText.setGravity(Gravity.CENTER);
        mTipText.setTextColor(Color.WHITE);
        mTipText.setLineSpacing(PercentUtil.HeightPxToPercent(4),1f);
        fl = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        fl.gravity = Gravity.CENTER;
        fl.bottomMargin = Config.AD_CENTER_BOTTOM_MARGIN;
        m_fr.addView(mTipText, fl);

        mOkBtn = new ImageView(getContext());
        mOkBtn.setImageResource(R.drawable.home4_frist_tip_ok);
        fl = new FrameLayout.LayoutParams(WidthPxxToPercent(411), HeightPxxToPercent(411));
        fl.gravity = Gravity.CENTER;
        fl.topMargin = Config.CAMERA_CENTER_TOP_MARGIN;
        m_fr.addView(mOkBtn, fl);
//        mOkBtn.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                if (event.getAction() == MotionEvent.ACTION_DOWN)
//                {
//                    v.setAlpha(0.7f);
//                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
//                {
//                    v.setAlpha(1f);
//                }
//                return false;
//            }
//        });
//        mOkBtn.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                if (mCallback != null)
//                {
//                    mCallback.onOk();
//                }
//            }
//        });
        mOkBtn.setOnTouchListener(new OnAnimationClickListener()
        {
            @Override
            public void onAnimationClick(View v)
            {
                if (mCallback != null)
                {
                    mCallback.onOk();
                }
            }
        });
    }

    public void setCallback(Callback callback)
    {
        this.mCallback = callback;
    }

    public interface Callback
    {
        void onOk();
    }
}
