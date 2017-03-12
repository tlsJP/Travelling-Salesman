package com.jp.client;

import com.jp.core.AddressedCircle;
import com.jp.core.PermutationService;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;

/**
 * Created by JP on 3/10/2017.
 */
public class View extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(View.class);

    private static final int GRID_HEIGHT = 9 * 6;
    private static final int GRID_WIDTH = 21 * 6;

    private static final int NODE_DIMENSION = 12;
    private static final int SCENE_HEIGHT = GRID_HEIGHT * NODE_DIMENSION;
    private static final int SCENE_WIDTH = GRID_WIDTH * NODE_DIMENSION;
    private static final int CITIES = 12;
    private static final int CITY_RADIUS = 10;

    private static Random rand = new Random();
    private Group root = new Group();
    private ForkJoinPool fjp = new ForkJoinPool();
    private AddressedCircle[] cities = new AddressedCircle[CITIES];

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void addPoints() {
        for (int i = 0; i < CITIES; i++) {
            AddressedCircle city = new AddressedCircle(rand.nextInt(SCENE_WIDTH), rand.nextInt(SCENE_HEIGHT), CITY_RADIUS, Color.GREEN);
            cities[i] = city;
            root.getChildren().add(city);

        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Scene scene = new Scene(root, SCENE_WIDTH, SCENE_HEIGHT, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.setTitle(this.getClass().getSimpleName());
        scene.setFill(Color.BLACK);
        primaryStage.show();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        addPoints();

        PermutationService p = new PermutationService(fjp);
        BlockingQueue permutations = (BlockingQueue) p.permute(cities);


        fjp.submit(() -> {
            try {
                int i=0;
                Object[] pp;
                while (( pp =(Object[])permutations.take()) != null) {
                    LOGGER.info("{}{}{}", pp);
                    // Only update every 9th permutation, otherwise the updates happen so fast the display seems to bug out and do nothing
                    if(i%9==0){
                        ((AddressedCircle)pp[1]).setFill(Color.rgb(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
                    }

//                    try {
//                        // Not putting this sleep here bugs out the display output apparently because the cpu is going too fast?
//                        Thread.sleep(1);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        });
    }

}
