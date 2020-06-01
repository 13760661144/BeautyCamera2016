package cn.poco.web.info;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.poco.pocointerfacelibs.AbsBaseInfo;

/**
 * Created by Raining on 2016/11/3.
 */

public class UpdateInfo extends AbsBaseInfo
{
	/**
	 * app更新类型。
	 0：不需要更新,不需要显示页面;
	 1：app版本低于最新版本，高于最低版本，建议更新;
	 2：app版本低于最低版本，强制更新,跳过的按钮隐藏显示;
	 3: 服务器维护中;
	 */
	public enum UpdateType{
		unnecessary, //不需要更新
		proposedUpdate, //建议更新
		mandatoryUpdate, //强制更新
		serverMaintenance;  //服务器维护
	}
	private UpdateType updateType;   //更新类型
	private BaseInfo title;          //更新版本标题;
	private BaseInfo version;				//更新版本号;
	private DetailsInfo details;				//更新内容;
	private UrlInfo detailsUrlBtn;				//了解更新详情;
	private UrlInfo downloadUrlBtn;				//立即更新;
	private IgnoreInfo isIgnore;				//跳过按钮;
	private int popVersion;
	private String jsonCache;

	/**
	 * {
	 code: 200,
	 message: "Success!",
	 data: {
	 ret_code: 0,
	 ret_data: {
	 update_type: 1,
	 title: {
	 val: "发现新版本",
	 is_show: 1
	 },
	 version: {
	 val: "2.3.0",
	 is_show: 1
	 },
	 details: {
	 val: [
	 "1、云端相册升级，文件夹分类让照片管理更便捷",
	 "2、修复Bug"
	 ],
	 is_show: 1
	 },
	 details_url_btn: {
	 val: "",
	 name: "了解详情",
	 is_show: 1
	 },
	 download_url_btn: {
	 val: "https://itunes.apple.com/cn/app/id891640660?mt=8",
	 name: "立即更新",
	 is_show: 1
	 },
	 is_ignore: {
	 is_show: 1,
	 name: "跳过"
	 },
	 ret_msg: "发现新版本",
	 ret_notice: "发现新版本"
	 },
	 client_code: 100,
	 ver: "v1"
	 }
	 * @param object
	 * @return
	 * @throws Throwable
	 */

	@Override
	protected boolean DecodeMyData(Object object) throws Throwable
	{
		JSONObject json = (JSONObject)object;
		JSONObject jsonObject;
		json = json.getJSONObject("ret_data");

		updateType =UpdateType.values()[json.getInt("update_type")];

		title = new BaseInfo();
		jsonObject = json.getJSONObject("title");
		title.val = jsonObject.getString("val");
		title.isShow = jsonObject.getInt("is_show");

		version = new BaseInfo();
		jsonObject = json.getJSONObject("version");
		version.val = jsonObject.getString("val");
		version.isShow = jsonObject.getInt("is_show");

		isIgnore = new IgnoreInfo();
		jsonObject = json.getJSONObject("is_ignore");
		isIgnore.isShow = jsonObject.getInt("is_show");
		isIgnore.name = jsonObject.getString("name");

		detailsUrlBtn = new UrlInfo();
		jsonObject = json.getJSONObject("details_url_btn");
		detailsUrlBtn.val = jsonObject.getString("val");
		detailsUrlBtn.isShow = jsonObject.getInt("is_show");
		detailsUrlBtn.name = jsonObject.getString("name");

		downloadUrlBtn = new UrlInfo();
		jsonObject = json.getJSONObject("download_url_btn");
		downloadUrlBtn.val = jsonObject.getString("val");
		downloadUrlBtn.isShow = jsonObject.getInt("is_show");
		downloadUrlBtn.name = jsonObject.getString("name");

		details = new DetailsInfo();
		details.vals= new ArrayList<String>();
		jsonObject = json.getJSONObject("details");
		JSONArray arr = jsonObject.getJSONArray("val");
		for (int i = 0; i < arr.length(); i++) {
			details.vals.add(arr.getString(i));
		}
		details.isShow = jsonObject.getInt("is_show");

		popVersion = json.getInt("pop_version");

		return true;
	}

	public class BaseInfo{
		public String val;       //更新内容描述;
		public int isShow;    //是否显示更新版本标题内容，1代表要显示，0代表不显示;

	}
	public class UrlInfo extends BaseInfo{
		public String name;               //链接按钮显示内容;
	}

	public class IgnoreInfo{
		public String name;
		public int isShow;
	}
	public class DetailsInfo{
		public ArrayList<String> vals;       //更新内容描述;
		public int isShow;    //是否显示更新版本标题内容，1代表要显示，0代表不显示;
	}

	public UpdateType getUpdateType()
	{
		return updateType;
	}

	public BaseInfo getTitle()
	{
		return title;
	}

	public BaseInfo getVersion()
	{
		return version;
	}

	public DetailsInfo getDetails()
	{
		return details;
	}

	public UrlInfo getDetailsUrlBtn()
	{
		return detailsUrlBtn;
	}

	public UrlInfo getDownloadUrlBtn()
	{
		return downloadUrlBtn;
	}

	public IgnoreInfo getIsIgnore()
	{
		return isIgnore;
	}


	/**
	 * 使用getJsonCache 获取缓存并保存，
	 * 需要获取数据时 UpdateInfo updateInfo = new UpdateInfo()
	 *              updateInfo.DecodeData(jsonCache);
	 */

	@Override
	public boolean DecodeData(String str)
	{
		jsonCache = str;
		return super.DecodeData(str);
	}
	public String getJsonCache(){
		return jsonCache;
	}

	/**
	 * 年月日格式
	 *2016114
	 * @return
	 */
	public int getPopVersion()
	{
		return popVersion;
	}
}
