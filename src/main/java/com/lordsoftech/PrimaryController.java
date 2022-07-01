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


    @FXML private TextField searchField;
   @FXML private ImageView img0;
   @FXML private ImageView img1;
   @FXML private ImageView img2;
    @FXML private ImageView img3;
    @FXML private ImageView img4;
    @FXML private ImageView img5;
    @FXML private ImageView img6;
    @FXML private ImageView img7;
    @FXML private ImageView img8;
   PauseTransition fetchDelay = new PauseTransition(Duration.millis(200));
    public void initialize() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {

            fetchDelay.setOnFinished(event -> fetchGifs(newText));
            fetchDelay.playFromStart();
        });
    }

    public void fetchGifs(String searchString) {
        if(searchString.length()>0) {
            searchString = searchString.replace(' ' ,'+');
            System.out.println(searchString);
            try {
                JSONObject searchResult = getSearchResults(searchString, 9);
                putImgInView(searchResult, 0, img0);
                putImgInView(searchResult, 1, img1);
                putImgInView(searchResult, 2, img2);
                putImgInView(searchResult, 3, img3);
                putImgInView(searchResult, 4, img4);
                putImgInView(searchResult, 5, img5);
                putImgInView(searchResult, 6, img6);
                putImgInView(searchResult, 7, img7);
                putImgInView(searchResult, 8, img8);

            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        //img0.setImage();
    }

    public void putImgInView(JSONObject searchResults, int index, ImageView img) {
        JSONArray resultArray = searchResults.getJSONArray("results");
        JSONObject gif = resultArray.getJSONObject(index).getJSONObject("media_formats").getJSONObject("tinygif");
        String url = gif.getString("url");
        img.setImage(new Image(url, true));
    }

    public static JSONObject getSearchResults(String searchTerm, int limit) {

        // make search request - using default locale of EN_US

        final String url = String.format("https://tenor.googleapis.com/v2/search?q=%1$s&key=%2$s&client_key=%3$s&limit=%4$s",
                searchTerm, ApiKey.getApiKey(), ApiKey.getClientKey(), limit);
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
