import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;

public class Wilmington {

    private static final double LATITUDE50FT = 0.00013724198;
    private static final double LONGITUDE50FT = 0.000173437674;

    //private static Address[] places;
    private static ArrayList<Street> streets;
    private static ArrayList<Intersection> intersections;

    public static void wilmington() throws FileNotFoundException {
        
        streets = new ArrayList<>();
        intersections = new ArrayList<>();

        File street = new File("C:\\Users\\220700jf\\Documents\\BFS\\WilmingtonMap\\src\\WilmingtonStreets.txt");
        
        Scanner scan = new Scanner(street);

        while(scan.hasNext()){
            String[] road = scan.nextLine().split("  |, "); 
            int oneWay = road.length > 5 ? Integer.parseInt(road[5]) : 0;
            HashMap<Coordinate, Integer> zIntervals = new HashMap<>();
            for(int i = 6; i < road.length; i++){
                String interval = road[i];
                zIntervals.put(new Coordinate(interval.substring(interval.indexOf(":") + 1, interval.indexOf(",")), 
                interval.substring(interval.indexOf(",") + 1)), 
                Integer.parseInt(interval.substring(0, interval.indexOf(":"))));
            }
            streets.add(new Street(road[0], new Coordinate(road[1], road[2]), new Coordinate(road[3], road[4]), oneWay, zIntervals));
        }

        scan.close();

        findIntersections();
    }

    public static void findIntersections() {
        
        for(int i = 0; i < streets.size()-1; i++){
            Street s1 = streets.get(i);
            for(int j = i+1; j < streets.size(); j++){
                Street s2 = streets.get(j);
                Coordinate inter = s1.getEqu().intersection(s2.getEqu());
                if(doesInter(s1, s2, inter)){
                    Intersection inters = new Intersection(inter, s1, s2);
                    
                    s1.addIntersection(inters);
                    s2.addIntersection(inters);
                    intersections.add(inters);
                   
                }
            }
            if(s1.getIntersections().size() > 1) s1.sortIntersections(0, s1.getIntersections().size() - 1);
        }
        Street last = streets.get(streets.size() - 1);
        last.sortIntersections(0, last.getIntersections().size() - 1);
        for(Intersection i : intersections){
            i.compileBlocks();
        }
    }

    //finds the intersections between roads with and error of 50ft
    public static boolean doesInter(Street s1, Street s2, Coordinate inter){
        
        return s1.zPosition(inter.x) == s2.zPosition(inter.x) && (inter.x >= s1.xLowerBound() - LONGITUDE50FT && inter.x <= s1.xUpperBound() + LONGITUDE50FT 
        && inter.y >= s1.yLowerBound() - LATITUDE50FT && inter.y <= s1.yUpperBound() + LATITUDE50FT
        && inter.x >= s2.xLowerBound() - LONGITUDE50FT && inter.x <= s2.xUpperBound() + LONGITUDE50FT 
        && inter.y >= s2.yLowerBound() - LATITUDE50FT && inter.y <= s2.yUpperBound() + LATITUDE50FT);
    }
    
    //main method 
    //calls the wilmington method to set up the map
    //calls shortest path to find the route between 2 different locations
    public static void main(String...args) throws FileNotFoundException {
        
        wilmington();
        Street str = streets.get(14);
        System.out.println();
        // for(Intersection i : str.getIntersections()){
        //     System.out.println(i);
        //     System.out.println(i.getStreets()[0].zPosition(i.getLocation().x));
        //     System.out.println(i.getStreets()[1].zPosition(i.getLocation().x));
        // }
        
        // System.exit(0);
        Intersection i1 = find(streets.get(0), streets.get(3));
        Street[] start = i1.getStreets();
        System.out.println("Start:\n" + start[0].getName());
        System.out.println(start[1].getName() + "\n");
        
        Intersection i2 = find(streets.get(23), streets.get(28));
        Street[] end = i2.getStreets();
        System.out.println("Destination:\n" + end[0].getName());
        System.out.println(end[1].getName() + "\n");
        // System.out.println(i2.getLocation());
        
        Group g = shortestPath(i1, i2); 

        ArrayList<Instruction> p = g.path.compilePath(i1);

        for(Instruction inter : p){

            System.out.println(inter.continueDist);
            
            System.out.println();
            System.out.println(inter.i.getStreets()[0].getName());
            System.out.println(inter.i.getStreets()[1].getName());
            System.out.println(inter.turn);
        }
        //total distance
        System.out.println("Distance: " + (int)(g.dist * 100 + 0.5) / 100.0 + " miles");
        
    }

    //finds an intersection between 2 streets
    //return null if they do not intersect
    static Intersection find(Street s1, Street s2){
        for(Intersection i : intersections){
            if(i.getStreets()[0] == s1 && i.getStreets()[1] == s2)
                return i;
        }
        return null;
    }

    public static Group shortestPath(Intersection start, Intersection end){
        //set used to keep track of all the Streets that have been visited
        Set<Block> visited = new HashSet<>();

        //priority queue weighted by the weight of the Streets in each group
        PriorityQueue<Group> queue = new PriorityQueue<>(streets.size(), new Comparator<Group>() {

            @Override
            public int compare(Group g1, Group g2) {
                return g1.dist > g2.dist ? 1 : -1;
            }

        });

        //adds the starting node to the queue, along with a distance of zero
        PathBuilder startPath = new PathBuilder(null, start, null);
        queue.offer(new Group(start, 0, startPath));
        
        while(!queue.isEmpty()){
            
            //pulls the next node, distance, and path from the queue
            Group next = queue.poll();
            Intersection node = next.node;
            double dist = next.dist;
            PathBuilder path = next.path;

            //base case, once the end node is reached
            if(node == end) 
                return next;

            //loops through all the neighbor nodes of this node and adds them to the queue if they are not yet visited
            Block[] blocks = node.getBlocks();
            
            for(int i = 0; i < blocks.length; i++) {
                Block road = blocks[i];
                if(road == null) continue;
                Intersection nextInter = road.destination;

                //node is enqueued if this Street is not already visited
                //the new distance is the sum of the previous distance and the weight of this Street
                if(!visited.contains(road)) {
                    PathBuilder p = new PathBuilder(path, nextInter, road);
                    double distance = calcDistance(node, nextInter);
                    Group g = new Group(nextInter, dist + distance, p);
                    visited.add(road);
                    queue.offer(g);
                }
            }
        }
        //return null if no such path exists between the start and the end
        System.out.println("No path");
        return null;
    }

    //finds the distance in miles between two intersections
    static double calcDistance(Intersection i1, Intersection i2){

        Coordinate point1 = i1.getLocation();
        Coordinate point2 = i2.getLocation();
        double disY = (point1.y - point2.y) * 54.6;
        double disX = (point1.x - point2.x) * 69;                                
        return Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
    }

}

//group class used to bundle a node, distance from the start, 
//and an ArrayList containg the path from the start to this node
class Group {

    Intersection node;
    double dist;
    PathBuilder path;
    //Block block;

    public Group(Intersection node, double dist, PathBuilder path){
        this.node = node;
        this.dist = dist;
        this.path = path;
    }

    @Override
    public String toString(){
        return dist + " " + node.getLocation();
    }

}   

class PathBuilder {

    PathBuilder previous;
    Intersection current;
    Block block;
  
    public PathBuilder(PathBuilder previous, Intersection current, Block b){
      this.current = current;
      this.previous = previous;
      this.block = b;
    }

    public ArrayList<Instruction> compilePath(Intersection start) {

        PathBuilder p = this;
        ArrayList<Instruction> path = new ArrayList<>();
        Intersection thisTurn = p.current;
        path.add(new Instruction(thisTurn, "Arrive at your destination."));
        
        while(p != null){
            String turn = turn(p);
            System.out.println(p.current + " " + turn);
            if(turn.charAt(0) == 'T'){
                thisTurn = p.previous.current;
                path.add(0, new Instruction(thisTurn, turn));
                Instruction prevTurn = path.get(1);
                // System.out.println(thisTurn);
                // System.out.println(prevTurn.i);
                
                prevTurn.setDist(Wilmington.calcDistance(thisTurn, prevTurn.i));
                // System.out.println(prevTurn.continueDist);
                // System.out.println();
            }
            p = p.previous;
        }
        Intersection firstTurn = path.get(0).i;
        // Instruction begin = new Instruction(start, "");
        // begin.setDist(Wilmington.calcDistance(start, firstTurn)); 
        // path.add(0, begin);
        path.get(0).setDist(Wilmington.calcDistance(start, firstTurn));
        
        return path;
    }

    private String turn(PathBuilder p){

        // System.out.println("\nTurn: " + p.current);
        
        PathBuilder prev = p.previous;
        if(prev == null || prev.previous == null) return "Straight";

        PathBuilder prev2 = p.previous.previous; 
        if(prev2.block == null){
            if(p.current.onSameStreet(prev2.current)) return "Straight";
        }
        else if(prev2.block.street == p.block.street ||
        prev.current.onSameStreet(p.current) && prev2.current.onSameStreet(p.current)){
            
            return "Straight";
        }

        boolean movingUp = (prev.current.getLocation().x - prev2.current.getLocation().x) > 0;

        if((movingUp && p.current.getLocation().y > prev.block.street.getEqu().value(p.current.getLocation().x)) || 
        (!movingUp && p.current.getLocation().y < prev.block.street.getEqu().value(p.current.getLocation().x))){
            // System.out.println("Turn Right on to " + p.block.street.getName());
            return "Turn Right on to " + p.block.street.getName();
        }
        // System.out.println("Turn Left on to " + p.block.street.getName());
        return "Turn Left on to " + p.block.street.getName(); 
    }

  }

class Instruction{

    Intersection i;
    String turn;
    String continueDist;

    Instruction(Intersection i, String turn){
        this.i = i;
        this.turn = turn;
    }

    void setDist(double dist){
        if(dist < 0.1) this.continueDist = "Continue for " + Math.round(dist * 5280) + " ft";
        else this.continueDist = "Continue for " + (int) (dist * 100 + 0.5) / 100.0 + " miles";

    }
}

