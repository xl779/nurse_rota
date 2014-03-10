
/*
 * This is the main class of the staff rota 
 * the algorithm adds all the constraints and find a solution for the nurse rota
 * 
 */
package logic;

/**
 *
 * @author 
 * xiaoyang liu 
 */
//import Jacop library 
import JaCoP.core.*; 
import JaCoP.constraints.*; 
import JaCoP.search.*; 
import java.awt.Color;
import org.swiftgantt.*;
import org.swiftgantt.ui.TimeUnit;
import org.swiftgantt.model.*;
import org.swiftgantt.common.*;
import java.util.*;


/**
 *definition of the class StaffRota 
 */
public class StaffRota{ 
 
    //create StaffRota object
    static StaffRota m = new StaffRota(); 
    
    //main method 
    public static void main (String[] args) { 
        Store store = new Store();  // define FD store  

        /* define finite domain variables*/
        // v[i][j] i means the nurse number j means the work shift number 
        //in the code v[i][j] needs to be transformed to v[21*m+n] because the jacop search only supports one dimension array  
        int i= 10;//means the rota is for ten nurses 
        int j= 21;//21 means there are 21 work slots in a week 
        
        //define all the varaibles in the constraint programming problem
        IntVar[] v = new IntVar[i*j];
        
        //construct integer varibale for each workshift of nurse nurse i
        for(int m=0;m<i;m++){
            //constrcut Intvar for the work shifts of nurse i
            for(int n=0;n<j;n++){
                //vm-n indicates value v 
                //intvar's value is either 0 or 1 
                v[j*m+n]=new IntVar(store,"v"+m+"-"+n,0,1);
            
            }
            
        }
      

        //define constraints
        
        /*
         * constraint 1:
         * the time between anyone's 2 consecutive shifts(C=the minimum time to rest) should be at least 2 work shifts 
         *
         */
        
        //construct array looply to implement Vij+Vij+1+Vij+2<=1
        for(int m=0;m<i;m++){
            //sum1 indicates <=1
            
            /*
             * each constraint Sum requires a new IntVar Array and A new sum1 varaible
             * so the definition of Intvar sum1 should be in the loop 
             */
            
            IntVar sum1=new IntVar(store,"s",0,1);
            //n means work shifts 
            for(int n=0;n<j-2;n++){
                //impose all the constraints 
                //the constraint specify that the sum of any 3 consecutive shifts is equal to or less than 1
                //for every nurse its 3 consecutive shifts can't be more than 1 
                store.impose(new Sum(new IntVar[]{v[21*m+n],v[21*m+n+1],v[21*m+n+2]},sum1));
              
            }
        }
       
      
     
        
        /*
         * constraint 2 
         * there must be 2 nurses working in a specific work shift 
         * so it's sum(v[i][1]) for all i = 2
         *
         */ 
        
        
        //impose the constraints looply
        //p indicates the work shift 
        for(int p=0;p<j;p++){
            //n2 means the number that each work shift requires 
            IntVar n2=new IntVar(store,"n",3,5);
           
            //for(int m=0;m<i;m++){
                //initialisation for constraint2
                
                store.impose(new Sum(new IntVar[]{v[p],v[j*1+p],v[j*2+p],
                v[j*3+p],v[j*4+p],v[j*5+p],
                v[j*6+p],v[j*7+p],v[j*8+p],
                v[j*6+p]
                },n2));
           
            //}    
        }
      
       
        
        /*
         * constraint 3 
         * a nurse's working hours in a week should be less than 5 work shifts 
         *
         */
        //p means the nurse number 
        for(int p=0;p<i;p++){
                //workshifts_per_nurse means the number of work shifts per nurse in a week 
                IntVar workshifts_per_nurse=new IntVar(store,"workpernurse",5,10);
                //the 21 values for each nurse sum up to an array and impose the sum constraints
                store.impose(new Sum(new IntVar[]{v[21*p],v[21*p+1],v[21*p+2],
                    v[21*p+3],v[21*p+4],v[21*p+5],
                    v[21*p+6],v[21*p+7],v[21*p+8],
                    v[21*p+9],v[21*p+10],v[21*p+11],
                    v[21*p+12],v[21*p+13],v[21*p+14],
                    v[21*p+15],v[21*p+16],v[21*p+17],
                    v[21*p+18],v[21*p+19],v[21*p+20]
                        
                },workshifts_per_nurse));   
        } 
        
        // search for a solution and print results 
        //create a search (specify search methods searched in data space) , this is to specify how to perform labeling
        Search<IntVar> search = new DepthFirstSearch<IntVar>(); 
        //specify a strategy to specify an value to a variable.
        //specify how to select FDV for an assignment from the array of FDVs (v); this is decided explicitly here by InputOrderSelect class 
        // that selects FDVs using the specified order present in v array.
        //IndomainMin is  to assign values for each FDV from its domain
        SelectChoicePoint<IntVar> select = new InputOrderSelect<IntVar>(store, v, new IndomainMin<IntVar>()); 
        
        //Print all solutions
        search.setSolutionListener(new PrintOutListener<IntVar>());
        //specify to search all the solutions and record all the solutions 
        search.getSolutionListener().searchAll(true);
        search.getSolutionListener().recordSolutions(true);
        //get the result 
        boolean result = search.labeling(store, select); 
        //print out the solutions 
        if ( result ) {
         //search.printAllSolutions();
            System.out.println("Solution: " + v[0]+", "+v[1]+", "+v[2]); 
        }
        else 
            System.out.println("*** No"); 
        
        
        
        
       //generate gantt chart 
       //construct the main class 
        GanttChart gantt=new GanttChart();
        //set time unit 
        gantt.setTimeUnit(TimeUnit.Hour);
        //decorate the gantt chart 
        Config config=gantt.getConfig();
        config.setWorkingTimeBackColor(Color.yellow);
        config.setTimeUnitWidth(50);//set width for time unit 
        config.setWorkingHoursSpanOfDay(new int[]{0,23});
        //set true if you want to show accurate task bar
        config.setAllowAccurateTaskBar(true);
        
        //cretae data model "GanttModel" for Gantt chart, all tasks information you want to display in gantt chart component are via the model class
        GanttModel model =new GanttModel();
        //set start time and end time for schedule
        model.setKickoffTime(new Time(2014,0,1));
        model.setDeadline(new Time(2014,0,7));
        
        //create tasks 
        Task task1=new Task("task 1",new Time(2014,0,1),new Time(2014,0,2));
        //add tasks;
        model.addTask(task1);
        gantt.setModel(model);
        
    } 
}
