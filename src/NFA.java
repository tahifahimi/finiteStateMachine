import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

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
                State current = states.get(i);
                if(current.name.equals(st)){
                    //find the start state
                    start_state = current;
                }
            }

            //assign the final state
            st = br.readLine();
            String[] f = st.split(" ");
            for(int i=0;i<states.size();i++){
                State current = states.get(i);
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
        drawNFA(states, alphabet);
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
                    System.out.println("the founded node is : "+n2dStates.get(i).name);
                    break;
                }
            }

            //check valid state
            if (index == -1){
                System.out.println("error in finding the State");
                break;
            }
            //find the not added alphabet for this state
            boolean founded ;
            for (int i=0;i<alphabet.size();i++){
                founded = false;
                for (int j=0;j<n2dStates.get(index).edges.size();j++){
                    if (alphabet.get(i).equals(n2dStates.get(index).edges.get(j).value)){
                        System.out.println("alpha "+alphabet.get(i)+"   "+n2dStates.get(index).edges.get(j).value);
                        founded = true;
                    }
                }
                if (!founded){
                    // the alpha is founded
                    alpha = alphabet.get(i);
                    System.out.println("the alpha is : "+alpha);
                    break;
                }
            }

            //step 2b: 1-separate the name of the state for finding NFA states
            //         2-calculate the accessible states and save them in accessible state
            String names[] = n2dStates.get(index).name.split(",");
            HashSet<String> accessible = new HashSet<String>();

            // save the accessible states with landa value and search in them recursively
            ArrayList<String> landaStates = new ArrayList<>();     // save states that have landa edge and not seen alpha
            ArrayList<String> seenAlpha = new ArrayList<>();     // save states that have landa edge but seen the alpha
            ArrayList<String> temp = new ArrayList<>();         // save temprory edges from landa states
            ArrayList<String> temp2 = new ArrayList<>();         // save temprory edges from landa states

            //search in each name of states names
            for (int i=0;i<names.length;i++){
                System.out.println("for the name of "+names[i]);
                // search for accessible states from each state
                for (int j=0;j<states.size();j++){
                    if (states.get(j).name.equals(names[i])) {
                        //search in edges
                        for (int e = 0; e < states.get(j).getEdges(); e++) {
                            if (states.get(j).edges.get(e).value.equals(alpha)) {
                                // add next states to accessible states
                                accessible.add( states.get(j).edges.get(e).otherSideOfEdge);
                                // check if the othersideofEdge have landa edge it should added to the landaState
                                if (haveLandaEdge(states.get(j).edges.get(e).otherSideOfEdge)){
//                                    System.out.println("there is landa edge in : "+((Edge) states.get(j).edges.get(e)).otherSideOfEdge);
                                    seenAlpha.add(states.get(j).edges.get(e).otherSideOfEdge);
                                }
                                System.out.println("from alpha is : "+states.get(j).edges.get(e).otherSideOfEdge);

                            } else if (states.get(j).edges.get(e).value.equals("λ")) {
                                // we can move with landa
                                // we can have thousands of landa after each other!
                                landaStates.add(states.get(j).edges.get(e).otherSideOfEdge);
                                System.out.println("from landa is : "+states.get(j).edges.get(e).otherSideOfEdge);

                            }
                        }
                    }
                }
            }
            boolean finished = false;
            while(!finished){
                for (String lan : landaStates){
                    for(State s : states){
                        if (s.name.equals(lan)){
                            // search for destination
                            for (int e = 0; e < s.getEdges(); e++) {
                                if (s.edges.get(e).value.equals(alpha)) {
                                    // add next states to accessible states
                                    accessible.add(s.edges.get(e).otherSideOfEdge);
                                    // check if there is landa edge in this node or not
                                    if (haveLandaEdge( s.edges.get(e).otherSideOfEdge)){
                                        System.out.println(" there is landa edge    "+s.edges.get(e).otherSideOfEdge);
                                        seenAlpha.add(s.edges.get(e).otherSideOfEdge);
                                    }
                                } else if (((Edge) (s.edges.get(e))).value.equals("λ")) {
                                    // we can move with landa
                                    // we can have thousands of landa after each other!
                                    temp.add(s.edges.get(e).otherSideOfEdge);
                                }
                            }
                        }
                    }
                }

                //search in ( seenAlpha and have landa edge )for landa edges recursively
                for (String al: seenAlpha){
                    for(State s : states){
                        if (s.name.equals(al)){
                            for (int e = 0; e < s.getEdges(); e++) {
                                if (s.edges.get(e).value.equals("λ")) {
                                    if (haveLandaEdge(s.edges.get(e).otherSideOfEdge))
                                        temp2.add(s.edges.get(e).otherSideOfEdge);
                                    accessible.add(s.edges.get(e).otherSideOfEdge);
                                }
                            }
                        }
                    }
                }
                // add temp data to the landaState array
                landaStates = temp;
                seenAlpha = temp2;
                temp2 = new ArrayList<>();
                temp = new ArrayList<>();
                if (landaStates.size()==0 && seenAlpha.size()==0)
                    finished = true;
            }

            // part 2c: now the accessible states are founded...
            //          1-add them together and remove the repetitive state
            //          2-and check if there is any state with that name  in n2dstates or not!
            String foundedState = "";
            Iterator<String> it = accessible.iterator();
            while(it.hasNext()){
                foundedState += it.next()+",";
            }

            boolean check = false;
            for (int i=0;i<n2dStates.size();i++){
                if (n2dStates.get(i).name.equals(foundedState)){
                    check = true;
                }
            }
            // add new state to the n2dState
            if (!check){
                System.out.println("the added state is : "+foundedState);
                n2dStates.add(new State(foundedState));
            }else {
                System.out.println("there is a sata with name "+foundedState);
            }

            // add founded edge to the state
            System.out.println("the new edges is : "+n2dStates.get(index).name+" "+alpha+" "+foundedState);
            n2dStates.get(index).addEdge(alpha,foundedState);
            numberOfEdges++;
            drawNFA(n2dStates,alphabet);
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
            Edge temp = n2dStart.edges.get(i);
            if (temp.otherSideOfEdge.equals(temp.value) && temp.value.equals("λ")){
                n2d_final_states.add(n2dStart.name);
                break;
            }
        }

        // save in file
        saveDataToFile(alphabet,n2dStart,n2d_final_states,n2dStates,fileName);

    }

    private void saveDataToFile(ArrayList<String> alphabet,State start, ArrayList<String> n2d_final_states, ArrayList<State> n2dStates,String fileName) {
        try (FileWriter writer = new FileWriter(fileName);
             BufferedWriter bw = new BufferedWriter(writer)) {

            for (String alpha : alphabet)
                bw.write(alpha+" ");
            bw.write("\n");

            bw.write(start.name+"\n");

            for (String finalStates : n2d_final_states)
                bw.write(finalStates+" ");
            bw.write("\n");

            for (State s : n2dStates)
                for (Object e: s.edges)
                    bw.write(s.name+" "+((Edge)e).value + " "+((Edge)e).otherSideOfEdge+"\n");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //check if the otherside have the landa edge return true
    private boolean haveLandaEdge(String otherSideOfEdge) {
        //first find the state
        for (State st: states){
            if (st.name.equals(otherSideOfEdge)){
                // check if there is landa in edges or not
                for (Edge ed : st.edges){
                    if (ed.value.equals("λ")){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void drawNFA(ArrayList<State> st,ArrayList<String> al){
        System.out.println("the alphabets are : ");
        for (String alpha : al)
            System.out.printf("%s ,",alpha);
        System.out.println();

        System.out.println("the edges are :");
        for (State s : st){
            for (Object e:s.edges){
                System.out.printf("%s  %s  %s\n",s.name,((Edge)e).value,((Edge)e).otherSideOfEdge);
            }
        }
    }
}
