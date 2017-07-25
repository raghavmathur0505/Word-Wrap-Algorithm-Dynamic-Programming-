package edu.utdallas.printNeatly;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Scanner;
import java.util.TreeMap;

public class PrintingNeatly {

	// Storing each word from the file in the ArrayList of words
	private List<String> words;
	// Total Number of Words
	private int n;
	// Maximum number of characters in each line
	private int M;

	// Cost of words i through words j in a line 
	int lineCost[][];
	// Number of extra spaces in a line containg words i through words j
	int extraSpaces[][];
	// Minimum Cost of printing each line 
	int printNeatlyCost[];
	// While printing the paragraph stores the next starting index
	int printNeatlyIndex[];

	public PrintingNeatly(String file, int M) {

		this.words = this.readFile(file);
		this.words = (words == null)? new ArrayList<>() : words;
		this.n = words.size();
		this.M = M;

		this.lineCost= new int[n][n];
		this.extraSpaces= new int[n][n];
		this.printNeatlyIndex = new int[n];
		this.printNeatlyCost = new int[n + 1];
		this.printNeatlyCost[n] = 0;
	}

	/*
	 * Reading the file from storing the words in it
	 */
	private List<String> readFile(String file) {

		List<String> words = new ArrayList<String>();

		String strLine = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {

			fileReader = new FileReader(file);
			bufferedReader = new BufferedReader(fileReader);

			while (null != (strLine = bufferedReader.readLine())) {

				strLine = strLine.trim();
				if (strLine.length() == 0) {
					continue;
				}
				String[] strData = strLine.split("\\s+");
				if((strData != null)) {
					for(int i = 0; i < strData.length; i++) {
						words.add(strData[i]);
					}
				}
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (null != fileReader) {
					fileReader.close();
				}
				if (null != bufferedReader) {
					bufferedReader.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return words;
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @return
	 * Calculating the extra spaces to put words i through words j
	 * in a single line
	 */
	private int iExtraSpaces(int i, int j) {

		int sumWord = 0;
		for(int indexWord = i; indexWord <= j; indexWord++) {

			if(indexWord < n) {
				sumWord += this.words.get(indexWord).length();
			}
		}

		int extraSpace = M - ((j - i) + sumWord);
		extraSpaces[i][j] = extraSpace;
		return extraSpace;
	}

	/**
	 * 
	 * @param i
	 * @param j
	 * @return
	 * Calculating the cost to put words i through words j
	 * in a single line
	 */
	private int iLineCost(int i, int j) {

		int lineCost = Integer.MAX_VALUE;
		int extraSpaces = this.iExtraSpaces(i, j);

		if(extraSpaces < 0) {
			lineCost = Integer.MAX_VALUE;
		}
		else {

			if(j == n - 1) {
				lineCost = 0;
			}
			else {
				lineCost = (int) Math.pow(extraSpaces, 3);
			}
		}
		return lineCost;
	}

	/**
	 * 
	 * @return
	 * The cost of printing words i through n 
	 */
	public int iPrintNeatlyCost() {

		for(int i = 0; i < n; i++) {

			printNeatlyCost[i] = Integer.MAX_VALUE;
			for(int j = 0; j < n; j++) {

				if(i>j) {
					lineCost[i][j] = Integer.MAX_VALUE;
				}
				else {
					lineCost[i][j] = this.iLineCost(i, j);
				}
			}
		}

		for(int i = n-1; i >= 0; i--) {
			for(int j = i; j <= n-1; j++) {

				int currentPrintNeatlyCost = (lineCost[i][j] + printNeatlyCost[j+1]);
				if((lineCost[i][j] != Integer.MAX_VALUE) && (printNeatlyCost[i] > currentPrintNeatlyCost)) {

					printNeatlyCost[i] = currentPrintNeatlyCost;
					printNeatlyIndex[i] = j+1;
				}
			}
		}
		return printNeatlyCost[0];
	}

	/**
	 * 
	 * @return
	 * Print neatly by giving extra spaces
	 */
	public List<String> printNeatly() {

		int i = 0;
		List<String> printLines = new ArrayList<>();
		
		while(i < n - 1) {

			String printLine = "";
			int extraSpacesNeeded = 0;
			if(printNeatlyIndex[i] != n ) {
				//extraSpacesNeeded = this.iExtraSpaces(i, printNeatlyIndex[i]-1);
				extraSpacesNeeded = extraSpaces[i][printNeatlyIndex[i]-1];
			}
			for(int j = i; j < printNeatlyIndex[i]; j++) {

				if(j == i) {
					printLine = printLine + this.words.get(j);
				}
				else {
					printLine = printLine + " " + this.words.get(j);
				}

				if((j != printNeatlyIndex[i] -1) && (extraSpacesNeeded > 0)) {
					printLine = printLine + "+";
					extraSpacesNeeded--;
				}
			}

			/*while(extraSpacesNeeded > 0) {

				String[] words = printLine.split(" ");
				printLine = "";
				for(int j = 0; j < words.length; j++) {

					if(j == 0) {
						printLine = printLine + words[j];
					}
					else {
						printLine = printLine + " " + words[j];
					}

					if((j != words.length -1) && (extraSpacesNeeded > 0)) {
						printLine = printLine + "+";
						extraSpacesNeeded--;
					}
				}
			}*/
			
			printLines.add(printLine);
			i = printNeatlyIndex[i];
		}
		return printLines;
	}
	
	/**
	 * 
	 * @return
	 * Print neatly giving extra spaces after the long words in a line  
	 */
	public List<String> printNeatlyExtraSpaceAfterLongWord() {

		int i = 0;
		List<String> printLines = new ArrayList<>();
		
		while(i < n - 1) {

			int extraSpacesNeeded = 0;
			if(printNeatlyIndex[i] != n) {
				extraSpacesNeeded = extraSpaces[i][printNeatlyIndex[i]-1];
			}
			
			TreeMap<Integer, Integer> mapWord = new TreeMap<>();
					
			for(int j = i; j < printNeatlyIndex[i]; j++) {

				if((j == printNeatlyIndex[i] - 1) || (j == i)) {
					
					continue;
				}
				
				String word = (this.words.get(j)) == null? "" : this.words.get(j);
				if(null == mapWord.get(word.length())) {
					
					mapWord.put(word.length(), 1);
				}
				else {
					
					int count = mapWord.get(word.length());
					mapWord.put(word.length(), ++count);
				}
			}
			
			NavigableMap<Integer, Integer> nMapWord = mapWord.descendingMap();
			for(Integer wordLength : nMapWord.keySet()) {
				
				int wordCount = mapWord.get(wordLength);
				
				if(wordCount <= extraSpacesNeeded) {
					extraSpacesNeeded = extraSpacesNeeded - wordCount;
				}
				else {
					mapWord.put(wordLength, extraSpacesNeeded);
					extraSpacesNeeded = 0;
				}
			}
			
			String printLine = "";
			for(int j = i; j < printNeatlyIndex[i]; j++) {

				String word = (this.words.get(j)) == null? "" : this.words.get(j);
				if(j != printNeatlyIndex[i] - 1) {
					printLine = printLine + word + " ";
				}
				else {
					printLine = printLine + word;
				}

				if(mapWord.get(word.length()) != null) {
					if(mapWord.get(word.length()) > 0) {
						
						int count = mapWord.get(word.length());
						printLine = printLine + " ";
						mapWord.put(word.length(), --count);
					}
				}
				
			}
			
			printLines.add(printLine);
			i = printNeatlyIndex[i];
		}
				
		return printLines;
	}

	
	public static void main(String[] args) {
		
		int M = 80;
		String file = "";
		
		/*if(args.length == 0) {
			
			args = new String[2];
			args[0] = "E:\\algo\\input1.txt";
			args[1] = "72"; 
		}
		*/
		
		if((args.length >= 1) && (args[0] != null)) {
			
			file = (args[0]).trim();
			
			if((args.length >= 2) && (args[1] != null)) {
				
				String strM = (args[1]).trim();
				
				try {
					M = Integer.parseInt(strM);
				}
				catch(NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			
			Scanner scanner = new Scanner(System.in);
			System.out.print("Please Enter the input file name : ");
			file = scanner.nextLine();
			scanner.close();
		}
		
		File objFile = new File(file);
		boolean isFileExists = objFile.exists();
		
		if(!isFileExists) {
			
			System.out.println("File Not Exists! Please try with new file...");
			System.exit(1);
		}
		
		PrintingNeatly printingNeatly = new PrintingNeatly(file, M);
		int printNeatlyCost = printingNeatly.iPrintNeatlyCost();
		
		System.out.println(printNeatlyCost);
		System.out.println();
		
		//List<String> output = printingNeatly.printNeatly();
		List<String> output = printingNeatly.printNeatlyExtraSpaceAfterLongWord();
		for(String line : output) {

			System.out.println(line);
		}
	}
}
