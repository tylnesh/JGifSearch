# JGifSearch

Simple Tenor gif search utility written in Java, with gui in JavaFX

## Features:
 - Simple JavaFX UI
 - Nice masonry tile layout using [JFoenix](https://github.com/sshahine/JFoenix)
 - Shows of current featured gifs on launch
 - Clicking on a gif copies its link to the clipboard
 - Dependencies pulled in using Maven
 
## Needs work:
 - More fancy design
 - Packaging it up in a runnable .jar or other format
 - Implementing the rest of the [Tenor Gif API](https://developers.google.com/tenor/guides/quickstart)
 
## How to run:
 - Currently it's easiest to run straight from IntelliJ IDEA
 - You need to implement your own ApiKey Class with properties API_KEY and CLIENT_KEY. You can get your own API_KEY [here](https://developers.google.com/tenor/guides/quickstart). The client_key is up to you and serves to distinguish between multiple apps by the same dev, that use the same API key.
 
 
