public class Block{
  
    Street street;
    Intersection start;
    Intersection destination;
    double distance;

    public Block(Street street, Intersection start, Intersection destination){
        
        this.street = street;
        this.start = start;
        this.destination = destination;
        this.distance = calcDistance();
    }

    private double calcDistance(){

        Coordinate point1 = start.getLocation();
        Coordinate point2 = destination.getLocation();
        return Math.sqrt(Math.pow(69 * (point1.x - point2.x), 2) + Math.pow(54.6 * (point2.y - point2.y), 2));
    }

    @Override
    public String toString(){
        return this.street.getName() + " " + destination.getStreets()[0].getName() + " " + destination.getStreets()[1].getName();
    }

}
