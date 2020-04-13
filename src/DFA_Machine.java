// DFA machine
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class DFA_Machine {
    private ArrayList alphabet = new ArrayList<String>();
    private ArrayList states = new ArrayList<State>();
    private ArrayList final_states = new ArrayList<String>();
    private State start_state ;

    //read the file and generate the states and edges
    public DFA_Machine() {
        try {
        File file = new File("file path");
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
                if (((State)states.get(i)).name.equals(v[0])){
                    ((State) states.get(i)).addEdge(v[1],v[2]);
                    break;
                }
            }
        }

        drawTheDFA();

        }catch(Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    //print the DFA (states and edges)
    private void drawTheDFA() {
    }

    //check the entered statements
    public void checkStatement(){
        Scanner scanner = new Scanner(System.in);
        String order = scanner.nextLine();
        while (order!="\n"){
            //check the letters of the order
            if(checkTheLetters(order)){
                //now check that order is acceptable in our DFA or not!
                checkSentence(order);
            }else {
                System.out.println("enter the acceptable alphabet");
            }
            order = scanner.nextLine();
        }
    }

    //check if the statement is acceptable in our DFA or not
    private void checkSentence(String order) {
        State current = start_state;
        String[] actions = order.split("(?!^)");

        //iterate over the sentence
        for (int i=0;i<actions.length;i++){
            String nextState = current.moveToNextState(actions[i]);
            if (nextState!=""){
                // find the next state and assign it to current state
                for (int k=0;k<states.size();k++){
                    if (((State)states.get(k)).name ==nextState ){
                        current = (State) states.get(k);
                        break;
                    }
                }
            }else {
                // no next state is found!
                ///Do sth!??????????????????????????
                System.out.println("the next state is not sounded!");
            }
        }

        //check current state is in the final state or not!
        for (int j=0;j<final_states.size();j++){
            if (current.name == ((State)final_states.get(j)).name ){
                System.out.println("the sentence is accepted!");
                //add some more information
                return;
            }
        }
        System.out.println("the sentence is not accepted!");
    }

    private boolean checkTheLetters(String statement){
        String[] alpha = statement.split("(?!^)");
        boolean flag =false;
        for (int i=0;i<alpha.length;i++){
            for(int j=0;j<alphabet.size();j++){
                if (alpha[i] == alphabet.get(j)){
                    flag = true;
                    break;
                }
            }
            if (!flag){
                return false;
            }
        }
        return true;
    }




}

