package cn.poco.home.home4.userInfoMenu.Cells;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.poco.advanced.ImageUtils;
import cn.poco.campaignCenter.utils.ImageLoaderUtil;
import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.login.UserMgr;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

import static cn.poco.tianutils.ShareData.PxToDpi_xhdpi;

/**
 * Created by admin on 2016/10/13.
 */

public class UserInfoCell extends RelativeLayout implements EventCenter.OnEventListener{
    private FrameLayout userAvatarContainer;
    private ImageView userAvatar;
//    private EmptyUserAvatar2 mDefaultAvatar;
    private ImageView mDefaultAvatar;
    private TextView mUserName;
    private EditUserInfoBtn mEditBtn;
    private MenuCellVetical mMyCredit, mMyWallet;

    private Context mContext;
    private boolean mIsUserLoggedIn;

    private String mUserAvatarUrl;
    private String mUserNameText;


    public UserInfoCell(Context context, boolean isLogged, String avatarUrl, String name) {
        super(context);
        mContext = context;
        mIsUserLoggedIn = isLogged;
        mUserAvatarUrl = avatarUrl;
        mUserNameText = name;
        initView();
        EventCenter.addListener(this);
    }

   private void initView() {
       userAvatarContainer = new FrameLayout(mContext);
       userAvatarContainer.setId(R.id.usermenu_avatar_container);
       RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       params1.topMargin = ShareData.PxToDpi_xhdpi(60);
       params1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
       userAvatarContainer.setLayoutParams(params1);
       this.addView(userAvatarContainer);
       addUserAvatar();
       addDefaultAvatar();
       showAvatar();

       //编辑按钮
       mEditBtn = new EditUserInfoBtn(mContext);
       FrameLayout.LayoutParams params3 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM | Gravity.RIGHT);
       params3.bottomMargin = PxToDpi_xhdpi(3);
       mEditBtn.setLayoutParams(params3);
       userAvatarContainer.addView(mEditBtn);
       if (mIsUserLoggedIn) {
           mEditBtn.setVisibility(View.VISIBLE);
       } else {
           mEditBtn.setVisibility(View.GONE);
       }

       // 用户名字
       mUserName = new TextView(mContext);
       mUserName.setGravity(Gravity.TOP);
       mUserName.setId(R.id.usermenu_avatar_name);
       mUserName.setIncludeFontPadding(false);
       mUserName.setLines(1);
       mUserName.setMaxLines(1);
       mUserName.setSingleLine(true);
       mUserName.setTextColor(0xff999999);
       mUserName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
       mUserName.setClickable(true);
       mUserName.setEllipsize(TextUtils.TruncateAt.END);
       mUserName.setVisibility(View.VISIBLE);
       RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
       params4.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
       params4.addRule(RelativeLayout.BELOW, R.id.usermenu_avatar_container);
       params4.topMargin = PxToDpi_xhdpi(14);
       mUserName.setLayoutParams(params4);
       this.addView(mUserName);

       if (mIsUserLoggedIn) {
           mUserName.setTag("login");
           mUserName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
           mUserName.setTextColor(Color.BLACK);
           mUserName.setText(mUserNameText);
       } else {
           mUserName.setText(mContext.getString(R.string.clickToLogin));
           mUserName.setTag("empty");
       }

       mMyCredit = new MenuCellVetical(mContext);
       mMyCredit.setTextAndIcon(R.string.myCredit, 0xff4c4c4c, 12, R.drawable.homes_menu_mycredit);
       params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       params4.addRule(RelativeLayout.BELOW, R.id.usermenu_avatar_name);
       params4.leftMargin = ShareData.PxToDpi_xhdpi(48);
       params4.topMargin = ShareData.PxToDpi_xhdpi(45);
       mMyCredit.setLayoutParams(params4);
       this.addView(mMyCredit);

       mMyWallet = new MenuCellVetical(mContext);
       mMyWallet.setTextAndIcon(R.string.wallet, 0xff4c4c4c, 12, R.drawable.homes_menu_wallet);
       params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
       params4.addRule(RelativeLayout.BELOW, R.id.usermenu_avatar_name);
       params4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
       params4.rightMargin = ShareData.PxToDpi_xhdpi(48);
       params4.topMargin = ShareData.PxToDpi_xhdpi(45);
       mMyWallet.setLayoutParams(params4);
       this.addView(mMyWallet);
    }

    private void addUserAvatar() {
        userAvatar = new ImageView(mContext);
        userAvatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        userAvatar.setLayoutParams(params2);
        userAvatarContainer.addView(userAvatar);
    }

    private void addDefaultAvatar() {
        mDefaultAvatar = new ImageView(mContext);
        Bitmap defaultBitmap = ImageUtils.MakeHeadBmp(BitmapFactory.decodeResource(getResources(), R.drawable.homepage_menu_empty_avatar), ShareData.PxToDpi_xhdpi(160), 0);
        mDefaultAvatar.setImageBitmap(defaultBitmap);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        mDefaultAvatar.setLayoutParams(params2);
        userAvatarContainer.addView(mDefaultAvatar);
        mDefaultAvatar.setVisibility(View.GONE);
    }

    public ImageView getUserAvatar() {
        return userAvatar;
    }

    public ImageView getDefaultAvatar() {
        return mDefaultAvatar;
    }

    public TextView getUserName() {
        return mUserName;
    }

    public EditUserInfoBtn getEditBtn() {
        return mEditBtn;
    }

    public MenuCellVetical getCreditBtn() {
        return mMyCredit;
    }

    public MenuCellVetical getMyWalletBtn() {
        return mMyWallet;
    }

    public void setUpUserState(String name) {
        setUpUserName(name, 16, Color.BLACK);
    }

    public void setUpUserName(String state, float textSize, int color) {
        mUserName.setText(state);
        mUserName.setTextSize(textSize);
        mUserName.setTextColor(color);
    }

    public boolean isUserLoggedIn() {
        return mIsUserLoggedIn;
    }

    public void clear() {
        EventCenter.removeListener(this);
    }

    @Override
    public void onEvent(int eventId, Object[] params) {
        if (eventId == EventID.HOMEPAGE_UPDATE_MENU_AVATAR) {
            updateAvatar();
        }
    }

    private void updateAvatar() {
        mIsUserLoggedIn = UserMgr.IsLogin(getContext(),null);
        mUserAvatarUrl = SettingInfoMgr.GetSettingInfo(getContext()).GetPoco2HeadUrl();
        mUserNameText = SettingInfoMgr.GetSettingInfo(getContext()).GetPocoNick();
        showAvatar();
        if (mIsUserLoggedIn) {
            mEditBtn.setVisibility(View.VISIBLE);
            mUserName.setText(mUserNameText);
            mUserName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
            mUserName.setTextColor(Color.BLACK);
            mUserName.setTag("login");
        } else {
            mEditBtn.setVisibility(View.GONE);
            mUserName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
            mUserName.setTextColor(0xff999999);
            mUserName.setText(getResources().getString(R.string.clickToLogin));
            mUserName.setTag("empty");
        }
    }

    private void showAvatar() {
        if (mIsUserLoggedIn) {
            ImageLoaderUtil.getBitmapByUrl(mContext, mUserAvatarUrl, new ImageLoaderUtil.ImageLoaderCallback() {
                @Override
                public void loadImageSuccessfully(Object object) {
                    if (mUserAvatarUrl != null && object instanceof Bitmap) {
                        Bitmap temp = (Bitmap) object;
                        // 用户已经登录，成功获取头像照片
                        Bitmap avatarBitmap = ImageUtils.MakeHeadBmp(temp, PxToDpi_xhdpi(160), 0);
                        userAvatar.setImageBitmap(avatarBitmap);
                        userAvatar.setVisibility(VISIBLE);
                        mDefaultAvatar.setVisibility(View.GONE);
                    }
                }
                @Override
                public void failToLoadImage() {
                    // 用户已经登录，获取头像照片失败
                    userAvatar.setVisibility(GONE);
                    mDefaultAvatar.setVisibility(View.VISIBLE);
                }
            });

        } else {
            userAvatar.setVisibility(GONE);
            mDefaultAvatar.setVisibility(View.VISIBLE);
        }
    }

}
