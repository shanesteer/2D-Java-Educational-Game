import javax.swing.*;
import java.awt.*;

public class Name extends JFrame {

    public JTextField input;
    JButton enter;

    public Name(){

        createComponents();
    }

    //Function for creating components needed for the JFrame ex. text fields and buttons
    public void createComponents(){

        //created a JPanel for the user input
        JPanel inputPanel = new JPanel();
        //prompts user to enter their name
        JLabel userInput = new JLabel("Enter Your name: ");
        input = new JTextField(20);

        //Setting the layout for the JFrame window and adds the input labels and panels to it
        setLayout(new BorderLayout());
        add(inputPanel, BorderLayout.CENTER);
        inputPanel.setLayout(new FlowLayout());
        inputPanel.add(userInput);
        inputPanel.add(input);

        //Initialising the enter button and connecting it to the button handler class
        enter = new JButton("Enter");

        enter.addActionListener(new ButtonHandler(this));



        //Adding the button to the JFrame window
        JPanel butPanel = new JPanel();
        butPanel.add(enter);
        add(butPanel, BorderLayout.SOUTH);

        //Setting the size  of the JFrame window
        setSize( 400, 100 );
    }


}
