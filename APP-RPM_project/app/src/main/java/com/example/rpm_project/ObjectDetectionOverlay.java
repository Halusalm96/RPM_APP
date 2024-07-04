package com.example.rpm_project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ObjectDetectionOverlay extends View {
    private List<DetectionResult> detectionResults = new ArrayList<>();
    private Paint boxPaint;
    private Paint textPaint;
    private Bitmap bitmap;

    public ObjectDetectionOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        boxPaint = new Paint();
        boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4f);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(32f);
    }


    public void setDetectionResults(List<DetectionResult> results) {
        this.detectionResults = results;
        invalidate();
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        if (detectionResults != null) {
            Paint paint = new Paint();
            paint.setColor(0xFF00FF00); // Green
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(4.0f);

            Paint textPaint = new Paint();
            textPaint.setColor(0xFFFF0000); // Red
            textPaint.setTextSize(32);

            if (detectionResults != null) {
                for (DetectionResult result : detectionResults) {
                    result.draw(canvas, boxPaint, textPaint);
                }
            }
//
//            for (DetectionResult result : detectionResults) {
//                canvas.drawRect(result.rect, paint);
//                canvas.drawText(result.label, result.rect.left, result.rect.top - 10, textPaint);
//            }
        }

        if (detectionResults != null) {
            for (DetectionResult result : detectionResults) {
                result.draw(canvas, boxPaint, textPaint);
            }
        }
    }

    public static class DetectionResult {
        private final String className;
        private final float confidence;
        private final float left;
        private final float top;
        private final float right;
        private final float bottom;
        public RectF rect;


        public DetectionResult(String className, float confidence, float left, float top, float right, float bottom) {
            this.className = className;
            this.confidence = confidence;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public void draw(Canvas canvas, Paint boxPaint, Paint textPaint) {
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRect(rect, boxPaint);

            String label = String.format("%s (%.2f)", className, confidence);
            canvas.drawText(label, left, top - 10, textPaint);
        }
    }
}
