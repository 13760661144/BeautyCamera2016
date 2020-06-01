package cn.poco.home.home4.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class GravitySensor
{

	private Sensor sensor;

	private SensorManager sensorManager;

	private Context context;

	private SensorListener mySensorListener;

	private SensorEventListener lsn;

	private float x, y, z;
//	private float mLastX = 0, mLastY = 0, mLastZ = 0;

	public GravitySensor(Context context)
	{

		this.context = context;

		sensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);

		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		lsn = new SensorEventListener()
		{
			/**
			 * 手机屏幕向上(z轴朝天)水平放置的时侯，(x，y，z)的值分别为(0，0，10);
			 　　手机屏幕向下(z轴朝地)水平放置的时侯，(x，y，z)的值分别为(0，0，-10);
			 　　手机屏幕向左侧放(x轴朝天)的时候，(x，y，z)的值分别为(10，0，0);
			 　　手机竖直(y轴朝天)向上的时候，(x，y，z)的值分别为(0，10，0);
			 * @param event
			 */
			@Override
			public void onSensorChanged(SensorEvent event)
			{
				if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				{
					x = event.values[0];
					y = event.values[1];
					z = event.values[2];
					mySensorListener.doRotate(x, y);
				}
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy)
			{

			}
		};


	}

	public void setSensorListener(SensorListener sensorListener)
	{
		this.mySensorListener = sensorListener;
	}

	public void onStart()
	{
        try {
            sensorManager.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_UI);
        } catch (Throwable t) {
            t.printStackTrace();
        }
	}

	public void onStop()
	{
        try {
            sensorManager.unregisterListener(lsn);
        } catch (Throwable t) {
            t.printStackTrace();
        }
	}

	public interface SensorListener
	{
		 void doRotate(float x, float y);
	}

}
