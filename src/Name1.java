import javax.swing.*;

public class Name1 {

    public static String name;

    //Gets the name of the player using a JOptionPane and stores the variable in a string called name
    public void name(){
        String name = JOptionPane.showInputDialog("Enter your name: ", JOptionPane.OK_OPTION);
        Name1.name = name;
    }


}
