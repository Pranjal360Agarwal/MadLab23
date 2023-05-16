class MainActivity : AppCompatActivity() {

    private lateinit var modiImageView: ImageView
    private lateinit var rahulImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        modiImageView = findViewById(R.id.modiImageView)
        rahulImageView = findViewById(R.id.rahulImageView)

        modiImageView.setOnClickListener {
            modiImageView.visibility = View.INVISIBLE
            Handler().postDelayed({
                rahulImageView.visibility = View.VISIBLE
            }, 2000)
        }

        rahulImageView.setOnClickListener {
            rahulImageView.visibility = View.INVISIBLE
            Handler().postDelayed({
                modiImageView.visibility = View.VISIBLE
            }, 2000)
        }
    }
}
