package dev.pgm.poembox.presentation.billing

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val billingManager: BillingManager
) : ViewModel() {

    val isPro: StateFlow<Boolean> = billingManager.isPro

    init {
        viewModelScope.launch {
            billingManager.connect()
            billingManager.queryExistingPurchases()
            billingManager.loadProduct()
        }
    }

    fun purchase(activity: Activity) {
        billingManager.launchPurchase(activity)
    }
}
