import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.Timer;
import javax.sound.sampled.*;


public class CE203_ss19021_Ass2 extends JFrame{

//Code was inspired by http://zetcode.com/javagames/pacman/
//Code was modified to fit the assignment brief and additional features were added



        public CE203_ss19021_Ass2() {

            KillerGUI();
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



        public static void main(String[] args) {

            //Displaying the game window first
            var banana = new CE203_ss19021_Ass2();
            banana.setVisible(true);

            //Using EventQueue.invokeLater() to make sure the name class and rules pop up in front of the game window instead of behind it
            EventQueue.invokeLater(() -> {
                //Using a JOption pane to show the rules
                JOptionPane.showMessageDialog(null,
                        "1. You have to collect all the vaccines to win the game.\n" +
                                "2. Once you have collected all the vaccines, the game restarts and the number of viruses increases by 1.\n" +
                                "3. If a virus touches you, you lose a life.\n" +
                                "4. Most viruses cannot pass over walls but some are a stronger strain of Covid, so they might just pass over walls, so be careful.\n" +
                                "5. You can press the escape button or the left mouse button to exit the game",
                        "RULES", JOptionPane.INFORMATION_MESSAGE);


            });
        }


    class KillerCovid extends JPanel implements ActionListener {

        private final int blockSize = 30;
        private final int no_Blocks = 15;
        private final int screenSize = no_Blocks * blockSize;
        private final int max_Viruses = 12;
        private int human_Speed = 6;
        private final int validSpeeds[] = {1, 2, 3, 4, 6, 8};
        private final int maxSpeed = 6;


        private int[] dx, dy; //needed for position of the virus
        private int[] virus_x, virus_y, virus_dx, virus_dy, virusSpeed; //needed to determine number and positions of ghost

        private Dimension dim;
        private final Font smallFont = new Font("Helvetica", Font.BOLD, 14); //font for start screen
        private Color mazeBorderColor;

        private int human_x, human_y, human_dx, human_dy; //stores x, y, and delta changes in vertical and horizontal directions of the human sprite
        private int req_dx, req_dy;
        private int no_Viruses = 6;
        private int currentSpeed = 3;
        int livesLeft, score, endScore, endScore1;

        private boolean inGame = false;
        private boolean die = false;

        private Image human, humanLeft, humanRight, humanDown;
        private Image virus;
        private Image vaccine;
        private Image lives;

        //file paths for images
        String humanFile = "src/assets/pics/GokuRun.gif";
        String humanDownFile = "src/assets/pics/GokuRun1.gif";
        String humanLeftFile = "src/assets/pics/GokuRun1.gif";
        String humanRightFile = "src/assets/pics/GokuRun.gif";
        String vaccineFile = "src/assets/pics/vaccine1.png";
        String virusFile = "src/assets/pics/CovidVirus.gif";
        String livesFile = "src/assets/pics/goku4.png";

        //What the board will look like
        private final short levelData[] = {
                19, 26, 26, 18, 26, 26, 22, 0, 19, 26, 26, 18, 26, 26, 22,
                21, 0, 0, 21, 0, 0, 5, 0, 5, 0, 0, 21, 0, 0, 21,
                21, 0, 0, 17, 18, 10, 8, 26, 8, 10, 18, 20, 0, 0, 21,
                21, 0, 0, 17, 20, 0, 0, 0, 0, 0, 17, 20, 0, 0, 21,
                17, 2, 2, 24, 24, 22, 0, 0, 0, 19, 24, 24, 2, 2, 20,
                17, 24, 20, 0, 0, 25, 22, 0, 19, 28, 0, 0, 17, 24, 20,
                21, 0, 21, 0, 0, 0, 1, 18, 4, 0, 0, 0, 21, 0, 21,
                5, 0, 17, 26, 22, 0, 25, 16, 28, 0, 19, 26, 20, 0, 5,
                5, 0, 21, 0, 21, 0, 4, 29, 1, 0, 21, 0, 21, 0, 5,
                5, 0, 21, 0, 17, 2, 30, 0, 27, 2, 20, 0, 21, 0, 5,
                5, 0, 5, 0, 17, 20, 0, 0, 0, 17, 20, 0, 5, 0, 5,
                21, 0, 1, 18, 24, 0, 10, 10, 10, 0, 24, 18, 4, 0, 21,
                17, 26, 24, 28, 0, 21, 0, 0, 0, 21, 0, 25, 24, 26, 20,
                21, 0, 0, 0, 0, 1, 22, 0, 19, 4, 0, 0, 0, 0, 21,
                25, 26, 26, 26, 26, 24, 28, 8, 25, 24, 26, 26, 26, 26, 28
        };

        private short[] dataOnScreen;
        private Timer timer;

        //Adding all relevant functions and components to the KillerCovid constructor
        public KillerCovid() {

            loadedImages();
            initialisedVariables();
            addKeyListener(new Controller());
            addMouseListener(new MouseClick());
            setFocusable(true);
            setBackground(Color.black);
            initialisedGame();
            run();
            imageErrorHandling(humanFile);
            imageErrorHandling(humanDownFile);
            imageErrorHandling(humanLeftFile);
            imageErrorHandling(humanRightFile);
            imageErrorHandling(vaccineFile);
            imageErrorHandling(virusFile);
            imageErrorHandling(livesFile);


        }



        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.black);
            g2d.fillRect(0, 0, dim.width, dim.height);

            drawMaze(g2d);
            drawScore(g2d);

            if (inGame) {
                playingGame(g2d);
            } else {
                IntroScreen(g2d);
            }

            //synchronizes the graphics state
            Toolkit.getDefaultToolkit().sync();
            //Disposes of this graphics context and releases any system resources that it is using
            g2d.dispose();
        }

        //A class for image error handling
        private void imageErrorHandling(String filename){
            boolean check = false;
            Image image = new ImageIcon(filename).getImage();
            //checks id the width of the image is -1.
            if(!(image.getWidth(null) == -1)){
                check = true;
            }
            String errorMessage = filename + " cannot be found. Please check the assets folder to ensure the image is present.\n" + "Exiting program....";
            //if the width of the image is -1, it means the image does not exist and an error message saying which image does not exist pops up
            if(!check){
                JOptionPane.showMessageDialog(null, errorMessage, "Image Error", JOptionPane.ERROR_MESSAGE);
                System.exit(-1);
            }
        }

        //Getting the images from the src folder
        private void loadedImages() {

            human = new ImageIcon(humanFile).getImage();
            humanDown = new ImageIcon(humanDownFile).getImage();
            humanLeft = new ImageIcon(humanLeftFile).getImage();
            humanRight = new ImageIcon(humanRightFile).getImage();
            vaccine = new ImageIcon(vaccineFile).getImage();
            virus = new ImageIcon(virusFile).getImage();
            lives = new ImageIcon(livesFile).getImage();


        }

        //Initialising all the variables needed for the game
        private void initialisedVariables() {

            //This is the amount of block needed width and height wise
            dataOnScreen = new short[no_Blocks * no_Blocks];
            mazeBorderColor = new Color(245, 28, 12);
            dim = new Dimension(400, 400);
            virus_x = new int[max_Viruses];
            virus_y = new int[max_Viruses];
            virus_dx = new int[max_Viruses];
            virus_dy = new int[max_Viruses];
            virusSpeed = new int[max_Viruses];
            dx = new int[4];
            dy = new int[4];

            //The images are redrawn every 60 milliseconds and the timer is started
            timer = new Timer(60, this);
            timer.start();
        }

        private void IntroScreen(Graphics2D g2d) {

            //When the game window  pops up, there will be an intro screen asking the user to press enter to start the game
            //Setting the background colour to black and the border colour to red
            g2d.setColor(new Color(0, 0, 0));
            g2d.fillRect(50, screenSize / 2 - 30, screenSize - 100, 50);
            g2d.setColor(new Color(51, 42, 42));
            g2d.drawRect(50, screenSize / 2 - 30, screenSize - 100, 50);


            //Starting text on the intro screen
            String s = "Press ENTER to start.";
            Font small = new Font("Helvetica", Font.BOLD, 14);
            FontMetrics metric = this.getFontMetrics(small);

            //Setting the colour of the text to red
            g2d.setColor(new Color(255, 17, 0));
            g2d.setFont(small);
            g2d.drawString(s, (screenSize - metric.stringWidth(s)) / 2, screenSize / 2);

        }

        //A function used to draw the score of the player at the bottom right of the window
        private void drawScore(Graphics2D g) {

            int i;
            String s;

            g.setFont(smallFont);
            g.setColor(new Color(130, 12, 12));
            s = "Score: " + score;
            g.drawString(s, screenSize / 2 + 96, screenSize + 16);

            endScore1 = score;

            //Checks to see how many lives the Human has left and displays it on the bottom left of the window
            for (i = 0; i < livesLeft; i++) {
                g.drawImage(lives, i * 28 + 8, screenSize + 1, this);
            }

        }

        //A function that gets the score of the player when all lives are lost
        private void outputScore(){
            Name1 name1 = new Name1();


            if(livesLeft == 0){
                Name1 n = new Name1();
                n.name();
                String name = name1.name;

                //When all the players lives have finished, the score is obtained and written to the Scores.txt file
                endScore = score;
                try {
                    FileWriter writer = new FileWriter("src/Scores.txt", true);
                    writer.write(name + ": " + endScore);
                    writer.write("\r\n");   // write new line

                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }


                //Sorting the players scores from highest to lowest
                try {
                    java.util.List<PlayerScores> playerScore;
                    //Read all lines from the Score.txt file as a stream and puts them in a collection toList()
                    playerScore = Files.lines(Path.of("src/Scores.txt")).map(PlayerScores::parseLine).collect(Collectors.toList());

                    //Sorting the list in descending order using the scores
                    Collections.sort(playerScore, Collections.reverseOrder(Comparator.comparingInt(PlayerScores::getScore)));

                    //Takes the sorted scores and writes them back to the Scores.txt file
                    java.util.List<String> lines = playerScore.stream().map(PlayerScores::toLine).collect(Collectors.toList());
                    Files.write(Path.of("src/Scores.txt"), lines);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Calls the HighestScores function
                try {
                    HighestScores();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        //A function for getting the high scores from the Scores.txt file and displaying it using a JOption pane when the game ends
        public void HighestScores() throws IOException{
            String input = "";
            int i =0;

            Scanner scanner = new Scanner(new File("src/Scores.txt"));

            //Reads through the file until there are no more lines to read in the file
            String line = "";
            while (scanner.hasNextLine() & i <5) {
                line = scanner.nextLine();

                //Add the line and then "\n" indicating a new line
                input += line + "\n";
                i+=1;


            }

            scanner.close();

            //A JOption pane then pops up with the 5 highest scores with the players names and when the ok button is pressed, the system closes
            int input_options = JOptionPane.showOptionDialog(null,input,"High Scores", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, null, null);

            if(input_options == JOptionPane.OK_OPTION){
                System.exit(1);
            }


        }


        //A function used when playing the game
        private void playingGame(Graphics2D g2d) {

            //When the human dies, the human loses a life
            if (die) {

                livesLeft--;

                //When all the humans lives have been used up, the game ends
                if(livesLeft == 0){


                    endGame();
                }

                //If there are still lives left, the human and viruses start at the starting positions again
                continueLevel();

            } else {

                moveHuman();
                drawHuman(g2d);
                moveVirus(g2d);
                checkMaze();
            }
        }

        //Initialises the game
        private void initialisedGame() {

            //the starting no of viruses is 6
            //The players speed is 3
            //Lives left is 3 and the players score starts at 0
            no_Viruses = 6;
            currentSpeed = 3;
            livesLeft = 3;
            score = 0;
            initialisedLevel();

        }

        //Initialises the level of the game by using the screen data
        private void initialisedLevel() {

            int i;
            for (i = 0; i < no_Blocks * no_Blocks; i++) {
                dataOnScreen[i] = levelData[i];
            }

            continueLevel();
        }

        private void continueLevel() {

            int dx = 1;
            int random;

            //Determines the position of the viruses when the game starts
            for (int i = 0; i < no_Viruses; i++) {

                virus_x[i] = 4 * blockSize;
                virus_y[i] = 4 * blockSize;
                virus_dx[i] = dx;
                virus_dy[i] = 0;
                dx = -dx;
                random = (int) (Math.random() * (currentSpeed + 1));

                //Assigns random speeds to the viruses
                if (random > currentSpeed) {
                    random = currentSpeed;
                }

                //The virus is only allowed to have random speeds that are initialised in the validSpeeds array
                virusSpeed[i] = validSpeeds[random];
            }

            //Determines the start position of the human when the game starts
            human_x = 7 * blockSize;
            human_y = 11 * blockSize;
            human_dx = 0;
            human_dy = 0;
            req_dx = 0;
            req_dy = 0;
            die = false;
        }


        //A function that is called to end the game when the player loses all lives
        private void endGame(){

            //The output score function is called to get the score at the end of the game and write it to the Score.txt file
            outputScore();

            //Ends the game
            inGame = false;

        }

        //A function for drawing the maze
        private void drawMaze(Graphics2D g2d) {

            short i = 0;
            int x, y;

            //This draws the x and y axis of the array by iterating through the arrays
            for (y = 0; y < screenSize; y += blockSize) {
                for (x = 0; x < screenSize; x += blockSize) {

                    //Colour of border
                    g2d.setColor(mazeBorderColor);
                    //width of border
                    g2d.setStroke(new BasicStroke(2));

                    //A left border is drawn
                    if ((dataOnScreen[i] & 1) != 0) {
                        g2d.drawLine(x, y, x, y + blockSize - 1);
                    }

                    //A top border is drawn
                    if ((dataOnScreen[i] & 2) != 0) {
                        g2d.drawLine(x, y, x + blockSize - 1, y);
                    }

                    //A right border is drawn
                    if ((dataOnScreen[i] & 4) != 0) {
                        g2d.drawLine(x + blockSize - 1, y, x + blockSize - 1,
                                y + blockSize - 1);
                    }

                    //A bottom border is drawn
                    if ((dataOnScreen[i] & 8) != 0) {
                        g2d.drawLine(x, y + blockSize - 1, x + blockSize - 1,
                                y + blockSize - 1);
                    }

                    //A picture of a vaccine is used if there is a point available to be eaten
                    if ((dataOnScreen[i] & 16) != 0) {
                        g2d.drawImage(vaccine,x,y,this);

                    }

                    i++;
                }
            }
        }

        //Checks if there are vaccines left for human to collect
        private void checkMaze() {

            int i = 0;
            boolean finished = true;

            while (i < no_Blocks * no_Blocks && finished) {

                if ((dataOnScreen[i] & 48) != 0) {
                    finished = false;
                }

                i++;
            }



            //If all vaccines are collected, the game restarts.
            //Score is increased by 20
            //The speed and number of viruses increases by one
            if (finished) {

                score += 20;

                //If the number of viruses is less than the maximum amount of viruses available then the number of viruses when the next level starts increases by 1
                if (no_Viruses < max_Viruses) {
                    no_Viruses++;
                }

                //If the current speed is less than the maximum speed available then the current speed when the next level starts increases by 1
                if (currentSpeed < maxSpeed) {
                    currentSpeed++;
                }

                //The next level is initialised
                initialisedLevel();
            }
        }


        //A function used for moving the virus
        public void moveVirus(Graphics2D g2d) {

            int position;
            int count;

            //The virus can only move 1 block at a time and may change direction
            for (int i = 0; i < no_Viruses; i++) {
                if (virus_x[i] % blockSize == 0 && virus_y[i] % blockSize == 0) {
                    position = virus_x[i] / blockSize + no_Blocks * (int) (virus_y[i] / blockSize);

                    count = 0;

                    //Using border information to determine how the virus can move
                    //Ex. if a virus comes into contact with a border in front of it, it may change direction to left or right
                    if ((dataOnScreen[position] & 1) == 0 && virus_dx[i] != 1) {
                        dx[count] = -1;
                        dy[count] = 0;
                        count++;
                    }

                    if ((dataOnScreen[position] & 2) == 0 && virus_dy[i] != 1) {
                        dx[count] = 0;
                        dy[count] = -1;
                        count++;
                    }

                    if ((dataOnScreen[position] & 4) == 0 && virus_dx[i] != -1) {
                        dx[count] = 1;
                        dy[count] = 0;
                        count++;
                    }

                    if ((dataOnScreen[position] & 8) == 0 && virus_dy[i] != -1) {
                        dx[count] = 0;
                        dy[count] = 1;
                        count++;
                    }

                    //The lines determine where the viruses are located
                    if (count == 0) {

                        if ((dataOnScreen[position] & 15) == 15) {
                            virus_dx[i] = 0;
                            virus_dy[i] = 0;
                        } else {
                            virus_dx[i] = -virus_dx[i];
                            virus_dy[i] = -virus_dy[i];
                        }

                    } else {

                        //Randomly assigns count a number
                        count = (int) (Math.random() * count);

                        if (count > 3) {
                            count = 3;
                        }

                        virus_dx[i] = dx[count];
                        virus_dy[i] = dy[count];
                    }

                }

                //The virus speed are randomly assigned
                //Some viruses may move fast and some may move slowly
                virus_x[i] = virus_x[i] + (virus_dx[i] * virusSpeed[i]);
                virus_y[i] = virus_y[i] + (virus_dy[i] * virusSpeed[i]);
                drawVirus(g2d, virus_x[i] + 1, virus_y[i] + 1);

                //If human touches the virus, human loses a life
                if (human_x > (virus_x[i] - 12) && human_x < (virus_x[i] + 12)
                        && human_y > (virus_y[i] - 12) && human_y < (virus_y[i] + 12)
                        && inGame) {

                    //Connected to the playingGame function
                    die = true;
                }

            }
        }


        //Uses the image initialised in the loadedImages function to draw the virus
        private void drawVirus(Graphics2D g2d, int x, int y) {

            g2d.drawImage(virus, x, y, this);

        }


        //A function for moving the human
        private void moveHuman() {

            int position;
            short ch;

            if (req_dx == -human_dx && req_dy == -human_dy) {
                human_dx = req_dx;
                human_dy = req_dy;

            }

            //If the Human collects a vaccine, the score of the player increases by 1
            if (human_x % blockSize == 0 && human_y % blockSize == 0) {
                position = human_x / blockSize + no_Blocks * (int) (human_y / blockSize);
                ch = dataOnScreen[position];

                if ((ch & 16) != 0) {
                    dataOnScreen[position] = (short) (ch & 15);
                    score++;
                }


                //If human is at a border, human cannot move past the border
                if (req_dx != 0 || req_dy != 0) {
                    if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                            || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                            || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                            || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                        human_dx = req_dx;
                        human_dy = req_dy;

                    }
                }

                //Checks if human is standing still
                if ((human_dx == -1 && human_dy == 0 && (ch & 1) != 0)
                        || (human_dx == 1 && human_dy == 0 && (ch & 4) != 0)
                        || (human_dx == 0 && human_dy == -1 && (ch & 2) != 0)
                        || (human_dx == 0 && human_dy == 1 && (ch & 8) != 0)) {
                    human_dx = 0;
                    human_dy = 0;
                }
            }

            //Human speed is adjusted
            human_x = human_x + human_Speed * human_dx;
            human_y = human_y + human_Speed * human_dy;
        }

        //A function for drawing the human
        public void drawHuman(Graphics2D g2d){

            //If the human runs left, the human faces left
            if(req_dx == -1){
                g2d.drawImage(humanLeft, human_x + 1, human_y + 1, this);
            }
            //If the human runs right, the human faces right
            else if(req_dx == 1){
                g2d.drawImage(humanRight, human_x + 1, human_y + 1, this);
            }
            //If the human runs up, the human faces right
            else if(req_dy == -1){
                g2d.drawImage(human, human_x + 1, human_y + 1, this);
            }
            //If the human runs down, the human faces left
            else if(req_dy == 1){
                g2d.drawImage(humanDown, human_x + 1, human_y + 1, this);
            }
            else{
                g2d.drawImage(human, human_x + 1, human_y + 1, this);
            }
        }




        //Adds background music to the game
        //calls the background music class and the .wav file path
        BackgroundMusic Music = new BackgroundMusic("src/assets/audio/GameAudio.wav");

        //Uses a run function that is added to the KillerCovid constructor
        public void run() {
            try {
                //Calls the loop function from the Background music class to loop the background music
                Music.loop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //A class for controlling the character using the cursor keys
        class Controller extends KeyAdapter {
            boolean pause = false;

            @Override
            public void keyPressed(KeyEvent e) {


                int key = e.getKeyCode();
                int key1 = e.getKeyCode();

                if (inGame) {
                    //When the left cursor key is pressed, the character moves left
                    if (key == KeyEvent.VK_LEFT) {
                        req_dx = -1;
                        req_dy = 0;
                        //human_Speed = 6;
                    }
                    //When the right cursor key is pressed, the character moves right
                    else if (key == KeyEvent.VK_RIGHT) {
                        req_dx = 1;
                        req_dy = 0;
                        //human_Speed = 6;
                    }
                    //When the up cursor key is pressed, the character moves up
                    else if (key == KeyEvent.VK_UP) {
                        req_dx = 0;
                        req_dy = -1;
                        //human_Speed = 6;
                    }
                    //When the down cursor key is pressed, the character moves down
                    else if (key == KeyEvent.VK_DOWN) {
                        req_dx = 0;
                        req_dy = 1;
                        //human_Speed = 6;
                    }
                    //When the escape key is pressed, the game ends
                    else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                        inGame = false;
                    }


                } else {

                    //When the enter key is pressed, the game starts
                    if(key == KeyEvent.VK_ENTER) {
                        inGame = true;
                        initialisedGame();
                    }
                }
                //When the space key is pressed, the game pauses
                if (key1 == KeyEvent.VK_PAUSE) {
                    if(timer.isRunning()){
                        timer.stop();
                    }else{
                        timer.start();
                    }
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();

                //When the right cursor key is not pressed, the character stops moving
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = 0;
                    req_dy = 0;
                    //human_Speed = 0;
                }
                //When the right cursor key is not pressed, the character stops moving
                else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 0;
                    req_dy = 0;
                    //human_Speed = 0;
                }
                //When the right cursor key is not pressed, the character stops moving
                else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = 0;
                    //human_Speed = 0;
                }
                //When the right cursor key is not pressed, the character stops moving
                else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 0;
                    //human_Speed = 0;
                }
            }


        }

        //A class for using the left mouse button to exit the game
        class MouseClick implements MouseListener {

            @Override
            public void mouseClicked(MouseEvent e) {


                if(e.getButton() == MouseEvent.BUTTON1)
                {
                    //game exits if mouse button is clicked
                    inGame = false;
                    System.exit(1);
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        }




        //Repaint is called to ensure that the images get repainted everytime a move is made
        @Override
        public void actionPerformed(ActionEvent e) {

            repaint();
        }
    }

    class BackgroundMusic {
        private Clip clip;
        public BackgroundMusic(String fileName) {
            //Using try catch as error handling when opening the .wav file for the background music
            try {
                File file = new File(fileName);
                if (file.exists()) {
                    AudioInputStream sound = AudioSystem.getAudioInputStream(file);
                    // load the sound into memory in the form of a Clip
                    clip = AudioSystem.getClip();
                    clip.open(sound);
                }
                else {
                    throw new RuntimeException("Sound: file not found: " + fileName);
                }
            }

            catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
                e.printStackTrace();
                throw new RuntimeException("Sound: Input/Output Error: " + e);
            }

        }

        //looping the sound file so that the background music replays even when the music finishes
        public void loop(){
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }

    }


    static class Name1 {

        public static String name;

        //Gets the name of the player using a JOptionPane and stores the variable in a string called name
        public void name(){
            String name = JOptionPane.showInputDialog("Enter your name: ", JOptionPane.OK_OPTION);
            Name1.name = name;
        }


    }

    static class PlayerScores {

        String name;
        int score;

        public PlayerScores(String name, int score){
            this.name = name;
            this.score = score;
        }

        //Functions used to get the name and score
        public String toLine(){

            return name + ": " + score;
        }

        //Function used to get the score
        public int getScore(){

            return score;
        }

        //Splitting the data by the : and assigning name and putting the name and scores into arrays so it is easier to access
        public static PlayerScores parseLine(String line) {
            String[] data = line.split(": ");
            String name = data[0];
            int score = Integer.parseInt(data[1]);
            return new PlayerScores(name, score);
        }
    }

}
