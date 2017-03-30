package com.jp.client;

import com.jp.core.AddressedCircle;
import com.jp.core.PermutationService;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by JP on 3/10/2017.
 */
public class View extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(View.class);

    private static final int GRID_HEIGHT = 9 * 4;
    private static final int GRID_WIDTH = 21 * 4;

    private static final int NODE_DIMENSION = 10;
    private static final int SCENE_HEIGHT = GRID_HEIGHT * NODE_DIMENSION;
    private static final int SCENE_WIDTH = GRID_WIDTH * NODE_DIMENSION;
    private static final int CITIES = 7;
    private static final int CITY_RADIUS = 10;

    private static Random rand = new Random();
    private static Group root = new Group();
    private static double shortestDistanceFound = Double.MAX_VALUE;
    private static ForkJoinPool fjp = new ForkJoinPool();
    private static AddressedCircle[] cities = new AddressedCircle[CITIES];
    private static Circle[] bestRoute = new Circle[CITIES];
    private static PermutationService permutationService;
    private static List<Line> lineRoute;

    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        addPoints();

        permutationService = new PermutationService(fjp, cities);

        launch(args);
    }

    public static double calculateDistance(Circle a, Circle b) {
        // Pythagorean theroem
        double xDist = Math.abs(a.getCenterX() - b.getCenterX());
        double yDist = Math.abs(b.getCenterY() - b.getCenterY());

        return Math.sqrt(xDist * xDist + yDist * yDist);

    }

    private static void generateLineRoute(Circle[] permutation, Color color) {

        for (int i = 0; i < permutation.length - 1; i++) {

            Line l = lineRoute.get(i);
            l.setStroke(color);
            l.setStartX(permutation[i].getCenterX());
            l.setStartY(permutation[i].getCenterY());
            l.setEndX(permutation[i + 1].getCenterX());
            l.setEndY(permutation[i + 1].getCenterY());
        }

    }

    /**
     * Creates cities
     */
    private static void addPoints() {
        for (int i = 0; i < CITIES; i++) {
            AddressedCircle city = new AddressedCircle(rand.nextInt(SCENE_WIDTH), rand.nextInt(SCENE_HEIGHT), CITY_RADIUS, Color.AQUAMARINE);
            cities[i] = city;
            root.getChildren().add(city);

            Text t = new Text();
            t.setFill(Color.WHITE);
            t.setX(city.getCenterX() + CITY_RADIUS);
            t.setY(city.getCenterY() - CITY_RADIUS);
            t.setText(city.getCenterX() + ", " + city.getCenterY());
            root.getChildren().add(t);
        }
        bestRoute = cities;
        lineRoute = Stream.generate(Line::new).limit(cities.length).collect(Collectors.toList());
        generateLineRoute(cities, Color.DARKGRAY);
        root.getChildren().addAll(lineRoute);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        LOGGER.info("main : " + Thread.currentThread().getName());
        int pCounter = 0;
        Circle[] currentPermutation = (Circle[]) permutationService.getNextPermutation();
        while (currentPermutation != null) {
            pCounter++;
            LOGGER.info("iteration : {}", pCounter);

            double cpDist = 0;
            for (int i = 0; i < currentPermutation.length - 1; i++) {
                // calculate distance between each circle
                cpDist += calculateDistance(currentPermutation[i], currentPermutation[i + 1]);
            }
            if (cpDist < shortestDistanceFound) {
                shortestDistanceFound = cpDist;
                LOGGER.info("Found current shortest distance: {}", shortestDistanceFound);
                LOGGER.info("best route : {}{}{}{}{} ", currentPermutation);
                bestRoute = currentPermutation.clone();

                generateLineRoute(bestRoute, Color.GREEN);

            }

            currentPermutation = (Circle[]) permutationService.getNextPermutation();
        }


        LOGGER.info("best route : {}{}{}{}{} ", bestRoute);
        LOGGER.info("shortest distance : {}", shortestDistanceFound);


        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.setTitle(this.getClass().getSimpleName());
        scene.setFill(Color.rgb(25, 25, 25));


        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();


        primaryStage.show();

    }

}
