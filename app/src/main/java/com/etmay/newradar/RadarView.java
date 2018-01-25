package com.etmay.newradar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by Administrator on 2018/1/5.
 */

public class RadarView extends SurfaceView implements Runnable, View.OnClickListener {
    private Paint paint;
    private SurfaceHolder holder;
    private boolean isDrawing;
    private boolean canDraw = true;
    private int maxRadius;
    private int bgColor;
    private int lineColor;
    private int textSize;
    private int textColor;
    private PointF[][] points = new PointF[5][8];
    private PointF center;

    private static int frameLineMoveSpeed = 4;
    private float topMoveX;
    private float topMoveY;
    private float rightMoveX;
    private float rightMoveY;
    private float topRightMoveX;
    private float topRightMoveY;
    private float rightBottomMoveX;
    private float rightBottomMoveY;
    private float bottomMoveX;
    private float bottomMoveY;
    private float leftBottomMoveX;
    private float leftBottomMoveY;
    private float leftMoveX;
    private float leftMoveY;
    private float topLeftMoveX;
    private float topLeftMoveY;

    private int alpha = 9;

    private static float angelRate;

    private static final String[] titles = new String[] {
            "甜美", "经典", "浪漫", "自然", "时尚", "文雅", "摩登", "帅气"
    };

    private float[] percentValues; // length must 8
    private PointF[] percentPoints = new PointF[8];
    private PointF indicatorPointTop;
    private PointF indicatorPointTopRight;
    private PointF indicatorPointRight;
    private PointF indicatorPointBottomRight;
    private PointF indicatorPointBottom;
    private PointF indicatorPointBottomLeft;
    private PointF indicatorPointLeft;
    private PointF indicatorPointTopLeft;
    private Path path;
    private static int INDICATOR_SPEED = 5;
    private static final int SIDE_MOVE_SPEED = 6;
    // Side lines anim's variables.
    private PointF sideLine1StartPoint;
    private PointF sideLine2StartPoint;
    private PointF sideLine3StartPoint;
    private PointF sideLine4StartPoint;
    private PointF sideLine5StartPoint;
    private PointF sideLine1MovePoint1;
    private PointF sideLine1MovePoint2;
    private PointF sideLine1MovePoint3;
    private PointF sideLine1MovePoint4;
    private PointF sideLine1MovePoint5;
    private PointF sideLine1MovePoint6;
    private PointF sideLine1MovePoint7;
    private PointF sideLine1MovePoint8;

//    private PointF sideLine2MovePoint;
//    private PointF sideLine3MovePoint;
//    private PointF sideLine4MovePoint;
//    private PointF sideLine5MovePoint;

    private Path slidLinePath1;
    private Path slidLinePath2;
    private Path slidLinePath3;
    private Path slidLinePath4;
    private Path slidLinePath5;

    /**
     * 外部调用设置各个方向的百分比
     * @param percentValues
     */
    public void setPercentValues(float[] percentValues) {
        this.percentValues = percentValues;
        initData();
    }

    public RadarView(Context context) {
        super(context);
        init(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSize <= 0 || heightSize <= 0) {
            widthSize = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 600, getResources().getDisplayMetrics());
//            widthSize = ((Activity)getContext()).getResources().getDisplayMetrics().widthPixels;
            heightSize = widthSize;
//            heightSize = ((Activity)getContext()).getResources().getDisplayMetrics().heightPixels;
        }
        int widthSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        int heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthSpec, heightSpec);
    }

    private boolean isInited;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (!isInited) {
            isInited = true;
            setPercentValues(new float[]{0.83f, 0.5f, 0.54f, 0.43f
                    , 0.33f, 0.28f, 0.29f, 0.33f});
        }
    }

    private void initData() {
        alpha = 9;
        angelRate = (float) (Math.sqrt(2) / 2);
        center = new PointF(getMeasuredWidth() / 2, getMeasuredHeight() / 2); // 圆心
        // 从正上方开始顺时针
        points[0][0] = new PointF(center.x, center.y - maxRadius); // top
        points[0][1] = new PointF((float) (center.x + maxRadius * angelRate), (float) (center.y - maxRadius * angelRate));
        points[0][2] = new PointF(center.x + maxRadius, center.y); // right
        points[0][3] = new PointF((float) (center.x + maxRadius * angelRate), (float) (center.y + maxRadius * angelRate));
        points[0][4] = new PointF(center.x, center.y + maxRadius); // bottom
        points[0][5] = new PointF((float) (center.x - maxRadius * angelRate), (float) (center.y + maxRadius * angelRate));
        points[0][6] = new PointF(center.x - maxRadius, center.y); // left
        points[0][7] = new PointF((float) (center.x - maxRadius * angelRate), (float) (center.y - maxRadius * angelRate));

        float deltaXY = maxRadius / 5.0f;
        for (int row = 1; row < 5; row++) {
            points[row][0] = new PointF(points[row - 1][0].x, points[row - 1][0].y + deltaXY); // top
            points[row][1] = new PointF(points[row - 1][1].x - (float)(deltaXY * angelRate)
                    , points[row - 1][1].y + (float)(deltaXY * angelRate)); // top right
            points[row][2] = new PointF(points[row - 1][2].x - deltaXY, points[row - 1][2].y); // right
            points[row][3] = new PointF(points[row - 1][3].x - (float)(deltaXY * angelRate)
                    , points[row - 1][3].y - (float)(deltaXY * angelRate)); // right bottom
            points[row][4] = new PointF(points[row - 1][4].x, points[row - 1][4].y - deltaXY); // bottom
            points[row][5] = new PointF(points[row - 1][5].x + (float)(deltaXY * angelRate)
                    , points[row - 1][5].y - (float)(deltaXY * angelRate)); // left bottom
            points[row][6] = new PointF(points[row - 1][6].x + deltaXY, points[row - 1][6].y); // left
            points[row][7] = new PointF(points[row - 1][7].x + (float)(deltaXY * angelRate)
                    , points[row - 1][7].y + (float)(deltaXY * angelRate)); // top left
        }

        topMoveX = points[0][0].x;
        topMoveY = points[0][0].y;
        topRightMoveX = points[0][1].x;
        topRightMoveY = points[0][1].y;
        rightMoveX = points[0][2].x;
        rightMoveY = points[0][2].y;
        rightBottomMoveX = points[0][3].x;
        rightBottomMoveY = points[0][3].y;
        bottomMoveX = points[0][4].x;
        bottomMoveY = points[0][4].y;
        leftBottomMoveX = points[0][5].x;
        leftBottomMoveY = points[0][5].y;
        leftMoveX = points[0][6].x;
        leftMoveY = points[0][6].y;
        topLeftMoveX = points[0][7].x;
        topLeftMoveY = points[0][7].y;

        // percent points
//        setPercentValues(new float[]{0.83f, 0.5f, 0.54f, 0.43f
//                , 0.33f, 0.28f, 0.29f, 0.33f});
        if (percentValues != null) {
            for (int i = 0; i < 8; i++) {
                float percent = percentValues[i];
                float targetX = 0;
                float targetY = 0;
                switch (i) {
                    case 0: // top
                        targetX = center.x;
                        targetY = center.y - maxRadius * percent;
                        break;
                    case 1: // top right
                        targetX = center.x + maxRadius * percent * angelRate;
                        targetY = center.y - maxRadius * percent * angelRate;
                        break;
                    case 2: // right
                        targetX = center.x + maxRadius * percent;
                        targetY = center.y;
                        break;
                    case 3:
                        targetX = center.x + maxRadius * percent * angelRate;
                        targetY = center.y + maxRadius * percent * angelRate;
                        break;
                    case 4:
                        targetX = center.x;
                        targetY = center.y + maxRadius * percent;
                        break;
                    case 5:
                        targetX = center.x - maxRadius * percent * angelRate;
                        targetY = center.y + maxRadius * percent * angelRate;
                        break;
                    case 6: // left
                        targetX = center.x - maxRadius * percent;
                        targetY = center.y;
                        break;
                    case 7:
                        targetX = center.x - maxRadius * percent * angelRate;
                        targetY = center.y - maxRadius * percent * angelRate;
                        break;
                }
                percentPoints[i] = new PointF(targetX, targetY);
            }
        }

//        indicatorPointTop = points[4][0];
//        indicatorPointTopRight = points[4][1];
//        indicatorPointRight = points[4][2];
//        indicatorPointBottomRight = points[4][3];
//        indicatorPointBottom = points[4][4];
//        indicatorPointBottomLeft = points[4][5];
//        indicatorPointLeft = points[4][6];
//        indicatorPointTopLeft = points[4][7];

        indicatorPointTop = new PointF(center.x, center.y);
        indicatorPointTopRight = new PointF(center.x, center.y);
        indicatorPointRight = new PointF(center.x, center.y);
        indicatorPointBottomRight = new PointF(center.x, center.y);
        indicatorPointBottom = new PointF(center.x, center.y);
        indicatorPointBottomLeft = new PointF(center.x, center.y);
        indicatorPointLeft = new PointF(center.x, center.y);
        indicatorPointTopLeft = new PointF(center.x, center.y);

        sideLine1StartPoint = new PointF(points[0][0].x, points[0][0].y);
        sideLine2StartPoint = new PointF(points[1][0].x, points[1][0].y);
        sideLine3StartPoint = new PointF(points[2][0].x, points[2][0].y);
        sideLine4StartPoint = new PointF(points[3][0].x, points[3][0].y);
        sideLine5StartPoint = new PointF(points[4][0].x, points[4][0].y);

        sideLine1MovePoint1 = new PointF(points[0][0].x, points[0][0].y);
        sideLine1MovePoint2 = new PointF(points[0][1].x, points[0][1].y);
        sideLine1MovePoint3 = new PointF(points[0][2].x, points[0][2].y);
        sideLine1MovePoint4 = new PointF(points[0][3].x, points[0][3].y);
        sideLine1MovePoint5 = new PointF(points[0][4].x, points[0][4].y);
        sideLine1MovePoint6 = new PointF(points[0][5].x, points[0][5].y);
        sideLine1MovePoint7 = new PointF(points[0][6].x, points[0][6].y);
        sideLine1MovePoint8 = new PointF(points[0][7].x, points[0][7].y);

        slidLinePath1 = new Path();
        slidLinePath2 = new Path();
        slidLinePath3 = new Path();
        slidLinePath4 = new Path();
        slidLinePath5 = new Path();
    } // initData

    private void init(Context context, AttributeSet attrs) {
        float speedRate = 480 / context.getResources().getDisplayMetrics().densityDpi;
        frameLineMoveSpeed *= speedRate;
        INDICATOR_SPEED *= speedRate;
        initAttrs(context, attrs);
        path = new Path();
        paint = new Paint();
        paint.setStrokeWidth(12);
        paint.setTextSize(textSize);
        paint.setAntiAlias(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.GREEN);
        paint.setDither(true);
        setZOrderOnTop(true);
        holder = getHolder();
        holder.setFormat(PixelFormat.TRANSLUCENT);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                if (isDrawing == false) {
                    isDrawing = true;
                    new Thread(RadarView.this).start();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                isDrawing = false;
            }
        });
        setOnClickListener(this);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadarView, 0, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.RadarView_bgColor:
                    bgColor = a.getColor(attr, Color.RED);
                    break;
                case R.styleable.RadarView_lineColor:
                    lineColor = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.RadarView_maxRadius:
                    // default is 80 dp
                    maxRadius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RadarView_textSize:
                    textSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RadarView_textColor:
                    textColor = a.getColor(attr, Color.RED);
                    break;
            }
        }
        a.recycle();
    }

    @Override
    public void run() {
        while (isDrawing) {
            if (canDraw) {
                draw();
            }
        }
    }

    private void draw() {
        Canvas canvas = holder.lockCanvas();
        if(canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        paint.setColor(bgColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
        paint.setStyle(Paint.Style.STROKE);
        // frame lines --------------------------------------
        paint.setColor(lineColor);
        paint.setStrokeWidth(4);
        if (alpha < 255) {
            alpha+=3;
        } else {
            alpha = 255;
        }
        paint.setAlpha(alpha);
        // top
        canvas.drawLine(points[0][0].x, points[0][0].y, topMoveX, topMoveY, paint);
        if (topMoveY - center.y < 0) {
            topMoveY += frameLineMoveSpeed;
        } else {
            topMoveY = center.y;
        }
        // top right
        canvas.drawLine(points[0][1].x, points[0][1].y, topRightMoveX, topRightMoveY, paint);
        if (topRightMoveY - center.y < 0) {
            topRightMoveX -= frameLineMoveSpeed * angelRate;
            topRightMoveY += frameLineMoveSpeed * angelRate;
        } else {
            topRightMoveX = center.x;
            topRightMoveY = center.y;
        }
        // right
        canvas.drawLine(points[0][2].x, points[0][2].y, rightMoveX, rightMoveY, paint);
        if (rightMoveX - center.x > 0) {
            rightMoveX -= frameLineMoveSpeed;
        } else {
            rightMoveX = center.x;
        }
        // bottom right
        canvas.drawLine(points[0][3].x, points[0][3].y, rightBottomMoveX, rightBottomMoveY, paint);
        if (rightBottomMoveX - center.x > 0) {
            rightBottomMoveX -= frameLineMoveSpeed * angelRate;
            rightBottomMoveY -= frameLineMoveSpeed * angelRate;
        } else {
            rightBottomMoveX = center.x;
            rightBottomMoveY = center.y;
        }
        // bottom
        canvas.drawLine(points[0][4].x, points[0][4].y, bottomMoveX, bottomMoveY, paint);
        if (bottomMoveY - center.y > 0) {
            bottomMoveY -= frameLineMoveSpeed;
        } else {
            bottomMoveY = center.y;
        }
        // left bottom
        canvas.drawLine(points[0][5].x, points[0][5].y, leftBottomMoveX, leftBottomMoveY, paint);
        if (leftBottomMoveX - center.x < 0) {
            leftBottomMoveX += frameLineMoveSpeed * angelRate;
            leftBottomMoveY -= frameLineMoveSpeed * angelRate;
        } else {
            leftBottomMoveX = center.x;
            leftBottomMoveY = center.y;
        }
        // left
        canvas.drawLine(points[0][6].x, points[0][6].y, leftMoveX, leftMoveY, paint);
        if (leftMoveX - center.x < 0) {
            leftMoveX += frameLineMoveSpeed;
        } else {
            leftMoveX = center.x;
        }
        // top left
        canvas.drawLine(points[0][7].x, points[0][7].y, topLeftMoveX, topLeftMoveY, paint);
        if (topLeftMoveX - center.x < 0) {
            topLeftMoveX += frameLineMoveSpeed * angelRate;
            topLeftMoveY += frameLineMoveSpeed * angelRate;
        } else {
            topLeftMoveX = center.x;
            topLeftMoveY = center.y;
        }

        // ------------- side lines ----------------
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 8; col++) {
                if (col < 7) {
                    canvas.drawLine(points[row][col].x, points[row][col].y, points[row][col + 1].x, points[row][col + 1].y, paint);
                } else {
                    canvas.drawLine(points[row][col].x, points[row][col].y, points[row][0].x, points[row][0].y, paint);
                }
            }
        }
        // -----------------------------------------------------------------------------------------
//        slidLinePath1.moveTo(sideLine1StartPoint.x, sideLine1StartPoint.y);
//        slidLinePath1.lineTo(sideLine1MovePoint1.x, sideLine1MovePoint1.y);
//        slidLinePath1.setFillType(Path.FillType.WINDING);
//        float bigdx1 = (points[0][1].x - sideLine1StartPoint.x);
//        float bigdy1 = (points[0][1].y - sideLine1StartPoint.y);
//        float bigBevelEdge = (float)Math.sqrt(bigdx1 * bigdx1 + bigdy1 * bigdy1); // 斜边
//        float dy1 = (bigdy1 / bigBevelEdge) * SIDE_MOVE_SPEED;
//        float dx1 = (bigdx1 / bigBevelEdge) * SIDE_MOVE_SPEED;
//        if (sideLine1MovePoint1.x < points[0][1].x) {
//            sideLine1MovePoint1.x += dx1;
//            sideLine1MovePoint1.y += dy1;
//        } else {
//            sideLine1MovePoint1.x = points[0][1].x;
//            sideLine1MovePoint1.y = points[0][1].y;
//            // 第二根小线
//            slidLinePath1.lineTo(sideLine1MovePoint2.x, sideLine1MovePoint2.y);
//            float bigdx2 = (points[0][2].x - points[0][1].x);
//            float bigdy2 = (points[0][2].y - points[0][1].y);
//            float dy2 = (bigdy2 / bigBevelEdge) * SIDE_MOVE_SPEED;
//            float dx2 = (bigdx2 / bigBevelEdge) * SIDE_MOVE_SPEED;
//            if (sideLine1MovePoint2.x < points[0][2].x) {
//                sideLine1MovePoint2.x += dx2;
//                sideLine1MovePoint2.y += dy2;
//            } else {
//                sideLine1MovePoint2.x = points[0][3].x;
//                sideLine1MovePoint2.y = points[0][3].y;
//                // 第三根小线
//                slidLinePath1.lineTo(sideLine1MovePoint3.x, sideLine1MovePoint3.y);
//                float bigdx3 = (points[0][3].x - points[0][2].x);
//                float bigdy3 = (points[0][3].y - points[0][2].y);
//                float dy3 = (bigdy3 / bigBevelEdge) * SIDE_MOVE_SPEED;
//                float dx3 = (bigdx3 / bigBevelEdge) * SIDE_MOVE_SPEED;
//                if (sideLine1MovePoint3.x > points[0][3].x) {
//                    sideLine1MovePoint3.x += dx3;
//                    sideLine1MovePoint3.y += dy3;
//                } else {
//                    sideLine1MovePoint3.x = points[0][3].x;
//                    sideLine1MovePoint3.y = points[0][3].y;
//                    // 第四根小线
//                }
//            }
//        }
//        canvas.drawPath(slidLinePath1, paint);
        // ------------------------------------------------------------------------------------------

        // titles
        paint.setColor(textColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        for (int i = 0; i < 8; i++) {
            float x = points[0][i].x;
            float y = points[0][i].y;
            switch (i) {
                case 0:
                    x -= paint.getTextSize();
                    y -= paint.getTextSize()*0.8f;
                    break;
                case 1:
                    x += paint.getTextSize()/2;
                    y -= paint.getTextSize()/2;
                    break;
                case 2:
                    x += paint.getTextSize()/2;
                    y += paint.getTextSize()/2;
                    break;
                case 3:
                    x += paint.getTextSize()/2;
                    y += paint.getTextSize();
                    break;
                case 4:
                    x -= paint.getTextSize();
                    y += paint.getTextSize()*1.8f;
                    break;
                case 5:
                    x -= paint.getTextSize()*2.6f;
                    y += paint.getTextSize()*1.5f;
                    break;
                case 6:
                    x -= paint.getTextSize()*2.6f;
                    y += paint.getTextSize()/2;
                    break;
                case 7:
                    x -= paint.getTextSize()*2.6f;
//                    y += paint.getTextSize()/3;
                    break;
            }
            canvas.drawText(titles[i], x, y, paint);
        }

        // show indicator
        LinearGradient mShader = new LinearGradient(0, 0, getWidth(), getHeight(),
                new int[] {Color.WHITE, Color.RED} , null, Shader.TileMode.REPEAT); // 一个材质,打造出一个线性梯度沿著一条线。
        paint.setShader(mShader);
        paint.setShadowLayer(1, 12, 12, Color.argb(180, 50, 50, 50));
//        paint.setPathEffect(new PathDashPathEffect(path, 0.5f, 0.5f, PathDashPathEffect.Style.TRANSLATE));
        path.reset();
        // top
        if (indicatorPointTop.y > percentPoints[0].y) {
            indicatorPointTop.y -= INDICATOR_SPEED;
        } else {
            indicatorPointTop.y = percentPoints[0].y;
        }
        // top right
        if (indicatorPointTopRight.x < percentPoints[1].x) {
            indicatorPointTopRight.x += INDICATOR_SPEED * angelRate;
            indicatorPointTopRight.y -= INDICATOR_SPEED * angelRate;
        } else {
            indicatorPointTopRight.x = percentPoints[1].x;
            indicatorPointTopRight.y = percentPoints[1].y;
        }
        // right
        if (indicatorPointRight.x < percentPoints[2].x) {
            indicatorPointRight.x += INDICATOR_SPEED;
        } else {
            indicatorPointRight.x = percentPoints[2].x;
            indicatorPointRight.y = percentPoints[2].y;
        }
        // bottom right
        if (indicatorPointBottomRight.x < percentPoints[3].x) {
            indicatorPointBottomRight.x += INDICATOR_SPEED * angelRate;
            indicatorPointBottomRight.y += INDICATOR_SPEED * angelRate;
        } else {
            indicatorPointBottomRight.x = percentPoints[3].x;
            indicatorPointBottomRight.y = percentPoints[3].y;
        }
        // bottom
        if (indicatorPointBottom.y < percentPoints[4].y) {
            indicatorPointBottom.y += INDICATOR_SPEED;
        } else {
            indicatorPointBottom.y = percentPoints[4].y;
        }
        // bottom left
        if (indicatorPointBottomLeft.y < percentPoints[5].y) {
            indicatorPointBottomLeft.x -= INDICATOR_SPEED * angelRate;
            indicatorPointBottomLeft.y += INDICATOR_SPEED * angelRate;
        } else {
            indicatorPointBottomLeft.x = percentPoints[5].x;
            indicatorPointBottomLeft.y = percentPoints[5].y;
        }
        // left
        if (indicatorPointLeft.x > percentPoints[6].x) {
            indicatorPointLeft.x -= INDICATOR_SPEED ;
        } else {
            indicatorPointLeft.x = percentPoints[6].x;
        }
        // top left
        if (indicatorPointTopLeft.y > percentPoints[7].y) {
            indicatorPointTopLeft.x -= INDICATOR_SPEED * angelRate;
            indicatorPointTopLeft.y -= INDICATOR_SPEED * angelRate;
        } else {
            indicatorPointTopLeft.x = percentPoints[7].x;
            indicatorPointTopLeft.y = percentPoints[7].y;
        }

        path.moveTo(indicatorPointTop.x, indicatorPointTop.y);
        path.lineTo(indicatorPointTopRight.x, indicatorPointTopRight.y);
        path.lineTo(indicatorPointRight.x, indicatorPointRight.y);
        path.lineTo(indicatorPointBottomRight.x, indicatorPointBottomRight.y);
        path.lineTo(indicatorPointBottom.x, indicatorPointBottom.y);
        path.lineTo(indicatorPointBottomLeft.x, indicatorPointBottomLeft.y);
        path.lineTo(indicatorPointLeft.x, indicatorPointLeft.y);
        path.lineTo(indicatorPointTopLeft.x, indicatorPointTopLeft.y);
        path.close();
        canvas.drawPath(path, paint);

//        paint.setStrokeWidth(12);
        paint.setShader(null);
        paint.clearShadowLayer();
        holder.unlockCanvasAndPost(canvas);
    }

    public void start() {
        canDraw = true;
        setVisibility(View.VISIBLE);
    }

    public void stop() {
        isDrawing = false;
        canDraw = false;
        setVisibility(View.INVISIBLE);
    }

    public void destroy() {
        stop();
    }

    @Override
    public void onClick(View v) {
        initData();
    }
}
