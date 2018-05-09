package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OutputWriter {

    private Map<Long,Integer> all_students;
    private Map<String,Integer> all_timeslots;
    private Map<String,Integer> all_subjects;

    private Map<Integer,Map<Integer,Integer>> s_in_g = new HashMap<Integer,Map<Integer,Integer>>();
    private Map<Integer,Integer> g_of_f = new HashMap<Integer,Integer>();
    private Map<Integer,Integer> g_in_t = new HashMap<Integer,Integer>();

    private Map<Long,Map<Integer,Integer>> s_in_g_out = new HashMap<Long,Map<Integer,Integer>>();
    private Map<Integer,String> g_of_f_out = new HashMap<Integer,String>();
    private Map<Integer,String> g_in_t_out = new HashMap<Integer,String>();

    public OutputWriter(SolutionConverter sc,InputReader ir){
        this.all_students = ir.getAll_students();
        this.all_timeslots = ir.getAll_timeslots();
        this.all_subjects = ir.getAll_subjects();
        this.s_in_g = sc.get_s_in_g();
        this.g_of_f = sc.get_g_of_f();
        this.g_in_t = sc.get_g_in_t();
    }

    //bilde die Matrix-Indezies auf die konkrete ID ab
    private void s_index_to_id(){
        for(Long s : all_students.keySet()){
           int index = all_students.get(s);
            Map<Integer,Integer> g_map = new HashMap<Integer,Integer>();
           for(Integer g : s_in_g.get(index).keySet()){
               g_map.put(g,1);
           }
            s_in_g_out.put(s,g_map);
            g_map = new HashMap<Integer,Integer>();

        }
    }

    private void f_index_to_id(){
        for(String f : all_subjects.keySet()){
            int index = all_subjects.get(f);
            for(Integer g : g_of_f.keySet()){
                if(g_of_f.get(g).equals(index)){
                    g_of_f_out.put(g,f);
                }
            }

        }
    }

    private void t_index_to_id(){
       for(String t : all_timeslots.keySet()) {
           int index = all_timeslots.get(t);
           for (Integer g : g_in_t.keySet()) {
              if(g_in_t.get(g).equals(index)){
                  g_in_t_out.put(g,t);
              }
           }
       }
    }


    //output als JSON-Datei
    public void writeInJSON(String path){
       JSONObject root = new JSONObject();
       JSONArray s_in_g_list = new JSONArray();
       JSONArray g_of_f_list = new JSONArray();
       JSONArray g_in_t_list = new JSONArray();
       s_index_to_id();
       f_index_to_id();
       t_index_to_id();

       for(Long s : s_in_g_out.keySet()){
           for(Integer g:s_in_g_out.get(s).keySet()){
                JSONObject obj = new JSONObject();
                obj.put("s_id",new Long(s));
                obj.put("g_id", new Long(g));
                s_in_g_list.add(obj);
           }
       }

       for(Integer g : g_of_f_out.keySet()){
           JSONObject obj = new JSONObject();
           obj.put("g_id", new Long(g));
           obj.put("f_id", new String(g_of_f_out.get(g)));
           g_of_f_list.add(obj);
       }

       for(Integer g : g_in_t_out.keySet()){
           JSONObject obj = new JSONObject();
           obj.put("g_id",new Long(g));
           obj.put("t_id",g_in_t_out.get(g));
           g_in_t_list.add(obj);
       }

       root.put("s_in_g",s_in_g_list);
       root.put("g_of_sbj",g_of_f_list);
       root.put("g_in_t",g_in_t_list);
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
