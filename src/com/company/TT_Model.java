package com.company;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class TT_Model extends Model{

   //private Model model;
    private IntVar[][][] s_in_g_of_sbj;
    private IntVar[][] s_in_g;
    private IntVar[] g_in_t;
    private IntVar[][][] s_in_g_in_t;
    private IntVar[][] s_rej_t;
    private IntVar[][] s_in_sbj;
    private IntVar[] g_of_sbj;
    private IntVar[] sbj_min_cap;
    private IntVar[] sbj_max_cap;
    private IntVar[][] t_accepts_sbj;

    private int total_students;
    private int total_groups;
    private int total_timeslots;
    private int total_subjects;

    public TT_Model(InputReader input_reader){
        init_model(input_reader);
    }


    public IntVar[][][] get_s_in_g_of_sbj() {
        return this.s_in_g_of_sbj;
    }

    public IntVar[][][] get_s_in_g_in_t() {
        return this.s_in_g_in_t;
    }

    public IntVar[][] get_s_in_g() {
        return this.s_in_g;
    }

    public IntVar[][] get_s_in_sbj() {
        return this.s_in_sbj;
    }

    public IntVar[][] get_t_accepts_sbj() {
        return this.t_accepts_sbj;
    }

    public IntVar[][] get_s_rej_t() {
        return this.s_rej_t;
    }

    public IntVar[] get_g_in_t() {
        return this.g_in_t;
    }

    public IntVar[] get_g_of_sbj() {
        return this.g_of_sbj;
    }

    public IntVar[] get_t_max_cap() {
        return this.sbj_max_cap;
    }

    public IntVar[] get_t_min_cap() {
        return this.sbj_min_cap;
    }

    public int get_total_students(){
        return this.total_students;
    }

    public int get_total_groups(){
        return this.total_groups;
    }

    public int get_total_timeslots(){
        return this.total_timeslots;
    }

    public int get_total_subjects(){
        return this.total_subjects;
    }

    private void init_model(InputReader input_reader){
        //model = new Model("Timetable-Solver");
        total_students = input_reader.get_all_students().size();
        total_timeslots = input_reader.get_all_timeslots().size();
        total_subjects = input_reader.get_all_subjects().size();
        total_groups = input_reader.get_all_groups().size();

        s_in_g_of_sbj = new IntVar[total_students][total_groups][total_subjects];


        s_in_g = new IntVar[total_students][total_groups];


        g_in_t = new IntVar[total_groups];

        t_accepts_sbj = new IntVar[total_timeslots][total_subjects];


        s_in_g_in_t = new IntVar[total_students][total_groups][total_timeslots];


        s_rej_t = new IntVar[total_students][total_timeslots];


        s_in_sbj = new IntVar[total_students][total_subjects];


        g_of_sbj = new IntVar[total_groups];


        sbj_min_cap = new IntVar[total_timeslots];


        sbj_max_cap = new IntVar[total_timeslots];

        /* Constraint-Variablen */
        //s_in_g_of_sbj
        for (int i = 0; i < total_students; i++) {
            for (int j = 0; j < total_groups; j++) {
                for (int k = 0; k < total_subjects; k++) {
                    s_in_g_of_sbj[i][j][k] = this.intVar(
                            "%s_ing_of_sbj[" + i + "][" + j + "][" + k + "]", 0, 1);
                }
            }
        }
        //s_in_g
        for (int i = 0; i < total_students; i++) {
            for (int j = 0; j < total_groups; j++) {
                s_in_g[i][j] = this.intVar("%s_in_g[" + i + "][" + j + "]", 0, 1);
            }
        }

        // if s_in_g is given in input
        if(input_reader.get_s_in_g().size()>0){
            for(Long s_id : input_reader.get_s_in_g().keySet()){
                int s_index = input_reader.get_all_students().get(s_id);
                for(Long g_id : input_reader.get_s_in_g().get(s_id)){
                    int g_index = input_reader.get_all_groups().get(g_id);
                    s_in_g[s_index][g_index] = this.intVar("%s_in_g[" + s_index + "][" + g_index + "]", 1);
                }
            }
        }

        // s_in_g_in_t
        for (int i = 0; i < total_students; i++) {
            for (int j = 0; j < total_groups; j++) {
                for (int k = 0; k < total_timeslots; k++) {
                    s_in_g_in_t[i][j][k] = this.intVar(
                            "%s_in_g_in_t[" + i + "][" + j + "][" + k + "]", 0, 1);
                }
            }
        }
        // s_rej_t
        for (int i = 0; i < total_students; i++) {
            for (int j = 0; j < total_timeslots; j++) {
                s_rej_t[i][j] = this.intVar(
                        "%s_rej_t[" + i + "][" + j + "]", input_reader.get_s_rejects_t_indexed()[i][j]);
            }
        }
        // g_in_t
        for (int i = 0; i < total_groups; i++) {
            g_in_t[i] = this.intVar("%g_in_t[" + i + "]", 0, total_timeslots-1);
        }


        for(int i=0; i< total_timeslots; i++){
            for(int j=0; j<total_subjects; j++){
                t_accepts_sbj[i][j] = this.intVar("%t_accepts_sbj[\" + i + \"][\" + j + \"]",
                        input_reader.get_t_accepts_sbj_indexed()[i][j]);
            }
        }

        // if g_in_t is given in input
        if(input_reader.get_g_in_t().size()>0){
            for(Long g_id : input_reader.get_g_in_t().keySet()){
                int g_index = input_reader.get_all_groups().get(g_id);
                int t_index = input_reader.get_all_timeslots().get(input_reader.get_g_in_t().get(g_id));
                g_in_t[g_index] = this.intVar("%g_in_t[" + g_index + "]", t_index);
            }
        }

        // g_of_sbj
        for (int i = 0; i < total_groups; i++) {
            g_of_sbj[i] = this.intVar("%g_of_f[" + i + "]", input_reader.get_g_of_sbj_indexed()[i]);
        }

        // s_in_sbj
        for (int i = 0; i < total_students; i++) {
            for (int j = 0; j < total_subjects; j++) {
                s_in_sbj[i][j] = this.intVar(
                        "%s_in_sbj[" + i + "][" + j + "]", input_reader.get_s_in_sbj_indexed()[i][j]);
            }
        }

        // sbj_min_cap, sbj_max_cap
        for (int i=0; i<total_timeslots;i++){
            sbj_min_cap[i] = this.intVar(input_reader.get_min_t_capacity_indexed()[i]);
            sbj_max_cap[i] = this.intVar(input_reader.get_max_t_capacity_indexed()[i]);
        }
    }


}
