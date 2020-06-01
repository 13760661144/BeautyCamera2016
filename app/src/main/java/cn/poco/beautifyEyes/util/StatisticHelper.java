package cn.poco.beautifyEyes.util;

import android.content.Context;

import com.baidu.mobstat.StatService;

import cn.poco.beautifyEyes.page.BeautifyEyesBasePage;
import cn.poco.statistics.MyBeautyStat;
import cn.poco.statistics.TongJi2;
import my.beautyCamera.R;

/**
 * Created by Shine on 2017/4/7.
 */

public class StatisticHelper{
   public static final int STATISTIC_BIGEYES = 0x01;
   public static final int STATISTIC_DROPEYESBAG = 0x02;
   public static final int STATISTIC_BRIGHTEYES = 0x03;

   private static BeautifyEyesStatistic sCurrentMode;
   private static BeautifyEyesBasePage.BeautifyModule sActiveModule;


   private StatisticHelper() {
   }

   private static volatile StatisticHelper sInstance = null;

   public static StatisticHelper getInstance(BeautifyEyesBasePage.BeautifyModule originModule, BeautifyEyesBasePage.BeautifyModule activeModule) {
      StatisticHelper localInstance = sInstance;
      if (localInstance == null) {
         synchronized (StatisticHelper.class) {
            localInstance = sInstance;
            if (localInstance == null) {
               sInstance = localInstance = new StatisticHelper();
            }
         }
      }
      int type = getTypeByModule(originModule);
      sCurrentMode = BeautifyModeFactory.makeBeautifyMode(type);
      sActiveModule = activeModule;
      assertType(type);
      return localInstance;
   }

   // 页面统计部分
   public static void countPageEnter(Context context, String pageTag) {
      StatService.onPageStart(context, pageTag);
      TongJi2.StartPage(context, pageTag);
   }

   public static void ShenCe_countPageEnter(int pageId) {
       MyBeautyStat.onPageStartByRes(pageId);
   }

   public static void countPageLeave(Context context, String pageTag) {
      StatService.onPageEnd(context, pageTag);
      TongJi2.EndPage(context, pageTag);
   }

   public static void ShenCe_countPageLeave(int pageId) {
       MyBeautyStat.onPageEndByRes(pageId);
   }

   public static void countPagePause(Context context, String pageTag) {
      TongJi2.OnPause(context, pageTag);
   }

   public static void countPageResume(Context context, String pageTag) {
      TongJi2.OnResume(context, pageTag);
   }

   private static int getTypeByModule(BeautifyEyesBasePage.BeautifyModule module) {
      int type = 0;
      if (module == BeautifyEyesBasePage.BeautifyModule.BIGEYES) {
         type = STATISTIC_BIGEYES;
      } else if (module == BeautifyEyesBasePage.BeautifyModule.DROPEYESBAG) {
         type = STATISTIC_DROPEYESBAG;
      } else if (module == BeautifyEyesBasePage.BeautifyModule.BRIGHTEYES) {
         type =  STATISTIC_BRIGHTEYES;
      }
      return type;
   }

   // 按钮统计部分
   public void onClickCancel(Context context) {
      sCurrentMode.onCancelClick(context);
      sCurrentMode.ShenCe_onCancelClick();
   }

   public void onClickConfirm(Context context, int bigEyesLevel, int dropEyesBagLevel, int brightEyesLevel) {
      sCurrentMode.onConfirmClick(context);
      sCurrentMode.ShenCe_onConfirmClick();
       MyBeautyStat.onUseEye(bigEyesLevel, dropEyesBagLevel, brightEyesLevel);
   }

   public void onClickPinPoint(Context context) {
      sCurrentMode.onPinPointClick(context);
      sCurrentMode.ShenCe_onPinPointManually();
   }

   public void onClickCompareBtn(Context context) {
      sCurrentMode.onClickCompareBtn(context);
      sCurrentMode.ShenCe_onCompareBtnClick();
   }

   public void shrinkBar(Context context) {
      if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.BIGEYES) {
         sCurrentMode.shrinkBigEyesBar(context);
         sCurrentMode.ShenCe_onShrinkBigEyesBar();
      } else if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.BRIGHTEYES) {
         sCurrentMode.shrinkBrightEyesBar(context);
         sCurrentMode.ShenCe_onShrinkBrightEyesBar();
      } else if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.DROPEYESBAG) {
         sCurrentMode.shrinkDropEyesBagBar(context);
         sCurrentMode.ShenCe_onShrinkDropEyesBagBar();
      }
   }

   public void unfoldedBar(Context context) {
      if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.BIGEYES) {
         sCurrentMode.unfoldedBigEyesBar(context);
         sCurrentMode.ShenCe_unFoldBigEyesBar();
      } else if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.BRIGHTEYES) {
         sCurrentMode.unfoldedBrightEyesBar(context);
         sCurrentMode.ShenCe_unFoldBrightEyesBar();
      } else if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.DROPEYESBAG) {
         sCurrentMode.unfoldedDropEyesBagBar(context);
         sCurrentMode.ShenCe_unFoldDropEyesBagBar();
      }
   }

   public void onClickSlidingBar(Context context) {
      if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.BIGEYES) {
         sCurrentMode.onClickBigEyesSlidingBar(context);
      } else if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.BRIGHTEYES) {
         sCurrentMode.onClickBrightEyesBar(context);
      } else if (sActiveModule == BeautifyEyesBasePage.BeautifyModule.DROPEYESBAG) {
         sCurrentMode.onClickDropEyesBagBar(context);
      }
   }





   private static void assertType(int type) {
      if (!(type == STATISTIC_BIGEYES || type == STATISTIC_BRIGHTEYES || type == STATISTIC_DROPEYESBAG)) {
         throw new IllegalArgumentException("the type " + type + " is not legal");
      }
   }


   private static class BeautifyModeFactory {

      public static BeautifyEyesStatistic makeBeautifyMode(int mode) {
         BeautifyEyesStatistic beautifyInterface = null;
         if (mode == STATISTIC_BIGEYES) {
            beautifyInterface = new BigEyes();
         } else if (mode == STATISTIC_DROPEYESBAG) {
            beautifyInterface = new DropEyesBag();
         } else if (mode == STATISTIC_BRIGHTEYES) {
            beautifyInterface = new BrightEyes();
         }
         return beautifyInterface;
      }
   }



   private static class BigEyes implements BeautifyEyesStatistic {
      @Override
      public void onCancelClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_取消);
      }

      @Override
      public void onConfirmClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_确认);
      }

      @Override
      public void onPinPointClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_手动定点);
      }

      @Override
      public void onClickCompareBtn(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_对比);
      }

      @Override
      public void shrinkBigEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_大眼bar收回);
      }

      @Override
      public void unfoldedBigEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_大眼bar展开);
      }

      @Override
      public void onClickBigEyesSlidingBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_大眼滑动杆);
      }

      @Override
      public void shrinkDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_祛眼袋bar收回);
      }

      @Override
      public void unfoldedDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_祛眼袋bar展开);
      }

      @Override
      public void onClickDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_祛眼袋滑动杆);
      }

      @Override
      public void shrinkBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_亮眼bar收回);
      }

      @Override
      public void unfoldedBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_亮眼bar展开);
      }

      @Override
      public void onClickBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_大眼_亮眼滑动杆);
      }

      // 神策统计
      @Override
      public void ShenCe_onConfirmClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_确认);
      }

      @Override
      public void ShenCe_onCancelClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_取消);
      }

      @Override
      public void ShenCe_onPinPointManually() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_手动定点);
      }

      @Override
      public void ShenCe_onCompareBtnClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_对比按钮);
      }

      @Override
      public void ShenCe_onShrinkBigEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_大眼bar收回);
      }

      @Override
      public void ShenCe_unFoldBigEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_大眼bar展开);
      }

      @Override
      public void ShenCe_onShrinkDropEyesBagBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_祛眼袋bar收回);
      }

      @Override
      public void ShenCe_unFoldDropEyesBagBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_祛眼袋bar展开);
      }

      @Override
      public void ShenCe_onShrinkBrightEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_亮眼bar收回);
      }

      @Override
      public void ShenCe_unFoldBrightEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_大眼页面_主页面_亮眼bar展开);
      }
   }

   private static class DropEyesBag implements BeautifyEyesStatistic {
      @Override
      public void onCancelClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_取消);
      }

      @Override
      public void onConfirmClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_确认);
      }

      @Override
      public void onPinPointClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_手动定点);
      }

      @Override
      public void onClickCompareBtn(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_对比);
      }

      @Override
      public void shrinkBigEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_大眼bar收回);
      }

      @Override
      public void unfoldedBigEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_大眼bar展开);
      }

      @Override
      public void onClickBigEyesSlidingBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_大眼滑动杆);
      }

      @Override
      public void shrinkDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_祛眼袋bar收回);
      }

      @Override
      public void unfoldedDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_祛眼袋bar展开);
      }

      @Override
      public void onClickDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_祛眼袋滑动杆);
      }

      @Override
      public void shrinkBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_亮眼bar收回);
      }

      @Override
      public void unfoldedBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_亮眼bar展开);
      }

      @Override
      public void onClickBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_祛眼袋_亮眼滑动杆);
      }

      // 神策统计


      @Override
      public void ShenCe_onConfirmClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_确认);
      }

      @Override
      public void ShenCe_onCancelClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_取消);
      }

      @Override
      public void ShenCe_onPinPointManually() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_手动定点);
      }

      @Override
      public void ShenCe_onCompareBtnClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_对比按钮);
      }

      @Override
      public void ShenCe_onShrinkBigEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_大眼bar收回);
      }

      @Override
      public void ShenCe_unFoldBigEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_大眼bar展开);
      }

      @Override
      public void ShenCe_onShrinkDropEyesBagBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_祛眼袋bar收回);
      }

      @Override
      public void ShenCe_unFoldDropEyesBagBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_祛眼袋bar展开);
      }

      @Override
      public void ShenCe_onShrinkBrightEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_亮眼bar收回);
      }

      @Override
      public void ShenCe_unFoldBrightEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_祛眼袋页面_主页面_亮眼bar展开);
      }
   }

   private static class BrightEyes implements BeautifyEyesStatistic {
      @Override
      public void onCancelClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_取消);
      }

      @Override
      public void onConfirmClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_确认);
      }

      @Override
      public void onPinPointClick(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_手动定点);
      }

      @Override
      public void onClickCompareBtn(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_对比);
      }

      @Override
      public void shrinkBigEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_大眼bar收回);
      }

      @Override
      public void unfoldedBigEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_大眼bar展开);
      }

      @Override
      public void onClickBigEyesSlidingBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_大眼滑动杆);
      }

      @Override
      public void shrinkDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_祛眼袋bar收回);
      }

      @Override
      public void unfoldedDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_祛眼袋bar展开);
      }

      @Override
      public void onClickDropEyesBagBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_祛眼袋滑动杆);
      }

      @Override
      public void shrinkBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_亮眼bar收回);
      }

      @Override
      public void unfoldedBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_亮眼bar展开);
      }

      @Override
      public void onClickBrightEyesBar(Context context) {
         TongJi2.AddCountByRes(context, R.integer.修图_美颜美形_亮眼_亮眼滑动杆);
      }

      // 神策统计
      @Override
      public void ShenCe_onConfirmClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_确认);
      }

      @Override
      public void ShenCe_onCancelClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_取消);
      }

      @Override
      public void ShenCe_onPinPointManually() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_手动定点);
      }

      @Override
      public void ShenCe_onCompareBtnClick() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_对比按钮);
      }

      @Override
      public void ShenCe_onShrinkBigEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_大眼bar收回);
      }

      @Override
      public void ShenCe_unFoldBigEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_大眼bar展开);
      }

      @Override
      public void ShenCe_onShrinkDropEyesBagBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_祛眼袋bar收回);
      }

      @Override
      public void ShenCe_unFoldDropEyesBagBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_祛眼袋bar展开);
      }

      @Override
      public void ShenCe_onShrinkBrightEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_亮眼bar收回);
      }

      @Override
      public void ShenCe_unFoldBrightEyesBar() {
         MyBeautyStat.onClickByRes(R.string.美颜美图_亮眼页面_主页面_亮眼bar展开);
      }
   }

}
