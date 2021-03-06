package injection.modules;

import android.app.Activity;

import com.whitelabel.app.data.service.IAccountManager;
import com.whitelabel.app.data.service.IBaseManager;
import com.whitelabel.app.data.service.ICheckoutManager;
import com.whitelabel.app.data.service.ICommodityManager;
import com.whitelabel.app.data.service.IGoogleAnalyticsManager;
import com.whitelabel.app.data.service.IShoppingCartManager;
import com.whitelabel.app.ui.checkout.CheckoutAddAddressContract;
import com.whitelabel.app.ui.checkout.CheckoutAddAddressPresenter;
import com.whitelabel.app.ui.checkout.CheckoutContract;
import com.whitelabel.app.ui.checkout.CheckoutDefaultAddressContract;
import com.whitelabel.app.ui.checkout.CheckoutDefaultAddressPresenter;
import com.whitelabel.app.ui.checkout.CheckoutPresenterImpl;
import com.whitelabel.app.ui.checkout.CheckoutRegisterContract;
import com.whitelabel.app.ui.checkout.CheckoutRegisterPresenter;
import com.whitelabel.app.ui.common.BaseAddressContract;
import com.whitelabel.app.ui.common.BaseAddressPresenter;
import com.whitelabel.app.ui.home.HomeCategoryDetailContract;
import com.whitelabel.app.ui.home.HomeContract;
import com.whitelabel.app.ui.home.HomeHomeContract;
import com.whitelabel.app.ui.home.MainContract;
import com.whitelabel.app.ui.home.ShopBrandContract;
import com.whitelabel.app.ui.home.presenter.HomeCategoryDetailPresenterImpl;
import com.whitelabel.app.ui.home.presenter.HomeHomePresenterImpl;
import com.whitelabel.app.ui.home.presenter.HomePresenterImpl;
import com.whitelabel.app.ui.home.presenter.MainPresenterImpl;
import com.whitelabel.app.ui.home.presenter.ShopBrandPresenterImpl;
import com.whitelabel.app.ui.login.LoginFragmentContract;
import com.whitelabel.app.ui.login.LoginFragmentPresenterImpl;
import com.whitelabel.app.ui.login.SettingContract;
import com.whitelabel.app.ui.login.SettingPresenterImpl;
import com.whitelabel.app.ui.menuMyOrder.MyOrderContract;
import com.whitelabel.app.ui.menuMyOrder.MyOrderPresenter;
import com.whitelabel.app.ui.notifyme.NotifyMeConstract;
import com.whitelabel.app.ui.notifyme.NotifyMePresenterImpl;
import com.whitelabel.app.ui.productdetail.BindProductContract;
import com.whitelabel.app.ui.productdetail.BindProductPresenterImpl;
import com.whitelabel.app.ui.productdetail.ProductDetailContract;
import com.whitelabel.app.ui.productdetail.ProductDetailPresenter;
import com.whitelabel.app.ui.search.SearchContract;
import com.whitelabel.app.ui.search.SearchPresenterImpl;
import com.whitelabel.app.ui.shoppingcart.ShoppingCartVersionContract;
import com.whitelabel.app.ui.shoppingcart.ShoppingCartVersionPresenter;
import com.whitelabel.app.ui.start.StartContract;
import com.whitelabel.app.ui.start.StartPresenterImpl;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import injection.ActivityScope;

/**
 * Created by ray on 2017/5/5.
 */

@Module
public class PresenterModule {

    private Activity mActivity;

    public PresenterModule(Activity activity) {
        this.mActivity = activity;
    }

    @Provides
    @ActivityScope
    public Activity provideActivity() {
        return mActivity;
    }

    @Provides
    @ActivityScope
    public StartContract.Presenter provideStartPresenter(IBaseManager configService,
        IAccountManager accountManager, ICommodityManager iCommodityManager) {
        return new StartPresenterImpl(configService, accountManager, iCommodityManager);
    }

    @Provides
    @ActivityScope
    public HomeContract.Presenter provideHomeFragmentV2Presenter(
        ICommodityManager iCommodityManager, IBaseManager configService, IShoppingCartManager iShoppingCartManager) {
        return new HomePresenterImpl(iCommodityManager, configService, iShoppingCartManager);
    }

    @Provides
    @ActivityScope
    public HomeHomeContract.Presenter provideHomeHomeFragmentV1Presenter(
        ICommodityManager iCommodityManager) {
        return new HomeHomePresenterImpl(iCommodityManager);
    }

    @Provides
    @ActivityScope
    public HomeCategoryDetailContract.Presenter provideHomeHomeV3Presenter(
        ICommodityManager iCommodityManager, IBaseManager iBaseManager) {
        return new HomeCategoryDetailPresenterImpl(iCommodityManager, iBaseManager);
    }

    @Provides
    @ActivityScope
    public ProductDetailContract.Presenter provideProductDetailPresenter(
        ICommodityManager iCommodityManager, IBaseManager iBaseManager,
        IAccountManager iAccountManager, IShoppingCartManager iShoppingCartManager,
        IGoogleAnalyticsManager iGoogleAnalyticsManager) {
        return new ProductDetailPresenter(iAccountManager, iCommodityManager, iBaseManager,
            iShoppingCartManager, iGoogleAnalyticsManager);
    }

    @Provides
    @ActivityScope
    public BaseAddressContract.Presenter provideAddressBasePresenter(
        ICommodityManager iCommodityManager, IAccountManager iAccountManager) {
        return new BaseAddressPresenter(iCommodityManager, iAccountManager);
    }

    @Provides
    @ActivityScope
    public CheckoutDefaultAddressContract.Presenter provideCheckoutDefaultAddressPresenter(
        ICheckoutManager iCheckoutManager, IBaseManager iBaseManager) {
        return new CheckoutDefaultAddressPresenter(iCheckoutManager, iBaseManager);
    }

    @Provides
    @ActivityScope
    public CheckoutAddAddressContract.Presenter provideCheckoutAddAddressPresenter(
        ICheckoutManager iCheckoutManager, IBaseManager iBaseManager) {
        return new CheckoutAddAddressPresenter(iCheckoutManager, iBaseManager);
    }

    @Provides
    @ActivityScope
    public CheckoutContract.Presenter provideCheckoutPresenter(ICheckoutManager iCheckoutManager,
        IBaseManager iBaseManager) {
        return new CheckoutPresenterImpl(iBaseManager, iCheckoutManager);
    }

    @Provides
    @ActivityScope
    public MainContract.Presenter provideMainPresenter(IBaseManager iBaseManager,
        IAccountManager iAccountManager) {
        return new MainPresenterImpl(iBaseManager, iAccountManager);
    }

    @Provides
    @ActivityScope
    public LoginFragmentContract.Presenter provideLoginFragment(IBaseManager iBaseManager,
        IAccountManager iAccountManager,IShoppingCartManager iShoppingCartManager) {
        return new LoginFragmentPresenterImpl(iBaseManager, iAccountManager,iShoppingCartManager);
    }

    @Provides
    @ActivityScope
    public ShopBrandContract.Presenter provideShopBrandPresenter(
        ICommodityManager iCommodityManager, IBaseManager iBaseManager) {
        return new ShopBrandPresenterImpl(iCommodityManager, iBaseManager);
    }

    @Provides
    @ActivityScope
    public SearchContract.Presenter provideSearchPresenter(ICommodityManager iCommodityManager,
        IBaseManager iBaseManager) {
        return new SearchPresenterImpl(iCommodityManager, iBaseManager);
    }

    @Provides
    @ActivityScope
    public BindProductContract.Presenter provideBindProductPresenter(
        ICommodityManager iCommodityManager) {
        return new BindProductPresenterImpl(iCommodityManager);
    }

    @Provides
    @ActivityScope
    public SettingContract.Presenter provideSettingPresenter(IBaseManager iBaseManager,
        IAccountManager iAccountManager) {
        return new SettingPresenterImpl(iBaseManager, iAccountManager);
    }

    @Provides
    @ActivityScope
    public MyOrderContract.Presenter provideMyOrderPresenter(ICommodityManager iCommodityManager,
        IBaseManager iBaseManager) {
        return new MyOrderPresenter(iCommodityManager, iBaseManager);
    }

    @Provides
    @ActivityScope
    public ShoppingCartVersionContract.Presenter provideCheckoutVersionPresenter(
        IBaseManager iBaseManager, IShoppingCartManager iShoppingCartManager) {
        return new ShoppingCartVersionPresenter(iBaseManager, iShoppingCartManager);
    }

    @Provides
    @ActivityScope
    public CheckoutRegisterContract.Presenter provideCheckoutRegisterPresenter(IBaseManager iBaseManager,
            IAccountManager iAccountManager, IShoppingCartManager iShoppingCartManager) {
        return new CheckoutRegisterPresenter(iAccountManager, iShoppingCartManager,iBaseManager);
    }

    @Provides
    @ActivityScope
    public NotifyMeConstract.Presenter provideNotifyMePresenter(IBaseManager iBaseManager, ICommodityManager iCommodityManager){
        return new NotifyMePresenterImpl(iBaseManager, iCommodityManager);
    }
}
