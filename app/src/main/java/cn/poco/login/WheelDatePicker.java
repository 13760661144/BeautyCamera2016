package cn.poco.login;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;

import cn.poco.tianutils.ShareData;
import my.beautyCamera.R;

public class WheelDatePicker extends LinearLayout
{
	public static final int MIN_YEAR = 1890;
	public static final int DEF_YEAR = 1990;
	protected PickerCtrl m_yearPicker;
	protected PickerCtrl m_monthPicker;
	protected PickerCtrl m_dayPicker;
	
	protected int m_curYear;	//当前获取的年
	protected int m_curMonth;	//月
	protected int m_curDay;		//日
	
	protected int m_curSelYear;
	protected int m_curSelMonth;
	protected int m_curSelDay;
	
	protected ArrayList<String> m_years;
	protected ArrayList<String> m_months;
	protected ArrayList<String> m_days;
	
	protected OnFocusChangeListener m_listener;

	public WheelDatePicker(Context context)
	{
		this(context, null);
	}

	public WheelDatePicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		Init();
	}
	
	private void Init()
	{
		this.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams ll;
		int width = ShareData.m_screenWidth / 5;
		
		m_yearPicker = new PickerCtrl(getContext());
		m_yearPicker.setGrivity(Gravity.RIGHT);
		m_yearPicker.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		m_yearPicker.setOnSelectListener(new PickerCtrl.OnSelectListener()
		{
			
			@Override
			public void onSelected(int index, String item)
			{
				int pos = item.lastIndexOf(getContext().getResources().getString(R.string.wheeldata_year));
				m_curSelYear = Integer.parseInt(item.substring(0, pos));
				
				InitMonths(m_curSelYear);
				int select = m_curSelMonth - 1;
				if(select >= m_months.size())
				{
					select = m_months.size() - 1;
				}
				m_monthPicker.setItems(m_months, select);
				m_curSelMonth = select + 1;
				
				InitDays(m_curSelYear, m_curSelMonth);
				select = m_curSelDay - 1;
				if(select >= m_days.size())
				{
					select = m_days.size() - 1;
				}
				m_dayPicker.setItems(m_days, select);
				m_curSelDay = select + 1;

				if(m_listener != null)
				{
					m_listener.onChange(m_curSelYear, m_curSelMonth, m_curSelDay);
				}
			}
		});
		ll = new LayoutParams(width * 2, LayoutParams.WRAP_CONTENT);
		this.addView(m_yearPicker, ll);
		
		m_monthPicker = new PickerCtrl(getContext());
		m_monthPicker.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		m_monthPicker.setOnSelectListener(new PickerCtrl.OnSelectListener()
		{
			
			@Override
			public void onSelected(int index, String item)
			{
				int pos = item.lastIndexOf(getContext().getResources().getString(R.string.wheeldata_month));
				m_curSelMonth = Integer.parseInt(item.substring(0, pos));
				InitDays(m_curSelYear, m_curSelMonth);
				int select = m_curSelDay - 1;
				if(select >= m_days.size())
				{
					select = m_days.size() - 1;
				}
				m_dayPicker.setItems(m_days, select);
				m_curSelDay = select + 1;

				if(m_listener != null)
				{
					m_listener.onChange(m_curSelYear, m_curSelMonth, m_curSelDay);
				}
			}
		});
		ll = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		this.addView(m_monthPicker, ll);
		
		m_dayPicker = new PickerCtrl(getContext());
		m_dayPicker.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		m_dayPicker.setGrivity(Gravity.LEFT);
		m_dayPicker.setOnSelectListener(new PickerCtrl.OnSelectListener()
		{
			@Override
			public void onSelected(int index, String item)
			{
				int pos = item.lastIndexOf(getContext().getResources().getString(R.string.wheeldata_day));
				m_curSelDay = Integer.parseInt(item.substring(0, pos));
				if(m_listener != null)
				{
					m_listener.onChange(m_curSelYear, m_curSelMonth, m_curSelDay);
				}
			}
		});
		ll = new LayoutParams(width * 2, LayoutParams.WRAP_CONTENT);
		this.addView(m_dayPicker, ll);
	}
	
	public void InitDate(int year, int mouth, int day)
	{
		m_curSelYear = year;
		m_curSelMonth = mouth;
		m_curSelDay = day;
		

		Calendar calendar=Calendar.getInstance();
        m_curYear = calendar.get(Calendar.YEAR);
        m_curMonth = calendar.get(Calendar.MONTH) + 1;
        m_curDay = calendar.get(Calendar.DAY_OF_MONTH);
		
		InitYears();
		
		if(m_curSelYear < MIN_YEAR)
		{
			m_curSelYear = DEF_YEAR;
		}
		if(m_curSelYear > m_curYear)
		{
			m_curSelYear = m_curYear;
		}
		int maxMonth = InitMonths(m_curSelYear);
		
		if(m_curSelMonth < 1)
		{
			m_curSelMonth = 1;
		}
		if(m_curSelMonth > maxMonth)
		{
			m_curSelMonth = 12;
		}
		InitDays(m_curSelYear, m_curSelMonth);
		if(m_curSelDay < 1)
		{
			m_curSelDay = 1;
		}
		int days = CalDaysOfYearMonth(m_curSelYear, m_curSelMonth);
		if(m_curSelDay > days)
		{
			m_curSelDay = days;
		}
		
		m_yearPicker.setItems(m_years, m_curSelYear - MIN_YEAR);
		m_monthPicker.setItems(m_months, m_curSelMonth - 1);
		m_dayPicker.setItems(m_days, m_curSelDay - 1);
		if(m_listener != null)
		{
			m_listener.onChange(m_curSelYear, m_curSelMonth, m_curSelDay);
		}
	}
	
	protected void InitYears()
	{
        m_years = new ArrayList<String>();
        for(int i = MIN_YEAR; i <= m_curYear; i ++)
        {
        	m_years.add(String.valueOf(i) + getContext().getResources().getString(R.string.wheeldata_year));
        }
	}
	
	protected int InitMonths(int year)
	{
		m_months = new ArrayList<String>();
		int maxMonth = 12;
		if(year == m_curYear)
		{
			maxMonth = m_curMonth;
		}
		for(int i = 1; i <= maxMonth; i ++)
		{
			m_months.add(String.valueOf(i) + getContext().getResources().getString(R.string.wheeldata_month));
		}
		return maxMonth;
	}
	
	protected void InitDays(int year, int month)
	{
		int days = CalDaysOfYearMonth(m_curSelYear, m_curSelMonth);
		if(year == m_curYear && month == m_curMonth)
		{
			days = m_curDay;
		}
		m_days = new ArrayList<String>();
		for(int i = 1; i <= days; i ++)
		{
			m_days.add(String.valueOf(i) + getContext().getResources().getString(R.string.wheeldata_day));
		}
	}
	
	protected int CalDaysOfYearMonth(int year, int month)
	{
		int days = 0;  
		if (month != 2) {  
			switch (month) {  
				case 1:  
				case 3:  
				case 5:  
				case 7:  
				case 8:  
				case 10:  
				case 12:  
					days = 31;  
					break;  
				case 4:  
				case 6:  
				case 9:  
				case 11:  
					days = 30;  

			}  
		} else {  
			//闰年  
			if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)  
				days = 29;  
			else  
				days = 28;  

		}  
		return days;  
	}
	
	public void SetOnFocusChangeListener(OnFocusChangeListener lis)
	{
		m_listener = lis;
	}
	
	public static interface OnFocusChangeListener
	{
		public void onChange(int year, int month, int day);
	}

}
