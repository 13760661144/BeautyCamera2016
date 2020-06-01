package cn.poco.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.poco.advanced.ImageUtils;
import cn.poco.advanced.RecommendItemConfig;
import cn.poco.advanced.RecommendItemConfig2;
import cn.poco.advanced.RecommendItemList;
import cn.poco.advanced.RecommendItemList2;
import cn.poco.beautify.FixNullItemList;
import cn.poco.beautify.MyFastHSV;
import cn.poco.tianutils.FullScreenDlg;
import cn.poco.tianutils.ShareData;
import cn.poco.tsv.FastDynamicListV2;
import cn.poco.tsv.FastDynamicListV3;
import cn.poco.tsv.FastHSV;
import cn.poco.tsv.FastItemList;
import cn.poco.tsv100.FastHSV100;
import cn.poco.tsv100.FastHSVCore100;
import my.beautyCamera.R;

public class CommonUI
{
	public static final int DIALOG_BK_COLOR = 0x80000000;

	public static FastHSV MakeFastItemList1(Activity ac, ArrayList<FastItemList.ItemInfo> infos, boolean hasTitle, FastItemList.ControlCallback cb)
	{
		ShareData.InitData(ac);

		FastHSV out = new FastHSV(ac);

		FastItemList svc = new FastItemList(ac);
		svc.def_item_width = ShareData.PxToDpi_xhdpi(128);
		svc.def_item_height = svc.def_item_width;
		svc.def_img_color = 0x80FFFFFF;
		svc.def_bk_over_color = 0xFF19B593;
		svc.def_item_left = ShareData.PxToDpi_xhdpi(8);
		svc.def_item_right = svc.def_item_left;
		svc.def_bk_x = 0;
		svc.def_bk_y = 0;
		svc.def_bk_w = svc.def_item_width;
		svc.def_bk_h = svc.def_bk_w;
		svc.def_img_x = ShareData.PxToDpi_xhdpi(4);
		svc.def_img_y = ShareData.PxToDpi_xhdpi(4);
		svc.def_img_w = ShareData.PxToDpi_xhdpi(120);
		svc.def_img_h = svc.def_img_w;
		svc.def_img_round_size = ShareData.PxToDpi_xhdpi(12);
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.def_show_title = hasTitle;
		if(hasTitle)
		{
			svc.def_item_height = ShareData.PxToDpi_xhdpi(155);

			svc.def_title_size = ShareData.PxToDpi_xhdpi(20);
			svc.def_title_color_out = 0xFFFFFFFF;
			svc.def_title_color_over = 0xFF19B593;
			svc.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(4);
		}
		//参数设置完成才能INIT
		svc.InitData(cb);
		svc.SetData(infos);

		out.SetShowCore(svc);

		return out;
	}

	public static FastHSV MakeFixNullItemList1(Activity ac, ArrayList<FastItemList.ItemInfo> infos, boolean hasTitle, FastItemList.ControlCallback cb)
	{
		ShareData.InitData(ac);

		FastHSV out = new FastHSV(ac);

		FixNullItemList svc = new FixNullItemList(ac);
		svc.def_item_width = ShareData.PxToDpi_xhdpi(128);
		svc.def_item_height = svc.def_item_width;
		svc.def_img_color = 0x80FFFFFF;
		svc.def_bk_over_color = 0xFF19B593;
		svc.def_item_left = ShareData.PxToDpi_xhdpi(8);
		svc.def_item_right = svc.def_item_left;
		svc.def_bk_x = 0;
		svc.def_bk_y = 0;
		svc.def_bk_w = svc.def_item_width;
		svc.def_bk_h = svc.def_bk_w;
		svc.def_img_x = ShareData.PxToDpi_xhdpi(4);
		svc.def_img_y = ShareData.PxToDpi_xhdpi(4);
		svc.def_img_w = ShareData.PxToDpi_xhdpi(120);
		svc.def_img_h = svc.def_img_w;
		svc.def_img_round_size = ShareData.PxToDpi_xhdpi(12);
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.def_show_title = hasTitle;
		if(hasTitle)
		{
			svc.def_item_height = ShareData.PxToDpi_xhdpi(155);

			svc.def_title_size = ShareData.PxToDpi_xhdpi(20);
			svc.def_title_color_out = 0xFFFFFFFF;
			svc.def_title_color_over = 0xFF19B593;
			svc.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(4);
		}
		//参数设置完成才能INIT
		svc.InitData(cb);
		svc.SetData(infos);

		out.SetShowCore(svc);

		return out;
	}

	/**
	 * 用在拍照滤镜界面
	 *
	 * @param ac
	 * @param infos
	 * @param hasTitle
	 * @param cb
	 * @return
	 */
	public static MyFastHSV MakeMyFastDynamicList2(Activity ac, ArrayList<FastDynamicListV2.ItemInfo> infos, boolean hasTitle, FastItemList.ControlCallback cb)
	{
		ShareData.InitData(ac);

		MyFastHSV out = new MyFastHSV(ac);
		out.def_blur_btn_res_out = R.drawable.filterbeautify_blur_btn_out;
		out.def_blur_btn_res_over = R.drawable.filterbeautify_blur_btn_over;
		out.def_dark_btn_res_out = R.drawable.filterbeautify_vignetting_btn_out;
		out.def_dark_btn_res_over = R.drawable.filterbeautify_vignetting_btn_over;
		out.def_btn_x = ShareData.PxToDpi_xhdpi(37);
		out.def_view_x = ShareData.PxToDpi_xhdpi(144);
		out.def_2btn_gap_size = ShareData.PxToDpi_xhdpi(50);

		FastDynamicListV3 svc = new FastDynamicListV3(ac);
		int numOffsetW = 0;
		int numOffsetH = 0;
		svc.def_item_width = ShareData.PxToDpi_xhdpi(186) + numOffsetW;
		svc.def_item_height = ShareData.PxToDpi_xhdpi(246) + numOffsetH;
		svc.def_img_color = 0x80FFFFFF;
		svc.def_bk_over_color = 0x6618cea7;
		svc.def_bk_out_color = 0x66ffffff;
		svc.def_item_left = 0;
		svc.def_item_right = ShareData.PxToDpi_xhdpi(18);
		svc.def_bk_x = 0;
		svc.def_bk_y = numOffsetH;
		svc.def_bk_w = svc.def_item_width - numOffsetW;
		svc.def_bk_h = svc.def_item_height - numOffsetH;
		svc.def_img_x = 0;
		svc.def_img_y = 0;
		svc.def_img_w = ShareData.PxToDpi_xhdpi(186);
		svc.def_img_h = svc.def_img_w;
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.def_show_title = hasTitle;
		if(hasTitle)
		{
			svc.def_title_size = ShareData.PxToDpi_xhdpi(20);
			svc.def_title_color_out = 0xFF737373;
			svc.def_title_color_over = 0xffffffff;
			svc.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(20);
		}
		svc.def_state_x = svc.def_img_x;
		svc.def_state_y = svc.def_img_y;
		svc.def_state_w = svc.def_img_w;
		svc.def_state_h = svc.def_img_h;
		svc.def_wait_res = R.drawable.photofactory_item_loading;
		svc.def_loading_res = R.drawable.photofactory_item_loading;
		svc.def_ready_res = R.drawable.photofactory_res_new_logo;
		svc.def_new_res = R.drawable.photofactory_res_new_logo;
		svc.def_download_item_res = R.drawable.photofactory_download_logo;
		//svc.def_num_bk_res = R.drawable.photofactory_download_num_bk;
		svc.def_num_x = svc.def_item_width - ShareData.PxToDpi_xhdpi(41);
		svc.def_num_y = 0;
		svc.def_num_text_size = ShareData.PxToDpi_hdpi(12);
		svc.def_lock_x = svc.def_item_width - ShareData.PxToDpi_xhdpi(32);
		svc.def_lock_y = 0;
		svc.def_lock_res = R.drawable.photofactory_item_lock;
		//参数设置完成才能INIT
		svc.InitData(cb);
		svc.SetData(infos);

		out.SetShowCore(svc);

		return out;
	}

	/**
	 * 毛玻璃装饰用
	 *
	 * @param ac
	 * @param infos
	 * @param hasTitle
	 * @param cb
	 * @return
	 */
	public static MyFastHSV MakeMyFastDynamicListOfFilterPendant(Activity ac, ArrayList<FastDynamicListV2.ItemInfo> infos, boolean hasTitle, FastItemList.ControlCallback cb)
	{
		ShareData.InitData(ac);

		MyFastHSV out = new MyFastHSV(ac);
		out.def_view_x = ShareData.PxToDpi_xhdpi(18);

		FastDynamicListV3 svc = new FastDynamicListV3(ac);
		int numOffsetW = 0;
		int numOffsetH = 0;
		svc.def_item_width = ShareData.PxToDpi_xhdpi(160) + numOffsetW;
		svc.def_item_height = ShareData.PxToDpi_xhdpi(206) + numOffsetH;
		svc.def_img_color = 0x80FFFFFF;
		svc.def_bk_over_color = ImageUtils.GetSkinColor(0x66e75988);
		svc.def_bk_out_color = 0x66ffffff;
		svc.def_item_left = 0;
		svc.def_item_right = ShareData.PxToDpi_xhdpi(12);
		svc.def_bk_x = 0;
		svc.def_bk_y = ShareData.PxToDpi_xhdpi(160);
		svc.def_bk_w = svc.def_item_width - numOffsetW;
		svc.def_bk_h = svc.def_item_height - numOffsetH;
		svc.def_img_x = 0;
		svc.def_img_y = 0;
		svc.def_img_w = ShareData.PxToDpi_xhdpi(160);
		svc.def_img_h = svc.def_img_w;
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.def_show_title = hasTitle;
		if(hasTitle)
		{
			svc.def_title_size = ShareData.PxToDpi_xhdpi(22);
			svc.def_title_color_out = 0xFF737373;
			svc.def_title_color_over = 0xffffffff;
			svc.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(14);
		}
		svc.def_state_x = svc.def_img_x;
		svc.def_state_y = svc.def_img_y;
		svc.def_state_w = svc.def_img_w;
		svc.def_state_h = svc.def_img_h;
		svc.def_wait_res = R.drawable.mosaicpage_list_loading_logo;// TODO: 2017/1/7 style内置素材
		svc.def_loading_res = R.drawable.mosaicpage_list_loading_logo;
		svc.def_loading_anim = true;
		svc.def_loading_mask_color = 0xA0FFFFFF;
		svc.def_ready_res = R.drawable.mosaicpage_list_download_logo;// TODO: 2017/1/7 style内置素材
		svc.def_new_res = R.drawable.mosaicpage_list_new_logo;
		svc.def_download_item_res = R.drawable.photofactory_download_logo;
		//svc.def_num_bk_res = R.drawable.photofactory_download_num_bk;
		svc.def_num_x = svc.def_item_width - ShareData.PxToDpi_xhdpi(41);
		svc.def_num_y = 0;
		svc.def_num_text_size = ShareData.PxToDpi_hdpi(20);
		svc.def_lock_x = 0;
		svc.def_lock_y = 0;
		svc.def_lock_res = R.drawable.mosaicpage_list_lock_logo;
		//参数设置完成才能INIT
		svc.InitData(cb);
		svc.SetData(infos);

		out.SetShowCore(svc);

		return out;
	}

	public static FastHSV100 MakeList(Context context, ArrayList<RecommendItemList.ItemInfo> resInfoArr, RecommendItemConfig config, FastHSVCore100.ControlCallback cb)
	{
		FastHSV100 out = new FastHSV100((Activity)context);

		RecommendItemList svc = new RecommendItemList((Activity)context);

		//参数设置完成才能INIT
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.InitData(config);
		svc.SetData(resInfoArr, cb);

		out.SetShowCore(svc);

		return out;
	}

	/**
	 * 毛玻璃/马赛克
	 *
	 * @param context
	 * @param resInfoArr
	 * @param config
	 * @param cb
	 * @return
	 * @deprecated
	 */
	public static FastHSV100 MakeList2(Context context, ArrayList<RecommendItemList.ItemInfo> resInfoArr, RecommendItemConfig2 config, RecommendItemList2.ControlCallback cb)
	{
		FastHSV100 out = new FastHSV100((Activity)context);

		RecommendItemList2 svc = new RecommendItemList2((Activity)context);

		//参数设置完成才能INIT
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.InitData(config);
		svc.SetData(resInfoArr, cb);

		out.SetShowCore(svc);

		return out;
	}

	public static FastHSV MakeFastDynamicList1(Activity ac, ArrayList<FastDynamicListV2.ItemInfo> infos, boolean hasTitle, FastItemList.ControlCallback cb)
	{
		ShareData.InitData(ac);

		FastHSV out = new FastHSV(ac);

		FastDynamicListV3 svc = new FastDynamicListV3(ac);
		int numOffsetW = 0;
		int numOffsetH = 0;
		svc.def_item_width = ShareData.PxToDpi_xhdpi(128) + numOffsetW;
		svc.def_item_height = ShareData.PxToDpi_xhdpi(128) + numOffsetH;
		svc.def_img_color = 0x80FFFFFF;
		svc.def_bk_over_color = ImageUtils.GetSkinColor(0xffe75988);
		svc.def_item_left = ShareData.PxToDpi_xhdpi(8);
		svc.def_item_right = svc.def_item_left;
		svc.def_bk_x = 0;
		svc.def_bk_y = numOffsetH;
		svc.def_bk_w = svc.def_item_width - numOffsetW;
		svc.def_bk_h = svc.def_item_height - numOffsetH;
		svc.def_img_x = ShareData.PxToDpi_xhdpi(4);
		svc.def_img_y = ShareData.PxToDpi_xhdpi(4) + numOffsetH;
		svc.def_img_w = ShareData.PxToDpi_xhdpi(120);
		svc.def_img_h = svc.def_img_w;
		svc.def_img_round_size = ShareData.PxToDpi_xhdpi(12);
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.def_show_title = hasTitle;
		if(hasTitle)
		{
			svc.def_item_height = ShareData.PxToDpi_xhdpi(155) + numOffsetH;

			svc.def_title_size = ShareData.PxToDpi_xhdpi(20);
			svc.def_title_color_out = 0xFFFFFFFF;
			svc.def_title_color_over = 0xFF19B593;
			svc.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(4);
		}
		svc.def_state_x = svc.def_img_x;
		svc.def_state_y = svc.def_img_y;
		svc.def_state_w = svc.def_img_w;
		svc.def_state_h = svc.def_img_h;
		svc.def_wait_res = R.drawable.photofactory_item_loading;
		svc.def_loading_res = R.drawable.photofactory_item_loading;
		svc.def_ready_res = R.drawable.photofactory_res_new_logo;
		svc.def_new_res = R.drawable.photofactory_res_new_logo;
		svc.def_download_item_res = R.drawable.photofactory_download_logo;
		svc.def_num_bk_res = R.drawable.photofactory_download_num_bk;
		svc.def_num_x = svc.def_item_width - ShareData.PxToDpi_xhdpi(41);
		svc.def_num_y = 0;
		svc.def_num_text_size = ShareData.PxToDpi_hdpi(14);
		svc.def_lock_x = svc.def_item_width - ShareData.PxToDpi_xhdpi(32);
		svc.def_lock_y = 0;
		svc.def_lock_res = R.drawable.photofactory_item_lock;
		//参数设置完成才能INIT
		svc.InitData(cb);
		svc.SetData(infos);

		out.SetShowCore(svc);

		return out;
	}

	/**
	 * 商业用
	 *
	 * @param ac
	 * @param infos
	 * @param hasTitle
	 * @param cb
	 * @return
	 */
	public static FastHSV MakeFastDynamicList3(Activity ac, ArrayList<FastDynamicListV2.ItemInfo> infos, boolean hasTitle, FastItemList.ControlCallback cb)
	{
		ShareData.InitData(ac);

		FastHSV out = new FastHSV(ac);

		FastDynamicListV3 svc = new FastDynamicListV3(ac);
		int numOffsetW = 0;
		int numOffsetH = 0;
		svc.def_item_width = ShareData.PxToDpi_xhdpi(120) + numOffsetW;
		svc.def_item_height = ShareData.PxToDpi_xhdpi(155) + numOffsetH;
		svc.def_img_color = 0x80FFFFFF;
		svc.def_bk_over_color = 0xff80d6c1;
		svc.def_bk_out_color = 0xfff3eeec;
		svc.def_item_left = ShareData.PxToDpi_xhdpi(6);
		svc.def_item_right = svc.def_item_left;
		svc.def_bk_x = 0;
		svc.def_bk_y = ShareData.PxToDpi_xhdpi(120);
		svc.def_bk_w = svc.def_item_width - numOffsetW;
		svc.def_bk_h = svc.def_item_height - numOffsetH;
		svc.def_img_x = 0;
		svc.def_img_y = 0;
		svc.def_img_w = ShareData.PxToDpi_xhdpi(120);
		svc.def_img_h = svc.def_img_w;
		svc.def_move_size = ShareData.PxToDpi_hdpi(30);
		svc.def_show_title = hasTitle;
		if(hasTitle)
		{
			svc.def_title_size = ShareData.PxToDpi_xhdpi(20);
			svc.def_title_color_out = 0xFF737373;
			svc.def_title_color_over = 0xffffffff;
			svc.def_title_bottom_margin = ShareData.PxToDpi_xhdpi(10);
		}
		svc.def_state_x = svc.def_img_x;
		svc.def_state_y = svc.def_img_y;
		svc.def_state_w = svc.def_img_w;
		svc.def_state_h = svc.def_img_h;
		//参数设置完成才能INIT
		svc.InitData(cb);
		svc.SetData(infos);

		out.SetShowCore(svc);

		return out;
	}

	public static FullScreenDlg MakeNoFaceHelpDlg(Activity ac, final View.OnClickListener listener)
	{
		FrameLayout.LayoutParams fl;

		FullScreenDlg dlg = new FullScreenDlg(ac, R.style.dialog);
		dlg.setCancelable(false);
		LinearLayout content = new LinearLayout(ac);
		content.setOrientation(LinearLayout.VERTICAL);
//		content.setBackgroundResource(R.drawable.photofactory_noface_help_bk);
		content.setBackgroundDrawable(DrawableUtils.shapeDrawable(Color.WHITE,ShareData.PxToDpi_xhdpi(22)));
		{
			TextView tex = new TextView(ac);
//			tex.setSingleLine();
			tex.setText(R.string.beautify4page_noface_help);
			tex.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
			tex.setTextColor(0xff333333);
			tex.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(520), LinearLayout.LayoutParams.WRAP_CONTENT);
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			tex.setLayoutParams(ll);
			content.addView(tex);

			MyTextButton btn = new MyTextButton(ac);
			btn.setBk(R.drawable.photofactory_noface_help_btn);
			btn.setName(R.string.beautify4page_noface_help_btn, 14, 0xffffffff, true);
			ll = new LinearLayout.LayoutParams(ShareData.PxToDpi_xhdpi(420), ShareData.PxToDpi_xhdpi(76));
			ll.gravity = Gravity.CENTER_HORIZONTAL;
			ll.topMargin = ShareData.PxToDpi_xhdpi(58);
			content.addView(btn, ll);
			btn.setOnTouchListener(new OnAnimationClickListener()
			{
				@Override
				public void onAnimationClick(View v)
				{
					listener.onClick(v);
				}

				@Override
				public void onTouch(View v)
				{

				}

				@Override
				public void onRelease(View v)
				{

				}
			});
		}
		fl = new FrameLayout.LayoutParams(ShareData.PxToDpi_xhdpi(568), FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.gravity = Gravity.CENTER;
		content.setPadding(0,ShareData.PxToDpi_xhdpi(60),0,ShareData.PxToDpi_xhdpi(60));
		dlg.AddView(content, fl);

		return dlg;
	}

	/**
	 * 修改editText控件的光标颜色
	 * @param editText 要修改光标颜色的editText
	 * @param color 光标的新颜色
	 */
	public static void modifyEditTextCursor(EditText editText, int color) {
		try {
			Field fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
			fCursorDrawableRes.setAccessible(true);
			int mCursorDrawableRes = fCursorDrawableRes.getInt(editText);
			Field fEditor = TextView.class.getDeclaredField("mEditor");
			fEditor.setAccessible(true);
			Object editor = fEditor.get(editText);
			Class<?> clazz = editor.getClass();
			Field fCursorDrawable = clazz.getDeclaredField("mCursorDrawable");
			fCursorDrawable.setAccessible(true);
			Drawable[] drawables = new Drawable[2];
			drawables[0] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
			drawables[1] = ContextCompat.getDrawable(editText.getContext(), mCursorDrawableRes);
			drawables[0].setColorFilter(color, PorterDuff.Mode.SRC_IN);
			drawables[1].setColorFilter(color, PorterDuff.Mode.SRC_IN);
			fCursorDrawable.set(editor, drawables);
		} catch (Throwable ignored) {

		}
	}





}
