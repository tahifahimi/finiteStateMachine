import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class NFA {
    private ArrayList<String> alphabet = new ArrayList<String>();
    private ArrayList<State> states = new ArrayList<State>();
    private ArrayList<String> final_states = new ArrayList<String>();
    private State start_state ;


    //read the file and generate the states and edges
    public NFA(String filePath) {
        try {
            File file = new File(filePath);
            BufferedReader br = new BufferedReader(new FileReader(file));

            //read the alphabet
            String st = br.readLine();
            String[] alpha = st.split(" ");
            for(int i=0;i<alpha.length;i++)
                alphabet.add(alpha[i]);

            // read the states
            st = br.readLine();
            String[] s = st.split(" ");
            for(int i=0;i<s.length;i++){
                //create new state and add it to the array list of states
                states.add(new State(s[i]));
            }

            //assign the start state
            st = br.readLine();
            for(int i=0;i<states.size();i++){
                State current = (State) states.get(i);
                if(current.name.equals(st)){
                    //find the start state
                    start_state = current;
                }
            }

            //assign the final state
            st = br.readLine();
            String[] f = st.split(" ");
            for(int i=0;i<states.size();i++){
                State current = (State) states.get(i);
                for(int j=0;j<f.length;j++){
                    if(current.name.equals(f[j])){
                        //find the start state
                        final_states.add(f[j]);
                    }
                }
            }

            //assign the edges
            while ((st = br.readLine()) != null) {
                String[] v = st.split(" ");
                // find the v[0] state and add the edge
                for(int i=0;i<states.size();i++){
                    if (states.get(i).name.equals(v[0])){
                        states.get(i).addEdge(v[1],v[2]);
                        break;
                    }
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    // change the NFA to DFA and write that into a file with passed name
    void NFA2DFA(String fileName){
        ArrayList<State> n2dStates = new ArrayList<State>();
        ArrayList<String> n2d_final_states = new ArrayList<String>();

        // count the number of added edges.
        int  numberOfEdges = 0;

        // step 1: add start state to GD and add it to the list of States
        State n2dStart = new State(start_state.name);
        n2dStates.add(n2dStart);

        // step 2 : do until all edges are clear for each states
        while (n2dStates.size()*alphabet.size() != numberOfEdges){
            int index = -1 ;
            String alpha = "";

            // step 2a : find a state that don't have enough edges
            for (int i=0;i<n2dStates.size();i++){
                if (n2dStates.get(i).getEdges()<alphabet.size()){
                    index = i;
                    break;
                }
            }

            //check valid state
            if (index == -1){
                System.out.println("error in finding the State");
                break;
            }
            //find the not added alphabet for this state
            boolean founded = false;
            for (int i=0;i<alphabet.size();i++){
                for (int j=0;j<n2dStates.get(index).edges.size();j++){
                    if (alphabet.get(i).equals(n2dStates.get(index).edges.get(j))){
                        founded = true;
                        break;
                    }
                }
                if (!founded){
                    // the alpha is founded
                    alpha = alphabet.get(i);
                    break;
                }
            }

            //step 2b: 1-separate the name of the state for finding NFA states
            //         2-calculate the accessible states and save them in accessible state
            String names[] = n2dStates.get(index).name.split(",");
            ArrayList<String> accessible = new ArrayList<String>();
            //search in each name of states names
            for (int i=0;i<names.length;i++){
                // search for accessible states from each state
                for (int j=0;j<states.size();j++){
                    if (states.get(j).name.equals(names[i])) {
                        //search in edges
                        for (int e = 0; e < states.get(j).getEdges(); e++) {
                            if (((Edge) (states.get(j).edges.get(e))).value.equals(alpha)) {
                                // add next states to accessible states
                                accessible.add(((Edge) states.get(i).edges.get(e)).otherSideOfEdge);

                            } else if (((Edge) (states.get(j).edges.get(e))).value.equals("λ")) {
                                // we can move with landa
                                // we can have thousands of landa after each other!
                            }
                        }
                    }
                }
            }
            // part 2c: now the accessible states are founded...
            //          1-add them together
            //          2-and check if there is any state with that name  in n2dstates or not!
            String foundedState = "";
            for (int i=0;i<accessible.size()-1;i++)
                foundedState = foundedState+accessible.get(i)+",";
            foundedState += accessible.get(accessible.size()-1);
            boolean check = false;
            for (int i=0;i<n2dStates.size();i++){
                if (n2dStates.get(i).name.equals(foundedState)){
                    check = true;
                }
            }
            // add new state to the n2dState
            if (!check)
                n2dStates.add(new State(foundedState));

            // add founded edge to the state
            n2dStates.get(index).addEdge(alpha,foundedState);
        }

        // part 3: assign the final_states
        for (int i=0;i<n2dStates.size();i++){
            String lable[] = n2dStates.get(i).name.split(",");
            for (int j=0;j<lable.length;j++){
                for (int k=0;k<final_states.size();k++){
                    if (lable[j].equals(final_states.get(k))){
                        // this state must be a final state
                        n2d_final_states.add(n2dStates.get(i).name);
                    }
                }
            }
        }

        //part 4: if our NFA accept the landa then the first state must be final state
        for (int i=0;i<n2dStart.edges.size();i++){
            Edge temp = (Edge) n2dStart.edges.get(i);
            if (temp.otherSideOfEdge.equals(temp.value) && temp.value.equals("λ")){
                n2d_final_states.add(n2dStart.name);
                break;
            }
        }

    }
}
