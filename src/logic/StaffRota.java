/*
 * This is the main class of the staff rota 
 * 
 * 
 */
package logic;

/**
 *
 * @author 
 * 
 */
//import Jacop library 
import JaCoP.core.*; 
import JaCoP.constraints.*; 
import JaCoP.search.*; 
 
public class StaffRota{ 
 
    //create StaffRota object
    static StaffRota m = new StaffRota(); 
 
    public static void main (String[] args) { 
        Store store = new Store();  // define FD store  

        /* define finite domain variables*/
        // v[i][j] i means the nurse number j means the work shift number 
        //in the code v[i][j] needs to be transformed to v[21*m+n] because the jacop search only supports one dimension array  
        int i= 10;
        int j= 21;
        //define all the varaibles in the constraint programming problem
        IntVar[] v = new IntVar[i*j];
        
        //construct integer varibale for each workshift of nurse nurse i
        for(int m=0;m<i;m++){
            
            //constrcut intvar for the work shifts of nurse i
            for(int n=0;n<j;n++){
                //vm-n indicates value v 
                //intvar's value is either 0 or 1 
                v[21*m+n]=new IntVar(store,"v"+m+"-"+n,0,1);
            
            }
        }
      

        //define constraints   
        /*constraint 1
         *the time between anyone's 2 consecutive shifts(C=the minimum time to rest) should be at least 2 work shifts 
         */
        //sum1 indicates <=1
        IntVar sum1=new IntVar(store,"s",0,1);
        //construct array looply to implement Vij+Vij+1+Vij+2<=1
        IntVar[] constraint1=new IntVar[3];
       
        for(int m=0;m<i;m++){
            
            //n means work shifts 
            for(int n=0;n<j-3;n++){
                //initialisation for constraint1
                constraint1[0]=v[21*m+n];
                constraint1[1]=v[21+m+n+1];
                constraint1[2]=v[21*m+n+2];
                
                //impose the constraint
                store.impose(new Sum(constraint1,sum1));
            }
        }
      
        // search for a solution and print results 
        //create a search (specify search methods searched in data space) , this is to specify how to perform labeling
        Search<IntVar> search = new DepthFirstSearch<IntVar>(); 
        //specify a strategy to specify an value to a variable.
        //specify how to select FDV for an assignment from the array of FDVs (v); this is decided explicitly here by InputOrderSelect class 
        // that selects FDVs using the specified order present in v array.
        //IndomainMin is  to assign values for each FDV from its domain
        SelectChoicePoint<IntVar> select = 
                           new InputOrderSelect<IntVar>(store, v, 
                                         new IndomainMin<IntVar>()); 
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
    } 
}
