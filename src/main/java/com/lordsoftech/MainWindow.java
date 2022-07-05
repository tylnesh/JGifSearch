package com.lordsoftech;

import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.effects.JFXDepthManager;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainWindow {

    private final Integer LIMIT = 20;
    private String nextPos = "";
    @FXML private TextField searchField;
    @FXML private com.jfoenix.controls.JFXMasonryPane masonryPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML private Button loadMoreBtn;

   PauseTransition fetchDelay = new PauseTransition(Duration.millis(200));
    public void initialize() {

        loadMoreBtn.setVisible(false);
        fetchAndDisplayGifs(searchField.getText());

        // Add listeners
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            fetchDelay.setOnFinished(event -> fetchAndDisplayGifs(newText));
            fetchDelay.playFromStart();
        });

        scrollPane.vvalueProperty().addListener((observableValue, number, t1) -> {
            if (t1.doubleValue()>=.90) {
                loadMoreBtn.setVisible(true);
            }
            else {
                loadMoreBtn.setVisible(false);
            }
        });

        loadMoreBtn.setOnAction(event -> fetchMoreGifs(searchField.getText()));
    }

    private void fetchMoreGifs(String searchString) {
        if(searchString.length()>0) {
            searchString = searchString.replace(' ' ,'+');
            System.out.println(searchString);
            try {
                JSONObject searchResult = getSearchResults(searchString,LIMIT,nextPos);
                nextPos = searchResult.getString("next");
                generateMasonry(searchResult);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        else {
            try {
                JSONObject featuredResults = getFeaturedResults(LIMIT,nextPos);
                generateMasonry(featuredResults);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }


    public void fetchAndDisplayGifs(String searchString) {
        masonryPane.getChildren().clear();
        if(searchString.length()>0) {
            searchString = searchString.replace(' ' ,'+');
            System.out.println(searchString);
            try {
                JSONObject searchResult = getSearchResults(searchString,LIMIT);
                nextPos = searchResult.getString("next");
                generateMasonry(searchResult);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
        else {
            try {
                JSONObject featuredResults = getFeaturedResults(LIMIT);
                generateMasonry(featuredResults);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }



    public void generateMasonry(JSONObject sourceJsonObject) {
        ArrayList<Node> masonryTiles = new ArrayList<>();
        JSONArray resultArray = sourceJsonObject.getJSONArray("results");

        for (int i = 0; i < LIMIT; i++) {
            //get tinygif object from the Json response
            JSONObject gifObject = resultArray.getJSONObject(i).getJSONObject("media_formats").getJSONObject("tinygif");

            //create c stackpane container object
            StackPane tile = new StackPane();
            int width = gifObject.getJSONArray("dims").getInt(0);
            tile.setPrefWidth(width + 20.0);
            int height = gifObject.getJSONArray("dims").getInt(1);
            tile.setPrefHeight(height + 20.0);
            JFXDepthManager.setDepth(tile, 1);
            masonryTiles.add(tile);

            // create image content
            ImageView img = new ImageView();
            img.setFitWidth(width);
            img.setFitHeight(height);
            String url = gifObject.getString("url");
            Image sourceImg = new Image(url, true);
            img.setImage(sourceImg);
            img.setOnMouseClicked( event -> {
                animateClick(img);
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent content = new ClipboardContent();
                content.putString(url);
                clipboard.setContent(content);
            });
            tile.getChildren().add(img);
        }

        masonryPane.getChildren().addAll(masonryTiles);
        Platform.runLater(() -> scrollPane.requestLayout());
        //JFXScrollPane.smoothScrolling(scrollPane);

    }


    public static void animateClick(Node node){
        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setByX(1.1);
        scaleTransition.setByY(1.1);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.setDuration(Duration.millis(50));
        scaleTransition.play();
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

    public static JSONObject getSearchResults(String searchTerm, int limit, String nextPos) {
        // make search request - using default locale of EN_US
        final String url = String.format("https://tenor.googleapis.com/v2/search?q=%1$s&key=%2$s&client_key=%3$s&limit=%4$s&pos=%5$s",
                searchTerm, ApiKey.getApiKey(), ApiKey.getClientKey(), limit, nextPos);
        try {
            return get(url);
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

    public static JSONObject getFeaturedResults(int limit) {
        final String url = String.format("https://tenor.googleapis.com/v2/featured?key=%1$s&client_key=%2$s&limit=%3$s",
                ApiKey.getApiKey(), ApiKey.getClientKey(),limit);
        try {
            return get(url);
        } catch (IOException | JSONException ignored) {
        }
        return null;
    }

    public static JSONObject getFeaturedResults(int limit, String nextPos) {
        final String url = String.format("https://tenor.googleapis.com/v2/featured?key=%1$s&client_key=%2$s&limit=%3$s&pos=%4$s",
                ApiKey.getApiKey(), ApiKey.getClientKey(),limit, nextPos);
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
