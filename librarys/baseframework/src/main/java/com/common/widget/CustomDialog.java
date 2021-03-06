package com.common.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.common.R;
import com.common.widget.circularProgressView.CircularProgressView;
import com.common.widget.circularProgressView.IndeterminateDrawable;



/**
 * Created by ray on 2015/7/29.
 */
public class CustomDialog extends Dialog{
    private ImageView imageView;
    private IndeterminateDrawable drawable;
    public static final String TOP="top";
    public static final String BOOTOM="bottom";
    private String mLocation="";
    private CircularProgressView mProgressView;

    public CustomDialog(Context context) {
         this(context, R.style.progressDialog,"");
    }
    public CustomDialog(Context context, String location) {
        this(context, R.style.progressDialog, location);
    }
    public CustomDialog(Context context, int theme, String location) {
        super(context, theme);
        this.mLocation=location;
        if("bottom".equals(mLocation)){
            this.getWindow().getAttributes().gravity = Gravity.BOTTOM;
            setContentView(R.layout.dialog_progress);
            mProgressView = (CircularProgressView) findViewById(R.id.progress_view);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(dip2px(50),dip2px(50));
            params.setMargins(0, dip2px(20),0,dip2px(40));
            mProgressView.setLayoutParams(params);
        }else{
            this.getWindow().getAttributes().gravity = Gravity.CENTER;
            setContentView(R.layout.dialog_progress);

        }
        setCanceledOnTouchOutside(false);
    }
    public  int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    @Override
    public void dismiss() {
        if(isShowing()) {
            if (getContext() instanceof Activity) {
                Activity activity = (Activity) getContext();
                if (activity != null && !activity.isFinishing()) {
                    super.dismiss();
                }
            } else {
                super.dismiss();
            }
        }
    }
    @Override
    public void show() {
        super.show();
    }
    @Override
    public void cancel() {
//        if(drawable!=null) {
//            drawable.stop();
//        }
        super.cancel();
    }


}
