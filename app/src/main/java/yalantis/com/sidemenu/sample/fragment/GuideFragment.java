package yalantis.com.sidemenu.sample.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import yalantis.com.sidemenu.sample.R;

public class GuideFragment extends Activity {

    private static final int MAX_VIEWS = 5;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_guide);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new WalkthroughPagerAdapter());
        mViewPager.setOnPageChangeListener(new WalkthroughPageChangeListener());
    }


    class WalkthroughPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_VIEWS;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == (View) object;
        }

        @Override
        public Object instantiateItem(View container, int position) {
            Log.e("walkthrough", "instantiateItem(" + position + ");");
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View imageViewContainer = inflater.inflate(R.layout.walkthrough_single_view, null);
            ImageView imageView = (ImageView) imageViewContainer.findViewById(R.id.image_view);

            switch(position) {
                case 0:
                    imageView.setImageResource(R.drawable.screen2);
                    break;

                case 1:
                    imageView.setImageResource(R.drawable.screenconfirm);
                    break;

                case 2:
                    imageView.setImageResource(R.drawable.scr1);
                    break;

                case 3:
                    imageView.setImageResource(R.drawable.screen0);
                    break;

                case 4:
                    imageView.setImageResource(R.drawable.screen4);
                    break;
            }

            ((ViewPager) container).addView(imageViewContainer, 0);
            return imageViewContainer;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager)container).removeView((View)object);
        }
    }


    class WalkthroughPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            // Here is where you should show change the view of page indicator
            switch(position) {

                case MAX_VIEWS - 1:
                    break;

                default:

            }

        }

    }
}