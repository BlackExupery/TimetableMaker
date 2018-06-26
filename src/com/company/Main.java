package com.company;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;


//anmerkung: constraint, dass eine Gruppe entweder 0 oder min_capazität einhält ist noch nicht implementiert

public class Main {

    public static void main(String[] args) {
         capsulatedModel();
    }

    // Anmerkung: Inputreader und Outputreader haben unterschiedliche lese/schreib Aufrufe
    public static void capsulatedModel(){
        InputReader ir = new InputReader("C:/Users/Tu/Desktop/tt_project/performancetest/tt_input.json");
        TT_Solver solver = new TT_Solver(ir);
        solver.solve("180s");
        //MappedSolution mc = solver.solve();
        //OutputWriter ow = new OutputWriter(mc,ir);
        //ow.writeInJSON("C:/Users/Tu/Desktop/tt_project/performancetest/tt_output.json");
        //OutputReader or = new OutputReader();
        //or.readFile("C:/Users/Tu/Desktop/tt_project/tt_output.json");
    }

}

