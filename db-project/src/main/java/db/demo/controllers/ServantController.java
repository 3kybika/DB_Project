package db.demo.controllers;

import db.demo.services.ServantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/service")
@RestController
public class ServantController {

    private ServantService servantService;

    @Autowired
    public ServantController(ServantService servantService) {
        this.servantService = servantService;
    }

    @GetMapping(value = "/status")
    public ResponseEntity getDatabaseStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(servantService.getStatus());
    }

    @PostMapping(value = "/clear")
    public ResponseEntity clearDatabase() {
        servantService.clearDatabase();
        return ResponseEntity.status(HttpStatus.OK).body("Database was cleared!");
    }
}
