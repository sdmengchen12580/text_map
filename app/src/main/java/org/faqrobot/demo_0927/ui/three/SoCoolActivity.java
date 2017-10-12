package org.faqrobot.demo_0927.ui.three;

import android.app.Activity;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.faqrobot.demo_0927.R;

public class SoCoolActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**因为加了主题的背景色为透明的属性*/
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_so_cool);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title);
        /**沉浸加黑色字体*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        /**代码设置第二个按钮*/
        init_one();
        /**最右下角按钮_________点击按钮改变容器按钮的背景色*/
        init_two();
        /**最左上角的按钮——动态添加2个*/
        init_three();
        /**最右上角*/
        init_four();
        /**按钮的点击换标题————————直接影藏按钮*/
        init();
    }

    /**按钮的点击换标题————————直接影藏按钮*/
    private void init() {
        final FloatingActionButton actionA = (FloatingActionButton) findViewById(R.id.action_a);
        actionA.setOnClickListener(view -> actionA.setTitle("Action A clicked"));

        findViewById(R.id.button_gone).setVisibility(View.GONE);
    }

    /**最右上角*/
    private void init_four() {
        /**移除按钮*/
        final FloatingActionButton removeAction = (FloatingActionButton) findViewById(R.id.button_remove);
        /**通过父容器来移除*/
        removeAction.setOnClickListener(v -> ((FloatingActionsMenu) findViewById(R.id.multiple_actions_down)).removeButton(removeAction));
        /**给原始的按钮中间添加一个椭圆的小圆圈*/
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(getResources().getColor(R.color.white));
        ((FloatingActionButton) findViewById(R.id.setter_drawable)).setIconDrawable(drawable);
    }

    /**最左上角的按钮——动态添加2个*/
    private void init_three() {
        /**容器*/
        FloatingActionsMenu rightLabels = (FloatingActionsMenu) findViewById(R.id.right_labels);
        /**创建第一个，并添加*/
        FloatingActionButton addedOnce = new FloatingActionButton(this);
        addedOnce.setTitle("添加的第一个蓝色");
        rightLabels.addButton(addedOnce);
        /**创建第二个，并添加*/
        FloatingActionButton addedTwice = new FloatingActionButton(this);
        addedTwice.setTitle("添加的第一二个蓝色");
        rightLabels.addButton(addedTwice);
        /**移除第二个*/
        rightLabels.removeButton(addedTwice);
        /**移除完后，在重新添加一次第二个*/
        rightLabels.addButton(addedTwice);
    }

    /**最右下角按钮_________点击按钮改变容器按钮的背景色*/
    private void init_two() {
        /**最右下角action_b按钮*/
        final View actionB = findViewById(R.id.action_b);
        /**代码创建第三个按钮*/
        FloatingActionButton actionC = new FloatingActionButton(getBaseContext());
        /**设置第三个按钮的标题*/
        actionC.setTitle("我是代码创建的第三个按钮");
        actionC.setOnClickListener(view -> actionB.setVisibility(actionB.getVisibility() == View.GONE ? View.VISIBLE : View.GONE));
        /**找到父容器的按钮——添加进入第三个按钮*/
        final FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        menuMultipleActions.addButton(actionC);



        /**点击换背景色——————————isEnabled()*/
        final FloatingActionButton actionEnable = (FloatingActionButton) findViewById(R.id.action_enable);
        actionEnable.setOnClickListener(view -> menuMultipleActions.setEnabled(!menuMultipleActions.isEnabled()));
    }

    /**代码设置第二个按钮*/
    private void init_one() {
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.setter);
        button.setSize(FloatingActionButton.SIZE_MINI);
        button.setColorNormalResId(R.color.pink);
        button.setColorPressedResId(R.color.pink_pressed);
        button.setIcon(R.drawable.ic_fab_star);
        button.setStrokeVisible(false);
    }


}
