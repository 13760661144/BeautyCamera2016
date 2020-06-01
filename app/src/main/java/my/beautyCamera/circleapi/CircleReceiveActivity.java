package my.beautyCamera.circleapi;

import android.os.Bundle;

import com.taotie.cn.circlesdk.CircleSDK;
import com.taotie.cn.circlesdk.ICIRCLEAPI;

import my.beautyCamera.BaseActivity;

/**
 * Created by pocouser on 2017/3/10.
 */

public class CircleReceiveActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		ICIRCLEAPI circleApi = CircleSDK.createApi(this, 3);
		circleApi.handleCallBackMessage(getIntent(), this);
	}
}
