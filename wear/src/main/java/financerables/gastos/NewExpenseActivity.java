package financerables.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;;
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
import java.util.concurrent.TimeUnit;


public class NewExpenseActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

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
        Intent intent = getIntent();
        final Date date = new Date();
        details = intent.getStringExtra(CmdWatchActivity.EXPENSE_DETAILS);
        amount = intent.getFloatExtra(CmdWatchActivity.EXPENSE_AMOUNT, -1.0f);
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
                ((TextView) stub.findViewById(R.id.expense)).setText(details);
                ((TextView) stub.findViewById(R.id.amount)).setText(Float.toString(amount));
            }
        });
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
}
