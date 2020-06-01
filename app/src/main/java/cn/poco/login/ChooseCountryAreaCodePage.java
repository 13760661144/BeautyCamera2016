package cn.poco.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.poco.advanced.ImageUtils;
import cn.poco.framework.BaseSite;
import cn.poco.framework.IPage;
import cn.poco.login.SideBar.OnTouchingLetterChangedListener;
import cn.poco.login.site.ChooseCountryAreaCodePageSite;
import cn.poco.statistics.TongJiUtils;
import cn.poco.tianutils.ShareData;
import cn.poco.utils.OnAnimationClickListener;
import my.beautyCamera.R;

/**
 * 选择国家地区码页面
 */
public class ChooseCountryAreaCodePage extends IPage {
    //private String TAG = "选择国家";
    private Context mContext;
    private Bitmap mBgBmp;
    private RelativeLayout m_MainLayout;

    private OnCountryAreaCodeListener mCountryAreaCodeListener;//获取国际区域码监听器

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<SortModel> SourceDateList;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    /***
     * ---布局---
     *****/
    private RelativeLayout mrLayoutTitle;
    private ImageView mBack;
    private TextView mTitle;

    private FrameLayout mfLayoutBrowse;
    private ListView mSortListView;
    private SideBar mSideBar;
    private SortAdapter mSortAdapter;


    private ChooseCountryAreaCodePageSite mSite;

    public ChooseCountryAreaCodePage(Context context, BaseSite site)
    {
        super(context,site);
        mContext = context;
        mSite = (ChooseCountryAreaCodePageSite) site;
        initUI();
        initDatas();

        TongJiUtils.onPageStart(getContext(), R.string.选择地区);
    }

    /**
     *
     * @param params
     */

    @Override
    public void SetData(HashMap<String, Object> params) {

    }



    public void initUI() {
        RelativeLayout.LayoutParams params;
        RelativeLayout.LayoutParams rlParams;
        m_MainLayout = new RelativeLayout(getContext());
        LayoutParams fl = new LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        m_MainLayout.setLayoutParams(fl);
        this.addView(m_MainLayout);

        //设置背景
        if (mBgBmp != null) {
            m_MainLayout.setBackgroundDrawable(new BitmapDrawable(mBgBmp));
        } else {
            m_MainLayout.setBackgroundColor(0xfff6f6f5);
        }

        //实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();

        //标题
        rlParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ShareData.PxToDpi_xhdpi(90));
        rlParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        mrLayoutTitle = new RelativeLayout(mContext);
        m_MainLayout.addView(mrLayoutTitle, rlParams);
        mrLayoutTitle.setId(R.id.login_choosecountrypage_mrlayouttitle);
        {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
//            params.leftMargin = ShareData.PxToDpi_xhdpi(16);
//            params.topMargin = ShareData.PxToDpi_xhdpi(24);
            mBack = new ImageView(mContext);
            mBack.setImageResource(R.drawable.framework_back_btn);
            mBack.setOnTouchListener(m_OnClickListenr);
            ImageUtils.AddSkin(getContext(),mBack);
            mrLayoutTitle.addView(mBack, params);

            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            mTitle = new TextView(mContext);
            mrLayoutTitle.addView(mTitle, params);
            mTitle.setText(getContext().getResources().getString(R.string.choosecountrypage_title));
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
            mTitle.setTextColor(0xff8b8b8b);
        }

        //国家地区浏览
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.BELOW, R.id.login_choosecountrypage_mrlayouttitle);
        mfLayoutBrowse = new FrameLayout(mContext);
        m_MainLayout.addView(mfLayoutBrowse, params);
        {
            LayoutParams flParams;
            flParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            flParams.gravity = Gravity.CENTER;
            mSortListView = new ListView(mContext);
            mfLayoutBrowse.addView(mSortListView, flParams);
            mSortListView.setDivider(null);
//            mSortListView.setSelector(R.drawable.lv_area_code_selector);
            mSortListView.setSelector(new ColorDrawable(0x00000000));
            mSortListView.setVerticalFadingEdgeEnabled(false);
            mSortListView.setOnItemClickListener(mOnItemClickListener);

            flParams = new LayoutParams(ShareData.PxToDpi_xhdpi(60), (int) (ShareData.m_screenHeight*0.5f));
            flParams.gravity = Gravity.END | Gravity.CENTER;
            flParams.setMargins(0,ShareData.PxToDpi_xhdpi(5),0,ShareData.PxToDpi_xhdpi(18));
            mSideBar = new SideBar(mContext);
            mfLayoutBrowse.addView(mSideBar, flParams);
            mSideBar.setOnTouchingLetterChangedListener(mOnTouchingLetterChangedListener);
        }
    }

    private void initDatas() {
        String allcountryString = getContext().getResources().getString(R.string.choosecountrypage_country);
        String[] countrys = allcountryString.split(",");
//        String[] countrys = {"AFGHANISTAN_阿富汗_93", "ALBANIA_阿尔巴尼亚_355", "ALGERIA_阿尔及利亚_213", "ANDORRA_安道尔_376", "ANGOLA_安哥拉_244", "ANGULLIAIS_安圭拉岛_1264", "ANTIGUAANDBARBUDA_安提瓜岛和巴布达_1268", "ARGENTINA_阿根廷_54",
//                "ARMENIA_亚美尼亚_374", "ASCENSION_阿森松_247", "AUSTRALIA_澳大利亚_61", "AUSTRIA_奥地利_43", "AZERBAIJAN_阿塞拜疆_994", "BAHAMAS_巴哈马群岛_1242", "BAHRANI_巴林_973", "BANGLADESH_孟加拉国_880", "BARBADOS_巴巴多斯_1246",
//                "BELARUS_白俄罗斯_375", "BELGIUM_比利时_32", "BELIZE_伯利兹_501", "BENIN_贝宁_229", "BERMUDAIS_百慕大群岛_1441", "BHUTAN_不丹_975", "BOLIVIA_玻利维亚_591", "BOSNIA&HERZEGOVINA_波黑_387", "BOTSWANA_博茨瓦纳_267", "BRAZIL_巴西_55",
//                "BRUNEI_文莱_673", "BULGARIA_保加利亚_359", "BURKINAFASO_布基纳法索_226", "BURUNDI_布隆迪_257", "CAMBODIA_柬埔寨_855", "CAMEROON_喀麦隆_237", "CANADA_加拿大_1", "CAPEVERDE_佛得角_238", "CAYMANIS_开曼群岛_1345",
//                "CENTRALAFRICA_非洲中部_236", "CHAD_乍得_235", "CHILE_智利_56", "CHINA_中国_86", "COLOMBIA_哥伦比亚_57", "COMORO_科摩罗_269", "CONGO_刚果_242", "Congo(DemRep)_刚果(金)_243", "COOKIS_库克群岛_682", "COSTARICA_哥斯达黎加_506",
//                "CROATIA_克罗地亚_385", "CUBA_古巴_53", "CYPRUS_塞浦路斯_357", "CZECH_捷克_420", "DENMARK_丹麦_45", "DIEGOGARCIA_迪戈加西亚岛_246", "DJIBOUTI_吉布提_253", "DOMINICA(COMMOMWEALTHOF)_多米尼克_1767", "DOMINICANREP_多米尼加_1809",
//                "ECUADOR_厄瓜多尔_593", "EGYPT_埃及_20", "ELSALVADOR_萨尔瓦多_503", "EQUATORIALGUINEA_赤道几内亚_240", "ERITREA_厄立特里亚_291", "ESTIONIA_爱沙尼亚_372", "ETHIOPIA_埃塞俄比亚_251", "FALKLANDIS_福克兰群岛_500", "FAROEIS_法罗群岛_298",
//                "FIJI_斐济_679", "FINLAND_芬兰_358", "FRANCE_法国_33", "FRENCHGUIANA_法属圭亚那_594", "FRENCHPOLYNESIA_法属波利尼西亚_689", "GABON_加蓬_241", "GAMBIA_冈比亚_220", "GEORGIA_乔治亚州_995", "GERMANY_德国_49", "GHANA_加纳_233",
//                "GIBRALTAR_直布罗陀_350", "GREECE_希腊_30", "GREENLANDIS_格陵兰岛_299", "GRENADA_格林纳达_1473", "GUADELOUPEIS_瓜德罗普岛_590", "GUAM_关岛_1671", "GUATEMAILA_危地马拉_502", "GUINEA_几内亚_224", "GUINEABISSAU_几内亚比绍_245",
//                "GUYANY_圭亚那_592", "HAITI_海地_509", "HONDURAS_洪都拉斯_504", "HONGKONG_香港_852", "HUNGARY_匈牙利_36", "ICELAND_冰岛_354", "INDIA_印度_91", "INDONESIA_印尼_62", "IRAN_伊朗_98", "IRAQ_伊拉克_964", "IRELAND_爱尔兰_353", "ISRAFI_以色列_972",
//                "ITALY_意大利_39", "IVORYCOAST_科特迪瓦_225", "JAMAICA_牙买加_1876", "JAPAN_日本_81", "JORDAN_约旦_962", "KENYA_肯尼亚_254", "KIRIBATI_基里巴斯_686", "KOREA_韩国_82", "KOREA（DPROF）_朝鲜_850", "KUWAIT_科威特_965",
//                "KYRGYZSTAN_吉尔吉斯斯坦_996", "LAOS_老挝_856", "LATVIA_拉脱维亚_371", "LEBANON_黎巴嫩_961", "LESOTHO_莱索托_266", "LIBERIA_利比里亚_231", "LIBYA_利比亚_218", "LIECHTENSTEIN_列支敦斯登_4175", "LITHUANIA_立陶宛_370",
//                "LUXEMBOURG_卢森堡_352", "MACAU_澳门_853", "MACEDONIJA_马其顿_389", "MADAGASCAR_马达加斯加_261", "MALAWI_马拉维_265", "MALAYSIA_马来西亚_60", "MALDIVE_马尔代夫_960", "MALI_马里_223", "MALTA_马耳他_356", "MARIANAIS_马里亚纳群岛_1670",
//                "MARSHALLIS_马歇尔群岛_692", "MARTINIQUE_马提尼克岛_596", "MAURITANIA_毛利塔尼亚_222", "MAURITIUS_毛里求斯_230", "MEXICO_墨西哥_52", "MICRONESIA_密克罗尼西亚_691", "MILDOVA_摩尔多瓦_373", "MONACO_摩纳哥_377", "MONGOLIA_蒙古_976",
//                "MONTSERRATIS_蒙特塞拉特岛_1664", "MOROCCO_摩洛哥_212", "MOZAMBIQUE_莫桑比克_258", "MYANMAR_缅甸_95", "NAMIBIA_纳米比亚_264", "NAURU_瑙鲁_674", "NEPAL_尼泊尔_977", "NETHERLANDS_荷兰_31", "NEWZEALAND_新西兰_64", "NICARAGUA_尼加拉瓜_505",
//                "NIGER_尼日尔_227", "NIGERIA_尼日利亚_234", "NIUEIS_纽埃岛_683", "NORFOLKIS_诺福克岛_672", "NORWAY_挪威_47", "OMAN_阿曼_968", "PAKISTAN_巴基斯坦_92", "PALAU_帕劳_680", "PANAMA_巴拿马_507", "PAPUANEWGUINEA_巴布亚新几内亚_675",
//                "PARAGUAY_巴拉圭_595", "PERU_秘鲁_51", "PHILIPPINES_菲律宾_63", "POLAND_波兰_48", "PORTUGAL_葡萄牙_351", "PUERTORICO_波多黎各_1787", "QATAR_卡塔尔_974", "REUNIONIS_留尼旺岛_262", "RUMANIA_罗马尼亚_40", "RUSSIA_俄罗斯_7",
//                "RWANDA_卢旺达_250", "SAMOA(EASTERN)_萨摩亚(东部)_684", "SAMOA(WESTERN)_萨摩亚(西部)_685", "SANMARINO_圣马力诺_378", "SAOTOME&PRINCIPE_圣多美和普林西比_239", "SAUDIARABIA_沙特阿拉伯_966", "SENEGAL_塞内加尔_221",
//                "SEYCHELLIES_塞舌尔共和国_248", "SIERRALEONE_塞拉利昂_232", "SINGAPORE_新加坡_65", "SLOVAK_斯洛伐克_421", "SLOVENIA_斯洛文尼亚_386", "SOLOMONIS_所罗门群岛_677", "SOMAIL_索马里_252", "SOUTHAFRICA_南非_27", "SPAIN_西班牙_34",
//                "SRILANKA_斯里兰卡_94", "ST.CHRISTOPHER&NEVISIS_圣基茨和尼维斯_1869", "ST.HELENA_圣赫勒拿_290", "ST.LUCIA_圣卢西亚_1784", "ST.PIERRE&MIQUELON_圣皮埃尔和密克隆群岛_508", "SUDAN_苏丹_249", "SURINAME_苏里南_597", "SWAZILAND_斯威士兰_268",
//                "SWEDEN_瑞典_46", "SWITZERLAND_瑞士_41", "SYRIA_叙利亚_963", "TAIWAN_台湾_886", "TANZANIA_坦桑尼亚_255", "THAILAND_泰国_66", "TOGO_多哥_228", "TOKELAUIS_托克劳岛_690", "TONGA_汤加_676", "TRINIDAD&TOBAGO_特立尼达和多巴哥_1868",
//                "TUISIA_突尼斯_216", "TURKEY_土耳其_90", "TURKMENISTAN_土库曼斯坦_993", "TURKS&CAICOSIS_特克斯和凯科斯群岛_1649", "TUVALU_图瓦卢_688", "U.K._英国_44", "UGANDA_乌干达_256", "UKRAINE_乌克兰_380", "UNITEDARABEMIRATES_阿联酋_971",
//                "URUGUAY_乌拉圭_598", "U.S.A_美国_1", "UZBEKISTAN_乌兹别克斯坦_998", "VANUATU_瓦努阿图_678", "VENEZUELA_委内瑞拉_58", "VIETNAM_越南_84", "VIRGINIS.(BRITISH)_维珍群岛（英属）_1284", "VIRGINIS.(U.S.A)_维珍群岛(美属)_1340",
//                "WAKEIS_威克岛_1808", "WALLSANDFUTUNA_墙壁瓦利斯群岛和富图纳群岛_1681", "YEMEN_也门_967", "YUGOSLAVIA_南斯拉夫_381", "ZAMBIA_赞比亚_260", "ZANZIBAR_桑给巴尔岛_259", "ZIMBABWE_津巴布韦_263"};

        SourceDateList = filledData(countrys);
        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);

        //添加四个常用地区
        SortModel sortModel;
        sortModel = new SortModel();
        sortModel.setEnName("TAIWAN");
        sortModel.setName(getContext().getResources().getString(R.string.choosecountrypage_taiwan));
        sortModel.setNum("886");
        sortModel.setSortLetters(getContext().getResources().getString(R.string.choosecountrypage_commonuse));
        SourceDateList.add(0, sortModel);

        sortModel = new SortModel();
        sortModel.setEnName("MACAU");
        sortModel.setName(getContext().getResources().getString(R.string.choosecountrypage_macau));
        sortModel.setNum("853");
        sortModel.setSortLetters(getContext().getResources().getString(R.string.choosecountrypage_commonuse));
        SourceDateList.add(0, sortModel);

        sortModel = new SortModel();
        sortModel.setEnName("HONGKONG");
        sortModel.setName(getContext().getResources().getString(R.string.choosecountrypage_hongkong));
        sortModel.setNum("852");
        sortModel.setSortLetters(getContext().getResources().getString(R.string.choosecountrypage_commonuse));
        SourceDateList.add(0, sortModel);


        sortModel = new SortModel();
        sortModel.setEnName("CHINA");
        sortModel.setName(getContext().getResources().getString(R.string.choosecountrypage_china));
        sortModel.setNum("86");
        sortModel.setSortLetters(getContext().getResources().getString(R.string.choosecountrypage_commonuse));
        SourceDateList.add(0, sortModel);

        mSortAdapter = new SortAdapter(mContext, SourceDateList);
        mSortListView.setAdapter(mSortAdapter);
    }

    /**
     * Listview 点击监听
     */
    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            view.setBackgroundColor(Color.BLACK);
//            mCountryAreaCodeListener.getCountryAreaCode(((SortModel) mSortAdapter.getItem(position)).getName(),
//                    ((SortModel) mSortAdapter.getItem(position)).getAreaCode());//获取国际码
            release();
//            PocoCamera.main.onBackPressed();
            if(mSite != null)
            {
                HashMap<String ,Object> params = new HashMap<>();
                LoginPageInfo info = new LoginPageInfo();
                info.m_country = ((SortModel) mSortAdapter.getItem(position)).getName();
                info.m_areaCodeNum = ((SortModel) mSortAdapter.getItem(position)).getAreaCode();
                params.put("info",info);
                mSite.onSel(params,getContext());
            }
        }
    };

    /**
     * 设置右侧触摸监听
     */
    private OnTouchingLetterChangedListener mOnTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
        @Override
        public void onTouchingLetterChanged(String s) {
            //常用
            if(s.charAt(0)=='★'){
                mSortListView.setSelection(0);
                return;
            }

            //该字母首次出现的位置
            int position = mSortAdapter.getPositionForSection(s.charAt(0));
            if(position != -1){
                mSortListView.setSelection(position);
            }
        }
    };

//    private OnClickListener mOnClickListener = new OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            if (v == mBack) {
//                release();
//                mSite.backToLastPage();
//
//            }
//        }
//    };

    OnAnimationClickListener m_OnClickListenr = new OnAnimationClickListener() {
        @Override
        public void onAnimationClick(View v) {
            if (v == mBack) {
                release();
                mSite.backToLastPage(getContext());
            }
        }

        @Override
        public void onTouch(View v) {

        }

        @Override
        public void onRelease(View v) {

        }
    };

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<SortModel> filledData(String[] date) {
        List<SortModel> mSortList = new ArrayList<SortModel>();

        String temp;
        String[] tempArr;
        for (int i = 0; i < date.length; i++) {
            temp = date[i];
            tempArr = temp.split("_");
            SortModel sortModel = new SortModel();
            sortModel.setEnName(tempArr[0]);
            sortModel.setName(tempArr[1]);
            sortModel.setNum(tempArr[2]);
            //汉字转换成拼音
            ArrayList<String> eachChineseSpell = characterParser.getSelling2(tempArr[1]);
            if (eachChineseSpell != null) {
                sortModel.setEachChineseSpell(eachChineseSpell);
                String sortString = eachChineseSpell.get(0).substring(0, 1).toUpperCase();
                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    sortModel.setSortLetters(sortString.toUpperCase());
                }
            }
            mSortList.add(sortModel);
        }
        return mSortList;
    }

    public void release() {
        if (mBgBmp != null && !mBgBmp.isRecycled()) {
            mBgBmp.isRecycled();
            mBgBmp = null;
        }
        this.setBackgroundDrawable(null);
        mContext = null;
        mCountryAreaCodeListener = null;
    }

    public interface OnCountryAreaCodeListener {
        public void getCountryAreaCode(String coutry, String areaCode);
    }

    public void setCountryAreaCodeListener(OnCountryAreaCodeListener countryAreaCodeListener) {
        mCountryAreaCodeListener = countryAreaCodeListener;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }


    @Override
    public void onBack() {
        mSite.onBack(getContext());
    }

    @Override
    public void onClose() {
        release();

        TongJiUtils.onPageEnd(getContext(), R.string.选择地区);
    }

    @Override
    public void onPause() {
        super.onPause();
        TongJiUtils.onPagePause(getContext(), R.string.选择地区);
    }

    @Override
    public void onResume() {
        super.onResume();
        TongJiUtils.onPageResume(getContext(), R.string.选择地区);
    }
}
