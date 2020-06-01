package cn.poco.camera3.util;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.util.Property;
import android.view.View;

/**
 * 动画
 * Created by Gxx on 2018/1/3.
 */

public class AnimUtil
{
	/**
	 * 左右抖动动画
	 *
	 * @param view          对象
	 * @param value         抖动偏移量
	 * @param anim_duration 抖动的总时长
	 * @param wait_duration 空白期时长 (抖动结束，直至下次开始抖动的过程)
	 * @param wait_first    先等待，再抖动
	 * @return
	 */
	public static ObjectAnimator jitterXAnim(View view, int value, long anim_duration, long wait_duration, boolean wait_first)
	{
		return jitterAnim(view, View.TRANSLATION_X, value, anim_duration, wait_duration, wait_first);
	}

	public static ObjectAnimator jitterYAnim(View view, int value, long anim_duration, long wait_duration, boolean wait_first)
	{
		return jitterAnim(view, View.TRANSLATION_Y, value, anim_duration, wait_duration, wait_first);
	}

	private static ObjectAnimator jitterAnim(View view, Property property, int value, long anim_duration, long wait_duration, boolean wait_first)
	{
		long duration = anim_duration + wait_duration;

		int len = wait_first ? 9 : 8;
		float[] duration_points = new float[len];

		duration_points[0] = 0f;
		if(wait_first)
		{
			duration_points[1] = wait_duration * 1f / duration;
			duration_points[2] = (wait_duration + .10f * anim_duration) / duration;
			duration_points[3] = (wait_duration + .26f * anim_duration) / duration;
			duration_points[4] = (wait_duration + .42f * anim_duration) / duration;
			duration_points[5] = (wait_duration + .58f * anim_duration) / duration;
			duration_points[6] = (wait_duration + .74f * anim_duration) / duration;
			duration_points[7] = (wait_duration + .90f * anim_duration) / duration;
			duration_points[8] = 1f;
		}
		else
		{
			duration_points[1] = .10f * anim_duration / duration;
			duration_points[2] = .26f * anim_duration / duration;
			duration_points[3] = .42f * anim_duration / duration;
			duration_points[4] = .58f * anim_duration / duration;
			duration_points[5] = .74f * anim_duration / duration;
			duration_points[6] = .90f * anim_duration / duration;
			duration_points[7] = 1f * anim_duration / duration;
		}

		Keyframe[] keyframes = new Keyframe[len];
		keyframes[0] = Keyframe.ofFloat(duration_points[0], 0);
		if(wait_first)
		{
			keyframes[1] = Keyframe.ofFloat(duration_points[1], 0);
			keyframes[2] = Keyframe.ofFloat(duration_points[2], -value);
			keyframes[3] = Keyframe.ofFloat(duration_points[3], value);
			keyframes[4] = Keyframe.ofFloat(duration_points[4], -value);
			keyframes[5] = Keyframe.ofFloat(duration_points[5], value);
			keyframes[6] = Keyframe.ofFloat(duration_points[6], -value);
			keyframes[7] = Keyframe.ofFloat(duration_points[7], value);
			keyframes[8] = Keyframe.ofFloat(duration_points[8], 0f);
		}
		else
		{
			keyframes[1] = Keyframe.ofFloat(duration_points[1], -value);
			keyframes[2] = Keyframe.ofFloat(duration_points[2], value);
			keyframes[3] = Keyframe.ofFloat(duration_points[3], -value);
			keyframes[4] = Keyframe.ofFloat(duration_points[4], value);
			keyframes[5] = Keyframe.ofFloat(duration_points[5], -value);
			keyframes[6] = Keyframe.ofFloat(duration_points[6], value);
			keyframes[7] = Keyframe.ofFloat(duration_points[7], 0f);
		}

		PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(property, keyframes);

		return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).setDuration(duration);
	}
}
