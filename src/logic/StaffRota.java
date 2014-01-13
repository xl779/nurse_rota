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
        int i= 10;//means the rota is for ten nurses 
        int j= 21;//21 means there are 21 work slots in a week 
        
        //define all the varaibles in the constraint programming problem
        IntVar[] v = new IntVar[i*j];
        
        //construct integer varibale for each workshift of nurse nurse i
        for(int m=0;m<i;m++){
            
            //constrcut intvar for the work shifts of nurse i
            for(int n=0;n<j;n++){
                //vm-n indicates value v 
                //intvar's value is either 0 or 1 
                v[j*m+n]=new IntVar(store,"v"+m+"-"+n,0,1);
            
            }
            
        }
      

        //define constraints   
        /*constraint 1
         *the time between anyone's 2 consecutive shifts(C=the minimum time to rest) should be at least 2 work shifts 
        */
    
        //sum1 indicates <=1
        IntVar sum1=new IntVar(store,"s",0,1);
        //construct array looply to implement Vij+Vij+1+Vij+2<=1
        //IntVar[] constraint1=new IntVar[3];
       
        for(int m=0;m<i;m++){
            
            //n means work shifts 
            for(int n=0;n<j-2;n++){
                //initialisation for constraint1
    
                //impose all the constraints 
                //the constraint specify that the sum of any 3 consecutive shifts is equal to or less than 1
                //for every nurse its 3 consecutive shifts can't be more than 1 
                store.impose(new Sum(new IntVar[]{v[21*m+n],v[21*m+n+1],v[21*m+n+2]},sum1));
               //store.impose(new XplusYplusQeqZ(v[21*m+n],v[21*m+n+1],v[21*m+n+2],sum1));
            }
        }
       
      
     
        
        /*constraint 2 
         * there must be 2 nurses working in a specific work shift 
         * so it's sum(v[i][1]) for all i = 2
         */ 
         
        //n2 means number 2 
        IntVar n2=new IntVar(store,"n",3,5);
        
        //create an array to store all the nurses for a single work shift 
        //i means the number of the nurses
        //IntVar[] constraint2=new IntVar[i];
        
        //impose the constraints looply
        //n indicates the work shift 
       
        //create an array to store all the nurses for a single work shift 
        //i means the number of the nurses
        //IntVar[] constraint2=new IntVar[i];
        
        //impose the constraints looply
        //p indicates the work shift
        //当维数很大 不好分析时 取几个小的参数来分析
        for(int p=0;p<j;p++){
            
            //m means the nurse number  
            //for(int m=0;m<i;m++){
                //initialisation for constraint2
                //the reason why i can't find a solution is on the definition of the constraints 
                store.impose(new Sum(new IntVar[]{v[p],v[21*1+p],v[21*2+p],
                v[21*3+p],v[21*4+p],v[21*5+p],
                v[21*6+p],v[21*7+p],v[21*8+p],
                
                },n2));
           
           // }
            //impose the constraint
               
        }
      
       
        
        /*
         *constraint 3 
         * a nurse's working hours in a week should be less than 5 work shifts 
         *
         */
        IntVar workshifts_per_nurse=new IntVar(store,"workpernurse",5,10);
        for(int p=0;p<i;p++){
            
            //m means the nurse number  
            //for(int m=0;m<i;m++){
                //initialisation for constraint2
                //the reason why i can't find a solution is on the definition of the constraints 
                store.impose(new Sum(new IntVar[]{v[21*p],v[21*p+1],v[21*p+2],
                    v[21*p+3],v[21*p+4],v[21*p+5],
                    v[21*p+6],v[21*p+7],v[21*p+8],
                    v[21*p+9],v[21*p+10],v[21*p+11],
                    v[21*p+12],v[21*p+13],v[21*p+14],
                    v[21*p+15],v[21*p+16],v[21*p+17],
                    v[21*p+18],v[21*p+19],v[21*p+20]
                    
                    
               
                },workshifts_per_nurse));
           
           // }
            //impose the constraint
               
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
