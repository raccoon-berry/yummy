package berry.yummy;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import berry.yummy.R;

public class HistoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        // TOPへボタン
        Button topButton = (Button) findViewById(R.id.history_to_top_button);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HistoryActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });

        // データ取得
//        List menus = getMenus();
    }

//    private List getMenus() {
//        DatabaseHelper databaseHelper = new DatabaseHelper(this);
//        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
//        String getMenuQuery = "select * from menus join eatings on menus.eating_id = eatings.id order by eatings.yummy_count desc;";
//        Cursor c = sqLiteDatabase.rawQuery(getMenuQuery, null);
//        List menus = new ArrayList();
//        while (c.moveToNext()) {
//            Map row = new HashMap<String, String>();
//            row.put("")
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.history, menu);
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
