package com.nedash.com.smartwatch.notifier.app.ui.fragment.purchase

import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentPurchaseBeforeTutorialBinding
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Subscribes.SUBS_YEAR_BEFORE
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.PP_URL
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.PURCHASE_CANCEL_SITE
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.TOU_URL
import com.nedash.com.smartwatch.notifier.app.utils.Utils.calculateOldPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.calculatePeriodPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.convertPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.openURL
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import io.github.armcha.autolink.AutoLinkTextView
import io.github.armcha.autolink.MODE_URL

@AndroidEntryPoint
class PurchaseBeforeTutorialFragment : Fragment() {

    private var _binding: FragmentPurchaseBeforeTutorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseBeforeTutorialBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.llClose.setOnClickListener { navToOnBoarding() }
        with((requireActivity() as MainActivity).billingHelper) {
            isPro.observe(viewLifecycleOwner, { if (it == true) navToOnBoarding() })
            querySubSku(listOf(SUBS_YEAR_BEFORE)) {
                val skuDetails = it.firstOrNull()
                skuDetails?.let { sku ->
                    requireActivity().runOnUiThread {
                        with(binding) {
                            tvWeekly.text =
                                getString(R.string.price_weekly, sku.calculatePeriodPrice(52))
                            tvAnnually.text = getString(R.string.price_annually, sku.convertPrice())
                            with(tvEveryAnnually) {
                                text = getString(R.string.price_annually, sku.calculateOldPrice(25))
                                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                            }
                            createLinkString(
                                tvBottomHint, getString(
                                    R.string.trial_purchase_bottom_hint,
                                    sku.convertPrice(), PP_URL, TOU_URL, PURCHASE_CANCEL_SITE
                                )
                            )
                            llPrices.setOnClickListener {
                                launchBillingFlow(
                                    requireActivity(),
                                    sku
                                )
                            }
                            llFreeTrialPeriod.setOnClickListener {
                                launchBillingFlow(
                                    requireActivity(),
                                    sku
                                )
                            }
                            clContent.visible()
                        }
                    }
                }
            }
        }
    }

    private fun navToOnBoarding() {
        findNavController().navigate(
            PurchaseAfterTutorialFragmentDirections
                .actionPurchaseAfterTutorialFragmentToHomeFragment()
        )
    }

    private fun createLinkString(textView: AutoLinkTextView, dataText: String) {
        with(textView) {
            addAutoLinkMode(MODE_URL)
            attachUrlProcessor { s: String ->
                when {
                    s.equals(TOU_URL, ignoreCase = true) -> {
                        return@attachUrlProcessor "Terms and Conditions"
                    }
                    s.equals(PP_URL, ignoreCase = true) -> {
                        return@attachUrlProcessor "Privacy Policy"
                    }
                    else -> {
                        return@attachUrlProcessor "Cancel"
                    }
                }
            }
            addSpan(MODE_URL, StyleSpan(Typeface.NORMAL), UnderlineSpan())
            urlModeColor = currentTextColor
            text = dataText
            onAutoLinkClick { (_, _, originalText) -> requireContext().openURL(originalText) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}