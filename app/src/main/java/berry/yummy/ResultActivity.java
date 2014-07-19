package berry.yummy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends Activity {

    // TODO 星5つになる回数、設定ファイルで持ちたい
    public static int MAX_COUNT = 10;
    private long eatingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // インテントからeatingIdとyummyCountを取り出す
        Intent intent = getIntent();
        this.eatingId = intent.getLongExtra("eatingId", 0);
        int yummyCount = intent.getIntExtra("yummyCount", 0);

        // yummy度の算出
        int starCount = yummyCount / (MAX_COUNT / 5);
        if (starCount > 5) {
            starCount = 5;
        }
        String starStr = "";
        for (int i = 0; i < starCount; i++) {
            starStr += "★";
        }
        TextView starText = (TextView) findViewById(R.id.result_star_text);
        starText.setText(starStr);

        TextView msgText = (TextView) findViewById(R.id.result_msg_text);
        if (starCount == 1) {
            msgText.setText(R.string.result_min_yummy_msg);
        } else if (starCount == 5) {
            msgText.setText(R.string.result_max_yummy_msg);
        }

        // TOPへボタン
        Button topButton = (Button) findViewById(R.id.to_top_button);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.result, menu);
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
