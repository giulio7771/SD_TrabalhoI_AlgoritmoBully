
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class App {

    public static List<Process> processes = new LinkedList<>();
    public static Deque<Long> availableIds = new ArrayDeque<>();
    public static long nextId = (long) 1;
    public static Process cordinator;
    public static boolean electionGoingOn = false;

    public static void main(String[] args) {
        System.out.println("Starting...");

        // Initiate simulation threads
        ProcessGenerator generator = new ProcessGenerator();
        ProcessKiller killer = new ProcessKiller();
        RequestMaker requestMaker = new RequestMaker();
        CordinatorKiller cordinatorKiller = new CordinatorKiller();
        generator.start();
        killer.start();
        requestMaker.start();
        cordinatorKiller.start();

        System.out.println("Threads started...");
        System.out.println("Showing simulation status...");

        while (true) {
            String processesList = "";
            for (Process process : processes) {
                if (processesList != "") {
                    processesList += ", ";
                }
                processesList += process.id;
            }

            System.out.println("Processes: " + processes.size());
            System.out.println("Processes: [" + processesList + "]");
            System.out.println("Master: " + (App.cordinator == null ? "null" : App.cordinator.id));

            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("\n\n");
        }

    }

    public static void election(Process requester) {
        electionGoingOn = true;
        for (Process process : processes) {
            if (process != requester) {
                if (process.id > requester.id) {
                    election(process);
                    return;
                }
            }
        }
        electionGoingOn = false;
        cordinator = requester;
    }

    public static void request(Process requester) {
        if (App.cordinator == null && !App.electionGoingOn) {
            App.election(requester);
            System.out.print("Process #" + requester.id + " started an election and #" + App.cordinator.id + " won.");

        } else {
            System.out.println("The process #" + requester.id + " made a succesful request to the cordinator (#" + App.cordinator.id + ").");
        }
    }

    public static void kill(Process process) {
        if (process != null) {
            if (cordinator == process) {
                cordinator = null;
            }
            availableIds.push(process.id);
            processes.remove(process);
        }
    }

}
