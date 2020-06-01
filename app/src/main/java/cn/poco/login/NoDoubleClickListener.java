package cn.poco.login;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.Calendar;

public abstract class NoDoubleClickListener implements OnClickListener {
	private static final long MIN_CLICK_DELAY_TIME = 1000L;
	private long lastTime = 0;
	@Override
	public void onClick(View v) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if(currentTime - lastTime > MIN_CLICK_DELAY_TIME){
			lastTime = currentTime;
			onNoDoubleClick(v);
		}
	}

	public abstract void onNoDoubleClick(View v);
}
