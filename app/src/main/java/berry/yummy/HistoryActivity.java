package berry.yummy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
//日付について
import android.widget.TextView;
import android.text.format.DateFormat;

public class HistoryActivity extends Activity {

    ListView listView;
    static List<History> dataList = new ArrayList<History>();
    static HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // TOPへボタン
        BootstrapButton topButton = (BootstrapButton) findViewById(R.id.history_to_top_button);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent = new Intent(HistoryActivity.this, MyActivity.class);
            startActivity(intent);
            }
        });

        // 履歴表示
        listView = (ListView)findViewById(R.id.listView);
        getHistory();
        setAdaptors();
    }

    protected void getHistory(){
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        String query = "select *" +
                        " from " + helper.EATING_TABLE + "," + helper.MENU_TABLE +
                        " where " + helper.EATING_TABLE + "." + helper.EATING_ID + " = " + helper.MENU_EATING_ID;
        Cursor c = db.rawQuery(query, null);

        if(c.getCount() <= 0){
            adapter.add(new History(
                    "-",
                    "未登録です",
                    "",
                    ""));
        }else {
            boolean isEof = c.moveToFirst();
            while (isEof) {
                dataList.add(
                    new History(
                        c.getString(3),
                        c.getString(8),
                        c.getString(9),
                        c.getString(4)
                    )
                );
                isEof = c.moveToNext();
            }
        }

        c.close();
        db.close();
    }

    protected void setAdaptors(){
        adapter = new HistoryAdapter(this, 0, dataList);
        listView.setAdapter(adapter);
    }

    public class HistoryAdapter extends ArrayAdapter<History> {
        private LayoutInflater layoutInflater_;

        public HistoryAdapter(Context context, int textViewResourceId, List<History> objects) {
            super(context, textViewResourceId, objects);
            layoutInflater_ = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            History history = (History)getItem(position);

            if (convertView == null) {
                convertView = layoutInflater_.inflate(R.layout.history_row, null);
            }

            TextView yumCount = (TextView) convertView.findViewById(R.id.yummy_count);
            TextView menuName = (TextView) convertView.findViewById(R.id.menu_name);
            TextView menuMemo = (TextView) convertView.findViewById(R.id.menu_memo);

            yumCount.setText("おいしさ：" + history.yummyPoint);
            menuName.setText("メニュー：" + history.menu);
            menuMemo.setText("メモ　　：" + history.memo);

            return convertView;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public History getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

    }

}

class History {
    String yummyPoint;
    String menu;
    String memo;
    String createDate;

    public History(String yummyPoint, String menu, String memo, String createDate) {
        this.yummyPoint = yummyPoint;
        this.menu = menu;
        this.memo = memo;
        this.createDate = createDate;
    }

    public String getYummyPoint() {
        return yummyPoint;
    }

    public String getMenu() {
        return menu;
    }

    public String getMemo() {
        return memo;
    }

    public String getCreateDate() {
        return createDate;
    }

}
