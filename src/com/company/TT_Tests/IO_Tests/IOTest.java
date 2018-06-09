package com.company.TT_Tests.IO_Tests;
import org.junit.Assert;
import org.junit.Test;
import com.company.InputReader;
import com.company.OutputReader;
import java.nio.file.Paths;
/**/

public class IOTest {



    public boolean correctInputTest() {
        InputReader ir = new InputReader(Paths.get(".").toAbsolutePath().normalize().toString()+ "/src/com/company/TT_Tests/IO_Tests/inputtest.json");

        if (!(ir.get_map_s_has_f().get(100L).contains("Algorithms 16_17") &&
                ir.get_map_s_has_f().get(100L).contains("Algorithms 16_17") &&
                ir.get_map_s_has_f().get(100L).contains("Maths II. 16_17") &&
                ir.get_map_s_has_f().get(100L).contains("Maths II. 16_17"))) {
            System.out.println("Error with reading s_has_sbj relationship.");
            return false;
        }

        if (!(  ir.get_map_max_g_capacity().get("Algorithms 16_17").equals(11) &&
                ir.get_map_min_g_capacity().get("Algorithms 16_17").equals(0) &&
                ir.get_map_max_g_capacity().get("Maths II. 16_17").equals(10) &&
                ir.get_map_min_g_capacity().get("Maths II. 16_17").equals(1))) {
            System.out.println("Error with reading min/max Group capacity.");
            return false;
        }

        if (!(ir.getAllTimeslots().containsKey("Mo 1.") &&
                ir.getAllTimeslots().containsKey("Mo 2."))) {
            System.out.println("Error with counting Timeslots.");
            return false;
        }

        if(!(   ir.get_map_s_rejects_t().get(100L).contains("Mo 1.")&&
                ir.get_map_s_rejects_t().get(101L).contains("Mo 2."))){
            System.out.println("Error with reading s_rejects_t relationship.");
            return false;
        }

        return true;
    }


    public boolean correctOutputTest(){
        OutputReader or = new OutputReader();
        or.readFile(Paths.get(".").toAbsolutePath().normalize().toString()+ "/src/com/company/TT_Tests/IO_Tests/outputtest.json");
        if(! (  or.get_s_in_g().get(100L).contains(1L)&&
                or.get_s_in_g().get(100L).contains(2L))){

            System.out.println("Error in reading s_in_g relationship.");
            return false;
        }

        if(! (  or.get_g_of_sbj().get(0L).equals("Algorithms 16_17")&&
                or.get_g_of_sbj().get(1L).equals("Maths II. 16_17"))){
            System.out.println("Error with reading g_of_sbj relationship.");
            return false;
        }

        if(! (  or.get_g_in_t().get(0L).equals("Di 3.")&&
                or.get_g_in_t().get(1L).equals("Di 4."))){
            System.out.println("Error with g_in_t relationship.");
            return false;
        }

        return true;
    }

    @Test
    public void firstTest() {
        Assert.assertTrue(correctInputTest());
    }

    @Test
    public void outputTest(){
        Assert.assertTrue((correctOutputTest()));
    }
}
