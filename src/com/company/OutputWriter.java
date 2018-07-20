package com.company;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.chocosolver.solver.variables.IntVar;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import org.chocosolver.solver.Solution;

public class OutputWriter {
    private Map<Long,Integer> all_students;
    private Map<String,Integer> all_timeslots;
    private Map<String,Integer> all_subjects;
    private Solution solution;

    //die Keys der Maps sind die Indezies aus der Berechnungsmatrix (MappedSolution)
    private IntVar[][] s_in_g;
    private int[] g_of_sbj_indexed;
    private IntVar[] g_in_t;

    public OutputWriter(TT_Model model, Solution solution, InputReader ir){
        this.all_students = ir.get_all_students();
        this.all_timeslots = ir.get_all_timeslots();
        this.all_subjects = ir.get_all_subjects();
        this.g_of_sbj_indexed  = ir.get_g_of_sbj_indexed();
        this.s_in_g = model.get_s_in_g();
        this.g_in_t = model.get_g_in_t();
        this.solution = solution;
    }

    public void writeInJSON(String path){
        JSONObject root = new JSONObject();
        JSONArray s_in_g_list = new JSONArray();
        JSONArray g_of_f_list = new JSONArray();
        JSONArray g_in_t_list = new JSONArray();

        //get real id's from students
        for(Long s : all_students.keySet()){
            //convert that id to index because, s_in_g works with indecies
            int index = all_students.get(s);
            for(int g=0; g<s_in_g[index].length;g++){
                //check if under these indecies s is in g
                if(solution.getIntVal(s_in_g[index][g])==1){
                    JSONObject obj = new JSONObject();
                    //if yes, write original student id and original group id into JSON-Object
                    obj.put("s_id",new Long(s));
                    obj.put("g_id", new Long(g));
                    s_in_g_list.add(obj);
                }
            }
        }

        //get real id's from subjects
        for(String f : all_subjects.keySet()){
            //convert that id to index because g_of_f works with indecies
            int index = all_subjects.get(f);
            for(int g=0; g<g_of_sbj_indexed.length;g++){
                //check if under these indecies g is of f
                if(g_of_sbj_indexed[g]==index){
                    JSONObject obj = new JSONObject();
                    //if yes, write original group id and subject id into JSON-Object
                    obj.put("g_id", new Long(g));
                    obj.put("f_id", f);
                    g_of_f_list.add(obj);
                }
            }
        }

        //get real id's from timeslots
        for(String t : all_timeslots.keySet()){
            //convert that id to index because g_in_t works with indecies
            int index = all_timeslots.get(t);
            for(int g=0; g<g_in_t.length;g++){
                //check if under these indecies g is of f
                if(solution.getIntVal(g_in_t[g])==index){
                    JSONObject obj = new JSONObject();
                    //if yes, write original group id and timeslot id into JSON-Object
                    obj.put("g_id", new Long(g));
                    obj.put("t_id", t);
                    g_in_t_list.add(obj);
                }
            }
        }

        root.put("studentInGroup",s_in_g_list);
        root.put("groupOfSubject",g_of_f_list);
        root.put("groupInTimeslot",g_in_t_list);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        try (FileWriter file = new FileWriter(path)) {
            //file.write(root.toJSONString());
            file.write(gson.toJson(root));
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
