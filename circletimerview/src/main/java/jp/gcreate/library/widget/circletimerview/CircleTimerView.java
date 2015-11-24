package jp.gcreate.library.widget.circletimerview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
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
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;
import java.util.concurrent.*;

/**
 * 2014/07/05
 */
public class CircleTimerView extends RelativeLayout {
    private static final String TAG = CircleTimerView.class.getSimpleName();
    private static final float DEFAULT_MARGIN = 20f;
    private static final float DEFAULT_TEXT_SIZE = 18f;
    private static final float DEFAULT_BAR_WIDTH = 15f;
    private static final int MSG_REDRAW = 1;
    private Resources res;
    private Resources.Theme theme;
    private RectF barRectF = new RectF();
    private Paint barPaint = new Paint();
    private Paint barBasePaint = new Paint();
    private float degree = 10f;
    private float viewMargin;
    private boolean isDebug;
    private boolean isKeepAspect;
    private HandlerThread animationThread;
    private Handler toAnimationHandler;
    private Handler toUiHandler;
    private Button innerButton;
    private Interpolator interpolator;
    private boolean isAnimation;
    private final Runnable animation = new Runnable() {
        @Override
        public void run() {
            try {
                final float startpoint = degree;
                final float fromTo = 360f - degree;
                final long startTime = System.currentTimeMillis();
                final long endTime = TimeUnit.MILLISECONDS.toMillis(300);
                long elapsed = System.currentTimeMillis() - startTime;
                long lap = 0;
                logd("circle bar animate start.");
                while(elapsed < endTime) {
                    elapsed = System.currentTimeMillis() - startTime;
                    // post to percentage 0 - 100
                    // elapsed / endTime is time percentage
                    if (Thread.currentThread().isInterrupted()){
                        throw new InterruptedException("animation canceled");
                    }
                    if (elapsed - lap > 10) {
                        float percentage = interpolator.getInterpolation((float)elapsed / (float)endTime);
                        float targetDegree = percentage * fromTo + startpoint;
                        toUiHandler.sendMessage(toUiHandler.obtainMessage(MSG_REDRAW, targetDegree));
                        lap = elapsed;
                        logd("circle bar animate to : " + targetDegree);
                    }
                }
                toUiHandler.sendMessage(toUiHandler.obtainMessage(MSG_REDRAW, 360f));
                isAnimation = false;
                logd("circle bar animate finished.");
            } catch (InterruptedException e) {
                logd("animation thread interrupted.");
                isAnimation = false;
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
        // set style from xml
        final float density = getResources().getDisplayMetrics().density;
        res = getResources();
        theme = getContext().getTheme();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTimerView);
        isDebug = a.getBoolean(R.styleable.CircleTimerView_debug, false);
        isKeepAspect = a.getBoolean(R.styleable.CircleTimerView_keep_aspect, true);
        viewMargin = a.getDimension(R.styleable.CircleTimerView_margin,
                getResources().getDisplayMetrics().density * DEFAULT_MARGIN);
        float baseWidth = a.getDimension(R.styleable.CircleTimerView_base_width,
                density * DEFAULT_BAR_WIDTH);
        float borderWidth = a.getDimension(R.styleable.CircleTimerView_border_width,
                density * DEFAULT_BAR_WIDTH);
        int barBaseColor = a.getColor(R.styleable.CircleTimerView_base_color,
                getColorFromResourceId(R.color.barBaseDefault));
        int barColor = a.getColor(R.styleable.CircleTimerView_border_color,
                getColorFromResourceId(R.color.barDefault));
        String buttonText = a.getString(R.styleable.CircleTimerView_button_text);
        // text size will compute to pixel at setTextSize()
        float textSize = a.getDimension(R.styleable.CircleTimerView_button_text_size,
                DEFAULT_TEXT_SIZE);
        int textColor = a.getColor(R.styleable.CircleTimerView_button_text_color,
                getColorFromResourceId(android.R.color.black));
        float mButtonMargin = a.getDimension(R.styleable.CircleTimerView_button_margin,
                density * 10f);
        int interpolatorResId = a.getResourceId(R.styleable.CircleTimerView_interpolator, 0);
        int buttonResId = a.getResourceId(R.styleable.CircleTimerView_button_background, R.drawable.button_selector);
        a.recycle();

        // initialize fields
        barPaint.setStrokeWidth(borderWidth);
        barPaint.setAntiAlias(true);
        barPaint.setColor(barColor);
        barPaint.setStyle(Paint.Style.STROKE);
        barBasePaint.setStrokeWidth(baseWidth);
        barBasePaint.setAntiAlias(true);
        barBasePaint.setColor(barBaseColor);
        barBasePaint.setStyle(Paint.Style.STROKE);
        viewMargin += Math.max(baseWidth, borderWidth); // bar is drawn over own width
        if (interpolatorResId != 0){
            interpolator = AnimationUtils.loadInterpolator(getContext(), interpolatorResId);
        }else{
            interpolator = new LinearInterpolator();
        }

        // inflate view
        View view = inflate(context, R.layout.layout_circle_timer_view, this);
        innerButton = (Button) view.findViewById(R.id.center_button);
        MarginLayoutParams params = (MarginLayoutParams) innerButton.getLayoutParams();
        int m = (int)(viewMargin + mButtonMargin);
        params.setMargins(m, m, m, m);
        innerButton.setLayoutParams(params);
        setText(buttonText);
        setTextSize(textSize);
        setTextColor(textColor);
        setBackground(buttonResId);
        this.setBackgroundResource(0);
        this.setBackgroundColor(Color.argb(0, 255, 255, 255));

        // initialize thread for animation
        animationThread = new HandlerThread("animation");
        animationThread.start();
        toAnimationHandler = new Handler(animationThread.getLooper());
        toUiHandler = new ToUiHandler(this);
    }

    private int getColorFromResourceId(int resId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return res.getColor(resId, theme);
        }else{
            return res.getColor(resId);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(barRectF, 270f, 360f, false, barBasePaint);
        canvas.drawArc(barRectF, 270f, degree, false, barPaint);
    }

    public void rewriteCircle(float angle){
        degree = angle;
        invalidate();
    }

    public void onFinishedToRestart(){
        if (!isAnimation) {
            isAnimation = true;
            toAnimationHandler.post(animation);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isKeepAspect) {
            int size = Math.min(MeasureSpec.getSize(widthMeasureSpec),
                                MeasureSpec.getSize(heightMeasureSpec));
            int widthMode  = MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            widthMeasureSpec  = MeasureSpec.makeMeasureSpec(size, widthMode);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(size, heightMode);
        }
        barRectF.set(0 + viewMargin,
                     0 + viewMargin,
                     MeasureSpec.getSize(widthMeasureSpec)  - viewMargin,
                     MeasureSpec.getSize(heightMeasureSpec) - viewMargin);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setText(String text){
        innerButton.setText(text);
    }

    public void setTextSize(float size){
        innerButton.setTextSize(size);
    }

    public void setTextColor(int color){
        innerButton.setTextColor(color);
    }

    public void setOnClickListener(OnClickListener listener){
        innerButton.setOnClickListener(listener);
    }

    public void setBackground(int resId){
        innerButton.setBackgroundResource(resId);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        toAnimationHandler.removeMessages(MSG_REDRAW);
        animationThread.interrupt();
    }

    private void logd(String msg){
        if (isDebug){
            Log.d(TAG, msg);
        }
    }

    static class ToUiHandler extends Handler{
        private WeakReference<CircleTimerView> target;

        public ToUiHandler(CircleTimerView instance){
            target = new WeakReference<>(instance);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REDRAW) {
                float degree = (Float) msg.obj;
                CircleTimerView view = target.get();
                if (view != null) {
                    view.rewriteCircle(degree);
                }
            }
        }
    }
}
