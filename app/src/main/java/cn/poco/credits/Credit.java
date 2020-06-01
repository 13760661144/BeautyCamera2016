package cn.poco.credits;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import cn.poco.framework.EventCenter;
import cn.poco.framework.EventID;
import cn.poco.login.HttpResponseCallback;
import cn.poco.login.LoginUtils2;
import cn.poco.loginlibs.info.UserInfo;
import cn.poco.scorelibs.CreditUtils;
import cn.poco.scorelibs.info.CreditConsumeInfo;
import cn.poco.scorelibs.info.CreditIncomeInfo;
import cn.poco.setting.SettingInfo;
import cn.poco.setting.SettingInfoMgr;
import cn.poco.system.AppInterface;
import my.beautyCamera.R;

/**
 * 积分分类ID
 */
public class Credit {
    //private final Object LOCK = new Object();
    public static final String APP_ID = "beauty_camera";

    public static final String CARD = "01t";    //卡片
    public static final String DECORATE = "02t";    //装饰
    public static final String FRAME = "03t";    //边框
    public static final String GLASS = "04t";    //毛玻璃
    public static final String MAKEUP = "05t";    //彩妆
    public static final String MOSAIC = "06t";    //马赛克
    public static final String PUZZLE_BK = "07t";    //拼图背景
    public static final String PUZZLE_TEMPLATE = "08t";    //拼图模板
    public static final String AUTO_DECORATE = "09t";    //动态贴纸
    public static final String BEAUTY = "10t";    //美颜
    public static final String FILTER = "11t";    //滤镜
    public static final String BRUSH = "12t";    //指尖魔法
    public static final String LIGHT_EFFECT = "13t";    //小时光
    public static final String THEME = "14t";    //主题
    public static Toast m_toast;

    /**
     * 自行在线程调用
     *
     * @param context
     * @param actionIds
     */
    public static void syncCreditIncome(final Context context, final String actionIds) {
        if (context != null) {
            final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
            final String accessToken = settingInfo.GetPoco2Token(false);
            final String userId = settingInfo.GetPoco2Id(false);
            final Handler handler = new Handler(Looper.getMainLooper());
            if (userId != null && accessToken != null) {
                CreditIncomeInfo income = CreditUtils.CreditIncome(userId, accessToken, actionIds, null, AppInterface.GetInstance(context));
                if (income != null && income.mCode == 0 &&
                        income.mIncomeItems != null && income.mIncomeItems.size() > 0 &&
                        income.mIncomeItems.get(0) != null &&
                        income.mIncomeItems.get(0).values > 0 && income.mProtocolCode == 200) {
                    final String creditMsg = income.mIncomeItems.get(0).message;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (m_toast == null) {
                                m_toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                            }
                            m_toast.setText(creditMsg);
                            m_toast.show();
                            getUserInfo(settingInfo, userId, accessToken);
                        }
                    });
                }
            }
        }
    }


    /**
     * 自行在线程调用
     */
    public static void syncCreditIncome(final String params, final Context context, final int action_id) {
        if (context != null) {
            final String actionId = context.getString(action_id);
            final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
            final String accessToken = settingInfo.GetPoco2Token(false);
            final String userId = settingInfo.GetPoco2Id(false);
            final Handler handler = new Handler(Looper.getMainLooper());
            if (userId != null && accessToken != null) {
                CreditIncomeInfo income = CreditUtils.CreditIncome(userId, accessToken, actionId, params, AppInterface.GetInstance(context));
                if (income != null && income.mCode == 0 &&
                        income.mIncomeItems != null && income.mIncomeItems.size() > 0 &&
                        income.mIncomeItems.get(0) != null &&
                        income.mIncomeItems.get(0).values > 0 && income.mProtocolCode == 200) {
                    final String creditMsg = income.mIncomeItems.get(0).message;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (m_toast == null) {
                                m_toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                            }
                            m_toast.setText(creditMsg);
                            m_toast.show();
                            getUserInfo(settingInfo, userId, accessToken);
                        }
                    });
                }
            }
        }
    }


    /**
     * @param params    app_id+type_id+res_id
     * @param action_id 资源id(只能传一个ActionId)
     */
    public static void CreditIncome(final String params, final Context context, final int action_id) {
        if (context != null) {
            final String actionId = context.getString(action_id);
            final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
            final String accessToken = settingInfo.GetPoco2Token(false);
            final String userId = settingInfo.GetPoco2Id(false);
            if (userId != null && accessToken != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        CreditIncomeInfo income = CreditUtils.CreditIncome(userId, accessToken, actionId, params, AppInterface.GetInstance(context));
                        if (income != null && income.mCode == 0 &&
                                income.mIncomeItems != null && income.mIncomeItems.size() > 0 &&
                                income.mIncomeItems.get(0) != null &&
                                income.mIncomeItems.get(0).values > 0 && income.mProtocolCode == 200) {
                            final String creditMsg = income.mIncomeItems.get(0).message;
                            final Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (m_toast == null) {
                                        m_toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                                    }
                                    m_toast.setText(creditMsg);
                                    m_toast.show();
                                    getUserInfo(settingInfo, userId, accessToken);
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    public static void CreditIncome(final Context context, final String actionIds) {
        if (context != null) {
            final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
            final String accessToken = settingInfo.GetPoco2Token(false);
            final String userId = settingInfo.GetPoco2Id(false);
            final Handler handler = new Handler();
            if (userId != null && accessToken != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CreditIncomeInfo income = CreditUtils.CreditIncome(userId, accessToken, actionIds, null, AppInterface.GetInstance(context));
                        if (income != null && income.mCode == 0 &&
                                income.mIncomeItems != null && income.mIncomeItems.size() > 0 &&
                                income.mIncomeItems.get(0) != null &&
                                income.mIncomeItems.get(0).values > 0 && income.mProtocolCode == 200) {
                            final String creditMsg = income.mIncomeItems.get(0).message;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (m_toast == null) {
                                        m_toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                                    }
                                    m_toast.setText(creditMsg);
                                    m_toast.show();
                                    getUserInfo(settingInfo, userId, accessToken);
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }


    public static void CreditIncome_notThreadinMethod(final Context context, final String actionIds, String userId, String accessToken, Handler handler) {
        if (context != null) {
            if (userId != null && accessToken != null) {
                CreditIncomeInfo income = CreditUtils.CreditIncome(userId, accessToken, actionIds, null, AppInterface.GetInstance(context));
                if (income != null && income.mCode == 0 &&
                        income.mIncomeItems != null && income.mIncomeItems.size() > 0 &&
                        income.mIncomeItems.get(0) != null &&
                        income.mIncomeItems.get(0).values > 0 && income.mProtocolCode == 200) {
                    final String creditMsg = income.mIncomeItems.get(0).message;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (m_toast == null) {
                                m_toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
                            }
                            m_toast.setText(creditMsg);
                            m_toast.show();
                        }
                    });
                }
            }
        }
    }

    public static void BussinessCreditIncome(final String params, final Context context, int action_id) {
        if (context != null) {
            final String actionId = String.valueOf(action_id);
            final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
            final String accessToken = settingInfo.GetPoco2Token(false);
            final String userId = settingInfo.GetPoco2Id(false);
            if (userId != null && accessToken != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        CreditUtils.CreditIncome(userId, accessToken, actionId, params, AppInterface.GetInstance(context));
                    }
                }).start();
            }
        }
    }

    public static void CreditConsume(final String params, final Context context, final int action_id, final Callback cb) {
        if (context != null) {
            final String actionId = String.valueOf(action_id);
            final SettingInfo settingInfo = SettingInfoMgr.GetSettingInfo(context);
            final String accessToken = settingInfo.GetPoco2Token(false);
            final String userId = settingInfo.GetPoco2Id(false);
            if (userId != null && accessToken != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final CreditConsumeInfo consumeInfo = CreditUtils.CreditConsume(userId, accessToken, actionId, params, AppInterface.GetInstance(context));
                        final Handler handler = new Handler(Looper.getMainLooper());
                        if (consumeInfo != null && consumeInfo.mCode == 0 && cb != null && consumeInfo.mProtocolCode == 200) {
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    cb.OnSuccess(consumeInfo.mCreditMessage);
                                    getUserInfo(settingInfo, userId, accessToken);
                                }
                            });
                        } else if (cb != null) {
                            handler.post(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    if (cb != null && context != null)
                                    {
                                        cb.OnFailed(context.getString(R.string.unlock_credit_redeem_failed));
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }

    // 重新获取用户信息
    private static void getUserInfo(final SettingInfo settingInfo, String userId, String accessToken)
    {
        LoginUtils2.getUserInfo(userId, accessToken, new HttpResponseCallback()
        {
            @Override
            public void response(Object object)
            {
                if(object == null) return;
                UserInfo userInfo = (UserInfo)object;
                if (userInfo.mCode == 0) {
                    settingInfo.SetPoco2Credit(userInfo.mFreeCredit + "");
                    EventCenter.sendEvent(EventID.UPDATE_USER_INFO);
                }
            }
        });
    }

    public interface Callback {

        void OnSuccess(String msg);

        void OnFailed(String msg);
    }
}
