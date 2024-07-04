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
@SessionAttributes(value = "song")
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
        service.stopListening();
        model.addAttribute("song",service.recognizeSound());
        return "redirect:/Soundy/matching";
    }

    @PostMapping ("/add")
    @ResponseBody
    public String addSound(@RequestParam String title) throws LineUnavailableException{
        return service.addSound(title);
    }

    @ModelAttribute("song")
    public String Song(){
        return null;
    }
}

