package mataffi.soundy.service;

import lombok.AllArgsConstructor;
import mataffi.soundy.model.AudioRecognizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sound.sampled.LineUnavailableException;
@Service
@AllArgsConstructor
public class SoundyService {

    AudioRecognizer audioRecognizer;
    public String recognizeSound() throws LineUnavailableException {
        audioRecognizer.listening("",true);
        return "Matching finished";
    }
    public String addSound(String title) throws LineUnavailableException {
        audioRecognizer.listening(title,false);
        return "Song added" + title;
    }
}
