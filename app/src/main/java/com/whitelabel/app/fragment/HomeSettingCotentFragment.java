package com.whitelabel.app.fragment;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.whitelabel.app.Const;
import com.whitelabel.app.R;
import com.whitelabel.app.activity.LoginRegisterActivity;
import com.whitelabel.app.WhiteLabelApplication;
import com.whitelabel.app.dao.OtherDao;
import com.whitelabel.app.ui.login.SettingContract;
import com.whitelabel.app.utils.AppUtils;
import com.whitelabel.app.utils.FirebaseEventUtils;
import com.whitelabel.app.utils.GaTrackHelper;
import com.whitelabel.app.utils.JLogUtils;
import com.whitelabel.app.utils.JStorageUtils;
import com.whitelabel.app.utils.JToolUtils;
import com.whitelabel.app.utils.JViewUtils;
import com.whitelabel.app.utils.RequestErrorHelper;
import com.whitelabel.app.utils.logger.Logger;
import com.whitelabel.app.widget.CustomMyDialog;
import com.whitelabel.app.widget.CustomSwitch;
import com.whitelabel.app.widget.MultiSwitchButton;
import java.lang.ref.WeakReference;

import injection.components.DaggerPresenterComponent1;
import injection.modules.PresenterModule;

/**
 * Created by imaginato on 2015/8/21.
 */
public class HomeSettingCotentFragment extends HomeBaseFragment<SettingContract.Presenter> implements View.OnClickListener,SettingContract.View{
    private Activity homeActivity;
    private View view;
    private Dialog mDialog;
    private  boolean signing=false;
    private OtherDao mOtherDao;
    public static  final  int CODE=8000;
    public static  final  String NEWSLETTER_SUBSCRIBED_OPEN = "1";
    public static  final  String NEWSLETTER_SUBSCRIBED_CLOSE = "0";

    private CustomSwitch switchUserCheck;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        homeActivity= (Activity) activity;
    }

    @Override
    public void inject() {
        super.inject();
        DaggerPresenterComponent1.builder().applicationComponent(WhiteLabelApplication.getApplicationComponent()).
            presenterModule(new PresenterModule(getActivity())).build().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_settingscontent,null);
        return view;
    }
    private void setAppVersionName(TextView version) {
        if(!TextUtils.isEmpty(AppUtils.getAppVersionName(homeActivity))){
            version.setText(" "+ AppUtils.getAppVersionName(homeActivity));
        }
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mCommonCallback!=null) {
            mCommonCallback.switchMenu(HomeCommonCallback.MENU_SETTING);
            mCommonCallback.getToolBar().getMenu().clear();
            mCommonCallback.setTitle(getResources().getString(R.string.settings));
        }
        TextView textView_cancle = (TextView) view.findViewById(R.id.home_search_cancel);
        String TAG = this.getClass().getSimpleName();
        RelativeLayout rlSettingRate = (RelativeLayout) view.findViewById(R.id.rl_setting_rate);
        rlSettingRate.setOnClickListener(this);
        TextView mVersion = (TextView) view.findViewById(R.id.tv_setting_version_name);

        setAppVersionName(mVersion);
        view.findViewById(R.id.rl_sound).setVisibility(View.GONE);
        textView_cancle.setOnClickListener(this);
        TextView sign_out = (TextView) view.findViewById(R.id.sign_out);
        JViewUtils.setStrokeButtonGlobalStyle(getActivity(), sign_out);
        sign_out.setOnClickListener(this);
        RelativeLayout rlBack = (RelativeLayout) view.findViewById(R.id.rl_back);
        mPresenter.getUserAgreement();
        switchUserCheck = (CustomSwitch) view.findViewById(R.id.switch_user_check);
        switchUserCheck.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              String userAgreement;
              if (switchUserCheck.isChecked()){
                  userAgreement=NEWSLETTER_SUBSCRIBED_OPEN;
                  gaEvent(Const.GA.EVENT_SETTINGS,getString(R.string.setting_receiver_newsletters),Const.GA.EVENT_SETTINGS_NEWSLETTERS_SELECT);
              }else {
                  userAgreement=NEWSLETTER_SUBSCRIBED_CLOSE;
                  gaEvent(Const.GA.EVENT_SETTINGS,getString(R.string.setting_receiver_newsletters),Const.GA.EVENT_SETTINGS_NEWSLETTERS_UNSELECT);
              }
              mPresenter.setUserAgreement(userAgreement);
          }
      });
        rlBack.setOnClickListener(this);

        //TODO joyson requirement diff style
//        MultiSwitchButton switchButton = (MultiSwitchButton) view.findViewById(R.id.swithch_button1);
//        if(isOpen==1){
//            switchButton.setCheckedImmediately(true);
//        }else{
//            switchButton.setCheckedImmediately(false);
//        }
        DataHandler dataHandler = new DataHandler(homeActivity, this);
        mOtherDao=new OtherDao(TAG, dataHandler);
        MultiSwitchButton sbClosedSound = (MultiSwitchButton) view.findViewById(R.id.sb_close_sound);
        if(WhiteLabelApplication.getInstance().getAppConfiguration().isSignIn(getActivity())) {
            sbClosedSound.setCheckedImmediately(WhiteLabelApplication.getInstance().getAppConfiguration().getUser().isClosedSound());
        }
        sbClosedSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (WhiteLabelApplication.getInstance().getAppConfiguration().isSignIn(getActivity())&&getActivity()!=null){
                    WhiteLabelApplication.getInstance().getAppConfiguration().getUser().setClosedSound(isChecked);
                    WhiteLabelApplication.getInstance().getAppConfiguration().updateUserData(getActivity(), WhiteLabelApplication.getInstance().getAppConfiguration().getUser());
                }
            }
        });
    }

    @Override
    public void showErrorMsg(String errorMsg) {

    }

    @Override
    public void setSubscriberSuccess(boolean isSuccess) {
        if (!isSuccess){
            switchUserCheck.setChecked(!switchUserCheck.isChecked());
        }
    }

    @Override
    public void getIsSubscriber(boolean isSuccess) {
        switchUserCheck.setChecked(isSuccess);
    }

    public  static final class DataHandler extends Handler{
        private final WeakReference<Activity> mActivity;
        private final WeakReference<HomeSettingCotentFragment> mFragment;
        public DataHandler(Activity activity,HomeSettingCotentFragment fragment){
                mActivity=new WeakReference<>(activity);
                mFragment=new WeakReference<>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            if(mActivity.get()==null||mFragment.get()==null){
                return;
            }
            switch (msg.what){
                case OtherDao.REQUEST_SUBSCRIBER:
                    break;
                case OtherDao.REQUEST_LOGOUT:
                    if (mFragment.get().mDialog != null) {
                        mFragment.get().mDialog.cancel();
                    }
                    //用户退出后，将不再显示AppRate
                    JStorageUtils.notShowAppRate(mActivity.get());
                    if(msg.arg1==OtherDao.RESPONSE_SUCCESS){
                        try {
                            GaTrackHelper.getInstance().googleAnalyticsEvent("Account Action",
                                    "Sign Out",
                                    null,
                                    Long.valueOf(mFragment.get().CustomerId));
                        }catch (Exception ex){
                            ex.getStackTrace();
                        }
                        try{
                            FirebaseEventUtils.getInstance().customizedSignOut(mActivity.get(), WhiteLabelApplication.getAppConfiguration().getUserInfo(mActivity.get()).getLoginType());
                        }catch (Exception ex){
                            ex.getMessage();
                        }
                        WhiteLabelApplication.getAppConfiguration().signOut(mActivity.get());
                        Intent intent = new Intent(mActivity.get(), LoginRegisterActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("Activity", "start");
                        intent.putExtras(bundle);
                        mActivity.get().startActivityForResult(intent, CODE);
                        mActivity.get().overridePendingTransition(R.anim.enter_bottom_top, R.anim.exit_bottom_top);
                        mFragment.get().signing = false;
                        mActivity.get().finish();
                        JLogUtils.i("googleGA", "Sign Out");
                    }else{
                        String errorMsg= (String) msg.obj;
                        mFragment.get().signing = false;
                        if ((!JToolUtils.expireHandler(mActivity.get(),errorMsg,1000))) {
                          Toast.makeText(mActivity.get(),errorMsg+"",Toast.LENGTH_LONG).show();
                         }
                    }
                    break;
                case OtherDao.REQUEST_ERROR:
                    if (mFragment.get().mDialog != null) {
                        mFragment.get().mDialog.cancel();
                    }
                    mFragment.get().signing = false;
                    RequestErrorHelper requestErrorHelper=new RequestErrorHelper(mActivity.get());
                    requestErrorHelper.showNetWorkErrorToast(msg);
                    break;

            }
            super.handleMessage(msg);
        }
    }
    private void showSignOutDialogPrompt(){
        CustomMyDialog.Builder builder = new CustomMyDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.are_you_sure_you_want_signout));
        builder.setPositiveButton(getString(R.string.yes_upp), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                CustomerId = WhiteLabelApplication.getAppConfiguration().getUser().getId();
                signing = true;
                mDialog = JViewUtils.showProgressDialog(homeActivity);
                mOtherDao.signOut(WhiteLabelApplication.getPhoneConfiguration().getRegistrationToken(), WhiteLabelApplication.getAppConfiguration().getUser().getSessionKey());
            }
        });
        builder.setNegativeButton(getString(R.string.no_upp),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        mDialog = builder.create();
        Window win = mDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        win.setAttributes(lp);
        mDialog.setCancelable(false);
        win.setWindowAnimations(R.style.dialogAnim);
        mDialog.show();
    }
    private String CustomerId;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_setting_rate:
                gaEvent(Const.GA.EVENT_SETTINGS,Const.GA.EVENT_SETTINGS_RATE_APP,"");
                JToolUtils.openPlayStore(homeActivity);
                break;
            case R.id.sign_out:
                if(!signing&& WhiteLabelApplication.getAppConfiguration().getUser()!=null) {
                    showSignOutDialogPrompt();
                }
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onStart() {
        super.onStart();
        GaTrackHelper.getInstance().googleAnalytics("Settings Screen", homeActivity);
    }

    private void gaEvent(String categoryName,String actionName,String eventLabel){
        GaTrackHelper.getInstance().googleAnalyticsEvent(categoryName,
            actionName,
            eventLabel,
            Long.valueOf(WhiteLabelApplication.getAppConfiguration().getUser().getId()));
    }
}
