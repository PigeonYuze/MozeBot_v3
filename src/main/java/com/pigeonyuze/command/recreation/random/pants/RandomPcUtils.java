package com.pigeonyuze.command.recreation.random.pants;

import java.util.ArrayList;

public class RandomPcUtils {
    public static final int RET_EPIC = 0;

    public static final int RET_RARE = 1;

    public static final int RET_COMMON = 2;

    public static final String COLOR = "color";

    public static final String PATTERN = "pattern";

    public static final String MATERIAL = "material";

    public static final String ORNAMENT = "ornament";

    public static final ArrayList<PantsProperty> colorList = new ArrayList<>();

    public static final ArrayList<PantsProperty> patternList = new ArrayList<>();

    public static final ArrayList<PantsProperty> baseMaterialList = new ArrayList<>();

    public static final ArrayList<PantsProperty> ornamentList = new ArrayList<>();

    public static final ArrayList<String> makeTime = new ArrayList<>() {{
        add("去年");
        add("前年");
        add("三年前");
        add("十年前");
        add("半个世纪前");
        add("清末");
        add("大唐");
        add("夏初");
        add("1919180年");
        add("明治十五年");
        add("2077年");
        add("公元元年");
        add("2042年");
        add("1453年5月29日");
        add("1437年");
    }};

    public static final ArrayList<String> valueList = new ArrayList<String>() {{
        add("二十周年限定");
        add("金色传说");
        add("纯粹的/Pure");
        add("114514元");
        add("一文不值");
        add("不菲");
        add("十万日元");
        add("50日元");
        add("9磅15便士");
        add("有胖次纪念");
        add("8848元");
        add("6便士");
    }};

    public static final ArrayList<String> statusList = new ArrayList<String>() {{
        add("破旧不堪/战损");
        add("崭新出厂");
        add("久经沙场");
        add("半条");
        add("焦炭");
        add("脱出不能");
        add("潮湿");
        add("丢失/Lost/Miss");
        add("加速同调");
        add("叠放");
        add("全抗");
        add("战破抗性/效破抗性");
        add("理论值 ");
        add("黑化中 ");
        add("活化");
        add("浴霸不能");
        add("光敏性癫痫患者退避");
        add("超量叠放");
        add("融合中 ");
        add("新年穿上崭新pc般的喜悦");
        add("沾上奇怪液体");
        add("掉san中");
        add("主人的 ");
        add("大成功才能穿");
        add("上升气流");

    }};

    private static final PantsProperty epicColor = new PantsProperty(RET_EPIC, COLOR);
    private static final ArrayList<String> epicColorList = new ArrayList<String>(){{
        add("智乃蓝");
        add("R!G!B!");
        add("八云紫absubcneksjnqban");
    }};
    
    private static final PantsProperty rareColor = new PantsProperty(RET_RARE, COLOR);
    private static final ArrayList<String> rareColorList = new ArrayList<String>(){{
        add("多娜多娜配色");
        add("早苗绿");
        add("心爱橙");
        add("笨蛋蓝");
        add("橙色");
    }};
    
    private static final PantsProperty commonColor = new PantsProperty(RET_COMMON, COLOR);
    private static final ArrayList<String> commonColorList = new ArrayList<String>(){{
        add("蓝白");
        add("棕色");
        add("浅褐");
        add("水蓝");
        add("莓红");
        add("红色");
        add("橘黄");
        add("雪白");
        add("米黄");
        add("迷彩");
        add("浅蓝");
        add("柠檬绿");
        add("灰色" );
        add("金属色");
        add("玫瑰金");
        add("远峰蓝");
        add("豆蔻绿");
        add("原谅绿");
        add("彩虹" );
        add("基佬紫");
        add("金色" );
        add("本子黄");
        add("灰暗" );
        add("防撞桶配色");
        add("黑白灰");
    }};
    
    private static final PantsProperty epicPattern = new PantsProperty(RET_EPIC, PATTERN);
    private static final ArrayList<String> epicPatternList = new ArrayList<String>(){{
        add("创可贴");
        add("克苏鲁风格");
    }};

    private static final PantsProperty rarePattern = new PantsProperty(RET_RARE, PATTERN);
    private static final ArrayList<String> rarePatternList = new ArrayList<String>(){{
        add("镭射");
        add("痛苦之梨");
    }};
    
    private static final PantsProperty commonPattern = new PantsProperty(RET_COMMON, PATTERN);
    private static final ArrayList<String> commonPatternList = new ArrayList<String>(){{
        add("平角/三角");
        add("蕾丝");
        add("拉链");
        add("口罩");
        add("纸质");
        add("细绳");
        add("3D打印");
        add("二维");
        add("机械化");
        add("黄金比例");
        add("透明");
        add("过分正常");
        add("bra");
        add("贞操带");
        add("片状");
        add("双层");
        add("可塑");
        add("兜裆布");
        add("头盔模式");
        add("流水线产品");
        add("全年龄向");
    }};

    private static final PantsProperty epicBaseMaterial = new PantsProperty(RET_EPIC, MATERIAL);
    private static final ArrayList<String> epicBaseMaterialList = new ArrayList<String>(){{
        add("光");
        add("泰伦虫族");
    }};
    
    private static final PantsProperty rareBaseMaterial = new PantsProperty(RET_RARE, MATERIAL);
    private static final ArrayList<String> rareBaseMaterialList = new ArrayList<String>(){{
        add("纳米机器人");
        add("能量护盾（提供给战士们）");
        add("纳垢大魔的瘟疫");
        add("色孽大魔的气息");
        add("奸奇大魔的幻象");
        add("恐虐大魔的残暴");
        add("灵能");
        add("修伦虫族");
        add("活体金属");
        add("易爆微粒");
        add("异星天然气");
    }};
    
    private static final PantsProperty commonBaseMaterial = new PantsProperty(RET_COMMON, MATERIAL);
    private static final ArrayList<String> commonBaseMaterialList = new ArrayList<String>(){{
        add("纯棉");
        add("奥利哈钢");
        add("纱布");
        add("软玻璃");
        add("镶钻");
        add("纤维");
        add("皮革");
        add("树叶");
        add("金属");
        add("工程塑料");
        add("pvc");
        add("地嗪");
        add("太阳花");
        add("振金");
        add("陶瓷");
        add("垂杨柳");
        add("琴弦");
        add("液晶");
        add("水晶塔");
        add("触手");
        add("五三精装版");  
    }};

    private static final PantsProperty epicOrnament = new PantsProperty(RET_EPIC, ORNAMENT);
    private static final ArrayList<String> epicOrnamentList = new ArrayList<String>(){{
        add("我超！初音未来！");
    }};

    private static final PantsProperty rareOrnament = new PantsProperty(RET_RARE, ORNAMENT);
    private static final ArrayList<String> rareOrnamentList = new ArrayList<String>(){{
        add("团队之光");
        add("暗牧");
        add("圣光");
        add("马赛克");
        add("模糊");
        add("RGB呼吸灯");
    }};
    
    private static final PantsProperty commonOrnament = new PantsProperty(RET_COMMON, ORNAMENT);
    private static final ArrayList<String> commonOrnamentList = new ArrayList<String>(){{
        add("闪光");
        add("荧光");
        add("包浆");
        add("震动");
        add("二手");
        add("三手");
        add("n手");
        add("紧缚");
        add("蠕动");
        add("膨胀");
        add("猪突猛进");
        add("图案印花");
        add("通电");
        add("钻石");
        add("刺钉");
        add("ゴゴゴゴゴゴ");
        add("正正正气凛然");
        add("背光");
        add("绝对领域");
        add("已腐蚀");
        add("数据化");
        add("抽象派");
        add("立体环绕");
    }};
    static {
        epicColor.setEntry(epicColorList);
        rareColor.setEntry(rareColorList);
        commonColor.setEntry(commonColorList);
        epicPattern.setEntry(epicPatternList);
        rarePattern.setEntry(rarePatternList);
        commonPattern.setEntry(commonPatternList);
        epicBaseMaterial.setEntry(epicBaseMaterialList);
        rareBaseMaterial.setEntry(rareBaseMaterialList);
        commonBaseMaterial.setEntry(commonBaseMaterialList);
        epicOrnament.setEntry(epicOrnamentList);
        rareOrnament.setEntry(rareOrnamentList);
        commonOrnament.setEntry(commonOrnamentList);

        colorList.add(epicColor);
        colorList.add(rareColor);
        colorList.add(commonColor);

        patternList.add(epicPattern);
        patternList.add(rarePattern);
        patternList.add(commonPattern);

        baseMaterialList.add(epicBaseMaterial);
        baseMaterialList.add(rareBaseMaterial);
        baseMaterialList.add(commonBaseMaterial);

        ornamentList.add(epicOrnament);
        ornamentList.add(rareOrnament);
        ornamentList.add(commonOrnament);
    }
}