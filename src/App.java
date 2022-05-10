import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.application.Application;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javafx.scene.input.MouseButton;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;

import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;

public class App extends Application {

    final static double LONG_MIN = -75.58294880060018;
    final static double LONG_MAX = -75.51808951219382;
    final static double LAT_MIN = 39.73523697352253;
    final static double LAT_MAX = 39.770128897728945;

    static Group root;
    static Pane map;
    static ArrayList<Pair> roadLines = new ArrayList<>();
    static double prevX = 0;
    static double prevY = 0;
    static double scaleX = 1;
    static double scaleY = 1;
    static Scale scale = new Scale(scaleX, scaleY);
    static boolean started = false;
    static int originalSize = 0;

    //static HashMap<String, Line> lines = new HashMap<>();

    public static void main(String...args) throws FileNotFoundException {
        
        Wilmington.wilmington();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception { 

        root = new Group();   

        // Line road = new Line(20, 300, 50, 100);
        // road.setStartX(20);
        // road.setEndX(300);
        // road.setStartY(50);
        // road.setEndY(100);
        
        // road.setStroke(Color.WHITE);
        // road.setStrokeWidth(15);

        map = new Pane();
        map.setPrefSize(2000, 1000);
        map.getTransforms().add(scale);

        root.getChildren().add(map);
        this.drawStreets(root);
    
        Scene scene = new Scene(root, 600, 600);
        scene.setFill(Color.LIGHTGREEN);

        stage.setTitle("Wilmington Map"); 
        stage.setScene(scene);

        GridPane grid = new GridPane();
        grid.setMinSize(0, 0);

        TextField start = new TextField();
        TextField destination = new TextField();
        start.setPrefSize(400, 50);
        start.setEditable(false);
        destination.setEditable(false);
        destination.setPrefSize(400, 50);

        Rectangle rect = new Rectangle();
        rect.setFill(Color.WHITE);
        grid.setBackground(Background.fill(Color.WHITE));

        Text directions = new Text();
        directions.setX(500);
        directions.setY(500);
        directions.setFont(new Font("", 20));
        directions.setOpacity(100);
        root.getChildren().add(directions);
        
        Button go = new Button();
        go.setText("Go");
        go.setPrefSize(100, 50);
        go.setTextFill(Color.BLACK);
        go.setBackground(Background.fill(Color.GREEN));

        grid.add(start, 0, 0); 
        grid.add(destination, 0, 1);
        grid.add(go, 1, 0);
        grid.add(directions, 0, 2);
        root.getChildren().add(grid);
        
        go.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent m) {
                
                try {
                    String route = Wilmington.findRoute(start.getText(), destination.getText());
                    directions.setText(route);
                } catch (FileNotFoundException e) {
                    
                    e.printStackTrace();
                }
                
            }

        });

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){

            @Override
            public void handle(MouseEvent m) {
        
                prevX = m.getX();
                prevY = m.getY();
                if(m.getButton() == MouseButton.SECONDARY){
                    
                    if(!started) {
                        start.setText(convert(m.getX(), m.getY()).toString());
                        started = true;
                    }
                    else destination.setText(convert(m.getX(), m.getY()).toString());
                    
                }
                if(m.getButton() == MouseButton.MIDDLE){
                    started = false;
                    start.setText("");
                    destination.setText("");
                    directions.setText("");
                    for(int i = originalSize; i < roadLines.size(); i++){
                        map.getChildren().remove(roadLines.get(i).line);
                    }
                }
            }

        });

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            
            int translateX = 0;
            int translateY = 0;
            

            @Override
            public void handle(MouseEvent e) {
                
                translateX += e.getX() - prevX;
                translateY += e.getY() - prevY;
                map.setTranslateX(translateX);
                map.setTranslateY(translateY);
                prevX = e.getX();
                prevY = e.getY();
                
            }
            
        });

        scene.setOnScroll(new EventHandler<ScrollEvent>() {
            
            @Override 
            public void handle(ScrollEvent e) {
                if(e.getDeltaY() > 0){
                    scaleX += .1;
                    scaleY += .1;
                    
                }
                else if(e.getDeltaY() < 0 && scaleX > .7) {
                    scaleX -= .1;
                    scaleY -= .1;
                    
                }
                scale.setX(scaleX);
                scale.setY(scaleY);
                
            }
        });

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
            text.setX((road.getStartX() + road.getEndX()) / 2 - 40);
            text.setY((road.getStartY() + road.getEndY()) / 2);
            text.setRotate(getAngle(s));
            //System.out.println(text.getRotate());
            Text text2 = new Text(s.getName());
            text2.setX(road.getEndX());
            text2.setY(road.getEndY());
            text2.setRotate(getAngle(s));
            road.setStrokeWidth(2);
            map.getChildren().addAll(road, text);
            roadLines.add(new Pair(road, text));
            // if(s.direction() != 0) {
            //     Text t = new Text("-1");
            //     t.setX(road.getEndX());
            //     t.setY(road.getEndY());
            //     Text d = new Text("1");
            //     d.setX(road.getStartX());
            //     d.setY(road.getStartY());
            //     map.getChildren().addAll(t, d);
            // }
            // streetLines.put(s.getName(), road);
            originalSize++;
            
        }
    }

    private double getAngle(Street s) {

        return - Math.atan(1 / s.getSlope()) * 180 / Math.PI;
    }

    public static void highLight(Intersection i1, Intersection i2, int x){

        Line street = new Line();
        
        street.setStartX((i1.getLocation().y - LONG_MIN) / (LONG_MAX - (LONG_MIN))  * 2000);
        street.setEndX((i2.getLocation().y - LONG_MIN) / (LONG_MAX - (LONG_MIN))  * 2000);
        street.setStartY((LAT_MAX - i1.getLocation().x) / (LAT_MAX - LAT_MIN) * 1000); 
        street.setEndY((LAT_MAX - i2.getLocation().x) / (LAT_MAX - LAT_MIN) * 1000);
        street.setStroke(Color.BLUE);
        street.setStrokeWidth(3);

        map.getChildren().add(street);
        roadLines.add(new Pair(street));
    }

    static double convertToX(double longitude){
        return (longitude - LONG_MIN) / (LONG_MAX - (LONG_MIN)) * 2000;
    }

    static double convertToY(double latitude){
        return (LAT_MAX - latitude) / (LAT_MAX - LAT_MIN) * 1000;
    }

    static Coordinate convert(double x, double y){
        
        x -= map.getTranslateX();
        y -= map.getTranslateY();
        double latiutude = (LAT_MAX - y * (LAT_MAX - LAT_MIN) / (1000 * scaleY));
        double longitude = (x * (LONG_MAX - LONG_MIN) / (2000 * scaleX)) + LONG_MIN;
        return new Coordinate(latiutude, longitude);
    }
}

class Pair{

    Line line;
    Text text;

    Pair(Line l, Text t){
        this.line = l;
        this.text = t;
    }
    Pair(Line l){
        this.line = l;
    }
}