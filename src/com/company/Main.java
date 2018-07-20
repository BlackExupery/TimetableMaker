package com.company;
import com.company.TT_Tests.SolutionTests.SolutionTest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.chocosolver.solver.Solution;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;


//Annmerkung: constraint, dass eine Gruppe entweder 0 oder min_capazität einhält ist noch nicht implementiert

public class Main {

    public static String INPUT_PATH = "C:/Users/Tu/Desktop/tt_project/performancetest/tt_newinput.json";
    public static String OUTPUT_PATH = "C:/Users/Tu/Desktop/tt_project/performancetest/tt_output.json";
    public static String SOLUTION_TIME = "900s";

    public static void main(String[] args) {

        /*
        if(args.length == 4){
            run_scheduler(args[0], args[1], Integer.parseInt(args[2]), args[3]);
        }

        if(args.length == 2){
            SolutionTest test = new SolutionTest();
            test.initializeIOReader(args[0], args[1]);
            if(test.check_s_has_f_condition() && test.check_s_rej_t_condition() &&
                    test.check_unique_studentassignment_per_timeslot()&& test.checkGroupCapacityCondition()&&
                    test.checkTimeslotAcceptsSubject()){
                System.out.println("Valid Solution!");
            }
            else{
                System.out.println("No Valid Solution!");
            }
        }*/
        run_scheduler(INPUT_PATH, OUTPUT_PATH, 1, "900s");
    }

    // Anmerkung: Inputreader und Outputreader haben unterschiedliche lese/schreib Aufrufe
    public static void run_scheduler(String input_path, String output_path, int constraint_mode, String solution_time){
        InputReader ir = new InputReader(input_path);
        TT_Model model = new TT_Model(ir);
        TT_Solver solver = new TT_Solver(model);
        Solution solution = solver.solve(solution_time, constraint_mode);
        OutputWriter ow = new OutputWriter(model, solution, ir);
        ow.writeInJSON(output_path);
    }

   static String [] days = {"Mo 1.", "Mo 2.", "Mo 3.", "Di 1.", "Di 2.", "Di 3.", "Mi 1.", "Mi 2.",
            "Mi 3.", "Do 1.", "Do 2.", "Do 3."};

    public static void generate_testdata(){
        JSONArray output = new JSONArray();
        int i =0;
            for(int j=0; j<50; j++){
                int s_id = j;
                for(int k=0; k<10; k++) {
                    JSONObject obj = new JSONObject();
                    obj.put("s_id", s_id + 100);
                    obj.put("ts_id", days[i % days.length]);
                    output.add(obj);
                    i++;
                }
            }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter file = new FileWriter("C:/Users/Tu/Desktop/tt_project/performancetest/testdata.json")) {
            //file.write(root.toJSONString());
            file.write(gson.toJson(output));
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

