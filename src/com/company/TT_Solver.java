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
 *
 *
 *
 */

public class TT_Solver {

    private Model model;
    private IntVar[][][] s_in_g_of_sbj;
    private IntVar[][] s_in_g;
    private IntVar[] g_in_t;
    private IntVar[][][] s_in_g_in_t;
    private IntVar[][] s_rej_t;
    private IntVar[][] s_has_sbj;
    private IntVar[] g_of_sbj;
    private IntVar[] sbj_min_cap;
    private IntVar[] sbj_max_cap;

    private int[][] student_rejects_timeslot;
    private int[][] student_has_subject;
    // Gruppenerstellung muss automatisiert werden!!!!!!
    private int[] group_of_subject = {0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2, 0, 1, 2};
    int[] subject_min_cap;
    int[] subject_max_cap;

    private InputReader myIr;
    private Solution solution;
    private int STUDENTS;
    private int GROUPS;
    private int TIMESLOTS;
    private int SUBJECTS;

    public TT_Solver(InputReader ir){

        /*  INITIALISIERE VARIABLEN! */

        /*Reguläre Variablen*/
        model = new Model("Timetable-Solver");
        myIr = ir;
        STUDENTS = ir.getAllStudents().size();
        GROUPS = 15;
        TIMESLOTS = ir.getAllTimeslots().size();
        SUBJECTS = ir.getAllSubjects().size();

        subject_min_cap = ir.get_min_g_capacity();
        subject_max_cap = ir.get_max_g_capacity();

        s_in_g_of_sbj = new IntVar[STUDENTS][GROUPS][SUBJECTS];
        s_in_g = new IntVar[STUDENTS][GROUPS];
        g_in_t = new IntVar[GROUPS];
        s_in_g_in_t = new IntVar[STUDENTS][GROUPS][TIMESLOTS];
        s_rej_t = new IntVar[STUDENTS][TIMESLOTS];
        s_has_sbj = new IntVar[STUDENTS][SUBJECTS];
        g_of_sbj = new IntVar[GROUPS];
        sbj_min_cap = new IntVar[SUBJECTS];
        sbj_max_cap = new IntVar[SUBJECTS];

        student_rejects_timeslot = ir.get_s_rejects_t();
        student_has_subject = ir.get_s_has_f();

        /* Constraint-Variablen */
        for (int i = 0; i < STUDENTS; i++) {
            for (int j = 0; j < GROUPS; j++) {
                s_in_g[i][j] = model.intVar("%s_in_g[" + i + "][" + j + "]", 0, 1);
                for (int k = 0; k < SUBJECTS; k++) {
                    s_in_g_of_sbj[i][j][k] = model.intVar("%s_ing_of_sbj[" + i + "][" + j + "][" + k + "]", 0, 1);
                }
            }
        }
        for (int i = 0; i < STUDENTS; i++) {
            for (int j = 0; j < GROUPS; j++) {
                for (int k = 0; k < TIMESLOTS; k++) {
                    s_in_g_in_t[i][j][k] = model.intVar("%s_in_g_in_t[" + i + "][" + j + "][" + k + "]", 0, 1);
                }
            }
        }
        for (int i = 0; i < STUDENTS; i++) {
            for (int j = 0; j < TIMESLOTS; j++) {
                s_rej_t[i][j] = model.intVar("%s_rej_t[" + i + "][" + j + "]", student_rejects_timeslot[i][j]);
            }
        }
        for (int i = 0; i < GROUPS; i++) {
            g_in_t[i] = model.intVar("%g_in_t[" + i + "]", 0, TIMESLOTS);
            g_of_sbj[i] = model.intVar("%g_of_f[" + i + "]", group_of_subject[i]);
        }

        for (int i = 0; i < STUDENTS; i++) {
            for (int j = 0; j < SUBJECTS; j++) {
                s_has_sbj[i][j] = model.intVar("%s_has_f[" + i + "][" + j + "]", student_has_subject[i][j]);
            }
        }

        for (int i=0; i<SUBJECTS;i++){
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
        OutputWriter ow = new OutputWriter(solution,s_in_g,g_of_sbj,g_in_t,myIr);
        ow.writeInJSON("C:/Users/Tu/Desktop/tt_project/performancetest/tt_output.json");
    }


    private void useStaticConstraints(){
        TT_Constraints.abideGroupCapacity(model,s_in_g,g_of_sbj,sbj_max_cap,sbj_min_cap,STUDENTS,GROUPS,SUBJECTS);
        TT_Constraints.assignStudentToGroupAcordingToSubject(model,s_in_g_of_sbj,s_has_sbj,STUDENTS,GROUPS,SUBJECTS);
        // TT_Constraints.cancelNotPossibleTimeslotsPerStudent(model, s_in_g,s_rej_t,g_in_t,STUDENTS,GROUPS,TIMESLOTS);
        TT_Constraints.s_in_g_of_f_to_s_in_g(model,s_in_g_of_sbj,s_in_g,STUDENTS,GROUPS,SUBJECTS);
        TT_Constraints.s_in_g_to_s_in_g_of_f(model,s_in_g_of_sbj,s_in_g,g_of_sbj,STUDENTS,GROUPS,SUBJECTS);

        TT_Constraints.setStudentInSameTimeslotAsItsGroup(model,s_in_g_in_t,s_in_g,g_in_t,STUDENTS,GROUPS,TIMESLOTS);
        TT_Constraints.studentPerTimeslot(model, s_in_g_in_t,STUDENTS, GROUPS,TIMESLOTS);
        TT_Constraints.studentInJustOneGroupPerSubject(model,s_in_g_of_sbj,STUDENTS,GROUPS,SUBJECTS);
    }



}
