package com.neonsofts.www.nemesis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.View;

import java.util.Random;

/**
 * Created by NewUsername on 1/4/2017.
 */

public class GradientBackgroundPainter {

    private static final int MIN = 4000;
    private static final int MAX = 7000;

    private final Random random;
    private final Handler handler;
    private final View target;
    private final int[] drawables;
    private final Context context;

    public GradientBackgroundPainter(@NonNull View target, int[] drawables) {
        this.target = target;
        this.drawables = drawables;
        random = new Random();
        handler = new Handler();
        context = target.getContext().getApplicationContext();
    }

    private void animate(final int firstDrawable, int secondDrawable, final int duration) {
        if (secondDrawable >= drawables.length) {
            secondDrawable = 0;
        }
        final Drawable first = ContextCompat.getDrawable(context, drawables[firstDrawable]);
        final Drawable second = ContextCompat.getDrawable(context, drawables[secondDrawable]);



        Bitmap bitmap_first = blur(centerCrop(((BitmapDrawable)first).getBitmap()));
        Bitmap bitmap_second = blur(centerCrop(((BitmapDrawable)second).getBitmap()));

        final Drawable drawable_first = new BitmapDrawable(context.getResources(), bitmap_first);
        final Drawable drawable_second = new BitmapDrawable(context.getResources(), bitmap_second);


        final TransitionDrawable transitionDrawable =
                new TransitionDrawable(new Drawable[] { drawable_first, drawable_second });

        target.setBackgroundDrawable(transitionDrawable);


        transitionDrawable.setCrossFadeEnabled(false);

        transitionDrawable.startTransition(duration);

        final int localSecondDrawable = secondDrawable;
        handler.postDelayed(new Runnable() {
            @Override public void run() {
                animate(localSecondDrawable, localSecondDrawable + 1, randInt(MIN, MAX));
            }
        }, duration);
    }

    public void start() {
        final int duration = randInt(MIN, MAX);
        animate(0, 1, duration);
    }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
    }

    private int randInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    private Bitmap centerCrop(Bitmap srcBmp){
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }
        return dstBmp;
    }

    //Set the radius of the Blur. Supported range 0 < radius <= 25
    private static final float BLUR_RADIUS = 2.5f;

    public Bitmap blur(Bitmap image) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(context);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
}