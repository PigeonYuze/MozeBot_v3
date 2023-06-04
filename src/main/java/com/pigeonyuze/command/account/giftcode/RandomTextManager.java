package com.pigeonyuze.command.account.giftcode;

public class RandomTextManager {
    /**
     * 生成num位的随机字符串(数字 、 大写字母随机混排)
     */
    public static String createBigSmallLetterStrOrNumberRandom(int num) {

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int intVal = (int) (Math.random() * 58 + 65);
            if (intVal >= 91 && intVal <= 96) {
                i--;
            }
            if (intVal < 91 || intVal > 96) {
                if (intVal % 2 == 0) {
                    str.append((char) intVal);
                } else {
                    str.append((int) (Math.random() * 10));
                }
            }
        }
        return str.toString();
    }

    /**
     * 生成num位的随机字符串(数字 、 小写字母随机混排)
     */
    public static String createSmallStrOrNumberRandom(int num) {

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int intVal = (int) (Math.random() * 26 + 97);
            if (intVal % 2 == 0) {
                str.append((char) intVal);
            } else {
                str.append((int) (Math.random() * 10));
            }
        }
        return str.toString();
    }

    /**
     * 生成num位的随机字符串(小写字母与数字混排)
     */
    public static String createBigStrOrNumberRandom(int num) {

        StringBuilder str = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int intVal = (int) (Math.random() * 26 + 65);
            if (intVal % 2 == 0) {
                str.append((char) intVal);
            } else {
                str.append((int) (Math.random() * 10));
            }
        }
        return str.toString();
    }
}
