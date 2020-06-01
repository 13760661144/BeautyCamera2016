package cn.poco.featuremenu.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.featuremenu.cell.CreditCell;
import cn.poco.holder.ObjHolder;
import cn.poco.login.UserMgr;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.SysConfig;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

import static cn.poco.setting.SettingInfoMgr.GetSettingInfo;

/**
 * Created by Shine on 2017/9/6.
 */

public class MenuTopBar extends LinearLayout {
    public interface MenuTopBarCallback {
        void onAvatarClick();
        void onCreditBtnClick();
    }

    private Context mContext;
    private ImageView mUserAvatar;
    private TextView mLoginView;
    private CreditCell mCreditCell;
    private boolean mIsUserLogin;

    private TopbarDefaultDrawable mBackgroundDrawable;
    private ObjHolder<UserMgr.UserInfoCallback> mObjectHolder = new ObjHolder<>();

    public MenuTopBar(Context context, boolean isUserLogin) {
        super(context);
        mContext = context;
        mIsUserLogin = isUserLogin;
        initView();
    }

    private void initView() {
        this.setOrientation(LinearLayout.VERTICAL);

        FrameLayout container = new FrameLayout(mContext);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin = ShareData.PxToDpi_xhdpi(16);
        params.leftMargin = ShareData.PxToDpi_xhdpi(30);
        container.setLayoutParams(params);
        this.addView(container);

        mUserAvatar = new ImageView(mContext);
        FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(110));
        params1.topMargin = ShareData.PxToDpi_xhdpi(6);
        mUserAvatar.setLayoutParams(params1);
        container.addView(mUserAvatar);
        mUserAvatar.setOnClickListener(mViewOnClickListener);

        mLoginView = new TextView(mContext);
        mLoginView.setSingleLine(true);
        mLoginView.setLines(1);
        mLoginView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        mLoginView.setTextColor(Color.WHITE);
        mLoginView.setEllipsize(TextUtils.TruncateAt.END);
//        //限制字数为9个
//        int maxLength = 7;
//        InputFilter[] characterCountFilter = new InputFilter[1];
//        characterCountFilter[0] = new InputFilter.LengthFilter(maxLength);
//        mLoginView.setFilters(characterCountFilter);
        params1 = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(320), ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL);
        params1.leftMargin = ShareData.PxToDpi_xhdpi(132);
        params1.topMargin = ShareData.PxToDpi_xhdpi(12);
        mLoginView.setLayoutParams(params1);
        container.addView(mLoginView);
        mLoginView.setOnClickListener(mViewOnClickListener);

        mCreditCell = new CreditCell(mContext);
        params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        params1.rightMargin = ShareData.PxToDpi_xhdpi(26);
        params1.topMargin = ShareData.PxToDpi_xhdpi(12);
        mCreditCell.setLayoutParams(params1);
        container.addView(mCreditCell);
        mCreditCell.setVisibility(View.GONE);
        mCreditCell.setOnClickListener(mViewOnClickListener);
        
        mBackgroundDrawable = new TopbarDefaultDrawable(0, ShareData.PxToDpi_xhdpi(123), ShareData.m_screenWidth, ShareData.PxToDpi_xhdpi(123), SysConfig.s_skinColor1, SysConfig.s_skinColor2);
        this.setBackgroundDrawable(mBackgroundDrawable);

        mObjectHolder.SetObj(mUserInfoCallback);
        updateUiDependOnState();
    }

    private MenuTopBarCallback mCallback;
    public void setMenuTopBarCallback(MenuTopBarCallback callback) {
        this.mCallback = callback;
    }


    public void updateUiDependOnState() {
        mIsUserLogin = UserMgr.IsLogin(getContext(), null);
        if (mIsUserLogin) {
            // 用户登录的情况
            String userNameText = GetSettingInfo(getContext()).GetPocoNick();
            final String userAvatarUrl = SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2HeadUrl();
            UserMgr.GetUserInfo(mContext, mObjectHolder);
            mLoginView.setText(userNameText);
            ImageLoaderUtil.getBitmapByUrl(mContext, userAvatarUrl, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void loadImageSuccessfully(Object object) {
                    if (userAvatarUrl != null && object instanceof Bitmap) {
                        Bitmap userAvatarBitmap = (Bitmap) object;
                        // 用户已经登录，成功获取头像照片
                        Bitmap avatarRoundedBitmap = ImageUtils.MakeHeadBmp(userAvatarBitmap, ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(3));
                        mUserAvatar.setImageBitmap(avatarRoundedBitmap);
                    }
                }

                @Override
                public void failToLoadImage() {
                    Bitmap defaultBitmap = ImageUtils.MakeHeadBmp(BitmapFactory.decodeResource(getResources(), R.drawable.featuremenu_default_avatar), ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(3));
                    mUserAvatar.setImageBitmap(defaultBitmap);
                }
            });
            mCreditCell.setVisibility(View.VISIBLE);
        } else {
            // 用户没有登录的情况
            mLoginView.setText(R.string.loginHint);
            mUserAvatar.setImageResource(R.drawable.featuremenu_default_avatar);

            Bitmap defaultBitmap = ImageUtils.MakeHeadBmp(BitmapFactory.decodeResource(getResources(), R.drawable.featuremenu_default_avatar), ShareData.PxToDpi_xhdpi(110), ShareData.PxToDpi_xhdpi(3));
            mUserAvatar.setImageBitmap(defaultBitmap);
            mCreditCell.setVisibility(View.GONE);
        }
    }

    public void setCreditButtonVisibility(boolean show) {
        if (mCreditCell != null) {
            if (show && mIsUserLogin) {
                mCreditCell.setVisibility(View.VISIBLE);
            } else {
                mCreditCell.setVisibility(View.GONE);
            }
        }
    }

    public void changeSkin() {
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setGradientColor(SysConfig.s_skinColor1, SysConfig.s_skinColor2);
            mBackgroundDrawable.invalidateSelf();
        }
    }

    public void refreshCredit() {
        if (mObjectHolder != null && mIsUserLogin) {
            UserMgr.GetUserInfo(mContext, mObjectHolder);
            //FIXME 正式版去掉
//            Toast.makeText(getContext(), "刷新积分", Toast.LENGTH_SHORT).show();
        }
    }

    public void clear() {
        mViewOnClickListener = null;
        mBackgroundDrawable = null;
        mObjectHolder.Clear();
    }

    private OnClickListener mViewOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == mCreditCell) {
                if (mCallback != null) {
                    mCallback.onCreditBtnClick();
                }
            } else if (v == mUserAvatar || v == mLoginView) {
                if (mCallback != null) {
                    mCallback.onAvatarClick();
                }
            }
        }
    };

    private UserMgr.UserInfoCallback mUserInfoCallback = new UserMgr.UserInfoCallback() {
        @Override
        public void onRefresh(UserInfo userInfo) {
            if (userInfo != null) {
                mCreditCell.setUserCredit(String.valueOf(userInfo.mFreeCredit));
            }
        }
    };



}
