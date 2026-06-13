package com.example.cricketscore.ui.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cricketscore.R
import com.example.cricketscore.database.MatchHistory
import com.example.cricketscore.databinding.ActivityGameScorerBinding
import com.example.cricketscore.databinding.ResultDialogBinding
import java.text.SimpleDateFormat
import java.util.*

class GameScorerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameScorerBinding
    private lateinit var viewModel: GameScorerViewModel

    private lateinit var teamA: String
    private lateinit var teamB: String
    private lateinit var batting: String

    private var totalOvers = 10
    private var inning = 0
    private var balls = 0

    private var teamAScore = 0
    private var teamBScore = 0
    private var teamAWicket = 0
    private var teamBWicket = 0

    private var needRun = 0
    private var leftBalls = 0

    private var matchFinished = false
    private val thisOver = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGameScorerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[GameScorerViewModel::class.java]

        setupIntentData()
        setupClickListeners()
        updateUI()
    }

    private fun setupIntentData() {
        teamA = intent.getStringExtra("teamA") ?: "Team A"
        teamB = intent.getStringExtra("teamB") ?: "Team B"
        batting = intent.getStringExtra("batting") ?: teamA
        totalOvers = intent.getStringExtra("overs")?.toIntOrNull() ?: 10

        binding.tvBattingTeam.text = batting
    }

    private fun setupClickListeners() = with(binding) {

        btnQuit.setOnClickListener { finish() }

        btnUndo.setOnClickListener {
            if (thisOver.isNotEmpty()) undoLastMove()
        }

        btnDot.setOnClickListener { score(0, 0, 1, "0") }
        btnOne.setOnClickListener { score(1, 0, 1, "1") }
        btnTwo.setOnClickListener { score(2, 0, 1, "2") }
        btnThree.setOnClickListener { score(3, 0, 1, "3") }
        btnFour.setOnClickListener { score(4, 0, 1, "4") }
        btnSix.setOnClickListener { score(6, 0, 1, "6") }

        btnWide.setOnClickListener { score(1, 0, 0, "WD") }
        btnNoBall.setOnClickListener { score(1, 0, 0, "NB") }
        btnOut.setOnClickListener { score(0, 1, 1, "W") }
    }

    private fun score(run: Int, wicket: Int, ball: Int, label: String) {
        if (matchFinished) return

        thisOver.add(label)

        if (batting == teamA) {
            teamAScore += run
            teamAWicket += wicket
        } else {
            teamBScore += run
            teamBWicket += wicket
        }

        balls += ball

        if (inning == 1) {
            needRun -= run
            leftBalls -= ball

            if (needRun <= 0) {
                finishMatch()
                return
            }

            if (leftBalls <= 0) {
                finishMatch()
                return
            }
        }

        updateUI()

        if (isInningFinished()) {
            if (inning == 0) {
                changeInning()
            } else {
                finishMatch()
            }
        }
    }

    private fun undoLastMove() {

        val last = thisOver.removeAt(thisOver.size - 1)

        val run = last.toIntOrNull() ?: if (last == "WD" || last == "NB") 1 else 0
        val ball = if (last == "WD" || last == "NB") 0 else 1
        val wicket = if (last == "W") 1 else 0

        if (batting == teamA) {
            teamAScore -= run
            teamAWicket -= wicket
        } else {
            teamBScore -= run
            teamBWicket -= wicket
        }

        balls -= ball

        if (inning == 1) {
            needRun += run
            leftBalls += ball
        }

        updateUI()
    }

    private fun updateUI() {

        val overs = balls / 6
        val ball = balls % 6

        if (batting == teamA) {
            binding.tvBattingScore.text = "$teamAScore - $teamAWicket"
        } else {
            binding.tvBattingScore.text = "$teamBScore - $teamBWicket"
        }

        binding.tvBowlingScore.text = "( $overs.$ball )"
        binding.tvThisOverScore.text = thisOver.joinToString(" ")

        if (inning == 1) {
            binding.tvToWin.text = "$batting need $needRun in $leftBalls balls"
        }

        if (balls % 6 == 0 && balls != 0) {
            thisOver.clear()
        }
    }

    private fun isInningFinished(): Boolean {
        val wickets = if (batting == teamA) teamAWicket else teamBWicket
        return balls == totalOvers * 6 || wickets == 10
    }

    private fun changeInning() {

        inning = 1
        balls = 0
        thisOver.clear()

        if (batting == teamA) {
            batting = teamB
            needRun = teamAScore + 1
        } else {
            batting = teamA
            needRun = teamBScore + 1
        }

        leftBalls = totalOvers * 6

        binding.tvBattingTeam.text = batting
        binding.tvToWin.text = "$batting need $needRun in $leftBalls balls"

        updateUI()
    }

    private fun finishMatch() {

        if (matchFinished) return
        matchFinished = true

        val dialogBinding = ResultDialogBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        val winner = when {
            teamAScore > teamBScore -> teamA
            teamAScore < teamBScore -> teamB
            else -> "Tie"
        }

        dialogBinding.result.text =
            if (winner == "Tie") "That was a tie!"
            else "$winner Won the Match"

        dialogBinding.btnQuit.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        dialog.show()
        saveMatch(winner)
    }

    private fun saveMatch(winner: String) {

        val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date())

        val matchHistory = MatchHistory(
            0,
            date,
            teamA,
            teamB,
            teamAScore,
            teamBScore,
            teamAWicket,
            teamBWicket,
            "",
            "",
            winner
        )

        viewModel.insertMatchHistory(matchHistory)
    }
}
