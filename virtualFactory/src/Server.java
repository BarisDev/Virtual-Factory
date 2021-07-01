import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server
{

    public void RunServer() throws IOException {
        ServerSocket ss = new ServerSocket(4999);
        System.out.println("Server is ready for connections..");
        //machines format: M+name+id+status+speed+tur
        ArrayList<String> machines = new ArrayList<>();
        //emir format: PGE+tur+uzunluk
        ArrayList<String> emirler = new ArrayList<>();
        //jobDone format: makineID+emirtür+emiruzunluk
        ArrayList<String> jobDone = new ArrayList<>();
        //availableMachines format: tur+count
        ArrayList<String> availableMachines = new ArrayList<>();
        availableMachines.add("cnc+0");
        availableMachines.add("dokuma+0");
        availableMachines.add("döküm+0");
        availableMachines.add("kılıf+0");
        availableMachines.add("kaplama+0");

        //userSession format: username+password+status
        ArrayList<String> userSession = new ArrayList<>();
        String user1 = "admin";
        String pass1 = "pass";
        String user2 = "admin2";
        String pass2 = "pass2";
        String session = new String(user1+"+"+pass1+"+"+"offline");
        userSession.add(session);
        session = new String(user2+"+"+pass2+"+"+"offline");
        userSession.add(session);
        while (true){
            Socket s = ss.accept();
            new ServerThread(s, machines, emirler, jobDone, userSession, availableMachines).run();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server().RunServer();

    }
}