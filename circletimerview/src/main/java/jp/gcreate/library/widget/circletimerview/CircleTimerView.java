package jp.gcreate.library.widget.circletimerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.concurrent.*;

/**
 * 2014/07/05
 */
public class CircleTimerView extends RelativeLayout {
    private static final String TAG = CircleTimerView.class.getSimpleName();
    private static final float DEFAULT_MARGIN = 20f;
    private static final int DEFAULT_BASE_COLOR = Color.rgb(200, 200, 200);
    private static final int DEFAULT_BORDER_COLOR = Color.rgb(40, 8, 0);
    private RectF mBarRectF = new RectF();
    private Paint mPaint = new Paint();
    private Paint mPaintBase = new Paint();
    private float mArc = 10f;
    private float mMargin;
    private float mButtonMargin;
    private String mButtonText;
    private boolean mIs1to1 = true;
    private HandlerThread animationThread;
    private Handler toAnimation;
    private Handler toUi;
    private Button mInnerButton;
    private Interpolator interpolator;
    private final Runnable animation = new Runnable() {
        @Override
        public void run() {
            try {
                final float startpoint = mArc;
                final float fromTo = 360f - mArc;
                final long startTime = System.currentTimeMillis();
                final long endTime = TimeUnit.MILLISECONDS.toMillis(300);
                long elapsed = System.currentTimeMillis() - startTime;
                long lap = 0;
                while(elapsed < endTime) {
                    elapsed = System.currentTimeMillis() - startTime;
                    // post to percentage 0 - 100
                    // elapsed / endTime is time percentage
                    if (Thread.currentThread().isInterrupted()){
                        throw new InterruptedException("animation canceled");
                    }
                    if (elapsed - lap > 10) {
                        float accel = interpolator.getInterpolation((float)elapsed / (float)endTime);
                        float percentage = accel * fromTo + startpoint;
                        Log.d(TAG, "animation rewrite circle arc to " + percentage +
                                " compute[" + accel + "*" + fromTo + "+" + startpoint + "]" +
                                " at elapsed " + elapsed);
                        Message msg = Message.obtain(toUi, 1, percentage);
                        toUi.sendMessage(msg);
                        lap = elapsed;
                    }
                }
                Message msg = toUi.obtainMessage(1, 360f);
                toUi.sendMessage(msg);
            } catch (InterruptedException e) {
                Log.d(TAG, "animation canceled so animation thread interrupted.");
            }
        }
    };

    public CircleTimerView(Context context) {
        this(context, null);
    }

    public CircleTimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CircleTimerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTimerView);
        mIs1to1 = a.getInt(R.styleable.CircleTimerView_aspect, 0) == 0;
        mMargin = a.getDimension(R.styleable.CircleTimerView_margin,
                getResources().getDisplayMetrics().density * DEFAULT_MARGIN);
        float baseWidth = a.getDimension(R.styleable.CircleTimerView_base_width, 15f);
        float borderWidth = a.getDimension(R.styleable.CircleTimerView_border_width, 20f);
        int baseColor = a.getColor(R.styleable.CircleTimerView_base_color, DEFAULT_BASE_COLOR);
        int borderColor = a.getColor(R.styleable.CircleTimerView_border_color, DEFAULT_BORDER_COLOR);
        mButtonText = a.getString(R.styleable.CircleTimerView_button_text);
        float textSize = a.getDimension(R.styleable.CircleTimerView_button_text_size,
                getResources().getDisplayMetrics().density * 20f);
        int textColor = a.getColor(R.styleable.CircleTimerView_button_text_color, Color.BLACK);
        mButtonMargin = a.getDimension(R.styleable.CircleTimerView_button_margin,
                getResources().getDisplayMetrics().density * 10f);
        int interporlatorResId = a.getResourceId(R.styleable.CircleTimerView_interporlator, 0);
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
        if (interporlatorResId != 0){
            interpolator = AnimationUtils.loadInterpolator(getContext(), interporlatorResId);
        }else{
            interpolator = new AccelerateInterpolator();
        }

        View view = inflate(context, R.layout.layout_circle_timer_view, this);
        mInnerButton = (Button) view.findViewById(R.id.center_button);
        MarginLayoutParams params = (MarginLayoutParams) mInnerButton.getLayoutParams();
        int m = (int)(mMargin + mButtonMargin);
        params.setMargins(m, m, m, m);
        mInnerButton.setLayoutParams(params);
        setText(mButtonText);
        setTextSize(textSize);
        setTextColor(textColor);

        animationThread = new HandlerThread("animation");
        animationThread.start();
        toAnimation = new Handler(animationThread.getLooper());
        toUi = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Float percentage = (Float)msg.obj;
                rewriteCircle(percentage);
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mBarRectF, 270f, 360f, false, mPaintBase);
        canvas.drawArc(mBarRectF, 270f, mArc, false, mPaint);
        super.onDraw(canvas);
    }

    public void rewriteCircle(float arc){
        mArc = arc;
        invalidate();
    }

    public void onFinishedToRestart(){
        toAnimation.post(animation);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
        if (mIs1to1) {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(size, widthMode);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, heightMode);
        }
        mBarRectF.set(0 + mMargin,
                0 + mMargin,
                MeasureSpec.getSize(widthMeasureSpec) - mMargin,
                MeasureSpec.getSize(heightMeasureSpec) - mMargin);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setText(String text){
        mInnerButton.setText(text);
    }

    public void setTextSize(float size){
        mInnerButton.setTextSize(size);
    }

    public void setTextColor(int color){
        mInnerButton.setTextColor(color);
    }

    public void setOnClickListener(OnClickListener listener){
        mInnerButton.setOnClickListener(listener);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        toAnimation.removeMessages(1);
        animationThread.interrupt();
    }
}
