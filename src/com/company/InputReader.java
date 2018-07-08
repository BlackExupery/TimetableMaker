package com.company;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



public class InputReader {

    // mapping zwischen konkreten studenten id und index für den Solver!
    private Map<Long,Integer> all_students;
    private Map<String,Integer> all_timeslots;
    private Map<String,Integer> all_subjects;
    private Map<Long, Integer> all_groups;

    private Map<Long,List<String>> map_s_has_f = new HashMap<Long,List<String>>();
    private Map<Long,List<String>> map_s_rejects_t = new HashMap<Long,List<String>>();
    private Map<String,Integer> map_min_g_capacity = new HashMap<String, Integer>();
    private Map<String,Integer> map_max_g_capacity = new HashMap<String,Integer>();
    private Map<Long, String> map_g_of_sbj = new HashMap<Long, String>();

    private int [] min_g_capacity;
    private int [] max_g_capacity;
    private int [][] s_has_f;
    private int [][] s_rejects_t;
    private int [] g_of_sbj;
    private JSONParser parser;


    public InputReader(String filepath){
        this.all_students = new HashMap<Long,Integer>();
        this.all_timeslots= new HashMap<String,Integer>();
        this.all_subjects = new HashMap<String,Integer>();
        this.all_groups = new HashMap<Long, Integer>();
        this.parser = new JSONParser();
        try {

            Object obj = parser.parse(new FileReader(filepath));

            JSONObject jsonObject = (JSONObject) obj;
            readAllStudents(jsonObject);
            readAllGroups(jsonObject);
            readAllSubjects(jsonObject);
            readAllTimeslots(jsonObject);
            read_s_has_f(jsonObject);
            read_s_rejects_t(jsonObject);
            read_g_capacity(jsonObject);
            //readGroupNumbersPerSubject(jsonObject);
            read_g_of_sbj(jsonObject);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public Map<Long, Integer> getAllStudents() {
        return all_students;
    }

    public Map<String, Integer> getAllSubjects() {
        return all_subjects;
    }

    public Map<String, Integer> getAllTimeslots() {
        return all_timeslots;
    }

    public Map<Long, Integer> getAllGroups(){return all_groups;}

    public int[][] get_s_has_f(){
        return this.s_has_f;
    }

    public int[][] get_s_rejects_t(){
        return this.s_rejects_t;
    }

    public int[] get_min_g_capacity(){
        return this.min_g_capacity;
    }

    public int[] get_max_g_capacity(){
        return this.max_g_capacity;
    }

    public int [] get_g_of_sbj() {return this.g_of_sbj;}

    public Map<String, Integer> get_map_min_g_capacity() {
        return map_min_g_capacity;
    }

    public Map<String, Integer> get_map_max_g_capacity() {
        return map_max_g_capacity;
    }

    public Map<Long,List<String>> get_map_s_has_f(){
        return this.map_s_has_f;
    }

    public Map<Long, List<String>> get_map_s_rejects_t() {
        return map_s_rejects_t;
    }

    public Map<Long, String> get_map_g_of_sbj(){return map_g_of_sbj;}


    public void printContent(){
        System.out.println(all_students.size());
        System.out.println(all_subjects.size());
        System.out.println(all_timeslots.size());

    }


    private void readAllStudents(JSONObject jObject){
        JSONArray subjects = (JSONArray)jObject.get("subjects");
        int id_index =0;
        for(int i=0; i<subjects.size();i++){
            JSONObject subject = (JSONObject)subjects.get(i);
            JSONArray students = (JSONArray)subject.get("students");

            for(int j=0; j<students.size();j++){
                JSONObject student = (JSONObject)students.get(j);
                if(!all_students.containsKey((Long)student.get("id"))){
                    all_students.put((Long)student.get("id"), id_index);
                    id_index++;
                }
            }
        }
    }

    private void readAllGroups(JSONObject jObject){
        JSONArray groups = (JSONArray)jObject.get("g_of_sbj");
        int id_index =0;
        for(int i=0; i<groups.size();i++){
            JSONObject group = (JSONObject)groups.get(i);
            if(!all_groups.containsKey(group.get("g_id"))){
                all_groups.put((Long)group.get("g_id"), id_index);
                id_index++;
            }
        }


    }

    private void readAllTimeslots(JSONObject jObject){
        JSONArray timeslots = (JSONArray)jObject.get("timeslots");
        int id_index =0;
        for(int i=0; i<timeslots.size();i++){
            JSONObject timeslot = (JSONObject)timeslots.get(i);
            if(!all_timeslots.containsKey((String)timeslot.get("id"))){
                all_timeslots.put((String)timeslot.get("id"), id_index);
                id_index++;
            }
        }
    }

    private void readAllSubjects(JSONObject jObject){
        JSONArray subjects = (JSONArray)jObject.get("subjects");
        int id_index =0;

        for(int i=0; i<subjects.size();i++){
            JSONObject subject = (JSONObject)subjects.get(i);
            if(!all_subjects.containsKey((String)subject.get("id"))){
                all_subjects.put((String)subject.get("id"), id_index);
                id_index++;
            }
        }
    }

    private void read_s_has_f(JSONObject jObject){
        s_has_f = new int[all_students.size()][all_subjects.size()];
        //first fill array with 0's
        for(int i=0; i<all_students.size();i++){
            for(int j=0; j<all_subjects.size();j++){
                s_has_f[i][j] =0;
            }
        }
        //get information out of json and safe the information binary
        JSONArray subjects = (JSONArray)jObject.get("subjects");

        for(int i=0; i<subjects.size();i++){
            JSONObject subject = (JSONObject) subjects.get(i);
            JSONArray students = (JSONArray)subject.get("students");

            for(int j=0; j<students.size();j++){
                JSONObject student = (JSONObject)students.get(j);
                Long sid = (Long) student.get("id");
                s_has_f[all_students.get(sid)][all_subjects.get((String)subject.get("id"))]=1;
                if(!this.map_s_has_f.containsKey(sid)){
                    this.map_s_has_f.put(sid,new LinkedList<String>());
                }
                this.map_s_has_f.get(sid).add((String)subject.get("id"));
            }
        }
    }

    private void read_g_capacity(JSONObject jObject){
        min_g_capacity = new int[all_subjects.size()];
        max_g_capacity = new int[all_subjects.size()];
        JSONArray subjects = (JSONArray) jObject.get("subjects");

        for(int i=0; i<subjects.size();i++){
            JSONObject subject = (JSONObject)subjects.get(i);
            String sbj_id = (String)subject.get("id");
            int min_cap = Math.toIntExact((Long) subject.get("min_cap"));
            int max_cap = Math.toIntExact((Long)subject.get("max_cap"));

            min_g_capacity[all_subjects.get(sbj_id)] = min_cap;
            max_g_capacity[all_subjects.get(sbj_id)] = max_cap;

            map_min_g_capacity.put(sbj_id,min_cap);
            map_max_g_capacity.put(sbj_id,max_cap);
        }
    }

    private void read_g_of_sbj(JSONObject obj){
        JSONArray j_g_of_sbj = (JSONArray) obj.get("g_of_sbj");
        this.g_of_sbj = new int[this.all_groups.size()];
        for(int i=0; i<j_g_of_sbj.size();i++){
            JSONObject gsbj = (JSONObject) j_g_of_sbj.get(i);
            if(!this.map_g_of_sbj.containsKey((Long)gsbj.get("g_id"))){
                map_g_of_sbj.put((Long)gsbj.get("g_id"),(String)gsbj.get("f_id"));
                this.g_of_sbj[this.all_groups.get((Long)gsbj.get("g_id"))] = all_subjects.get((String)gsbj.get("f_id"));
            }
        }
    }


    // in der JSON müssen unter timeslots: ALLE timeslots existieren (s_rejects_t darf keinen timeslot ansprechen
    // welcher nicht unter timeslots aufgelistet ist)
    private void read_s_rejects_t(JSONObject jObject){
        s_rejects_t = new int[all_students.size()][all_timeslots.size()];
        for(int i=0; i<all_students.size();i++){
            for(int j=0; j<all_timeslots.size();j++){
                s_rejects_t[i][j] =0;
            }
        }

        JSONArray unwanted_timeslots = (JSONArray)jObject.get("unwanted_timeslots");
        for(int i=0; i<unwanted_timeslots.size();i++){
            JSONObject ut = (JSONObject)unwanted_timeslots.get(i);
            Long sid = (Long) ut.get("s_id");
            s_rejects_t[all_students.get(sid)][all_timeslots.get((String)ut.get("ts_id"))]=1;
            if(!map_s_rejects_t.containsKey(sid)){
                map_s_rejects_t.put(sid,new LinkedList<String>());
            }
            map_s_rejects_t.get(sid).add((String)ut.get("ts_id"));
        }
    }





}
