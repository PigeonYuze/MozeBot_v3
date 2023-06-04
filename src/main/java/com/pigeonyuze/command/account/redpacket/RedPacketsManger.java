package com.pigeonyuze.command.account.redpacket;

import net.mamoe.mirai.utils.ExternalResource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Random;

public class RedPacketsManger {

    static final BigDecimal MIN = new BigDecimal("1");

    private static ArrayList<BigDecimal> genRedPackets(double total, int count) throws RedPacketsException {


        ArrayList<BigDecimal> packets = new ArrayList<>();


        double min = MIN.multiply(new BigDecimal(count)).setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        if (min > total) {
            throw new RedPacketsException("每个红包不能连1元都没有");
        } else if (min == total) {
            for (int i = 0; i < count; i++) {
                BigDecimal item = new BigDecimal("1");
                packets.add(item);
            }
        } else {
            BigDecimal totalMoney = new BigDecimal(total);
            BigDecimal now = new BigDecimal(0);
            double[] scale = randomScale(count);
            for (int i = 0; i < count - 1; i++) {
                BigDecimal item = totalMoney.multiply(BigDecimal.valueOf(scale[i])).setScale(2, RoundingMode.HALF_EVEN);
                packets.add(item);
                now = now.add(item);
            }
            BigDecimal last = totalMoney.subtract(now);
            packets.add(last);
        }

        return packets;
    }

    /**
     * 得出随机红包金额的比例
     *
     * @param count 红包的个数
     * @return 每份红包的比例数组
     */
    private static double[] randomScale(int count) {
        double[] scale = new double[count];
        Random r = new Random();
        double total = 0.0;
        for (int i = 0; i < count; i++) {
            scale[i] = r.nextInt(100) + 1;
            total += scale[i];
        }
        for (int i = 0; i < count; i++) {
            scale[i] = scale[i] / total;
        }
        return scale;
    }

    /**
     * @param coins 总金币
     * @param count 最多使用次数
     */
    public static ArrayList<Integer> getUserCoins(int coins, int count, boolean isLuck) throws RedPacketsException {
        if (!isLuck) {
            ArrayList<Integer> getCoins = new ArrayList<>();
            if (coins % count != 0) throw new RedPacketsException("The total amount of the regular red packet must be divisible by the number of recipients\n" +
                    "You can use the \"Luck RedPacket\" to send");
            else {
                int getCoin = coins / count;
                for (int i = 0; i < coins; i++) {
                    getCoins.add(getCoin);
                }
            }
            return getCoins;
        }
        ArrayList<BigDecimal> list = genRedPackets(coins, count);

        for (int j = 0; j < list.size(); j++) {
            if (list.get(j).intValue() == (0)) {
                list.set(j, BigDecimal.valueOf((list.get(j).intValue() + 1)));
                continue;
            }
            int randomSize = new Random().nextInt(list.size());
            BigDecimal redPacket = list.get(randomSize);
            redPacket = new BigDecimal(redPacket.intValue() + 1);
            list.set(randomSize, BigDecimal.valueOf(redPacket.intValue()));
        }
        ArrayList<Integer> ret = new ArrayList<>();
        for (BigDecimal rp : list) {
            ret.add(rp.intValue());
        }
        return ret;
    }

    /**
     * 红包异常
     *
     */
    public static class RedPacketsException extends Exception {

        public RedPacketsException() {
        }

        public RedPacketsException(String message) {
            super(message);
        }

    }

}
