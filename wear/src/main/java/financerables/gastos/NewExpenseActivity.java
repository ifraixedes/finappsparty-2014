package financerables.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class NewExpenseActivity extends Activity {

    public final String UID = "/expense";
    public final String DETAILS_KEY = "details";
    public final String AMOUNT_KEY = "amount";
    public final String DATETIME_KEY = "datetime";
    protected String details;
    protected float amount;
    protected GregorianCalendar calendar;

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
        PutDataMapRequest dataMapReq = PutDataMapRequest.create(UID);
        DataMap dataMap = dataMapReq.getDataMap();
        ArrayList dateAsList = new ArrayList <Integer>();

        dateAsList.add(calendar.get(Calendar.YEAR));
        dateAsList.add(calendar.get(Calendar.MONTH));
        dateAsList.add(calendar.get(Calendar.DAY_OF_MONTH));
        dateAsList.add(calendar.get(Calendar.HOUR));
        dateAsList.add(calendar.get(Calendar.MINUTE));

        dataMap.putString(DETAILS_KEY, details);
        dataMap.putFloat(AMOUNT_KEY, amount);
        dataMap.putIntegerArrayList(DATETIME_KEY, dateAsList);


    }

    public void cancelExpense(View view) {

    }
}
