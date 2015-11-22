package jp.gcreate.sample.samplearccustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 2014/07/05
 */
public class CircleTimerView extends View {

    private static final float DEFAULT_MARGIN = 20f;
    private static final int DEFAULT_BASE_COLOR = Color.rgb(200, 200, 200);
    private static final int DEFAULT_BORDER_COLOR = Color.rgb(40, 8, 0);
    private RectF mBarRectF = new RectF();
    private RectF mButtonRectF = new RectF();
    private Paint mPaint = new Paint();
    private Paint mPaintBase = new Paint();
    private Paint mButtonPaint = new Paint();
    private Paint mTextPaint = new Paint();
    private float mArc = 300f;
    private float mMargin;
    private float mButtonMargin;

    public CircleTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTimerView);
        mMargin = a.getDimension(R.styleable.CircleTimerView_margin,
                getResources().getDisplayMetrics().density * DEFAULT_MARGIN);
        float baseWidth = a.getDimension(R.styleable.CircleTimerView_base_width, 15f);
        float borderWidth = a.getDimension(R.styleable.CircleTimerView_border_width, 20f);
        int baseColor = a.getColor(R.styleable.CircleTimerView_base_color, DEFAULT_BASE_COLOR);
        int borderColor = a.getColor(R.styleable.CircleTimerView_border_color, DEFAULT_BORDER_COLOR);
        float textSize = a.getDimension(R.styleable.CircleTimerView_button_text_size,
                getResources().getDisplayMetrics().density * 20f);
        int textColor = a.getColor(R.styleable.CircleTimerView_button_text_color, Color.BLACK);
        mButtonMargin = a.getDimension(R.styleable.CircleTimerView_button_margin,
                getResources().getDisplayMetrics().density * 10f);
        a.recycle();

        mPaint.setStrokeWidth(borderWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(borderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaintBase.setStrokeWidth(baseWidth);
        mPaintBase.setAntiAlias(true);
        mPaintBase.setColor(baseColor);
        mPaintBase.setStyle(Paint.Style.STROKE);
        mButtonPaint.setAntiAlias(true);
        mButtonPaint.setColor(borderColor);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setAntiAlias(true);
        mMargin += Math.max(baseWidth, borderWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mBarRectF.set(0 + mMargin,
                0 + mMargin,
                canvas.getWidth() - mMargin,
                canvas.getHeight() - mMargin);
        mButtonRectF.set(0 + mMargin + mButtonMargin,
                0 + mMargin + mButtonMargin,
                canvas.getWidth() - mMargin - mButtonMargin,
                canvas.getHeight() - mMargin - mButtonMargin);
        canvas.drawArc(mBarRectF, 270f, 360f, false, mPaintBase);
        canvas.drawArc(mBarRectF, 270f, mArc, false, mPaint);
        canvas.drawOval(mButtonRectF, mButtonPaint);
        float textWidth =  mTextPaint.measureText("test");
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;
        float textX = (canvas.getWidth() - textWidth) / 2;
        float textY = (canvas.getHeight() + textHeight) / 2;
        canvas.drawText("test", textX, textY, mTextPaint);
    }

    public void rewriteCircle(float arc){
        mArc = arc;
        invalidate();
    }

    public void onFinishedToRestart(){
        mArc = 360f;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spec = Math.min(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(spec, spec);
    }
}
