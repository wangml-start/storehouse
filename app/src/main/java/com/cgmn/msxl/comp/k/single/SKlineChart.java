package com.cgmn.msxl.comp.k.single;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.cgmn.msxl.R;
import com.cgmn.msxl.comp.k.KlineGroup;
import com.cgmn.msxl.comp.k.KlinePaint;

public class SKlineChart extends View {

    private RectF contentRect;
    private SKlinePaint klinePaint;
    private KlineGroup mData;

    public SKlinePaint getKlinePaint() {
        return klinePaint;
    }

    public SKlineChart(Context context) {
        this(context, null);
    }

    public SKlineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SKlineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        contentRect = new RectF();
        klinePaint = new SKlinePaint();

        klinePaint.setColors(
                getResources().getColor(R.color.kline_up),
                getResources().getColor(R.color.kline_down),
                getResources().getColor(R.color.kline_ave_5),
                getResources().getColor(R.color.kline_ave_10),
                getResources().getColor(R.color.kline_ave_20)
        );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        contentRect.set(0, 0, w , h);
        klinePaint.setContentRect(contentRect);
        if(mData != null){
            invalidateView();
        }
    }

    /**
     * Sets a new data object for the chart. The data object contains all values
     * and information needed for displaying.
     */
    public void setData(KlineGroup data) {
        mData = data;
        klinePaint.setData(mData);
        mData.calcMinMax(0, mData.getNodes().size());
    }

    public void invalidateView() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mData != null){
            klinePaint.render(canvas);
        }
    }
}
