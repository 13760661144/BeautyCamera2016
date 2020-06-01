#pragma version(1)
#pragma rs java_package_name(cn.poco.image)

rs_allocation TableAllocation;
int BlueTab[1024];
float scaleTab[256];

uchar4 __attribute__((kernel)) root(uchar4 in, int x, int y)
{
    uchar4 cur= in;
    uchar4 color, color1;

    float blueColor = scaleTab[cur.b];

    int q1_x = BlueTab[4*cur.b];
    int q1_y = BlueTab[4*cur.b+1];
    int q2_x = BlueTab[4*cur.b+2];
    int q2_y = BlueTab[4*cur.b+3];

    float redColor = scaleTab[cur.r];
    float greenColor = scaleTab[cur.g];

    float scaleR = redColor - floor(redColor);
    float scaleG = greenColor - floor(greenColor);

    int left = (int)((q1_x << 4) + floor(redColor));
    int right = (int)((q1_x << 4) + ceil(redColor));
    int top = (int)((q1_y << 4) + floor(greenColor));
    int bottom = (int)((q1_y << 4) + ceil(greenColor));

    uchar4 pt = rsGetElementAt_uchar4(TableAllocation, left, top);
    uchar4 pt1 = rsGetElementAt_uchar4(TableAllocation, right, top);
    uchar4 pt2 = rsGetElementAt_uchar4(TableAllocation, left, bottom);
    uchar4 pt3 = rsGetElementAt_uchar4(TableAllocation, right, bottom);

    float3 pt_ = {pt.r, pt.g, pt.b};
    float3 pt1_ = {pt1.r, pt1.g, pt1.b};
    float3 pt2_ = {pt2.r, pt2.g, pt2.b};
    float3 pt3_ = {pt3.r, pt3.g, pt3.b};
    float3 scaleR_ = {scaleR, scaleR, scaleR};
    float3 scaleG_ = {scaleG, scaleG, scaleG};

    float3 m0_ = pt_ + scaleR_ * (pt1_ - pt_);
    float3 m1_ = pt2_ + scaleR_ * (pt3_ - pt2_);
    float3 color_ = m0_ + scaleG_ * (m1_ - m0_);
    color.r = (uchar)color_.r;
    color.g =(uchar)color_.g;
    color.b = (uchar)color_.b;

    left = (int)((q2_x << 4) + floor(redColor));
    right = (int)((q2_x << 4) + ceil(redColor));
    top = (int)((q2_y << 4) + floor(greenColor));
    bottom = (int)((q2_y << 4) + ceil(greenColor));

    pt = rsGetElementAt_uchar4(TableAllocation, left, top);
    pt1 = rsGetElementAt_uchar4(TableAllocation, right, top);
    pt2 = rsGetElementAt_uchar4(TableAllocation, left, bottom);
    pt3 = rsGetElementAt_uchar4(TableAllocation, right, bottom);

    float3 pt_1 = {pt.r, pt.g, pt.b};
    float3 pt1_1 = {pt1.r, pt1.g, pt1.b};
    float3 pt2_1 = {pt2.r, pt2.g, pt2.b};
    float3 pt3_1 = {pt3.r, pt3.g, pt3.b};
    float3 scaleR_1 = {scaleR, scaleR, scaleR};
    float3 scaleG_1 = {scaleG, scaleG, scaleG};

    float3 m0_1 = pt_1 + scaleR_1 * (pt1_1 - pt_1);
    float3 m1_1 = pt2_1 + scaleR_1 * (pt3_1 - pt2_1);
    float3 color_1 = m0_1 + scaleG_1 * (m1_1 - m0_1);
    color1.r = (uchar)color_1.r;
    color1.g =(uchar)color_1.g;
    color1.b = (uchar)color_1.b;

    float scale = blueColor - floor(blueColor);
    cur.r = (uchar)((color.r) * (1 - scale) + (color1.r) * scale);
    cur.g = (uchar)((color.g) * (1 - scale) + (color1.g) * scale);
    cur.b = (uchar)((color.b) * (1 - scale) + (color1.b) * scale);

    return cur;
}

void init() {
    //init the array with zeros
    for (int i = 0; i < 256; i++) {
		BlueTab[4*i] = 0;
		BlueTab[4*i+1] = 0;
		BlueTab[4*i+2] = 0;
		BlueTab[4*i+3] = 0;

		scaleTab[i] = 0.f;
    }
}

void createRemapArray() {
    for (int i = 0; i < 256; i++) {
        float blueColor = i / 255.0f * 15.0f;

        int q1_y = (int)(floor(floor(blueColor) / 4.f));
        int q1_x = (int)(floor(blueColor) - (q1_y * 4.f));

        int q2_y = (int)(floor(ceil(blueColor) / 4.f));
        int q2_x = (int)(ceil(blueColor) - (q2_y * 4.f));

        BlueTab[4*i] = q1_x;
        BlueTab[4*i+1] = q1_y;
        BlueTab[4*i+2] = q2_x;
        BlueTab[4*i+3] = q2_y;

        scaleTab[i] = blueColor;
    }
}