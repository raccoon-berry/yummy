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

//public class ProgressView extends View {
public class ProgressView extends View {

    private Bitmap prgImage1 = null;
    private Bitmap prgImage2 = null;

    private int left = 300;
    private int top = 600;
    private int incrementPoint = 100;
    private int maxTop = 30;
    private String baseColor = "#f0e68c";
    private int imagePath = R.drawable.smile_girl_small;

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


        //進捗画像
        Paint p = new Paint();
        if(prgImage1 != null) {
            if (top > maxTop) {
                c.drawBitmap(prgImage1, left, top, p);
//inValidateのfor文が効かないときの代替手段
//                top = top - 1;
                top = top - incrementPoint;
            } else {
                c.drawBitmap(prgImage1, left, maxTop, p);
            }
        }
    }

}
