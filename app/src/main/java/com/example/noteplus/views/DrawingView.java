package com.example.noteplus.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class DrawingView extends View {

    private Paint currentPaint;
    private Path currentPath;
    private int penColor = Color.BLACK;
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private Matrix scaleMatrix = new Matrix();
    private float lastX, lastY;
    private boolean isScaling = false;
    private boolean isDragging = false;
    private boolean isEraserMode = false;
    private float[] matrixValues = new float[9];

    private List<Path> paths = new ArrayList<>();
    private List<Paint> paints = new ArrayList<>();

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        currentPaint = new Paint();
        currentPaint.setColor(penColor);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(5);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.setMatrix(scaleMatrix);
        for (int i = 0; i < paths.size(); i++) {
            canvas.drawPath(paths.get(i), paints.get(i));
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();

        scaleMatrix.getValues(matrixValues);
        float transX = matrixValues[Matrix.MTRANS_X];
        float transY = matrixValues[Matrix.MTRANS_Y];

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!isScaling && !isDragging) {
                    if (isEraserMode) {
                        erasePathAtPoint((x - transX) / scaleFactor, (y - transY) / scaleFactor);
                    } else {
                        currentPath = new Path();
                        currentPath.moveTo((x - transX) / scaleFactor, (y - transY) / scaleFactor);
                        paths.add(currentPath);
                        paints.add(new Paint(currentPaint));
                    }
                }
                lastX = x;
                lastY = y;
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!isScaling && !isDragging) {
                    if (isEraserMode) {
                        erasePathAtPoint((x - transX) / scaleFactor, (y - transY) / scaleFactor);
                    } else {
                        currentPath.lineTo((x - transX) / scaleFactor, (y - transY) / scaleFactor);
                    }
                } else if (isDragging) {
                    float dx = x - lastX;
                    float dy = y - lastY;
                    scaleMatrix.postTranslate(dx, dy);
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isScaling = false;
                isDragging = false;
                break;
        }
        invalidate();
        return true;
    }

    private void erasePathAtPoint(float x, float y) {
        Iterator<Path> pathIterator = paths.iterator();
        Iterator<Paint> paintIterator = paints.iterator();
        while (pathIterator.hasNext() && paintIterator.hasNext()) {
            Path path = pathIterator.next();
            Paint paint = paintIterator.next();
            if (pathIntersectsCircle(path, x, y, 20)) { // 20 is the eraser radius
                pathIterator.remove();
                paintIterator.remove();
                break; // Only remove the first intersecting path
            }
        }
    }

    private boolean pathIntersectsCircle(Path path, float x, float y, float radius) {
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);
        return RectF.intersects(bounds, new RectF(x - radius, y - radius, x + radius, y + radius));
    }

    public void setPenMode() {
        isEraserMode = false;
        currentPaint.setXfermode(null);
        currentPaint.setColor(penColor);
    }

    public void setEraserMode() {
        isEraserMode = true;
    }

    public void setPenColor(int color) {
        penColor = color;
        currentPaint.setColor(penColor);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f));
            scaleMatrix.setScale(scaleFactor, scaleFactor);
            invalidate();
            return true;
        }
    }
}