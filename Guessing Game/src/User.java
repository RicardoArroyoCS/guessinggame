import java.net.Socket;
import java.util.ArrayList;

public class User{
	private Socket ID; //ID identify each user
	private String name; //The name of the player who added the celeb
	private ArrayList<String> celeb = new ArrayList<>();

	//constructor
	public User(Socket ID, String name){
		this.ID = ID;
		this.name = name;
	}
	
	//return socket ID
	public Socket getID(){
		return ID;
	}
	
	//return String name
	public String getName(){
		return name;
	}
	
	//return String name
	public String toString(){
		return name;
	}
	
	//return arraylist containing the celebs the user added
	public ArrayList<String> getArray(){
		return celeb;
	}
	
	//add celeb to player list
	public void addToList(String person) {
		// TODO Auto-generated method stub
		this.celeb.add(person);
		
	}


}