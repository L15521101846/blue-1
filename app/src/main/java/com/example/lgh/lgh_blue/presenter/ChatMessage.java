package com.example.lgh.lgh_blue.presenter;

/**
 * Created by LGH on 2017/2/10.
 */

public class ChatMessage {
    private int person;//人物头像
    private long time;//显示的时间
    private String input;//内容
    private int type;//信息类型，发送（右边显示）还是接收（左边显示）
    /*
    * 无参构造器
    * */
    public ChatMessage(){

    }
    /*
    *传值构造器
    * */
    public ChatMessage(int person,long time,String input){
        this.person=person;
        this.time=time;
        this.input=input;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type=type;
    }

    public int getPerson(){
        return person;
    }

    public void setPerson(int person){
        this.person=person;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}
