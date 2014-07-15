package berry.yummy;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by igawa on 2014/07/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "yummy.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // 食事テーブル(1回の食事で作成される基本テーブル、ここにおいしい回数や日付が入る
        String createEatingsQuery = "create table eatings (" +
                " id integer primary key," +
                " start_time text," +
                " end_time text," +
                " yummy_count integer default 0," +
                " created_at text," +
                " updated_at text);";
        sqLiteDatabase.execSQL(createEatingsQuery);
        // メニューテーブル(1回の食事に紐づく献立、これがある場合のみ履歴に表示させる)
        String createMenusQuery = "create table menus (" +
                "id integer primary key," +
                " eating_id integer," +
                " name text not null," +
                " memo text);";
        sqLiteDatabase.execSQL(createMenusQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // TODO upgrade
    }
}
