package com.cgmn.msxl.comp.k;

public class KlineStyle {
    public static float chartRate = 0.68f;
    public static float indexRate = 0.16f;

    //画笔宽度
    public static float timeShareBold;
    //画笔宽度
    public static float kLineBold;
    //K线间隔
    public static float mBarSpace;

    //gridLine
    public static float gridLine;


    //K线宽度
    public static float kWidth;
    public static float kWidthMax;
    public static float kWidthmin;
    //
    public static float rightWidth;
    public static float chartSpace;
    public static float indexSpace;

    public  static float kTextSize;

    public static float pxScaleRate = 1f;

    public static void resetSize(){
        kTextSize = kTextSize * pxScaleRate;
        if(kTextSize > 50){
            kTextSize = 50;
        }

        rightWidth = rightWidth * pxScaleRate;
        kWidth = kWidth * pxScaleRate;
        mBarSpace = mBarSpace * pxScaleRate;
        kLineBold = kLineBold * pxScaleRate;
        timeShareBold = timeShareBold * pxScaleRate;
        kWidthMax = kWidthMax * pxScaleRate;
        kWidthmin = kWidthmin * pxScaleRate;
        gridLine = gridLine * pxScaleRate;

        chartSpace = chartSpace * pxScaleRate;
        indexSpace = indexSpace * pxScaleRate;
    }

    public static void initSize(){
        //画笔宽度
        kLineBold = 0.6f;
        timeShareBold = 0.8f;
        //K线间隔
        gridLine = 0.05f;
        //K线间隔
        mBarSpace = 1.6f;
        //K线宽度
        kWidth = 4f;
        kWidthMax = 20f;
        kWidthmin = 0.8f;
         //
        rightWidth = 20f;
        chartSpace = 12f;
        indexSpace = 2.5f;
        kTextSize = 10f;
    }
}
