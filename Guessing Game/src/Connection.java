import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.net.*;

public class Connection {

	//synchronized save to file method. Only one thread can access this method. 
	private static void saveToFile(CelebTreeMap<String> node){
		try{
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
	private static CelebTreeMap<String> readToFile(){
		CelebTreeMap<String> output;
		try{
			FileInputStream fin = new FileInputStream("save");
			InputStream buffer = new BufferedInputStream(fin);
			ObjectInputStream ois = new ObjectInputStream(buffer);
			output = (CelebTreeMap<String>)ois.readObject();
			ois.close();
			
			return output;
			
		}catch (Exception ex){
			System.out.println("File Does not exist. Will create new one");
			return (new CelebTreeMap<String>("Barack Obama")) ;
		}
	}	
	
	
	public static void main(String argv[]) throws Exception{
		ServerSocket welcomeSocket = new ServerSocket(6001);
		CelebTreeMap<String> temp = (CelebTreeMap<String>)readToFile();
		
		while(true){
			Socket connectionSocket = null;
			try{
				connectionSocket = welcomeSocket.accept();
				Runnable game = new CelebrityGame(connectionSocket, temp);
				Thread t = new Thread(game);
				t.start();
			} catch(SocketException e) {
				System.err.println(e);
				e.printStackTrace();
				continue;
		    }
		}
	}
}
