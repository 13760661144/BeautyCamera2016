package cn.poco.Theme;

/**
 * Created by lgd on 2016/12/9.
 */

public class ThemeInfo
{
	enum Type
	{
		/** draw the gradient from the top-left to the bottom-right */
		LEFT_RIGHT(0),
		/** draw the gradient from the top-right to the bottom-left */
		TOP_BOTTOM(1),
		/** draw the gradient from the bottom-right to the top-left */
		RIGHT_LEFT(2);

		private int value = 0;

		private Type(int value) {    //    必须是private的，否则编译错误
			this.value = value;
		}

		public static Type valueOf(int value) {    //    手写的从int到enum的转换函数
			switch (value) {
				case 0:
					return LEFT_RIGHT;
				case 1:
					return TOP_BOTTOM;
				case 2:
					return RIGHT_LEFT;
				default:
					return null;
			}
		}

		public int value() {
			return this.value;
		}

	}
	private int skinColor;      //app主题颜色
	private int[] colors;		//渐变颜色
	private String title;
	private Type type;
	public ThemeInfo(int[] colors, String title)
	{
		this.colors = colors;
		this.title = title;
	}

	public ThemeInfo(int[] colors, String title,int skinColor, Type type)
	{
		this.skinColor = skinColor;
		this.colors = colors;
		this.title = title;
		this.type = type;
	}

	public int[] getColors()
	{
		return colors;
	}

	public void setColors(int[] colors)
	{
		this.colors = colors;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getSkinColor()
	{
		return skinColor;
	}

	public void setSkinColor(int skinColor)
	{
		this.skinColor = skinColor;
	}

	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}
}
