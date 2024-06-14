package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.dto.RawOrderInsertDTO;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RawRegister {

    private final RawOrderInsertService rawOrderInsertService;

    @PostMapping("/register")
    public ResponseEntity<?> insertRawOrder(@RequestBody RawOrderInsertDTO rawOrderInsertDTO) {
        rawOrderInsertService.insert(rawOrderInsertDTO);
        return new ResponseEntity<>("Raw order inserted successfully", HttpStatus.CREATED);
    }
}
