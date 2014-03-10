/*
 * This is the main class of the staff rota 
 * the algorithm adds all the constraints and find a solution for the nurse rota
 */
package logic;
/**
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
public class StaffRota2{ 
 
    //create StaffRota object
    //static StaffRota2 m = new StaffRota2(); 
    
    
    //declare all the variables 
    Store store;//the store to store all the constraints and variables
    int i;//nurse number 
    int j;//work shift number 
    //nurse numbers per work shifts
    int nurseNumberPerShift;
    //the range of number of work shifts that each nurse works 
    int minPerWeek;
    int maxPerWeek;
    
    
    
    IntVar[] v;
    
    //the construct method 
    public StaffRota2(int nurseNumber){
        store = new Store();  // define FD store  

        /* define finite domain variables*/
        // v[i][j] i means the nurse number j means the work shift number 
        //in the code v[i][j] needs to be transformed to v[21*m+n] because the jacop search only supports one dimension array  
        i= nurseNumber;
        j= 21;//21 means there are 21 work slots in a week 
        
        //define all the varaibles in the constraint programming problem
        v = new IntVar[i*j];
        
        //construct integer varibale for each workshift of nurse nurse i
        for(int m=0;m<i;m++){
            //constrcut Intvar for the work shifts of nurse i
            for(int n=0;n<j;n++){
                //vm-n indicates value v 
                //intvar's value is either 0 or 1 
                //convert the two-dimensional array v[m][n] to one-dimensional array v[j*m+n]
                v[j*m+n]=new IntVar(store,"v"+m+"-"+n,0,1);
            
            }
            
        }
     
        
    }//end of construct method   
       
    
    
    
     //define constraints
        
        /*
         * constraint 1:
         * the time between anyone's 2 consecutive shifts(C=the minimum time to rest) should be at least 2 work shifts 
         *
         */
        
       //construct array looply to implement Vij+Vij+1+Vij+2<=1
       //this function used to add three kinds of constrains
       //2. the time between anyone's 2 consecutive shifts
       //3. the time between anyone's 3 consecutive shifts
       //4. the time between anyone's 4 consecutive shifts
     public void setBreak(int breakNum){   
        
        
         if(breakNum==2){    
         /*set the break time between 2 consecutive workshifts is 4 work shifts*/
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
         }else if(breakNum==3){
              /*set the break time between 2 consecutive workshifts is 3 work shifts*/
             
             
             for(int m=0;m<i;m++){
            //sum1 indicates <=1
            
            /*
             * each constraint Sum requires a new IntVar Array and A new sum1 varaible
             * so the definition of Intvar sum1 should be in the loop 
             */
            IntVar sum1=new IntVar(store,"s",0,1);
            //n means work shifts 
            for(int n=0;n<j-3;n++){
                //impose all the constraints 
                //the constraint specify that the sum of any 3 consecutive shifts is equal to or less than 1
                //for every nurse its 3 consecutive shifts can't be more than 1 
                store.impose(new Sum(new IntVar[]{v[21*m+n],v[21*m+n+1],v[21*m+n+2],v[21*m+n+3]},sum1));
              
            }
           }
         
         
         
         
         
         }else{
             /*set the break time between 2 consecutive workshifts is 4 work shifts*/
             
             for(int m=0;m<i;m++){
            //sum1 indicates <=1
            
            /*
             * each constraint Sum requires a new IntVar Array and A new sum1 varaible
             * so the definition of Intvar sum1 should be in the loop 
             */
            IntVar sum1=new IntVar(store,"s",0,1);
            //n means work shifts 
            for(int n=0;n<j-4;n++){
                //impose all the constraints 
                //the constraint specify that the sum of any 3 consecutive shifts is equal to or less than 1
                //for every nurse its 3 consecutive shifts can't be more than 1 
                store.impose(new Sum(new IntVar[]{v[21*m+n],v[21*m+n+1],v[21*m+n+2],v[21*m+n+3],v[21*m+n+4]},sum1));
              
            }
           }
         
         
         
         
         
         
         
         }
         
         
       
     }
       
    
    //the function to call to specify the nurse numbers to i
 /*   public void specifyNurseNumber(int nurseNumber){
        i=nurseNumber;
    
    }
   */ 
    
    //the function to set the nurse number that each work shift requires
    public void setNurseNumberPerShift(int nurseNumber){
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
            IntVar n2=new IntVar(store,"n",nurseNumber,nurseNumber);
            //create an arraylist to store all the values of nurses for a single work shift 
            ArrayList<IntVar> nurses=new ArrayList<IntVar>();
            
            //looply add nurses variable(intVariale) to arraylist
            //m< the number of nurses
            for(int m=0;m<i;m++){
                nurses.add(v[j*m+p]);
            
            }
            
            //convert the arraylist to an array
            IntVar[] nurse_array=new IntVar[nurses.size()];
            nurse_array=nurses.toArray(nurse_array);
            //for(int m=0;m<i;m++){
            //initialisation for constraint2
                
            store.impose(new Sum(nurse_array,n2));
           
            //}    
        }
      
    
    }
   
    
    //the function set the range of number of work shifts each nurse works per week
    
    public void setWorkRangePerWeek(int min,int max){
         /*
         * constraint 3 
         * a nurse's working hours in a week should be less than 5 work shifts 
         *
         */
        //p means the nurse number 
        for(int p=0;p<i;p++){
                //workshifts_per_nurse means the number of work shifts per nurse in a week 
                IntVar workshifts_per_nurse=new IntVar(store,"workpernurse",min,max);
                //the 21 values for each nurse sum up to an array and impose the sum constraints
                store.impose(new Sum(new IntVar[]{v[j*p],v[j*p+1],v[j*p+2],
                    v[j*p+3],v[j*p+4],v[j*p+5],
                    v[j*p+6],v[j*p+7],v[j*p+8],
                    v[j*p+9],v[j*p+10],v[j*p+11],
                    v[j*p+12],v[j*p+13],v[j*p+14],
                    v[j*p+15],v[j*p+16],v[j*p+17],
                    v[j*p+18],v[j*p+19],v[j*p+20]
                        
                },workshifts_per_nurse));   
        } 
   
    
    }
     
    /*this function adds the constraints to the solver
    * constraint: a nurse want to work in a work shift 
    * 
    *
    */
    public void setNurseWorkShiftPreference(int nurseNum,int workShift){
         //use X = Const	XeqC(X, Const)  constraint to solve this problem 
        //nurseNum range from 1 to ...   workshift range from 0 to 20
         store.impose(new XeqC(v[(nurseNum-1)*21+workShift],1));
 
    }
    
    
    
    
    //the function to find a solution 
    public IntVar[] findSolution(){
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
        //search.getSolutionListener().searchAll(true);
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
        
        return v;
    }
        
 
        
     
}
