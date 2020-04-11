package com.dmslob.controller;

import com.dmslob.domain.Guest;
import com.dmslob.domain.GuestModel;
import com.dmslob.exception.GuestNotFoundException;
import com.dmslob.repository.GuestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/guests")
public class GuestServicesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuestServicesController.class);

    private final GuestRepository guestRepository;

    public GuestServicesController(GuestRepository guestRepository) {
        super();
        this.guestRepository = guestRepository;
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        return new ArrayList<>(this.guestRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Guest> addGuest(@RequestBody GuestModel model) {
        Guest guest = this.guestRepository.save(model.translateModelToGuest());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(guest.getId()).toUri();
        return ResponseEntity.created(location).body(guest);
    }

    @GetMapping("/{id}")
    public Guest getGuest(@PathVariable Long id) {
        Optional<Guest> guest = this.guestRepository.findById(id);
        if (guest.isPresent()) {
            return guest.get();
        }
        throw new GuestNotFoundException("Guest not found with id: " + id);
    }

    @PutMapping("/{id}")
    public Guest updateGuest(@PathVariable Long id, @RequestBody GuestModel model) {
        Optional<Guest> existing = this.guestRepository.findById(id);
        if (!existing.isPresent()) {
            throw new GuestNotFoundException("Guest not found with id: " + id);
        }
        Guest guest = model.translateModelToGuest();
        guest.setId(id);
        return this.guestRepository.save(guest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public void deleteGuest(@PathVariable Long id) {
        Optional<Guest> existing = this.guestRepository.findById(id);
        if (!existing.isPresent()) {
            throw new GuestNotFoundException("Guest not found with id: " + id);
        }
        this.guestRepository.deleteById(id);
    }
}
