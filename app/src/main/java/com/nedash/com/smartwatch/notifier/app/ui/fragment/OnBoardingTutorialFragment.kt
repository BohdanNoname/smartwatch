package com.nedash.com.smartwatch.notifier.app.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.nedash.com.smartwatch.notifier.app.R
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentOnBoardingTutorialBinding
import com.nedash.com.smartwatch.notifier.app.databinding.FragmentOnBoardingViewPagerContentBinding
import com.nedash.com.smartwatch.notifier.app.ui.activity.MainActivity
import com.nedash.com.smartwatch.notifier.app.utils.Utils.gone
import com.nedash.com.smartwatch.notifier.app.utils.Utils.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingTutorialFragment : Fragment() {

    private var _binding: FragmentOnBoardingTutorialBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingTutorialBinding
            .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = OnBoardingSlidePagerAdapter(this)
        with(binding) {
            introPager.adapter = pagerAdapter
            TabLayoutMediator(intoTabLayout, introPager)
            { _, _ -> }.attach()
            introPager
                .registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        4 -> {
                            llStart.visible()
                            llNext.gone()
                        }
                        else -> {
                            llNext.visible()
                            llStart.gone()
                        }
                    }
                }
            })
            llNext.setOnClickListener {
                introPager.currentItem = introPager.currentItem + 1
            }
            llStart.setOnClickListener {
                navNext()
            }
            tvSkip.setOnClickListener { navNext() }
        }
    }

    private fun navNext() {
        findNavController().navigate(
            if ((requireActivity() as MainActivity).isPro)
                OnBoardingTutorialFragmentDirections
                    .actionOnBoardingTutorialFragmentToHomeFragment()
            else OnBoardingTutorialFragmentDirections
                .actionOnBoardingTutorialFragmentToPurchaseAfterTutorialFragment()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    inner class OnBoardingSlidePagerAdapter(
        fragment: Fragment,
    ) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = 5

        override fun createFragment(position: Int): Fragment {
            return OnBoardingViewPagerContentFragment().apply {
                arguments = Bundle().apply { putInt("position", position) }
            }
        }
    }
}

class OnBoardingViewPagerContentFragment : Fragment() {

    private var _binding: FragmentOnBoardingViewPagerContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            FragmentOnBoardingViewPagerContentBinding
                .inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            arguments?.takeIf { it.containsKey("position") }?.apply {
                when (getInt("position")) {
                    0 -> {
                        ivIcon.setImageResource(R.drawable.img_tutorial_1)
                        tvHint.setText(R.string.tutorial_text_1)
                    }
                    1 -> {
                        ivIcon.setImageResource(R.drawable.img_tutorial_2)
                        tvHint.setText(R.string.tutorial_text_2)
                    }
                    2 -> {
                        ivIcon.setImageResource(R.drawable.img_tutorial_3)
                        tvHint.setText(R.string.tutorial_text_3)
                    }
                    3 -> {
                        ivIcon.setImageResource(R.drawable.img_tutorial_4)
                        tvHint.setText(R.string.tutorial_text_4)
                    }
                    4 -> {
                        ivIcon.setImageResource(R.drawable.img_tutorial_5)
                        tvHint.setText(R.string.tutorial_text_5)
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