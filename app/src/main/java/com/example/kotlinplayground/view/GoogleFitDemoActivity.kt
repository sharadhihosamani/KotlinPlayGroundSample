package com.example.kotlinplayground.view


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinplayground.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.FitnessActivities
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.*
import com.google.android.gms.fitness.data.HealthDataTypes.TYPE_BLOOD_PRESSURE
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.SessionReadRequest
import com.google.android.gms.fitness.result.DataReadResponse
import com.google.android.gms.fitness.result.SessionReadResponse
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_google_fit_demo.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit


const val TAG = "BasicHistoryApi"

/**
 * This enum is used to define actions that can be performed after a successful sign in to Fit.
 * One of these values is passed to the Fit sign-in, and returned in a successful callback, allowing
 * subsequent execution of the desired action.
 */
enum class FitActionRequestCode {
    READ_DATA
}

/**
 * This sample demonstrates how to use the History API of the Google Fit platform to insert data,
 * query against existing data, and remove data. It also demonstrates how to authenticate a user
 * with Google Play Services and how to properly represent data in a {@link DataSet}.
 */
class GoogleFitDemoActivity : AppCompatActivity() {

    private val dateFormat = DateFormat.getDateInstance()
    private val fitnessOptions: FitnessOptions by lazy {
        FitnessOptions.builder()
            .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
            .addDataType(TYPE_BLOOD_PRESSURE, FitnessOptions.ACCESS_READ)
            .addDataType(HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_DISTANCE_DELTA)
            .addDataType(DataType.AGGREGATE_DISTANCE_DELTA)
            .build()
       }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_google_fit_demo)
            setSupportActionBar(findViewById(R.id.toolbar))
            fitSignIn(FitActionRequestCode.READ_DATA)
            }


    /**
     * Checks that the user is signed in, and if so, executes the specified function. If the user is
     * not signed in, initiates the sign in flow, specifying the post-sign in function to execute.
     *
     * @param requestCode The request code corresponding to the action to perform after sign in.
     */
    private fun fitSignIn(requestCode: FitActionRequestCode) {
        if (oAuthPermissionsApproved()) {
            performActionForRequestCode(requestCode)
        } else {
            requestCode.let {
                GoogleSignIn.requestPermissions(
                    this,
                    requestCode.ordinal,
                    getGoogleAccount(), fitnessOptions)
            }
        }
    }

    /**
     * Handles the callback from the OAuth sign in flow, executing the post sign in function
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (resultCode) {
            RESULT_OK -> {
                val postSignInAction = FitActionRequestCode.values()[requestCode]
                postSignInAction.let {
                    performActionForRequestCode(postSignInAction)
                }
            }
            else -> oAuthErrorMsg(requestCode, resultCode)
        }
    }

    /**
     * Runs the desired method, based on the specified request code. The request code is typically
     * passed to the Fit sign-in flow, and returned with the success callback. This allows the
     * caller to specify which method, post-sign-in, should be called.
     *
     * @param requestCode The code corresponding to the action to perform.
     */
    private fun performActionForRequestCode(requestCode: FitActionRequestCode) = when (requestCode) {
        FitActionRequestCode.READ_DATA -> readHistoryData().continueWith {readWorkOutData()}.continueWith {readBloodPressure()}
    }

    private fun readBloodPressure(): Task<DataReadResponse> {
        // Begin by creating the query.
        val readRequest = queryBloodPressureData()

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                // For the sake of the sample, we'll print the data so we can see what we just
                // added. In general, logging fitness information should be avoided for privacy
                // reasons.
                printData(dataReadResponse,"BP")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the
     * data.
     */

    private fun readWorkOutFitnessData(): Task<DataReadResponse> {
        // Begin by creating the query.
        val readRequest = queryFitnessSessionData()

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                // For the sake of the sample, we'll print the data so we can see what we just
                // added. In general, logging fitness information should be avoided for privacy
                // reasons.
                val bucketList: List<Bucket> = dataReadResponse.buckets
//                for (bucket in bucketList) {
//                    for (ds in bucket.dataSets) {
//                        for (dp in ds.dataPoints) {
//                            Log.i(TAG, "Data point:")
//                            Log.i(TAG, "\tType: ${dp.dataType.name}")
//                            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
//                            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
//                            var tempDesc = ""
//                            dp.dataType.fields.forEach {
//                                Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
//                                tempDesc += "\tField: ${it.name} Value: ${dp.getValue(it)}"
//                            }
//                        }
//                    }

                var returnValue: String? = ""

                if (dataReadResponse.buckets.size > 0) {
                    for (i in 0 until dataReadResponse.buckets.size) {
                        returnValue += ("\n\n$i ---new bucket-- activity: " + dataReadResponse.buckets[i].activity + "\n"
                              + "~")
                        for (j in 0 until dataReadResponse.buckets[i].dataSets.size) {
                            returnValue += "\n-data set $j-package: " + dataReadResponse.buckets[i].dataSets[j].dataSource
                                .appPackageName + ", stream: " + dataReadResponse.buckets[i].dataSets[j].dataSource.streamIdentifier
                            returnValue += handleDailyRecordInDataSet(
                                dataReadResponse.buckets[i].dataSets[j]
                            )
                        }
                    }
                }
            }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "There was a problem reading the data.", e)
                    }
    }

    private fun handleDailyRecordInDataSet(dataSet: DataSet): String? {
        var returnValue = ""
        for (dataPoint in dataSet.dataPoints) {

            //var activityName= dataPoint.getValue(Field.FIELD_ACTIVITY)
           // var activityyCalorie = dataPoint.getValue(Field.FIELD_CALORIES)

            val startTime =
                dataPoint.getStartTime(TimeUnit.MILLISECONDS)
            val endTime =
                dataPoint.getEndTime(TimeUnit.MILLISECONDS)
            var tempValue =
                "DataPoint start: " + startTime
                    .toString() + ", end=" + endTime
                    .toString() + ", type=" + dataPoint.dataType.name
                    .toString() + ",  package=" + dataPoint.dataSource.appPackageName
                    .toString() + ", stream=" + dataPoint.dataSource.streamIdentifier
            if (dataPoint.dataSource.device != null) {
                tempValue += "\nManufacturer=" + dataPoint.dataSource.device!!
                    .manufacturer.toString() + ", model=" + dataPoint.dataSource
                    .device!!.model
                    .toString() + ", uid: " + dataPoint.dataSource.device!!.uid
                    .toString() + ", type=" + dataPoint.dataSource.device!!.type
            }
            tempValue += "\norigin source: package=" + dataPoint.originalDataSource
                .appPackageName.toString() + ", stream=" + dataPoint.originalDataSource
                .streamIdentifier
            if (dataPoint.originalDataSource.device != null) {
                tempValue += "\nManufacturer=" + dataPoint.originalDataSource.device!!
                    .manufacturer.toString() + ", model=" + dataPoint.originalDataSource
                    .device!!.model
                    .toString() + ", uid: " + dataPoint.originalDataSource.device!!.uid
                    .toString() + ", type=" + dataPoint.originalDataSource.device!!
                    .type
            }
            returnValue += """
                
                
                $tempValue
                """.trimIndent()
            for (field in dataPoint.dataType.fields) {
                val fieldValue =
                    "Field name: " + field.name.toString() + ", value: " + dataPoint.getValue(
                        field
                    )
                returnValue += """
                    
                    $fieldValue
                    """.trimIndent()
            }
        }
        return returnValue
    }

    private fun readWorkOutData(): Task<SessionReadResponse> {
        val readRequest = queryWorkOutData()
        return Fitness.getSessionsClient(
            this,
            GoogleSignIn.getLastSignedInAccount(this)!!
        )
            .readSession(readRequest)
            .addOnSuccessListener { sessionReadResponse ->
                // Get a list of the sessions that match the criteria to check the result.
                val sessions =
                    sessionReadResponse.sessions
                Log.i(
                    TAG, "Session read was successful. Number of returned sessions is: "
                            + sessions.size
                )
                for (session in sessions) {
                    // Process the session
                    dumpSession(session)

                    // Process the data sets for this session
                    val dataSets =
                        sessionReadResponse.getDataSet(session)
                    for (dataSet in dataSets) {
                        dumpDataSet(dataSet!!,"")
                    }
                }
            }
            .addOnFailureListener { Log.i(TAG, "Failed to read session") }
    }

    private fun dumpSession(session: Session) {
        var concatText=   txt_work_out_value.text
       var act= session.activity
        txt_work_out_value.text =  "Data returned for Session: " + session.name +
                "\n\tDescription: " +
                session.description + "\n\tStart: " +
                session.getStartTimeString()+ "\n\tEnd: " +
                session.getEndTimeString() + concatText

        Log.i(TAG, "Data returned for Session: " + session.name
                + "\n\tDescription: " + session.description
                + "\n\tStart: " + session.getStartTimeString()
                + "\n\tEnd: " + session.getEndTimeString())
    }

    private fun queryWorkOutData(): SessionReadRequest {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        // Set a start and end time for our query, using a start time of 1 week before this moment.
        // Set a start and end time for our query, using a start time of 1 week before this moment.
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.WEEK_OF_YEAR, -3)
        val startTime = cal.timeInMillis

        return SessionReadRequest.Builder()
            .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
            .read(DataType.TYPE_CALORIES_EXPENDED)
            .read(DataType.TYPE_SPEED)
            .read(DataType.TYPE_DISTANCE_DELTA)
            .readSessionsFromAllApps()
            .build()
    }

    private fun queryFitnessSessionData() : DataReadRequest {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        return DataReadRequest.Builder()
            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
            .read(DataType.TYPE_ACTIVITY_SEGMENT)
            .bucketByActivitySegment( 1, TimeUnit.MINUTES)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build()
    }
    /** Returns a [DataReadRequest] for all step count changes in the past week.  */
    private fun queryFitnessData(): DataReadRequest {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        return DataReadRequest.Builder()
            // The data request can specify multiple data types to return, effectively
            // combining multiple data queries into one call.
            // In this example, it's very unlikely that the request is for several hundred
            // datapoints each consisting of a few steps and a timestamp.  The more likely
            // scenario is wanting to see how many steps were walked per day, for 7 days.
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
            // Analogous to a "Group By" in SQL, defines how data should be aggregated.
            // bucketByTime allows for a time span, whereas bucketBySession would allow
            // bucketing by "sessions", which would need to be defined in code.
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)

            .build()
    }

    private  fun queryBloodPressureData():DataReadRequest
    {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val now = Date()
        calendar.time = now
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis

        Log.i(TAG, "Range Start: ${dateFormat.format(startTime)}")
        Log.i(TAG, "Range End: ${dateFormat.format(endTime)}")

        return DataReadRequest.Builder()
            .aggregate(
                TYPE_BLOOD_PRESSURE,
                HealthDataTypes.AGGREGATE_BLOOD_PRESSURE_SUMMARY
            )
            .bucketByTime(1, TimeUnit.DAYS) //important thing
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .enableServerQueries()
            .build()
    }

    /**
     * Asynchronous task to read the history data. When the task succeeds, it will print out the
     * data.
     */
    private fun readHistoryData(): Task<DataReadResponse> {
        // Begin by creating the query.
        val readRequest = queryFitnessData()

        // Invoke the History API to fetch the data with the query
        return Fitness.getHistoryClient(this, getGoogleAccount())
            .readData(readRequest)
            .addOnSuccessListener { dataReadResponse ->
                // For the sake of the sample, we'll print the data so we can see what we just
                // added. In general, logging fitness information should be avoided for privacy
                // reasons.
                printData(dataReadResponse)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "There was a problem reading the data.", e)
            }
    }

    private fun oAuthErrorMsg(requestCode: Int, resultCode: Int) {
        val message = """
            There was an error signing into Fit. Check the troubleshooting section of the README
            for potential issues.
            Request code was: $requestCode
            Result code was: $resultCode
        """.trimIndent()
        Log.e(TAG, message)
    }

    private fun oAuthPermissionsApproved() = GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions)

    /**
     * Gets a Google account for use in creating the Fitness client. This is achieved by either
     * using the last signed-in account, or if necessary, prompting the user to sign in.
     * `getAccountForExtension` is recommended over `getLastSignedInAccount` as the latter can
     * return `null` if there has been no sign in before.
     */
    private fun getGoogleAccount() = GoogleSignIn.getAccountForExtension(this, fitnessOptions)

    /**
     * Logs a record of the query result. It's possible to get more constrained data sets by
     * specifying a data source or data type, but for demonstrative purposes here's how one would
     * dump all the data. In this sample, logging also prints to the device screen, so we can see
     * what the query returns, but your app should not log fitness information as a privacy
     * consideration. A better option would be to dump the data you receive to a local data
     * directory to avoid exposing it to other applications.
     */
    private fun printData(dataReadResult: DataReadResponse,type : String="") {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.buckets.isNotEmpty()) {
            Log.i(TAG, "Number of returned buckets of DataSets is: " + dataReadResult.buckets.size)
            for (bucket in dataReadResult.buckets) {
                bucket.dataSets.forEach { dumpDataSet(it, type) }
            }
        } else if (dataReadResult.dataSets.isNotEmpty()) {
            Log.i(TAG, "Number of returned DataSets is: " + dataReadResult.dataSets.size)
            dataReadResult.dataSets.forEach { dumpDataSet(it, type) }
        }
        // [END parse_read_data_result]
    }

    // [START parse_dataset]
    @SuppressLint("SetTextI18n")
    private fun dumpDataSet(dataSet: DataSet, type: String) {
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        for (dp in dataSet.dataPoints) {
            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            Log.i(TAG, "\tStart: ${dp.getStartTimeString()}")
            Log.i(TAG, "\tEnd: ${dp.getEndTimeString()}")
            var tempDesc=""
            dp.dataType.fields.forEach {
                Log.i(TAG, "\tField: ${it.name} Value: ${dp.getValue(it)}")
                tempDesc += "\tField: ${it.name} Value: ${dp.getValue(it)}"
            }

            if(type=="BP")
            {
                txt_blood_pressure_value.text= "Data point: \tType: ${dp.dataType.name} \tStart: ${dp.getStartTimeString()} \tEnd: ${dp.getEndTimeString()} $tempDesc"
            }
            else
            {
                txt_step_count_value.text= "Data point: \tType: ${dp.dataType.name} \tStart: ${dp.getStartTimeString()} \tEnd: ${dp.getEndTimeString()} $tempDesc"
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_data -> {
               // fitSignIn(FitActionRequestCode.DELETE_DATA)
                true
            }
            R.id.action_update_data -> {
               // fitSignIn(FitActionRequestCode.UPDATE_AND_READ_DATA)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
