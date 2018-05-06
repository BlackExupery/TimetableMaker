package com.company;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class InputReader {

    private Map<Long,Integer> all_students;
    private Map<String,Integer> all_timeslots;
    private Map<String,Integer> all_subjects;
    private int [][] s_has_f;
    private int [][] s_rejects_t;
    private JSONParser parser;

    private void fill_s_has_f(JSONObject jObject){
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
                s_has_f[all_students.get((Long)student.get("id"))][all_subjects.get((String)subject.get("id"))]=1;
            }
        }
    }


    private void fill_s_rejects_t(JSONObject jObject){
        s_rejects_t = new int[all_students.size()][all_timeslots.size()];
        for(int i=0; i<all_students.size();i++){
            for(int j=0; j<all_timeslots.size();j++){
                s_rejects_t[i][j] =0;
            }
        }

        JSONArray unwanted_timeslots = (JSONArray)jObject.get("unwanted_timeslots");
        for(int i=0; i<unwanted_timeslots.size();i++){
            JSONObject ut = (JSONObject)unwanted_timeslots.get(i);
            s_rejects_t[all_students.get((Long)ut.get("s_id"))][all_timeslots.get((String)ut.get("ts_id"))]=1;
        }
    }

    private void getAllStudents(JSONObject jObject){
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

    private void getAllTimeslots(JSONObject jObject){
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

    private void getAllSubjects(JSONObject jObject){
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

    public InputReader(String filepath){
        this.all_students = new HashMap<Long,Integer>();
        this.all_timeslots= new HashMap<String,Integer>();
        this.all_subjects = new HashMap<String,Integer>();
        this.parser = new JSONParser();
        try {
            Object obj = parser.parse(new FileReader(filepath));
            JSONObject jsonObject = (JSONObject) obj;
            getAllStudents(jsonObject);
            getAllSubjects(jsonObject);
            getAllTimeslots(jsonObject);
            fill_s_has_f(jsonObject);
            fill_s_rejects_t(jsonObject);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public Map<Long, Integer> getAll_students() {
        return all_students;
    }

    public Map<String, Integer> getAll_subjects() {
        return all_subjects;
    }

    public Map<String, Integer> getAll_timeslots() {
        return all_timeslots;
    }

    public int[][] get_s_has_f(){
        return this.s_has_f;
    }

    public int[][] get_s_rejects_t(){
        return this.s_rejects_t;
    }

    public void printContent(){
		System.out.println(all_students.size());
		System.out.println(all_subjects.size());
		System.out.println(all_timeslots.size());
    }


}
