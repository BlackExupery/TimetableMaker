package com.company;

import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.Map;
import com.company.TT_Solver;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappedSolution {

        private Map<Integer, Map<Integer,Map<Integer,Integer>>> s_in_g_of_sbj = new HashMap<Integer, Map<Integer,Map<Integer,Integer>>>();
        private Map<Integer,Map<Integer,Map<Integer,Integer>>> s_in_g_in_t = new HashMap<Integer, Map<Integer,Map<Integer,Integer>>>();
        private Map<Integer,Map<Integer,Integer>> s_in_g = new HashMap<Integer,Map<Integer,Integer>>();
        private Map<Integer,Map<Integer,Integer>> s_has_sbj = new HashMap<Integer,Map<Integer,Integer>>();
        private Map<Integer,Integer> g_of_sbj = new HashMap<Integer,Integer>();
        private Map<Integer,Integer> g_in_t = new HashMap<Integer,Integer>();
        private Solution solution;


        public MappedSolution(Solution solution, IntVar[][][]s_in_g_of_sbj, IntVar[][][]s_in_g_in_t,
                                  IntVar[][]s_in_g, IntVar[][]s_has_f, IntVar[]g_of_sbj, IntVar[]g_in_t){
            this.solution = solution;
            read_s_in_g_of_sbj(s_in_g_of_sbj);
            read_s_in_g_in_t(s_in_g_in_t);
            read_s_in_g(s_in_g);
            read_s_has_sbj(s_has_f);
            read_g_in_t(g_in_t);
            read_g_of_sbj(g_of_sbj);
            System.out.println("helo: " + this.s_in_g);
        }

        public Map<Integer, Map<Integer, Integer>> get_s_in_g() {
            return this.s_in_g;
        }

        public Map<Integer, Integer> get_g_in_t() {
            return this.g_in_t;
        }

        public Map<Integer, Integer> get_g_of_sbj() {
            return this.g_of_sbj;
        }


        private void read_s_in_g_of_sbj(IntVar[][][]s_in_g_of_sbj){
            for(int i=0; i< s_in_g_of_sbj.length;i++){
                if(!this.s_in_g_of_sbj.containsKey(i)){
                    this.s_in_g_of_sbj.put(i, new HashMap<Integer, Map<Integer, Integer>>());
                }
                for(int j=0; j<s_in_g_of_sbj[i].length;j++){
                    if(!this.s_in_g_of_sbj.get(i).containsKey(j)){
                        this.s_in_g_of_sbj.get(i).put(j,new HashMap<Integer,Integer>());
                    }
                    for(int k=0; k<s_in_g_of_sbj[i][j].length;k++){
                        if(solution.getIntVal(s_in_g_of_sbj[i][j][k])==1) {
                            this.s_in_g_of_sbj.get(i).get(j).put(k, solution.getIntVal(s_in_g_of_sbj[i][j][k]));
                        }
                    }
                }
            }
        }

        private void read_s_in_g_in_t(IntVar[][][]s_in_g_in_t){
            for(int i=0; i< s_in_g_in_t.length;i++){
                if(!this.s_in_g_in_t.containsKey(i)){
                    this.s_in_g_in_t.put(i, new HashMap<Integer, Map<Integer, Integer>>());
                }
                for(int j=0; j<s_in_g_in_t[i].length;j++){
                    if(!this.s_in_g_in_t.get(i).containsKey(j)){
                        this.s_in_g_in_t.get(i).put(j,new HashMap<Integer,Integer>());
                    }
                    for(int k=0; k<s_in_g_in_t[i][j].length;k++){
                        if(solution.getIntVal(s_in_g_in_t[i][j][k])==1) {
                            this.s_in_g_in_t.get(i).get(j).put(k, solution.getIntVal(s_in_g_in_t[i][j][k]));
                        }
                    }
                }
            }
        }

        private void read_s_in_g(IntVar[][]s_in_g){
            for(int i=0; i<s_in_g.length;i++){
                if(!this.s_in_g.containsKey(i)){
                    this.s_in_g.put(i,new HashMap<Integer, Integer>());
                }
                for(int j=0; j<s_in_g[i].length;j++){
                    if(solution.getIntVal(s_in_g[i][j])==1) {
                        this.s_in_g.get(i).put(j, solution.getIntVal(s_in_g[i][j]));
                    }
                }
            }
        }

        private void read_s_has_sbj(IntVar[][]s_has_sbj){
            for(int i=0; i<s_has_sbj.length;i++){
                if(!this.s_has_sbj.containsKey(i)){
                    this.s_has_sbj.put(i,new HashMap<Integer, Integer>());
                }
                for(int j=0; j<s_has_sbj[i].length;j++){
                    if(solution.getIntVal(s_has_sbj[i][j])==1) {
                        this.s_has_sbj.get(i).put(j, solution.getIntVal(s_has_sbj[i][j]));
                    }
                }
            }
        }

        private void read_g_of_sbj(IntVar[]g_of_sbj){
            for(int i=0; i<g_of_sbj.length;i++){
                this.g_of_sbj.put(i, solution.getIntVal(g_of_sbj[i]));
            }
        }

        private void read_g_in_t(IntVar[]g_in_t){
            for(int i=0; i<g_in_t.length;i++){
                this.g_in_t.put(i, solution.getIntVal(g_in_t[i]));
            }
        }


    }


