package dong.lan.code.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import dong.lan.code.R;

/**
 * Created by Dooze on 2015/10/2.
 */
public class LockView extends View {
    private Matrix matrix = new Matrix();
    private Point points[][] = new Point[3][3];  //屏幕的九宫格点
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Point> pointList = new ArrayList<>(); //绘制过程的收集的点
    private boolean isInit;
    private int w;  // 屏幕的宽
    private int h;  //屏幕的高
    private float raduis;  //图片点的半径
    private int offsetX;   //起始点的X偏移量
    private int offsetY;    //起始点的Y偏移量
    private float x;      //当前触摸点的X坐标
    private float y;     //当前触摸点的Y坐标
    private Bitmap pointNormal;
    private Bitmap pointError;
    private Bitmap pointPress;
    private Bitmap lineNormal;
    private Bitmap lineError;
    private boolean isSelecte;  //绘制在继续
    private boolean isFinish;  //绘制结束
    private boolean moveAndPaint; //可控制是否停止绘制

    public LockView(Context context) {
        super(context);
    }

    public LockView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface LockPaintFinish {
        void onLockPaintFinish(String pwd);
    }

    LockPaintFinish lockPaintFinish;

    public void setOnLockPaintFinish(LockPaintFinish lockPaintFinish) {
        this.lockPaintFinish = lockPaintFinish;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInit) {
            initPoint();
        }
        paint2Cavans(canvas);

        if (pointList.size() > 0) {
            Point a = pointList.get(0);
            for (int i = 0; i < pointList.size(); i++) {
                Point b = pointList.get(i);
                paintLine(canvas, a, b);
                a = b;
            }
            if (moveAndPaint) {
                paintLine(canvas, a, new Point(x, y));
            }
        }
    }

    /*
    绘制解锁点
     */
    private void paint2Cavans(Canvas canvas) {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (point.status == Point.STATUS_NORMAL) {
                    canvas.drawBitmap(pointNormal, point.x - raduis, point.y - raduis, paint);
                } else if (point.status == Point.STATUS_ERROR) {
                    canvas.drawBitmap(pointError, point.x - raduis, point.y - raduis, paint);
                } else {
                    canvas.drawBitmap(pointPress, point.x - raduis, point.y - raduis, paint);
                }
            }
        }
    }

    private void paintLine(Canvas canvas, Point p1, Point p2) {
        float lineLength = (float) Point.distance(p1, p2);
        float degree = Point.getDegree(p1, p2);
        canvas.rotate(degree, p1.x, p1.y);
        if (p1.status == Point.STATUS_PRESS) {
            matrix.setScale(lineLength / lineNormal.getWidth(), 1);
            matrix.postTranslate(p1.x - lineNormal.getWidth() / 2, p1.y - lineNormal.getHeight() / 2);
            canvas.drawBitmap(lineNormal, matrix, paint);
        } else {
            matrix.setScale(lineLength / lineNormal.getWidth(), 1);
            matrix.postTranslate(p1.x - lineNormal.getWidth() / 2, p1.y - lineNormal.getHeight() / 2);
            canvas.drawBitmap(lineError, matrix, paint);
        }
        canvas.rotate(-degree, p1.x, p1.y);
    }

    /*
    初始化所有锁屏点
     */
    private void initPoint() {
        //获取屏幕宽高
        w = getWidth();
        h = getHeight();
        if (w > h) {
            offsetX = (w - h) / 2;
            w = h;
        } else {
            offsetY = (h - w) / 2;
            h = w;
        }

        pointNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.point_nomal);
        pointPress = BitmapFactory.decodeResource(getResources(), R.mipmap.point_press);
        pointError = BitmapFactory.decodeResource(getResources(), R.mipmap.point_error);
        lineNormal = BitmapFactory.decodeResource(getResources(), R.mipmap.line_normal);
        lineError = BitmapFactory.decodeResource(getResources(), R.mipmap.line_error);

        raduis = pointError.getWidth() / 2;

        points[0][0] = new Point(offsetX + w / 4, offsetY + w / 4, 1);
        points[0][1] = new Point(offsetX + w / 2, offsetY + w / 4, 2);
        points[0][2] = new Point(offsetX + w - w / 4, offsetY + w / 4, 3);

        points[1][0] = new Point(offsetX + w / 4, offsetY + w / 2, 4);
        points[1][1] = new Point(offsetX + w / 2, offsetY + w / 2, 5);
        points[1][2] = new Point(offsetX + w - w / 4, offsetY + w / 2, 6);

        points[2][0] = new Point(offsetX + w / 4, offsetY + w - w / 4, 7);
        points[2][1] = new Point(offsetX + w / 2, offsetY + w - w / 4, 8);
        points[2][2] = new Point(offsetX + w - w / 4, offsetY + w - w / 4, 9);

        isInit = true;
    }

    private Point checkSelect() {
        for (int i = 0; i < points.length; i++) {
            for (int j = 0; j < points[i].length; j++) {
                Point point = points[i][j];
                if (Point.with(point.x, point.y, x, y, raduis)) {
                    return point;
                }
            }
        }

        return null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        x = event.getX();
        y = event.getY();
        Point point = null;
        moveAndPaint = false;
        isFinish = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                resetPoint();
                point = checkSelect();
                if (point != null) {
                    isSelecte = true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (isSelecte) {
                    if ((point = checkSelect()) == null) {
                        moveAndPaint = true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                isSelecte = false;
                isFinish = true;

                break;
        }

        if (isFinish) {
            if (pointList.size() == 1) {
                resetPoint();
            } else if (pointList.size() < 5 && pointList.size() >= 2) {
                pointError();
            } else {
                if (lockPaintFinish != null) {
                    StringBuilder builder = new StringBuilder();
                    for (Point p : pointList)
                        builder.append(p.index);
                    lockPaintFinish.onLockPaintFinish(builder.toString());
                }
                resetPoint();
            }
        }

        if (!isFinish && isSelecte && point != null) {
            if (isRepeatPoint(point)) {
                moveAndPaint = true;
            } else {
                point.status = Point.STATUS_PRESS;
                pointList.add(point);
            }
        }


        //刷新画布
        postInvalidate();

        return true;
    }

    private boolean isRepeatPoint(Point point) {
        return pointList.contains(point);
    }

    private void resetPoint() {
        for (Point point : pointList) {
            point.status = Point.STATUS_NORMAL;

        }
        pointList.clear();
    }

    private void pointError() {
        for (Point point : pointList) {
            point.status = Point.STATUS_ERROR;
        }
        lockPaintFinish.onLockPaintFinish("ERROR");
    }

    public static class Point {
        public float x;
        public float y;
        public int status = 0;
        public int index = 0;
        public static int STATUS_NORMAL = 0;
        public static int STATUS_PRESS = 1;
        public static int STATUS_ERROR = 3;

        public Point() {
        }

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public Point(float x, float y, int index) {
            this.x = x;
            this.y = y;
            this.index = index;
        }

        public static double distance(Point a, Point b) {
            return Math.sqrt((Math.abs(a.x - b.x) * Math.abs(a.x - b.x) + Math.abs(a.y - b.y) * Math.abs(a.y - b.y)));
        }

        public static boolean with(float pointX, float pointY, float moveX, float moveY, float r) {
            return Math.sqrt(((pointX - moveX) * (pointX - moveX) + (pointY - moveY) * (pointY - moveY))) < r;
        }

        public static float getDegree(Point a, Point b) {
            float ax = a.x;
            float ay = a.y;
            float bx = b.x;
            float by = b.y;
            float degree = 0;
            if (ax == bx) {
                if (ay < by)
                    degree = 90;
                if (ay > by)
                    degree = 270;
            } else if (by == ay) {
                if (ax > bx)
                    degree = 180;
                if (ax < bx)
                    degree = 0;
            } else if (ay > by && bx > ax) {
                degree = 360 - (float) (Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) * 180 / Math.PI);
            } else if (ay > by && bx < ax) {
                degree = 180 + (float) (Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) * 180 / Math.PI);
            } else if (ay < by && bx < ax) {
                degree = 180 - (float) (Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) * 180 / Math.PI);
            } else if (ay < by && bx > ax) {
                degree = (float) (Math.atan(Math.abs(by - ay) / Math.abs(bx - ax)) * 180 / Math.PI);
            }


            return degree;
        }
    }

}
