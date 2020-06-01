#include <jni.h>
#include <android/bitmap.h>
#include <android/log.h>
#include <stdlib.h>
#include <stdio.h>
#include <utime.h>
#include <unistd.h>

#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"JniUtils",__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,"JniUtils",__VA_ARGS__)

void adjust_pixels(unsigned char* data, int w, int h)
{
	int s = h-1;
	unsigned char a,r,g,b;
	int f,l,t,j;
	int x, y;
	int k = h/2;
	for(y = 0; y < k; y++)
	{
		t = y*w;
		j = (s-y)*w;
		for(x = 0; x < w; x++)
		{
			f = (t+x)*4;
			l = (j+x)*4;
			b = data[f];
			g = data[f+1];
			r = data[f+2];
			a = data[f+3];
			
			data[f] = data[l+2];
			data[f+1] = data[l+1];
			data[f+2] = data[l];
			data[f+3] = data[l+3];
			data[l] = r;
			data[l+1] = g;
			data[l+2] = b;
			data[l+3] = a;
		}
	}
	if(h%2 == 1)
	{
		t = k*w;
		for(x = 0; x < w; x++)
		{
			f = (t+x)*4;
			b = data[f];
			data[f] = data[f+2];
			data[f+2] = b;
		}
	}
}

void reversePixels(int *data, int w, int h)
{
	int s = h-1;
	int f,l,t,j;
	int x, y;
	int k = h/2;
	int temp;
	for(y = 0; y < k; y++)
	{
		t = y*w;
		j = (s-y)*w;
		for(x = 0; x < w; x++)
		{
			f = t+x;
			l = j+x;
			temp = data[f];
			data[f] = data[l];
			data[l] = temp;
		}
	}
}

jobject createBitmap32(JNIEnv* env, int w, int h)
{
	jclass cls = (*env)->FindClass(env, "android/graphics/Bitmap");
	if(cls != NULL)
	{
		jmethodID method = (*env)->GetStaticMethodID(env, cls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
		if(method != NULL)
		{
			jclass cls_cfg = (*env)->FindClass(env, "android/graphics/Bitmap$Config");
			if(cls_cfg != NULL)
			{
				jfieldID field = (*env)->GetStaticFieldID(env, cls_cfg, "ARGB_8888", "Landroid/graphics/Bitmap$Config;");
				if(field != NULL)
				{
					jobject argb8888 = (*env)->GetStaticObjectField(env, cls_cfg, field);
					if(argb8888 != NULL)
					{
						jobject bmp = (*env)->CallStaticObjectMethod(env, cls, method, w, h, argb8888);
						(*env)->DeleteLocalRef(env, argb8888);
						(*env)->DeleteLocalRef(env, cls);
						(*env)->DeleteLocalRef(env, cls_cfg);
						return bmp;
					}
				}
				(*env)->DeleteLocalRef(env, cls_cfg);
			}
		}
		(*env)->DeleteLocalRef(env, cls);
	}
	cls = (*env)->FindClass(env, "cn/poco/utils/JniUtils");
	if(cls != NULL)
	{
		jmethodID method = (*env)->GetStaticMethodID(env, cls, "createBitmap", "(II)Landroid/graphics/Bitmap;");
		if(method != NULL)
		{
			jobject bmp = (*env)->CallStaticObjectMethod(env, cls, method, w, h);
			(*env)->DeleteLocalRef(env, cls);
			return bmp;
		}
		(*env)->DeleteLocalRef(env, cls);
	}
	return NULL;
}

void Java_cn_poco_utils_JniUtils_conversePixels(JNIEnv* env, jobject thiz, 
	jbyteArray pixelArray, jint w, jint h)
{
	unsigned char *data;
	data = (*env)->GetByteArrayElements(env, pixelArray, 0);
	adjust_pixels(data, w, h);
	(*env)->ReleaseByteArrayElements(env, pixelArray, data, 0);
}

void Java_cn_poco_utils_JniUtils_reversePixels(JNIEnv* env, jobject thiz, 
	jintArray pixelArray, jint w, jint h)
{
	int *data;
	data = (*env)->GetIntArrayElements(env, pixelArray, 0);
	reversePixels(data, w, h);
	(*env)->ReleaseIntArrayElements(env, pixelArray, data, 0);
}

jbyteArray Java_cn_poco_utils_JniUtils_byteArrayToIntArray(JNIEnv* env, jobject thiz, jbyteArray byteArray)
{
	int len = (*env)->GetArrayLength(env, byteArray);
	unsigned char *data;
	data = (*env)->GetByteArrayElements(env, byteArray, 0);
	len = len/4;
	jintArray outArray = (*env)->NewIntArray(env, len);
	(*env)->SetIntArrayRegion(env, outArray, 0, len, (jint*)data);
	(*env)->ReleaseByteArrayElements(env, byteArray, data, 0);
	return outArray;
}

JNIEXPORT jboolean JNICALL Java_cn_poco_utils_JniUtils_saveAlphaBitmap(JNIEnv * env, jobject  obj, jobject bitmap, jbyteArray file)
{
	AndroidBitmapInfo  info;
    unsigned char*     pixels;
    int                ret;
    
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_88888 !");
        return JNI_FALSE;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void*)&pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    
    //读取文件名
    int len = (*env)->GetArrayLength(env, file);
	char* lpstr = (char*)(*env)->GetByteArrayElements(env, file, 0);
	char* lpcsfile = (char*)malloc(len+1);
	memcpy(lpcsfile, lpstr, len);
	lpcsfile[len] = 0;
    
    //参数设置
    int		temp_int = 0;
	short	temp_short = 0;
	int		offset = 54;
	int		w = info.width;
	int		h = info.height;
	int		data_size = w*h*4;
	int		fsize = data_size+offset;
	jboolean result = JNI_FALSE;
	
	//拷贝数据
	unsigned char*   bits = (unsigned char*)malloc(data_size);
	memcpy(bits, pixels, data_size);
	AndroidBitmap_unlockPixels(env, bitmap);
	
	FILE* fp = fopen(lpcsfile, "wb");
	if(fp != NULL)
	{
		adjust_pixels(bits, w, h);
		fwrite("BM", 1, 2, fp);
		fwrite(&fsize, 4, 1, fp);
		temp_int = 0;
		fwrite(&temp_int, 4, 1, fp);
		fwrite(&offset, 4, 1, fp);
		temp_int = 0x00000028;
		fwrite(&temp_int, 4, 1, fp);
		fwrite(&w, 4, 1, fp);
		fwrite(&h, 4, 1, fp);
		temp_short = 1;
		fwrite(&temp_short, 2, 1, fp);
		temp_short = 32;
		fwrite(&temp_short, 2, 1, fp);
		temp_int = 0;
		fwrite(&temp_int, 4, 1, fp);
		fwrite(&data_size, 4, 1, fp);
		temp_int = 0;
		fwrite(&temp_int, 4, 1, fp);
		fwrite(&temp_int, 4, 1, fp);
		fwrite(&temp_int, 4, 1, fp);
		fwrite(&temp_int, 4, 1, fp);
		fwrite(bits, 1, data_size, fp);
		fclose(fp);
		result = JNI_TRUE;
	}
	
	free(bits);
	free(lpcsfile);
    (*env)->ReleaseByteArrayElements(env, file, lpstr, 0);
    return result;
}

JNIEXPORT jobject JNICALL Java_cn_poco_utils_JniUtils_readAlphaBitmap(JNIEnv * env, jobject  obj, jbyteArray file, jint sample_size)
{
    //读取文件名
    int len = (*env)->GetArrayLength(env, file);
	char* lpstr = (char*)(*env)->GetByteArrayElements(env, file, 0);
	char* lpcsfile = (char*)malloc(len+1);
	memcpy(lpcsfile, lpstr, len);
	lpcsfile[len] = 0;
    
    //参数设置
	int		vint;
	short	vshort;
	int		w = 0;
	int		h = 0;
	unsigned char buffer[1024];

	if(sample_size < 1)
	{
		sample_size = 1;
	}
	
	jobject bitmap = NULL;
	//拷贝数据
	FILE* fp = fopen(lpcsfile, "rb");
	if(fp != NULL)
	{
		fread(buffer, 1, 2, fp);
		if(buffer[0] == 'B' && buffer[1] == 'M')
		{
			fread(buffer, 1, 8, fp);
			fread(&vint, 4, 1, fp);
			int offset = vint;
			fread(buffer, 1, 4, fp);
			fread(&vint, 4, 1, fp);
			w = vint;
			fread(&vint, 4, 1, fp);
			h = vint;
			fread(buffer, 1, 2, fp);
			fread(&vshort, 2, 1, fp);
			int bits = vshort;
			fread(&vint, 4, 1, fp);
			int compression = vint;
			fread(&vint, 4, 1, fp);
			int len = vint;
			if(bits == 32 && compression == 0)
			{
				fread(buffer, 1, offset-38, fp);
				int read = 0;
				int line_cbytes = w*4;
				int read_size = 0;
				w /= sample_size;
				h /= sample_size;
				len = w*h*4;
				int j = 0;
				unsigned char* data = (unsigned char*)malloc(len);
				if(sample_size > 1)
				{
					unsigned char* line_bytes = (unsigned char*)malloc(line_cbytes);
					int k = 0;
					int xskip = sample_size*4;
					int x, y;
					for(y = 0; y < h; y++)
					{
						read_size = 0;
						while((read = fread(line_bytes+read_size, 1, line_cbytes-read_size, fp)) != -1)
						{
							read_size += read;
							if(read_size >= line_cbytes)
								break;
						}
						x = 0;
						if(read_size == line_cbytes)
						{
							j = (h-y-1)*w*4;
							while(x < w)
							{
								k = x*xskip;
								data[j++] = line_bytes[k+2];
								data[j++] = line_bytes[k+1];
								data[j++] = line_bytes[k];
								data[j++] = line_bytes[k+3];
								x++;
							}
						}
						fseek(fp, line_cbytes*(sample_size-1), SEEK_CUR);
					}
					free(line_bytes);
				}
				else
				{
					while((read = fread(data+read_size, 1, len-read_size, fp)) != -1)
					{
						read_size += read;
						if(read_size >= len)
							break;
					}
					adjust_pixels(data, w, h);
				}
				//
				AndroidBitmapInfo  info;
				int ret;
				unsigned char* pixels = NULL;
				bitmap = createBitmap32(env, w, h);
				if(bitmap != NULL)
				{
					int ok = 1;
					if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
						LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
						ok = 0;
					}
					if(ok)
					{
						if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
							LOGE("Bitmap format is not RGB_88888 !");
							ok = 0;
						}
					}
					if(ok)
					{
						if(AndroidBitmap_lockPixels(env, bitmap, (void*)&pixels) < 0) {
							LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
							ok = 0;
						}
					}
					if(ok)
					{
						for(j = 0; j < len; j++)
						{
							pixels[j] = data[j];
						}
					}
				}
				free(data);
				AndroidBitmap_unlockPixels(env, bitmap);
			}
		}
		fclose(fp);
	}
	free(lpcsfile);
    return bitmap;
}

JNIEXPORT jintArray JNICALL Java_cn_poco_utils_JniUtils_getAlphaArea(JNIEnv * env, jobject  obj, jobject bitmap)
{
	AndroidBitmapInfo  info;
    unsigned char*     pixels;
    int                ret;
    
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return NULL;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_88888 !");
        return NULL;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void*)&pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return NULL;
    }
    
    jintArray rcArray = NULL;
    int w = info.width;
    int h = info.height;
    int minX = w;
	int maxX = 0;
	int minY = h;
	int maxY = 0;
	int x = 0;
	int y = 0;
	unsigned char a = 0;
	for(y = 0; y < h; y++)
	{
		for(x = 0; x < w; x++)
		{
			a = pixels[(w*y+x)*4+3];
			if(a < 200)
			{
				if(x < minX)
				{
					minX = x;
				}
				if(x > maxX)
				{
					maxX = x;
				}
				if(y < minY)
				{
					minY = y;
				}
				if(y > maxY)
				{
					maxY = y;
				}
			}
		}
	}
	if(minX < maxX && minY < maxY)
	{
		rcArray = (*env)->NewIntArray(env, 4);
		jint rc[4] = {minX, minY, maxX, maxY};
		(*env)->SetIntArrayRegion(env, rcArray, 0, 4, (jint*)rc);
	}
    AndroidBitmap_unlockPixels(env, bitmap);
    return rcArray;
}

JNIEXPORT jboolean JNICALL Java_cn_poco_utils_JniUtils_setLastModified(JNIEnv * env, jobject  obj, jobject file, jlong time)
{
	char* szfile = NULL;
	if(file != NULL)
	{
		szfile = (*env)->GetStringUTFChars(env, file, NULL);
	}
	time_t modtime = time/1000;
	if(szfile != NULL)
	{
		struct utimbuf utim;
		utim.actime = modtime;
		utim.modtime = modtime;
		return (utime(szfile, &utim) == 0);
	}
	return JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_cn_poco_utils_JniUtils_imgFilter(JNIEnv * env, jobject  obj, jobject img)
{
	char* szfile = NULL;
	if(img != NULL)
	{
		szfile = (*env)->GetStringUTFChars(env, img, NULL);
	}
	jboolean filtered = JNI_FALSE;
	if(szfile != NULL)
	{
		char* name = strrchr(szfile, '/');
		if(name != NULL)
		{
			name++;
		}
		char* dot = strchr(szfile, '.');
		if(dot != NULL)
		{
			dot--;
		}
		int len = strlen(szfile);
		if(strstr(szfile, "/cache/") != NULL
				|| strstr(szfile, "/temp/") != NULL
				|| strstr(szfile, "/tmp/") != NULL
				|| strstr(szfile, "/asset/") != NULL
				|| (name != NULL && name[0] == '~')
				|| (dot != NULL && dot[0] == '/')
				)
		{
			filtered = JNI_TRUE;
		}
		(*env)->ReleaseStringUTFChars(env, img, szfile);
	}
	return filtered;
}

JNIEXPORT jboolean JNICALL Java_cn_poco_utils_JniUtils_isFileExist(JNIEnv * env, jobject  obj, jobject file)
{
	char* szfile = NULL;
	if(file != NULL)
	{
		szfile = (*env)->GetStringUTFChars(env, file, NULL);
		if(szfile != NULL)
		{
			int res = access(szfile, R_OK);
			(*env)->ReleaseStringUTFChars(env, file, szfile);
			return res==0?JNI_TRUE:JNI_FALSE;
		}
	}
}

JNIEXPORT jboolean JNICALL Java_cn_poco_utils_JniUtils_getMaskedBitmap(JNIEnv * env, jobject  obj, jobject bitmap, jobject mask)
{
	AndroidBitmapInfo  info;
    unsigned char*     pixels;
    AndroidBitmapInfo  maskInfo;
    unsigned char*     maskPixels;
    int                ret;
    
	if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_88888 !");
        return JNI_FALSE;
    }
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, (void*)&pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    if ((ret = AndroidBitmap_getInfo(env, mask, &maskInfo)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    if (maskInfo.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGB_88888 !");
        return JNI_FALSE;
    }
    if ((ret = AndroidBitmap_lockPixels(env, mask, (void*)&maskPixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return JNI_FALSE;
    }
    
    int* pixels32 = (int*)pixels;
    int w = info.width;
    int h = info.height;
	int x = 0;
	int y = 0;
	int maskW = maskInfo.width;
	int maskH = maskInfo.height;
	unsigned char* p;
	unsigned char mska;
	unsigned char srca;
	unsigned char c;
	for(y = 0; y < maskH; y++)
	{
		for(x = 0; x < maskW; x++)
		{
			mska = maskPixels[(maskW*y+x)*4+3];
			if(x < w && y < h && mska < 255)
			{
				p = &pixels[(w*y+x)*4+3];
				srca = *p;
				if(srca > mska)
				{
					if(mska == 0)
					{
						pixels32[w*y+x] = 0;
					}
					else
					{
						*p = mska;
						
						p = &pixels[(w*y+x)*4+2];
						c = *p;
						c = c*mska/srca;
						*p = c;
						
						p = &pixels[(w*y+x)*4+1];
						c = *p;
						c = c*mska/srca;
						*p = c;
						
						p = &pixels[(w*y+x)*4];
						c = *p;
						c = c*mska/srca;
						*p = c;
					}
				}
				//pixels[(w*y+x)*4+2] = 128;//maskPixels[(maskW*y+x)*4+2];
				//pixels[(w*y+x)*4+1] = 128;//maskPixels[(maskW*y+x)*4+1];
				//pixels[(w*y+x)*4+0] = 128;//maskPixels[(maskW*y+x)*4+0];
			}
		}
	}
    AndroidBitmap_unlockPixels(env, bitmap);
    AndroidBitmap_unlockPixels(env, mask);
    return JNI_TRUE;
}

unsigned long hwq_encrypt(unsigned char* pdata, unsigned long len, unsigned char* key, int keylen)
{
	if(pdata == NULL || keylen <= 0 || len <= 0)
		return 0;
	int i = 0;
	int l = 0;
	int r = 0;
	int k = 0;

	unsigned int* p = (unsigned int*)pdata;
	len = len/4;
	//换位取反
	for(i = 0; i < len; i++)
	{
		k = key[i%keylen];
		l = (k+i)%31+1;
		r = 32-l;
		p[i] = ~((p[i]<<l&(0xffffffff<<l))|(p[i]>>r&(0xffffffff>>r)));
	}
	return len;
}

unsigned long hwq_decrypt(unsigned char* pdata, unsigned long len, unsigned char* key, int keylen)
{
	if(pdata == NULL || keylen <= 0 || len <= 0)
		return 0;
	int i = 0;
	int l = 0;
	int r = 0;
	int k = 0;

	unsigned int* p = (unsigned int*)pdata;
	len = len/4;
	for(i = 0; i < len; i++)
	{
		k = key[i%keylen];
		r = (k+i)%31+1;
		l = 32-r;
		p[i] = ~((p[i]>>r&(0xffffffff>>r))|(p[i]<<l&(0xffffffff<<l)));
	}
	return len;
}

unsigned char* hwq_encrypt_bytes(unsigned char* data, long len, unsigned char* key, int keylen, long* lpout_length)
{
	unsigned char* pdata = data;
	long size = ((len-1)/4+1)*4;
	int i = 0;
	if(size != len)
	{
		unsigned char* bytes = (unsigned char*)malloc(size);
		memcpy(bytes, data, len);
		for(i = len; i < size; i++)
		{
			bytes[i] = 0;
		}
		pdata = bytes;
	}

	*lpout_length = size;

	hwq_encrypt(pdata, size, key, keylen);

	return pdata;
}

void hwq_decrypt_bytes(unsigned char* data, long len, unsigned char* key, int keylen)
{
	hwq_decrypt(data, len, key, keylen);
}

void tea_encrypt(unsigned long *v, unsigned long *k) {
    unsigned long y = v[0], z = v[1], sum = 0, i;         /* set up */
    unsigned long delta = 0x9e3779b9;                 /* a key schedule constant */
    unsigned long a = k[0], b = k[1], c = k[2], d = k[3];   /* cache key */
    for(i = 0; i < 32; i++) /* basic cycle start */
	 {
        sum += delta;
        y += ((z<<4) + a) ^ (z + sum) ^ ((z>>5) + b);
        z += ((y<<4) + c) ^ (y + sum) ^ ((y>>5) + d);/* end cycle */
    }
    v[0] = y;
    v[1] = z;
}

void tea_decrypt(unsigned long *v, unsigned long *k) {
   unsigned long y = v[0], z = v[1], sum = 0xC6EF3720, i; /* set up */
   unsigned long delta = 0x9e3779b9;                  /* a key schedule constant */
   unsigned long a = k[0], b = k[1], c = k[2], d = k[3];    /* cache key */
   for(i = 0; i < 32; i++) {                            /* basic cycle start */
       z -= ((y<<4) + c) ^ (y + sum) ^ ((y>>5) + d);
       y -= ((z<<4) + a) ^ (z + sum) ^ ((z>>5) + b);
       sum -= delta;                                /* end cycle */
   }
   v[0]=y;
   v[1]=z;
}

unsigned char* tea_encrypt_bytes(unsigned char* data, long len, unsigned char* key, long* lpout_length)
{
	unsigned long* pdata = (unsigned long*)data;
	long size = ((len-1)/8+1)*8;
	int i = 0;
	if(size != len)
	{
		unsigned char* bytes = (unsigned char*)malloc(size);
		memcpy(bytes, data, len);
		for(i = len; i < size; i++)
		{
			bytes[i] = 0;
		}
		pdata = (unsigned long*)bytes;
	}

	*lpout_length = size;

	size = size/4;
	unsigned long* pkey = (unsigned long*)key;
	for(i = 0; i < size; i+=2)
	{
		tea_encrypt(pdata+i, pkey);
	}
	return (unsigned char*)pdata;
}

void tea_decrypt_bytes(unsigned char* data, long len, unsigned char* key)
{
	unsigned long* pdata = (unsigned long*)data;
	long size = len/8*8;

	int i = 0;
	size = size/4;
	unsigned long* pkey = (unsigned long*)key;
	for(i = 0; i < size; i+=2)
	{
		tea_decrypt(pdata+i, pkey);
	}
}

JNIEXPORT jbyteArray JNICALL Java_cn_poco_utils_JniUtils_teaEncrypt(JNIEnv * env, jobject  obj, jbyteArray data, jbyteArray key)
{
	jbyteArray byteArray = NULL;
	int datalen = (*env)->GetArrayLength(env, data);
	jbyte *pdata = (*env)->GetByteArrayElements(env, data, 0);
	int keylen = (*env)->GetArrayLength(env, key);
	jbyte *pkey = (*env)->GetByteArrayElements(env, key, 0);
	if(pdata != NULL && pkey != NULL && keylen >= 16)
	{
		byteArray = data;
		long outlen = 0;
		jbyte *pbytes = tea_encrypt_bytes(pdata, datalen, pkey, &outlen);
		if(pbytes != pdata)
		{
			byteArray = (*env)->NewByteArray(env, outlen);
			(*env)->SetByteArrayRegion(env, byteArray, 0, outlen, pbytes);
			free(pbytes);
		}
	}
	(*env)->ReleaseByteArrayElements(env, data, pdata, 0);
	(*env)->ReleaseByteArrayElements(env, key, pkey, 0);
	return byteArray;
}

JNIEXPORT void JNICALL Java_cn_poco_utils_JniUtils_teaDecrypt(JNIEnv * env, jobject  obj, jbyteArray data, jbyteArray key)
{
	int datalen = (*env)->GetArrayLength(env, data);
	jbyte *pdata = (*env)->GetByteArrayElements(env, data, 0);
	int keylen = (*env)->GetArrayLength(env, key);
	jbyte *pkey = (*env)->GetByteArrayElements(env, key, 0);
	if(pdata != NULL && pkey != NULL && keylen >= 16)
	{
		tea_decrypt_bytes(pdata, datalen, pkey);
	}
	(*env)->ReleaseByteArrayElements(env, data, pdata, 0);
	(*env)->ReleaseByteArrayElements(env, key, pkey, 0);
}

JNIEXPORT jbyteArray JNICALL Java_cn_poco_utils_JniUtils_hwqEncrypt(JNIEnv * env, jobject  obj, jbyteArray data, jbyteArray key)
{
	jbyteArray byteArray = NULL;
	int datalen = (*env)->GetArrayLength(env, data);
	jbyte *pdata = (*env)->GetByteArrayElements(env, data, 0);
	int keylen = (*env)->GetArrayLength(env, key);
	jbyte *pkey = (*env)->GetByteArrayElements(env, key, 0);
	if(pdata != NULL && pkey != NULL)
	{
		byteArray = data;
		long outlen = 0;
		jbyte *pbytes = hwq_encrypt_bytes(pdata, datalen, pkey, keylen, &outlen);
		if(pbytes != pdata)
		{
			byteArray = (*env)->NewByteArray(env, outlen);
			(*env)->SetByteArrayRegion(env, byteArray, 0, outlen, pbytes);
			free(pbytes);
		}
	}
	(*env)->ReleaseByteArrayElements(env, data, pdata, 0);
	(*env)->ReleaseByteArrayElements(env, key, pkey, 0);
	return byteArray;
}

JNIEXPORT void JNICALL Java_cn_poco_utils_JniUtils_hwqDecrypt(JNIEnv * env, jobject  obj, jbyteArray data, jbyteArray key)
{
	int datalen = (*env)->GetArrayLength(env, data);
	jbyte *pdata = (*env)->GetByteArrayElements(env, data, 0);
	int keylen = (*env)->GetArrayLength(env, key);
	jbyte *pkey = (*env)->GetByteArrayElements(env, key, 0);
	if(pdata != NULL && pkey != NULL)
	{
		hwq_decrypt_bytes(pdata, datalen, pkey, keylen);
	}
	(*env)->ReleaseByteArrayElements(env, data, pdata, 0);
	(*env)->ReleaseByteArrayElements(env, key, pkey, 0);
}

unsigned char R_V_table[256][256];
unsigned char B_U_table[256][256];
unsigned char G_U_V_table[256][256];

void yuv2rgb(JNIEnv *env, jclass jc, jint w, jint h, jint sz, jbyteArray yuv, jintArray rgb)
{
	jbyte *im_yuv;
	im_yuv = (*env)->GetByteArrayElements(env, yuv, 0);
	jint *im_rgb;
	im_rgb = (*env)->GetIntArrayElements(env, rgb, 0);

		int i = 0,y1,y2;
		int uvp,u = 0,v = 0;
		int r, g,b;	
		int r2, g2,b2;	
		int j,yp;
		int off_set;
		int h_1 = h - 1;
		int gray1, gray2;
		for(j=0,yp=0;j<h;j++)
			{		
			    off_set = (h_1 - j);
				uvp = sz + (j>>1)*w;
				for(i=0;i<w;i+=2,yp+=2)
				{
					y1=(0xff & ((short)im_yuv[yp]));
					y2=(0xff & ((short)im_yuv[yp+1]));
					if(y1<0)y1=0;
					if(y2<0)y2=0;
					
					v = (0xff & im_yuv[uvp++]);
					u = (0xff & im_yuv[uvp++]);				
					
					r = R_V_table[y1][v];
					b =  B_U_table[y1][u];	

					g = (y1 - G_U_V_table[u][v]);	
					g<0?g=0:g>255?g = 255:0;	
			
					
					r2 = R_V_table[y2][v];
					b2 = (short) B_U_table[y2][u];	

					g2 = (y2 - G_U_V_table[u][v]);		
					g2<0?g2=0:g2>255?g2 = 255:0;	 	 
							
					gray1 = r<<16 | g<<8 | b | 0xff000000;		
					gray2 = r2<<16 | g2<<8 | b2 | 0xff000000;				
																
					im_rgb[off_set] =gray1;						
					off_set+=h;				
					im_rgb[off_set] =gray2;					
					off_set+=h;										
				}
			}	
			
	(*env)->ReleaseByteArrayElements(env, yuv, im_yuv, 0);
	(*env)->ReleaseIntArrayElements(env, rgb, im_rgb, 0);
}

void init_table() 
{
	unsigned short i,j;
	for(j=0;j<256;j++)
	{
			for(i=0;i<256;i++)
			{				
				int dx = (int)(1000*j +1402 *(i-128))/1000;					
				if(dx<0)dx=0;else if(dx>255)	dx = 255;
				R_V_table[j][i] = dx;					
			}			
	}
	
	for(j=0;j<256;j++)
	{
			for(i=0;i<256;i++)
			{								
				int dx =  (int)(1000*j + 1772 *(i-128))/1000;						
				if(dx<0)dx=0;else if(dx>255)dx = 255;					
				B_U_table[j][i] = dx;					
			}
	}
	for(j=0;j<256;j++)
	{
			for(i=0;i<256;i++)
			{				
				int dx =  (int)( 344 *(j-128) + 714 *(i-128))/1000;					
				if(dx<0)dx=0;else if(dx>255)dx = 255;
				G_U_V_table[j][i] = dx;			
			}
	}	
}

int g_table_initialized = 0;
JNIEXPORT void JNICALL Java_cn_poco_utils_JniUtils_yuv2rgb(JNIEnv *env, jclass jc, jint w,jint h,jint sz, jbyteArray yuv, jintArray rgb) 
{
	if(g_table_initialized == 0)
	{
		g_table_initialized = 1;
		init_table();
	}
	yuv2rgb(env, jc, w,h,sz, yuv, rgb);			
}
