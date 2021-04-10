public class PlayerScores {
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
