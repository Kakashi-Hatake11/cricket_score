package com.example.cricketscore.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cricketscore.database.MatchHistory
import com.example.cricketscore.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()
    private val adapter = HistoryAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeHistory()
        historyViewModel.fetchAllMatchHistory()
    }

    private fun setupRecyclerView() = with(binding.rvHistory) {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = this@HistoryFragment.adapter
    }

    private fun observeHistory() {
        historyViewModel.matchHistory().observe(viewLifecycleOwner) { list ->
            updateUI(list)
        }
    }

    private fun updateUI(list: List<MatchHistory>?) {
        if (list.isNullOrEmpty()) {
            adapter.data = emptyList()
            binding.rvHistory.visibility = View.GONE
            binding.emptyHistory.visibility = View.VISIBLE
        } else {
            adapter.data = list
            binding.rvHistory.visibility = View.VISIBLE
            binding.emptyHistory.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
