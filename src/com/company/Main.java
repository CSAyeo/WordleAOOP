package com.company;

import java.io.IOException;
import java.util.Scanner;


//this class only exists for testing and debugging purposes.
public class Main {
    public static void main(String[] args) throws IOException {
        int SpoilerFlag = Decider("Standard", "Spoiler");
        int FixedFlag = Decider("Random Word", "Fixed Word");
        int GuessFlag = Decider("Strict", "Loose");
        int GameType = Decider("CLI", "GUI");
        Model model = new Model();
        model.initalise();
        model.setAnswer(); //Calls set answer to select a new answer
        if (GameType == 1){
            CLIGame(model);
        }else if (GameType ==2){
            GUIGame(model);
        }else{
            System.out.println("ERROR");
        }
    }

    public static int Decider(String... params){
        Scanner Scan = new Scanner(System.in);  // Create a Scanner object
        int d = 0;
        while (!(d >= 1 && d <= params.length)) {
            System.out.printf("%s (1) OR %s (2)", params[0], params[1]);
            d = Scan.nextInt();  // Read user input
        }
        return d;
    }
    static void CLIGame(Model model) throws IOException {
        while (!model.getGame()) {
            model.TakeGuess();
            model.CLIPrint(model.calcTurn());
        }
        int NewGame = Decider("New Game", "End Game");
        if (NewGame==1){
            model.setAnswer();
            CLIGame(model);}
    }
    static void GUIGame(Model model){
        Controller controller = new Controller(model);
        View GUI = new View(model, controller);
    }
}
/*
[Model|+forname: string;+surname: string;-password: string|login(user,pass)]
[Observable|+setChanged(); +notifyObservers()]
[<<Interface>>Observer|+addObserver()]


[note: You can stick notes on diagrams too!],
[Observable]^ [Model]
[View] -> [Model]
[Controller] -> [Model]
[View] <-> [Controller]
[Observer]^-.-[View]
*/