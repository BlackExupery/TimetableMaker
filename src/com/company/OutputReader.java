package com.company;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OutputReader {

    Map<Long,List<Long>> s_in_g = new HashMap<Long,List<Long>>();
    Map<Long,String> g_of_sbj = new HashMap<Long,String>();
    Map<Long,String> g_in_t = new HashMap<Long,String>();



    private void fill_s_in_g(JSONObject obj){
        JSONArray j_s_in_g = (JSONArray)obj.get("s_in_g");
        for(int i=0; i<j_s_in_g.size();i++){
            JSONObject sg = (JSONObject)j_s_in_g.get(i);
            if(!this.s_in_g.containsKey((Long)sg.get("s_id"))){
                this.s_in_g.put((Long)sg.get("s_id"),new LinkedList<Long>());
            }
            this.s_in_g.get((Long)sg.get("s_id")).add((Long)sg.get("g_id"));

        }
    }

    private void fill_g_of_sbj(JSONObject obj){
        JSONArray j_g_of_sbj = (JSONArray) obj.get("g_of_sbj");

        for(int i=0; i<j_g_of_sbj.size();i++){
            JSONObject gsbj = (JSONObject) j_g_of_sbj.get(i);
            if(!this.g_of_sbj.containsKey((Long)gsbj.get("g_id"))){
                g_of_sbj.put((Long)gsbj.get("g_id"),(String)gsbj.get("f_id"));
            }
        }
    }

    private void fill_g_in_t(JSONObject obj){
        JSONArray j_g_in_t = (JSONArray) obj.get("g_in_t");

        for(int i=0; i<j_g_in_t.size();i++){
            JSONObject gt = (JSONObject) j_g_in_t.get(i);
            if(!this.g_in_t.containsKey((Long)gt.get("g_id"))){
                this.g_in_t.put((Long)gt.get("g_id"),(String)gt.get("t_id"));
            }
        }
    }

    public void readFile(String path){
        JSONParser parser = new JSONParser();


        try {
            Object obj = null;
            try {
                obj = parser.parse(new FileReader(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject object = (JSONObject) obj;
            fill_s_in_g(object);
            fill_g_in_t(object);
            fill_g_of_sbj(object);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(s_in_g);
        System.out.println(g_in_t);
        System.out.println(g_of_sbj);

    }




}