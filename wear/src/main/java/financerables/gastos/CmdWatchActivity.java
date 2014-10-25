package financerables.gastos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdWatchActivity extends Activity {
    public final static String EXPENSE_DETAILS = "financerables.gastos.expense_details";
    public final static String EXPENSE_AMOUNT = "financerables.gastos.expense_amount";

    private static final int SPEECH_REQUEST_CODE = 0;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        processExpense("Apples 10.38");
        super.onResume();
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
//            List<String> results = data.getStringArrayListExtra(
//                    RecognizerIntent.EXTRA_RESULTS);
//            processExpense(results.get(0));
//            //
        super.onActivityResult(requestCode, resultCode, data);
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
            Intent intent = new Intent(this, NewExpenseActivity.class);
            intent.putExtra(EXPENSE_DETAILS, expenseMatcher.group(1));
            intent.putExtra(EXPENSE_AMOUNT, Float.parseFloat(expenseMatcher.group(2)));
            startActivity(intent);
        }
    }
}
