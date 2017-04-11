package com.whitelabel.app.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.whitelabel.app.R;
import com.whitelabel.app.activity.HomeActivity;
import com.whitelabel.app.activity.NotificationDetailActivity;
import com.whitelabel.app.adapter.HomeNotificationListAdapter;
import com.whitelabel.app.application.WhiteLabelApplication;
import com.whitelabel.app.dao.NotificationDao;
import com.whitelabel.app.model.NotificationCell;
import com.whitelabel.app.model.SVRAppserviceNotificationListReturnEntity;
import com.whitelabel.app.utils.BadgeUtils;
import com.whitelabel.app.utils.GaTrackHelper;
import com.whitelabel.app.utils.JLogUtils;
import com.whitelabel.app.utils.JViewUtils;
import com.whitelabel.app.utils.RequestErrorHelper;
import com.whitelabel.app.utils.SendBoardUtil;
import com.whitelabel.app.widget.CustomXListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

public class HomeNotificationListFragment extends HomeBaseFragment implements CustomXListView.IXListViewListener,SwipeRefreshLayout.OnRefreshListener {
    private HomeActivity homeActivity;
    private HomeNotificationListAdapter adapter;
    private CustomXListView clistView;
    private Dialog mDialog;
    private LinearLayout rlEmpty;
    private ArrayList<NotificationCell> list;
    private int page = 1;
    private static final int pagesize = 10;
    private int unReadCount;
    private String TAG="HomeNotificationListFragment";
    private final int LOADING=1;
    private final int SUCCESS=2;
    private final int FAILED=3;
    private int currType=LOADING;
    private SwipeRefreshLayout swipeContainer;
    private View view;
    private NotificationDao mDao;
    private RequestErrorHelper requestErrorHelper;
    private View connectionLayout;
    private LinearLayout tryAgain;
    private DataHandler mHandler;
    private boolean isStop=false;
    private String mCurrTag;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        homeActivity = (HomeActivity) activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        JLogUtils.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_home_notification_list, null);
        clistView = (CustomXListView) view.findViewById(R.id.lv_notification);
        rlEmpty = (LinearLayout) view.findViewById(R.id.rl_notification_empty);
        swipeContainer= (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeContainer.setColorSchemeResources(R.color.colorAccent);
        swipeContainer.setOnRefreshListener(this);
        return view;
    }
    @Override
    public void onResume() {
        JLogUtils.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        JLogUtils.d(TAG, "onActivityCreated: ");
        homeActivity.getToolbar().getMenu().clear();
        homeActivity.setTitle(getResources().getString(R.string.NOTIFICATION));
        if(mCommonCallback!=null) {
            mCommonCallback.switchMenu(HomeCommonCallback.MENU_NOTIFICATION);
            mCommonCallback.getToolBar().getMenu().clear();
        }
        connectionLayout=view.findViewById(R.id.connectionBreaks);
        requestErrorHelper=new RequestErrorHelper(getContext(),connectionLayout);
        tryAgain= (LinearLayout) view.findViewById(R.id.try_again);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionLayout.setVisibility(View.GONE);
                mDialog.show();
                newSendRequestToGetList();
            }
        });
        mCurrTag=getClass().getSimpleName();
        mHandler=new DataHandler(homeActivity,this);
        list = new ArrayList<NotificationCell>();
        mDialog = JViewUtils.showProgressDialog(homeActivity);

        mDao=new NotificationDao(TAG,mHandler);
        page=1;
        newSendRequestToGetList();
        adapter = new HomeNotificationListAdapter(homeActivity, list);
        clistView.setAdapter(adapter);
        clistView.setXListViewListener(this);
        clistView.setPullRefreshEnable(false);
        clistView.setPullLoadEnable(true);
        clistView.setHeaderDividersEnabled(false);
        clistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (list.get(position - 1).getUnread() == 1) {
//                    /**
//                     * If it is unread, send request to update its status to 'read'.
//                     */
////                    sendRequestToTellRead(list.get(position - 1).getItems_id());
//                }
                //page = 1;//If go to another fragment,then come back,we should refresh the list from first page.
                //homeActivity.switchFragment(HomeActivity.FRAGMENT_TYPE_HOME_NOTIFICATIONLIST, HomeActivity.FRAGMENT_TYPE_HOME_NOTIFICATIONDETAIL, list.get(position));
                if(position<=list.size()) {
                    Intent intent = new Intent(homeActivity, NotificationDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("data", list.get(position - 1));
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 100);
                    homeActivity.overridePendingTransition(R.anim.enter_righttoleft,
                            R.anim.exit_righttoleft);
                }
            }
        });

    }
    public void showProgressDialog(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                swipeContainer.setRefreshing(true);
            }
        });
    }
    private boolean allowSendRequest(){
        String deviceToken= WhiteLabelApplication.getPhoneConfiguration().getRegistrationToken();
        boolean isSignIn= WhiteLabelApplication.getAppConfiguration().isSignIn(homeActivity);
        if(("".equals(deviceToken)||deviceToken==null)&&!isSignIn){
            return  false;
        }
        return true;
    }
    private void sendRequestToGetList() {
        currType=LOADING;
        String session_key= WhiteLabelApplication.getAppConfiguration().getUser() == null ? null : WhiteLabelApplication.getAppConfiguration().getUser().getSessionKey();
        String device_token= WhiteLabelApplication.getPhoneConfiguration().getRegistrationToken();
        mDao.sendRequestToGetList(session_key, String.valueOf(page), String.valueOf(pagesize), device_token);
    }
    private static final class DataHandler extends Handler{
        private final WeakReference<HomeActivity> mActivity;
        private final WeakReference<HomeNotificationListFragment> mFragment;
        public DataHandler(HomeActivity activity,HomeNotificationListFragment fragment){
            mActivity=new WeakReference<HomeActivity>(activity);
            mFragment=new WeakReference<HomeNotificationListFragment>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            if(mActivity.get()==null||mFragment.get()==null||!mFragment.get().isAdded()){
                return;
            }
            final HomeNotificationListFragment fragment=mFragment.get();
            if(fragment.mDialog!=null){
                fragment. mDialog.cancel();
            }
            switch (msg.what){
                case NotificationDao.REQUEST_GETLIST:
                    fragment.swipeContainer.setRefreshing(false);
                    if(msg.arg1==NotificationDao.RESPONSE_SUCCESS){
                        fragment.connectionLayout.setVisibility(View.GONE);
                        SVRAppserviceNotificationListReturnEntity notificationListReturnEntity = (SVRAppserviceNotificationListReturnEntity)  msg.obj;
                        if (notificationListReturnEntity.getStatus() == 1&&notificationListReturnEntity.getPage()==fragment.page) {
                            fragment.clistView.stopLoadMore();
                            if (fragment.page == 1) {
                                fragment.list.clear();
                                fragment.currType = fragment.SUCCESS;
                                if (notificationListReturnEntity == null || notificationListReturnEntity.getNotification_items() == null || notificationListReturnEntity.getNotification_items().length == 0) {
                                    fragment.rlEmpty.setVisibility(View.VISIBLE);
                                }
                            }
                            fragment.unReadCount = notificationListReturnEntity.getNotification_unread_count();
                             mActivity.get().setTitleNum(fragment.unReadCount);
                            JLogUtils.d("jay","page="+fragment.page);
                            JLogUtils.d("jay","length="+notificationListReturnEntity.getNotification_items().length);
                            if (notificationListReturnEntity.getNotification_items().length > 0) {
                                fragment.clistView.setPullLoadEnable(true);
                                fragment.page++;
                                fragment.rlEmpty.setVisibility(View.INVISIBLE);
                                //update adapter
                                fragment.list.addAll(Arrays.asList(notificationListReturnEntity.getNotification_items()));
                                fragment. adapter.notifyDataSetChanged();
                            } else {
                                fragment.clistView.setPullLoadEnable(false);
                                fragment.page=1;
                            }
                        }
                    }else{
                        if (fragment.page == 1) {
                            fragment.currType = fragment.FAILED;
                        }
                        fragment.clistView.stopLoadMore();
                    }
                    break;
                case NotificationDao.REQUEST_NOTIFICATIONDETAIL:
                    if(msg.arg1==NotificationDao.RESPONSE_SUCCESS&&fragment.currType==fragment.SUCCESS){
                        try {
                            NotificationCell cell= (NotificationCell) msg.obj;
                            boolean isexist=false;
                            if(fragment.list!=null&&cell!=null){
                                for(int i=0;i<fragment.list.size();i++){
                                    if(cell.getItems_id().equals(fragment.list.get(i).getItems_id())){
                                        isexist=true;
                                        break;
                                    }
                                }
                                if(!isexist){
                                    cell.setUnread(1);
                                    fragment.unReadCount++;
                                    fragment.mCommonCallback.setTitleNum(fragment.unReadCount);
                                    mActivity.get().setTitleNum(fragment.unReadCount);

                                    fragment.rlEmpty.setVisibility(View.GONE);
                                    fragment.list.add(0, cell);
                                    fragment.adapter.notifyDataSetChanged();
                                }
                            }
                        }catch (Exception ex){
                            ex.getStackTrace();
                        }

                    }
                    break;
                case NotificationDao.REQUEST_ERROR:
                    fragment.swipeContainer.setRefreshing(false);
                    fragment.currType = fragment.FAILED;
                    fragment.clistView.stopLoadMore();
                    fragment.currType = fragment.FAILED;
                    if(fragment.page==1) {
                        fragment.requestErrorHelper.showConnectionBreaks(msg);
                    }else{
                        fragment.requestErrorHelper.showNetWorkErrorToast(msg);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }
    public void  refresh(int type,String id){
        if(homeActivity!=null){
            JLogUtils.i(mCurrTag,"refresh(int type,String id)");
            if(type== SendBoardUtil.LOGINCODE){
                page=1;
                newSendRequestToGetList();
            }else if(type== SendBoardUtil.NOTIFICATION){
                if(!TextUtils.isEmpty(id)&&currType==SUCCESS) {
                    mDao.getNotificationDetail(WhiteLabelApplication.getAppConfiguration().getUser() == null ? null : WhiteLabelApplication.getAppConfiguration().getUser().getSessionKey(), id,"0","");
                }
            }else if(type==SendBoardUtil.READFLAG){
                setRead(id);
            }
          }
    }

    public void setRead(String id ){
        JLogUtils.i(TAG,"notificationId:"+id);
        if(list!=null&&!TextUtils.isEmpty(id)) {
            for (int i = 0; i < list.size(); i++) {
                JLogUtils.i(TAG,"getItems_id:"+list.get(i).getItems_id());
                if(id.equals(list.get(i).getItems_id())){
                    JLogUtils.i(TAG,"right");
                    unReadCount--;
                    mCommonCallback.setTitleNum(unReadCount);
                    if(getActivity()!=null) {
                        BadgeUtils.setBadge(getActivity(), unReadCount);
                    }
                    list.get(i).setUnread(0);
                    break;
                }
            }
            if(adapter!=null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
//        if(isStop){
//            JLogUtils.i(mCurrTag,"onStart()");
//            newSendRequestToGetList();
//        }
        super.onStart();
//        GaTrackHelper.getInstance().googleAnalyticsReportActivity(homeActivity, true);
//        GaTrackHelper.getInstance().googleAnalytics("Notification list screen", homeActivity);
//        JLogUtils.i("googleGA_screen", "Notification list screen");
    }
    @Override
    public void onStop() {
        super.onStop();
//        page=1;
        isStop=true;
        GaTrackHelper.getInstance().googleAnalyticsReportActivity(homeActivity, false);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onRefresh() {
        page = 1;
        newSendRequestToGetList();
    }
    @Override
    public void onLoadMore() {
//        if(page!=1) {
            newSendRequestToGetList();
//        }else{
//            clistView.stopLoadMore();
//        }
    }
    public void newSendRequestToGetList(){
//        if(allowSendRequest()){
            sendRequestToGetList();
//        }else{
//            if (mDialog != null) {
//                mDialog.cancel();
//            }
////            mHandler.post(new Runnable() {
////                @Override
////                public void run() {
////                    swipeContainer.setRefreshing(false);
////                }
////            });
//            rlEmpty.setVisibility(View.VISIBLE);
//            clistView.stopLoadMore();
//        }
    }
}
