package com.company;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class SolutionConverter {

    private Map<Integer, Map<Integer,Map<Integer,Integer>>> s_in_g_of_f = new HashMap<Integer, Map<Integer,Map<Integer,Integer>>>();
    private Map<Integer,Map<Integer,Integer>> s_in_g = new HashMap<Integer,Map<Integer,Integer>>();
    private Map<Integer,Map<Integer,Integer>> s_has_f = new HashMap<Integer,Map<Integer,Integer>>();
    private Map<Integer,Integer> g_of_f = new HashMap<Integer,Integer>();
    private Map<Integer,Integer> g_in_t = new HashMap<Integer,Integer>();
    private Map<Integer,Map<Integer,Map<Integer,Integer>>> s_in_g_in_t = new HashMap<Integer, Map<Integer,Map<Integer,Integer>>>();

    private List<Integer> getValues(String input) {
        List<Integer> values = new LinkedList<Integer>();

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c > 47 && c < 58) {
                int index = i;
                StringBuilder sb = new StringBuilder(input.length());
                while (index<input.length()&& input.charAt(index)> 47 && input.charAt(index) < 58) {
                    sb.append(input.charAt(index));
                    index++;
                }
                i = index;
                values.add(Integer.parseInt(sb.toString()));
            }
        }
        return values;
    }

    private void writeSolutionInMap(String solution){
            List<Integer> vals = getValues(solution);

            if( solution.contains("%s_in_g_of_f[") && solution.contains("=1")){
                if(!s_in_g_of_f.containsKey(vals.get(0))){
                    s_in_g_of_f.put(vals.get(0),new HashMap<Integer,Map<Integer,Integer>>());
                }
                if(!s_in_g_of_f.get(vals.get(0)).containsKey(vals.get(1))){
                    s_in_g_of_f.get(vals.get(0)).put(vals.get(1),new HashMap<Integer,Integer>());
                }
                s_in_g_of_f.get(vals.get(0)).get(vals.get(1)).put(vals.get(2),vals.get(3));

            }
            else if(solution.contains("%s_in_g[")&& solution.contains("=1")){
                if(!s_in_g.containsKey(vals.get(0))){
                    s_in_g.put(vals.get(0),new HashMap<Integer,Integer>());
                }
                s_in_g.get(vals.get(0)).put(vals.get(1),vals.get(2));
            }
            else if(solution.contains("%s_has_f[")&& solution.contains("=1")){
                if(!s_has_f.containsKey(vals.get(0))){
                    s_has_f.put(vals.get(0),new HashMap<Integer,Integer>());
                }
                s_has_f.get(vals.get(0)).put(vals.get(1),vals.get(2));
            }
            else if( solution.contains("%g_of_f[")){
                g_of_f.put(vals.get(0),vals.get(1));
            }
            else if(solution.contains("%g_in_t[")){
                g_in_t.put(vals.get(0),vals.get(1));
            }
            else if(solution.contains("%s_in_g_in_t[")&& solution.contains("=1")){
                if(!s_in_g_in_t.containsKey(vals.get(0))){
                    s_in_g_in_t.put(vals.get(0),new HashMap<Integer,Map<Integer,Integer>>());
                }
                if(!s_in_g_in_t.get(vals.get(0)).containsKey(vals.get(1))){
                    s_in_g_in_t.get(vals.get(0)).put(vals.get(1),new HashMap<Integer,Integer>());
                }
                s_in_g_in_t.get(vals.get(0)).get(vals.get(1)).put(vals.get(2),vals.get(3));
            }
        }




    public SolutionConverter(Solution s){
        String [] solution_string = s.toString().split(" ");
        for(int i=0; i< solution_string.length;i++){
            writeSolutionInMap(solution_string[i]);
        }



    }

    public SolutionConverter(String s, int[]g_of_f_a){
        for(String solution : s.split(" ")){
            writeSolutionInMap(solution);
        }

        for(int i=0; i<g_of_f_a.length;i++){
            this.g_of_f.put(i,g_of_f_a[i]);
        }

        System.out.println("sgf: "+s_in_g_of_f.toString());
        System.out.println("sg: "+s_in_g.toString());
        System.out.println("sf: "+s_has_f.toString());
        System.out.println("sgt: "+s_in_g_in_t.toString());
        System.out.println("gf: "+g_of_f.toString());
        System.out.println("gt: "+g_in_t.toString());
    }

    public Map<Integer, Map<Integer, Integer>> get_s_in_g() {
        return this.s_in_g;
    }

    public Map<Integer, Integer> get_g_in_t() {
        return this.g_in_t;
    }

    public Map<Integer, Integer> get_g_of_f() {
        return this.g_of_f;
    }
}
