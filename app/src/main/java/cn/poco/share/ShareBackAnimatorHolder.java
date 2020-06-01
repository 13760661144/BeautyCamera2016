package cn.poco.share;

import cn.poco.framework.AnimatorHolder;

/**
 * Created by pocouser on 2017/8/8.
 */

public abstract class ShareBackAnimatorHolder extends AnimatorHolder
{
	@Override
	public AddType getAddType()
	{
		return AddType.pre;
	}
}
