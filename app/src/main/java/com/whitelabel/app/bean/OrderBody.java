package com.whitelabel.app.bean;

/**
 * Created by Administrator on 2016/4/8.
 */
public class OrderBody {
    private int type;
    private String orderNumber;
    private String orderImage;
    private String orderName;
    private String orderCs;
    private String orderPrice;
    private String orderQuantity;
    private String orderTextStatus;
    private String orderStatusCode;
    private String merchantName;
    private String vendor_id;
    private int isRPayment;
    private boolean isLast;
    private String availability;
    private String orderId;
    private boolean isChecked;
    private String itemId;
    private String stockQty;
    private boolean isBtnAddCartEnable;
    private String productId;

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public int getIsRPayment() {
        return isRPayment;
    }

    public void setIsRPayment(int isRPayment) {
        this.isRPayment = isRPayment;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }
    private String trickingTitle;
    private String trickingUrl;

    public String getTrickingTitle() {
        return trickingTitle;
    }

    public void setTrickingTitle(String trickingTitle) {
        this.trickingTitle = trickingTitle;
    }

    public String getTrickingUrl() {
        return trickingUrl;
    }

    public void setTrickingUrl(String trickingUrl) {
        this.trickingUrl = trickingUrl;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getOrderCs() {
        return orderCs;
    }

    public void setOrderCs(String orderCs) {
        this.orderCs = orderCs;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public void setOrderImage(String orderImage) {
        this.orderImage = orderImage;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getOrderTextStatus() {
        return orderTextStatus;
    }

    public void setOrderTextStatus(String orderTextStatus) {
        this.orderTextStatus = orderTextStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getStockQty() {
        return stockQty;
    }

    public void setStockQty(String stockQty) {
        this.stockQty = stockQty;
    }

    public boolean isBtnAddCartEnable() {
        return isBtnAddCartEnable;
    }

    public void setBtnAddCartEnable(boolean btnAddCartEnable) {
        isBtnAddCartEnable = btnAddCartEnable;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }
}
