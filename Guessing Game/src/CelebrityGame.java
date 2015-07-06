import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;

public class CelebrityGame implements Runnable {
	private Socket socket; //thread socket
	boolean invalidResponse;
	private CelebTreeMap<String> tNode = new CelebTreeMap<String>("Barack Obama"); //instantiate Tree
	public static HashMap<String, ArrayList<String>> allCelebs = new HashMap<String, ArrayList<String>>(); //HashMap 
													//	containing Celeb added and people who guessed/added the celeb
	ArrayList<String> init = new ArrayList<>(); //ArrayList used for containing people who guessed/added celeb to tree 
	User player; //Each thread has a User containing variables such as Socket ID, String name, & ArrayList<String> Celebs 
				//added by user
	
	//IO Readers/Writers
	BufferedReader inFromUser;
	PrintWriter outToClient;

	
	//Basic constructor: takes in Socket and assigns it to Readers/Writers 
	public CelebrityGame(Socket socket) throws IOException{
		inFromUser = 
				new BufferedReader(new InputStreamReader(socket.getInputStream())); //read the input response 
		outToClient = 
				 new PrintWriter(socket.getOutputStream());
		this.socket = socket;
	}
	
	//Constructor takes in socket and also adds a (read from file) TreeMap to the tNode variable
	public CelebrityGame(Socket socket, CelebTreeMap<String> node) throws IOException{
		inFromUser = 
				new BufferedReader(new InputStreamReader(socket.getInputStream())); //read the input response 
		outToClient = 
				 new PrintWriter(socket.getOutputStream());
		this.socket = socket;
		
		tNode = node;
	}
	
	//synchronized save to file method. Only one thread can access this method.  
	private synchronized void saveToFile(CelebTreeMap<String> node){
		try{	//create a new "save" file and write the TreeMap OBJECT to disk.
			FileOutputStream fout = new FileOutputStream("save");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(node);
			oos.close();
			System.out.println("created save file");
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	
	//sychronized read to file method returns a MapTree Object. Only one thread can access this method
	private synchronized CelebTreeMap<String> readToFile(){
		CelebTreeMap<String> output;
		try{ //read from file "save" and assign the object to temp variable "output". Returns object.
			FileInputStream fin = new FileInputStream("save");
			InputStream buffer = new BufferedInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(buffer);
			output = (CelebTreeMap<String>)ois.readObject();
			ois.close();
			
			return output;
		}catch (Exception ex){
			//ex.printStackTrace();
			System.out.println("File Does not exist. Will create new one");
			return null;
		}
	}

	//method accessed immediately after system asks user if they wish to play a game. Checks to see if the HashMap has any
	//Celebrity that the play contains in their player.arrayList
	private void voicemail(){
		boolean hasPrintedOut = false;
		if(!allCelebs.isEmpty()){ //Check to see if HashMap is empty
			
			for(String s : this.player.getArray()){ //S contains all the Celebs the particular player added
				if(allCelebs.get(s) == null){ //Check to see if the HashMap contains any value the sth index
					continue;
				}
				else{ //if s does contain a value
				//	System.out.println(allCelebs.entrySet());//debug
					synchronized(allCelebs){
					for(String user : allCelebs.get(s)){ //assign play name to string user
						if(!(user.equals(this.player.getName()))){ //if the user != player name, print it out 
							hasPrintedOut = false;
							outToClient.println("" + user + " gusesed your celebrity " + s);
							hasPrintedOut = true;	
							ArrayList<String> tempp = new ArrayList<String>();
							allCelebs.put(s, tempp);
						}
					}
					}//end synch
				}
			}
		}
		else
			System.out.println("Celeb tree is empty");
	}
	
	public void run(){
			CelebTreeMap<String> temp = tNode; //keeps track of the first node
			CelebTreeMap<String> finalMap = temp; //used later to save the MapTree to file
			
		//prompt for name
			outToClient.println("Please enter your name\n");
			outToClient.flush();
			String name;
			try {
				name = inFromUser.readLine();
				this.player = new User(socket, name);
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}

		//Initialize variables
			String response; //store user response
			boolean continueRound = true; //continuing round until told otherwise
			
			while (true) { //inf loop-- Signifies each Guessing Game Match Round
				try { //set Timeout to 0 so user has inf. time to answer. Meant to be used in case user adds to tree, causing
					//user to get their timeout set to 20 secs. This overwrites this
					this.socket.setSoTimeout(0);
				} catch (SocketException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();}
				
				boolean hasStartedOver = true; //Keeps track if the game has ended yet 


				
			//Read response
				try {
			do{
				//prompt to play game
				outToClient.println("Would you like to play a guessing game?");
				outToClient.flush();
				invalidResponse = true;
					response = inFromUser.readLine();
				if(response.equals("y") || response.equals("yes")){ //play/continue to play guessing game
					voicemail(); //Check to see if this.this.player B guessed your celeb
					continueRound = true;
					invalidResponse = false;}
				else if( response.equals("n") || response.equals("no")){ //exit guessing game
					continueRound = false;
					voicemail(); //Check to see if this.this.player B guessed your celeb
					invalidResponse = false;
					outToClient.println("\nGood Bye!");	
					outToClient.flush();
					this.socket.close();
					break; }
			}while(invalidResponse);
			
				while (continueRound) { //inf loop-- Signifies each Guessing Game Question Round
				//start the tree back in its original position if the match starts over	
				
					if(hasStartedOver == true)
						tNode = temp;

					if (tNode.isLeaf()) { //check to see if the node is a leaf (contains no children) 
						//prevent other threads from modifying the tree
						synchronized(tNode){
							this.socket.setSoTimeout(20000); //prevent one user from blocking another user. 20 secs
							
							if(tNode.isLeaf()){ //Check again to see if Leaf (or hasnt been added recently). If not, Loop again
								
								outToClient.println("Is your Celebrity " + tNode.getItem() + "?"); 
								outToClient.flush();
								response = inFromUser.readLine();
			
								if (response.equals("n") || response.equals("no")) { //if celebrity is NOT correct,
																					//add to database

										outToClient.println("Who were you thinking of?");
										outToClient.flush();
										
										String person = inFromUser.readLine(); //person player was thinking of
										
										outToClient.println("Question that would help distinguish "+  person + " from "+ tNode.getItem() + "?");
										outToClient.flush();
										
										String question = inFromUser.readLine(); //question player assigns to person
										
										outToClient.println("What would you answer to the question?");
										outToClient.flush();
										
										String answerToQuestion = inFromUser.readLine(); //ans to question play assigns
										
										outToClient.println("Thank you for adding to the guessing game database!");
										outToClient.flush();
										
										init = new ArrayList<String>();
										init.add(this.player.toString()); //add play name to arraylist
										String addedCeleb = new String(person);
										allCelebs.put(addedCeleb, init); //add archived celeb and arraylist of player
										this.player.addToList(person); //add the celeb to the User array
										
										
										tNode.setQuestion(question); //add question to node
				
										if (answerToQuestion.equals("y") || answerToQuestion.equals("yes")) { //add celeb to corresponding node:
											tNode.setLeft(new CelebTreeMap<String>(person));					//"yes" set to left
											tNode.setRight(new CelebTreeMap<String>(tNode.getItem()));	
										} else {															// "no" to right
											tNode.setRight(new CelebTreeMap<String>(person));
											tNode.setLeft(new CelebTreeMap<String>(tNode.getItem()));
										}
										saveToFile(finalMap);
										
									
								}

								else if(response.equals("y") || response.equals("yes")){ // Program has guessed correctly! Print statement and break Question Rounds
									outToClient.println("\nI have guessed your star!");
									outToClient.println("\nThank you for playing!\n\n");
									outToClient.flush();
									try{ //handle adding player who had their celeb guessed. Add them to HashMap
										init = new ArrayList<String>();
										init = allCelebs.get(tNode.getItem()); //arraylist is set equal the Arraylist<String> containing the player

										init.add(this.player.toString()); //arraylist adds the player who got celebrity guesed to list
										
										allCelebs.put(tNode.getItem(), init); //HashMap contains the person guessed correctly with a list of people who guessed the person
									}
									
									catch(NullPointerException npe){
									}
									break;
								}
								hasStartedOver = true;
								break;
							}

						}// End of Synchronized
					}
					else{
						hasStartedOver = false; //game has not started over/is in progress
					do{
						outToClient.println(tNode.getQuestion());
						outToClient.flush();
						
						response = inFromUser.readLine();
						invalidResponse = true;			
						if(response.equals("y") || response.equals("yes")){ //move to node to left node if user enters "yes"
							tNode = tNode.getLeft();
							invalidResponse = false;
						}
						else if(response.equals("n") || response.equals("no")){
							invalidResponse = false;
							tNode = tNode.getRight(); //move to right if user types anything but yes/no
						}
					}while(invalidResponse);
						
					}
					//end synchronized
				}// end while
			} // end while	
			catch 	(SocketException s){
				try {
					this.socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			catch(SocketTimeoutException ste){
				outToClient.println("timed out");
				outToClient.flush();
			} //end SocketTimeoutEx
			catch(IOException e) {
			    System.err.println(e);
			    e.printStackTrace();
			    try {
					socket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}//End  Catch IOException
			}//End IOException

		}//end Guessing Game Round While Loop
	} //end Run Method
	
} //end class
