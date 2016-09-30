package scrabblepack;


import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

//last checked word in the english words folder = brined
public class ScrabbleSolver extends Applet implements KeyListener
{
	static Map<String, Set<String>> wordMap;
	TextField textBox;
	TextField numBox;
	Label numInfo;
	Label title;
	Label instructions;
	Label solutions;
	String answers = "answers";
	ArrayList<Label> labelList;
	
	public void init()
	{
		wordMap = mapWords();
		this.setSize(800, 400);
		this.setLayout(getLayout());
		this.setLayout(new GridLayout(0,1));
		this.setBackground(new Color(50,250,0));
		//this.setBackground(Color.green);
		
		labelList = new ArrayList<Label>();
		
	}
	//project freetv
	
	public void start()
	{
		//Label blank = new Label("");
		textBox = new TextField("");
		textBox.addKeyListener(this);
		numBox = new TextField("0");
		numBox.addKeyListener(this);
		title = new Label("Doc Scrabble");
		solutions = new Label("");
		instructions = new Label("Enter the letters you have, and Doc Scrabble will tell you all the words you can play.");
		numInfo = new Label("Enter the number of letters you want the word to have here. Enter 0 or less for all options.");
		//Label instructions2 = new Label("Press r to restart, and delete to go back one level. Click on this box to begin.");
		Font font = new Font("Mysterious Mr.L", Font.HANGING_BASELINE, 18);
		instructions.setFont(font);
		numInfo.setFont(font);
		solutions.setFont(font);
		Font titleFont = new Font("Wat the refrance", Font.CENTER_BASELINE, 30);
		title.setFont(titleFont);
		this.add(title, "North");
		//this.add(blank);
		this.add(instructions, "North");
		this.add(textBox, "Center");
		this.add(numInfo, "Center");
		this.add(numBox, "Center");
		this.add(solutions, "Center");
		
	}
	
	@Override
	public void keyPressed(KeyEvent e) 
	{
		int key = e.getKeyCode();
	    if (key == KeyEvent.VK_ENTER) 
	    {
	    	
	    	String letters = textBox.getText();
	    	if(letters==null)
	    		letters=" ";
	    	String number = numBox.getText();
	    	int numOfLetters=0;
	    	if(this.isNumeric(number))
	    		numOfLetters = Integer.parseInt(number);
			findSolutions(letters, wordMap, numOfLetters);
	    }
	}
	@Override
	public void keyReleased(KeyEvent arg0) {}
	@Override
	public void keyTyped(KeyEvent arg0) {}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	public void findSolutions(String letters, Map<String, Set<String>> jumblemap, int wordsize)
	{
		//puts letters in alphabetical order
		char[] newarray = new char[letters.length()]; int count = 0;
		char letter = 'a';
		for(int j=0; j<26; j++)
		{
			for(int i=0; i<letters.length(); i++)
			{
				if(letters.charAt(i)==letter)
				{
					newarray[count] = letters.charAt(i);
					count++;
				}
			}
			letter++;
		}
		
		String c[] = new String[newarray.length];
		for (int j = 0; j < c.length; j++) 
		{
			c[j] = String.valueOf(newarray[j]);
		}
		
		//runs combinations to find keys
		ArrayList<String> keys = new ArrayList<String>(combinationsRunner(c));
		ArrayList<String> newKeys = new ArrayList<String>();
		
		//takes out all keys that don't fit the wordsize
		if(wordsize>0)
		{
			for(int i=0; i<keys.size(); i++)
			{
				if(keys.get(i).length()==wordsize)
				{
					System.out.println(keys.get(i)+" "+wordsize);
					newKeys.add(keys.get(i));
				}
			}
		}
		else
		{
			newKeys = keys;
		}
		
		TreeMap<Integer, Set<String>> solutionsmap = new TreeMap<Integer, Set<String>>();

		for(int i=newKeys.size()-1; i>=0; i--)
		{
			if(jumblemap.get(newKeys.get(i))!=null)
			{
				String s = "0"+scoreString(newKeys.get(i));
				int score = Integer.parseInt(s);
				
				if(solutionsmap.containsKey(score))
				{
					solutionsmap.get(score).addAll(jumblemap.get(newKeys.get(i)));
				}
				else
				{
					solutionsmap.put(score,jumblemap.get(newKeys.get(i)));
				}
			}
		}
		
		displaySolutions(solutionsmap);
	}
	
	public void displaySolutions(TreeMap<Integer, Set<String>> solutionsmap)
	{
		Font font = new Font("Mysterious Mr.L", Font.PLAIN, 18);
		
		for(Label l: labelList)
		{
			this.remove(l);
		}
		
		labelList.clear();
		if(!solutionsmap.isEmpty())
		{
			for(int i=solutionsmap.lastKey(); i>0; i--)
			{
				if(solutionsmap.get(i)!=null)
				{
					Label l = new Label(i+" points: "+solutionsmap.get(i).toString());
					l.setFont(font);
					labelList.add(l);
					this.add(l,"Center");
				}
			}
		}
		else
		{
			Label l = new Label("I have found no results. I have, however, prepared this message of failure for you.");
			Label m = new Label("After all, I am a great host.");
			l.setFont(font); m.setFont(font);
			labelList.add(l); labelList.add(m);
			this.add(l, "North"); this.add(m, "North");
		}
		this.resize(800, 501);
		this.resize(800, 500);
	}
	
	public static String scoreString(String key)
	{
		int score = 0;
		for(int i=0; i<key.length(); i++)
		{
			switch(key.charAt(i))
			{
				case 'q':
				case 'z':
					score = score+10;
					break;
				case 'j':
				case 'x':
					score = score+8;
					break;
				case 'k':
					score = score+5;
					break;
				case 'f':
				case 'h':
				case 'v':
				case 'w':
				case 'y':
					score = score+4;
					break;
				case 'b':
				case 'c':
				case 'm':
				case 'p':
					score = score+3;
					break;
				case 'd':
				case 'g':
					score = score+2;
					break;
				default:
					score = score+1;
			}
		}
	
		return String.valueOf(score);
	}
	
	@SuppressWarnings("resource")
	public static Map<String, Set<String>> mapWords()
	{
		TreeMap<String, Set<String>> jumblemap = new TreeMap<String, Set<String>>();
		Scanner scan = null;
		try 
		{
			scan = new Scanner(new File("EnglishWords.txt"));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		while(scan.hasNext())
		{
			String string = scan.next().toLowerCase();
			string = string.replace(',', ' ');
			string = string.trim();
			char[] newarray = new char[string.length()]; int count = 0;
			char letter = 'a';
			for(int j=0; j<26; j++)
			{
				for(int i=0; i<string.length(); i++)
				{
					if(string.charAt(i)==letter)
					{
						newarray[count] = string.charAt(i);
						count++;
					}
				}
				letter++;
			}
			String key = new String(newarray);
			
			if(jumblemap.containsKey(key))
			{
				jumblemap.get(key).add(string);
			}
			else
			{
				HashSet<String> newset = new HashSet<String>();
				newset.add(string);
				jumblemap.put(key, newset);
			}
		}
		return jumblemap;
	}

	public static ArrayList<Word> combinations(ArrayList<Word> solutions, int count, String[] c, int letterAtEnd)
	{
		//when taking the subsets of something, exclude the first one, take subsets of rest.
		//count down from number of subsets
		//for loop through 0-size,  each time combined with all different combinations of the count-1
		//? have permutation return list of strings?
		if(count==0)
		{
			solutions.add(new Word(letterAtEnd, c[letterAtEnd]));
			solutions.addAll(combinations(solutions, count+1, c, letterAtEnd));
			return solutions;	
		}
		
		ArrayList<Word> newWords = new ArrayList<Word>();
		
		if(count>0)
		{
			for(int letter=0; letter<letterAtEnd; letter++) //the original list, abcd
			{
				for(int j=0; j<solutions.size(); j++) // the solutions list,
				{
					if(solutions.get(j).getNumber()>letter)
					//if(c[letter].charAt(0)<solutions.get(j).getString().charAt(0)) 
					// if (the number that the first letter of solutions.get(j) used to hold in the list) is greater than (letter)
					{
						String string = c[letter]+solutions.get(j).getString();
						Word word = new Word(letter, string);
						newWords.add(word);
					}
				}
			}
			if(count==c.length-1)
			{
				return newWords;
			}
			if(count<c.length-1)
			{
				newWords.addAll(combinations(newWords, count+1, c, letterAtEnd));
				return newWords;
			}
		}
		return newWords;
	}
	
	public static ArrayList<String> combinationsRunner(String[] c)
	{
		ArrayList<String> keys = new ArrayList<String>();
		
		for(int i=0; i<c.length; i++)
		{
			ArrayList<Word> solutions = new ArrayList<Word>();
			ArrayList<Word> words = combinations(solutions, 0, c, i);
			for(int j=0; j<words.size(); j++)
			{
				keys.add(words.get(j).getString());
			}
		}

		return keys;
	}
}

class Word 
{
	int initplace; // the initial place of the first letter
	String word;
	
	Word(int init, String string)
	{
		initplace = init;
		word = string;
	}

	public void setNumber(int number)
	{
		initplace = number;
	}
	
	public int getNumber()
	{
		return initplace;
	}
	
	public String getString()
	{
		return word;
	}
	
	@Override
	public String toString()
	{
		return word+initplace;
	}
}

