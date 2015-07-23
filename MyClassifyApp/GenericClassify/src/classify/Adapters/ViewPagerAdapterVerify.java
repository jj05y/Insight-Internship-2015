package classify.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.cmw.R;

import java.io.IOException;
import java.util.ArrayList;

import classify.Constants.C;
import classify.DatabaseClasses.DatabaseHandler;
import classify.Fragments.VerifyClassificationFragment;
import classify.ListItems.ItemForVerification;

/**
 * Created by joe on 22/07/15.
 */
public class ViewPagerAdapterVerify extends PagerAdapter {

    private ArrayList<ItemForVerification> items;
    private Context c;
    private VerifyClassificationFragment parent;
    private DatabaseHandler db;

    private boolean paused;
    private boolean notStarted;
    private boolean finished;
    private boolean readyToPlayVid;
    private MediaPlayer mp;



    public ViewPagerAdapterVerify(ArrayList<ItemForVerification> items, Context c, DatabaseHandler db, VerifyClassificationFragment parent) {
        this.items = items;
        this.c = c;
        this.db = db;
        mp = new MediaPlayer();
        this.parent = parent;

    }


    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View myInflatedView = inflater.inflate(R.layout.view_pager_item_verify_page, null);


        String name = items.get(position).getName();
        String fileName = items.get(position).getFileName();
        int actualLabel = items.get(position).getActualLabel();
        int predictedLabel = items.get(position).getPredictedLabel();
        int rowID = items.get(position).getRowID();
        int rep = items.get(position).getRep();

        TextView nameText = (TextView) myInflatedView.findViewById(R.id.text_verify_name);
        TextView actualText = (TextView) myInflatedView.findViewById(R.id.text_verify_actual);
        TextView predictedText = (TextView) myInflatedView.findViewById(R.id.text_verify_predicted);

        nameText.setText(name + " - Rep: " + rep);
        actualText.setText(C.LABELS[actualLabel]);


        predictedText.setText(C.LABELS[predictedLabel]);



        Button keepButton = (Button) myInflatedView.findViewById(R.id.button_keep_prediction);
        Button ignoreButton = (Button) myInflatedView.findViewById(R.id.button_ignore_prediction);

        keepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.updateActualLabel(rowID, predictedLabel);
                db.updatePredictedLabel(rowID, 0);
                keepButton.setEnabled(false);
                ignoreButton.setEnabled(false);
                parent.nextPage();
            }
        });

        ignoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keepButton.setEnabled(false);
                ignoreButton.setEnabled(false);
                parent.nextPage();
            }
        });


        readyToPlayVid = false;

        VideoView videoView = (VideoView) myInflatedView.findViewById(R.id.new_verify_video_view);
        SurfaceHolder vidHolder = videoView.getHolder();
        vidHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                Log.d("surfaceTesting", "created position " + position);
                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fileName, MediaStore.Images.Thumbnails.MINI_KIND);
                BitmapDrawable bitmapDrawable = new BitmapDrawable(c.getResources(), thumb);
                videoView.setBackground(bitmapDrawable);
                readyToPlayVid = true;
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                Log.d("surfaceTesting", "changed position " + position);
                readyToPlayVid = true;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                Log.d("surfaceTesting", "destroyed position " + position);


            }
        });



        paused = false;
        notStarted = true;
        finished = false;

        videoView.setOnTouchListener(new VideoView.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                finished = true;
                            }
                        });
                        if (notStarted) {
                            if (readyToPlayVid) {
                                notStarted = false;
                                videoView.setBackground(null);
                                Log.d("surfaceTesting", "Boom");
                                mp.reset();
                                try {
                                    Log.d("storage", "from viewpageradapter: " + fileName);
                                    mp.setDataSource(fileName);

                                    mp.setDisplay(vidHolder);
                                    mp.prepare();
                                    mp.start();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (finished) {
                            mp.start();
                            finished = false;
                        } else if (!paused) {
                            mp.pause();
                            paused = true;
                        } else if (paused) {
                            mp.start();
                            paused = false;
                        }
                }
                return false;
            }
        });





        container.addView(myInflatedView);
        return myInflatedView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View) object);
    }

    public void stopMediaPlayer() {
        if (mp.isPlaying()) {
            mp.stop();
        }
        paused = false;
        notStarted = true;
        finished = false;
    }

}
