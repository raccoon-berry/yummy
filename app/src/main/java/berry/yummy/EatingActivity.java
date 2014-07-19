package berry.yummy;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

import berry.yummy.R;

public class EatingActivity extends Activity {

    private long eatingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eating);
        // ごちそうさまボタン
        Button endButton = (Button) findViewById(R.id.end_button);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ここに聞き取りをSTOPする処理が入るはず

                // 結果を更新
                // FIXME これはテストコードです。
                int yummyCount = 20;
                updateEating(yummyCount, eatingId);
                Intent intent = new Intent(EatingActivity.this, ResultActivity.class);
                intent.putExtra("eatingId", eatingId);
                intent.putExtra("yummyCount", yummyCount);
                startActivity(intent);
            }
        });

        // 結果テーブルを作成
        long result = insertEating();
        if (result != -1) {
            this.eatingId = result;
        }
    }

    private long insertEating() {
        String now = DatabaseHelper.getCureentTimeString();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.EATING_CREATED_AT, now);
        values.put(DatabaseHelper.EATING_START_TIME, now);
        values.put(DatabaseHelper.EATING_YUMMY_COUNT, 0);
        long result = -1;
        if (sqLiteDatabase != null) {
            result = sqLiteDatabase.insert(DatabaseHelper.EATING_TABLE, null, values);
        }
        return result;
    }

    private long updateEating(int yummyCount, long eatingId) {
        String now = DatabaseHelper.getCureentTimeString();
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.EATING_ID, eatingId);
        values.put(DatabaseHelper.EATING_CREATED_AT, now);
        values.put(DatabaseHelper.EATING_START_TIME, now);
        values.put(DatabaseHelper.EATING_YUMMY_COUNT, yummyCount);
        long result = -1;
        if (sqLiteDatabase != null) {
            result = sqLiteDatabase.insert(DatabaseHelper.EATING_TABLE, null, values);
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.eating, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
