import java.util.ArrayList;

public class State {

    //save the name of the state (name of the state is identical)
    public String name;
    //edges of a state save where we can go from this state
    public ArrayList<Edge> edges;

    public State(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }

    // add new edge to the array of this State'edge!
    public void addEdge(String alpha,String destination){
        edges.add(new Edge(alpha,destination));
    }

    // pass the next state
    public String moveToNextState(String action){
        for(int i=0;i<edges.size();i++){
            if (action.equals(edges.get(i).value)){
                return edges.get(i).otherSideOfEdge;
            }
        }
        return "";
    }

    //pass number of the edges
    public int getEdges(){
        return edges.size();
    }
}
