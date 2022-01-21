package com.nedash.com.smartwatch.notifier.app.ui.fragment.purchase

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentPurchaseAfterTutorialBinding
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Subscribes.SUBS_YEAR_AFTER
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.PP_URL
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.PURCHASE_CANCEL_SITE
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Url.TOU_URL
import com.nedash.com.smartwatch.notifier.app.utils.Utils.convertPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.getColoredText
import com.nedash.com.smartwatch.notifier.app.utils.Utils.openURL
import dagger.hilt.android.AndroidEntryPoint
import io.github.armcha.autolink.AutoLinkTextView
import io.github.armcha.autolink.MODE_URL

@AndroidEntryPoint
class PurchaseAfterTutorialFragment: BottomSheetDialogFragment() {

    private var _binding: FragmentPurchaseAfterTutorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseAfterTutorialBinding
            .inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.purchase_after_tutorial_bg)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            ivClose.setOnClickListener { navNext() }
            with((requireActivity() as MainActivity).billingHelper) {
                isPro.observe(viewLifecycleOwner, { if (it == true) navNext() })
                querySubSku(listOf(SUBS_YEAR_AFTER)) {
                    val skuDetails = it.firstOrNull()
                    skuDetails?.let { sku ->
                        requireActivity().runOnUiThread {
                            with(binding) {
                                tvPerYear.text =
                                    getColoredText(
                                        R.string.per_year_after_trial_ends,
                                        sku.convertPrice()
                                    )
                                createLinkString(
                                    tvBottomHint, getString(
                                        R.string.trial_purchase_bottom_hint,
                                        skuDetails.convertPrice(),
                                        PP_URL,
                                        TOU_URL,
                                        PURCHASE_CANCEL_SITE
                                    )
                                )
                                btnBuy.setOnClickListener {
                                    launchBillingFlow(
                                        requireActivity(),
                                        sku
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        setupBackPressListener()
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

    private fun setupBackPressListener() {
        this.view?.isFocusableInTouchMode = true
        this.view?.requestFocus()
        this.view?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        navNext()
    }

    private fun navNext() {
        findNavController().navigate(
            PurchaseAfterTutorialFragmentDirections
            .actionPurchaseAfterTutorialFragmentToHomeFragment()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}