package com.example.cricketscore.ui.play

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.cricketscore.R
import com.example.cricketscore.databinding.FragmentPlayBinding
import com.example.cricketscore.ui.activity.GameScorerActivity

class PlayFragment : Fragment() {

    private var _binding: FragmentPlayBinding? = null
    private val binding get() = _binding!!

    private val playViewModel: PlayViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTextWatchers()
        setupStartButton()

        // 1. Listen for the result coming back from TossFragment
        parentFragmentManager.setFragmentResultListener(
            "tossResult",
            viewLifecycleOwner
        ) { _, bundle ->
            val winner = bundle.getString("winnerTeam")
            val choice = bundle.getString("choice")
            val teamA = binding.etTeamA.text.toString().trim()
            val teamB = binding.etTeamB.text.toString().trim()

            // Logic to automatically check the correct RadioButton
            if (choice == "Batting") {
                // If winner chose batting, the winner bats
                if (winner == teamA) binding.rbTeamA.isChecked = true
                else if (winner == teamB) binding.rbTeamB.isChecked = true
            } else {
                // If winner chose bowling, the OTHER team bats
                if (winner == teamA) binding.rbTeamB.isChecked = true
                else if (winner == teamB) binding.rbTeamA.isChecked = true
            }
        }

        // 2. Setup the Toss Button navigation
        binding.btnToss.setOnClickListener {
            val teamA = binding.etTeamA.text.toString().trim()
            val teamB = binding.etTeamB.text.toString().trim()

            if (teamA.isEmpty() || teamB.isEmpty()) {
                Toast.makeText(requireContext(), "Enter team names first", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val bundle = Bundle().apply {
                putString("teamA", teamA)
                putString("teamB", teamB)
            }

            // Navigate using NavController
            findNavController().navigate(R.id.navigation_toss, bundle)
        }
    }

    private fun setupTextWatchers() {
        binding.etTeamA.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.rbTeamA.text = if (s.isNullOrEmpty()) "Team A" else s.toString()
            }
        })

        binding.etTeamB.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.rbTeamB.text = if (s.isNullOrEmpty()) "Team B" else s.toString()
            }
        })
    }

    private fun setupStartButton() {
        binding.btnStartMatch.setOnClickListener {
            val teamA = binding.etTeamA.text?.toString()?.trim()
            val teamB = binding.etTeamB.text?.toString()?.trim()

            if (teamA.isNullOrEmpty() || teamB.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Please Enter Team Name", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val selectedId = binding.rgBatting.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(requireContext(), "Please Select Batting Team", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val battingTeam = if (selectedId == binding.rbTeamA.id) teamA else teamB
            val overs = binding.etOver.text?.toString()?.trim()

            val intent = Intent(requireContext(), GameScorerActivity::class.java).apply {
                putExtra("teamA", teamA)
                putExtra("teamB", teamB)
                putExtra("batting", battingTeam)
                putExtra("overs", overs)
            }
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}