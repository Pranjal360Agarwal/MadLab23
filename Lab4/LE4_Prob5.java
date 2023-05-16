public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
    }

    public void onImageClick(View view) {
        if (imageView.getDrawable().getConstantState() == getResources().getDrawable(R.drawable.trump).getConstantState()) {
            imageView.setImageResource(R.drawable.modi);
        } else {
            imageView.setImageResource(R.drawable.trump);
        }
    }
}