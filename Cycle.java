import java.util.*;
 
public class Cycle {
 
    static Stack<Edge> stack;
    static Set<Edge> visited;
    static ArrayList<ArrayList<Integer>> parentNodes;
    static ArrayList<ArrayList<Edge>> graph;

    // static Queue<Edge> queue;
    static Set<Integer> sorted;
 
    public static void main(String...args) {
        graph = new ArrayList<>();
        parentNodes = new ArrayList<>();
        setUpGraph();

        // System.out.println("Sink: " + sink(graph));
        // System.out.println("Source: " + source(graph));
        
        int[] sort = topologicalSort(graph);
        if(sort != null){
          for(int x: sort)
            System.out.print(x + " ");
        }
        else System.out.println("No sorting possible");
    }
 
    static void setUpGraph(){
 
        for(int i = 0; i < 5; i++) graph.add(new ArrayList<>());
        // graph.get(0).add(new Edge(1));
        // graph.get(0).add(new Edge(3));
        // graph.get(1).add(new Edge(2));
        // graph.get(1).add(new Edge(3));
        // graph.get(2).add(new Edge(3));
        // graph.get(3).add(new Edge(1));
        // graph.get(0).add(new Edge(3));
        // graph.get(0).add(new Edge(4));
        // graph.get(1).add(new Edge(4));
        // graph.get(3).add(new Edge(5));
        // graph.get(4).add(new Edge(6));
        // graph.get(4).add(new Edge(7));
        // graph.get(5).add(new Edge(7));
        graph.get(0).add(new Edge(1));
        graph.get(1).add(new Edge(2));
        graph.get(1).add(new Edge(3));
        // graph.get(3).add(new Edge(4));
        graph.get(2).add(new Edge(4));
    }
 
    static int sink(ArrayList<ArrayList<Edge>> graph) {
        for(int i = 0; i < graph.size(); i++){
            // System.out.println(i);
            if(graph.get(i).size() == 0) return i;
        }
        return -1;
    }

    static boolean isDAG(ArrayList<ArrayList<Edge>> graph, int source){
      for(int i = 0; i < graph.size(); i++)
            parentNodes.add(new ArrayList<>());

      Edge first = graph.get(source).get(0);
      stack = new Stack<>();
      stack.push(first);
      visited = new HashSet<>();
      visited.add(first);
      return !hasBackEdge(graph, source);
    }
 
    static boolean hasBackEdge(ArrayList<ArrayList<Edge>> graph, int node) {
       
        if(stack.isEmpty()) return false;
        ArrayList<Edge> edges = graph.get(node);
        for(int i = 0; i < edges.size(); i++){
            Edge e = edges.get(i);
            int neigh = e.destination;
            if(parentNodes.get(node).contains(neigh))
                return true;
            else if(!visited.contains(e)) {
                stack.push(e);
                visited.add(e);
                parentNodes.get(neigh).add(node);
            }
        }
        return hasBackEdge(graph, stack.pop().destination);
    }

    static int[] topologicalSort(ArrayList<ArrayList<Edge>> graph) {

      sorted = new HashSet<>();
      int source = findSource(graph);
      if(source == -1 || !isDAG(graph, source)) return null;
      int[] sorting = new int[graph.size()];
      sorting[0] = source;
      sorted.add(source);
      return sort(graph, sorting, 1);
    }

    static int[] sort(ArrayList<ArrayList<Edge>> graph, int[] sorting, int index){
      
      if(index == graph.size()) return sorting;
      int next = findSource(graph);
      sorted.add(next);
      sorting[index] = next;
      return sort(graph, sorting, index + 1);
    }

    static int findSource(ArrayList<ArrayList<Edge>> graph){
        boolean[] incoming = new boolean[graph.size()];
        for(int i = 0; i < graph.size(); i++){
          if(sorted.contains(i)) continue;
          ArrayList<Edge> edges = graph.get(i);
          for(Edge e : edges){
            incoming[e.destination] = true;
          }
        }
        for(int i = 0; i < incoming.length; i++){
            if(!incoming[i] && !sorted.contains(i)) return i;
        }
        return -1;
    }

}
 
class Edge{
 
    int destination;
 
    Edge(int destination){
       
        this.destination = destination;
    }
}
 
