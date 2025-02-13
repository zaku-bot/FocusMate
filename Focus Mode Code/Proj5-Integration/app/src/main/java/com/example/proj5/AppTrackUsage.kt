package com.example.proj5

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Process
import android.provider.CalendarContract
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale



class AppTrackUsage : AppCompatActivity() {
    data class AppUsageData(
        val appName: String,
        val appIcon: Drawable?,
        val timeSpent: String
    )

    private lateinit var appUsageView:ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_usage)
        appUsageView = findViewById(R.id.appsListView)

        if(checkUsageStatsPermission()){
            showUsageStats()
        }else{
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }
    private fun showUsageStats(){
        var usageStatsManager: UsageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val startOfDay: Long = cal.timeInMillis
        val endOfDay: Long = System.currentTimeMillis()

        val queryUsageStates: List<UsageStats> = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startOfDay,
            endOfDay
        )
        val sortedUsageStats = queryUsageStates.sortedByDescending { it.totalTimeInForeground }.distinctBy { it.packageName }.take(10)

        val statsDataList: MutableList<AppUsageData> = mutableListOf()

        for (currUsageStats in sortedUsageStats) {
            var currAppPackageName = currUsageStats.packageName
            Log.d("APP name is ", currUsageStats.packageName + " usage = " + currUsageStats.totalTimeInForeground.toString())

            val packageInfo = try {
                packageManager.getPackageInfo(currUsageStats.packageName, 0)

            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
            if (packageInfo != null) {
                val appName = packageInfo?.applicationInfo?.loadLabel(packageManager).toString()
                val appIcon = packageInfo?.applicationInfo?.loadIcon(packageManager)
                val timeSpent = formatTimeToHoursMins(currUsageStats.totalTimeInForeground)

                statsDataList.add(AppUsageData(appName, appIcon, timeSpent))
            }else{
                if(currAppPackageName.contains("com.google.android.")){
                    currAppPackageName = currAppPackageName.substring(19)
                }else if(currAppPackageName.contains("com.google.")){
                    currAppPackageName = currAppPackageName.substring(11)
                }else if(currAppPackageName.contains("com.app.")){
                    currAppPackageName = currAppPackageName.substring(8)
                }else if(currAppPackageName.contains("com.")){
                    currAppPackageName = currAppPackageName.substring(4)
                }else if(currAppPackageName.contains("apps.")){
                    currAppPackageName = currAppPackageName.substring(5)
                }
                currAppPackageName = currAppPackageName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
                val appName = currAppPackageName
                val appIcon = ContextCompat.getDrawable(this, R.drawable.deafult_app_icon)
                val timeSpent = formatTimeToHoursMins(currUsageStats.totalTimeInForeground)

                statsDataList.add(AppUsageData(appName, appIcon, timeSpent))
            }
        }

        val adapter = CustomAdapter(this, statsDataList)
        appUsageView.adapter = adapter
    }


    class CustomAdapter(
        private val context: Context,
        private val data: List<AppUsageData>
    ) : ArrayAdapter<AppUsageData>(
        context,
        R.layout.list_item_app_usage,
        data
    ) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var rowView = convertView
            val viewHolder: ViewHolder

            if (rowView == null) {
                val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) !! as LayoutInflater
                rowView = inflater.inflate(R.layout.list_item_app_usage, null)
                viewHolder = ViewHolder()
                viewHolder.appNameTextView = rowView.findViewById(R.id.appName)
                viewHolder.appIconImageView = rowView.findViewById(R.id.appIcon)
                viewHolder.appTimeSpentTextView = rowView.findViewById(R.id.appTimeSpent)
                rowView.tag = viewHolder
            } else {
                viewHolder = rowView.tag as ViewHolder
            }

            viewHolder.appNameTextView?.text = data[position].appName
            viewHolder.appIconImageView?.setImageDrawable(data[position].appIcon)
            viewHolder.appTimeSpentTextView?.text = data[position].timeSpent

            return rowView!!
        }

        internal class ViewHolder {
            var appTimeSpentTextView: TextView? = null
            var appNameTextView: TextView? = null
            var appIconImageView: ImageView? = null
        }
    }
    private fun formatTime(time: Long): String{
        var date: Date = Date(time)
        var format: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        return format.format(date)
    }
    private fun formatTimeToHoursMins(time: Long): String{
        var date: Date = Date(time)
        var format: SimpleDateFormat = SimpleDateFormat("hh:mm", Locale.ENGLISH)
        return format.format(date)
    }
    private fun checkUsageStatsPermission(): Boolean{
        var appOpsManager: AppOpsManager ?= null
        var mode: Int = 0
        appOpsManager = getSystemService(Context.APP_OPS_SERVICE) !! as AppOpsManager
        mode = appOpsManager.unsafeCheckOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED
    }

}