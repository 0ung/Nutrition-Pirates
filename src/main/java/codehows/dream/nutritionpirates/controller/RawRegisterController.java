package codehows.dream.nutritionpirates.controller;


import codehows.dream.nutritionpirates.dto.RawOrderInsertDTO;
import codehows.dream.nutritionpirates.service.RawOrderInsertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class RawRegisterController {

    private final RawOrderInsertService rawOrderInsertService;

    @PostMapping("/register")
    public ResponseEntity<?> insertRawOrder(@RequestBody RawOrderInsertDTO rawOrderInsertDTO) {
        try{
            rawOrderInsertService.insert(rawOrderInsertDTO);
            return new ResponseEntity<>("Raw order inserted successfully", HttpStatus.CREATED);

        }catch (IllegalArgumentException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{rawsCode}")
    public ResponseEntity<?> importRaw(@PathVariable(name = "rawsCode") String rawsCode) {
        rawOrderInsertService.rawImport(rawsCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @PatchMapping("/{rawsCode}")
    public ResponseEntity<?> exportRaw(@PathVariable(name = "rawsCode") String rawsCode) {
        rawOrderInsertService.rawExport(rawsCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/{page}")
    public ResponseEntity<?> getList(@PathVariable(name = "page")Optional <Integer> page) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,10);

        try {
            return new ResponseEntity<>(rawOrderInsertService.getRawOrderList(pageable), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/rawstock/{page}")
    public ResponseEntity<?> getRawStockList(@PathVariable(name = "page")Optional <Integer> page) {

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get():0, 10);

        try {
            return new ResponseEntity<>(rawOrderInsertService.getRawStockList(pageable), HttpStatus.OK);

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
