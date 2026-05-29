package dev.pgm.poembox.presentation.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * ID del producto en-app registrado en Google Play Console.
 * Cámbialo cuando crees el producto en Play Console.
 */
const val PRODUCT_ID_PRO = "poembox_pro"

@Singleton
class BillingManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PurchasesUpdatedListener {

    private val _isPro = MutableStateFlow(false)
    val isPro: StateFlow<Boolean> = _isPro.asStateFlow()

    private var productDetails: ProductDetails? = null

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    // ── Conexión ───────────────────────────────────────────────────────────

    suspend fun connect(): Boolean = suspendCancellableCoroutine { cont ->
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                cont.resume(result.responseCode == BillingClient.BillingResponseCode.OK)
            }
            override fun onBillingServiceDisconnected() {
                if (cont.isActive) cont.resume(false)
            }
        })
    }

    // ── Consulta producto ──────────────────────────────────────────────────

    suspend fun loadProduct(): ProductDetails? {
        if (!billingClient.isReady) connect()
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID_PRO)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        return suspendCancellableCoroutine { cont ->
            billingClient.queryProductDetailsAsync(params) { result, list ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    productDetails = list.firstOrNull()
                    cont.resume(productDetails)
                } else {
                    cont.resume(null)
                }
            }
        }
    }

    // ── Compra ─────────────────────────────────────────────────────────────

    fun launchPurchase(activity: Activity) {
        val details = productDetails ?: return
        // For INAPP (one-time purchase) products, no offer token is required
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build()
        )
        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()
        billingClient.launchBillingFlow(activity, flowParams)
    }

    // ── Verificar compras existentes ────────────────────────────────────────

    fun queryExistingPurchases() {
        if (!billingClient.isReady) return
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { _, purchases ->
            handlePurchases(purchases)
        }
    }

    // ── Listener de compras ────────────────────────────────────────────────

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases)
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        for (purchase in purchases) {
            if (purchase.products.contains(PRODUCT_ID_PRO)) {
                _isPro.value = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
                    acknowledgePurchase(purchase)
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        billingClient.acknowledgePurchase(params) { result ->
            if (result.responseCode != BillingClient.BillingResponseCode.OK) {
                FirebaseCrashlytics.getInstance().log(
                    "Acknowledge failed: ${result.debugMessage}"
                )
            }
        }
    }
}
