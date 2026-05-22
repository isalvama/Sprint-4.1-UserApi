package cat.itacademy.s04.t01.userapi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/health")
    public Status healthCheck(){
        return new Status(StatusType.OK);
    }
}