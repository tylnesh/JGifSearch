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
 
## Screenshots

### Featured GIFs
![Featured gifs](https://user-images.githubusercontent.com/6696161/177363501-ad17046a-a286-491c-a24b-179a7ea97a41.png)
### Searching for Hello world!
![Hello World](https://user-images.githubusercontent.com/6696161/177363816-4d337dcf-77a5-4982-af94-6106bc06120d.png)
### Searching for Happy Go Lucky, loading more gifs, copying the URL of some of them to system clipboard
[Searching, loading more, copying URL](https://user-images.githubusercontent.com/6696161/177364692-66091abc-5cdb-4036-a5b3-bb944bab714f.webm)
### Resizing window
[Resizing Window](https://user-images.githubusercontent.com/6696161/177365172-16975c32-526c-4e07-9b7a-79c25c468022.webm)


