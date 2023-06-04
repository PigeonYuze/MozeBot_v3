package com.pigeonyuze.command.recreation.random.pants;

import com.pigeonyuze.account.UserManager;
import com.pigeonyuze.util.UnopenedRunningData;
import kotlin.Pair;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;


public class RandomPc {

    private static final int MIN_RANDOM = 0;

    private static final int MAX_RANDOM = 101;

    private static final int EPIC_HIT = 100;

    private static final int RARE_THRESHOLD = 10;

    private static final int RET_EPIC = 0;

    private static final int RET_RARE = 1;

    private static final int RET_COMMON = 2;

    private static final int SCORE_EPIC = 2;

    private static final int SCORE_RARE = 1;

    private static final int SCORE_COMMON = 0;

    private static final int COLOR_IDX = 0;

    private static final int PATTERN_IDX = 1;

    private static final int BASE_MATERIAL_IDX = 2;

    private static final int ORNAMENT_IDX = 3;

    private static final int MAKE_TIME_IDX = 4;

    private static final int VALUE_IDX = 5;

    private static final int STATUS_IDX = 6;

    private static int totalScore = 0;

    private static final ArrayList<ArrayList<PantsProperty>> RANDOM_LIST = new ArrayList<ArrayList<PantsProperty>>() {{
        add(RandomPcUtils.colorList);
        add(RandomPcUtils.patternList);
        add(RandomPcUtils.baseMaterialList);
        add(RandomPcUtils.ornamentList);
    }};

    private static final ArrayList<ArrayList<String>> NO_LEVEL_VALUE = new ArrayList<ArrayList<String>>() {{
        add(RandomPcUtils.makeTime);
        add(RandomPcUtils.valueList);
        add(RandomPcUtils.statusList);
    }};

    private static int getLevel() {
        int ret = RandomUtils.nextInt(MIN_RANDOM, MAX_RANDOM);
        if (ret == EPIC_HIT) {
            totalScore += SCORE_EPIC;
            return RET_EPIC;
        } else if (ret <= RARE_THRESHOLD) {
            totalScore += SCORE_RARE;
            return RET_RARE;
        } else {
            return RET_COMMON;
        }
    }

    private static int getRandomIdx(int listSize) {
        return RandomUtils.nextInt(MIN_RANDOM, listSize);
    }

    private static String getRandomEntry(ArrayList<String> arrayList) {
        int idx = getRandomIdx(arrayList.size());
        return arrayList.get(idx);
    }

    private static String[] getProperties() {
        String[] properties = new String[RANDOM_LIST.size() + NO_LEVEL_VALUE.size()];

        /* Iterate through all PantsProperty list */
        for (int i = 0; i < RANDOM_LIST.size(); i++) {
            int level = getLevel();
            ArrayList<PantsProperty> propertyList = RANDOM_LIST.get(i);
            /* Fetch the PantsProperty that matches with selected level */
            for (PantsProperty curProperty : propertyList) {
                if (curProperty.getLevel() != level) {
                    continue;
                }
                ArrayList<String> entry = curProperty.getEntry();
                String propertyEntry = getRandomEntry(entry);
                properties[i] = propertyEntry;
                break;
            }
        }
        /* Iterate through all No-level PantsProperty list */
        for (int i = 0; i < NO_LEVEL_VALUE.size(); i++) {
            ArrayList<String> propertyList = NO_LEVEL_VALUE.get(i);
            String propertyEntry = getRandomEntry(propertyList);
            properties[i + RANDOM_LIST.size()] = propertyEntry;
        }
        return properties;
    }

    public static String getResult(long qqID) {
        totalScore = 0;
        String[] properties = getProperties();
        String ret = "您的胖次是于 ";
        ret += properties[MAKE_TIME_IDX] + " 制造的 ";
        ret += properties[BASE_MATERIAL_IDX] + " 材质的";
        ret += properties[COLOR_IDX] + " 的 ";
        ret += properties[PATTERN_IDX] + " 胖次\n拥有 ";
        ret += properties[ORNAMENT_IDX] + " 的特性\n价值:";
        ret += properties[VALUE_IDX] + "\n";
        ret += "目前状态为: " + properties[STATUS_IDX];
        ret += "\n该胖次评分为: " + totalScore;
        /* Write data and plus message*/
        var bestData = UnopenedRunningData.getRandomPantBestUid();
        long uid = bestData.getFirst();
        int data = bestData.getSecond();
        if (totalScore <= data) {
            ret += "\n今日评分最佳" + data + "(" + uid + ")";
        } else {
            ret += "\n原今日评分最佳" + data + "(" + uid + ")";
            int senderUid = UserManager.regUserOf(qqID).getUid();
            UnopenedRunningData.setRandomPantBestUid(new Pair<Integer, Integer>(senderUid,totalScore));
        }
        return ret;
    }

}