package mataffi.soundy.service;

import lombok.AllArgsConstructor;
import mataffi.soundy.model.AudioRecognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.sound.sampled.LineUnavailableException;
@Service
@AllArgsConstructor
public class SoundyService {

    AudioRecognizer audioRecognizer;
    public String recognizeSound() throws LineUnavailableException {
        audioRecognizer.listening("",true);
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {}
        return audioRecognizer.bestSong;
    }
    public String addSound(String name) throws LineUnavailableException {
        audioRecognizer.listening(name,false);
        return "Song added: " + name;
    }
}
