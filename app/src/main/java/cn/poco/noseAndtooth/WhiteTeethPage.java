package cn.poco.noseAndtooth;

import android.content.Context;
import android.graphics.Bitmap;

import cn.poco.face.FaceDataV2;
import cn.poco.framework.BaseSite;
import cn.poco.image.PocoCameraEffect;
import cn.poco.makeup.MakeupUIHelper;
import cn.poco.noseAndtooth.abs.AbsNATPresenter;
import cn.poco.noseAndtooth.abs.AbsNATModel;
import cn.poco.noseAndtooth.abs.AbsNoseAndToothPage;
import cn.poco.noseAndtooth.abs.INATPresenter;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import cn.poco.statistics.TongJiUtils;
import cn.poco.view.beauty.BeautyCommonViewEx;
import cn.poco.view.beauty.MakeUpViewEx1;
import my.beautyCamera.R;

//美牙
public class WhiteTeethPage extends AbsNoseAndToothPage {
    public WhiteTeethPage(Context context, BaseSite site) {
        super(context, site);
        TongJiUtils.onPageStart(getContext(), R.string.美牙);
        MyBeautyStat.onPageStartByRes(R.string.美颜美图_美牙页_主页面);
    }

    @Override
    public void onCheckBtnOnclick(MakeupUIHelper.ChangePointFr changePointFr) {
        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_手动定点);
        MyBeautyStat.onClickByRes(R.string.美颜美图_美牙页_主页面_手动定点);
        if(changePointFr != null)
        {
            changePointFr.setUIFlag(MakeupUIHelper.ChangePointFr.CHECK_INDEX_LIP);
        }
        m_view.Data2UI();
        m_view.setMode(BeautyCommonViewEx.MODE_MAKEUP);
        m_view.m_showPosFlag = MakeUpViewEx1.POS_LIP;
        m_view.m_touchPosFlag = m_view.m_showPosFlag;
        m_view.DoFixedPointAnim();
    }

    @Override
    public INATPresenter getPresenter(final Context context,BaseSite site) {
        return new AbsNATPresenter(context,this,site) {
            @Override
            public void onTitleBtn(boolean isHide) {
                if(isHide)
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_bar收回);
                    MyBeautyStat.onClickByRes(R.string.美颜美图_美牙页_主页面_收回bar);
                }
                else
                {
                    TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_bar展开);
                    MyBeautyStat.onClickByRes(R.string.美颜美图_美牙页_主页面_展开bar);
                }
            }

            @Override
            public void onCompareBtn() {
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_对比按钮);
                MyBeautyStat.onClickByRes(R.string.美颜美图_美牙页_主页面_对比按钮);
            }

            @Override
            public void back() {
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_取消);
                MyBeautyStat.onClickByRes(R.string.美颜美图_美牙页_主页面_取消);
                super.back();
            }

            @Override
            public void ok() {
                TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_确认);
                MyBeautyStat.onClickByRes(R.string.美颜美图_美牙页_主页面_确认);
                MyBeautyStat.onUseTooth(getCurProgress());
                super.ok();
            }

            @Override
            public AbsNATModel getModel() {
                return new AbsNATModel(context) {
                    @Override
                    public Bitmap makeEffect(Bitmap bmp) {
                        Bitmap out = bmp;
                        if(FaceDataV2.RAW_POS_MULTI != null && FaceDataV2.RAW_POS_MULTI.length > 0)
                        {
                            for (int i = 0; i < FaceDataV2.RAW_POS_MULTI.length; i++)
                            {
                                int progress = getFaceLocalData().m_WhiteTeetch_multi[i];
                                out = PocoCameraEffect.TeethWhiten(bmp,getContext(), FaceDataV2.RAW_POS_MULTI[i],progress);
                            }
                        }
                        return out;
                    }

                    @Override
                    public String getTitle() {
                        return getContext().getResources().getString(R.string.whiteteeth_title);
                    }

                    @Override
                    public int getIconRes() {
                        return R.drawable.beautify_meiya_icon;
                    }

                    @Override
                    public int getProgress() {
                        return getFaceLocalData().m_WhiteTeetch_multi[FaceDataV2.sFaceIndex];
                    }

                    @Override
                    public void changeProgress(int progress) {
                        getFaceLocalData().m_WhiteTeetch_multi[FaceDataV2.sFaceIndex] = progress;
                        TongJi2.AddCountByRes(getContext(), R.integer.修图_美颜美形_美牙_美牙滑动杆);
                    }

                    @Override
                    public int initProgressValue() {
                        return 30;
                    }
                };
            }
        };
    }

    @Override
    public void onClose()
    {
        super.onClose();
        TongJiUtils.onPageEnd(getContext(), R.string.美牙);
        MyBeautyStat.onPageEndByRes(R.string.美颜美图_美牙页_主页面);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.美牙);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.美牙);
    }
}
