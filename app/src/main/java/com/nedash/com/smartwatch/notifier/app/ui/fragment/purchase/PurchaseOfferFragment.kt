package com.nedash.com.smartwatch.notifier.app.ui.fragment.purchase

import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentPurchaseOfferBinding
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Constants.Subscribes.SUBS_OFFER
import com.nedash.com.smartwatch.notifier.app.utils.Utils.calculateOldPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.calculatePeriodPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.convertPrice
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PurchaseOfferFragment : Fragment() {

    private var _binding: FragmentPurchaseOfferBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchaseOfferBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            llClose.setOnClickListener { findNavController().popBackStack() }
            with((requireActivity() as MainActivity).billingHelper) {
                isPro.observe(
                    viewLifecycleOwner,
                    { if (it == true) findNavController().popBackStack() })
                querySubSku(listOf(SUBS_OFFER)) {
                    val skuDetails = it.firstOrNull()
                    skuDetails?.let { sku ->
                        requireActivity().runOnUiThread {
                            tvWeekly.text =
                                getString(
                                    R.string.price_weekly,
                                    sku.calculatePeriodPrice(52)
                                )
                            tvAnnually.text = getString(R.string.price_annually, sku.convertPrice())
                            with(tvEveryAnnually) {
                                paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                                text = getString(
                                    R.string.price_annually,
                                    sku.calculateOldPrice(50)
                                )
                            }
                            btnBuy.setOnClickListener { launchBillingFlow(requireActivity(), sku) }
                            clContent.visible()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}