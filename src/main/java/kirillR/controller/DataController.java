package kirillR.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import kirillR.dto.UserDto;
import kirillR.repository.UserRepository;
import kirillR.util.XssSanitizer;

@RestController
@RequestMapping("/api")
public class DataController {

    private final UserRepository userRepository;

    public DataController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/data")
    public List<UserDto> getData() {
        return userRepository.findAll().stream()
                .map(user -> new UserDto(user.getId(), XssSanitizer.sanitize(user.getUsername())))
                .toList();
    }
}
