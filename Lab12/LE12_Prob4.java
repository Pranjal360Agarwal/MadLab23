public class Story {
    private long id;
    private String title;
    private String url;
    private String imageUrl;
    private String date;

    public Story(long id, String title, String url, String imageUrl, String date) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDate() {
        return date;
    }
}


public class StoryAdapter extends ArrayAdapter<Story> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM d, yyyy", Locale.US);

    public StoryAdapter(Context context, List<Story> stories) {
        super(context, 0, stories);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_story, parent, false);
        }

        Story story = getItem(position);

        TextView titleTextView = convertView.findViewById(R.id.titleTextView);
        titleTextView.setText(story.getTitle());

        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        dateTextView.setText(DATE_FORMAT.format(new Date(Long.parseLong(story.getDate()) * 1000)));

        ImageView imageView = convertView.findViewById(R.id.imageView);
        Picasso.get().load(story.getImageUrl()).into(imageView);

        return convertView;
    }
}


public class NewsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "news.db";
    private static final int DATABASE_VERSION = 1;

    public NewsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE stories (_id INTEGER PRIMARY KEY, title TEXT, url TEXT, imageUrl TEXT, date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS stories");
        onCreate(db);
    }
}
