public class MainActivity extends AppCompatActivity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
    }

    public void onConvertClick(View view) {
        String amountStr = editText.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
        } else {
            double amount = Double.parseDouble(amountStr);
            double conversionRate = 73.27; // current conversion rate
            double inr = amount * conversionRate;
            String result = String.format("INR %.2f", inr);
            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        }
    }
}
