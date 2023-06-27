package com.kylecorry.weather.ui

import android.util.Log
import com.kylecorry.andromeda.exceptions.*
import com.kylecorry.weather.infrastructure.errors.FragmentDetailsBugReportGenerator
import com.kylecorry.weather.R

object ExceptionHandler {

    fun initialize(activity: MainActivity) {
        val handler = EmailExceptionHandler(
            activity,
            AggregateBugReportGenerator(
                listOf(
                    AppDetailsBugReportGenerator(activity.getString(R.string.app_name)),
                    AndroidDetailsBugReportGenerator(),
                    DeviceDetailsBugReportGenerator(),
                    FragmentDetailsBugReportGenerator(),
                    StackTraceBugReportGenerator()
                )
            )
        ) { context, log ->
            Log.e(context.getString(R.string.app_name), log)
            BugReportEmailMessage(
                context.getString(R.string.error_occurred),
                context.getString(R.string.error_occurred_message),
                context.getString(R.string.email_developer),
                context.getString(android.R.string.cancel),
                context.getString(R.string.email),
                "Error in ${context.getString(R.string.app_name)}"
            )
        }
        handler.bind()
    }

}