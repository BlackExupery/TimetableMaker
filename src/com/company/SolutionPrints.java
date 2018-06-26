package com.company;


public class SolutionPrints {

    private String solution;

    public SolutionPrints(String s){
        this.solution = s;
    }

    public void print_variable(String v_id,int line_length){
        String [] solution_str = solution.split(" ");
        String output = "";
        int line_index = 1;
        for(int i=0; i<solution_str.length;i++){
            if(solution_str[i].contains(v_id)){
                if(line_index%line_length==0){
                    output +="\n";
                    line_index = 1;
                }
                output+=solution_str[i] + " ";
                line_index++;
            }
        }
        System.out.println(output);
    }

    public void print_schedule(String v_id, int line_length){
        String [] solution_str = solution.split(" ");
        String output = "";
        int line_index = 0;
        for(int i=0; i<solution_str.length;i++){
            if(solution_str[i].contains(v_id)&&!solution_str[i].contains("]=0")){
                if(line_index%line_length==0){
                    output +="\n";
                    line_index = 1;
                }
                output+= solution_str[i] + " ";
                line_index++;
            }
        }
        System.out.println(output+"\n");
    }


}
