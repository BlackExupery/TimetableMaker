package com.company;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class TT_Constraints {

    // a) wenn student einem fach zugeordnet ist, dann befindet er sich in genau einer der Gruppen
    // die diesem Fach zugeordnet sind
    public static void assignStudentToGroupAcordingToSubject(Model model, IntVar[][][]s_in_g_of_sbj, IntVar s_has_sbj[][],
                                                             int totalStudents, int totalGroups, int totalSubjects){
        for (int s = 0; s < totalStudents; s++) {
            for (int g = 0; g < totalGroups; g++) {
                for (int f = 0; f < totalSubjects; f++) {
                    IntVar[] abs = new IntVar[totalGroups];
                    for (int i = 0; i < totalGroups; i++) {
                        abs[i] = s_in_g_of_sbj[s][i][f];
                    }
                    model.ifThenElse(model.arithm(s_has_sbj[s][f], "=", 1), model.sum(abs, "=", 1),
                            model.arithm(s_in_g_of_sbj[s][g][f], "=", 0));
                }
            }
        }
    }

    //b) Wenn Student sich in einer Gruppe befindet und diese Gruppe auch dem entsprechenden Fach zugeordnet ist,
    //dann befindet sich der Student s in der Gruppe g, welcher dem Fach f zugeordnet ist. Ansonsten nicht!
    //(Denn jede Gruppe ist nur einem Fach zugeordnet, deshalb kann der Student keiner Gruppe zugeordnet sein,
    //die einem anderen Fach zugeordnet ist!
    public static void s_in_g_to_s_in_g_of_f(Model model, IntVar[][][] s_in_g_of_sbj, IntVar[][]s_in_g, IntVar[] g_of_sbj,
                                             int totalStudents, int totalGroups, int totalSubjects){

        for (int s = 0; s < totalStudents; s++) {
            for (int g = 0; g < totalGroups; g++) {
                for (int f = 0; f < totalSubjects; f++) {
                    model.ifThenElse(model.and(model.arithm(s_in_g[s][g], "=", 1), model.arithm(g_of_sbj[g], "=", f)),
                            model.arithm(s_in_g_of_sbj[s][g][f], "=", 1), model.arithm(s_in_g_of_sbj[s][g][f], "=", 0));
                }

            }
        }
    }

    //c) Wenn sich ein Student in einer Gruppe befindet, die in einem Fachzugeordnet ist, dann befindet
    // sie sich der Student auch in der Gruppe. (Beziehung zwischen s_ing_of_sbj und s_in_g festlegen,
    // damit s_ing_of_sbj nur dann zutrifft, wenn auch s_in_g zutrifft und umgekehrt
    public static void s_in_g_of_f_to_s_in_g(Model model, IntVar[][][]s_in_g_of_sbj, IntVar[][]s_in_g,
                                             int totalStudents, int totalGroups, int totalSubjects){

        for (int s = 0; s < totalStudents; s++) {
            for (int g = 0; g < totalGroups; g++) {
                for (int f = 0; f < totalSubjects; f++) {
                    model.ifThen(model.arithm(s_in_g_of_sbj[s][g][f], "=", 1),
                            model.arithm(s_in_g[s][g], "=", 1));
                }
            }
        }
    }

    //d) Ein Student darf nur in einer Gruppe je Fach sich befinden!
    public static void studentInJustOneGroupPerSubject(Model model, IntVar[][]s_in_g, IntVar[]g_of_sbj,
                                                       int totalStudents, int totalGroups, int totalSubjects){
        for (int s = 0; s < totalStudents; s++) {
            for (int f = 0; f < totalGroups; f++) {
                IntVar[] abs = new IntVar[totalGroups];
                for (int i = 0; i < totalGroups; i++) {
                    abs[i] = model.intVar(0, 1);
                    model.ifThenElse(model.and(model.arithm(g_of_sbj[i], "=", f), model.arithm(s_in_g[s][i], "=", 1))
                            , model.arithm(abs[i], "=", 1), model.arithm(abs[i], "=", 0));
                }
                model.sum(abs, "<=", 1).post();
            }
        }
    }


    //e) gruppenkapazitäten je Fach einhalten!
    public static void abideGroupCapacity(Model model, IntVar[][]s_in_g, IntVar[] g_of_sbj, IntVar[]sbj_max_cap, IntVar[]sbj_min_cap,
                                          int totalStudents, int totalGroups, int totalSubjects){
        for(int s=0; s<totalStudents;s++){
            for(int g=0; g<totalGroups;g++){
                IntVar[] abs = new IntVar[totalStudents];
                for (int i = 0; i < totalStudents; i++) {
                    abs[i] = s_in_g[i][g];
                }
                for(int f=0; f<totalSubjects;f++){
                    model.ifThen(model.and(model.arithm(s_in_g[s][g],"=",1),model.arithm(g_of_sbj[g],"=",f)),
                            model.sum(abs,"<=",sbj_max_cap[f]) );
                    model.ifThen(model.and(model.arithm(s_in_g[s][g],"=",1),model.arithm(g_of_sbj[g],"=",f)),
                            model.sum(abs,">=",sbj_min_cap[f]) );
                }
            }
        }
    }

    //f) wenn s_in_g dann auch s_in_g_in_t unter maximal einem timeslot
    public static void setStudentInSameTimeslotAsItsGroup(Model model, IntVar[][][]s_in_g_in_t, IntVar[][]s_in_g, IntVar[] g_in_t,
                                                          int totalStudents, int totalGroups, int totalTimeslots){
        for (int s = 0; s < totalStudents; s++) {
            for (int g = 0; g < totalGroups; g++) {
                IntVar[] abs = new IntVar[totalTimeslots];
                for (int i = 0; i < totalTimeslots; i++) {
                    abs[i] = s_in_g_in_t[s][g][i];
                }
                model.sum(abs, "<=", 1).post();
                for (int t = 0; t < totalTimeslots; t++) {
                    model.ifThenElse(model.and(model.arithm(s_in_g[s][g], "=", 1),
                            model.arithm(g_in_t[g], "=", t)),
                            model.arithm(s_in_g_in_t[s][g][t], "=", 1),
                            model.arithm(s_in_g_in_t[s][g][t], "=", 0));
                }
            }
        }
    }

    //g) wenn Student einen Timeslot als nicht möglich markeirt hat, befindet er sich in keiner Gruppe in diesem Timeslot
    public static void cancelNotPossibleTimeslotsPerStudent(Model model, IntVar[][]s_in_g, IntVar[][]s_rej_t,IntVar[]g_in_t,
                                                            int totalStudents, int totalGroups, int totalTimeslots){
        for (int s = 0; s < totalStudents; s++) {
            for (int g = 0; g < totalGroups; g++) {
                for (int t = 0; t < totalTimeslots; t++) {
                    model.ifThen(model.and(model.arithm(s_rej_t[s][t], "=", 1), model.arithm(g_in_t[g], "=", t)),
                            model.arithm(s_in_g[s][g], "=", 0));
                }
            }
        }
    }



}


