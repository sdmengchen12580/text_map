package org.faqrobot.demo_0927.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.faqrobot.demo_0927.R;

import static android.content.ContentValues.TAG;

/**
 * Created by 孟晨 on 2017/9/30.
 */
public class CommomDialog extends Dialog implements View.OnClickListener{
    private BounceBallView bbv1;
    private TextView submitTxt;
    private TextView cancelTxt;
    private RadioGroup radioGroup;

    private Context mContext;
    private int current_number=-1;
    private OnCloseListener listener;
    private MyCheckedChangeListener mylistener;

    public CommomDialog(Context context) {
        super(context);
        this.mContext = context;
    }

    public CommomDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.mContext = context;
    }

    public CommomDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    protected CommomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView(){
        mylistener=new MyCheckedChangeListener();
        bbv1 = (BounceBallView) findViewById(R.id.bbv1);
        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        cancelTxt = (TextView)findViewById(R.id.cancel);
        submitTxt = (TextView)findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(mylistener);
        bbv1.start();
    }

    private class  MyCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            // TODO: 2017/9/30 为什么数字越来越大 
            Log.e( "真正的数字i为: ", i+"" );
            if(i%3==0){
                current_number = 3;
                Log.e(TAG, "onCheckedChanged选择了"+current_number);
            }else{
                current_number = i%3;
                Log.e(TAG, "onCheckedChanged选择了"+current_number);
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(current_number!=-1){
                    current_number = -1;
                    Log.e(TAG, "选了+点取消："+current_number);
                }
                if(listener != null){
                    listener.onClick(this, false,-1);
                }
                bbv1.setTop(-1);
                /**置空radioGroup，不然值会一直增加*/
                this.dismiss();
                break;
            case R.id.submit:
                if(listener != null&&current_number==-1){
                    listener.onClick(this, true,-1);
                    Log.e(TAG, "没选+点确认："+current_number);
                }
                if(listener != null&&current_number!=-1){
                    listener.onClick(this, true,current_number);
                    current_number=-1;
                }
                /**置空radioGroup，不然值会一直增加*/
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm,int which);
    }
}
