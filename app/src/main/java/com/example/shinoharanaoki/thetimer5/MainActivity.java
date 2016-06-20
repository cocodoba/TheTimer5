package com.example.shinoharanaoki.thetimer5;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected static ViewPager viewPager;
    protected static TextView moneyCounter;
    private AlertDialog dialog;

    private MainListFragment mlf;
    private RunningTaskFragment rtf;



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //activity_start includes viewpager_layout

        //mRelativeLayout = (RelativeLayout) findViewById(R.id.fill_window_layout);
        //setContentView(mRelativeLayout);

        /*final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //method ex.AppCompatActivity
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

       moneyCounter = (TextView)findViewById(R.id.money_counter);

        //final ActionBar ab = getSupportActionBar(); //ToolbarにActionBarの機能を持たせる
        //ab.setHomeAsUpIndicator(R.drawable.); //メニューを開く三本線のアイコン画像//オプショ
        //ab.setDisplayHomeAsUpEnabled(true); //上にスクロール時にアクションバーを表示する//オプション

        //CUSTUM: ページの中身の部分###############
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        final LinearLayout layout = new LinearLayout(MainActivity.this); //Layout on Dialog
        //テキスト入力を受け付けるビューを作成します。
        final EditText editName = new EditText(MainActivity.this);
        final EditText editDuration = new EditText(MainActivity.this);

        editName.setInputType( InputType.TYPE_CLASS_TEXT);
        editDuration.setInputType( InputType.TYPE_CLASS_NUMBER);

        editName.setHint("予定名");
        editDuration.setHint("予定時間");

        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(editName);
        layout.addView(editDuration);


        //TODO RunningTaskFragmentページに遷移した時fabを表示しないようにする

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dialog == null) {

                    dialog = new AlertDialog.Builder(MainActivity.this)
                            //.setIcon(android.R.drawable.ic_dialog_info)
                            .setTitle("新しい予定を作成")
                                    //setViewにてビューを設定します。
                            .setView(layout)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    String name = editName.getText().toString();
                                    int dur = Integer.parseInt(editDuration.getText().toString());


                                    Task newTask = new Task(name, dur, false, 0);

                                    mlf.addTask(newTask);

                                    //TODO リストが更新したことをアダプタに通知する
                                    //mlf.updateList();

                                    String toastText = String.format(getString(R.string.new_task_toast), name, dur);
                                    //入力した文字をトースト出力する
                                    Toast.makeText(MainActivity.this,
                                            toastText,
                                            Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("キャンセル", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                            .create();
                    editName.setText("");
                    editDuration.setText("");
                }

                dialog.show();

                editName.setText("");
                editDuration.setText("");



                /**
                 * 新しい予定を作るダイアログを呼び出す
                 *
                 *
                 * DialogFragment dialog = new NewTaskCreateFragment
                 *
                 *
                 * dialog.addName();
                 *
                 * dialog.addTime();
                 *
                 *
                 *http://qiita.com/kojionilk/items/138eea19dadb14997136
                 *
                 * */

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.sample_actions, menu); //sample_actionsをmenuにinする
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //CUSTUM: 設定ボタン(3本横線)を押してドロアーを出す
        //switch (item.getItemId()) {
        //case android.R.id.home:
        //mDrawerLayout.openDrawer(GravityCompat.START); //<<<???
        return true;
    }

    //CUSTUM: 下のAdapterクラス(static)のメソッドをつかってページ(Fragment)を生成する
    //ページ数と各タイトルを指定する(ここではタブは関係ない)
    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        //Adapter2 adapter = new Adapter2(getSupportFragmentManager());

        mlf = new MainListFragment();
        rtf = new RunningTaskFragment();

        rtf.mlf = mlf;

        adapter.addFragment(mlf, "1"); //一度アダプタのリスト変数(mFragments)につっこむ
        adapter.addFragment(rtf, "2");
        //必要な分だけ足す...
        viewPager.setAdapter(adapter);
    }

    public static void updateMoneyCounter(String s){

        moneyCounter.setText(s);

        /*if(Task.total_money_amount >= 0 ){
            //MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_plus), Task.total_money_amount));
            moneyCounter.setText(String.format(getString(R.string.total_money_plus), Task.total_money_amount));
        }else{
            //MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_minus), Task.total_money_amount));
            moneyCounter.setText(String.format(getString(R.string.total_money_minus), Task.total_money_amount));
        }*/

    }


    //アダプタ
    static class Adapter extends FragmentPagerAdapter { //ViewPager専用のアダプタ
        private final List<Fragment> mFragments = new ArrayList<>();//表示前の複数のフラグメントを一度に格納する
        private final List<String> mFragmentTitles = new ArrayList<>();

        //TODO ページタイトルを表示できるようにする

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) { //↑adapter.addFragment
            //タイトルの他にも画像など一緒に表示したいものがあれば引数で要求できる
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position); //出来たフラグメントを順番に親クラスに渡す、おしまい
        }

        @Override
        public int getCount() {
            return mFragments.size();

            /*もしくは
            *int pageCount = mFragments.size();
            *
            *findViewById(R.id.~)
            * ~ .setText(pageCount)
            */

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

    }

}