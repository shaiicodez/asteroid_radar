package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        //view model
        val viewModelFactory = MainViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        // handle the click events on adapter items
        val adapter = AsteroidListAdapter(AsteroidListAdapter.OnClickListener {
            viewModel.onAsteroidItemClicked(asteroid = it)
        })
        binding.asteroidRecycler.adapter = adapter

        //navigate to details
        viewModel.navigateToAsteroidDetail.observe(viewLifecycleOwner, Observer {
            it?.let {

                this.findNavController().navigate(MainFragmentDirections
                    .actionShowDetail(it))
                viewModel.onSleepDataQualityNavigated()
            }
        })

//        viewModel.asteroidsDummy.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                adapter.submitList(it)
//            }
//        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.show_all_menu -> viewModel.daysIncluded.value = MainViewModel.PeriodDays.SEVEN
            R.id.show_today_menu -> viewModel.daysIncluded.value = MainViewModel.PeriodDays.ONE
        }

        return true
    }
}
