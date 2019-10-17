package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.PCBException;
import CS143B.josepdm1.Exceptions.RCBException;
import javafx.util.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProcessManagerTest {
    @DisplayName("Test Manager.init()")
    @Test
    void testInit() {
        Manager processManager = new Manager();

    }

    @DisplayName("Test Manager.create()")
    @Test
    void testCreate() {
        Manager processManager = new Manager();
        //ensure size of manager
        try {
            processManager.create(1);

            processManager.create(2);
            //2nd process(1) created 3rd process (2)
            PCB thirdProcess = processManager.getCurrentProcess();
            assert thirdProcess.getParent() == 1;
            assert processManager.getSize() == 3;

            for (int i = 1; i < 14; ++i) {
                //3rd process(2) creates the rest of processes
                processManager.create(2);
                assert thirdProcess.getChildren().size() == i;
            }
            assert processManager.getSize() == 16;

            //trying to create more processes
            try {
                processManager.create(1);
            } catch (PCBException e) {
                assert processManager.getSize() == 16;
            }
        }
        catch (PCBException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Test Manager.destroy()")
    @Test
    void testDestroy() {
        Manager processManager = new Manager();
    }

    @DisplayName("Test Manager.request()")
    @Test
    void testRequest() {
        Manager processManager = new Manager();

        try {
            processManager.create(1);
            //request
            //current process request resource R for K units
            processManager.request(0, 1);
            PCB current = processManager.getCurrentProcess();
            assert current.getResources().size() == 1;

            processManager.create(2);
            current = processManager.getCurrentProcess();
            processManager.request(1, 1);

            //request a resource that's blocked

        } catch (PCBException e) {

        }

    }

    @DisplayName("Test Manager.release()")
    @Test
    void testRelease() {
        Manager processManager = new Manager();

        try {
            processManager.create(1);
            //request
            //current process request resource R for K units
            processManager.request(0, 1);

            PCB firstProcess = processManager.getCurrentProcess();
            assert firstProcess.getResources().size() == 1;

            processManager.create(2);
            PCB secondProcess = processManager.getCurrentProcess();
            processManager.request(1, 1);

            /*** releasing a held resource with LESS units: process should KEEP resource ***/
            System.out.println("releasing a held resource with LESS units: process should KEEP resource");
            int units = 3;
            int r4 = 3;
            processManager.request(r4, units);
            assert secondProcess.getResources().size() == 2;

            units = units - 2;
            processManager.release(r4, units);
            assert secondProcess.getResources().size() == 2;

            /*** unblocking a HIGHER priority process ***/
            System.out.println("unblocking a HIGHER priority process");
            //secondProcess should be blocked
            processManager.request(0, 1); //firstProcess now running

            System.out.println(processManager.toString());
            processManager.release(0, 1);
            System.out.println(processManager.toString());

            assert firstProcess.getResources().size() == 0;
            assert secondProcess.getResources().size() == 2;
            assert processManager.getCurrentProcess() == secondProcess;

            /*** unblocking a LOWER priority process ***/

        } catch (PCBException e) {

        } catch (RCBException e) {

        }
    }

    @DisplayName("Test Manager.timeout()")
    @Test
    void testTimeout() {
        Manager processManager = new Manager();

    }

    //ensure that the scheduler schedules the correct process to be run, after several events
    @DisplayName("Test Manager.scheduler()")
    @Test
    void testScheduler() {
        Manager processManager = new Manager();

    }
}
