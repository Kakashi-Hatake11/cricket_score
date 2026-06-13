package com.example.cricketscore.ui.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cricketscore.databinding.FragmentTossBinding
import kotlin.random.Random

class TossFragment : Fragment() {

    private var _binding: FragmentTossBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTossBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get team names passed from PlayFragment
        val teamA = arguments?.getString("teamA") ?: "Team A"
        val teamB = arguments?.getString("teamB") ?: "Team B"

        // Update UI to show which team is calling (defaulting to Team A for the call)
        binding.tvTossTitle.text = "$teamA's Call"

        binding.btnDoToss.setOnClickListener {
            // 1. Check if Heads or Tails is selected (Assuming you have a RadioGroup or similar in XML)
            // If using the layout provided in previous turns:
            val selectedCallId = binding.rgHeadsTails.checkedRadioButtonId

            if (selectedCallId == -1) {
                Toast.makeText(requireContext(), "Please select Heads or Tails", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userCalledHeads = selectedCallId == binding.rbHeads.id

            // 2. Randomly flip the coin
            val isHeadsResult = Random.nextBoolean()
            val coinResultText = if (isHeadsResult) "Heads" else "Tails"

            // 3. Determine winner
            // If the call matches the flip, Team A wins. Otherwise, Team B wins.
            val winner = if (userCalledHeads == isHeadsResult) teamA else teamB

            // 4. Update UI
            binding.tvTossResult.text = "It's $coinResultText!\n$winner won the toss!"
            binding.layoutDecision.visibility = View.VISIBLE

            // Hide toss controls to prevent re-flipping
            binding.btnDoToss.visibility = View.GONE
            binding.rgHeadsTails.visibility = View.GONE

            // 5. Handle Bat/Bowl selection
            binding.btnChooseBat.setOnClickListener {
                sendResultAndGoBack(winner, "Batting")
            }
            binding.btnChooseBowl.setOnClickListener {
                sendResultAndGoBack(winner, "Bowling")
            }
        }
    }

    private fun sendResultAndGoBack(winner: String, choice: String) {
        val result = Bundle().apply {
            putString("winnerTeam", winner)
            putString("choice", choice)
        }
        parentFragmentManager.setFragmentResult("tossResult", result)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}