package com.example.shinoharanaoki.thetimer5;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by shinoharanaoki on 2015/12/15.
 */
public class Task {

    //TODO 金額設定する画面で設定した値をここに保存するようにする
    protected static int timeMoneyRatePerHour = 750;
    protected static double timeMoneyRatePerSecond  = (double)timeMoneyRatePerHour/3600;

    //TODO 現在までの全てのタスクの累計差引金額をここに加算
    protected static int total_money_amount;

    //TODO タスクに附属文を付けられるようにする

    protected String item_name = null;
    protected static int totalItems;
    protected int item_id;
    protected int duration = 0;//所要時間
    protected int durationHour = 0;
    protected int durationMinutes;
    private int createDate = 0;//作成日時
    private int numberOfRun = 0;//起動回数
    private int position_on_list;
    protected boolean restart = false;//一時停止からの再開か否か
    //http://msugai.fc2web.com/java/permitmod.html

    private String firstStartTime = null;//開始時刻<リスト表示>
    private int stopTime = 0;
    protected long elapsedTime = 0;//再開時用経過時間保持
    protected long endTimeLong = 0;

    //<リスト表示>
    private String passedSeconds = null;
    private String passedMinutes = null;
    private String passedHours = null;

    protected String currentTime = null;
    protected String scheduledEndTime = "2015-12-25 00:00:00";
    protected String remainingTime = null;//残り時間<リスト表示>
    protected String subtractionTime = null;//差し引き時間<リスト表示>

    //http://www.javaroad.jp/java_date3.htm
    /*SimpleDateFormat sdf_HHmmss = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdf_mmss = new SimpleDateFormat("m:ss");
    SimpleDateFormat sdf_H = new SimpleDateFormat("H");
    SimpleDateFormat sdf_m = new SimpleDateFormat("m");
    SimpleDateFormat sdf_ss = new SimpleDateFormat("ss");*/

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private int convertToMoney= 0;//money+-<リスト表示>
    private boolean overTheTime = false; //時間超過したか否か<リスト表示>

    private MainListFragment.TaskListAdapter list_adapter = null;
    //private SimpleTimer timer =null;

    //前回起動日時

    //CONSTRUCTOR(0)
    public Task (){

    }

    //CONSTRUCTOR(1)
    public Task (int dur, boolean restart){

        duration = dur;
        this.restart = restart;
        //setEnd_Time(duration);
    }

    //CONSTRUCTOR(2)
    public Task (String name, int dur, int date, int times, boolean restart, int elps){

        item_name = name;
        duration = dur;
        createDate = date;
        numberOfRun = times;
        //this.restart = restart;
        elapsedTime = elps;
       //setEnd_Time(duration);

    }

    //CONSTRUCTOR(for TEST)
    public Task (String name, int dur, boolean restart, int end){

        item_name = name;
        duration = dur;
        //durationMinutes = duration;
        //this.restart = restart;
        endTimeLong = end;
        item_id = totalItems;

        totalItems++;

        //timeMoneyRatePerSecond = timeMoneyRatePerHour/3600;

        //setEnd_Time(duration);
    }


    /*public void setEnd_Time(int dur){

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 2014/08/01でDateオブジェクト作成
        Date dt = format.parse(currentTime);

        // カレンダークラスのインスタンスを取得
        Calendar cal = Calendar.getInstance();

        // 現在時刻を設定
        cal.setTime(dt);

        // 150日を加算
        cal.add(Calendar.DATE, dur);



        scheduledEndTime = format.format(sb.toString() + currentTime );

    }*/




    public String getEnd_date(){

        return scheduledEndTime;


    }

    public void addTotalMoneyAmount(int money){

        total_money_amount += money;
    }

    //Setters

    public void setName(String name){
        item_name = name;
    }

    public void setId(int id){
        item_id = id;
    }

    public void setDuration(int dur){
        duration = dur;
    }

    public void setPosition(int pos){
        position_on_list = pos;
    }

    public void setTotalItems(int i){
        totalItems = i;
    }

    public void setEndTimeLong(long t){
        endTimeLong = t;
    }

    public void setTotal_money_amount(int m){
        total_money_amount = m;
    }


    //Getters
    public String getName(){
        return item_name;
    }

    public int getId(){
        return item_id;
    }

    public int getDuration(){
        return duration;
    }

    public int getPosition(){
        return position_on_list;
    }

    public int getTotalItems(){
        return totalItems;
    }

    public long getEndTimeLong(){
        return endTimeLong;
    }

    public int getTotal_money_amount(){
        return total_money_amount;
    }

}
