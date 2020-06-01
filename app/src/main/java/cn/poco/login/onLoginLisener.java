package cn.poco.login;

import cn.poco.loginlibs.info.LoginInfo;

/**
 * 登陆之后的回调接口
 */
public interface onLoginLisener {
	
    public void onLoginSuccess(LoginInfo info, LoginStyle.LoginBaseInfo baseInfo, boolean isShouldBindPhone);
    
    public void onLoginFailed();
    
    public void onCancel();
    
    public void onActionLogin();

    public void showLoginErrorTips(String str);

}
