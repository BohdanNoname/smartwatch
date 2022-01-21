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
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentPurchaseInsideApplicationBinding
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Subscribes.SUBS_MONTH
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Subscribes.SUBS_WEEK
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Subscribes.SUBS_YEAR
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.PURCHASE_CANCEL_SITE
import com.nedash.com.smartwatch.notifier.app.utils.Utils.calculateOldPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.calculatePeriodPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.convertPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.openURL
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint
import io.github.armcha.autolink.AutoLinkTextView
import io.github.armcha.autolink.MODE_URL

@AndroidEntryPoint
class PurchaseInsideApplicationFragment : Fragment() {

    private var _binding: FragmentPurchaseInsideApplicationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseInsideApplicationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            llClose.setOnClickListener { findNavController().popBackStack() }
            createLinkString(
                tvBottomHint, getString(R.string.in_app_bottom_hint, PURCHASE_CANCEL_SITE)
            )
            with((requireActivity() as MainActivity).billingHelper) {
                isPro.observe(
                    viewLifecycleOwner,
                    { if (it == true) findNavController().popBackStack() })
                querySubSku(listOf(SUBS_WEEK, SUBS_MONTH, SUBS_YEAR)) { skus ->
                    requireActivity().runOnUiThread {
                        skus.forEach { skuDetails ->
                            when (skuDetails.sku) {
                                SUBS_WEEK -> {
                                    with(tvWeeklyOld) {
                                        paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                        text = getString(
                                            R.string.price_weekly,
                                            skuDetails.calculateOldPrice(25)
                                        )
                                    }
                                    tvWeekly.text =
                                        getString(R.string.price_week, skuDetails.convertPrice())
                                    clWeek.setOnClickListener {
                                        launchBillingFlow(requireActivity(), skuDetails)
                                    }
                                }
                                SUBS_MONTH -> {
                                    with(tvMonthOld) {
                                        paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                        text = getString(
                                            R.string.price_monthly,
                                            skuDetails.calculateOldPrice(25)
                                        )
                                    }
                                    tvMonth.text =
                                        getString(R.string.price_month, skuDetails.convertPrice())
                                    tvMonthWeekly.text = getString(
                                        R.string.price_weekly,
                                        skuDetails.calculatePeriodPrice(4)
                                    )
                                    clMonth.setOnClickListener {
                                        launchBillingFlow(requireActivity(), skuDetails)
                                    }
                                }
                                SUBS_YEAR -> {
                                    with(tvYearOld) {
                                        paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                        text = getString(
                                            R.string.price_yearly,
                                            skuDetails.calculateOldPrice(25)
                                        )
                                    }
                                    tvYear.text =
                                        getString(R.string.price_year, skuDetails.convertPrice())
                                    tvYearWeekly.text = getString(
                                        R.string.price_weekly,
                                        skuDetails.calculatePeriodPrice(52)
                                    )
                                    clYear.setOnClickListener {
                                        launchBillingFlow(requireActivity(), skuDetails)
                                    }
                                }
                            }
                        }
                        clContent.visible()
                    }
                }
            }
        }
    }

    private fun createLinkString(textView: AutoLinkTextView, dataText: String) {
        with(textView) {
            addAutoLinkMode(MODE_URL)
            attachUrlProcessor { "Cancel" }
            addSpan(MODE_URL, StyleSpan(Typeface.NORMAL), UnderlineSpan())
            urlModeColor = currentTextColor
            text = dataText
            onAutoLinkClick { (_, _, originalText) -> requireContext().openURL(originalText) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}