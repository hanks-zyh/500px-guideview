# 500px-guideview
500px guideview demo
----
实现原理：[一步一步实现500px引导动画 – 酷酷哒](http://hanks.xyz/2015/08/18/%E4%B8%80%E6%AD%A5%E4%B8%80%E6%AD%A5%E5%AE%9E%E7%8E%B0500px%E5%BC%95%E5%AF%BC%E5%8A%A8%E7%94%BB_--_%E9%85%B7%E9%85%B7%E5%93%92/)

![](https://github.com/hanks-zyh/500px-guideview/blob/master/demo.gif)
 
---
 
>[博客主页](http://blog.csdn.net/hpu_zyh)  |   [简书](http://www.jianshu.com/users/3e9f552e6ba7)  |  [知乎](http://www.zhihu.com/people/yuhan-zhang-36)  |  [微博](http://weibo.com/u/2359002991)  |  [github](https://github.com/hanks-zyh)


下了500px应用, 瞬间被它的引导动画吸引住了,下面一步一步来实现引导动画

## 最终效果图:

![](http://file.bmob.cn/M01/09/67/oYYBAFXS0giAVZ_yAFK58BsXp6I373.gif)   

---
清晰版
 ![](http://file.bmob.cn/M01/09/76/oYYBAFXS1AeADqx8AERLXngnTHk663.gif)

下面的小圆点简单的,就先省略,重点在切换动画

--------------------------
### 创建viewpager
可以左右随手指滑动的只有底部文字部分,所以ViewPager中存放的布局是底部文字部分
`activity_main.xml`
```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#fff">
    <android.support.v4.view.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</RelativeLayout>
```
先添加viewpager控件

`MainActivity.java`
```
//创建adapter
GuideAdapter adapter = new GuideAdapter(getFragmentManager());
//设置viewpager缓存页数,默认的缓存一页,因为引导页共有4页,
//所以设置缓存3页,这样所以page在滑动过程中不会重新创建
viewpager.setOffscreenPageLimit(3);
viewpager.setAdapter(adapter);
```
在`MainActivity`中,首先获取到xml中的viewpager,为viewpager设置Adapter,并且设置viewpage的缓存页数
```
   private List<Fragment> fragmentList = new ArrayList<>();
```
创建了用于填充viewpager的fragmentList
```
fragment00 = new GuideFragment();
fragment01 = new GuideFragment();
fragment02 = new GuideFragment();
fragment03 = new GuideFragment();

fragmentList.add(fragment00);
fragmentList.add(fragment01);
fragmentList.add(fragment02);
fragmentList.add(fragment03);
```
初始化fragmentList

```
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
```
GuideAdapter 继承FragmentPagerAdapter


```
/**
 * Created by Hanks on 2015/8/13.
 */
public class GuideFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide, container, false);
        return view;
    }
}
```
在Fragment中只是展示了一下布局
![这里写图片描述](http://img.blog.csdn.net/20150818114547069)

运行效果
 ![这里写图片描述](http://img.blog.csdn.net/20150818114332478)


-----
##开始引导动画

```
//给viewpager设置Pagetransformer
viewpager.setPageTransformer(false, new HKTransformer());
```
```
/**
 * by Hanks
 */
class HKTransformer implements ViewPager.PageTransformer {
    @Override
    public void transformPage(View view, float position) {
        Log.i("", "view:    " + view + "position= " + position);
    }
}
```
观察滑动过程中**view**和**position**的值

从第0页到第1页,
![这里写图片描述](http://img.blog.csdn.net/20150818114914483)

![这里写图片描述](http://img.blog.csdn.net/20150818115006696)
可以看到4个页面的对应的position 从 0, 1, 2, 3 变化为 -1, 0, 1, 2

然后从第1页滑动到第2页
![这里写图片描述](http://img.blog.csdn.net/20150818115021583)

![这里写图片描述](http://img.blog.csdn.net/20150818115031865)
可以看到4个页面的对应的position 从 -1, 0, 1, 2 变化为 -2, -1, 0, 1

继续观察可以看到其中的规律,这里不再贴出图片了.

###观察滑动规律
假设4个页面分别为A,B,C,D
只看第0个页面

 - 从 A-B,  position:  0 ~ -1
 - 从 B-C,  position: -1 ~ -2
 - 从 C-D,  position: -2 ~ -3

所以可以以其中一个view的position为标准来确定当前滑动的是哪个页面
```
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


            if (-1 < currentPosition && currentPosition <= 0) {
               // A ~ B 之间的动画


            } else if (-2 < currentPosition && currentPosition <= -1) {
                //B ~  C 之间的动画


            } else if (-3 < currentPosition && currentPosition <= -2) {
                //C ~  D 之间的动画

            }


        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
        }
    }
}
```

 在position处于**(0,1]**时,表示两个页面正在滑动切换,可以打印一下看看,然后就是基于上面的规律, 根据**currentPosition**来判断是从在哪两个页面之间滑动

###开始动画

####A~B界面的动画
```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#fff">


    <ImageView
        android:id="@+id/iv_device"
        android:layout_width="340dp"
        android:layout_height="500dp"
        android:layout_centerInParent="true"
        android:scaleType="centerInside"
        android:src="@drawable/tour_device" />

    <ImageView
        android:id="@+id/iv_initial_phone"
        android:layout_width="200dp"
        android:layout_height="400dp"
        android:layout_centerInParent="true"
        android:scaleType="centerCrop"
        android:src="@drawable/tour_initial_photo" />

    <android.support.v4.view.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></android.support.v4.view.ViewPager>


</RelativeLayout>
```
一个手机边框 :
- **scaleX : 1 ~ 2**
- **alpha : 1 ~ 0**

中间"大人小孩"图片 :
 **scaleX : 1 ~ 0.5**
 **scaleY : 1 ~ 0.5**
 **translationY: 0 ~ -600**

评论模块:
**scaleX : 2 ~ 1**
 **scaleY : 2 ~ 1**
 **translationY: 800 ~ 0**
 **alpha :  ~ 1**
```
iv_initial_phone.setTranslationY(-600 * f);
iv_initial_phone.setScaleX(0.5f * p + 0.5f);
iv_initial_phone.setScaleY(0.5f * p + 0.5f);
iv_device.setScaleX(1 + 2 * f);

//仔细观察iv_device是在滑到中间时就完全alpha=0了
if (p > 0.5 && p <= 1) {
    iv_device.setAlpha(2 * p - 1);
} else {
    iv_device.setAlpha(0f);
}
ll_comments.setTranslationY(800 * p);
ll_comments.setAlpha(f);
ll_comments.setScaleX(2 - f);
ll_comments.setScaleY(2 - f);
```
运行效果  
![这里写图片描述](http://img.blog.csdn.net/20150818132835708)

####B~C界面的动画
布局文件
```
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#fff">

    <LinearLayout
        android:id="@+id/ll_comments"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:orientation="vertical">

        <TextView
            android:layout_width="200dp"
            android:layout_height="63dp"
            android:layout_gravity="right"
            android:drawableLeft="@drawable/tour_avatar1"
            android:drawablePadding="8dp"
            android:gravity="center_vertical|left"
            android:text="Kaitlyn 收藏了\n您的照片\n刚刚"
            android:textColor="#111"
            android:textSize="14sp" />

        <TextView
            android:layout_width="200dp"
            android:layout_height="63dp"
            android:layout_gravity="left"
            android:drawablePadding="8dp"
            android:drawableRight="@drawable/tour_avatar2"
            android:gravity="center_vertical|right"
            android:text="Adam 评论了\n您的照片\n刚刚"
            android:textColor="#111"
            android:textSize="14sp" />

        <TextView
            android:layout_width="200dp"
            android:layout_height="63dp"
            android:layout_gravity="right"
            android:drawableLeft="@drawable/tour_avatar3"
            android:drawablePadding="8dp"
            android:gravity="center_vertical|left"
            android:text="Kaitlyn 收藏了\n您的照片\n刚刚"
            android:textColor="#111"
            android:textSize="14sp" />

        <TextView
            android:layout_width="200dp"
            android:layout_height="63dp"
            android:layout_gravity="left"
            android:drawablePadding="8dp"
            android:drawableRight="@drawable/tour_avatar4"
            android:gravity="right|center_vertical"
            android:text="Adam 评论了\n您的照片\n刚刚"
            android:textColor="#111"
            android:textSize="14sp" />
    </LinearLayout>
    <LinearLayout
        android:id="@+id/ll_rows"
        android:layout_width="600dp"
        android:layout_height="match_parent"
        android:layout_centerHorizontal="true"
        android:gravity="center_horizontal"
        android:orientation="vertical">

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row1" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row2" />

        <ImageView
            android:layout_width="843dp"
            android:layout_height="72dp"
            android:layout_gravity="center_horizontal"
            android:layout_marginLeft="5.5dp"
            android:scaleType="fitXY"
            android:src="@drawable/row3" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row4" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row5" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row6" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row7" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row8" />

        <ImageView
            android:layout_width="600dp"
            android:layout_height="72dp"
            android:scaleType="centerCrop"
            android:src="@drawable/row9" />
    </LinearLayout>
    <ImageView
        android:id="@+id/iv_device"
        android:layout_width="340dp"
        android:layout_height="500dp"
        android:layout_centerInParent="true"
        android:scaleType="centerInside"
        android:src="@drawable/tour_device" />


    <ImageView
        android:id="@+id/iv_initial_phone"
        android:layout_width="200dp"
        android:layout_height="400dp"
        android:layout_centerInParent="true"
        android:scaleType="centerCrop"
        android:src="@drawable/tour_initial_photo" />

    <ImageView
        android:id="@+id/iv_final_photo"
        android:layout_width="48dp"
        android:layout_height="72dp"
        android:layout_centerHorizontal="true"
        android:layout_marginTop="144dp"
        android:scaleType="centerCrop"
        android:src="@drawable/tour_final_photo" />


    <android.support.v4.view.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"></android.support.v4.view.ViewPager>


</RelativeLayout>

```

"大人小孩"图片:
**translationY : -600 ~ -900**

评论模块:
**alpha : 1 ~ 0**

横条图片模块:
**translationY : -1000 ~ 0**
**alpha : 1 ~ 0.5**

横条图片中间图片(这是一个单独的图片,后面的动画用到):
**translationY : -1000 ~ 0**
**alpha : 0.5 ~ 1**

```
 iv_initial_phone.setTranslationY(-600 + -300 * f);

ll_comments.setAlpha(p);

ll_rows.setTranslationY(-1000 * p);
ll_rows.setAlpha(0.5f + 0.5f * f);

iv_final_photo.setTranslationY(-1000 * p);
iv_final_photo.setAlpha(0.5f + 0.5f * f);
```
效果:
![这里写图片描述](http://img.blog.csdn.net/20150818133033145)

####C~D界面的动画

设置缩放中心点
```
iv_final_photo.setPivotY(0f);
iv_final_photo.setPivotX(iv_final_photo.getWidth() / 2);
```

最后图片:
**scaleX : 1 ~ 3**
**scaleY : 1 ~ 3**

横条图片模块
*奇数行向右移动, **translationX :  0 ~ -100**
*偶数行向左移动, **translationX :  0 ~ 100**

头像模块:
**translationY :  -300 ~ 0**

注册按钮
**translationX :  300 ~ 0**

```
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
```
运行结果:
![这里写图片描述](http://img.blog.csdn.net/20150818133956984)

----
###最后的模糊效果
看到一篇[博客](http://developers.500px.com/2015/03/17/a-blurring-view-for-android.html) ([中文翻译版](http://www.devtf.cn/?p=158))讲解了500px的模糊效果

项目地址:[https://github.com/500px/500px-android-blur](https://github.com/500px/500px-android-blur)


在build.gradle中添加
```
renderscriptTargetApi 18
renderscriptSupportModeEnabled true
```

将BlurringView.java复制到项目目录

使用BlurringView,

```
   <hanks.com.customview.BlurringView
        android:id="@+id/blurringView"
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:layout_alignParentBottom="true"
        app:blurRadius="11"
        app:downsampleFactor="6"
        app:overlayColor="#99FFFFFF"
        >
    </hanks.com.customview.BlurringView>
```
```
 View blurredView = findViewById(R.id.blurredView);
 blurringView.setBlurredView(blurredView);
```
```
 blurringView.invalidate();
```
最终的xml:
```
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:background="#fff"
    android:orientation="vertical">

    <RelativeLayout
        android:id="@+id/blurredView"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:id="@+id/ll_comments"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:orientation="vertical">

            <TextView
                android:layout_width="200dp"
                android:layout_height="63dp"
                android:layout_gravity="right"
                android:drawableLeft="@drawable/tour_avatar1"
                android:drawablePadding="8dp"
                android:gravity="center_vertical|left"
                android:text="Kaitlyn 收藏了\n您的照片\n刚刚"
                android:textColor="#111"
                android:textSize="14sp" />

            <TextView
                android:layout_width="200dp"
                android:layout_height="63dp"
                android:layout_gravity="left"
                android:drawablePadding="8dp"
                android:drawableRight="@drawable/tour_avatar2"
                android:gravity="center_vertical|right"
                android:text="Adam 评论了\n您的照片\n刚刚"
                android:textColor="#111"
                android:textSize="14sp" />

            <TextView
                android:layout_width="200dp"
                android:layout_height="63dp"
                android:layout_gravity="right"
                android:drawableLeft="@drawable/tour_avatar3"
                android:drawablePadding="8dp"
                android:gravity="center_vertical|left"
                android:text="Kaitlyn 收藏了\n您的照片\n刚刚"
                android:textColor="#111"
                android:textSize="14sp" />

            <TextView
                android:layout_width="200dp"
                android:layout_height="63dp"
                android:layout_gravity="left"
                android:drawablePadding="8dp"
                android:drawableRight="@drawable/tour_avatar4"
                android:gravity="right|center_vertical"
                android:text="Adam 评论了\n您的照片\n刚刚"
                android:textColor="#111"
                android:textSize="14sp" />
        </LinearLayout>

        <LinearLayout
            android:id="@+id/ll_rows"
            android:layout_width="600dp"
            android:layout_height="match_parent"
            android:layout_centerHorizontal="true"
            android:gravity="center_horizontal"
            android:orientation="vertical">

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row1" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row2" />

            <ImageView
                android:layout_width="843dp"
                android:layout_height="72dp"
                android:layout_gravity="center_horizontal"
                android:layout_marginLeft="5.5dp"
                android:scaleType="fitXY"
                android:src="@drawable/row3" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row4" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row5" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row6" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row7" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row8" />

            <ImageView
                android:layout_width="600dp"
                android:layout_height="72dp"
                android:scaleType="centerCrop"
                android:src="@drawable/row9" />
        </LinearLayout>


        <ImageView
            android:id="@+id/iv_device"
            android:layout_width="340dp"
            android:layout_height="500dp"
            android:layout_centerInParent="true"
            android:scaleType="centerInside"
            android:src="@drawable/tour_device" />

        <ImageView
            android:id="@+id/iv_initial_phone"
            android:layout_width="200dp"
            android:layout_height="400dp"
            android:layout_centerInParent="true"
            android:scaleType="centerCrop"
            android:src="@drawable/tour_initial_photo" />

        <ImageView
            android:id="@+id/iv_final_photo"
            android:layout_width="48dp"
            android:layout_height="72dp"
            android:layout_centerHorizontal="true"
            android:layout_marginTop="144dp"
            android:scaleType="centerCrop"
            android:src="@drawable/tour_final_photo" />

        <TextView
            android:id="@+id/tv_avatar_you"
            android:layout_width="wrap_content"
            android:layout_height="90dp"
            android:layout_centerHorizontal="true"
            android:drawableTop="@drawable/tour_avatar_you"
            android:gravity="center"
            android:paddingTop="30dp"
            android:text="您收藏这张照片\n刚刚"
            android:textColor="#222"
            android:textSize="10sp" />
    </RelativeLayout>

    <hanks.com.guideview.BlurringView
        android:id="@+id/blurringView"
        android:layout_width="match_parent"
        android:layout_height="150dp"
        android:layout_alignParentBottom="true"
        app:blurRadius="11"
        app:downsampleFactor="6"
        app:overlayColor="#99FFFFFF"
        >

    </hanks.com.guideview.BlurringView>

    <android.support.v4.view.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        ></android.support.v4.view.ViewPager>


    <TextView
        android:id="@+id/tv_register"
        android:layout_width="80dp"
        android:layout_height="25dp"
        android:layout_alignParentBottom="true"
        android:layout_centerHorizontal="true"
        android:layout_marginBottom="15dp"
        android:background="#2595ec"
        android:gravity="center"
        android:text="注册"
        android:textColor="#fff"
        android:textSize="12sp" />

</RelativeLayout>
```

```
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

```


