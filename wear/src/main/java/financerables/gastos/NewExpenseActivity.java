package financerables.gastos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class NewExpenseActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

    }
}
