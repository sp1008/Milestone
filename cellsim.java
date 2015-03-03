import java.awt.Color;

public class CellSim{

	public static void main(String[] args){
	
		//Create a n x n 2-D character array representing the tissue sample
		int movement = 0;
		int maxRounds;
		int numRounds;
		int frequency;
		boolean Happy = false;
		int threshold;
		int percentX;
		int percentBlank;
		int n = 0;
		do{
			System.out.println("Please enter n for n x n grid size:");
			n = IO.readInt();
		}while(n <= 0);
		
		do{
			System.out.println("Please enter the threshold:");
			threshold = IO.readInt();
		}while(threshold < 0 || threshold > 100);
		
		do{
			System.out.println("Please enter the maxmimum rounds:");	// do maxRounds = 0
			maxRounds = IO.readInt();
		}while(maxRounds < 0);
		
		do{
			System.out.println("Please enter the frequency:");
			frequency = IO.readInt();
		}while(frequency < 0);
		
		do{
			System.out.println("Please enter the percentage of X agents:");
			percentX = IO.readInt();
		}while(percentX < 0 || percentX > 100);
		
		do{
			System.out.println("Please enter the percentage of blanks:");
			percentBlank = IO.readInt();
		}while(percentBlank < 0 || percentBlank > 100);
		
		
		int freqCounter = 1;
		char[][] tissue = new char[n][n];
		//Write your code to test your methods here
		CellSimGUI instance = new CellSimGUI(n,1000);
		
		assignCellTypes(tissue, percentBlank, percentX, instance);
		System.out.println("Start:");
		printTissue(tissue);
		for(numRounds = 0; numRounds < maxRounds; numRounds++){
			if(boardSatisfied(tissue, threshold)){
				//System.out.println("Board is satisfied!");
				Happy = true;
				numRounds++;
				if(freqCounter % frequency == 0){
					System.out.println("Round " + freqCounter + ":");
					printTissue(tissue);
				}
				break;
			}
			else{
				movement += moveAllUnsatisfied(tissue, threshold, instance);
			}
			if(freqCounter % frequency == 0){
				System.out.println("Round " + freqCounter + ":");
				printTissue(tissue);
			}
			freqCounter++;
		}
		
		System.out.println("End:");
		printTissue(tissue);
		System.out.println("\nMoved " + movement + " number of agents.");
		if(Happy){
			System.out.println("Board is SATISFIED after " + numRounds + " number of rounds.");
		}
		else{
			double percentSatisfied = getPercentageAgents(tissue, threshold);
			percentSatisfied = Math.round(percentSatisfied);
			System.out.println("Board is NOT SATISFIED. Only " + percentSatisfied +
								"% of the agents are satisfied");
		}
		
	}
	
	/**
	* Given a tissue sample, prints the cell make up in grid form
	*
	* @param tissue a 2-D character array representing a tissue sample
	* 
	***/
	public static void printTissue(char[][] tissue){
		for(int x=0; x<tissue.length; x++){
			for(int y=0; y<tissue[x].length; y++){
				System.out.print("'"+ tissue[x][y] + "' ");
			}
			System.out.print("\n");
		}
	}
	
	/**
	* Given a blank tissue sample, populate it with the correct cell makeup given the parameters. 
	* Cell type 'X' will be represented by the character 'X'
	* Cell type 'O' will be represented by the character 'O'
	* Vacant spaces will be represented by the character ' '
	*
	* Phase I: alternate X and O cells throughout, vacant cells at the "end" (50% credit)
	*		e.g.:	'X' 'O' 'X' 'O' 'X'
	*				'O' 'X' 'O' 'X' 'O'
	*				'X' 'O' 'X' 'O' 'X'
	*				' ' ' ' ' ' ' ' ' '
	*				' ' ' ' ' ' ' ' ' '
	*
	* Phase II: Random assignment of all cells (100% credit)
	*
	* @param tissue a 2-D character array that has been initialized
	* @param percentBlank the percentage of blank cells that should appear in the tissue
	* @param percentX Of the remaining cells, not blank, the percentage of X cells that should appear in the tissue. Round up if not a whole number
	*
	**/
	public static void assignCellTypes(char[][] tissue, int percentBlank, int percentX, CellSimGUI instance){
		int total = tissue.length * tissue[0].length;
		int size = tissue.length;
		int x = 0;
		int y = 0;

		double percentageB = (double)percentBlank/100;	// just do all calculations...
		double percentageX = (double)percentX/100;
		double fill = total - (total*percentageB);
		double numX = fill*percentageX;
		double numO = fill - numX;

		for(;numX>0;numX--){						// take the grid, take maximum number of X, then place them randomly.
			do{	
				x = (int)(Math.random()*100) % size;
				y = (int)(Math.random()*100) % size;
			}while(tissue[x][y] == 'X');			// make sure it doesn't overwrite an existing X
	
			tissue[x][y] = 'X';
			instance.setCell(x, y, Color.red);
		}
		
		for(;numO>=1;numO--){						// take the grid, take maximum number of Y, then place them randomly.
			do{
				x = (int)(Math.random()*100) % size;
				y = (int)(Math.random()*100) % size;
			}while(tissue[x][y] == 'X' || tissue[x][y] == 'O');	// make sure it doesn't overwrite an existing X or Y

			tissue[x][y] = 'O';
			instance.setCell(x, y, Color.blue);
		}
		// not a very efficient program. It will take a long time to prepare a grid for n = 1000.
	}
/**	
	* Given a tissue sample, and a (row,col) index into the array, determines if the agent at that location is satisfied.
    * Note: Blank cells are always satisfied (as there is no agent)
    *
    * @param tissue a 2-D character array that has been initialized
    * @param row the row index of the agent
    * @param col the col index of the agent
    * @param threshold the percentage of like agents that must surround the agent to be satisfied
    * @return boolean indicating if given agent is satisfied
    *
    **/
    public static boolean isSatisfied(char[][] tissue, int row, int col, int threshold){
    	char check = tissue[row][col];
    	if(check != 'X' && check != 'O'){
    		return true;
    	}
    	double counter = 0;
    	double checked = 0;
    	//ROW - 1
    	if(row-1 >= 0){
    		if(tissue[row-1][col] == 'X' || tissue[row-1][col] == 'O'){
    			checked++;
    			if(check == tissue[row-1][col])
    				counter++;
    		}
    		if(col-1 >=0){
    			if(tissue[row-1][col-1] == 'X' || tissue[row-1][col-1] == 'O'){
    				checked++;
    				if(check == tissue[row-1][col-1])
    					counter++;
    			}
    		}
    		if(col+1 < tissue[row-1].length){
    			if(tissue[row-1][col+1] == 'X' || tissue[row-1][col+1] == 'O'){
    				checked++;
    				if(check == tissue[row-1][col+1])
    					counter++;
    			}
    		}
    	}
    	// ROW + 1
    	if(row+1 < tissue.length){
    		if(tissue[row+1][col] == 'X' || tissue[row+1][col] == 'O'){
    			checked++;
    			if(check == tissue[row+1][col])
    				counter++;
    		}
    		if(col-1 >=0){
    			if(tissue[row+1][col-1] == 'X' || tissue[row+1][col-1] == 'O'){
    				checked++;
    				if(check == tissue[row+1][col-1])
    					counter++;
    			}
    		}
    		if(col+1 < tissue[row+1].length){
    			if(tissue[row+1][col+1] == 'X' || tissue[row+1][col+1] == 'O'){
    				checked++;
    				if(check == tissue[row+1][col+1])
    					counter++;
    			}
    		}
    	}
    	// ROW
    	if(col-1 >=0){
    		if(tissue[row][col-1] == 'X' || tissue[row][col-1] == 'O'){
				checked++;
				if(check == tissue[row][col-1])
					counter++;
    		}
    	}
    	if(col+1 < tissue[row].length){
    		if(tissue[row][col+1] == 'X' || tissue[row][col+1] == 'O'){
				checked++;
				if(check == tissue[row][col+1])
					counter++;
    		}
    	}
    	if(checked != 0){
    		double chkthreshold = counter/checked;
    		chkthreshold = chkthreshold * 100;
    		if(chkthreshold >= threshold)
    			return true;
    	}
    	return false;
    }
    
    /**
    * Given a tissue sample, determines if all agents are satisfied.
    * Note: Blank cells are always satisfied (as there is no agent)
    *
    * @param tissue a 2-D character array that has been initialized
    * @return boolean indicating whether entire board has been satisfied (all agents)
    **/
    public static double getPercentageAgents(char[][] tissue, int threshold){
    	double badflag = 0;
    	double totalAgents = 0;
    	for(int x=0; x<tissue.length; x++){
    		for(int y=0; y<tissue[x].length;y++){
    			if(tissue[x][y] == 'X' || tissue[x][y] == 'O'){
    				if(!isSatisfied(tissue, x, y, threshold)){
    					badflag++;
    				}
    				totalAgents++;
    			}
    		}
    	}
    	return (int)100 * ((totalAgents - badflag) / totalAgents); 
    }
    public static boolean boardSatisfied(char[][] tissue, int threshold){
    	int badflag = 0;
    	for(int x=0; x<tissue.length; x++){
    		for(int y=0; y<tissue[x].length;y++){
    			if(!isSatisfied(tissue, x, y, threshold)){
    				//tissue[x][y] = Character.toLowerCase(tissue[x][y]);
    				badflag++;
    			}
    		}
    	}
    	if(badflag == 0)
    		return true;
    	else
    		return false;
    }
    
    /**
     * Given a tissue sample, move all unsatisfied agents to a vacant cell
     *
     * @param tissue a 2-D character array that has been initialized
     * @param threshold the percentage of like agents that must surround the agent to be satisfied
     * @return an integer representing how many cells were moved in this round
     **/
     public static int moveAllUnsatisfied(char[][] tissue, int threshold, CellSimGUI instance){
    	 int[][] location = new int [tissue.length * tissue[0].length][2];
    	 int locationXcounter = 0;
    	 for(int x=0; x<tissue.length; x++){
    		 for(int y=0; y<tissue[x].length; y++){
    			 if(!isSatisfied(tissue, x, y, threshold)){
    				 location[locationXcounter][0] = x;
    				 location[locationXcounter][1] = y;
    				 locationXcounter++;
    			 }
    		 }
    	 }
    	 
    	 int Xcounter = 0;
    	 int Ocounter = 0;
    	 
    	 for(int current=0; current<locationXcounter; current++){
    		 int x = location[current][0];
    		 int y = location[current][1];
    		 if(tissue[x][y] == 'X'){
    			 Xcounter++;
    		 }
    		 else
    			 Ocounter++;
    		 tissue[x][y] = ' ';
    		 instance.setCell(x, y, Color.white);
    	 }
    	 
    	 for(;Xcounter>=1;Xcounter--){						// take the grid, take maximum number of Y, then place them randomly.
 			 int x;
 			 int y;
    		 do{
 				 x = (int)(Math.random()*100) % tissue.length;
 				 y = (int)(Math.random()*100) % tissue[0].length;
 			 }while(tissue[x][y] == 'X' || tissue[x][y] == 'O');	// make sure it doesn't overwrite an existing X or O

 			 tissue[x][y] = 'X';
 			 instance.setCell(x, y, Color.red);
 		 }
    	 
    	 for(;Ocounter>=1;Ocounter--){
    		 int x;
 			 int y;
    		 do{
 				 x = (int)(Math.random()*100) % tissue.length;
 				 y = (int)(Math.random()*100) % tissue[0].length;
 			 }while(tissue[x][y] == 'X' || tissue[x][y] == 'O');	// make sure it doesn't overwrite an existing X or O

 			 tissue[x][y] = 'O';
 			 instance.setCell(x, y, Color.blue);
    	 }
    	
    	 
    	 return locationXcounter;
     }
}
