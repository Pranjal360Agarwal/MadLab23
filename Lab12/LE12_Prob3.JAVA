public class Story {

    private int id;
    private String title;

    public Story(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_BASE_URL = "https://hacker-news.firebaseio.com/v0/";
    private static final String TOP_STORIES_ENDPOINT = "topstories.json";
    private static final String ITEM_ENDPOINT = "item/";

    private List<Story> stories;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stories = new ArrayList<>();
        textView = findViewById(R.id.textView);

        fetchTopStories();
    }

    private void fetchTopStories() {
        String url = API_BASE_URL + TOP_STORIES_ENDPOINT;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                int id = response.getInt(i);
                                fetchStory(id);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing top stories JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error fetching top stories", error);
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void fetchStory(int id) {
        String url = API_BASE_URL + ITEM_ENDPOINT + id + ".json";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject storyObject = response.getJSONObject(0);
                            int id = storyObject.getInt("id");
                            String title = storyObject.getString("title");
                            Story story = new Story(id, title);
                            stories.add(story);
                            updateUI();
                        } catch (JSONException e) {
                            Log.e(TAG, "Error parsing story JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    Log.e(TAG, "Error fetching story", error);
                }
            });

    Volley.newRequestQueue(this).add(request);
}

private void updateUI() {
    StringBuilder builder = new StringBuilder();
    for (Story story : stories) {
        builder.append(story.getId()).append(": ").append(story.getTitle()).append("\n\n");
    }
    textView.setText(builder.toString());
}
