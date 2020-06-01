package cn.poco.web;

import cn.poco.pocointerfacelibs.IPOCO;

/**
 * Created by Raining on 2016/11/3.
 * 获取网络信息，更新状态等url
 */

public interface IWeb extends IPOCO
{

	/**
	 * 获取app更新信息
	 */
	String GetAppUpdateInfoUrl();
}
