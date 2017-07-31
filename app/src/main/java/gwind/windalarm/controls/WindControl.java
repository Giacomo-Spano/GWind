package gwind.windalarm.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;


/**
 * Created by giacomo on 28/02/14.
 */
public class WindControl extends LinearLayout {

    private String Id;
    //private String SpotName;

    public WindControl(Context context) {
        super(context);
    }

    public WindControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindControl(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float sinval[] = {0,0.346F,0.649F,0.938F,1F,0.938F,0.649F,0.346F,0,-0.346F,-0.649F,-0.938F,-1F,-0.938F,-0.649F,-0.346F};
    private float cosval[] = {1F,0.938F,0.649F,0.346F,0,-0.346F,-0.649F,-0.938F,-1F,-0.938F,-0.649F,-0.346F,0,0.346F,0.649F,0.938F};
    private float lineStrokesize = 6F;

    private int maxpower = 10;
    private int power = 5;
    private float width = 200;
    private float height = 200;
    private Double mAngle = 0.0;
    private String mDirectionSymbol = "--";
    private Double mPower = 5.0;

    public void setDirection(Double degangle, String symbol) {
        mAngle = degangle;
        mDirectionSymbol = symbol;
        invalidate();
    }

    public Double getDirection() {
        return mAngle;
    }

    public void setPower(Double power) {
        mPower = power;
        invalidate();
    }

    public Double getPower() {
        return mPower;
    }

    float getCX() {
        return width / 2;
    }
    float getCY() {
        return height / 2;
    }
    float getCircleRadius() { // raggio cerchio freccia

        return 0.3F * width / 2;
    }
    float getLineLength() {// lunghezza coda
        return 0.85F * width - 2 * getCircleRadius();
    }
    float getLineFlagLength() {// lunghezza flag coda
        return getLineLength() / 5;
    }
    float getFlagDistance() {// distanza flag coda
        return getLineLength() / 6;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawArrow(canvas,mAngle,mPower);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        this.height = h;
        this.width = h;//w;
        super.onSizeChanged(w, h, oldw, oldh);
    }


    private void drawArrow(Canvas canvas, Double degangle, Double power) {

        /*final RectF rectF = new RectF();
        final Paint paint = new Paint();
        paint.setARGB(128, 255, 255, 255);
        rectF.set(0,0, getMeasuredWidth(), getMeasuredHeight());
        canvas.drawRect(drawRect, paint);*/
        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));


        canvas.drawColor(Color.WHITE);
        Paint p = new Paint();
        // smooths
        p.setAntiAlias(true);
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(lineStrokesize);
        // opacity
        //p.setAlpha(0x80); //

        if (degangle < 0F || degangle >= 360) return;

        int angle = (int)(degangle/22.5);

        /*canvas.drawLine(0,0,width,0,p);
        canvas.drawLine(0,height,width,height,p);
        canvas.drawLine(0,0,0,height,p);
        canvas.drawLine(width,0,width,height,p);

        canvas.drawLine(width/2,0,width/2,height,p);
        canvas.drawLine(0,height/2,width,height/2,p);
*/

        float totalelength = 2 * getCircleRadius() + getLineLength(); // lunghezza totale cerchio + coda
        // calcolo centro cerchio freccia
        float cx1 = getCX() - (totalelength / 2 - getCircleRadius()) * cosval[angle];
        float cy1 = getCY() + (totalelength / 2 - getCircleRadius()) * sinval[angle];
        canvas.drawCircle(cx1, cy1, getCircleRadius(), p);

        float length = getLineLength();// - lineStrokesize;
        float lx1 = cx1+(getCircleRadius()+length)*cosval[angle];
        float ly1 = cy1-(getCircleRadius()+length)*sinval[angle];
        float lx2 = cx1+getCircleRadius()*cosval[angle];
        float ly2 = cy1-getCircleRadius()*sinval[angle];


        canvas.drawLine(lx2, ly2,lx1, ly1,p);
        //p.setColor(Color.RED);
        int k = 1;
        for (int i = 0; i < power / 3; i++) {

            float lx3 = lx1 + k * getLineFlagLength() * sinval[angle];
            float ly3 = ly1 - k * getLineFlagLength() * (-cosval[angle]);
            canvas.drawLine(lx1, ly1, lx3, ly3, p);

            if (k == 1) {
                k = 2;
            } else {
                k = 1;
                length -= getFlagDistance();
                lx1 = cx1+(getCircleRadius()+length)*cosval[angle];
                ly1 = cy1-(getCircleRadius()+length)*sinval[angle];

            }
        }

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        //canvas.drawPaint(textPaint);

        // calcolo centro scritta 1
        int angleText = angle - 4; // ruota di 90 gradi = 22.5 * 4 /22.5
        if (angleText < 0) {
            angleText = sinval.length + angleText;
        }
        float cxText = getCX() - (totalelength / 2 - getCircleRadius()) * cosval[angleText];
        float cyText = getCY() + (totalelength / 2 - getCircleRadius()) * sinval[angleText];
        //canvas.drawCircle(cxText, cyText, getCircleRadius(), p);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(40);
        Rect bounds = new Rect();

        if (mDirectionSymbol != null) {
            String textToDraw = mDirectionSymbol;//+" "+mAngle;
            textPaint.getTextBounds(textToDraw, 0, textToDraw.length(), bounds);
        }
        //canvas.drawText(textToDraw, cxText, cyText + (bounds.bottom-bounds.top)/2, textPaint);


/*        // calcolo centro scritta2
        angleText = angle + 4; // ruota di 90 gradi = 22.5 * 4 /22.5
        if (angleText >= sinval.length) {
            angleText = angleText - sinval.length;
        }
        cxText = getCX() - (totalelength / 2 - getCircleRadius()) * cosval[angleText];
        cyText = getCY() + (totalelength / 2 - getCircleRadius()) * sinval[angleText];
        //canvas.drawCircle(cxText, cyText, getCircleRadius(), p);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(35);
        bounds = new Rect();
        textToDraw = ""+mPower+"km/h";
        textPaint.getTextBounds(textToDraw, 0, textToDraw.length(), bounds);
        //canvas.drawText(textToDraw, cxText, cyText + (bounds.bottom-bounds.top)/2, textPaint);


        //canvas.drawText("Some Text", cxText, cyText + 70/2, textPaint);*/
    }
}