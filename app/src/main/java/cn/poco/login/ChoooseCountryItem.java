package cn.poco.login;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class ChoooseCountryItem extends LinearLayout{

    private Context mContext;

    private final int mBgColor = Color.WHITE;

    public RelativeLayout mRlTag;
    public View mdivTop;
    public TextView mChoooseCountryTag;
    private View mdivBottom;
    public FrameLayout mflChooseCountry;
    public View mdivTop1;
    public TextView mCountry;
    public TextView mPhone;

    public ChoooseCountryItem(Context context) {
        super(context);
        mContext=context;
        this.setOrientation(LinearLayout.VERTICAL);
        initUI();
    }

    private void initUI()
    {
        LayoutParams llParams;
        RelativeLayout.LayoutParams rlParams;

        this.setBackgroundColor(mBgColor);

        //条目标志
        llParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(42));
        llParams.weight=1;
        mRlTag=new RelativeLayout(mContext);
        this.addView(mRlTag, llParams);
        {
            //分割线
            rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mdivTop = new View(mContext);
            mRlTag.addView(mdivTop, rlParams);
            mdivTop.setBackgroundResource(R.drawable.beauty_login_line);

            rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            rlParams.leftMargin=ShareData.PxToDpi_xhdpi(44);
//            llParams.weight = 1;
            mChoooseCountryTag = new TextView(mContext);
            mRlTag.addView(mChoooseCountryTag, rlParams);
            mChoooseCountryTag.setGravity(Gravity.CENTER_VERTICAL);
            mChoooseCountryTag.setTextColor(0xff333333);
            mChoooseCountryTag.setTextSize(13.5f);

          //分割线
            rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
            rlParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            mdivBottom = new View(mContext);
            mRlTag.addView(mdivBottom, rlParams);
            mdivBottom.setBackgroundResource(R.drawable.beauty_login_line);
        }

        //条目
        llParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(112));
        llParams.weight=1;
        llParams.gravity=Gravity.CENTER_VERTICAL;
        mflChooseCountry =new FrameLayout(mContext);
        this.addView(mflChooseCountry, llParams);
//        mflChooseCountry.setBackgroundColor(0xffeaf0eb);
        mflChooseCountry.setPadding(ShareData.PxToDpi_xhdpi(90), 0, 0, 0);
        {
            FrameLayout.LayoutParams flParams;
            //分割线
            flParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 1);
            flParams.gravity=Gravity.CENTER_HORIZONTAL|Gravity.TOP;
            mdivTop1=new View(mContext);
            mflChooseCountry.addView(mdivTop1,flParams);
            mdivTop1.setBackgroundResource(R.drawable.beauty_login_line);

            flParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            flParams.gravity= Gravity.CENTER_VERTICAL|Gravity.LEFT;
            mCountry=new TextView(mContext);
            mflChooseCountry.addView(mCountry, flParams);
            mCountry.setTextColor(0xff707070);
            mCountry.setTextSize(15.5f);

            flParams=new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            flParams.rightMargin=ShareData.PxToDpi_xhdpi(58);
            flParams.gravity=Gravity.CENTER_VERTICAL|Gravity.RIGHT;
            mPhone=new TextView(mContext);
            mflChooseCountry.addView(mPhone, flParams);
            mPhone.setTextColor(0xff535f56);
            mPhone.setTextSize(13.5f);
            mPhone.setText("+86");
        }
    }
}
