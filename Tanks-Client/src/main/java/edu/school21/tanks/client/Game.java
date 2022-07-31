package edu.school21.tanks.client;

import edu.school21.tanks.client.communication.Connection;
import edu.school21.tanks.client.gameobjects.Projectile;
import edu.school21.tanks.client.gameobjects.Tank;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;

public class Game extends Application {
    public static int RESOLUTION_WIDTH = 640;
    public static int RESOLUTION_HEIGHT = 480;
    public final static int PROJECTILES_DAMAGE = 5;
    public static ImageView board;
    public static Tank player;
    public static Tank enemy;
    public static Projectile playerBullet;
    public static Projectile enemyBullet;
    public static ImageView healsBarPlayer;
    public static ImageView healsBarBorderPlayer;
    public static ImageView healsBarEnemy;
    public static ImageView healsBarBorderEnemy;
    public static ImageView explosion;

    public static double HEALS_BAR_WIDTH = 100;
    public static double HEALS_BAR_HEIGHT = 10;

    public static boolean gameStatus;



    static {
        Image healsBarBorderImage = new Image(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/border.png").toURI().toString());
        Image healsBarImage = new Image(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/life.png").toURI().toString());
        healsBarPlayer = new ImageView(healsBarImage);
        healsBarPlayer.setFitHeight(HEALS_BAR_HEIGHT);
        healsBarPlayer.setFitWidth(HEALS_BAR_WIDTH);
        healsBarPlayer.setLayoutY(RESOLUTION_HEIGHT - HEALS_BAR_HEIGHT);
        healsBarBorderPlayer = new ImageView(healsBarBorderImage);
        healsBarBorderPlayer.setFitHeight(HEALS_BAR_HEIGHT);
        healsBarBorderPlayer.setFitWidth(HEALS_BAR_WIDTH);
        healsBarBorderPlayer.setLayoutY(RESOLUTION_HEIGHT - HEALS_BAR_HEIGHT);
        healsBarEnemy = new ImageView(healsBarImage);
        healsBarEnemy.setFitHeight(HEALS_BAR_HEIGHT);
        healsBarEnemy.setFitWidth(HEALS_BAR_WIDTH);
        healsBarEnemy.setLayoutX(RESOLUTION_WIDTH - HEALS_BAR_WIDTH);
        healsBarBorderEnemy = new ImageView(healsBarBorderImage);
        healsBarBorderEnemy.setFitHeight(HEALS_BAR_HEIGHT);
        healsBarBorderEnemy.setFitWidth(HEALS_BAR_WIDTH);
        healsBarBorderEnemy.setLayoutX(RESOLUTION_WIDTH - HEALS_BAR_WIDTH);


        gameStatus = false;
        Image boardImage = new Image(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/field.png").toURI().toString());
        board = new ImageView(boardImage);

        player = new Tank(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/player.png").toURI().toString());
        enemy = new Tank(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/enemy.png").toURI().toString());
        playerBullet = new Projectile(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/enemyBullet.png").toURI().toString());
        enemyBullet = new Projectile(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/playerBullet.png").toURI().toString());
        Image explosionImage = new Image(new File("/Users/dquordle/JavaPiscine/Rush01/MIsha/client/src/main/resources/images/fail.png").toURI().toString());
        explosion = new ImageView(explosionImage);


        explosion.setLayoutX((RESOLUTION_WIDTH - explosionImage.getWidth()) / 2);
        explosion.setLayoutY((RESOLUTION_HEIGHT - explosionImage.getHeight()) / 2);
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Super Tank Battles 47000");
        GridPane mainMenuPane = new GridPane();
        mainMenuPane.setAlignment(Pos.CENTER);
        mainMenuPane.add(new Label("Add server address:"), 0, 0);
        TextField serverAddress = new TextField();
        mainMenuPane.add(serverAddress, 1, 0);
        Button joinServerButton = new Button("Join Server");
        mainMenuPane.add(joinServerButton, 2, 0);
        Scene mainMenuScene = new Scene(mainMenuPane, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
        stage.setTitle("Super Tank Battles 47000");
        stage.setScene(mainMenuScene);
        stage.show();
        Connection connection = new Connection();
        joinServerButton.setOnAction(e -> {
            if (connection.setConnection(serverAddress.getText())) {
                gameStatus = true;
            }
        });

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if(gameStatus == true) {
                    Pane gameMap = new Pane();
                    gameMap.getChildren().add(board);
                    try {
                        gameStatus = fillPane(connection.getMap(), gameMap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Scene gameScene = new Scene(gameMap, RESOLUTION_WIDTH, RESOLUTION_HEIGHT);
                    gameScene.setOnKeyPressed(keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.RIGHT
                                && player.getLayoutX() < RESOLUTION_WIDTH - player.getFitWidth()) {
                            try {
                                connection.sendMoveRight();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (keyEvent.getCode() == KeyCode.LEFT
                                && player.getLayoutX() > player.getFitWidth()) {
                            try {
                                connection.sendMoveLeft();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    gameScene.setOnKeyReleased(keyEvent -> {
                        if (keyEvent.getCode() == KeyCode.SPACE) {
                            try {
                                connection.sendShoot();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    stage.setScene(gameScene);
                    if (gameStatus == false) {
                        this.stop();
                        int [] gameResults = connection.getGameResults();
                        GridPane endGamePane = new GridPane();
                        endGamePane.setAlignment(Pos.CENTER);
                        endGamePane.add(new Label("Shots:"), 0, 0);
                        TextField shotsResults = new TextField();
                        shotsResults.setEditable(false);
                        shotsResults.setDisable(false);
                        shotsResults.setText(String.valueOf(gameResults[0]));
                        endGamePane.add(shotsResults, 1, 0);
                        endGamePane.add(new Label("Hits:"), 0, 1);
                        TextField hitsResults = new TextField();
                        hitsResults.setEditable(false);
                        hitsResults.setDisable(false);
                        hitsResults.setText(String.valueOf(gameResults[1]));
                        endGamePane.add(hitsResults, 1, 1);
                        endGamePane.add(new Label("Misses:"), 0, 2);
                        TextField missesResults = new TextField();
                        missesResults.setEditable(false);
                        missesResults.setDisable(false);
                        missesResults.setText(String.valueOf(gameResults[0] - gameResults[1]));
                        endGamePane.add(missesResults, 1, 2);
                        if (gameResults[2] == 1) {
                            endGamePane.add(new Label("YOU WIN"), 0, 3);
                        } else {
                            endGamePane.add(new Label("YOU LOST"), 1, 3);
                        }
                        stage.setScene(new Scene(endGamePane, RESOLUTION_WIDTH, RESOLUTION_HEIGHT));
                        stage.show();
                    }
                }
            }
        }.start();
    }

    public static void main(String[] args) {
        launch();
    }

    private static boolean fillPane(byte [][] map, Pane gameMap) {
        boolean continueGame = true;
        for (int y = 0; y < map.length; ++y) {
            for (int x = 0; x < map[0].length; ++x) {
                switch (map[y][x]) {
                    case 'U':
                        player.setXPosition(x);
                        player.setYPosition(y);
                        gameMap.getChildren().add(player);
                        break;
                    case 'E':
                        enemy.setXPosition(x);
                        enemy.setYPosition(y);
                        gameMap.getChildren().add(enemy);
                        break;
                    case 'T':
                        Projectile currPlayerBullet = playerBullet.clone();
                        currPlayerBullet.setXPosition(x);
                        currPlayerBullet.setYPosition(y);
                        gameMap.getChildren().add(currPlayerBullet);
                        break;
                    case 'B':
                        Projectile currEnemyBullet = enemyBullet.clone();
                        currEnemyBullet.setXPosition(x);
                        currEnemyBullet.setYPosition(y);
                        gameMap.getChildren().add(currEnemyBullet);
                        break;
                    case 'u':
                        player.getDamage(PROJECTILES_DAMAGE);
                        break;
                    case 'e':
                        enemy.getDamage(PROJECTILES_DAMAGE);
                        break;
                    case 'L':
                        continueGame = false;
                        break;
                }
            }
        }

        if (continueGame) {
            healsBarPlayer.setFitWidth(HEALS_BAR_WIDTH * player.getHeals() / 100);
            gameMap.getChildren().add(healsBarPlayer);
            gameMap.getChildren().add(healsBarBorderPlayer);

            healsBarEnemy.setFitWidth(HEALS_BAR_WIDTH * enemy.getHeals() / 100);
            gameMap.getChildren().add(healsBarEnemy);
            gameMap.getChildren().add(healsBarBorderEnemy);
        } else {
            gameMap.getChildren().add(explosion);
        }
        return continueGame;
    }
}