package com.example.proj5

import kotlin.math.round
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class RecommendationsDialogFragment(private val recommendations: List<Recommendation>) : DialogFragment() {

    private lateinit var listView: ListView
    private lateinit var detailView: LinearLayout
    private lateinit var detailScrollView: ScrollView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_recommendations, container, false)

        listView = view.findViewById(R.id.listViewRecommendations)
        detailScrollView = view.findViewById(R.id.detailView)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, recommendations.map { it.event.eventName })
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            showRecommendationDetails(recommendations[position])
        }

        return view
    }

    private fun showRecommendationDetails(recommendation: Recommendation) {
        listView.visibility = View.GONE
        val detailScrollView: ScrollView = view?.findViewById(R.id.detailView) ?: return
        detailScrollView.visibility = View.VISIBLE

        // Populate the TextViews
        view?.findViewById<TextView>(R.id.eventName)?.text = "Event: ${recommendation.event.eventName}"
        view?.findViewById<TextView>(R.id.eventDate)?.text = "Date: ${recommendation.event.eventDate}"
        view?.findViewById<TextView>(R.id.eventTime)?.text = "Time: ${recommendation.event.eventTime}"
        view?.findViewById<TextView>(R.id.eventLocationName)?.text = "Location: ${recommendation.event.eventLocationName}"
        view?.findViewById<TextView>(R.id.cost)?.text = "Cost: ${recommendation.event.additionalDetails.cost}"
        view?.findViewById<TextView>(R.id.weather)?.text = "Weather: The weather on that day will be ${recommendation.weatherData.weather}."
        view?.findViewById<TextView>(R.id.traffic)?.text = "Traffic: The traffic will be ${recommendation.trafficData.trafficDetails}."
        val distance = round(0.000621371 * recommendation.trafficData.distance * 100) / 100
        view?.findViewById<TextView>(R.id.distance)?.text = "The travel distance is ${distance} miles."
    }

    companion object {
        fun newInstance(recommendations: List<Recommendation>): RecommendationsDialogFragment {
            return RecommendationsDialogFragment(recommendations)
        }
    }
}