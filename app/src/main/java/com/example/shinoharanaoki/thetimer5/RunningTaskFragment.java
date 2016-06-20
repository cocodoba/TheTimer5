package com.example.shinoharanaoki.thetimer5;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



public class RunningTaskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final int TIMER_IS_NOT_RUNNING = 1;
    private static final int TIMER_HAS_STOPPED = 2;
    private static final int TIMER_IS_RUNNING = 3;

    // TODO: Rename and change types of parameters
    protected static Task mTask;
    protected static CustomTimer mTimer;
    protected static ScheduledExecutorService service = null;

    protected static TextView title;
    protected static TextView seconds;
    protected static TextView minutes;
    protected static TextView hours;
    protected static TextView hourChar;
    protected static TextView moneyConversion;
    protected static TextView cancelButton;

    //TODO 終了ボタンを押したときタイマーを止める
    protected static TextView closeButton;

    protected MainListFragment mlf;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_running_task, container, false);

        View v = inflater.inflate(R.layout.fragment_running_task_omit_money, container, false);

        title = (TextView)v.findViewById(R.id.running_task_title);
        seconds = (TextView)v.findViewById(R.id.seconds_meter);
        minutes = (TextView)v.findViewById(R.id.minutes_meter);
        hours = (TextView)v.findViewById(R.id.hour_meter);
        hourChar = (TextView)v.findViewById(R.id.hour_char);
        closeButton = (TextView)v.findViewById(R.id.close_button);
        cancelButton = (TextView)v.findViewById(R.id.cancel_button);
        moneyConversion = (TextView)v.findViewById(R.id.money_conversion);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(service.isShutdown() == false) {

                        mTask.addTotalMoneyAmount(mTimer.getEarnedMoney());

                        if (Task.total_money_amount >= 0) {
                            MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_plus), Task.total_money_amount));
                        } else {
                            MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_minus), Task.total_money_amount));
                        }

                        service.shutdown();
                        MainListFragment.itemRunning = TIMER_IS_NOT_RUNNING;
                        //TODO MainListFragmentに色を戻すメソッドを設置する
                        MainListFragment.mHolder.mTitle.setTextColor(Color.parseColor("#9acd32"));

                        mTask.setEndTimeLong(0);

                    mlf.mAdapter.notifyDataSetChanged();

                }
            }
        });

        //キャンセルボタンを押した時の処理
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(service.isShutdown() == false) {

                    service.shutdown();
                    MainListFragment.itemRunning = TIMER_IS_NOT_RUNNING;
                    //TODO MainListFragmentに色を戻すメソッドを設置する
                    MainListFragment.mHolder.mTitle.setTextColor(Color.parseColor("#9acd32"));

                    MainActivity.viewPager.setCurrentItem(MainActivity.viewPager.getCurrentItem() - 1);

                    mTask.setEndTimeLong(0);

                    mlf.mAdapter.notifyDataSetChanged();

                }
            }
        });

        return v;

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();

        if(mTask != null) {
            mTask.setEndTimeLong(mTimer.getEndTimeLong());

            Toast.makeText(getActivity(), "end_time:" + Long.toString(mTask.getEndTimeLong()),
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onStop(){
        super.onStop();
        if(mTask != null) {
            mTask.setEndTimeLong(mTimer.getEndTimeLong());
            service.shutdown();

            Toast.makeText(getActivity(), "RunningTaskFragment.onStop",
                    Toast.LENGTH_LONG).show();
        }
    }

    public static void startTimer(CustomTimer timer) {

        mTimer = timer;
        mTimer.setEndTime(mTask);

        title.setText(mTask.item_name);

        hours.setText("");
        hourChar.setText("");

        //http://stackoverflow.com/questions/19873063/handler-is-abstract-cannot-be-instantiated
        final Handler mainHandler = new Handler(Looper.getMainLooper());
        
        service = Executors.newSingleThreadScheduledExecutor();

        //ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        mTimer.setDifferenceTimer(hours, minutes, seconds, hourChar, moneyConversion);
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }







}
