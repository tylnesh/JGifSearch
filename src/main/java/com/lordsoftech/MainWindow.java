package com.lordsoftech;

import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.effects.JFXDepthManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.util.Duration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Stack;

public class MainWindow {

    private final Integer LIMIT = 20;
    private String nextPos = "";
    @FXML private TextField searchField;
    @FXML private com.jfoenix.controls.JFXMasonryPane masonryPane;
    @FXML
    private ScrollPane scrollPane;
    @FXML private Button loadMoreBtn;



    // Frosty glassy effect rolling up on hover
    private static final double BLUR_AMOUNT = 30;
    private static final Effect frostEffect =
            new BoxBlur(BLUR_AMOUNT, BLUR_AMOUNT, 3);
    private static final Duration SLIDE_DURATION = Duration.seconds(.3);
    //private static final double UPPER_SLIDE_POSITION = 100;

   PauseTransition fetchDelay = new PauseTransition(Duration.millis(200));
    public void initialize() {

        loadMoreBtn.setVisible(false);
        fetchAndDisplayGifs(searchField.getText());

        // Add listeners
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            fetchDelay.setOnFinished(event -> fetchAndDisplayGifs(newText));
            fetchDelay.playFromStart();
        });

        //TODO: Fix a bug where scrollpane is unavailable and loadMoreBtn doesn't show
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
            String url = gifObject.getString("url");
            Image sourceImg = new Image(url, true);

            //get width and height
            double width = gifObject.getJSONArray("dims").getDouble(0);
            double height = gifObject.getJSONArray("dims").getDouble(1);

            DoubleProperty y = new SimpleDoubleProperty(height + 20);

            //create stack pane container object
            StackPane tile = createTile(width, height);

            // create image content
            ImageView img = createGifContent(sourceImg, url, width, height);

            // create frosted layer
            StackPane frostView = createFrostedLayer(sourceImg, y, width, height);
            frostView.setAlignment(Pos.BOTTOM_CENTER);

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.BOTTOM_CENTER);


            Button urlButton = createButtonFromPath("M51.173,3.801c-5.068-5.068-13.315-5.066-18.384,0l-9.192,9.192c-0.781,0.781-0.781,2.047,0,2.828\n" +
                    "\t\tc0.781,0.781,2.047,0.781,2.828,0l9.192-9.192c1.691-1.69,3.951-2.622,6.363-2.622c2.413,0,4.673,0.932,6.364,2.623\n" +
                    "\t\ts2.623,3.951,2.623,6.364c0,2.412-0.932,4.672-2.623,6.363L36.325,31.379c-3.51,3.508-9.219,3.508-12.729,0\n" +
                    "\t\tc-0.781-0.781-2.047-0.781-2.828,0s-0.781,2.048,0,2.828c2.534,2.534,5.863,3.801,9.192,3.801s6.658-1.267,9.192-3.801\n" +
                    "\t\tl12.021-12.021c2.447-2.446,3.795-5.711,3.795-9.192C54.968,9.512,53.62,6.248,51.173,3.801z\n" +
                    "\t\tM27.132,40.57l-7.778,7.778c-1.691,1.691-3.951,2.623-6.364,2.623c-2.412,0-4.673-0.932-6.364-2.623\n" +
                    "\t\tc-3.509-3.509-3.509-9.219,0-12.728L17.94,24.306c1.691-1.69,3.951-2.622,6.364-2.622c2.412,0,4.672,0.932,6.363,2.622\n" +
                    "\t\tc0.781,0.781,2.047,0.781,2.828,0s0.781-2.047,0-2.828c-5.067-5.067-13.314-5.068-18.384,0L3.797,32.793\n" +
                    "\t\tc-2.446,2.446-3.794,5.711-3.794,9.192c0,3.48,1.348,6.745,3.795,9.191c2.446,2.447,5.711,3.795,9.191,3.795\n" +
                    "\t\tc3.481,0,6.746-1.348,9.192-3.795l7.778-7.778c0.781-0.781,0.781-2.047,0-2.828S27.913,39.789,27.132,40.57z");

            Button downloadButton = createButtonFromPath("M39.914,0H0.5v49h48V8.586L39.914,0z M10.5,2h26v16h-26V2z M39.5,47h-31V26h31V47zM13.5,32h7c0.553,0,1-0.447,1-1s-0.447-1-1-1h-7c-0.553,0-1,0.447-1,1S12.947,32,13.5,32zM13.5,36h10c0.553,0,1-0.447,1-1s-0.447-1-1-1h-10c-0.553,0-1,0.447-1,1S12.947,36,13.5,36zM26.5,36c0.27,0,0.52-0.11,0.71-0.29c0.18-0.19,0.29-0.45,0.29-0.71s-0.11-0.521-0.29-0.71c-0.37-0.37-1.04-0.37-1.41,0c-0.19,0.189-0.3,0.439-0.3,0.71c0,0.27,0.109,0.52,0.29,0.71C25.979,35.89,26.229,36,26.5,36z");
            Button favButton = createButtonFromPath("M51.911,16.242C51.152,7.888,45.239,1.827,37.839,1.827c-4.93,0-9.444,2.653-11.984,6.905\n" +
                    "\tc-2.517-4.307-6.846-6.906-11.697-6.906c-7.399,0-13.313,6.061-14.071,14.415c-0.06,0.369-0.306,2.311,0.442,5.478\n" +
                    "\tc1.078,4.568,3.568,8.723,7.199,12.013l18.115,16.439l18.426-16.438c3.631-3.291,6.121-7.445,7.199-12.014\n" +
                    "\tC52.216,18.553,51.97,16.611,51.911,16.242z");


            hbox.getChildren().addAll(urlButton,downloadButton, favButton);



            frostView.getChildren().add(hbox);

            //frostView.setVisible(false);
            tile.getChildren().add(img);
            tile.getChildren().add(frostView);

            double upperSlideLimit = height > 150 ? height*0.75 : height*.5;
            addSlideHandlers(y, upperSlideLimit, height, frostView, tile);
            masonryTiles.add(tile);

        }

        masonryPane.getChildren().addAll(masonryTiles);
        Platform.runLater(() -> scrollPane.requestLayout());
        //JFXScrollPane.smoothScrolling(scrollPane);

    }

    private StackPane createTile(double width, double height) {
        StackPane tile = new StackPane();
        tile.setPrefWidth(width);
        tile.setPrefHeight(height);
        JFXDepthManager.setDepth(tile, 1);
        return tile;
    }

    private Button createButtonFromPath(String pathString){
        SVGPath path = new SVGPath();
        path.setContent(pathString);
        Bounds bounds = path.getBoundsInLocal();

        // scale to size 20x20 (max)
        double scaleFactor = 30 / Math.max(bounds.getWidth(), bounds.getHeight());
        path.setScaleX(scaleFactor);
        path.setScaleY(scaleFactor);
        path.getStyleClass().add("button-icon");

        Button button = new Button();
        button.setPickOnBounds(true); // make sure transparent parts of the button register clicks too
        button.setGraphic(path);
        button.setAlignment(Pos.CENTER);
        button.getStyleClass().add("icon-button");
        return button;

    }

    private ImageView createGifContent(Image sourceImg, String url, double width, double height) {
        ImageView img = new ImageView();
        img.setFitWidth(width);
        img.setFitHeight(height);
        img.setImage(sourceImg);
        img.setOnMouseClicked( event -> {
            animateClick(img);
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(url);
            clipboard.setContent(content);
        });
        return img;
    }

    private StackPane createFrostedLayer(Image sourceImg, DoubleProperty y, double width, double height) {

            Image frostImage = sourceImg;
            ImageView frost = new ImageView(frostImage);
            Rectangle filler = new Rectangle(0,0,width,height);
            filler.setFill(Color.WHITESMOKE);
            Pane frostPane = new Pane(frost);
            frostPane.setEffect(frostEffect);
            StackPane frostView = new StackPane( filler, frostPane);
            Rectangle clipShape = new Rectangle( 5, y.get(), width, height);
            frostView.setClip(clipShape);
            clipShape.yProperty().bind(y);
            return frostView;

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

    private void addSlideHandlers(DoubleProperty y, double UPPER_SLIDE_POSITION, double height, Node frostedContent, Node container) {

        Timeline slideIn = new Timeline(
          new KeyFrame(
                  SLIDE_DURATION, new KeyValue(y,UPPER_SLIDE_POSITION)
          )
        );

        slideIn.setOnFinished(e -> frostedContent.setVisible(true));

        Timeline slideOut = new Timeline(
          new KeyFrame(
                  SLIDE_DURATION, new KeyValue(y, height)
          )
        );



        container.setOnMouseEntered(e-> {
            frostedContent.setVisible(true);
            slideOut.stop();
            slideIn.play();

        });

        container.setOnMouseExited( e-> {
            slideIn.stop();
            slideOut.play();
            slideOut.setOnFinished(e2->{
                frostedContent.setVisible(false);
            });
        });
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
