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
    private String baseColor = "#f0e68c";
    private int imagePath = R.drawable.smile_girl;

    private float prgBarStart = 700;
    private float prgBarEnd = 30;
    private float prgBarLeft = 370;
    private String prgBarColor = "#228b22";

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

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas c){
        super.onDraw(c);
        c.drawColor(Color.parseColor(baseColor));

        //進捗バー
        Paint pLine = new Paint();
        pLine.setStrokeWidth(30);
        pLine.setColor(Color.parseColor(prgBarColor));
        c.drawLine(prgBarLeft, prgBarStart, prgBarLeft, prgBarEnd, pLine);

        //進捗画像
        Paint p = new Paint();
        if(prgImage1 != null) {
            if (top > maxTop) {
                c.drawBitmap(prgImage1, left, top, p);
                //inValidateのfor文が効かないときので代替手段
                //top = top - 1;
                top = top - incrementPoint;
            } else {
                c.drawBitmap(prgImage1, left, maxTop, p);
            }
        }
    }

}
