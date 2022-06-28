package com.lordsoftech;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PrimaryController {

    private static final String API_KEY = "AIzaSyAa91J9YuHAFgymjhZg3tC0HR-CoOU-UHs";
    private static final String CLIENT_KEY = "javagifsearch";
    private static final String LogTag = "TenorTest";



    @FXML
    private void searchRemote(StringProperty property, String oldValue, String searchTerm) {
        //API KEY: AIzaSyAa91J9YuHAFgymjhZg3tC0HR-CoOU-UHs
        //String searchTerm = searchField


        System.out.println(searchTerm);
    }

    @FXML
    private TextField searchField;
   @FXML
   private ImageView img0;

   PauseTransition fetchDelay = new PauseTransition(Duration.millis(200));
    public void initialize() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {

            fetchDelay.setOnFinished(event -> fetchGifs(newText));
            fetchDelay.playFromStart();
        });
    }

    public void fetchGifs(String searchString) {
        if(searchString.length()>0) {
            try {
                JSONObject searchResult = getSearchResults(searchString, 8);
                JSONArray resultArray = searchResult.getJSONArray("results");
                JSONObject gif = resultArray.getJSONObject(0).getJSONObject("media_formats").getJSONObject("gif");
                String url = gif.getString("url");
                Image img = new Image(url, true);
                System.out.println();
                img0.setImage(img);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        //img0.setImage();
    }

    public static JSONObject getSearchResults(String searchTerm, int limit) {

        // make search request - using default locale of EN_US

        final String url = String.format("https://tenor.googleapis.com/v2/search?q=%1$s&key=%2$s&client_key=%3$s&limit=%4$s",
                searchTerm, API_KEY, CLIENT_KEY, limit);
        try {
            return get(url);
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

    private static JSONObject get(String url) throws IOException, JSONException {
        HttpURLConnection connection = null;
        try {
            // Get request
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            // Handle failure
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED) {
                String error = String.format("HTTP Code: '%1$s' from '%2$s'", statusCode, url);
                throw new ConnectException(error);
            }

            // Parse response
            return parser(connection);
        } catch (Exception ignored) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return new JSONObject("");
    }

    private static JSONObject parser(HttpURLConnection connection) throws JSONException {
        char[] buffer = new char[1024 * 4];
        int n;
        try (InputStream stream = new BufferedInputStream(connection.getInputStream())) {
            InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
            return new JSONObject(writer.toString());
        } catch (IOException ignored) {
        }
        return new JSONObject("");
    }
}
