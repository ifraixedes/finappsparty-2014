package financerables.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

;


public class NewExpenseActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

    private static final int SPEECH_REQUEST_CODE = 0;
    public final String TAG = "NewExpenseActivity";
    public final String UID = "/expense";
    public final String DETAILS_KEY = "details";
    public final String AMOUNT_KEY = "amount";
    public final String DATETIME_KEY = "datetime";
    protected String details;
    protected float amount;
    protected GregorianCalendar calendar;
    protected PutDataMapRequest dataMapRequest;
    protected GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Date date = new Date();
        calendar = new GregorianCalendar();
        calendar.setTime(date);

        setContentView(R.layout.activity_new_expense);


        setContentView(R.layout.activity_new_expense);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.expense_watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ((TextView) stub.findViewById(R.id.date)).setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
                ((TextView) stub.findViewById(R.id.time)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
            }
        });

        displaySpeechRecognizer();
    }

    public void showExpense(String details, String amount) {
        TextView expenseTextView = (TextView) findViewById(R.id.expense);
        TextView amountTextView = (TextView) findViewById(R.id.amount);

        expenseTextView.setText(details);
        amountTextView.setText(amount);
    }

    public void confirmExpense(View view) {
        dataMapRequest = PutDataMapRequest.create(UID);
        ArrayList dateAsList = new ArrayList<Integer>();

        dateAsList.add(calendar.get(Calendar.YEAR));
        dateAsList.add(calendar.get(Calendar.MONTH));
        dateAsList.add(calendar.get(Calendar.DAY_OF_MONTH));
        dateAsList.add(calendar.get(Calendar.HOUR));
        dateAsList.add(calendar.get(Calendar.MINUTE));

        dataMapRequest.getDataMap().putString(DETAILS_KEY, details);
        dataMapRequest.getDataMap().putFloat(AMOUNT_KEY, amount);
        dataMapRequest.getDataMap().putIntegerArrayList(DATETIME_KEY, dateAsList);

        sendExpenseData();
    }

    public void cancelExpense(View view) {
        finish();
    }

    public void sendExpenseData() {
        Log.i(TAG, "sendExpenseData");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "Connected to Google Api Service");
        }
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                .putDataItem(googleApiClient, dataMapRequest.asPutDataRequest());

        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(DataApi.DataItemResult dataItemResult) {
                Log.e(TAG, "READY");
                googleApiClient.disconnect();
            }
        }, 5, TimeUnit.SECONDS);
    }

    @Override
    public void onConnectionSuspended(int code) {
        Log.e(TAG, Integer.toString(code));
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, Integer.toString(result.getErrorCode()));
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            processExpense(results.get(0));
            //
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    private void processExpense(String spokenText) {
        Log.i("got", spokenText);

        Pattern expenseInputPattern = Pattern.compile("([a-z]+) ([\\d]*(?:.[\\d]+)*)", Pattern.CASE_INSENSITIVE);
        Matcher expenseMatcher = expenseInputPattern.matcher(spokenText);

        if (expenseMatcher.matches()) {
            Log.i("expense", expenseMatcher.group(1));
            Log.i("amount", expenseMatcher.group(2));
            showExpense(expenseMatcher.group(1), expenseMatcher.group(2));
        }
    }
}
