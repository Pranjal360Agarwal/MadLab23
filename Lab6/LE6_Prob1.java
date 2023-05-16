class MainActivity : AppCompatActivity() {

    private lateinit var gridLayout: GridLayout
    private lateinit var cellImageViews: Array<ImageView>
    private var isX: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gridLayout = findViewById(R.id.gridLayout)
        cellImageViews = arrayOf(
            findViewById(R.id.cell1ImageView),
            findViewById(R.id.cell2ImageView),
            findViewById(R.id.cell3ImageView),
            findViewById(R.id.cell4ImageView),
            findViewById(R.id.cell5ImageView),
            findViewById(R.id.cell6ImageView),
            findViewById(R.id.cell7ImageView),
            findViewById(R.id.cell8ImageView),
            findViewById(R.id.cell9ImageView)
        )

        for (cellImageView in cellImageViews) {
            cellImageView.setOnClickListener {
                val drawableId = if (isX) R.drawable.x else R.drawable.o
                val drawable = ContextCompat.getDrawable(this, drawableId)
                animateFallingImage(cellImageView, drawable)
                isX = !isX
            }
        }
    }

    private fun animateFallingImage(imageView: ImageView, drawable: Drawable?) {
        val anim = ObjectAnimator.ofFloat(imageView, "translationY", -1000f, 0f)
        anim.duration = 1000
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                imageView.setImageDrawable(drawable)
            }
        })
        anim.start()
    }
}