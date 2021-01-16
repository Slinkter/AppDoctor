package com.cudpast.app.doctor.doctorApp.Activities.Support;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.cudpast.app.doctor.doctorApp.R;

import java.util.List;

public class IntroViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<ScreenItem> mListScreen;

    private Animation animation;


    public IntroViewPagerAdapter(Context mContext, List<ScreenItem> mListScreen) {
        this.mContext = mContext;
        this.mListScreen = mListScreen;

    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.layout_screen, null);
        animation = AnimationUtils.loadAnimation(mContext, R.anim.button_animation);
        //
        ImageView imgSlide = v.findViewById(R.id.intro_img);
        TextView title = v.findViewById(R.id.intro_title);
        TextView description = v.findViewById(R.id.intro_description);
        //
        // imgSlide.setAnimation(animation);
        // title.setAnimation(animation);
        // description.setAnimation(animation);
        //
        imgSlide.setImageResource(mListScreen.get(position).getScreenImg());
        title.setText(mListScreen.get(position).getTitle());
        description.setText(mListScreen.get(position).getDescription());
        //
        container.addView(v);

        //
        return v;
    }

    @Override
    public int getCount() {
        return mListScreen.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
