package mataffi.soundy.Controller;

import lombok.AllArgsConstructor;
import mataffi.soundy.service.SoundyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.LineUnavailableException;

@Controller
@RequestMapping("/Soundy")
@AllArgsConstructor
public class MainController {
    SoundyService service;

    @GetMapping
    public String getMainPage(){
        return "MainPage";
    }

    @GetMapping("/matching")
    public String matchingSong(){
        return "SearchPage";
    }
    @PostMapping("/matching")
    public String recognizeSound(Model model) throws LineUnavailableException {
        model.addAttribute("song",service.recognizeSound());
        return "SearchPage";
    }

    @GetMapping("/add")
    public String addNewSong(Model model){
        model.addAttribute("name",new String());
        return "AdditionPage";
    }
    @PostMapping ("/add")
    public String addSound(@RequestParam("name") String name) throws LineUnavailableException{
        service.addSound(name);
        return "AdditionPage";
    }
}
