package cn.poco.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

/**
 * Created by zwq on 2016/11/17 10:55.<br/><br/>
 * 倒计时
 */
public class CountDownView extends AppCompatTextView
{

    private boolean mDoingRotationAnim = false;
    private int mLastDegree;
    private int mDegree;
    private int mAnimTargetDegree;

    private boolean mUseTextControlVisible;

    public CountDownView(Context context) {
        super(context);

        setMinimumWidth(ShareData.getRealPixel_720P(40));
        setMinimumHeight(ShareData.getRealPixel_720P(40));
        setGravity(Gravity.CENTER);
        setTextColor(0xffffffff);

        setUseTextControlVisible(true);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 44);
        setBackgroundResource(R.drawable.camera_count_down_bg);
    }

    /**
     * 通过文本控制显示
     * @param control
     */
    public void setUseTextControlVisible(boolean control) {
        mUseTextControlVisible = control;
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (mUseTextControlVisible) {
            if (text == null || text.toString().trim().isEmpty()) {
                setVisibility(View.GONE);
            } else {
                if (getVisibility() != View.VISIBLE) {
                    setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void startRotationAnim(final int degree)
    {
        if (mDegree != degree)
        {
            mLastDegree = mDegree % 360;
            mDegree = (degree + 360) % 360;

            if (mLastDegree == 270 && mDegree == 0)
            {
                mDegree = 360;
            }

            if (mLastDegree == 0 && mDegree == 270)
            {
                mLastDegree = 360;
            }

            if (!mDoingRotationAnim)
            {
                mDoingRotationAnim = true;
                createRotationAnim();
            }
        }
    }

    private void createRotationAnim()
    {
        mAnimTargetDegree = mDegree;
        final int mAnimLastDegree = mLastDegree;

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                float value = (float) animation.getAnimatedValue();
                int degree = (int) (mAnimLastDegree + (mAnimTargetDegree - mAnimLastDegree) * value);
                setRotation(degree);
            }
        });

        animator.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                if (mAnimTargetDegree != mDegree)
                {
                    createRotationAnim();
                }
                else
                {
                    mDoingRotationAnim = false;
                }
            }
        });
        animator.start();
    }
}
