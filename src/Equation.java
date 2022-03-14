
public class Equation {

    Coordinate point;
    double slope;

    public Equation(double slope, Coordinate point){
        this.slope = slope;
        this.point = point;
    }

    public Coordinate intersection(Equation equ2){
        //Coordinate intersection = null;
        double x = (this.slope * this.point.x - equ2.slope * equ2.point.x + equ2.point.y - this.point.y) / (this.slope - equ2.slope);
        double y = this.value(x);
        return new Coordinate(x, y);
    }

    public double value(double x){

        return this.slope * (x - this.point.x) + this.point.y;
    }

}
