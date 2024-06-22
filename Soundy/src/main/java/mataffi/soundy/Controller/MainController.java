package mataffi.soundy.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/Soundy")
    public String getMainPage(){
        return "MainPage";
    }
}
