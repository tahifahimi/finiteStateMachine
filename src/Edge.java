public class Edge {
    public String value;
    public String otherSideOfEdge ;

    public Edge(String value , String nextState) {
        this.value = value;
        this.otherSideOfEdge = nextState;
    }
}
