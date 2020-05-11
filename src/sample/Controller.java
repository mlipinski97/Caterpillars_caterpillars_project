package sample;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

public class Controller implements Initializable {

    @FXML
    Pane pane;
    @FXML
    GridPane panel;
    @FXML
    Slider slider_4;
    @FXML
    Slider slider_2;
    @FXML
    Slider slider_3;

    volatile private static Rectangle[][] grid_map = new Rectangle[11][18]; //all rectangles
    List<List<Rectangle>> wormPaths = new ArrayList<>();

    static CollisionSector sectionX;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        draw_map(panel);
        sectionX = new CollisionSector(grid_map[5][10]);

        wormPaths.add(createPath(0,0, 5, 10));
        wormPaths.add(createPath(5,0, 10, 10));
        wormPaths.add(createPath(2,10, 8, 17));

        CollisionSector sectionEtry1End2 = new CollisionSector(grid_map[5][0]);
        CollisionSector sectionEntry3End1 = new CollisionSector(grid_map[2][10]);
        CollisionSector sectionEtry2End3 = new CollisionSector(grid_map[8][10]);

        Semaphore wholeSectionSemaphore = new Semaphore(2);

        Platform.runLater(()->{
        Worm worm_1 = new Worm(wormPaths.get(0), Color.RED, 6, sectionEtry1End2, sectionEntry3End1, wholeSectionSemaphore, slider_4,3);
        Worm worm_2 = new Worm(wormPaths.get(1), Color.BLUE, 9, sectionEtry2End3, sectionEtry1End2, wholeSectionSemaphore, slider_2, 4);
        Worm worm_3 = new Worm(wormPaths.get(2), Color.GREEN,4, sectionEntry3End1, sectionEtry2End3, wholeSectionSemaphore, slider_3, 7);

        worm_1.setDaemon(true);
        worm_1.start();
        worm_2.setDaemon(true);
        worm_2.start();
        worm_3.setDaemon(true);
        worm_3.start();
        });
    }

    void draw_map(GridPane panel) {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 18; j++) {
                grid_map[i][j] = new Rectangle(109, 44);
                grid_map[i][j].setFill(Color.WHITE);
                grid_map[i][j].setStroke(Color.WHITE);
                grid_map[i][j].setStrokeType(StrokeType.INSIDE);
                panel.add(grid_map[i][j], i, j);
            }
        }
        colorWormPaths();
    }

    private void colorWormPaths(){
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 18; j++) {
                if((j<10 && (i == 0 || i == 5 || i == 10)) || (j == 0 || j == 10) || (j>9 && (i == 2 || i == 8)) || (j == 17 && (i>1 && i<9)) ){
                    grid_map[i][j].setStroke(Color.BLACK);
                }
            }
        }
    }


    private List<Rectangle> createPath(int ax, int ay, int bx, int by){
        List<Rectangle> returnList = new ArrayList<>();
        for (int i = ax; i <= bx; i++) {
            returnList.add(grid_map[i][ay]);
        }

        for (int i = ay+1; i <= by-1; i++) {
            returnList.add(grid_map[bx][i]);
        }

        for (int i = bx; i >= ax ; i--) {
            returnList.add(grid_map[i][by]);
        }

        for (int i = by-1; i >= ay+1 ; i--) {
            returnList.add(grid_map[ax][i]);
        }
        return returnList;
    }

}