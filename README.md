# A Simple Number Guessing Game using JAVA Agent DEvelopment Framework (JADE)

## Build

### Using `javac`

First, download the `jade.jar` file from [here](https://jade.tilab.com/download/jade/). There are 5 different options for download. You'll only need the `jade.jar` file for this application, so you'll be fine with just downloading `jadeBin`.

Then add the `jade.jar` to your `$CLASSPATH` variable. On Linux:

```
export CLASSPATH=.:/path/to/your/jade.jar/file
```

For permanently add this to your `$CLASSPATH`, put the above in your `.bashrc` file and then restart your bash.

Clone this project and go to the root folder. Compile the code using the following command:

```
javac -d . *.java
```

### Using `ant`

```bash
ant && cd bin
```

<br/>

## Run

```
java jade.Boot -agents "controller:mkf.jade.guessinggame.CreatePlayers(3)"
```

In the above command, `3` is the number of players you wish to add to the game. You can change it to another number. There should be at least 2 players for the game to start. So, anything above 2 would be fine.

### About the game

In the game, there is one `Host` and multiple `Player`s. The `Host` first selects a random number between 0 and `MAX_VALUE`-1 which you can change in the `Constants.java` file. The default is 1500. The players then each join the game and start guessing random numbers until one of them guesses correctly. The `Host` then will anounce the winner and send a message to all the players indicating that the game is over. At the end, `Host` and `Player`s will shut down themselves. The `CreatePlayers` class is responsible for both creating the `Player`s and the `Host`.

The project should be fine with JDK 1.8 and above.
