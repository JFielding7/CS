import java.util.*;

public class Path {

    public static void main(String...args){
        Graph graph = new Graph(5);
        graph.addEdges(0, new Edge(1, 15), new Edge(2, 300));
        graph.addEdges(1, new Edge(4, 1000), new Edge(2, 71));
        graph.addEdges(2, new Edge(3, 2));
        graph.addEdges(3, new Edge(4, 12));
        graph.addEdges(4);
        System.out.println(shortestPath(0, 4, graph));
        
    }

    public static Group shortestPath(int start, int end, Graph graph){
        //set used to keep track of all the edges that have been visited
        Set<Edge> visited = new HashSet<>();

        //priority queue weighted by the weight of the edges in each group
        PriorityQueue<Group> queue = new PriorityQueue<>(graph.edges.size(), new Comparator<Group>() {

            @Override
            public int compare(Group pair1, Group pair2) {
                return pair1.dist - pair2.dist;
            }
        });

        //adds the starting node to the queue, along with a distance of zero
        ArrayList<Integer> startPath = new ArrayList<>();
        startPath.add(start);
        queue.offer(new Group(start, 0, startPath));
        
        while(!queue.isEmpty()){
            
            //pulls the next node, distance, and path from the queue
            Group next = queue.poll();
            int node = next.node;
            int dist = next.dist;
            ArrayList<Integer> path = next.path;

            //base case, once the end node is reached
            if(node == end) return next;

            //loops through all the neighbor nodes of this node and adds them to the queue if they are not yet visited
            ArrayList<Edge> connectedEdges = graph.edges.get(node);
            for(int i = 0; i < connectedEdges.size(); i++) {
                Edge edge = connectedEdges.get(i);
                int vertex = edge.destination;
                
                //node is enqueued if this edge is not already visited
                //the new distance is the sum of the previous distance and the weight of this edge
                if(!visited.contains(edge)) {
                    ArrayList<Integer> p = new ArrayList<>(path);
                    visited.add(edge);
                    p.add(vertex);
                    queue.offer(new Group(vertex, dist + edge.getWeight(), p));
                }
            }
        }
        //return null if no such path exists between the start and the end
        return null;
    }

}

//group class used to bundle a node, distance from the start, 
//and an ArrayList containg the path from the start to this node
class Group {

    int node;
    int dist;
    ArrayList<Integer> path;

    public Group(int node, int dist, ArrayList<Integer> path){
        this.node = node;
        this.dist = dist;
        this.path = path;
    }

    @Override
    public String toString(){
        String str = "Distance " + this.dist + "\n";
        for(Integer x : path){
            str += x + "->";
        }
        return str.substring(0, str.length()-2);
    }
}


class Edge implements Comparable<Edge> {

    int destination;
    private int weight;

    public Edge(int destination, int weight) {
        this.destination = destination;
        this.weight = weight;
    }

    public Edge(int destination) {
        this.destination = destination;
    }

    public int getWeight(){
        return this.weight;
    }

    @Override
    public int compareTo(Edge other) {

        return Double.compare(this.weight, other.getWeight());
    }

}