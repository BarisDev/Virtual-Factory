import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
public class ServerThread implements Runnable{
    Socket socket;
    ArrayList<String> machines;
    ArrayList<String> emirler;
    ArrayList<String> jobDone;
    ArrayList<String> userSession;
    ArrayList<String> availableMachines;
    int availableMachineCount;

    public ServerThread(Socket socket, ArrayList<String> machines, ArrayList<String> emirler, ArrayList<String> jobDone, ArrayList<String> userSession, ArrayList<String> availableMachines){
        this.socket = socket;
        this.machines = machines;
        this.emirler = emirler;
        this.jobDone = jobDone;
        this.userSession = userSession;
        this.availableMachines = availableMachines;
    }
    public void run(){
        InputStreamReader in = null;
        String str = null;

        try {
            in = new InputStreamReader(socket.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            str = bf.readLine();

            String[] token = str.split("\\+");

            //geçici değişkenler
            String[] machinesParse = null;
            String emir = null;
            String[] emirParse = null;
            String machineReplace;
            String machine;

            String[] sessionParse = null;
            String session = null;
            String sessionReplace;

            String[] aMachineParse = null;
            String aMachine = null;

            String temp = null;


            if(token[0].equals("P")){
                System.out.println("planlamaci login'den mesaj var!");

                String username = token[1];
                String password = token[2];
                System.out.println("server username: "+username);
                System.out.println("server password: "+password);

                PrintWriter pr = new PrintWriter(socket.getOutputStream());
                boolean isOnline = false;
                for (int i = 0; i < userSession.size(); i++){
                    session = userSession.get(i);
                    sessionParse = session.split("\\+");
                    if(username.trim().equals(sessionParse[0]) && password.trim().equals(sessionParse[1]) && sessionParse[2].equals("offline")){
                        isOnline = true;
                        sessionReplace = session.replace("offline", "online");
                        userSession.remove(session);
                        userSession.add(sessionReplace);
                    }
                }

                if(isOnline){
                    pr.print("yes");
                    System.out.println("Kullanıcı girişi başarılı!");
                }else{
                    pr.print("no");
                    System.out.println("Bu kullanıcı zaten online!!");
                }
                pr.flush();
                System.out.println("userSession: "+userSession);
                socket.close();
            }
            else if(token[0].equals("PO")){
                String offlineUser = token[1];

                for(int i = 0; i < userSession.size(); i++){
                    session = userSession.get(i);
                    sessionParse = userSession.get(i).split("\\+");

                    if(sessionParse[0].equals(offlineUser)) {
                        sessionReplace = session.replace("online", "offline");
                        userSession.remove(session);
                        userSession.add(sessionReplace);
                    }
                }
                System.out.println(offlineUser + " kullanıcısı artık OFFLINE!!");
                System.out.println("userSession: " + userSession);
                socket.close();

            }
            else if(token[0].equals("M")){
                System.out.println("Makineden mesaj var!");
                String name = token[1];
                String id = token[2];
                String status = token[3];
                String speed = token[4];
                String tur = token[5];
                boolean isSame = false;
                //sistemde makine yokken ilk girilen makineyi kabul et. Sonraki her makine için makine listesinde id kontrolü yap
                if(machines.size() == 0){
                    machines.add(str);
                    PrintWriter pr = new PrintWriter(socket.getOutputStream());
                    pr.println("yes");
                    pr.flush();
                    //availableMachineCount++;

                    for(int k = 0; k < availableMachines.size(); k++) {
                        aMachine = availableMachines.get(k);
                        aMachineParse = aMachine.split("\\+");
                        //müsait makinenin türü availableMachines türüyle aynıysa
                        if (aMachineParse[0].equals(tur)) {
                            availableMachineCount++;
                            temp = aMachineParse[0] + "+" + availableMachineCount;
                            availableMachines.remove(aMachine);
                            availableMachines.add(temp);
                            break;
                        }
                    }
                    System.out.println("Makine eklendi!");
                } else{

                    for(int i = 0; i < machines.size(); i++){
                        machine = machines.get(i);
                        machinesParse = machine.split("\\+");
                        if (machinesParse[2].equals(id)){
                            isSame = true;
                        }
                    }
                    if(isSame){
                        PrintWriter pr = new PrintWriter(socket.getOutputStream());
                        pr.println("no");
                        pr.flush();
                        System.out.println("Ayni ID'de Makine eklenemedi!");
                    } else{
                        machines.add(str);
                        PrintWriter pr = new PrintWriter(socket.getOutputStream());
                        pr.println("yes");
                        pr.flush();

                        for(int k = 0; k < availableMachines.size(); k++) {
                            aMachine = availableMachines.get(k);
                            aMachineParse = aMachine.split("\\+");
                            //müsait makinenin türü availableMachines türüyle aynıysa
                            if (aMachineParse[0].equals(tur)) {
                                availableMachineCount = Integer.parseInt(aMachineParse[1]);
                                availableMachineCount++;
                                temp = aMachineParse[0] + "+" + availableMachineCount;
                                availableMachines.remove(aMachine);
                                availableMachines.add(temp);
                                System.out.println("Makine eklendiiiiiiiiiiiii!: " + availableMachineCount);
                                break;
                            }
                        }
                    }
                }

                socket.close();

            }
            else if(token[0].equals("PG")){
                System.out.println("Plan GUIden mesaj var!");
                if(machines != null){
                    PrintWriter pr = new PrintWriter(socket.getOutputStream());
                    pr.println(machines);
                    pr.flush();
                } else System.out.println("makine listesi boş!");

                socket.close();

            }
            else if(token[0].equals("PGE")){
                System.out.println("Plan GUIden Emir var!");
                emirler.add(str);
                socket.close();

            }
            else if(token[0].equals("PGS")){
                System.out.println("Emirler Listelenecek");
                PrintWriter pr = new PrintWriter(socket.getOutputStream());
                pr.println(emirler);
                pr.flush();
            }
            else if(token[0].equals("MO")){
                System.out.println("Server: makine silinecek!");
                //makine string formatı: M+name+id+status+speed+tur

                String silID = token[1];
                String[] temp2 = null;

                for(int i = 0; i < machines.size(); i++){
                    temp = machines.get(i);
                    temp2 = machines.get(i).split("\\+");

                    if(temp2[2].equals(silID)){
                        //System.out.println("if'e girdim siliyorum");
                        machines.remove(temp);
                        //availableMachineCount--;
                    }

                }
                System.out.println(silID + " id'li makine silindi!");
                socket.close();

            }
            else if(token[0].equals("PGED")){
                System.out.println("Thread görevini tamamladı");
                String id = token[1];

                //machines string format: M+name+id+status+speed+tur
                for(int i = 0; i < machines.size(); i++){
                    machine = machines.get(i);
                    machinesParse = machine.split("\\+");
                    if(machinesParse[2].equals(id)){
                        machineReplace = machine.replace("BUSY", "EMPTY");

                        machines.remove(machine);
                        machines.add(machineReplace);
                        machinesParse = machineReplace.split("\\+");
                        //availableMachineCount++;
                        System.out.println(machinesParse[1]+" makine emre hazır!");
                        String jobDoneString = new String(machinesParse[2]+"+"+machinesParse[5]+"+"+token[2]);
                        jobDone.add(jobDoneString);
                        for(int l = 0; l < availableMachines.size(); l++){
                            aMachine = availableMachines.get(l);
                            aMachineParse = aMachine.split("\\+");
                            //müsait makinenin türü availableMachines türüyle aynıysa

                            if(aMachineParse[0].equals(machinesParse[5]) && machinesParse[3].equals("EMPTY")){
                                availableMachineCount = Integer.parseInt(aMachineParse[1]);
                                availableMachineCount++;
                                temp = aMachineParse[0] + "+" + availableMachineCount;
                                availableMachines.remove(aMachine);
                                availableMachines.add(temp);
                                break;
                            }
                        }
                        break;
                    }
                }

            }
            else if(token[0].equals("PGJ")){
                System.out.println("Bitmiş İşler Listelenecek");
                PrintWriter pr = new PrintWriter(socket.getOutputStream());
                pr.println(jobDone);
                pr.flush();
            }
            else{
                System.out.println("Sunucu Mesajı anlamlandıramadı!");
                socket.close();
            }

            //boşta makine varsa ve emir varsa atama işlemi yap

            //emir geldiğinde o emre uygun kaç makine var hesaplıyorum
            if(emirler.size() != 0){
                for (int i = 0; i < emirler.size(); i++) {
                    emir = emirler.get(i);
                    emirParse = emir.split("\\+");

                    for(int j = 0; j < machines.size(); j++){
                        machine = machines.get(j);
                        machinesParse = machine.split("\\+");
                        //müsait makine hesabı yapılıyor

                        for(int k = 0; k < availableMachines.size(); k++){
                            aMachine = availableMachines.get(k);
                            aMachineParse = aMachine.split("\\+");
                            //müsait makinenin türü availableMachines türüyle aynıysa
                            if(aMachineParse[0].equals(emirParse[1]) && machinesParse[3].equals("EMPTY") && aMachineParse[0].equals(machinesParse[5])){
                                availableMachineCount = Integer.parseInt(aMachineParse[1]);
                                break;
                            }
                        }
                    }
                }
            }
//            System.out.println("available Machines: "+availableMachines);

            for (int q = 0; q < emirler.size() ; q++) {
                emir = emirler.get(q);
                emirParse = emir.split("\\+");

                for (int i = 0; i < availableMachines.size(); i++) {
                    aMachine = availableMachines.get(i);
                    aMachineParse = aMachine.split("\\+");

                    if(aMachineParse[0].equals(emirParse[1])){
                        availableMachineCount = Integer.parseInt(aMachineParse[1]);
                        while(availableMachineCount != 0 && emirler.size() != 0){

                            for(int j = 0; j < machines.size(); j++) {
                                machine = machines.get(j);
                                machinesParse = machine.split("\\+");
                                //makineler boştaysa ve iş emir türü ile makine türü aynıysa
                                if (machinesParse[3].equals("EMPTY") && emirParse[1].equals(machinesParse[5])) {
                                    System.out.println(machinesParse[1] + " adlı makinesine emir verildi!");
                                    machineReplace = machine.replace("EMPTY", "BUSY");
                                    //Replace string
                                    machines.remove(machine);
                                    machines.add(machineReplace);

                                    isMakinasiRegister isMakinasiRegister = new isMakinasiRegister(machinesParse[4], emirParse[2], machinesParse[2]);
                                    Thread thread = new Thread(isMakinasiRegister);
                                    thread.start();
                                    emirler.remove(emir);

                                    for(int l = 0; l < availableMachines.size(); l++){
                                        aMachine = availableMachines.get(l);
                                        aMachineParse = aMachine.split("\\+");
                                        //müsait makinenin türü availableMachines türüyle aynıysa
                                        if(aMachineParse[0].equals(emirParse[1]) && machinesParse[3].equals("EMPTY") && aMachineParse[0].equals(machinesParse[5])){
                                            availableMachineCount = Integer.parseInt(aMachineParse[1]);
                                            availableMachineCount--;
                                            temp = aMachineParse[0] + "+" + availableMachineCount;
                                            availableMachines.remove(aMachine);
                                            availableMachines.add(temp);
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Available Machines: "+availableMachines);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
