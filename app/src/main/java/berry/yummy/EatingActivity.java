package berry.yummy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fuetrek.fsr.FSRServiceEnum.BackendType;
import com.fuetrek.fsr.FSRServiceEnum.EventType;
import com.fuetrek.fsr.FSRServiceEnum.Ret;
import com.fuetrek.fsr.FSRServiceEventListener;
import com.fuetrek.fsr.FSRServiceOpen;
import com.fuetrek.fsr.entity.AbortInfoEntity;
import com.fuetrek.fsr.entity.ConstructorEntity;
import com.fuetrek.fsr.entity.RecognizeEntity;
import com.fuetrek.fsr.entity.ResultInfoEntity;
import com.fuetrek.fsr.entity.StartRecognitionEntity;

class SyncObj{
    boolean isDone=false;

    synchronized void initialize() {
        isDone=false;
    }

    synchronized void wait_(){
        try {
            // wait_()より前にnotify_()が呼ばれた場合の対策としてisDoneフラグをチェックしている
            while(isDone==false){
                wait(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    synchronized void notify_(){
        isDone = true;
        notify();
    }

}

public class EatingActivity extends Activity {

    private Handler handler_;
//    private TextView textResult_;
    private fsrController controller_ = new fsrController();
    private String[] yummies = {"美味","うま","うめ","おいし","旨","最高"};

    // BackendTypeはBackendType.D固定
    private static final BackendType backendType_ = BackendType.D;

    // Context
    private Activity activity_ = null;

    private long eatingId;

    private ProgressView prview = null;

    // FSRServiceの待ち処理でブロッキングする実装としている為、
    // UI更新を妨げないよう別スレッドとしている。
    public class fsrController extends Thread implements FSRServiceEventListener {
        FSRServiceOpen fsr_;
        SyncObj event_CompleteConnect_ = new SyncObj();
        SyncObj event_CompleteDisconnect_ = new SyncObj();
        SyncObj event_EndRecognition_ = new SyncObj();
        SyncObj event_StopRecognition_ = new SyncObj();
        Ret ret_;
        String result_;
        boolean carryOn = true;

        // 認識完了時の処理
        // (UIスレッドで動作させる為にRunnable()を使用している)
        final Runnable notifyFinished = new Runnable() {
            public void run() {
//                try {
//                    // 念のためスレッドの完了を待つ
//                    controller_.join();
//                } catch (InterruptedException e) {
//                }
                if (controller_.result_ == null) {
                    return;
                }
                String target = controller_.result_;
                int count = 0;
                for (String yummy : yummies) {
                    count += (target.length() - target.replaceAll(yummy, "").length()) / yummy.length();
                }

                if (controller_.carryOn) {
                    //進捗アイコンを表示
                    reDrawProgress();
                } else {
//停止時の処理
//                // 結果を更新
//                // FIXME これはテストコードです。
                    int yummyCount = 20;
                    updateEating(yummyCount, eatingId);
                    Intent intent = new Intent(EatingActivity.this, ResultActivity.class);
                    intent.putExtra("eatingId", eatingId);
                    intent.putExtra("yummyCount", yummyCount);
                    startActivity(intent);
                }
            }
        };


        // 認識処理
        @Override
        public void run() {
            result_ = "";
            try {
                final ConstructorEntity construct = new ConstructorEntity();
                construct.setContext(activity_);

                // TODO apiキーをコミットしないこと
                // 別途発行されるAPIキーを設定してください(以下の値はダミーです)
                construct.setApiKey("70473974327a764c314337636a624e5131326a6b332e6d4257367446344d30354d745064572e6452696132");

                construct.setSpeechTime(10000);
                construct.setRecordSize(240);
                construct.setRecognizeTime(10000);

                // インスタンス生成
                // (thisは FSRServiceEventListenerをimplementsしている。)
                if( null == fsr_ ){
                    fsr_ = new FSRServiceOpen(this, this, construct);
                }

                if (!carryOn) {
                    return;
                }
                // connect
                fsr_.connectSession(backendType_);
                event_CompleteConnect_.wait_();
                if( ret_ != Ret.RetOk ){
                    Exception e = new Exception("filed connectSession.");
                    throw e;
                }

                while(carryOn) {
                    result_ = execute();
                    handler_.post(notifyFinished);
                }

                // 切断
                fsr_.disconnectSession(backendType_);
                event_CompleteDisconnect_.wait_();

                if (fsr_ != null) {
                    fsr_.destroy();
                    fsr_=null;
                }

            } catch (Exception e) {
                result_ = "(error)";
                e.printStackTrace();
            }
        }

        /**
         * 認識処理
         *
         * 現状は毎回インスタンス生成～destroy()を実施しているが、
         * 繰り返し認識させる場合は、以下のように制御した方がオーバーヘッドが少なくなる
         * アプリ起動時：インスタンス生成～connectSession()
         * 認識要求時　：startRecognition()～getSessionResult()
         * アプリ終了時：destroy()
         *
         * @throws Exception
         */
        public String execute() throws Exception {

            try{
                // 認識開始
                event_EndRecognition_.initialize();

                final StartRecognitionEntity startRecognitionEntity = new StartRecognitionEntity();
                startRecognitionEntity.setAutoStart(true);
                startRecognitionEntity.setAutoStop(false);				// falseにする場合はUIからstopRecognition()実行する仕組みが必要
                startRecognitionEntity.setVadOffTime((short) 500);
                startRecognitionEntity.setListenTime(0);
                startRecognitionEntity.setLevelSensibility(1);

                // 認識開始
                fsr_.startRecognition(backendType_, startRecognitionEntity);

                // 認識完了待ち
                event_EndRecognition_.wait_();

                RecognizeEntity recog = fsr_.getSessionResultStatus(backendType_);
                String result="(no result)";
                if( recog.getCount()>0 ){
                    ResultInfoEntity info=fsr_.getSessionResult(backendType_, 1);
                    result = info.getText();
                }

                return result;
            } catch (Exception e) {
                showErrorDialog(e);
                throw e;
            }
        }

        public void sendStopSignal() {
            carryOn = false;
        }

        @Override
        public void notifyAbort(Object arg0, AbortInfoEntity arg1) {
            Exception e = new Exception("Abort!!");
            showErrorDialog(e);
        }

        @Override
        public void notifyEvent(final Object appHandle, final EventType eventType, final BackendType backendType, Object eventData) {

            switch(eventType){

                case CompleteConnect:
                    // 接続完了
                    ret_ = (Ret)eventData;
                    event_CompleteConnect_.notify_();
                    break;

                case CompleteDisconnect:
                    // 切断完了
                    event_CompleteDisconnect_.notify_();
                    break;

                case NotifyEndRecognition:
                    // 認識完了
                    event_EndRecognition_.notify_();
                    break;

                case CompleteStop:
                    // 停止完了
                    event_StopRecognition_.notify_();
                    break;

                case NotifyLevel:
                    // レベルメータ更新
                    int level = (Integer)eventData;
//                    progressLevel_.setProgress(level);
                    break;
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eating);

        handler_ = new Handler();
        activity_ = this;

        controller_ = new fsrController();
        controller_.start();

        // ごちそうさまボタン
        Button endButton = (Button) findViewById(R.id.end_button);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ここに聞き取りをSTOPする処理が入るはず
                controller_.sendStopSignal();

//                // 結果を更新
//                // FIXME これはテストコードです。
//                int yummyCount = 20;
//                updateEating(yummyCount, eatingId);
//                Intent intent = new Intent(EatingActivity.this, ResultActivity.class);
//                intent.putExtra("eatingId", eatingId);
//                intent.putExtra("yummyCount", yummyCount);
//                startActivity(intent);
            }
        });

        // 結果テーブルを作成
        long result = insertEating();
        if (result != -1) {
            this.eatingId = result;
        }

        // デフォルト進捗
        reDrawProgress();
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
        values.put(DatabaseHelper.EATING_UPDATED_AT, now);
        values.put(DatabaseHelper.EATING_END_TIME, now);
        values.put(DatabaseHelper.EATING_YUMMY_COUNT, yummyCount);
        String whereStr = "id = " + String.valueOf(eatingId);
        long result = -1;
        if (sqLiteDatabase != null) {
            result = sqLiteDatabase.update(DatabaseHelper.EATING_TABLE, values, whereStr, null);
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

    /**
     * エラーダイアログを表示する
     */
    public final void showErrorDialog(Exception e) {
        final android.app.Activity activity = this;
        final String text=(e.getCause()!=null)?e.getCause().toString():e.toString();
        final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle("Error");
        ad.setMessage(text);
        ad.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int whichButton) {
                activity.setResult(Activity.RESULT_OK);
                activity.finish();
            }
        });
        ad.create();
        ad.show();
    }

    public void reDrawProgress(){
        prview = (ProgressView) findViewById(R.id.progress_view);
        //ループだとinvalidateが効かない...
//        for(int i=0; i < 10; i++){
            prview.invalidate();
//        }
    }
}
