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

    private Map<Long,List<String>> s_in_sbj = new HashMap<Long,List<String>>();
    private Map<Long,List<String>> s_rejects_t = new HashMap<Long,List<String>>();
    private Map<String, List<String>> t_accepts_sbj = new HashMap<String, List<String>>();
    private Map<String,Integer> min_t_capacity = new HashMap<String, Integer>();
    private Map<String,Integer> max_t_capacity = new HashMap<String,Integer>();
    private Map<Long, String> g_of_sbj = new HashMap<Long, String>();

    private Map<Long,List<Long>> s_in_g = new HashMap<Long, List<Long>>();
    private Map<Long,String> g_in_t = new HashMap<Long,String>();

    private int [] min_t_capacity_indexed;
    private int [] max_t_capacity_indexed;
    private int [][] s_in_sbj_indexed;
    private int [][] s_rejects_t_indexed;
    private int [][] t_accepts_sbj_indexed;
    private int [] g_of_sbj_indexed;
    private JSONParser parser;


    public InputReader(String filepath){
        this.all_students = new HashMap<Long,Integer>();
        this.all_timeslots= new HashMap<String,Integer>();
        this.all_subjects = new HashMap<String,Integer>();
        this.all_groups = new HashMap<Long, Integer>();
        this.s_in_sbj = new HashMap<Long,List<String>>();
        this.s_rejects_t = new HashMap<Long,List<String>>();
        this.t_accepts_sbj = new HashMap<String, List<String>>();
        this.min_t_capacity = new HashMap<String, Integer>();
        this.max_t_capacity = new HashMap<String,Integer>();
        this.s_in_g = new HashMap<Long, List<Long>>();
        this.g_of_sbj = new HashMap<Long, String>();
        this.g_in_t = new HashMap<Long,String>();
        this.parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(filepath));
            JSONObject jsonObject = (JSONObject) obj;
            read_all_students(jsonObject);
            read_all_groups(jsonObject);
            read_all_subjects(jsonObject);
            read_all_timeslots(jsonObject);
            read_s_has_f(jsonObject);
            read_s_rejects_t(jsonObject);
            read_t_capacity(jsonObject);
            read_t_accpets_sbj(jsonObject);
            read_g_of_sbj(jsonObject);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public Map<Long, Integer> get_all_students() {
        return all_students;
    }

    public Map<String, Integer> get_all_subjects() {
        return all_subjects;
    }

    public Map<String, Integer> get_all_timeslots() {
        return all_timeslots;
    }

    public Map<Long, Integer> get_all_groups(){
        return all_groups;
    }

    public int[][] get_s_in_sbj_indexed() {
        return this.s_in_sbj_indexed;
    }

    public int[][] get_s_rejects_t_indexed() {
        return this.s_rejects_t_indexed;
    }

    public int[][] get_t_accepts_sbj_indexed() {
        return this.t_accepts_sbj_indexed;
    }

    public int[] get_min_t_capacity_indexed(){
        return this.min_t_capacity_indexed;
    }

    public int[] get_max_t_capacity_indexed(){
        return this.max_t_capacity_indexed;
    }

    public int [] get_g_of_sbj_indexed() {
        return this.g_of_sbj_indexed;
    }

    public Map<String, Integer> get_min_t_capacity() {
        return min_t_capacity;
    }

    public Map<String, Integer> get_max_t_capacity() {
        return max_t_capacity;
    }

    public Map<Long,List<String>> get_s_in_sbj(){
        return this.s_in_sbj;
    }

    public Map<Long, List<String>> get_s_rejects_t() {
        return s_rejects_t;
    }

    public Map<String, List<String>> get_t_accepts_sbj(){
        return t_accepts_sbj;
    }

    public Map<Long, String> get_g_of_sbj(){
        return g_of_sbj;
    }

    public Map<Long,List<Long>> get_s_in_g(){
        return this.s_in_g;
    }

    public Map<Long, String> get_g_in_t() {
        return this.g_in_t;
    }


    public void printContent(){
        System.out.println(all_students.size());
        System.out.println(all_subjects.size());
        System.out.println(all_timeslots.size());
    }

    private void read_all_students(JSONObject j_obj){
        int id_index = 0;
        for(Object subject : (JSONArray)j_obj.get("subjects")){
            JSONObject j_subject = (JSONObject) subject;
            JSONArray students = (JSONArray)j_subject.get("students");
            for(int j=0; j<students.size(); j++){
                JSONObject j_student = (JSONObject) students.get(j);
                if(!all_students.containsKey((Long)j_student.get("id"))){
                    all_students.put((Long)j_student.get("id"), id_index);
                    id_index++;
                }
            }
        }
    }

    private void read_all_groups(JSONObject j_obj){
        int id_index = 0;
        for(Object group : (JSONArray)j_obj.get("groupOfSubject")){
            JSONObject j_group = (JSONObject) group;
            if(!all_groups.containsKey(j_group.get("g_id"))){
                all_groups.put((Long) j_group.get("g_id"), id_index);
                id_index++;
            }
        }
    }

    private void read_all_timeslots(JSONObject j_obj){
        int id_index = 0;
        for(Object timeslot : (JSONArray)j_obj.get("timeslots")){
            JSONObject j_timeslot = (JSONObject) timeslot;
            if(!all_timeslots.containsKey((String)j_timeslot.get("id"))){
                all_timeslots.put((String)j_timeslot.get("id"), id_index);
                id_index++;
            }
        }
    }

    private void read_all_subjects(JSONObject j_obj){
        int id_index = 0;
        for(Object subject : (JSONArray)j_obj.get("subjects")){
            JSONObject j_subject = (JSONObject) subject;
            if(!all_subjects.containsKey((String)j_subject.get("id"))){
                all_subjects.put((String)j_subject.get("id"), id_index);
                id_index++;
            }
        }
    }

    private void read_s_has_f(JSONObject j_obj){
        this.s_in_sbj_indexed = new int[all_students.size()][all_subjects.size()];
        //first fill array with 0's
        for(int i=0; i<all_students.size();i++){
            for(int j=0; j<all_subjects.size();j++){
                this.s_in_sbj_indexed[i][j] =0;
            }
        }
        //get information out of json and safe the information binary
        for(Object subject : (JSONArray)j_obj.get("subjects")){
            JSONObject j_subject = (JSONObject) subject;
            for(Object student : (JSONArray)j_subject.get("students")){
                JSONObject j_student = (JSONObject)student;
                Long sid = (Long) j_student.get("id");
                this.s_in_sbj_indexed[all_students.get(sid)][all_subjects.get((String)j_subject.get("id"))]=1;
                if(!this.s_in_sbj.containsKey(sid)){
                    this.s_in_sbj.put(sid,new LinkedList<String>());
                }
                this.s_in_sbj.get(sid).add((String)j_subject.get("id"));
            }
        }
    }

    private void read_t_capacity(JSONObject j_obj){
        this.min_t_capacity_indexed = new int[all_timeslots.size()];
        this.max_t_capacity_indexed = new int[all_timeslots.size()];

        for(Object timeslot : (JSONArray)j_obj.get("timeslots")){
            JSONObject j_timeslot = (JSONObject)timeslot;
            String t_id = (String)j_timeslot.get("id");
            int min_cap = Math.toIntExact((Long) j_timeslot.get("min_cap"));
            int max_cap = Math.toIntExact((Long)j_timeslot.get("max_cap"));

            this.min_t_capacity_indexed[all_timeslots.get(t_id)] = min_cap;
            this.max_t_capacity_indexed[all_timeslots.get(t_id)] = max_cap;

            this.min_t_capacity.put(t_id,min_cap);
            this.max_t_capacity.put(t_id,max_cap);
        }
    }

    private void read_g_of_sbj(JSONObject j_obj){
        this.g_of_sbj_indexed = new int[this.all_groups.size()];

        for(Object g_of_sbj : (JSONArray)j_obj.get("groupOfSubject")){
            JSONObject j_g_of_sbj =  (JSONObject)g_of_sbj;
            if(!this.g_of_sbj.containsKey((Long)j_g_of_sbj.get("g_id"))){
                this.g_of_sbj.put((Long)j_g_of_sbj.get("g_id"),(String)j_g_of_sbj.get("f_id"));
                this.g_of_sbj_indexed[this.all_groups.get((Long)j_g_of_sbj.get("g_id"))] =
                        all_subjects.get((String)j_g_of_sbj.get("f_id"));
            }
        }
    }

    private void read_t_accpets_sbj(JSONObject j_obj){
        this.t_accepts_sbj_indexed = new int[this.all_timeslots.size()][this.all_subjects.size()];

        for(int i=0; i<this.all_timeslots.size();i++){
            for(int j=0; j<this.all_subjects.size();j++){
                this.t_accepts_sbj_indexed[i][j]=0;
            }
        }
        for(Object timeslot : (JSONArray)j_obj.get("timeslots")){
            JSONObject j_timeslot = (JSONObject) timeslot;

            for(Object subject : (JSONArray)j_timeslot.get("subjects")){
                JSONObject j_subject = (JSONObject) subject;

                String subject_id = (String) j_subject.get("id");
                this.t_accepts_sbj_indexed[all_timeslots.get((String)j_timeslot.get("id"))][all_subjects.get(subject_id)] = 1;
                if(!this.t_accepts_sbj.containsKey(j_timeslot.get("id"))){
                    this.t_accepts_sbj.put((String)j_timeslot.get("id"), new LinkedList<String>());
                }
                this.t_accepts_sbj.get((String)j_timeslot.get("id")).add(subject_id);
            }
        }
    }


    // in der JSON müssen unter timeslots: ALLE timeslots existieren (s_rejects_t darf keinen timeslot ansprechen
    // welcher nicht unter timeslots aufgelistet ist)
    private void read_s_rejects_t(JSONObject j_obj){
        this.s_rejects_t_indexed = new int[all_students.size()][all_timeslots.size()];
        for(int i=0; i<all_students.size();i++){
            for(int j=0; j<all_timeslots.size();j++){
                this.s_rejects_t_indexed[i][j] =0;
            }
        }
        for(Object ut : (JSONArray)j_obj.get("unwanted_timeslots")){
            JSONObject j_ut = (JSONObject)ut;
            Long sid = (Long) j_ut.get("s_id");
            this.s_rejects_t_indexed[all_students.get(sid)][all_timeslots.get((String)j_ut.get("t_id"))]=1;
            if(!this.s_rejects_t.containsKey(sid)){
                this.s_rejects_t.put(sid,new LinkedList<String>());
            }
            this.s_rejects_t.get(sid).add((String)j_ut.get("t_id"));
        }
    }
/*
    private void read_s_in_g(JSONObject j_obj){
        for(Object s_in_g : (JSONArray)j_obj.get("studentInGroup")){
            JSONObject j_s_in_g = (JSONObject)s_in_g;
            if(!this.s_in_g.containsKey((Long)j_s_in_g.get("s_id"))){
                this.s_in_g.put((Long)j_s_in_g.get("s_id"),new LinkedList<Long>());
            }
            this.s_in_g.get((Long)j_s_in_g.get("s_id")).add((Long)j_s_in_g.get("g_id"));
        }
    }

    private void read_g_in_t(JSONObject j_obj){
        for(Object g_in_t : (JSONArray)j_obj.get("groupInTimeslot")){
            JSONObject j_g_in_t = (JSONObject) g_in_t;
            if(!this.g_in_t.containsKey((Long)j_g_in_t.get("g_id"))){
                this.g_in_t.put((Long)j_g_in_t.get("g_id"),(String)j_g_in_t.get("t_id"));
            }
        }
    }*/

}
