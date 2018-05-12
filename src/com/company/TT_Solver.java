package com.company;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class TT_Solver {


    InputReader input;

    public TT_Solver(InputReader ir){
        this.input = ir;
    }


    public SolutionConverter solve() {
        Model model = new Model("usefulmodel");

        int STUDENTS = this.input.getAll_students().size();
        int GROUPS = 9;
        int TIMESLOTS = this.input.getAll_timeslots().size();
        int SUBJECTS = this.input.getAll_subjects().size();
        int MAX_CAP = 11;
        int MIN_CAP = 0;

        int[] f_min_cap = this.input.get_min_g_capacity();
        int[] f_max_cap = this.input.get_max_g_capacity();

        IntVar[][][] s_in_g_of_f = new IntVar[STUDENTS][GROUPS][SUBJECTS];
        IntVar[][] s_in_g = new IntVar[STUDENTS][GROUPS];
        //IntVar[][] s_in_t = new IntVar[STUDENTS][TIMESLOTS];
        IntVar[] g_in_t = new IntVar[GROUPS];
        IntVar[][][] s_in_g_in_t = new IntVar[STUDENTS][GROUPS][TIMESLOTS];
        IntVar[][] s_rej_t = new IntVar[STUDENTS][TIMESLOTS];
        IntVar[][] _s_has_f = new IntVar[STUDENTS][SUBJECTS];
        IntVar[] _g_of_f = new IntVar[GROUPS];
        IntVar[] _f_min_cap = new IntVar[SUBJECTS];
        IntVar[] _f_max_cap = new IntVar[SUBJECTS];

        int[][] s_rejects_t = this.input.get_s_rejects_t();
        int[][] s_has_f = this.input.get_s_has_f();
        int[] g_of_f = {0, 1, 2, 0, 1, 2, 0, 1, 2};


        //initialize variables:
        for (int i = 0; i < STUDENTS; i++) {
            for (int j = 0; j < GROUPS; j++) {
                s_in_g[i][j] = model.intVar("%s_in_g[" + i + "][" + j + "]", 0, 1);
                for (int k = 0; k < SUBJECTS; k++) {
                    s_in_g_of_f[i][j][k] = model.intVar("%s_in_g_of_f[" + i + "][" + j + "][" + k + "]", 0, 1);
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
                s_rej_t[i][j] = model.intVar("%s_rej_t[" + i + "][" + j + "]", s_rejects_t[i][j]);
            }
        }
        for (int i = 0; i < GROUPS; i++) {
            g_in_t[i] = model.intVar("%g_in_t[" + i + "]", 0, TIMESLOTS);
            _g_of_f[i] = model.intVar("%g_of_f[" + i + "]", g_of_f[i]);
        }

        for (int i = 0; i < STUDENTS; i++) {
            for (int j = 0; j < SUBJECTS; j++) {
                _s_has_f[i][j] = model.intVar("%s_has_f[" + i + "][" + j + "]", s_has_f[i][j]);
            }
        }

        for (int i=0; i<SUBJECTS;i++){
            _f_min_cap[i] = model.intVar(f_min_cap[i]);
            _f_max_cap[i] = model.intVar(f_max_cap[i]);
        }

        //CONSTRAINTS!
        // a) wenn student einem fach zugeordnet ist, dann befindet er sich in genau einer der Gruppen
        // die diesem Fach zugeordnet sind
        for (int s = 0; s < STUDENTS; s++) {
            for (int g = 0; g < GROUPS; g++) {
                for (int f = 0; f < SUBJECTS; f++) {
                    IntVar[] abs = new IntVar[GROUPS];
                    for (int i = 0; i < GROUPS; i++) {
                        abs[i] = s_in_g_of_f[s][i][f];
                    }
                    model.ifThenElse(model.arithm(_s_has_f[s][f], "=", 1), model.sum(abs, "=", 1),
                            model.arithm(s_in_g_of_f[s][g][f], "=", 0));
                }
            }
        }

        //b) Wenn Student sich in einer Gruppe befindet und diese Gruppe auch dem entsprechenden Fach zugeordnet ist,
        //dann befindet sich der Student s in der Gruppe g, welcher dem Fach f zugeordnet ist. Ansonsten nicht!
        //(Denn jede Gruppe ist nur einem Fach zugeordnet, deshalb kann der Student keiner Gruppe zugeordnet sein,
        //die einem anderen Fach zugeordnet ist!
        for (int s = 0; s < STUDENTS; s++) {
            for (int g = 0; g < GROUPS; g++) {
                for (int f = 0; f < SUBJECTS; f++) {
                    model.ifThenElse(model.and(model.arithm(s_in_g[s][g], "=", 1), model.arithm(_g_of_f[g], "=", f)),
                            model.arithm(s_in_g_of_f[s][g][f], "=", 1), model.arithm(s_in_g_of_f[s][g][f], "=", 0));
                }

            }
        }

        //c) Wenn sich ein Student in einer Gruppe befindet, die in einem Fachzugeordnet ist, dann befindet
        // sie sich der Student auch in der Gruppe. (Beziehung zwischen s_in_g_of_f und s_in_g festlegen,
        // damit s_in_g_of_f nur dann zutrifft, wenn auch s_in_g zutrifft und umgekehrt
        for (int s = 0; s < STUDENTS; s++) {
            for (int g = 0; g < GROUPS; g++) {
                for (int f = 0; f < SUBJECTS; f++) {
                    model.ifThen(model.arithm(s_in_g_of_f[s][g][f], "=", 1),
                            model.arithm(s_in_g[s][g], "=", 1));
                }
            }
        }

        //d) Ein Student darf nur in einer Gruppe je Fach sich befinden!
        for (int s = 0; s < STUDENTS; s++) {
            for (int f = 0; f < SUBJECTS; f++) {
                IntVar[] abs = new IntVar[GROUPS];
                for (int i = 0; i < GROUPS; i++) {
                    abs[i] = model.intVar(0, 1);
                    model.ifThenElse(model.and(model.arithm(_g_of_f[i], "=", f), model.arithm(s_in_g[s][i], "=", 1))
                            , model.arithm(abs[i], "=", 1), model.arithm(abs[i], "=", 0));
                }
                model.sum(abs, "<=", 1).post();
            }
        }

        //e) gruppenkapazitäten einhalten!
       /* for (int s = 0; s < STUDENTS; s++) {
            for (int g = 0; g < GROUPS; g++) {
                IntVar[] abs = new IntVar[STUDENTS];
                for (int i = 0; i < STUDENTS; i++) {
                    abs[i] = s_in_g[i][g];
                }
                model.sum(abs, "<=", MAX_CAP).post();
                model.sum(abs, ">=", MIN_CAP).post();
            }
        }*/

        //e) gruppenkapazitäten je Fach einhalten!
        for(int s=0; s<STUDENTS;s++){
            for(int g=0; g<GROUPS;g++){
                IntVar[] abs = new IntVar[STUDENTS];
                for (int i = 0; i < STUDENTS; i++) {
                    abs[i] = s_in_g[i][g];
                }
                for(int f=0; f<SUBJECTS;f++){
                    model.ifThen(model.and(model.arithm(s_in_g[s][g],"=",1),model.arithm(_g_of_f[g],"=",f)),
                           model.sum(abs,"<=",_f_max_cap[f]) );
                    model.ifThen(model.and(model.arithm(s_in_g[s][g],"=",1),model.arithm(_g_of_f[g],"=",f)),
                            model.sum(abs,">=",_f_min_cap[f]) );
                }
            }

        }


        //f) wenn s_in_g dann auch s_in_g_in_t unter maximal einem timeslot
        for (int s = 0; s < STUDENTS; s++) {
            for (int g = 0; g < GROUPS; g++) {
                IntVar[] abs = new IntVar[TIMESLOTS];
                for (int i = 0; i < TIMESLOTS; i++) {
                    abs[i] = s_in_g_in_t[s][g][i];
                }
                model.sum(abs, "<=", 1).post();
                for (int t = 0; t < TIMESLOTS; t++) {
                    model.ifThenElse(model.and(model.arithm(s_in_g[s][g], "=", 1),
                            model.arithm(g_in_t[g], "=", t)),
                            model.arithm(s_in_g_in_t[s][g][t], "=", 1),
                            model.arithm(s_in_g_in_t[s][g][t], "=", 0));
                }
            }
        }

        //g) wenn Student einen Timeslot als nicht möglich markeirt hat, befindet er sich in keiner Gruppe in diesem Timeslot
        for (int s = 0; s < STUDENTS; s++) {
            for (int g = 0; g < GROUPS; g++) {
                for (int t = 0; t < TIMESLOTS; t++) {
                    model.ifThen(model.and(model.arithm(s_rej_t[s][t], "=", 1), model.arithm(g_in_t[g], "=", t)),
                            model.arithm(s_in_g[s][g], "=", 0));
                }
            }
        }

        Solution solution = model.getSolver().findSolution();
        SolutionConverter sc = new SolutionConverter(solution.toString(),g_of_f);
        return sc;
    }


}
