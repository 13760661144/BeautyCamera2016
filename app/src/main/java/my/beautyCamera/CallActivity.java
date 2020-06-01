package my.beautyCamera;

import android.content.Intent;
import android.os.Bundle;

public class CallActivity extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		intent.setClass(this, PocoCamera.class);
		startActivity(intent);
		this.finish();
	}
}
