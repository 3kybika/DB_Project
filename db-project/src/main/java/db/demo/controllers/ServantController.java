package db.demo.controllers;

import db.demo.services.ServantService;
import db.demo.views.StatusModel;
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
        long st = System.nanoTime();
        StatusModel stat = servantService.getStatus();
        System.out.println("getStatus:" + (System.nanoTime() - st));

        return ResponseEntity.status(HttpStatus.OK).body(stat);
    }

    @PostMapping(value = "/clear")
    public ResponseEntity clearDatabase() {
        servantService.clearDatabase();
        return ResponseEntity.status(HttpStatus.OK).body("Database was cleared!");
    }
}
