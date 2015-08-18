package hanks.com.guideview;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * by Hanks
 */
public class MainActivity extends FragmentActivity {

    private ViewPager viewpager;
    private ImageView iv_device, iv_initial_phone;
    private ImageView iv_final_photo;
    private List<Fragment> fragmentList = new ArrayList<>();
    private LinearLayout ll_rows, ll_comments;
    private float currentPosition = 0;
    private GuideFragment fragment00, fragment01, fragment02, fragment03;
    private TextView tv_avatar_you;
    private TextView tv_register;

    private BlurringView blurringView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        setFullscreen();

        fragment00 = new GuideFragment();
        fragment01 = new GuideFragment();
        fragment02 = new GuideFragment();
        fragment03 = new GuideFragment();

        fragmentList.add(fragment00);
        fragmentList.add(fragment01);
        fragmentList.add(fragment02);
        fragmentList.add(fragment03);


        viewpager = (ViewPager) findViewById(R.id.viewpager);
        iv_device = (ImageView) findViewById(R.id.iv_device);
        iv_final_photo = (ImageView) findViewById(R.id.iv_final_photo);
        tv_avatar_you = (TextView) findViewById(R.id.tv_avatar_you);
        tv_register = (TextView) findViewById(R.id.tv_register);

        blurringView = (BlurringView) findViewById(R.id.blurringView);

        ll_rows = (LinearLayout) findViewById(R.id.ll_rows);
        ll_comments = (LinearLayout) findViewById(R.id.ll_comments);

        iv_initial_phone = (ImageView) findViewById(R.id.iv_initial_phone);

        View blurredView = findViewById(R.id.blurredView);
        blurringView.setBlurredView(blurredView);
        
        //创建adapter
        GuideAdapter adapter = new GuideAdapter(getSupportFragmentManager());
        //设置viewpager缓存页数,默认的缓存一页,因为引导页共有4页,
        //所以设置缓存3页,这样所以page在滑动过程中不会重新创建
        viewpager.setOffscreenPageLimit(3);
        viewpager.setAdapter(adapter);
        viewpager.setPageTransformer(true, new HKTransformer());
    }

    private void setFullscreen() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

    class GuideAdapter extends FragmentPagerAdapter {
        public GuideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }


    /**
     * by Hanks
     */
    class HKTransformer implements ViewPager.PageTransformer {
        @Override
        public void transformPage(View view, float position) {
            if (fragment00.getView() == view) {
                Log.i("", "view:    " + view + "position= " + position);
                currentPosition = position;
            }

            blurringView.invalidate();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
            } else if (position <= 1) { // (0,1]
                // Fade the page out.

                float p = Math.abs(position);
                float f = (1 - p);

                Log.i("", "p= " + p);
                // p : 1~0
                // f : 0~1

                iv_final_photo.setPivotY(0f);
                iv_final_photo.setPivotX(iv_final_photo.getWidth() / 2);

                if (-1 < currentPosition && currentPosition <= 0) {
                    // A ~ B 界面的动画

                    iv_initial_phone.setTranslationY(-600 * f);
                    iv_initial_phone.setScaleX(0.5f * p + 0.5f);
                    iv_initial_phone.setScaleY(0.5f * p + 0.5f);
                    iv_device.setScaleX(1 + 2 * f);

                    if (p > 0.5 && p <= 1) {
                        iv_device.setAlpha(2 * p - 1);
                    } else {
                        iv_device.setAlpha(0f);
                    }

                    ll_comments.setTranslationY(800 * p);
                    ll_comments.setAlpha(f);
                    ll_comments.setScaleX(2 - f);
                    ll_comments.setScaleY(2 - f);

                    ll_rows.setTranslationY(-1000 - 500 * p);
                    ll_rows.setAlpha(0.5f);
                    iv_final_photo.setTranslationY(-1000 - 500 * p);
                    iv_final_photo.setAlpha(0.5f);

                    tv_avatar_you.setTranslationY(-300);

                    tv_register.setTranslationY(300);

                } else if (-2 < currentPosition && currentPosition <= -1) {
                    // B ~ C 界面的动画

                    iv_initial_phone.setTranslationY(-600 + -300 * f);

                    ll_comments.setAlpha(p);

                    ll_rows.setTranslationY(-1000 * p);
                    ll_rows.setAlpha(0.5f + 0.5f * f);

                    iv_final_photo.setTranslationY(-1000 * p);
                    iv_final_photo.setAlpha(0.5f + 0.5f * f);

                    tv_avatar_you.setTranslationY(-300);
                    tv_register.setTranslationY(300);
                } else if (-3 < currentPosition && currentPosition <= -2) {
                    // C ~ D 界面的动画


                    iv_final_photo.setScaleX(1 + 3 * f); //1~3
                    iv_final_photo.setScaleY(1 + 3 * f); //1~3

                    for (int i = 0; i < ll_rows.getChildCount(); i++) {
                        View child = ll_rows.getChildAt(i);
                        child.setAlpha(p);
                        if (i % 2 == 0) {
                            child.setTranslationX(100 * f);
                        } else {
                            child.setTranslationX(-100 * f);
                        }
                    }

                    tv_avatar_you.setTranslationY(-300 + 300 * f);

                    tv_register.setTranslationY(300 - 300 * f);
                }


            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
            }
        }
    }
}
