package com.whitelabel.app.ui.checkout;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.whitelabel.app.R;
import com.whitelabel.app.activity.BaseActivity;
import com.whitelabel.app.activity.CheckoutPaymentStatusActivity;
import com.whitelabel.app.utils.JLogUtils;
import com.whitelabel.app.utils.JToolUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PayPalPaymentActivity extends com.whitelabel.app.BaseActivity {
    @BindView(R.id.wv_checkout_payment_redirect)
    WebView wvCheckoutPaymentRedirect;
    private String mUrl;
    public final static String  PAYMENT_URL="payment_url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_pal_payment);
        ButterKnife.bind(this);
        mUrl=getIntent().getStringExtra(PAYMENT_URL);
        initToolbar();
        initWebView();
    }
    private void initWebView() {
        WebSettings settings=wvCheckoutPaymentRedirect.getSettings();
        settings.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(wvCheckoutPaymentRedirect, true);
        }
        wvCheckoutPaymentRedirect.setWebViewClient(webViewClient);
        wvCheckoutPaymentRedirect.loadUrl(mUrl,null);
    }
    private WebViewClient  webViewClient=new WebViewClient(){
        private String paymentFaild="checkout/cart/";
        private String paymentSuccess="checkout/onepage/success/";
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//            JLogUtils.i("ray","url:");
//            return false;
//        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            JLogUtils.i("PayPalPaymentActivity","url:"+url);
            if(url.contains(paymentFaild)){
                startPaymentFaildActivity();
                finish();
            }
            if(url.contains(paymentSuccess)){
                startPaymentSuccessActivity();
                finish();
            }
            return false;
        }
    };

    private void startPaymentSuccessActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("payment_status", "1");
        startNextActivity(bundle, CheckoutPaymentStatusActivity.class, true);

    }

    private void startPaymentFaildActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("payment_status", "0");
        startNextActivity(bundle, CheckoutPaymentStatusActivity.class, true);
    }

    private void initToolbar() {
        setTitle(getResources().getString(R.string.CHECKOUT));
        setLeftMenuIcon(JToolUtils.getDrawable(R.drawable.action_back));
    }
}
