package financerables.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;
import java.util.Date;
import java.text.DateFormat;

public class NewExpenseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final String details = intent.getStringExtra(CmdWatchActivity.EXPENSE_DETAILS);
        final float amount = intent.getFloatExtra(CmdWatchActivity.EXPENSE_AMOUNT, -1.0f);

        setContentView(R.layout.activity_new_expense);



        setContentView(R.layout.activity_new_expense);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.expense_watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                Date date = new Date();
                ((TextView) stub.findViewById(R.id.date)).setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(date));
                ((TextView) stub.findViewById(R.id.time)).setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(date));
                ((TextView) stub.findViewById(R.id.expense)).setText(details);
                ((TextView) stub.findViewById(R.id.amount)).setText(Float.toString(amount));
                ((TextView) stub.findViewById(R.id.total)).setText("$$$");
            }
        });

    }
}
