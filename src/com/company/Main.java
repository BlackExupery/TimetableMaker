package com.company;


//Annmerkung: constraint, dass eine Gruppe entweder 0 oder min_capazität einhält ist noch nicht implementiert

public class Main {

    public static void main(String[] args) {
         capsulatedModel();
    }

    // Anmerkung: Inputreader und Outputreader haben unterschiedliche lese/schreib Aufrufe
    public static void capsulatedModel(){
        InputReader ir = new InputReader("C:/Users/Tu/Desktop/tt_project/performancetest/tt_testinput.json");
        TT_Solver solver = new TT_Solver(ir);
        solver.solve("900s");
    }

}

