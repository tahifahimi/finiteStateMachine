public class Edge {
    // value = each alphabet of the transport
    public String value;
    // next state that we can arrive from that!
    public String otherSideOfEdge ;

    public Edge(String value , String nextState) {
        this.value = value;
        this.otherSideOfEdge = nextState;
    }
}
