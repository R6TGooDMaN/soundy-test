package mataffi.soundy;

import mataffi.soundy.config.KeyPoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;

import static mataffi.soundy.utilities.Serialization.deserializeHashMap;

@SpringBootApplication
public class SoundyApplication {
    public static void main(String[] args) {
        SpringApplication.run(SoundyApplication.class, args);
    }
}
