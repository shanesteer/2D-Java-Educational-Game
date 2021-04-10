import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BackgroundMusic {
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
