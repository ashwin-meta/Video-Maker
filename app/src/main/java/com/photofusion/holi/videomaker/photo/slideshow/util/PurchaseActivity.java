package com.photofusion.holi.videomaker.photo.slideshow.util;

import androidx.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import com.photofusion.holi.videomaker.photo.slideshow.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseActivity extends BaseActivity {


    private BillingClient billingClient;
    List<ProductDetails> productDetailsList;
    Prefs prefs;
    public static final String LIFETIME_KEY = "lifetime";//in App purchase key

    LinearLayout ll_lifetime;
    private TextView lifetimeTv, restore;

    private static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);


        init();

        prefs = new Prefs(this);
        productDetailsList = new ArrayList<>();

        PendingPurchasesParams pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build();

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .setListener(
                        (billingResult, list) -> {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                                for (Purchase purchase : list) {
                                    verifySubPurchase(purchase);
                                }
                            }
                        }
                ).build();

        //start the connection after initializing the billing client
        establishConnection();

        restore.setOnClickListener(view -> restorePurchases());

    }

    private void init() {

        ll_lifetime = findViewById(R.id.ll_lifetime);
        lifetimeTv = findViewById(R.id.tv_lifetime);
        restore = findViewById(R.id.restore);

//        findViewById(R.id.iv_back).setOnClickListener(view -> onBackPressed());
    }

    void establishConnection() {
        showLoader(this);
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    showProductsInApp();
                    dismissDialog();
                } else {
                    dismissDialog();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                establishConnection();
            }
        });
    }

    String rateLifetime;


    void showProductsInApp() {

        List<QueryProductDetailsParams.Product> productList = new ArrayList<>();
        productList.add(QueryProductDetailsParams.Product.newBuilder()
                .setProductId(LIFETIME_KEY)
                .setProductType(BillingClient.ProductType.INAPP)
                .build());

        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build();

        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, productDetailsList) -> {
                    Log.e("showProducts: ", productDetailsList.toString());
                    Log.e("showProducts: ", "" + productDetailsList.size());

                    // Safety check: ensure productDetailsList is not empty
                    if (productDetailsList != null && !productDetailsList.isEmpty()) {
                        rateLifetime = productDetailsList.get(0).getOneTimePurchaseOfferDetails().getFormattedPrice();
                        Log.e("showProductsInApp: ", rateLifetime);
                        runOnUiThread(() -> {
                            Log.e("showProductsInApp: ", rateLifetime);
                            lifetimeTv.setText(rateLifetime);
                            ll_lifetime.setOnClickListener(v -> launchPurchaseFlowInApp(productDetailsList.get(0)));
                        });
                    } else {
                        Log.e("showProductsInApp: ", "Product details list is empty");
                        runOnUiThread(() -> Toast.makeText(PurchaseActivity.this, "Unable to load products", Toast.LENGTH_SHORT).show());
                    }
                }
        );
    }


    void launchPurchaseFlowInApp(ProductDetails productDetails) {
        List<BillingFlowParams.ProductDetailsParams> productDetailsParamsList = new ArrayList<>();
        productDetailsParamsList.add(BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build());

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build();

        billingClient.launchBillingFlow(PurchaseActivity.this, billingFlowParams);
    }

    void verifySubPurchase(Purchase purchases) {

        AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchases.getPurchaseToken())
                .build();

        billingClient.acknowledgePurchase(acknowledgePurchaseParams, billingResult -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                prefs.setPremium(1);
//                prefs.setIsGoBack(true);
                onBackPressed();
            }
        });

        if (purchases.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            prefs.setPremium(1);
//            prefs.setIsGoBack(true);
            onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (billingResult, list) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        for (Purchase purchase : list) {
                            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged()) {
                                verifySubPurchase(purchase);
                            }

                        }
                    }

                }
        );


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    void restorePurchases() {

        PendingPurchasesParams pendingPurchasesParams = PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build();

        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases(pendingPurchasesParams)
                .setListener((billingResult, list) -> {
                }).build();

        final BillingClient finalBillingClient = billingClient;
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.e("onDisconnected: ", "failed");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                Log.e("onConnected: ", "done");
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.e("onConnected: ", billingResult.toString());
                    finalBillingClient.queryPurchasesAsync(
                            QueryPurchasesParams.newBuilder().setProductType
                                    (BillingClient.ProductType.INAPP).build(),
                            (billingResult1, list) -> {
                                Log.e("onConnected: ", billingResult1.toString());
                                if (billingResult1.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                    // Check if any purchases exist
                                    if (list != null && !list.isEmpty()) {
                                        Log.e("onConnected: ", "Purchases found: " + list.size());
                                        runOnUiThread(() -> {
                                            prefs.setPremium(1); // set 1 to activate premium feature
                                            Toast.makeText(PurchaseActivity.this, "Successfully restored.", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        });
                                    } else {
                                        runOnUiThread(() -> {
                                            Toast.makeText(PurchaseActivity.this, "Oops, No purchase found.", Toast.LENGTH_SHORT).show();
                                            prefs.setPremium(0); // set 0 to de-activate premium feature
                                        });
                                    }
                                } else {
                                    runOnUiThread(() -> {
                                        Toast.makeText(PurchaseActivity.this, "Oops, No purchase found.", Toast.LENGTH_SHORT).show();
                                        prefs.setPremium(0); // set 0 to de-activate premium feature
                                    });
                                }
                            });
                }
            }
        });
    }

    public static void showLoader(Context context) {
        dialog = new ProgressDialog(context);
        dialog.setTitle("Loading.....");
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}