package cn.poco.beautifyEyes.util;

import android.content.Context;

/**
 * Created by Shine on 2017/4/7.
 */

public interface BeautifyEyesStatistic {

    void onCancelClick(Context context);

    void onConfirmClick(Context context);

    void onPinPointClick(Context con);

    void onClickCompareBtn(Context context);



    void shrinkBigEyesBar(Context context);

    void unfoldedBigEyesBar(Context context);

    void onClickBigEyesSlidingBar(Context context);



    void shrinkDropEyesBagBar(Context context);

    void unfoldedDropEyesBagBar(Context context);

    void onClickDropEyesBagBar(Context context);


    void shrinkBrightEyesBar(Context context);

    void unfoldedBrightEyesBar(Context context);

    void onClickBrightEyesBar(Context context);

    // 新增神策统计
    void ShenCe_onConfirmClick();

    void ShenCe_onCancelClick();

    void ShenCe_onPinPointManually();

    void ShenCe_onCompareBtnClick();

    void ShenCe_onShrinkBigEyesBar();

    void ShenCe_unFoldBigEyesBar();

    void ShenCe_onShrinkDropEyesBagBar();

    void ShenCe_unFoldDropEyesBagBar();

    void ShenCe_onShrinkBrightEyesBar();

    void ShenCe_unFoldBrightEyesBar();

}
