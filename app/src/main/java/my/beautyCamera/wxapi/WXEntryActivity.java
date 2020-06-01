package my.beautyCamera.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.poco.blogcore.WeiXinBlog;
import cn.poco.login.LoginPage;
import cn.poco.share.Constant;
import my.beautyCamera.BaseActivity;
import my.beautyCamera.PocoCamera;

public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler
{
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, Constant.getWeixinAppId(this));
		api.handleIntent(getIntent(), this);
		SendAuth.Resp resp = new SendAuth.Resp(getIntent().getExtras());
		if(resp != null && resp.state != null && resp.state.equals(WeiXinBlog.WX_LOGIN))
		{
			if(resp.errCode == BaseResp.ErrCode.ERR_OK) LoginPage.mWeiXinGetCode = resp.code;
			SendWXAPI.dispatchResult(resp.errCode);
		}
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req)
	{
		switch(req.getType())
		{
			case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
				Intent intent = new Intent(WXEntryActivity.this, PocoCamera.class);
				if(getIntent() != null)
				{
					Bundle bundle = new Bundle();
					bundle.putBundle("bundle", getIntent().getExtras());
					bundle.putString("startBy", "wx");
					intent.putExtras(bundle);
					WXEntryActivity.this.startActivity(intent);
				}
				finish();
				break;
			case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
				Toast.makeText(this, "分享到微信成功", Toast.LENGTH_LONG).show();
				finish();
				break;

			default:
				finish();
				break;
		}
	}

	@Override
	public void onResp(BaseResp resp)
	{
		SendWXAPI.dispatchResult(resp.errCode);
		finish();
		/*File sdcard = Environment.getExternalStorageDirectory();
		String FileName = sdcard.getPath() + Constant.PATH_APPDATA + "/share/wxrequest";
		File file = new File(FileName);
		if(file.exists())
		{
			String request = null;
	        FileInputStream fis;
			try 
			{
				fis = new FileInputStream(file);
		        byte[] buffer = new byte[fis.available()];  
		        fis.read(buffer); 
		        fis.close();
		        request = EncodingUtils.getString(buffer, "UTF-8");
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			} 
			if(request != null && request.length() > 0)
			{
				int chongyin = request.indexOf("chongyin");
				if(chongyin != -1)
				{
					String code = null;
					switch (resp.errCode) 
					{
					case BaseResp.ErrCode.ERR_OK:
						code = "succeed";
						break;
					case BaseResp.ErrCode.ERR_USER_CANCEL:
						code = "cancel";
						break;
					case BaseResp.ErrCode.ERR_AUTH_DENIED:
						code = "failed";
						break;
					default:
						code = "failed";
						break;
					}
					if(code.equals("succeed"))
					{
						Intent intent =new Intent(WXEntryActivity.this, PocoCamera.class);
						intent.setAction("pocoprint.sendwx");
						Bundle bundle = new Bundle();
						bundle.putString("order_no", getValue(request, "number"));
						bundle.putString("wx_result", code);
						bundle.putString("order_key", getValue(request, "key"));
						intent.putExtras(bundle);
						WXEntryActivity.this.startActivity(intent);
					}
				
				}
			}
			file.delete();
		}
		finish();*/
	}
/*	private String getValue(String text, String type)
	{
		String value = null;
		int begin = text.indexOf("<"+type+">");
		int end = text.indexOf("</"+type+">");
		if(begin != -1 && end > begin)
		{
			value = text.substring(begin+2+type.length(), end);
		}
		return value;
	}*/

}
