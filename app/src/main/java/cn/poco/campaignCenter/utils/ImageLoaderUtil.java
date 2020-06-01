package cn.poco.campaignCenter.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

public class ImageLoaderUtil {

	/**
	 * 显示图片
	 * @param url 图片url
	 * @param imageView ImageView实例
	 */
	public static void displayImage(Context context, String url, ImageView imageView) {
		displayImage(context, url, imageView, 0, 0, 0, null, true);
	}

	/**
	 * 显示图片
	 * @param url 图片url
	 * @param imageView ImageView实例
	 * @param needAnimation 是否需要过渡动画
	 */
	public static void displayImage(Context context, String url, ImageView imageView, boolean needAnimation, ImageLoaderCallback callback) {
		displayImageAndGetInfo(context, url, imageView, 0, null, needAnimation, callback);
	}


	/**
	 * 显示图片
	 * @param url 图片url
	 * @param imageView ImageView实例
	 * @param width 目标图片宽度
	 * @param height 目标图片高度
	 */
	public static void displayImage(Context context, String url, ImageView imageView, int width, int height) {
		displayImage(context, url, imageView, width, height, 0, null, true);
	}

	/**
	 * 显示图片
	 * @param url 图片url
	 * @param imageView ImageView实例
	 * @param placeholder 图片占位符
	 */
	public static void displayImage(Context context, String url, ImageView imageView, @DrawableRes int placeholder) {
		displayImage(context, url, imageView, 0, 0, placeholder, null, true);
	}

	public static void displayGif(Context context, int resId, ImageView imageView) {
        if (context == null) {
            throw new RuntimeException("context为空");
		}
		Glide.with(context).load(resId).asGif().into(imageView);
	}



	private static void displayImage(Context context, String url, ImageView imageView, int width, int height,
									 int placeholder, ImageView.ScaleType scaleType, boolean needCrossFadeAnimation) {
        if (context == null) {
            throw new RuntimeException("context为空");
		}

		BitmapTypeRequest<String> request = Glide.with(context).load(url).asBitmap();
        if (width > 0 && height > 0) {
            request.override(width , height);
		}


        if (placeholder != 0) {
            request.placeholder(placeholder);
		}

		if (scaleType == ImageView.ScaleType.FIT_CENTER) {
            request.fitCenter();
		} else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            request.centerCrop();
		}

		if (needCrossFadeAnimation) {
            request.crossFade();
		} else {
            request.centerCrop();
		}
		request.into(imageView);
	}

	public static void displayImageAndGetInfo(Context context, String url, ImageView imageView, int placeholder, ImageView.ScaleType scaleType, boolean needCrossFadeAnimation, final ImageLoaderCallback callback) {
		if (context == null) {
			throw new RuntimeException("context为空");
		}

		final DrawableTypeRequest<String> request = Glide.with(context).load(url);


		if (placeholder != 0) {
			request.placeholder(placeholder);
		} else {
//			request.placeholder(sPlaceholder);
		}

		if (scaleType == ImageView.ScaleType.FIT_CENTER) {
			request.fitCenter();
		} else if (scaleType == ImageView.ScaleType.CENTER_CROP) {
			request.centerCrop();
		}

		if (needCrossFadeAnimation) {
			request.crossFade();
		} else {
			request.dontAnimate();
		}
		request.listener(new RequestListener<String, GlideDrawable>() {
			@Override
			public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
				return false;
			}

			@Override
			public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
				if (callback != null) {
					callback.loadImageSuccessfully(resource);
				}
				return false;
			}
		}).into(imageView);
	}


	// 此api不需要另外开线程调用
	public static void getBitmapByUrl(Context context, String url, final ImageLoaderCallback callback) {
		if (TextUtils.isEmpty(url)) {
			callback.failToLoadImage();
		}
		Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
				if (resource != null) {
					callback.loadImageSuccessfully(resource);
				} else {
					callback.failToLoadImage();
				}
			}
			@Override
			public void onLoadFailed(Exception e, Drawable errorDrawable) {
				callback.failToLoadImage();
			}
		});
	}

	public static void releaseMemory(Context context) {
		Glide.get(context).clearMemory();
	}

	public interface ImageLoaderCallback {
		void loadImageSuccessfully(Object object);
		void failToLoadImage();
	}


}
