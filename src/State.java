import java.util.ArrayList;

public class State {

    //save the name of the state (name of the state is identical)
    public String name;
    private ArrayList edges;

    public State(String name) {
        this.name = name;
        this.edges = new ArrayList<Edge>();
    }

    // add new edge to the array of this State'edge!
    public void addEdge(String alpha,String destination){
        edges.add(new Edge(alpha,destination));
    }

    // pass the next state
    public String moveToNextState(String action){
        for(int i=0;i<edges.size();i++){
            if (action.equals(((Edge)edges.get(i)).value)){
                return ((Edge)edges.get(i)).otherSideOfEdge;
            }
        }
        return "";
    }
}
