package com.example.shinoharanaoki.thetimer5;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//import android.widget.ImageView;
//import android.widget.TextView;


/**
 * Created by shinoharanaoki on 2015/07/21.
 *
 * http://www.takaiwa.net/2012/10/arrayadapterlistview.html
 */

//１ページ目に表示されるメインのフラグメント
//RecyclerViewの動作等をここで調整

public class MainListFragment extends Fragment {

    /**
     *
     * 定数...自動再登録真偽、
     *
     * 所要時間、作成日時、起動回数、前回起動日時、前回リスト登録日時、
     * (指定曜日、本登録予約日時、紐付け予定id、起動アプリ、タグ、必要P残高、予定終了時上積みP額、)
     *
     * */

    //TODO RunningTaskFragment上でタイマーが稼働しているときはリストを選択できないようにする
    private static final int TIMER_IS_NOT_RUNNING = 1;
    private static final int TIMER_HAS_STOPPED = 2;
    private static final int TIMER_IS_RUNNING = 3;

    static int itemRunning = TIMER_IS_NOT_RUNNING;

    static int listNumberTag;
    static int currentlyRunningTask;

    private static CustomTimer mTimer;

    private static ArrayList<Task> tasklist;
    protected TaskListAdapter mAdapter;
    protected static TaskListAdapter.ViewHolder mHolder;

    protected static TaskListAdapter.ViewHolder resumeHolder;
    protected static Task resumeTask;

    private TaskDao taskDao;
    private TaskDataBaseHelper dbHelper;
    private SQLiteDatabase db;
    
    @Nullable
    @Override
    //[1]
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(
                R.layout.recyclable_list, container, false); //layout.~ ��container��in����
        //container = viewpager_layout.xml ----> CoordinatorLayout ex.ViewGroup
        //viewpager.xml��recyclerble_list.xml(RecyclerView)���h�b�L���O

        rv.addItemDecoration(new ListItemDecoration(
                //getResources().getDimensionPixelSize(R.dimen.photos_list_spacing),
                20, //
                //getResources().getInteger(R.integer.photo_list_preview_columns)));
                1) //�1)
        );


        //to[2]
        setupRecyclerView(rv);


        return rv;


    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Toast.makeText(getActivity(),"MainListFragment.onCreate",
                Toast.LENGTH_LONG).show();

        dbHelper = new TaskDataBaseHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        taskDao = new TaskDao(db);

    }

    @Override
    public void onResume(){

        super.onResume();

        if (Task.total_money_amount >= 0) {
            MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_plus), Task.total_money_amount));
        } else {
            MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_minus), Task.total_money_amount));
        }


        Toast.makeText(getActivity(),"MainListFragment.onResume",
                Toast.LENGTH_LONG).show();


    }

    @Override
    public void onPause(){

        super.onPause();
        Toast.makeText(getActivity(),"MainListFragment.onPause",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStop(){
        super.onStop();

        Toast.makeText(getActivity(),"MainListFragment.onStop",
                Toast.LENGTH_LONG).show();

        mAdapter.notifyDataSetChanged();

        tasklist = mAdapter.getmValues();

        for(int i=0; i<tasklist.size(); i++){
            taskDao.save(tasklist.get(i));
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(getActivity(),"MainListFragment.onDestroy",
                Toast.LENGTH_LONG).show();
    }

    //[2]
    private void setupRecyclerView(RecyclerView recyclerView) {

        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        //recyclerView.setLayoutManager(new GridLayoutManager(recyclerView.getContext(),2));

        mAdapter = new TaskListAdapter(makeList());
        mAdapter.setHasStableIds(true);

        ItemTouchHelper itemDecor = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        mAdapter.notifyItemMoved(fromPos, toPos);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        taskDao.delete(tasklist.get(fromPos).getId());
                        tasklist.remove(fromPos);
                        mAdapter.notifyItemRemoved(fromPos);
                    }
                });
        itemDecor.attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(mAdapter);
        //to[4]


    }
    //[3]

    //TODO fabで呼び出したダイアログで作成した新しいタスクオブジェクトをリストに加えるメソッドを用意する

    //TODO 現在リスト上にあるタスクオブジェクトを保存するメソッドを用意する

    //TODO 保存していたタスクオブジェクトをリストに加えるメソッドを用意する

    //TODO 履歴データからTask型に変換したタスクをリストに加えるメソッドを用意する

    private ArrayList<Task> makeList() {

        //tasklist = new ArrayList();

        tasklist = taskDao.findAll();

        /*tasklist.add(new Task("study",10,false,0));
        tasklist.add(new Task("meal",20,false,0));
        tasklist.add(new Task("sleep",15,false,0));
        tasklist.add(new Task("work",15,false,0));
        tasklist.add(new Task("sleep",15,false,0));
        tasklist.add(new Task("work",15,false,0));*/

        //http://javamania.blog25.fc2.com/blog-entry-100.html
        //http://www.takaiwa.net/2012/10/arrayadapterlistview.html


        return tasklist;

    }

    public void addTask(Task task){

        tasklist.add(0, task);
        mAdapter.notifyItemInserted(0);

    }


    public void showTimerStopOrNoDialog(final Context context,
                                        final TaskListAdapter.ViewHolder holder, final CustomTimer timer,
                                        final int position, final Task task){
        
        new AlertDialog.Builder(getActivity())
                .setTitle("Alert Dialog")

                        // メッセージを設定
                .setMessage("現在実行中のタスクを終了してこのタスクを実行しますか？")

                        // Positiveボタン、リスナを設定
                .setPositiveButton("実行",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                // Positiveボタンがクリックされたときの処理
                                //varTextView.setText("YESボタンがクリックされました。");

                                task.addTotalMoneyAmount(timer.getEarnedMoney());

                                if(Task.total_money_amount >= 0 ){
                                    //MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_plus), Task.total_money_amount));
                                    MainActivity.updateMoneyCounter(String.format(getString(R.string.total_money_plus), Task.total_money_amount));
                                }else{
                                    //MainActivity.moneyCounter.setText(String.format(getString(R.string.total_money_minus), Task.total_money_amount));
                                    MainActivity.updateMoneyCounter(String.format(getString(R.string.total_money_minus), Task.total_money_amount));
                                }

                                RunningTaskFragment.service.shutdown();

                                task.setEndTimeLong(0);
                                //文字の大きさを元に戻す
                                //holder.mPassedSeconds.setTextAppearance(context, android.R.style.TextAppearance_Medium);
                                itemRunning = TIMER_IS_NOT_RUNNING;
                                MainListFragment.mHolder.mTitle.setTextColor(Color.YELLOW);

                                timer.setEndTime(task);

                                RunningTaskFragment.mTask = task;

                                RunningTaskFragment.startTimer(timer);

                                currentlyRunningTask = position;

                                MainActivity.viewPager.setCurrentItem(MainActivity.viewPager.getCurrentItem() + 1);

                                //TODO 新しいタスクを始められるようにする
                                //TODO RTFragmentで稼働中だったタスクの差し引き時間超過と金額をTaskクラスに送る

                                //TODO 終了したタスクの稼働実績を何らかの方法で履歴に保存する

                                holder.mTitle.setTextColor(Color.MAGENTA);

                                mHolder = holder;

                                itemRunning = TIMER_IS_RUNNING;

                                mAdapter.notifyDataSetChanged();

                            }
                        })
                        // Neutralボタン、リスナを設定
                .setNeutralButton("一時停止",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                // Neutralボタンがクリックされたときの処理
                                //varTextView.setText("NEUTRALボタンがクリックされました。");

                                timer.setPausedDate();

                                RunningTaskFragment.service.shutdown();
                                itemRunning = TIMER_HAS_STOPPED;
                            }
                        })
                        // Negativeボタン、リスナを設定
                .setNegativeButton("キャンセル",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int which) {
                                // Negativeボタンが押されたときの処理
                                //varTextView.setText("NOボタンがクリックされました。");

                                itemRunning = TIMER_IS_RUNNING;
                            }
                        })
                .show();
        
    }


    //アダプタ-------------------------------------------------------------------------------
    public class TaskListAdapter
            extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {


        protected ArrayList<Task> mValues;

        public ArrayList<Task> getmValues(){
            return mValues;

        }

        //private ImageLoader mImageLoader;
        private Context context;
        private Handler handler;
        //private ScheduledExecutorService service;
        /******************************************/
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        boolean dateHasSet = false;


        /******************************************/
        //private ScheduledFuture updateFuture;

        public void setmListItems(ArrayList<Task> mListItems) {
            this.mValues = mListItems;
            //update the adapter to reflect the new set of mListItems
        }

        //[5]Setup layout of each list items

        public class ViewHolder extends RecyclerView.ViewHolder {

            //TODO タスクに附属文を付けられるようにする


            public final View mView;//body of the list
            public final TextView mTitle;
            public final TextView mDuration;
            //TODO 一時停止した経過時間を表示できるようにする
            public final TextView mPassedMinutes;
            public final TextView mItemId;
            //public final TextView mPassedSeconds;
            //public final TextView mSubtruction;
            //public ImageView pulltab;//上下移動する際のツマミ
            public final TextView mAdpPosition;
            public final TextView mLstPosition;
            public final TextView mHldPosition;
            public final TextView mTagPosition;

            //[7]
            //ViewHolderのコンストラクタ
            public ViewHolder(View view) {
                super(view);
                mView = view;
                mTitle = (TextView) view.findViewById(R.id.taskName); //in list_item.xml
                mDuration = (TextView) view.findViewById(R.id.duration);
                mPassedMinutes = (TextView) view.findViewById(R.id.passed_minutes);
                mItemId = (TextView) view.findViewById(R.id.item_id);
                //mPassedSeconds = (TextView) view.findViewById(R.id.passed_seconds);
                //mSubtruction = (TextView) view.findViewById(R.id.subtruction);
                //pulltab = (ImageView) view.findViewById(R.id.pulltab_icon);
                //imgViewRemoveIcon = (ImageView) view.findViewById(R.id.remove_icon);
                mAdpPosition = (TextView) view.findViewById(R.id.adp_position_on_list);
                mLstPosition = (TextView) view.findViewById(R.id.lst_position_on_list);
                mHldPosition = (TextView) view.findViewById(R.id.hld_position_on_list);
                mTagPosition = (TextView) view.findViewById(R.id.tag_position);

            }


        }

        //[4]
        //アダプタのコンストラクタ
        public TaskListAdapter(ArrayList<Task> items) {

            mValues = items;
        }

        //アダプタのコンストラクタその２
        public TaskListAdapter(Context context) {

            this.context = context;
            //mImageLoader = AppController.getInstance().getImageLoader();

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.items_of_the_list_layout, parent, false);
            return new ViewHolder(itemView);

        }

        //[8]Set contents to each list Items
        @Override  //ex. RecyclerView.Adapter
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final Task currentTask = mValues.get(position);
            //final ViewHolder viewHolder = holder;
            mHolder = holder;

            //取り出したオブジェクトから必要な変数を取得してリスト蘭に表示
            holder.mTitle.setText(currentTask.item_name);
            //holder.taskId = mValues.get(position);
            holder.mDuration.setText(String.valueOf(currentTask.duration));

            holder.mPassedMinutes.setText(String.valueOf(currentTask.endTimeLong));

            holder.mItemId.setText(String.valueOf(currentTask.getId()));

            //!!position check!!
            holder.mAdpPosition.setText(Integer.toString(holder.getAdapterPosition()));
            holder.mLstPosition.setText(Integer.toString(holder.getLayoutPosition()));
            holder.mHldPosition.setText(Integer.toString(position));

            holder.mTagPosition.setText(Integer.toString(listNumberTag));

            holder.mView.setTag(R.integer.list_pos_key, listNumberTag);
            listNumberTag++;

            holder.mView.setTag(R.integer.bind_task_id, currentTask.getId());

            if(currentTask.endTimeLong != 0){

                /*resumeHolder = holder;
                resumeTask = currentTask;*/


                mTimer = new CustomTimer();

                mTimer.setEndTime(currentTask);

                RunningTaskFragment.mTask = currentTask;

                //service = Executors.newSingleThreadScheduledExecutor();
                RunningTaskFragment.startTimer(mTimer);

                currentlyRunningTask = (Integer)holder.mView.getTag(R.integer.list_pos_key);

                holder.mTitle.setTextColor(Color.MAGENTA);

                itemRunning = TIMER_IS_RUNNING;

                MainActivity.viewPager.setCurrentItem(MainActivity.viewPager.getCurrentItem() + 1);

                Toast.makeText(getActivity(),"Restarted",Toast.LENGTH_LONG).show();
            }



            /*
            クリックした時の状態
            1.どのタスクも稼働していない
            2.クリックしたタスクは現在稼働中
            3.クリックしたタスクは稼働していないが、他のタスクが稼働している
            */
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //DONE switch文に修正する

                    switch(itemRunning){

                        case TIMER_IS_NOT_RUNNING:

                            mTimer = new CustomTimer();

                            mTimer.setEndTime(currentTask);

                            RunningTaskFragment.mTask = currentTask;

                            //service = Executors.newSingleThreadScheduledExecutor();
                            RunningTaskFragment.startTimer(mTimer);

                            currentlyRunningTask = (Integer)view.getTag(R.integer.list_pos_key);

                            holder.mTitle.setTextColor(Color.MAGENTA);



                            //TODO 稼働中のタスク欄に「稼働中」と表示する

                            //TODO 稼働中のタスクをクリックした場合はRTFragmentページヘ遷移するようにする

                            /*holder.mPassedSeconds.setTextSize(50);

                            mTimer.setEndTime(currentTask.durationHour, currentTask.durationMinutes);

                            //http://stackoverflow.com/questions/19873063/handler-is-abstract-cannot-be-instantiated*/

                            itemRunning = TIMER_IS_RUNNING;

                            MainActivity.viewPager.setCurrentItem(MainActivity.viewPager.getCurrentItem() + 1);

                            Toast.makeText(getActivity(),Long.toString(currentTask.getId()),
                                    Toast.LENGTH_LONG).show();

                            break;


                        case TIMER_IS_RUNNING:

                            if((Integer)view.getTag(R.integer.list_pos_key) == currentlyRunningTask){
                                MainActivity.viewPager.setCurrentItem(MainActivity.viewPager.getCurrentItem() + 1);
                            }else {
                                showTimerStopOrNoDialog(context, holder, mTimer, (Integer)view.getTag(R.integer.list_pos_key), currentTask);
                            }

                            Toast.makeText(getActivity(),Long.toString(currentTask.getId()),
                                    Toast.LENGTH_LONG).show();

                            break;


                        /*case TIMER_HAS_STOPPED:

                            //TODO 残り時間をHolderから取り出してserviceを続行

                            showTimerStopOrNoDialog(context, holder, mTimer, position, currentTask);

                            //holder.mPassedSeconds.setTextSize(50);

                            mTimer.setEndTime(currentTask);

                            //http://stackoverflow.com/questions/19873063/handler-is-abstract-cannot-be-instantiated
                            final Handler mainHandler = new Handler(Looper.getMainLooper());
                            service = Executors.newSingleThreadScheduledExecutor();
                            service.scheduleAtFixedRate(new Runnable() {
                                @Override
                                public void run() {
                                    mainHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            //setDifferenceTimer(holder, currentTask.durationHour, currentTask.durationMinutes);

                                            notifyDataSetChanged();
                                            dateHasSet = true;
                                        }
                                    });
                                }
                            }, 0, 1000, TimeUnit.MILLISECONDS);
                            itemRunning = TIMER_IS_RUNNING;*/

                    }
                }
            });

        }


        @Override  //ex. RecyclerView.Adapter
        public int getItemCount() {
            return mValues.size();
        }

        protected void updateDataset(){
            for(int i=0; i<mValues.size(); i++){
                notifyItemInserted(i);
            }
        }


    }


}
