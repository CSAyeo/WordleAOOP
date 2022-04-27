package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.util.*;

class CLI{

    public static void main(String[] args) throws IOException {//Main for a new CLI game
        Model model = new Model(); //initates a Model class, no controller or view required
        model.initalise(); //exectues model first run initalisation
        model.setAnswer(); //Calls set answer to select a new answer
        CLIGame(model); //calls the gameloop method
    }

    static void CLIGame(Model model) throws IOException {
        while (!model.winflag) { //while the game isnt over
            model.CLIPrint(model.calcTurn(model.TakeGuess()));
        }
        System.out.println("Enter 1 for a new game:"); //offer the user a new game
        Scanner scan = new Scanner(System.in);  // Create a Scanner object
        int NewGame = scan.nextInt();
        if (NewGame==1){
            CLIGame(model);} //recursively create a new game
    }
}

public class Model extends Observable {

    private List<String> printlist = Arrays.asList("\u001B[0m", "\u001B[43m","\u001B[42m" ); //reset, out, in
    private static final String Solution_File = "src/com/company/common.txt";
    private static final String Guess_File = "src/com/company/words.txt";
    private static List<String> Solution;
    private static List<String> Guess;
    private String Answer;
    private int gameflag;
    private boolean spoilerflag=true; //print the spoiler? true - print, false -don't print
    private boolean randomflag= true; //select a random word or fixed. true - 'cigar', false random word
    private boolean allowflag= false; // Allow entry of 5 letters not in wordlist. true - allow all, false - allow only valid
    private String UserGuess = null; //stores the user guess for processing
    public boolean winflag; //has the user won?
    private final int guesslimit = 8; //sets the number of guesses allowed per game
    private ArrayList<String> Inplace = new ArrayList<String>();
    private ArrayList<String> Outplace =new ArrayList<String>();
    private ArrayList<String> Noplace =new ArrayList<String>();
    private ArrayList<String> Unplace =new ArrayList<String>();


    public String getAnswer() {
        return this.Answer;
    }

    public int getTurn(){return gameflag;}

    public boolean getWin(){return winflag;}


    public void setAnswer() {
        Random rand = new Random();
        if (randomflag) {
            this.Answer = Solution.get(rand.nextInt(Solution.size()));
        }
        else{this.Answer=Solution.get(0);};
        this.gameflag = 0;
        this.Inplace.clear();
        this.Outplace.clear();
        this.Noplace.clear();
        this.Unplace.clear();
        for(int i = 0; i < 26; i++){
            Unplace.add(String.valueOf((char) (97 + i)));}
        this.winflag = false;
        if (spoilerflag) {
            System.out.println(this.Answer);
        }
    }

    public void initalise() throws IOException {
        this.Guess = new ArrayList<>();
        BufferedReader sl = new BufferedReader(new FileReader(Solution_File));
        this.Solution = new ArrayList<>();
        String line;
        while ((line = sl.readLine()) != null) {
            this.Solution.add(line);
            this.Guess.add(line);
        }
        sl.close();
        //Guess list
        BufferedReader gr = new BufferedReader(new FileReader(Guess_File));
        while ((line = gr.readLine()) != null) {
            this.Guess.add(line);
        }
        gr.close();
    }


    //FIX ME - Binary search and removal :)))
    private Integer letterlogic(char letter, int pos) {
        assert this.Answer != null;
        if (letter == this.Answer.charAt(pos)){
            if (!Inplace.contains(String.valueOf(letter))) {
                Inplace.add(String.valueOf(letter));
            }
            if(Outplace.contains(String.valueOf(letter))){
                Outplace.remove(new String(String.valueOf((letter))));
            }
            if(Unplace.contains(String.valueOf(letter))){
                Unplace.remove(new String(String.valueOf((letter))));
            }
            return 2;
        } else if (this.Answer.indexOf(letter) != -1){
            if ((!Inplace.contains(String.valueOf(letter))) && (!Outplace.contains(String.valueOf(letter)))){
            Outplace.add(String.valueOf(letter));
        }
            if(Unplace.contains(String.valueOf(letter))){
                Unplace.remove(new String(String.valueOf((letter))));
            }
            return 1;
        } else {
            if(Unplace.contains(String.valueOf(letter))){
                Unplace.remove(new String(String.valueOf((letter))));
            }
            Noplace.add(String.valueOf(letter));
            return 0;
        }

    }


    public void wordaccept(String GuessInput){
            UserGuess = null;
            if (Guess.contains(GuessInput)||allowflag) {
                this.gameflag += 1;
                UserGuess = GuessInput;
                setChanged();
                notifyObservers();
            }
        }


    public ArrayList<Integer> calcTurn(String UserGuess) {
        int cpos = 0;
        ArrayList<Integer> res = new ArrayList<Integer>();
        if (UserGuess.equals(this.Answer)) {
            this.winflag = true;
            System.out.println(printlist.get(0) + "YOU WON!");
        }

        for (char letter : UserGuess.toCharArray()){
            res.add(letterlogic(letter, cpos));
            //sort all the lists - a BST implementation would be preferable but this works for now
            Collections.sort(Noplace);
            Collections.sort(Unplace);
            Collections.sort(Inplace);
            Collections.sort(Outplace);
            cpos++;
        }
        return res;
    }

    //getters and setters for GUI




    // The following are only for CLI - can be safely moved to different class if needed

    private void output(){
        System.out.println("Inplace: " + this.Inplace);
        System.out.println("Out of place: " + this.Outplace);
        System.out.println("No place (Wrong): " + this.Noplace);
        System.out.println("Unplaced: " + this.Unplace);

    }

    public void CLIPrint(ArrayList<Integer> res) {
        int cpos = 0;
        for (int i : res){
            System.out.print(printlist.get(i) + UserGuess.charAt(cpos));
            cpos++;
        }
        System.out.println(printlist.get(0));
        output();
        if (gameflag > guesslimit){
            winflag = true;
            System.out.println(("Game over! Guess limit of %d reached.\nThe word was : " + this.getAnswer()).formatted(guesslimit));
        }
    }
    public String TakeGuess() {
        Scanner Scan = new Scanner(System.in);  // Create a Scanner object
        String GuessInput = null;
        System.out.println("\nEnter Guess");
        GuessInput = Scan.nextLine().toLowerCase();
        wordaccept(GuessInput);
        while (UserGuess==null) {
            System.out.println("Word not valid! \nEnter Guess");
            GuessInput = Scan.nextLine().toLowerCase();
            wordaccept(GuessInput);
        }
        return GuessInput;
    }

    public int getLimit(){
        return guesslimit;
    }
}

//set changed
//notifyobservers