
public class Coordinate {
    double x;
    double y;

    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Coordinate(String x, String y){
        this.x = Double.parseDouble(x);
        this.y = Double.parseDouble(y);
    }

    public String toString(){
        return this.x + ", " + this.y;
    }
}
