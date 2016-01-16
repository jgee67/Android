package edu.ucsb.cs.cs185.jgee.cameraroll;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by jgee on 5/14/15.
 */
public class TouchView extends ImageView {

    class mListener extends GestureDetector.SimpleOnGestureListener implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector){
            float scaleFactor = detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            float currentFocusX = detector.getFocusX() + getScrollX();
            float currentFocusY = detector.getFocusY() + getScrollY();
            mtx.postScale(scaleFactor, scaleFactor, currentFocusX, currentFocusY);
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector){
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector){
            //do nothing
        }

        @Override
        public boolean onSingleTapUp(MotionEvent event){
            float[] pts = new float[2];
            pts[0] = event.getX();
            pts[1] = event.getY();
            Canvas canvas = new Canvas(img);
            Matrix inverse = new Matrix();
            mtx.invert(inverse);
            inverse.mapPoints(pts);
            canvas.drawCircle(pts[0], pts[1], 25, p);
            invalidate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
            mtx.postTranslate(-distanceX, -distanceY);
            invalidate();
            return true;
        }

        @Override
        public boolean onDown(MotionEvent event){
            return true;
        }

        @Override
        public void OnRotation(RotationGestureDetector detector){
            mtx.postRotate(-detector.getAngle(), detector.getMidX(), detector.getMidY());
            invalidate();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mDetector.onTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        mRotationDetector.onTouchEvent(event);
        return true;
    }
//
    private mListener listener = new mListener();
    private ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(this.getContext(), listener);
    private RotationGestureDetector mRotationDetector = new RotationGestureDetector(listener);
    private GestureDetector mDetector = new GestureDetector(this.getContext(), listener);
    private Bitmap img;
    private Matrix mtx;
    private Paint p = new Paint();

    public TouchView(Context context, AttributeSet attrs){
        super(context, attrs);
        // p.setColor(getResources().getColor(R.color.actionbar));
        p.setStyle(Paint.Style.FILL);
    }

    public void setImage(Bitmap bm){
        img = bm;
        mtx = new Matrix();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        setImageMatrix(mtx);
        canvas.drawBitmap(img, mtx, null);
    }
}
