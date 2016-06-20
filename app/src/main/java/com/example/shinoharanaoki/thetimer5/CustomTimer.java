package com.example.shinoharanaoki.thetimer5;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by shinoharanaoki on 2015/12/17.
 */
public class CustomTimer {


    static long diff; //millisecond

    static long diffSeconds;
    static long diffMinutes = 0;
    static long diffHours;
    static long diffDays;

    static Date pausedDate = null;
    static Date currentDate;
    static Date endDate;

    static long offsetTime = 0;

    long endTimeLong;

    DecimalFormat df;

    public CustomTimer(){

        diff = 0;
        diffSeconds = 0;
        diffMinutes = 0;
        diffHours = 0;
        diffDays = 0;

        endDate = null;
    }


    public void setEndTime(Task task){

        if(task.endTimeLong == 0) {
            //current_date = new Date();
            Calendar cal = Calendar.getInstance();
            // reachableDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(itemEndDate);
            // 現在時刻を設定
            // 150日を加算
            cal.add(Calendar.HOUR, task.durationHour);
            cal.add(Calendar.MINUTE, task.duration);

            endDate = cal.getTime();

            offsetTime = task.elapsedTime;

            df = new DecimalFormat("0");
            //df.applyPattern("###,###,###.00");

            df.setMaximumFractionDigits(0);
            df.setMinimumFractionDigits(0);

            endTimeLong = endDate.getTime();
        }else{
            endTimeLong = task.getEndTimeLong();
        }
    }


    public void setDifferenceTimer(final TextView hView, final TextView mView, final TextView sView,
                                   final TextView hChar, final TextView money){

        //final MainListFragment.TaskListAdapter.ViewHolder viewHolder = (MainListFragment.TaskListAdapter.ViewHolder) holder;
        currentDate = Calendar.getInstance().getTime(); //スレッド継続中更新し続ける

        //parse()...http://www.ne.jp/asahi/hishidama/home/tech/java/date.html#java.sql.Date

        if(pausedDate == null) {
            diff = endTimeLong - currentDate.getTime();

        }else {
            diff = endTimeLong + pausedDate.getTime() - currentDate.getTime() + pausedDate.getTime();
        }

        diffSeconds = diff / 1000 % 60;
        diffMinutes = diff / (60 * 1000) % 60;
        diffHours = diff / (60 * 60 * 1000) % 24;
        diffDays = diff / (24 * 60 * 60 * 1000);
        //http://detail.chiebukuro.yahoo.co.jp/qa/question_detail/q1118297861




        //viewHolder.days_tf.setText(""+diffDays);
        //viewHolder.hours_tf.setText(""+diffHours);
        mView.setText("" + diffMinutes);
        sView.setText("" + diffSeconds);


        /*if(設定で表示・非表示を切り替え) {
            double diffMoneyPerSeconds = (diff / 1000) * Task.timeMoneyRatePerSecond;
            //TODO 換算金額を表示する
            money.setText(df.format(diffMoneyPerSeconds));
        }*/

        if(diff < 60000 && diff >= 0){
            sView.setTextSize(55);
        }else{
            sView.setTextSize(45);
        }

        if(diffHours <= -1 || diffHours >= 1 ){
            hView.setText("" + diffHours);
            hChar.setText("時間");
        }

        if(diff < 0){
            //TODO 残り0秒切ったら文字盤の色を赤にする
            sView.setTextColor(Color.RED);
            mView.setTextColor(Color.RED);
            hView.setTextColor(Color.RED);
            sView.setText("" + Math.abs(diffSeconds));
            if(diff >= -60000){
                sView.setText("" + diffSeconds);
            }
        }else{
            sView.setTextColor(Color.BLACK);
            mView.setTextColor(Color.BLACK);
            hView.setTextColor(Color.BLACK);

        }


    }

    public void setPausedDate(){

        pausedDate = Calendar.getInstance().getTime();
    }


    public int getEarnedMoney(){

        double diffMoneyPerSeconds = (diff / 1000) * Task.timeMoneyRatePerSecond;

        return (int)diffMoneyPerSeconds;
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("endTimeLong", endTimeLong );
        //outState.putLong("diff", diff);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // 再生成時にはsavedInstanceStateがnullじゃない
        if (savedInstanceState != null) {
            endTimeLong = savedInstanceState.getLong("endTimeLong");
            //diff = savedInstanceState.getLong("diff");

            RunningTaskFragment.startTimer();
        }
    }*/

    public long getEndTimeLong(){
        return endTimeLong;
    }

}
