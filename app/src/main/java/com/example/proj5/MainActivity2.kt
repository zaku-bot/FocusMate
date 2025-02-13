package com.example.proj5

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment


class MainActivity2 : AppCompatActivity() {

    private lateinit var btnEnter: Button
    private var dateValue: String = ""
    private var distanceValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val repository = RecommendationRepository()

        btnEnter = findViewById(R.id.enterBtn)

        val btnPreferences = findViewById<Button>(R.id.preferencesBtn)

        btnPreferences.setOnClickListener {
            val preferencesFragment = PreferencesFragment()
            preferencesFragment.show(supportFragmentManager, "PreferencesFragment")
        }

        val dateSpinnerOptions = listOf(
            SpinnerOption("Today", "TODAY"),
            SpinnerOption("This Week", "THIS_WEEK"),
            SpinnerOption("This Month", "THIS_MONTH")
        )

        val distanceSpinnerOptions = listOf(
            SpinnerOption("Less than 10 miles", "LESS_THAN_10_MILES"),
            SpinnerOption("Between 10 and 20 miles", "BETWEEN_10_AND_20_MILES"),
            SpinnerOption("Greater than 20 miles", "GREATER_THAN_20_MILES")
        )

        val dateSpinner = findViewById<Spinner>(R.id.spinnerDateFilter)
        val dateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateSpinnerOptions.map { it.displayText })
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateSpinner.adapter = dateAdapter

        val distanceSpinner = findViewById<Spinner>(R.id.spinnerDistanceFilter)
        val distanceAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, distanceSpinnerOptions.map { it.displayText })
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        distanceSpinner.adapter = distanceAdapter

        val defaultDateFilter = "TODAY"
        val defaultDistanceFilter = "GREATER_THAN_20_MILES"

        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDateOption = dateSpinnerOptions[position]
                dateValue = selectedDateOption.value
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                dateValue = defaultDateFilter
            }
        }

        distanceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedDistanceOption = distanceSpinnerOptions[position]
                distanceValue= selectedDistanceOption.value
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                distanceValue = defaultDistanceFilter
            }
        }

        btnEnter.setOnClickListener {
            val date = dateValue
            val mileRadius = distanceValue

            repository.getRecommendations("H6KY1R3hJkhfHRiclKnZ4gb7yRs1", 33.425026,-111.937437,"12:00%20PM%20MST", "$date", "$mileRadius") { apiResponse ->
                runOnUiThread {
                    apiResponse?.let {
                        println("Response: $apiResponse")
                        updateUI(it)
                    } ?: run {

                    }
                }
            }

        }
    }

    private fun updateUI(response: ApiResponse) {
        showRecommendations(response.recommendations)
    }

    private fun showRecommendations(recommendations: List<Recommendation>) {
        val dialogFragment = RecommendationsDialogFragment.newInstance(recommendations)
        dialogFragment.show(supportFragmentManager, "RecommendationsDialog")
    }
}

class PreferencesFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val events = arrayOf(
            "Sports",
            "Concerts",
            "Comedy shows",
            "Restaurants",
            "Movies",
            "Nightclubs"
        )

        val checkedItems = booleanArrayOf(false, false, false, false, false, false)

        return AlertDialog.Builder(requireContext())
            .setTitle("Select Events")
            .setMultiChoiceItems(events, checkedItems) { _, which, isChecked ->
                checkedItems[which] = isChecked
            }
            .setPositiveButton("Save") { _, _ ->

            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()
    }
}

data class SpinnerOption(val displayText: String, val value: String)