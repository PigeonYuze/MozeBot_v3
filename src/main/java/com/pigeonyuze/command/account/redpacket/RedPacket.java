package com.pigeonyuze.command.account.redpacket;

import com.pigeonyuze.account.User;
import com.pigeonyuze.account.UserManager;
import com.pigeonyuze.command.account.giftcode.GiftCode;
import com.pigeonyuze.command.account.giftcode.GiftCodeManager;
import com.pigeonyuze.command.account.giftcode.RandomTextManager;
import com.pigeonyuze.util.LoggerManager;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.*;

import static com.pigeonyuze.util.GiftCodeData.getRedPacketsList;
import static com.pigeonyuze.util.GiftCodeData.getTextRedPackets;


public class RedPacket {
    /**
     * 对红包系统的定时任务
     * <br> - 对已经到达红包期限的红包进行删除 并归还coin
     * <br> - 删除失效的红包
     */
    private static void startTask() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                for (Iterator<RedPackets> iterator = getRedPacketsList().iterator(); iterator.hasNext(); ) {
                    RedPackets redPackets = iterator.next();
                    String code = redPackets.giftCode;
                    GiftCode giftCode = GiftCodeManager.INSTANCE.of(code);
                    if (giftCode == null || !giftCode.isCancel()) {
                        continue;
                    }
                    if (giftCode.isOverTime()) {
                        List<Integer> getCoinList = redPackets.userCanGetCoin;
                        int needReturnCoin = 0;
                        for (int i = giftCode.getUsedNum(); i < giftCode.getMaxUserNumber(); i++) {
                            needReturnCoin += getCoinList.get((i));
                        }
                        UserManager.INSTANCE.incrementCoin(Objects.requireNonNull(UserManager.userOf(giftCode.getInitiator())), needReturnCoin);
                        iterator.remove();
                        GiftCodeManager.INSTANCE.rm(code);
                    }
                    if (giftCode.isCancel()) {
                        iterator.remove();
                        GiftCodeManager.INSTANCE.rm(code);
                    }
                    if (redPackets.type == Type.TEXT || redPackets.type == Type.LUCK_TEXT)
                        getTextRedPackets().remove(redPackets.text);
                }
            }
        };
        timer.schedule(task, 1000 * 60 * 10, 1000 * 60 * 10);
    }

//    /**
//     * <strong>处理指令</strong>
//     */
//    public static void command(GroupMessageEvent event) {
//        MessageChain messageChain = event.getMessage();
//        String msg = messageChain.contentToString();
//        // create : /mz hb create [type] [coin] [maxUse]
//        Type type;
//        Group subject = event.getSubject();
//        long qqid = event.getSender().getId();
//        if (msg.matches("/mz hb create(.+)")) {
//            Matcher matcher = Pattern.compile("/mz hb create (.+) (.+) (.+)").matcher(msg);
//            if (!matcher.find()) {
//                return;
//            }
//            Matcher checkText = Pattern.compile("(.+) (.+)").matcher(matcher.group(1));
//            int maxUse;
//            String text = null;
//            int typeMsg;
//            int coin;
//            if (checkText.find()) {
//                maxUse = Integer.parseInt(matcher.group(2));
//                text = matcher.group(3);
//                typeMsg = Integer.parseInt(checkText.group(1));
//                coin = Integer.parseInt(checkText.group(2));
//            } else {
//                maxUse = Integer.parseInt(matcher.group(3));
//                typeMsg = Integer.parseInt(matcher.group(1));
//                coin = Integer.parseInt(matcher.group(2));
//            }
//            switch (typeMsg) {
//                case 1: {
//                    type = Type.TEXT;
//                    break;
//                }
//                case 2: {
//                    type = Type.COMMON;
//                    break;
//                }
//                case 3: {
//                    type = Type.LUCK_TEXT;
//                    break;
//                }
//                default: {
//                    type = Type.LUCK_COMMON;
//                    break;
//                }
//            }
//            addRedPacket(type, qqid, coin, text, maxUse, event);
//        } else if (msg.matches("/mz hb use (.+)")) {
//            Matcher matcher = Pattern.compile("/mz hb use (.+)").matcher(msg);
//            if (matcher.find()) {
//                GetRedPacketData useOver = useRedPacket(matcher.group(1), qqid);
//                quote(subject, messageChain, useOver.getUserMsg());
//            }
//        } else if (msg.matches("/mz hb (.+)")) {
//            Matcher matcher = Pattern.compile("/mz hb (.+)").matcher(msg);
//            if (!matcher.find()) {
//                return;
//            }
//            String key = matcher.group(1);
//            try {
//                GetRedPacketData useOver = useRedPacket(getTextRedPacketKey(key), qqid);
//                quote(subject, messageChain, useOver.getUserMsg());
//            } catch (RedPacketsManger.RedPacketsException e) {
//                quote(subject, messageChain, "领取失败\n原因: " + e.getMessage());
//                LoggerManager.loggingError(e);
//            }
//        }
//    }

    /**
     * 由口令获取对应的红包码
     */
    private static String getTextRedPacketKey(String text) throws RedPacketsManger.RedPacketsException {
//        System.out.println(getTextRedPackets());
        if (!getTextRedPackets().containsKey(text))
            throw new RedPacketsManger.RedPacketsException("没有找到指定口令红包\n口令：" + text);
        return getTextRedPackets().get(text).code;
    }

    /**
     * 创建一个红包
     *
     * @param coin   红包总余额
     * @param text   红包口令
     *               <br>当 type 为非口令红包时可传值null
     * @param type   红包种类
     * @param maxUse 红包份数
     */
    public static String addRedPacket(Type type, CommandSender sender, int coin, String text, int maxUse) {
        User user = UserManager.regUserOf(sender);
        if (user.getCoin() < coin) {
            return "创建红包失败\n您的雨沫币不足";
        }
        String giftCode = "";
        boolean isLuck = false;
        boolean isText = false;
        String hbCode = RandomTextManager.createBigStrOrNumberRandom(8);
        int uid = user.getUid();
        if (type == Type.TEXT) {
            isText = true;
            giftCode = "mozeFunction--" + uid + " -%1 -" + text + " -common_text --" + hbCode;
        }
        if (type == Type.COMMON) {
            giftCode = "mozeFunction--" + uid + " -%1 -null -common_common --" + hbCode;
        }
        if (type == Type.LUCK_TEXT) {
            isText = true;
            isLuck = true;
            giftCode = "mozeFunction--" + uid + " -%1 -" + text + " -luck_text --" + hbCode;
        }
        if (type == Type.LUCK_COMMON) {
            isLuck = true;
            giftCode = "mozeFunction--" + uid + " -%1 -null -luck_common --" + hbCode;
        }

        List<Integer> userCoin = new ArrayList<>();
        RedPackets redPackets = new RedPackets(
                hbCode,
                giftCode,
                maxUse,
                user.getUid(),
                isLuck,
                coin,
                userCoin,
                type
        );

        try {
            redPackets.setUserCanGetCoin(RedPacketsManger.getUserCoins(coin, maxUse, isLuck));
            if (isText) createTextRedPacket(text, redPackets);
        } catch (RedPacketsManger.RedPacketsException e) {
            LoggerManager.loggingError(e);
            return "创建红包失败\n原因：" + e.getMessage();
        }

        createRedPacket(redPackets);

        UserManager.INSTANCE.incrementCoin(user, -coin);
        GiftCode newGiftCode = new GiftCode(
                giftCode,
                0 /* Luck Red packet do not read coins num */
                , 200, com.pigeonyuze.util.data.LocalDateTime.Companion.toKLocalDateTime( LocalDateTime.now().plusDays(1L)), user.getQqId(), maxUse
        );
        GiftCodeManager.INSTANCE.add(newGiftCode);
        return isText
                ? "您成功创建了一个 一共" + coin + "雨沫币，最多 " + maxUse + " 人领取的 " + type + " 红包\n可使用红包口令 " + text + " 领取\n(指令： /mz hb " + text + ")"
                : "您成功创建了一个 一共" + coin + "雨沫币，最多 " + maxUse + " 人领取的 " + type + " 红包\n可使用红包码 " + hbCode + " 领取\n(指令： /mz hb use " + hbCode + ")";
    }

    /**
     * 创建一个口令红包
     */
    private static void createTextRedPacket(String key, RedPackets redPacket) throws RedPacketsManger.RedPacketsException {
        if (getTextRedPackets().containsKey(key)) throw new RedPacketsManger.RedPacketsException("在系统中存在着同样口令的红包");
        if (key == null || key.length() > 13 || key.length() < 2)
            throw new RedPacketsManger.RedPacketsException("红包口令长度不合格！\n口令总长度需要小于13并大于2");
        redPacket.setText(key);
        getTextRedPackets().put(key, redPacket);
    }

    /**
     * 创建一个红包
     */
    private static void createRedPacket(@NotNull RedPackets redPackets) {
        if (getRedPacketsList().contains(redPackets)) return;
        getRedPacketsList().add(redPackets);
    }

    /**
     * 获取红包码其对应的兑换码
     */
    private static String getGiftCode(String hbCode) {
        for (RedPackets redPackets : getRedPacketsList()) {
            if (redPackets.code.equals(hbCode)) {
                return redPackets.giftCode;
            }
        }

        return hbCode;
    }

    /**
     * 使用红包
     *
     * @param code 红包码
     * @param qqid 使用者qq号
     */
    public static GetRedPacketData useRedPacket(String code, long qqid) {
        code = getGiftCode(code);
        User user = UserManager.regUserOf(qqid);
        for (RedPackets redPackets : getRedPacketsList()) {
            if (!redPackets.giftCode.equals(code)) {
                continue;
            }
            GiftCode giftCodeInfo = GiftCodeManager.INSTANCE.of(redPackets.giftCode);
            if (giftCodeInfo == null) {
                throw new NullPointerException("Gift code should not be null!");
            }
            if (giftCodeInfo.isCancel()) {
                getRedPacketsList().remove(redPackets);
                return new GetRedPacketData(new RedPackets(), 0, false, redPackets.type);
            }
            if (giftCodeInfo.isUserUsed(user))
                return new GetRedPacketData(new RedPackets(), 0, false, redPackets.type);

            int use = giftCodeInfo.getUsedNum();
            giftCodeInfo.use(user);
            UserManager.INSTANCE.incrementCoin(user,redPackets.userCanGetCoin.get(use));
            return new GetRedPacketData(redPackets, use, true, redPackets.type);
        }
        return new GetRedPacketData(new RedPackets(), 0, false, null);
    }

    public enum Type {
        TEXT, COMMON, LUCK_TEXT, LUCK_COMMON
    }

    /**
     * 一个红包对象
     */
    public static class RedPackets implements java.io.Serializable {


        private static final long serialVersionUID = 1054025524627145916L;

        String text = ""; //口令

        String code = "1A2B3C4D"; //红包码

        String giftCode = "mozeFunction--root -%0 -null -null"; //兑换码

        int max = -1; //份数

        int senderUID = 0; //发送者uid

        boolean isLuck = false; //是否为幸运红包

        int allCoins = 0; //总金额

        Type type = Type.COMMON; //红包类型

        List<Integer> userCanGetCoin = new ArrayList<>(); //可领取的红包中的每份金额 使用时就调用该参数

        public RedPackets() {
        }

        public RedPackets(String hbCode) {
            this.code = hbCode;
        }

        public RedPackets(@NotNull String code, @NotNull String giftCode, int max, int senderUID, boolean isLuck, int allCoins, @NotNull List<Integer> userCanGetCoin, @NotNull Type type) {
            this.code = code;
            this.giftCode = giftCode;
            this.max = max;
            this.senderUID = senderUID;
            this.isLuck = isLuck;
            this.allCoins = allCoins;
            this.userCanGetCoin = userCanGetCoin;
            this.type = type;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return code + "," + giftCode + "," + max + "," + senderUID + "," + isLuck + "," + allCoins + userCanGetCoin + "\n";
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RedPackets)) return false;
            RedPackets redPackets = (RedPackets) obj;
            if (redPackets.code.equals(this.code)) {
                return true;
            }
            return redPackets.giftCode.equals(this.giftCode);
        }

        public void setUserCanGetCoin(List<Integer> userCanGetCoin) {
            this.userCanGetCoin = userCanGetCoin;
        }


        @Override
        public int hashCode() {
            return Objects.hash(text, code, giftCode, max, senderUID, isLuck, allCoins, type);
        }
    }

    /**
     * 领取的红包
     */
    public static class GetRedPacketData {
        final int getSize;
        final int formUID;
        final int max;
        final int surplus;
        final RedPackets redPackets;
        final Type type;
        int getCoin;
        boolean canUse;

        public GetRedPacketData(RedPackets redPackets, int getSize, boolean canUse, Type type) {
            this.redPackets = redPackets;
            this.getSize = getSize;
            this.canUse = canUse;
            this.max = redPackets.max;
            this.formUID = redPackets.senderUID;
            this.surplus = this.max - this.getSize - 2/*Fix last more 1*/;
            this.type = type;
            if (!canUse) return;
            if (surplus == -1) {
                this.canUse = false;
                this.getCoin = 0;
            } else this.getCoin = redPackets.userCanGetCoin.get(getSize);
        }


        public String getUserMsg() {
            if (!canUse) return "领取失败\n原因：红包码无效 或 已经领取过该红包了";
            if (surplus != 0) {
                return "您领取到了来自 " + formUID + " 的 " + type + " 红包\n获取了 " + getCoin + " 个雨沫币 该红包还剩余 " + surplus + " 个!";
            } else {
                getRedPacketsList().remove(this.redPackets);
                GiftCodeManager.INSTANCE.rm(redPackets.code);
                if (!Objects.equals(this.redPackets.text, "")) {
                    getTextRedPackets().remove(this.redPackets.text);
                }
                return "您领取到了来自 " + formUID + " 的 " + type + " 红包\n获取了 " + (getCoin) + " 个雨沫币 没有剩余的了！";
            }
        }
    }
}

