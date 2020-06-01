package cn.poco.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.poco.utils.DrawableUtils;
import cn.poco.utils.ScreenUtils;

/**
 * 用户积分提示
 * Created by MarkChan on 16/6/13.
 */
public class CustomerPointToast extends LinearLayout {

    private Context context;
    private LinearLayout container;
    private TextView descritionView;//描述、说明
    private TextView addSymbol;// +号
    private TextView pointView;//积分
    private CustomToast mToast;

    /**
     * 用户积分
     * @param context
     */
    public CustomerPointToast(Context context) {
        super(context);
        this.context = context;
        initToastView();
    }

    private void initToastView() {
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(context), ScreenUtils.getRealPixel3(context, 350));
        RelativeLayout root = new RelativeLayout(getContext());
        addView(root, rParams);

        rParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rParams.topMargin = ScreenUtils.getRealPixel3(context, 186);
        container = new LinearLayout(getContext());
        container.setBackgroundDrawable(DrawableUtils.shapeDrawable(0x9e000000, ScreenUtils.getRealPixel3(context, 45)));
        container.setPadding(ScreenUtils.getRealPixel3(context, 20), ScreenUtils.getRealPixel3(context, 15), ScreenUtils.getRealPixel3(context, 20), ScreenUtils.getRealPixel3(context, 15));
        root.addView(container, rParams);

        LayoutParams lParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descritionView = new TextView(getContext());
        descritionView.setTextColor(Color.WHITE);
        descritionView.setTextSize(14);
        container.addView(descritionView, lParams);

        lParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lParams.leftMargin = ScreenUtils.getRealPixel3(context, 8);
        addSymbol = new TextView(getContext());
        addSymbol.setTextColor(Color.WHITE);
        addSymbol.setTextSize(16);
        addSymbol.setText("+");
        container.addView(addSymbol, lParams);

        lParams = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pointView = new TextView(getContext());
        pointView.setTextColor(Color.WHITE);
        pointView.setTextSize(16);
        container.addView(pointView, lParams);

        initToast();
    }

    /**
     * @param descrition 积分说明
     * @param credit 积分
     */
    public void setText(String descrition, String credit) {
        descritionView.setText(descrition);
        pointView.setText(credit);
    }

    private AnimationSet initAnim() {
        AnimationSet animationSet = new AnimationSet(true);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(300);
        animationSet.addAnimation(alphaAnimation);

        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0);
        translateAnimation.setFillAfter(true);
        translateAnimation.setStartOffset(100);
        translateAnimation.setDuration(200);
        animationSet.addAnimation(translateAnimation);

        return animationSet;
    }

    public void startAnim(int duration) {
        AnimationSet animSet;
        animSet = new AnimationSet(true);
        TranslateAnimation transAnim;
        transAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.5f, Animation.RELATIVE_TO_SELF, 0);
        transAnim.setDuration(duration);
        animSet.addAnimation(transAnim);

        RotateAnimation rotateAnim = new RotateAnimation(-10, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnim.setDuration(duration);
        animSet.setFillAfter(true);
        animSet.addAnimation(rotateAnim);
        container.startAnimation(animSet);

        AnimationSet animationSet1 = initAnim();//300
        if (animationSet1 != null) {
            animationSet1.setStartOffset(duration + 100);
            addSymbol.startAnimation(animationSet1);
        }

        AnimationSet animationSet2 = initAnim();
        if (animationSet2 != null) {
            animationSet2.setStartOffset(duration + 400);
            pointView.startAnimation(animationSet2);
        }
    }

    private void initToast() {
        mToast = new CustomToast(getContext());
        mToast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        mToast.setView(this);
        mToast.setDuration(Toast.LENGTH_LONG);
    }

    public void show() {
        if (mToast != null) {
            mToast.show();
            startAnim(800);
        }
    }
}