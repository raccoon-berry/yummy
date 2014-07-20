package berry.yummy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ProgressView extends View {

    private Bitmap prgImage1 = null;
    private Bitmap prgImage2 = null;

    private int left = 230;
    private int top = 500;
    private int incrementPoint = 250;
    private int maxTop = 30;
    private String baseColor = "#ff8c00";
    private int imagePath = R.drawable.smile_girl;

    private float prgBarStart = 700;
    private float prgBarEnd = 30;
    private float prgBarLeft = 370;
    private String prgBarColor = "#ff4500";

    private int starLeft = 20;
    private int starTop = 700;
    private int starPath = R.drawable.star;

    private int progressGrade = 5;//5つに分類

    private int barWidth = 60;//プログレスバー幅
    private float barX = 50;//プログレスバーX軸始点
    private int maxBarX = 650;//プログレスバーX軸終点
    private float barY = 80;//プログレスバーY軸始点
    private float tmpBarX = 50;

    public ProgressView(Context context){
        super(context);
        initial(context);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context,attrs);
        initial(context);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs, defStyle);
        initial(context);
    }

    public void initial(Context context){
        Resources res = context.getResources();
        prgImage1 = BitmapFactory.decodeResource(res, imagePath);
        prgImage2 = BitmapFactory.decodeResource(res, starPath);

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas c){
        super.onDraw(c);
        c.drawColor(Color.parseColor(baseColor));

        Paint pBaseLine = new Paint();
        pBaseLine.setStrokeWidth(barWidth);
        pBaseLine.setColor(Color.WHITE);
        c.drawLine(barX, barY, maxBarX, barY, pBaseLine);

        Paint pLine = new Paint();
        pLine.setStrokeWidth(barWidth);
        pLine.setColor(Color.parseColor(prgBarColor));

        c.drawLine(barX, barY, tmpBarX, barY, pLine);
        if(tmpBarX < maxBarX){
            //tmpBarX = tmpBarX + (maxBarX - barX) / progressGrade ;
            tmpBarX = tmpBarX + 120;
        }

    }

}
