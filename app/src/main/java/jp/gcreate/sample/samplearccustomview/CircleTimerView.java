package jp.gcreate.sample.samplearccustomview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 2014/07/05
 */
public class CircleTimerView extends View{

    private RectF mRectF = new RectF();
    private Paint mPaint = new Paint();
    private Paint mPaintBase = new Paint();
    private float mArc = 300f;

    public CircleTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint.setStrokeWidth(20f);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.rgb(40, 8, 0));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintBase.setStrokeWidth(20f);
        mPaintBase.setAntiAlias(true);
        mPaintBase.setColor(Color.rgb(200,200,200));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mRectF.set(getLeft(),getTop(),getWidth(),getHeight());
        canvas.drawArc(mRectF,270f,360f,true,mPaintBase);
        canvas.drawArc(mRectF,270f,mArc,false,mPaint);
    }

    public void rewriteCircle(float arc){
        mArc = arc;
        invalidate();
    }

    public void onFinishedToRestart(){
        mArc = 360f;
        invalidate();
    }
}
