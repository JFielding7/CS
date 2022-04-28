import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.Group;

public class App extends Application {

    final static double X_MIN = -75.58294880060018;
    final static double X_MAX = -75.51808951219382;
    final static double Y_MIN = 39.73523697352253;
    final static double Y_MAX = 39.770128897728945;

    static Group root;

    static HashMap<String, Line> streetLines = new HashMap<>();

    public static void main(String...args) throws FileNotFoundException {
        
        Wilmington.wilmington();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        Text text = new Text();
        text.setText("Hello");
        text.setX(50); 
        text.setY(50); 

        root = new Group();   

        Line road = new Line(20, 300, 50, 100);
        road.setStartX(20);
        road.setEndX(300);
        road.setStartY(50);
        road.setEndY(100);
        
        road.setStroke(Color.WHITE);
        road.setStrokeWidth(15);

        this.drawStreets(root);
                
        Scene scene = new Scene(root, 600, 600);  
        scene.setFill(Color.GREEN);

        stage.setTitle("Wilmington Map"); 
        stage.setScene(scene);

        GridPane g = new GridPane();
        g.setMinSize(500, 500);

        TextField start = new TextField();
        
        TextField destination = new TextField();
        
        Button go = new Button();
        go.setText("Go");
        
        go.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent m) {

                try {
                    Wilmington.findRoute(start.getText(), destination.getText());
                } catch (FileNotFoundException e) {
                    
                    e.printStackTrace();
                }
                
            }

        });

        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent m) {
                m.getX();
            }

        });

        g.add(start, 0, 0); 
        g.add(destination, 0, 1);
        g.add(go, 1, 0);
        root.getChildren().add(g);

        stage.show();
        
    }

    //39.750172542038634, -75.58294880060018  39.7488613843985, -75.51808951219382
//39.73523697352253, -75.55154939489482  39.770128897728945, -75.547264256135
    public void drawStreets(Group g){
        
        for(Street s : Wilmington.streets){
            
            Line road = new Line();
            road.setStartX(convertToX(s.getCoord1().y));
            road.setEndX(convertToX(s.getCoord2().y));
            road.setStartY(convertToY(s.getCoord1().x)); 
            road.setEndY(convertToY(s.getCoord2().x));
            road.setStroke(Color.WHITE);
            Text text = new Text(s.getName());
            text.setX(road.getStartX());
            text.setY(road.getStartY());
            Text text2 = new Text(s.getName());
            text2.setX(road.getEndX());
            text2.setY(road.getEndY());
            road.setStrokeWidth(2);
            g.getChildren().addAll(road, text, text2);
            streetLines.put(s.getName(), road);
            
        }
    }

    public static void highLight(Intersection i1, Intersection i2){

        Line street = new Line();
        street.setStartX((i1.getLocation().y - X_MIN) / (X_MAX - (X_MIN))  * 2000);
        street.setEndX((i2.getLocation().y - X_MIN) / (X_MAX - (X_MIN))  * 2000);
        street.setStartY((Y_MAX - i1.getLocation().x) / (Y_MAX - Y_MIN) * 1000); 
        street.setEndY((Y_MAX - i2.getLocation().x) / (Y_MAX - Y_MIN) * 1000);
        street.setStroke(Color.BLUE);
        street.setStrokeWidth(3);
        root.getChildren().add(street);
    }

    static double convertToX(double longitude){
        return (longitude - X_MIN) / (X_MAX - (X_MIN)) * 2000;
    }

    static double convertToY(double latitude){
        return (Y_MAX - latitude) / (Y_MAX - Y_MIN) * 1000;
    }
}