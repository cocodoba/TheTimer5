package com.example.shinoharanaoki.thetimer5;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shinoharanaoki on 2015/12/23.
 */
public class TaskDao {

    private static final String TABLE_NAME = "task";

    private static final String COLUMN_TASK_NAME = "task_name";
    private static final String COLUMN_TASK_ID = "taskid";
    private static final String COLUMN_TOTAL_TASKS = "total_tasks";
    private static final String COLUMN_TOTAL_MONEY = "total_money";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_END_TIME = "end_time";
    private static final String COLUMN_POSITION_ON_LIST = "position";


    private static final String[] COLUMNS = {
                                            COLUMN_TASK_NAME,
                                            COLUMN_TASK_ID,
                                            COLUMN_TOTAL_TASKS,
                                            COLUMN_TOTAL_MONEY,
                                            COLUMN_DURATION,
                                            COLUMN_END_TIME,
                                            COLUMN_POSITION_ON_LIST};

    private SQLiteDatabase db;

    public static final String CREATE_SQL = "CREATE TABLE " + TABLE_NAME + " ("
                                            + COLUMN_TASK_NAME + " TEXT, "
                                            + COLUMN_TASK_ID + " INTEGER PRIMARY KEY, "
                                            + COLUMN_TOTAL_TASKS + " INTEGER, "
                                            + COLUMN_TOTAL_MONEY + " INTEGER, "
                                            + COLUMN_DURATION + " INTEGER, "
                                            + COLUMN_END_TIME + " LONG, "
                                            + COLUMN_POSITION_ON_LIST + " INTEGER)";

    public TaskDao(SQLiteDatabase db){
        this.db = db;
    }

    public ArrayList<Task> findAll() {

        ArrayList<Task> list = new ArrayList<>();
        //query生成
        Cursor c = db.query(
                TABLE_NAME,
                COLUMNS,
                null, //selection
                null, //selection args
                null, //group by
                null, //having
                null); //順番

        while (c.moveToNext()) {
            Task task = new Task();
            task.setName(c.getString(c.getColumnIndex(COLUMN_TASK_NAME)));
            task.setId(c.getInt(c.getColumnIndex(COLUMN_TASK_ID)));
            task.setTotalItems(c.getInt(c.getColumnIndex(COLUMN_TOTAL_TASKS)));
            task.setDuration(c.getInt(c.getColumnIndex(COLUMN_DURATION)));
            task.setPosition(c.getInt(c.getColumnIndex(COLUMN_POSITION_ON_LIST)));
            task.setEndTimeLong(c.getLong(c.getColumnIndex(COLUMN_END_TIME)));
            task.setTotal_money_amount(c.getInt(c.getColumnIndex(COLUMN_TOTAL_MONEY)));

            list.add(task);
        }
        c.close();

        return list;

    }

    public Task find(long id) {
        // query生成
        Cursor c = db.query(TABLE_NAME, COLUMNS, COLUMN_TASK_ID + " = ?",
                new String[] { String.valueOf(id) }, null, null, COLUMN_TASK_ID);
        Task task = null;
        // 1行だけfetch
        if (c.moveToFirst()) {
            task = new Task();
            task.setName(c.getString(c.getColumnIndex(COLUMN_TASK_NAME)));
            task.setId(c.getInt(c.getColumnIndex(COLUMN_TASK_ID)));
            task.setTotalItems(c.getInt(c.getColumnIndex(COLUMN_TOTAL_TASKS)));
            task.setDuration(c.getInt(c.getColumnIndex(COLUMN_DURATION)));
            task.setPosition(c.getInt(c.getColumnIndex(COLUMN_POSITION_ON_LIST)));
        }

        // cursorのclose
        c.close();

        return task;
    }

    /**
     * 保存
     *
     * @param task
     * @return
     */
    public long save(Task task) {
        /*if (!task.validate()) {
            // validationチェックにひっかかったら保存しない
            return -1;
        }*/
        // 値設定
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_ID, task.getId());
        values.put(COLUMN_TASK_NAME, task.getName());
        values.put(COLUMN_TOTAL_TASKS, task.getTotalItems());
        values.put(COLUMN_DURATION, task.getDuration());
        values.put(COLUMN_POSITION_ON_LIST, task.getPosition());
        values.put(COLUMN_END_TIME, task.getEndTimeLong());
        values.put(COLUMN_TOTAL_MONEY, task.getTotal_money_amount());

        if (exists(task.getId())) {
            // データすでに存在するなら更新
            String where = COLUMN_TASK_ID + " = ?";
            String[] arg = { String.valueOf(task.getId()) };
            return db.update(TABLE_NAME, values, where, arg);
        } else {
            // データがまだないなら挿入
            values.put(COLUMN_TASK_ID, task.getId());
            return db.insert(TABLE_NAME, null, values);
        }
    }

    public int delete(int id) {
        String whereClause = COLUMN_TASK_ID + "=" + id;
        return db.delete(TABLE_NAME, whereClause, null);
    }

    /**
     * 日付で存在チェック
     *
     * @param id
     * @return
     */
    public boolean exists(int id) {
        return find(id) != null;
    }

    /**
     * データの存在チェック
     *
     * @return
     */
    public boolean exists() {
        return findAll().size() > 0;
    }


}
