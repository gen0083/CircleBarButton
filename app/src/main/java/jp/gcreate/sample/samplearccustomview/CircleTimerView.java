package jp.gcreate.sample.samplearccustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * 2014/07/05
 */
public class CircleTimerView extends RelativeLayout {

    private static final float DEFAULT_MARGIN = 20f;
    private static final int DEFAULT_BASE_COLOR = Color.rgb(200, 200, 200);
    private static final int DEFAULT_BORDER_COLOR = Color.rgb(40, 8, 0);
    private RectF mBarRectF = new RectF();
    private Rect mButtonRect = new Rect();
    private Paint mPaint = new Paint();
    private Paint mPaintBase = new Paint();
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
        mMargin += Math.max(baseWidth, borderWidth);

        View view = inflate(context, R.layout.layout_circle_timer_view, this);
        Button button = (Button) view.findViewById(R.id.center_button);
        MarginLayoutParams params = (MarginLayoutParams) button.getLayoutParams();
        int m = (int)(mMargin + mButtonMargin);
        params.setMargins(m, m, m, m);
        button.setLayoutParams(params);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mBarRectF, 270f, 360f, false, mPaintBase);
        canvas.drawArc(mBarRectF, 270f, mArc, false, mPaint);
//        super.onDraw(canvas);
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
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, widthMode);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, heightMode);
        int margin = (int) (mMargin + mButtonMargin);
        mButtonRect.set(margin, margin, size - margin, size - margin);
        mBarRectF.set(0 + mMargin,
                0 + mMargin,
                size - mMargin,
                size - mMargin);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
