import java.util.ArrayList;

public class Graph {

    ArrayList<ArrayList<Edge>> edges;

    public Graph(int numNodes){
        this.edges = new ArrayList<>();
        for(int i = 0; i < numNodes; i++){
            edges.add(new ArrayList<>());
        }
    }

    public void addEdges(int v, Edge...edges){
        for(Edge edge : edges){
            this.edges.get(v).add(edge);
        }
    }

}
