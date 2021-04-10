import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

public class ButtonHandler implements ActionListener {

    Name myApp;

    public ButtonHandler(Name app){
        myApp = app;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = myApp.input.getText();

        //Pressing the enter button from the name class
        if(e.getSource() == myApp.enter){

            //Gets the players name from the name class and writes the name to the Scores.txt file
            try {
                FileWriter writer = new FileWriter("src/Scores.txt", true);
                writer.write(name + ": ");

                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            //Makes the window disappear when the enter button is pressed
            myApp.setVisible(false);

        }

    }
}
