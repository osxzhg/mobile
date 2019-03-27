package com.example.animation;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class SpriteAnimationSurface extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
    Thread controller = null;

    Boolean running = new Boolean(false);

    int MAX_SPRITES = 7;
    ArrayList<Bitmap> animation = new ArrayList<Bitmap>();
    int current_frame = 0;

    Paint paint = new Paint();

    public SpriteAnimationSurface(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        getHolder().addCallback(this); // setup the surface holder callback

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap frames = BitmapFactory.decodeResource(getResources(), R.drawable.deer_sprite,options);

        for(int i =0; i<= MAX_SPRITES-1; i++)
        {
            Bitmap frame = Bitmap.createBitmap(frames,i * 279, 0, 279 , 456 );
            animation.add(frame);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        // start the animation thread once the surface has been created
        controller = new Thread(this);


        running = true;

        controller.start(); // start the thread
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        //TODO handle the surface changed event properly
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        synchronized(running)
        {
            running = false;

            if(controller != null)
            {
                try
                {
                    controller.join();  // finish the animation thread and let the animation thread die a natural death
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace(); //TODO log this exception properly
                }
            }
        }
    }

    @Override
    public void run() {
        while(running)
        {
            Canvas canvas = null;
            SurfaceHolder holder = getHolder();
            synchronized(holder)
            {
                canvas = holder.lockCanvas();

                canvas.drawColor(Color.WHITE); // clear the screen
                //canvas.scale(2,2);

                Bitmap sprite = animation.get(current_frame);

                canvas.drawBitmap(sprite,getWidth()/2 - sprite.getWidth()/2,getHeight()/2 - sprite.getHeight()/2, paint);

                current_frame = current_frame + 1;

                if(current_frame == MAX_SPRITES-1)
                {
                    current_frame = 0;
                }

                try {
                    Thread.sleep(1000 / 10); // 60 FPS
                }
                catch (InterruptedException e) {
                    e.printStackTrace(); // TODO log the exception properly (this exception is never going to happen)
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}

