package com.example.networking;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private String[] mountainNames = {"Matterhorn", "K2", "Mount Everest"};
    private String[] mountainLocations = {"Alps", "The Karakoram range", "The Mahalangur Himal sub-range of the Himalayas"};
    private Integer[] mountainHeights = {4478, 8611, 8884};

    //private ArrayList<Mountain> mountainArrayList;

    public ArrayList<String> listDataNames = new ArrayList<>();
    private ArrayList<Integer> listDataSize = new ArrayList<>();
    private ArrayList<String> listDataLocation = new ArrayList<>();
    private ArrayList<Mountain> mountainArrayList=new ArrayList<>();
    public ArrayAdapter<Mountain> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        new JsonTask().execute("https://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?login=brom");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.textView);
        adapter = new ArrayAdapter<Mountain>(this, R.layout.list_item, R.id.list_item, mountainArrayList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Toast.makeText(getApplicationContext(), mountainArrayList.get(position).info(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {

        private HttpURLConnection connection = null;
        private BufferedReader reader = null;
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?login=brom");
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !isCancelled()) {
                    builder.append(line).append("\n");
                }
                return builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            Log.d("TAG", json);
            JSONArray json1 = null;
            try {
                json1 = new JSONArray(json);
                for (int i = 0; i < json1.length(); i++) {
                    JSONObject berg = json1.getJSONObject(i);
                    Log.d("berg", berg.toString());
                    MainActivity.this.listDataNames.add(berg.getString("name"));
                    MainActivity.this.listDataSize.add(berg.getInt("size"));
                    MainActivity.this.listDataLocation.add(berg.getString("location"));
                    Mountain mountain = new Mountain(berg.getString("name"),(berg.getString("location")),(berg.getInt("size")));
                    mountainArrayList.add(mountain);
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}