package com.company;

import java.util.List;
import java.util.Map;

public class TimeTableValidator {

    private InputReader inputdata;
    private OutputReader outputdata;

    public TimeTableValidator(InputReader ir, OutputReader or){
        this.inputdata = ir;
        this.outputdata = or;
    }

    public boolean check_s_has_f_condition(){

        Map<Long,List<String>> s_has_f = inputdata.get_map_s_has_f();
        Map<Long,List<Long>> s_in_g = outputdata.get_s_in_g();
        Map<Long,String> g_of_sbj= outputdata.get_g_of_sbj();

        //gehe jeden studenten s durch
        for(Long s : s_has_f.keySet()){

            //gehe jedes fach f durch, den student s besucht durch
            for(String sbj : s_has_f.get(s)){
                int gs_of_f =0;
                //gehe jede gruppe g durch
                for(Long g : g_of_sbj.keySet()){

                    //überprüfe ob g von fach f ist.
                    if(g_of_sbj.get(g).equals(sbj)){
                        //zähle wie viele gruppen g von fach f, student s besucht (Es muss genau 1 sein!)
                        if(s_in_g.get(s).contains(g)){
                            gs_of_f++;
                        }
                    }
                }
                // wenn student s in mehr oder weniger als einer Gruppe von fach f ist, in dem student s
                // angemeldet ist, ist es ein fehler!
                if(gs_of_f != 1){
                    return false;
                }

            }

        }
        //wenn alle studenten fehlerfrei untersucht wurden, ist die s_in_g-Voraussetzung erfüllt.
        return true;
    }


    public boolean check_s_rej_t_condition(){

        Map<Long,List<String>> s_rej_t = inputdata.get_map_s_rejects_t();
        Map<Long,List<Long>> s_in_g = outputdata.get_s_in_g();
        Map<Long,String> g_in_t= outputdata.get_g_in_t();

        //gehe jeden studenten s durch
        for(Long s : s_rej_t.keySet()){

            //gehe jeden timeslot t durch, den student s abgesagt hat durch
            for(String t : s_rej_t.get(s)){
                int gs_in_t =0;
                //gehe jede gruppe g durch
                for(Long g : g_in_t.keySet()){
                    //überprüfe ob g in timeslot t ist.
                    if(g_in_t.get(g).equals(t)){
                        //zähle wie viele gruppen g in t, student s besucht (Es muss genau 0 sein!)
                        if(s_in_g.get(s).contains(g)){
                            gs_in_t++;
                        }
                    }
                }
                // wenn student s in mehr oder weniger als einer Gruppe von fach f ist, in dem student s
                // angemeldet ist, ist es ein fehler!
                if(gs_in_t != 0){
                    return false;
                }

            }

        }
        //wenn alle studenten fehlerfrei untersucht wurden, ist die s_in_g-Voraussetzung erfüllt.
        return true;
    }

    public boolean check_groupCapacity_condition(){
        Map<String,Integer> min_cap = inputdata.get_map_min_g_capacity();
        Map<String,Integer> max_cap = inputdata.get_map_max_g_capacity();
        Map<Long,String> g_of_sbj = outputdata.get_g_of_sbj();
        Map<String,Integer> all_subjects = inputdata.getAll_subjects();

        for(String sbj : all_subjects.keySet()){
            int g_count =0;
            for(Long g : g_of_sbj.keySet()){
                if(g_of_sbj.containsKey(sbj)){
                    g_count++;
                }

            }

            if(g_count<min_cap.get(sbj)){
                return false;
            }
            if(g_count>max_cap.get(sbj)){
                return false;
            }
        }


        return true;
    }


}
