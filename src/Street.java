import java.util.ArrayList;
import java.util.HashMap;

//streets represent the edges of the graph
public class Street {

    private String name;
    private int speedLimit;
    private ArrayList<Intersection> intersections;
    private Coordinate coord1;
    private Coordinate coord2;
    private Equation equ;
    private int oneWay;
    private HashMap<Coordinate, Integer> zIntervals; 

    public Street(String name, Coordinate coord1, Coordinate coord2, int oneWay, HashMap<Coordinate, Integer> zIntervals, int speedLimit){
        this.name = name;
        this.coord1 = coord1;
        this.coord2 = coord2;
        this.equ = new Equation(this.getSlope(), coord1);
        this.intersections = new ArrayList<>();
        this.oneWay = oneWay;
        this.zIntervals = zIntervals;
        this.speedLimit = speedLimit;
    }

    public Street(String name){
        this.name = name;
    }

    public HashMap<Coordinate, Integer> getzIntervals() {

        return zIntervals;
    }

    public int zPosition(double latitude){
        for(Coordinate c : zIntervals.keySet()){
            if(latitude >= c.x && latitude <= c.y){
                return zIntervals.get(c);
            }
        }
        return 0;
    }

    public int direction(){

        return this.oneWay;
    }

    public Equation getEqu() {
        return equ;
    }

    public void setEqu(Equation equ) {
        this.equ = equ;
    }

    public Coordinate getCoord1() {
        return coord1;
    }

    public Coordinate getCoord2() {
        return coord2;
    }

    public double getSlope(){

        return (coord1.y - coord2.y) / (coord1.x - coord2.x);
    }

    public double xLowerBound(){
        return this.coord1.x > this.coord2.x ? coord2.x : coord1.x;
    }

    public double yLowerBound(){
        return this.coord1.y > this.coord2.y ? coord2.y : coord1.y;
    }

    public double xUpperBound(){
        return this.coord1.x > this.coord2.x ? coord1.x : coord2.x;
    }

    public double yUpperBound(){
        return this.coord1.y > this.coord2.y ? coord1.y : coord2.y;
    }

    public Coordinate maxLongCoord(){
        return this.coord1.y > this.coord2.y ? coord1 : coord2; 
    }

    public Coordinate minLongCoord(){
        return this.coord1.y < this.coord2.y ? coord1 : coord2; 
    }

    public ArrayList<Intersection> getIntersections() {
        return intersections;
    }
/*
    public void setIntersections(ArrayList<Intersection> inter) {
        this.intersections = new Intersection[inter.size()];
        for(int i = 0; i < inter.size(); i++){
            intersections[i] = inter.get(i);
        }
        sortIntersections(intersections, 0, intersections.length - 1);
    }
*/

    public void addIntersection(Intersection inter) {
        this.intersections.add(inter);
    }

    //sorts all the intersections that are connect to this street by their longitude (same as x) positions
    //uses merge sort, runs in O(nlogn)
    void sortIntersections(int start, int end){
        //System.out.println(start + " " + end);
        if(start == end) return;  
        
        sortIntersections(start, (start + end) / 2);
        sortIntersections((start + end) / 2 + 1, end);
        merge(start, end);
    }
    
    //used for merging the sorted parts of the arraylist into each other utnil fully sorted
    void merge(int start, int end){
        Intersection[] subArr = new Intersection[end - start + 1];
        for(int i = start; i <= end; i++)
            subArr[i - start] = this.intersections.get(i);
        int mid = (start + end) / 2 + 1;
        int i1 = 0;
        int i2 = mid - start;
        for(int i = start; i <= end; i++){
            if(i1 < (mid - start) && (i2 >= subArr.length || subArr[i1].getLocation().x <= subArr[i2].getLocation().x)) {
                intersections.set(i, subArr[i1]);
                i1++;
            }
            else {
                intersections.set(i, subArr[i2]);
                i2++;
            }
        }
    }

    public Intersection nextIntersection(Intersection prev) {

        int index = intersections.indexOf(prev) + 1;
        if(index < intersections.size() && oneWay != -1) return intersections.get(index);
        return null;
    }

    public Intersection prevIntersection(Intersection curr) {

        int index = intersections.indexOf(curr) - 1;
        if(index > -1 && oneWay != 1) return intersections.get(index);
        return null;
    }

    public String getName(){
        return this.name;
    }

    public int getSpeed(){
        return this.speedLimit;
    }

    public String toString(){
        return this.getName();
    }

    public boolean same(Street s) {
        
        return (this == s) || this.name.equals(s.name) && 
        (Wilmington.calcDistance(this.maxLongCoord(), s.minLongCoord()) < .0038 
        || Wilmington.calcDistance(this.minLongCoord(), s.maxLongCoord()) < .0038);
    }

}