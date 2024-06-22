package mataffi.soundy.Controller;

import lombok.AllArgsConstructor;
import mataffi.soundy.SoundyApplication;
import mataffi.soundy.service.SoundyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;

@RestController
@RequestMapping("/Soundy")
@AllArgsConstructor
public class MainController {
    SoundyService service;

    @GetMapping
    public String getMainPage(){
        return "MainPage";
    }
    @GetMapping("/add")
    public String addNewSong(){
        return "AdditionPage";
    }
    @GetMapping("/matching")
    public String matchingSong(){
        return "SearchPage";
    }
    @PostMapping("/matching")
    public String recognizeSound() throws LineUnavailableException {
       return service.recognizeSound();
    }
    @PostMapping ("/add")
    public String addSound(@RequestParam String title) throws LineUnavailableException{
        return service.addSound(title);
    }
}
