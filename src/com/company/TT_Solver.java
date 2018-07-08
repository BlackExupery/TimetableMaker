package com.company;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import static org.chocosolver.solver.search.strategy.Search.activityBasedSearch;

/*
 * TO DO'S
 *- Gruppenanzahl muss geschätzt werden.
 *
 *
 * */


/**
 * ENTWURFSNOTIZEN:
 * - Ein Interface welches eine unbestimmte Anzahl von parametern unterschiedlicher Datentypen ist nicht erstellbar
 * in Java, deshalb werde ich mir vornehmen, eine statische Klasse mit Constraint-Methoden zu entwerfen,
 * die dann aufgerufen werden.
 *
 * Dies ist notwendig, um ansteuern zu können, welche Constraints wir aktiviert haben wollen oder nicht in
 * der Anwendung.
 *
 * - Bin zufrieden mit den Einlesevorgang. (VariablenNamen umbenennen.)
 *
 */

public class TT_Solver {

    //Modellvariablen
    private Model model;
    private IntVar[][][] s_in_g_of_sbj;
    private IntVar[][] s_in_g;
    private IntVar[] g_in_t;
    private IntVar[][][] s_in_g_in_t;
    private IntVar[][] s_rej_t;
    private IntVar[][] s_in_sbj;
    private IntVar[] g_of_sbj;
    private IntVar[] sbj_min_cap;
    private IntVar[] sbj_max_cap;

    //Eingabevariablen
    private int[][] student_rejects_timeslot;
    private int[][] student_in_subject;
    private int[] group_of_subject ;
    int[] subject_min_cap;
    int[] subject_max_cap;

    private InputReader inputReader;
    private int totalStudents;
    private int totalGroups;
    private int totalTimeslots;
    private int totalSubjects;

    public TT_Solver(InputReader ir){

        /*  INITIALISIERE VARIABLEN! */

        /*Reguläre Variablen*/
        model = new Model("Timetable-Solver");
        inputReader = ir;
        totalStudents = ir.getAllStudents().size();
        totalTimeslots = ir.getAllTimeslots().size();
        totalSubjects = ir.getAllSubjects().size();
        totalGroups = ir.getAllGroups().size();

        subject_min_cap = ir.get_min_g_capacity();
        subject_max_cap = ir.get_max_g_capacity();
        group_of_subject = ir.get_g_of_sbj();
        student_rejects_timeslot = ir.get_s_rejects_t();
        student_in_subject = ir.get_s_has_f();


        s_in_g_of_sbj = new IntVar[totalStudents][totalGroups][totalSubjects];


        s_in_g = new IntVar[totalStudents][totalGroups];


        g_in_t = new IntVar[totalGroups];


        s_in_g_in_t = new IntVar[totalStudents][totalGroups][totalTimeslots];


        s_rej_t = new IntVar[totalStudents][totalTimeslots];


        s_in_sbj = new IntVar[totalStudents][totalSubjects];


        g_of_sbj = new IntVar[totalGroups];


        sbj_min_cap = new IntVar[totalSubjects];


        sbj_max_cap = new IntVar[totalSubjects];

        /* Constraint-Variablen */
        //s_in_g_of_sbj
        for (int i = 0; i < totalStudents; i++) {
            for (int j = 0; j < totalGroups; j++) {
                for (int k = 0; k < totalSubjects; k++) {
                    s_in_g_of_sbj[i][j][k] = model.intVar(
                            "%s_ing_of_sbj[" + i + "][" + j + "][" + k + "]", 0, 1);
                }
            }
        }
        //s_in_g
        for (int i = 0; i < totalStudents; i++) {
            for (int j = 0; j < totalGroups; j++) {
                s_in_g[i][j] = model.intVar("%s_in_g[" + i + "][" + j + "]", 0, 1);
            }
        }

        // if s_in_g is given in input
        if(ir.get_map_s_in_g().size()>0){
            for(Long s_id : ir.get_map_s_in_g().keySet()){
                int s_index = ir.getAllStudents().get(s_id);
                for(Long g_id : ir.get_map_s_in_g().get(s_id)){
                    int g_index = ir.getAllGroups().get(g_id);
                    s_in_g[s_index][g_index] = model.intVar("%s_in_g[" + s_index + "][" + g_index + "]", 1);
                }
            }
        }

        // s_in_g_in_t
        for (int i = 0; i < totalStudents; i++) {
            for (int j = 0; j < totalGroups; j++) {
                for (int k = 0; k < totalTimeslots; k++) {
                    s_in_g_in_t[i][j][k] = model.intVar(
                            "%s_in_g_in_t[" + i + "][" + j + "][" + k + "]", 0, 1);
                }
            }
        }
        // s_rej_t
        for (int i = 0; i < totalStudents; i++) {
            for (int j = 0; j < totalTimeslots; j++) {
                s_rej_t[i][j] = model.intVar(
                        "%s_rej_t[" + i + "][" + j + "]", student_rejects_timeslot[i][j]);
            }
        }
        // g_in_t
        for (int i = 0; i < totalGroups; i++) {
            g_in_t[i] = model.intVar("%g_in_t[" + i + "]", 0, totalTimeslots);
        }

        // if g_in_t is given in input
        if(ir.get_map_g_in_t().size()>0){
            for(Long g_id : ir.get_map_g_in_t().keySet()){
                int g_index = ir.getAllGroups().get(g_id);
                int t_index = ir.getAllTimeslots().get(ir.get_map_g_in_t().get(g_id));
                g_in_t[g_index] = model.intVar("%g_in_t[" + g_index + "]", t_index);
            }
        }

        // g_of_sbj
        for (int i = 0; i < totalGroups; i++) {
            g_of_sbj[i] = model.intVar("%g_of_f[" + i + "]", group_of_subject[i]);
        }

        // s_in_sbj
        for (int i = 0; i < totalStudents; i++) {
            for (int j = 0; j < totalSubjects; j++) {
                s_in_sbj[i][j] = model.intVar(
                        "%s_in_sbj[" + i + "][" + j + "]", student_in_subject[i][j]);
            }
        }

        // sbj_min_cap, sbj_max_cap
        for (int i=0; i<totalSubjects;i++){
            sbj_min_cap[i] = model.intVar(subject_min_cap[i]);
            sbj_max_cap[i] = model.intVar(subject_max_cap[i]);
        }
    }

    public void solve(String timeLimit){
        useStaticConstraints();
        long startTime = System.currentTimeMillis();
        model.getSolver().limitTime(timeLimit);
        model.getSolver().setSearch(activityBasedSearch(model.retrieveIntVars(true)));
        Solution solution = model.getSolver().findSolution();


        long stopTime = System.currentTimeMillis();
        System.out.println("Elapsed time: " + (stopTime-startTime));
        model.getSolver().printStatistics();
        if(solution == null){
            System.out.println("No Solution found");
        }
        OutputWriter ow = new OutputWriter(solution,s_in_g,g_in_t,inputReader);
        ow.writeInJSON("C:/Users/Tu/Desktop/tt_project/performancetest/tt_output.json");
    }


    private void useStaticConstraints(){
        TT_Constraints.abideGroupCapacity(model,s_in_g,g_of_sbj,sbj_max_cap,sbj_min_cap,totalStudents,totalGroups,totalSubjects);
        TT_Constraints.assignStudentToGroupAcordingToSubject(model,s_in_g_of_sbj,s_in_sbj,totalStudents,totalGroups,totalSubjects);
        // TT_Constraints.cancelNotPossibleTimeslotsPerStudent(model, s_in_g,s_rej_t,g_in_t,totalStudents,totalGroups,totalTimeslots);
        TT_Constraints.s_in_g_of_f_to_s_in_g(model,s_in_g_of_sbj,s_in_g,totalStudents,totalGroups,totalSubjects);
        TT_Constraints.s_in_g_to_s_in_g_of_f(model,s_in_g_of_sbj,s_in_g,g_of_sbj,totalStudents,totalGroups,totalSubjects);
        TT_Constraints.setStudentInSameTimeslotAsItsGroup(model,s_in_g_in_t,s_in_g,g_in_t,totalStudents,totalGroups,totalTimeslots);
        TT_Constraints.studentPerTimeslot(model, s_in_g_in_t,totalStudents, totalGroups,totalTimeslots);
        TT_Constraints.studentInJustOneGroupPerSubject(model,s_in_g_of_sbj,totalStudents,totalGroups,totalSubjects);
    }



}
