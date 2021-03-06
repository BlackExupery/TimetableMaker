package com.company.TT_Tests.SolutionTests;

import com.company.InputReader;
import com.company.OutputReader;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/*
* Anmerkung: -lass den TimeTableValidator so wie er ist und integriere ihn in JUnittests.
* - schreibe testfälle für die Pipelines (korrekte eingabe und ausgabe!)
* - schreibe testfälle um die Korrektheit des TimeTableValidators zu bestätigen.
*
    // ein Student darf sich nur 1x in einer Gruppe befinden. muss nicht überprüft werden, wegen Hash-Datenstruktur
    // eine Gruppe darf sich nur in genau 1 Zeitslot befinden. muss nicht überprüft werden wegen Hash-Datenstruktur!
* */

public class SolutionTest {

    private InputReader inputdata;
    private OutputReader outputdata;
   // private static String input = Paths.get(".").toAbsolutePath().normalize().toString()+ "/src/com/company/TT_Tests/SolutionTests/tt_input.json";
    //private static String output = Paths.get(".").toAbsolutePath().normalize().toString()+ "/src/com/company/TT_Tests/SolutionTests/tt_output.json";
    private static String input = "C:/Users/Tu/Desktop/tt_project/performancetest/tt_newinput.json";
    private static String output = "C:/Users/Tu/Desktop/tt_project/performancetest/tt_output.json";

    public void initializeIOReader(String inputPath, String outputPath){
       this.inputdata = new InputReader(inputPath);
       this.outputdata = new OutputReader();
       outputdata.readFile(outputPath);

   }

    public boolean check_s_has_f_condition(){
        Map<Long,List<String>> s_has_f = inputdata.get_s_in_sbj();
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



    // ein Student darf sich nur in einer Gruppe je Zeitslot befinden.
    public boolean check_unique_studentassignment_per_timeslot(){
        Map<Long,List<Long>> s_in_g = outputdata.get_s_in_g();
        Map<Long,String> g_in_t = outputdata.get_g_in_t();

        //gehe jeden Studenten durch
        for(Long s : s_in_g.keySet()){
            //gehe jeden Zeitslot durch
            for(String t : g_in_t.values()){
                int counter = 0;
                //überprüfe, wie viele Gruppen des Studentens zur selben Zeit stattfinden
                for(Long g : s_in_g.get(s)){
                    if(g_in_t.get(g).equals(t)){
                        counter++;
                    }
                    //Es darf nicht mehr als 1 Gruppe des Studenten zur selben Zeit stattfinden!
                    if(counter > 1){
                        return false;
                    }
                }

            }

        }
        return true;
    }

    public boolean check_s_rej_t_condition(){

        Map<Long,List<String>> s_rej_t = inputdata.get_s_rejects_t();
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

    public boolean checkGroupCapacityCondition(){
        Map<String,Integer> min_cap = inputdata.get_min_t_capacity();
        Map<String,Integer> max_cap = inputdata.get_max_t_capacity();
        Map<Long,String> g_in_t = outputdata.get_g_in_t();
        Map<Long,List<Long>> s_in_g= outputdata.get_s_in_g();

        // gehe alle gruppen durch
        for(Long g : g_in_t.keySet()) {

            int counter = 0;
            //gehe alle studenten durch
            for (Long s : s_in_g.keySet()) {
                //zähle wie viele studenten in gruppe g sind
                if(s_in_g.get(s).contains(g)){
                    counter++;
                }
            }

            if(counter > max_cap.get(g_in_t.get(g))){
                System.out.println("Gruppe: "+g+" hat zu viele Studenten");
                return false;
            }

            if(counter < min_cap.get(g_in_t.get(g))){
                return false;
            }
        }
        return true;
    }

    public boolean checkTimeslotAcceptsSubject(){
        Map<String, List<String>> t_accepts_sbj = inputdata.get_t_accepts_sbj();
        Map<Long, String> group_of_sbj = inputdata.get_g_of_sbj();
        Map<Long, String> g_in_t = outputdata.get_g_in_t();
        for(Long g : g_in_t.keySet()){
            if(!t_accepts_sbj.get(g_in_t.get(g)).contains(group_of_sbj.get(g))){
                return false;
            }
        }
        return true;
    }

    @Test
    public void checkAllConstraints(){
       initializeIOReader(input,output);
       Assert.assertTrue(check_s_has_f_condition());
       Assert.assertTrue(check_s_rej_t_condition());
       Assert.assertTrue(checkGroupCapacityCondition());
       Assert.assertTrue(check_unique_studentassignment_per_timeslot());
       Assert.assertTrue(checkTimeslotAcceptsSubject());
    }


}
