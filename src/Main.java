import java.awt.EventQueue;
import javax.swing.*;


public class Main extends JFrame{

    public Main() {

        KillerGUI();
        PlayerName();
    }

    private void KillerGUI() {

        //Adding the KillerCovid class to the GUI
        add(new KillerCovid());

        //Setting the title and size of the game window
        setTitle("Killer Covid");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(465, 515);
        //Used to make sure that the window appears in the centre of the screen
        setLocationRelativeTo(null);
    }

    private void PlayerName(){

    }


    public static void main(String[] args) {

        //Setting the game window to visible
        var banana = new Main();
        banana.setVisible(true);

        //Using EventQueue.invokeLater() to make sure the name class and rules pop up in front of the game window instead of behind it
        EventQueue.invokeLater(() -> {
            //Using a JOption pane to show the rules
            JOptionPane.showMessageDialog(null,
                    "1. You have to collect all the vaccines to win the game.\n" +
                            "2. Once you have collected all the vaccines, the game restarts and the number of viruses increases by 1.\n" +
                            "3. If a virus touches you, you lose a life.\n" +
                            "4. Most viruses cannot pass over walls but some are a stronger strain of Covid, so they might just pass over walls, so be careful.",
                    "RULES", JOptionPane.INFORMATION_MESSAGE);


            //Calling the name class which is where the players enter their names
            /*Name myFrame = new Name();
            myFrame.setTitle("Name");
            myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);*/



        });
    }
}
