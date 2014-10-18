package berry.yummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;

public class ResultActivity extends Activity {

    private long eatingId;
    private PopupWindow mPopupWindow;
    private View popupView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        // インテントからeatingIdとyummyCountを取り出す
        Intent intent = getIntent();
        this.eatingId = intent.getLongExtra("eatingId", 0);
        int yummyCount = intent.getIntExtra("yummyCount", 0);

        TextView yummyCountText = (TextView) findViewById(R.id.yummy_count_text);
        yummyCountText.setText(String.valueOf(yummyCount));

        // TOPへボタン
        BootstrapButton topButton = (BootstrapButton) findViewById(R.id.to_top_button);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultActivity.this, MyActivity.class);
                startActivity(intent);
            }
        });

        // POPUPの設定
        mPopupWindow = new PopupWindow(ResultActivity.this);
        popupView = getLayoutInflater().inflate(R.layout.popup_layout, null);
        popupView.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
            }
        });
        popupView.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BootstrapEditText menuEditText = (BootstrapEditText) popupView.findViewById(R.id.menu_text);
                BootstrapEditText memoEditText = (BootstrapEditText) popupView.findViewById(R.id.memo_text);
                String inputMenu = menuEditText.getText().toString();
                String inputMemo = memoEditText.getText().toString();
                // メニューの登録
                insertMenu(ResultActivity.this.getEatingId(), inputMenu, inputMemo);
                Toast.makeText(getApplicationContext(), inputMenu + "を記録しました。", Toast.LENGTH_LONG).show();
                menuEditText.setText("");
                memoEditText.setText("");
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.setContentView(popupView);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setHeight(300);
        mPopupWindow.setWidth(300);
        mPopupWindow.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // メニュー記録ボタン
        BootstrapButton addButton = (BootstrapButton) findViewById(R.id.result_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

//
//                AlertDialog.Builder builder = new AlertDialog.Builder(ResultActivity.this);
//                builder.setTitle(R.string.result_add_menu_title);
//
//                final EditText menuName = new EditText(ResultActivity.this);
//                final EditText memo = new EditText(ResultActivity.this);
//                TextView menuNameTitle = new TextView(ResultActivity.this);
//                menuNameTitle.setText(R.string.menu_text);
//                menuNameTitle.setTextSize(20);
//                TextView memoTitle = new TextView(ResultActivity.this);
//                memoTitle.setText(R.string.memo_text);
//                memoTitle.setTextSize(20);
//
//                //外枠とパーツの作成
//                LinearLayout layout = new LinearLayout(ResultActivity.this);
//                layout.setOrientation(LinearLayout.VERTICAL);
//
//                //外枠にパーツを組み込む
//                layout.addView(menuNameTitle, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layout.addView(menuName, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layout.addView(memoTitle, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layout.addView(memo, ViewGroup.LayoutParams.WRAP_CONTENT);
//
//                //レイアウトをダイアログに設定
//                builder.setView(layout);
//
//                // ボタンの追加
//                builder.setPositiveButton(R.string.add_menu_ok_button, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String inputMenu = menuName.getText().toString();
//                        String inputMemo = memo.getText().toString();
//                        // メニューの登録
//                        insertMenu(ResultActivity.this.getEatingId(), inputMenu, inputMemo);
//                        Toast.makeText(getApplicationContext(), inputMenu + "を記録しました。", Toast.LENGTH_LONG).show();
//                    }
//                });
//                builder.setNegativeButton(R.string.add_menu_cancel_button, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                builder.show();
            }
        });


    }

    public long insertMenu(long targetEatingId, String menu, String memo) {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.MENU_NAME, menu);
        values.put(DatabaseHelper.MENU_MEMO, memo);
        values.put(DatabaseHelper.MENU_EATING_ID, targetEatingId);
        long result = -1;
        if (sqLiteDatabase != null) {
            result = sqLiteDatabase.insert(DatabaseHelper.MENU_TABLE, null, values);
        }
        return result;
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

    public long getEatingId() {
        return this.eatingId;
    }
}
