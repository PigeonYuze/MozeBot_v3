package com.pigeonyuze.command.phigros.data;

import com.pigeonyuze.exception.AlreadyExistException;
import com.pigeonyuze.exception.NoPermissionException;
import com.pigeonyuze.util.LoggerManager;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.code.MiraiCode;
import net.mamoe.mirai.message.data.*;

import java.util.*;

public class PhigrosSongComments {

    private final PhigrosSong phigrosSong;
    List<CommentsObject> commentsObjectList = new ArrayList<>();

    public PhigrosSongComments(PhigrosSong song){
        this.phigrosSong = song;
    }

    public PhigrosSongComments(PhigrosSong song,List<CommentsObject> commentsObjectList){
        this.phigrosSong = song;
        this.commentsObjectList = commentsObjectList;
    }

    public PhigrosSong getPhigrosSong() {
        return phigrosSong;
    }

    public void addComments(long senderID, String senderName, String miraiStringData, boolean isQuote, Integer quoteID) throws IllegalArgumentException{
        CommentsObject commentsObject = new CommentsObject(
                senderName, senderID, miraiStringData, 0,0, isQuote, 1000 + commentsObjectList.size());
        if (isQuote){
            if (quoteID==null) throw new IllegalArgumentException("If is quote,then quoteID must be not null");
            try {
                commentsObject.setQuote(quoteID);
            } catch (AlreadyExistException | NoPermissionException ignored) {
            }
        }
        commentsObjectList.add(commentsObject);
        commentsObjectList.sort(Collections.reverseOrder());
    }

    @Override
    public String toString() {
        return "PhigrosSongComments{" +
                "phigrosSong=" + phigrosSong.getMasterName() +
                ",commentsObjectList=" + commentsObjectList +
                '}';
    }



    public void likeComments(long sender, int commentsID) throws AlreadyExistException,NullPointerException {
        for (CommentsObject commentsObject : commentsObjectList){
            if (commentsID != commentsObject.getId()) continue;
            commentsObject.like(sender);
            return;
        }
        throw new NullPointerException("cant find commentsID :"+commentsID);
    }

    public void doNotLikeComments(long sender,int commentsID) throws AlreadyExistException,NullPointerException {
        for (CommentsObject commentsObject : commentsObjectList){
            if (commentsID != commentsObject.getId()) continue;
            commentsObject.bad(sender);
            return;
        }
        throw new NullPointerException("cant find commentsID :"+commentsID);
    }

    public void reportComments(long sender,int commentsID) throws NullPointerException{
        for (CommentsObject commentsObject : commentsObjectList){
            if (commentsID != commentsObject.getId()) continue;
            LoggerManager.debugMessage(new PlainText(sender+" 发送了一条举报信息\n发送者 "+ commentsObject.getSenderID() +" | 信息：").plus(
                    MiraiCode.deserializeMiraiCode(commentsObject.getMiraiStringData())).plus("\n\n原内容："+ commentsObject));
            return;
        }
        throw new NullPointerException("cant find commentsID :"+commentsID);
    }

    public void removeComments(long sender,int commentsID) throws NoPermissionException,NullPointerException {
        for (CommentsObject commentsObject : commentsObjectList){
            if (commentsID != commentsObject.getId()) continue;
            if (sender != commentsObject.getSenderID()) throw new NoPermissionException("The comment must be deleted by the sender above permissions");
            commentsObject.rm(sender);
            return;
        }
        throw new NullPointerException("cant find commentsID :"+commentsID);
    }

    public ForwardMessage buildComments(Contact contact,MessageSource source){
        ForwardMessageBuilder forwardMessageBuilder = new ForwardMessageBuilder(contact);
        forwardMessageBuilder.add(contact.getBot(),new PlainText("关于 "+phigrosSong.getMasterName() +" 的墨泽酱使用者评论"));
//        forwardMessageBuilder.add(contact.getBot(),phigrosSong.toMiraiMessage(contact));
        Map<Integer,CommentsObject> idAndMessageChainMap = new HashMap<>();
        for (CommentsObject commentsObject : commentsObjectList){
            Message thisMessage = commentsObject.message();
            idAndMessageChainMap.put(commentsObject.getId(),commentsObject);
            if (commentsObject.isQuote()) {
                MessageSource messageSource = new MessageSourceBuilder()
                        .allFrom(source)
                        .messages(idAndMessageChainMap.get(commentsObject.getQuoteID()).message())
                        .sender(commentsObject.getSenderID())
                        .time(commentsObject.getSendTime())
                        .build(contact.getBot().getId(),MessageSourceKind.GROUP);
                QuoteReply quoteReply = new QuoteReply(messageSource);
                forwardMessageBuilder.add(commentsObject.getSenderID(),
                        commentsObject.getSenderName(),
                        quoteReply.plus(thisMessage));
            } else {
                forwardMessageBuilder.add(
                        commentsObject.getSenderID(),
                        commentsObject.getSenderName(),
                        thisMessage,
                        commentsObject.getSendTime());
            }
        }
        return forwardMessageBuilder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PhigrosSongComments)) return false;
        PhigrosSongComments that = (PhigrosSongComments) o;
        return getPhigrosSong().equals(that.getPhigrosSong()) && commentsObjectList.equals(that.commentsObjectList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPhigrosSong(), commentsObjectList);
    }
}
