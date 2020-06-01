#pragma version(1)
#pragma rs java_package_name(cn.poco.image)

rs_allocation mask;
int alpha;
int comOp;
float rgbTransfer[256];


static int pocoDiv255Round(int prod) {
	prod += 128;
	return (prod + (prod >> 8)) >> 8;
}

//正常
static uchar opacity(unsigned char val1,unsigned char val2,int opa)
{
	int retVal;

	if(0 == opa)
		retVal = val1;
	else if(255 == opa)
		retVal = val2;
	else
	{
		retVal = pocoDiv255Round(opa * val2 + (255 - opa) * val1);
	}

	return (uchar)retVal;
}

//颜色加深
static unsigned char colorBurn(unsigned char val1,unsigned char val2)
{
	int retVal;

	if(val1 == 0)
		retVal = 0;
	else
	{
		retVal = 255 - (255 * (255 - val2)) / val1;
		retVal = (int)(clamp((float)retVal, 0.f, 255.f));
	}

	return (unsigned char)retVal;
}

//颜色减淡
static unsigned char colorDodge(unsigned char val1,unsigned char val2)
{
	int retVal;

	if(val1 == 255)
		retVal = 255;
	else
	{
		retVal = (255 * val2) / (255 - val1);
		retVal = (int)(clamp((float)retVal, 0.f, 255.f));
	}

	return (unsigned char)retVal;
}

//线性减淡
static unsigned char linearDodge(unsigned char val1,unsigned char val2)
{
	int valRet = (int)val1 + val2;
	valRet = (int)(clamp((float)valRet, 0.f, 255.f));

	return (unsigned char)(valRet);

}

//线性加深
static unsigned char linearBurn(unsigned char val1,unsigned char val2)
{
	int val3=val1+val2-255;
	val3=(val3<0?0:(val3>255?255:val3));
	return (unsigned char)val3;
}

//正片叠底
static unsigned char multiply(unsigned char val1,unsigned char val2)
{
	int retVal;

	retVal =  pocoDiv255Round((int)val1 * val2);

	return (unsigned char)retVal;
}

//滤色
static uchar screen(unsigned char val1,unsigned char val2)
{
	int retVal;

	retVal =  pocoDiv255Round(65025 -(255 - val1) * (255 - val2));
	retVal =  (int)(clamp((float)retVal, 0.f, 255.f));

	return (uchar)retVal;
}

//柔光
static uchar softlight(unsigned char val1,unsigned char val2)
{
    int retVal;
	if(val1 <= 127)
	{
		retVal = (2 * val1 - 255) * (255 * val2 - val2 * val2);
		retVal /= 65025;
		retVal += val2;
		retVal = (int)(clamp((float)retVal, 0.f, 255.f));
	}
	else
	{
		retVal = (2 * val1 - 255) * (255*sqrt((float)val2/255.f) - val2);
		retVal = pocoDiv255Round(retVal);
		retVal += val2;
		retVal = (int)(clamp((float)retVal, 0.f, 255.f));
	}
	return (uchar)retVal;
}

//变暗
static uchar darken(unsigned char val1,unsigned char val2)
{
	uchar retVal = min(val1, val2);
	return retVal;
}

//变亮
static unsigned char lighten(unsigned char val1,unsigned char val2)
{
	unsigned char retVal = max(val1, val2);
	return retVal;
}

//叠加
static unsigned char overlay(unsigned char val1,unsigned char val2)
{
	int retVal;

	if(val2 <= 127)
	{
		retVal = pocoDiv255Round(2 * (int)val1 * val2);

	}
	else
	{
		retVal = 255 - pocoDiv255Round(2 * (255 - (int)val1) * (255 - val2));

	}

	retVal = (int)(clamp((float)retVal, 0.f, 255.f));

	return (unsigned char)retVal;

}

//强光
static unsigned char hardLight(unsigned char val1,unsigned char val2)
{
	int retVal;

	if(val1 <= 127)
	{
		retVal = pocoDiv255Round(2 * (int)val1 * val2);

	}
	else
	{
		retVal = 255 - pocoDiv255Round(2*(255-val1)*(255-val2));
	}

	retVal = (int)(clamp((float)retVal, 0.f, 255.f));

	return (unsigned char)retVal;
}

//亮光
static unsigned char vividLight(unsigned char val1,unsigned char val2)
{
	int val3;
	if(val1>127)
		val3=(255*val2)/(float)(2*(255-val1));
	else
		val3=255-128*(255-val2)/(float)val1;
	val3=(val3<0?0:(val3>255?255:val3));
	return (unsigned char)val3;

}

//线性光
static unsigned char linerLight(unsigned char val1,unsigned char val2)
{
	int val3;
	val3=val2+2*val1-255;
	val3=(val3<0?0:(val3>255?255:val3));
	return (unsigned char)val3;
}

//差值
static unsigned char difference(unsigned char val1,unsigned char val2)
{
	int val3 = (int)val1 - val2;
	val3 =  val3 < 0 ? (-val3) : val3;

	return (unsigned char )(val3);
}

//排除
static unsigned char exclusion(unsigned char val1,unsigned char val2)
{
	int val3;
	val3=val1+val2-2*val1*val2/255;
	val3=(val3<0?0:(val3>255?255:val3));
	return (unsigned char)val3;
}

uchar4 __attribute__((kernel)) root(uchar4 in, int x, int y) {

   uchar4 pt = rsGetElementAt_uchar4(mask, x, y);

   uchar r = pt.r;
   uchar g = pt.g;
   uchar b = pt.b;

   if(pt.a == 0)
   {
     return in;
   }
   else if(pt.a != 255)
   {
        r = pt.r * rgbTransfer[pt.a] > 255 ? 255 : pt.r * rgbTransfer[pt.a];
        g = pt.g * rgbTransfer[pt.a] > 255 ? 255 : pt.g * rgbTransfer[pt.a];
        b = pt.b * rgbTransfer[pt.a] > 255 ? 255 : pt.b * rgbTransfer[pt.a];
   }

   switch(comOp)
   {
        case 20:
           r = darken(pt.r, in.r);
           g = darken(pt.g, in.g);
           b = darken(pt.b, in.b);
           break;
        case 45:
           r = screen(pt.r, in.r);
           g = screen(pt.g, in.g);
           b = screen(pt.b, in.b);
           break;
        case 46:
           r = softlight(pt.r, in.r);
           g = softlight(pt.g, in.g);
           b = softlight(pt.b, in.b);
           break;
        case 33:
           r = lighten(pt.r, in.r);
           g = lighten(pt.g, in.g);
           b = lighten(pt.b, in.b);
           break;
        case 8:
           r = colorBurn(pt.r, in.r);
           g = colorBurn(pt.g, in.g);
           b = colorBurn(pt.b, in.b);
           break;
        case 38:
           r = multiply(pt.r, in.r);
           g = multiply(pt.g, in.g);
           b = multiply(pt.b, in.b);
           break;
        case 9:
           r = colorDodge(pt.r, in.r);
           g = colorDodge(pt.g, in.g);
           b = colorDodge(pt.b, in.b);
           break;
        case 61:
           r = linearDodge(pt.r, in.r);
           g = linearDodge(pt.g, in.g);
           b = linearDodge(pt.b, in.b);
           break;
        case 62:
           r = linearBurn(pt.r, in.r);
           g = linearBurn(pt.g, in.g);
           b = linearBurn(pt.b, in.b);
           break;
        case 41:
           r = overlay(pt.r, in.r);
           g = overlay(pt.g, in.g);
           b = overlay(pt.b, in.b);
           break;
        case 30:
           r = hardLight(pt.r, in.r);
           g = hardLight(pt.g, in.g);
           b = hardLight(pt.b, in.b);
           break;
        case 59:
           r = vividLight(pt.r, in.r);
           g = vividLight(pt.g, in.g);
           b = vividLight(pt.b, in.b);
           break;
        case 34:
           r = linerLight(pt.r, in.r);
           g = linerLight(pt.g, in.g);
           b = linerLight(pt.b, in.b);
           break;
        case 26:
           r = difference(pt.r, in.r);
           g = difference(pt.g, in.g);
           b = difference(pt.b, in.b);
           break;
        case 29:
           r = exclusion(pt.r, in.r);
           g = exclusion(pt.g, in.g);
           b = exclusion(pt.b, in.b);
           break;
   }

   in.r = opacity(in.r, r, alpha * pt.a / 255);
   in.g = opacity(in.g, g, alpha * pt.a / 255);
   in.b = opacity(in.b, b, alpha * pt.a / 255);

   return in;
}

void init(){

    rgbTransfer[0] = 0.f;
    for(int i = 1; i < 256; i++)
    {
        rgbTransfer[i] = 255.f / i;
    }
}