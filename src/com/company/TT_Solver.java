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
    //private Model model;

    private Solution current_solution;
    private TT_Model model;

    public TT_Solver(TT_Model model){
        this.model = model;
    }

    public Solution solve(String timeLimit, int constraint_mode){
        abideGroupCapacity();
        assignStudentToGroupAcordingToSubject();
        if(constraint_mode == 1) {
            cancelNotPossibleTimeslotsPerStudent();
        }
        s_in_g_of_f_to_s_in_g();
        s_in_g_to_s_in_g_of_f();
        setStudentInSameTimeslotAsItsGroup();
        studentPerTimeslot();
        studentInJustOneGroupPerSubject();
        setGroupInAcceptedTimeslot();
        model.getSolver().limitTime(timeLimit);
        model.getSolver().setSearch(activityBasedSearch(model.retrieveIntVars(true)));
        current_solution = model.getSolver().findSolution();
        model.getSolver().printStatistics();

        if(current_solution == null){
            System.out.println("No Solution found");
        }
        return current_solution;
    }

    /* a) Wenn ein Student einem Fach zugeordnet ist, dann befindet er sich in genau einer der Gruppen
   die diesem Fach zugeordnet sind.*/
    public void assignStudentToGroupAcordingToSubject(){

        for (int s = 0; s < model.get_total_students(); s++) {
            for (int g = 0; g < model.get_total_groups(); g++) {
                for (int f = 0; f < model.get_total_subjects(); f++) {
                    IntVar[] abs = new IntVar[model.get_total_groups()];
                    for (int i = 0; i < model.get_total_groups(); i++) {
                        abs[i] = model.get_s_in_g_of_sbj()[s][i][f];
                    }
                    model.ifThenElse(model.arithm(model.get_s_in_sbj()[s][f], "=", 1),
                            model.sum(abs, "=", 1),
                            model.arithm(model.get_s_in_g_of_sbj()[s][g][f], "=", 0));
                }
            }
        }

    }

    //b) Wenn Student sich in einer Gruppe befindet und diese Gruppe auch dem entsprechenden Fach zugeordnet ist,
    //dann befindet sich der Student s in der Gruppe g, welcher dem Fach f zugeordnet ist. Ansonsten nicht!
    //(Denn jede Gruppe ist nur einem Fach zugeordnet, deshalb kann der Student keiner Gruppe zugeordnet sein,
    //die einem anderen Fach zugeordnet ist!
    public void s_in_g_to_s_in_g_of_f(){
        for (int s = 0; s < model.get_total_students(); s++) {
            for (int g = 0; g < model.get_total_groups(); g++) {
                for (int f = 0; f < model.get_total_subjects(); f++) {
                    model.ifThenElse(model.and(model.arithm(model.get_s_in_g()[s][g], "=", 1),
                            model.arithm(model.get_g_of_sbj()[g], "=", f)),
                            model.arithm(model.get_s_in_g_of_sbj()[s][g][f], "=", 1),
                            model.arithm(model.get_s_in_g_of_sbj()[s][g][f], "=", 0));
                }

            }
        }
    }

    //c) Wenn sich ein Student in einer Gruppe befindet, die in einem Fachzugeordnet ist, dann befindet
    // sie sich der Student auch in der Gruppe. (Beziehung zwischen s_ing_of_sbj und s_in_g festlegen,
    // damit s_ing_of_sbj nur dann zutrifft, wenn auch s_in_g zutrifft und umgekehrt
    public void s_in_g_of_f_to_s_in_g(){

        for (int s = 0; s < model.get_total_students(); s++) {
            for (int g = 0; g < model.get_total_groups(); g++) {
                for (int f = 0; f < model.get_total_subjects(); f++) {
                    model.ifThen(model.arithm(model.get_s_in_g_of_sbj()[s][g][f], "=", 1),
                            model.arithm(model.get_s_in_g()[s][g], "=", 1));
                }
            }
        }

    }

    //d) Ein Student darf nur in einer Gruppe je Fach sich befinden!
    public void studentInJustOneGroupPerSubject(){


        for (int s = 0; s < model.get_total_students(); s++) {
            for (int f = 0; f < model.get_total_subjects(); f++) {
                IntVar[] abs = new IntVar[model.get_total_groups()];
                for (int i = 0; i < model.get_total_groups(); i++) {
                    abs[i] = model.get_s_in_g_of_sbj()[s][i][f];
                }
                model.sum(abs, "<=", 1).post();
            }
        }
    }


    //e) gruppenkapazitäten je Fach einhalten!
    public void abideGroupCapacity(){
        for(int g=0; g<model.get_total_groups();g++){
            IntVar[] abs = new IntVar[model.get_total_students()];
            for (int i = 0; i < model.get_total_students(); i++) {
                abs[i] = model.get_s_in_g()[i][g];
            }
            for(int t=0; t<model.get_total_timeslots();t++){
                model.ifThen(model.arithm(model.get_g_in_t()[g],"=",t),
                        model.sum(abs,"<=",model.get_t_max_cap()[t]));

                model.ifThen(model.arithm(model.get_g_in_t()[g],"=",t),
                        model.sum(abs,">=",model.get_t_min_cap()[t]));
            }
        }
    }

    //f) wenn s_in_g dann auch s_in_g_in_t unter maximal einem timeslot, ansonsten nicht.
    public void setStudentInSameTimeslotAsItsGroup(){
        for (int s = 0; s < model.get_total_students(); s++) {
            for (int g = 0; g < model.get_total_groups(); g++) {
                for (int t = 0; t < model.get_total_timeslots(); t++) {
                    model.ifThenElse(model.and(model.arithm(model.get_s_in_g()[s][g], "=", 1),
                            model.arithm(model.get_g_in_t()[g], "=", t)),
                            model.arithm(model.get_s_in_g_in_t()[s][g][t], "=", 1),
                            model.arithm(model.get_s_in_g_in_t()[s][g][t], "=", 0));
                }
            }
        }
    }

    //Eine Gruppe darf nur in einen Zeitslot gesetzt werden, wenn das Fach zu dem es gehört, vom Zeitslot
    // akzeptiert wird.

    public void setGroupInAcceptedTimeslot(){
        for(int f =0; f<model.get_total_subjects(); f++){
            for(int t=0; t<model.get_total_timeslots(); t++){
                for(int g=0; g<model.get_total_groups(); g++){
                    model.ifThen(
                            model.and(model.arithm(model.get_t_accepts_sbj()[t][f],"=",0),model.arithm(model.get_g_of_sbj()[g],"=",f)),
                            model.arithm(model.get_g_in_t()[g],"!=",t));
                }
            }
        }
    }

    //g) Ein Student darf sich in einem Timeslot nicht mehr als 1x aufhalten.

    public void studentPerTimeslot(){

        for(int s=0; s<model.get_total_students(); s++){
            for(int t =0; t<model.get_total_timeslots(); t++){
                IntVar[] abs = new IntVar[model.get_total_groups()];
                for(int i =0; i<model.get_total_groups(); i++){
                    abs[i] = model.get_s_in_g_in_t()[s][i][t];
                }
                model.sum(abs,"<=",1).post();
            }
        }


    }

    //h) wenn Student einen Timeslot als nicht möglich markeirt hat, befindet er sich in keiner Gruppe in diesem Timeslot
    public void cancelNotPossibleTimeslotsPerStudent(){

        for (int s = 0; s < model.get_total_students(); s++) {
            for (int g = 0; g < model.get_total_groups(); g++) {
                for (int t = 0; t < model.get_total_timeslots(); t++) {
                    model.ifThen(model.and(model.arithm(model.get_s_rej_t()[s][t], "=", 1),
                            model.arithm(model.get_g_in_t()[g], "=", t)),
                            model.arithm(model.get_s_in_g()[s][g], "=", 0));
                }
            }
        }
    }



}
