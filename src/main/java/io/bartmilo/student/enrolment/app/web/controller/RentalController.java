package io.bartmilo.student.enrolment.app.web.controller;

import io.bartmilo.student.enrolment.app.domain.dto.RentalDto;
import io.bartmilo.student.enrolment.app.domain.entity.RentalEntity;
import io.bartmilo.student.enrolment.app.mapper.Mapper;
import io.bartmilo.student.enrolment.app.mapper.impl.RentalMapper;
import io.bartmilo.student.enrolment.app.service.RentalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RentalController.class);

    private final RentalService rentalService;
    private final Mapper<RentalEntity, RentalDto> rentalMapper;

    @Autowired
    public RentalController(RentalService rentalService, RentalMapper rentalMapper) {
        this.rentalService = rentalService;
        this.rentalMapper = rentalMapper;
    }

    /**
     * Endpoint to rent a book.
     * @param rentalDto Contains the 'dueDate' in 'yyyy-MM-dd HH:mm' format.
     * @return The rental details.
     */
    @PostMapping
    public ResponseEntity<RentalDto> rentBook(@RequestBody RentalDto rentalDto) {
        LOGGER.info("Request to rent book: {}", rentalDto);
        var rentalEntity = rentalService.rentBook(
                rentalDto.getBookId(),
                rentalDto.getStudentId(),
                rentalDto.getDueDate()
        );

        var savedRentalDto = rentalMapper.mapFrom(rentalEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(rentalEntity.getId())
                .toUri();

        LOGGER.info("Rental created successfully: {}", savedRentalDto);
        return ResponseEntity
                .created(location)
                .body(savedRentalDto);
    }

    /**
     * Handles the HTTP PUT request to return a book that has been previously rented.
     *
     * @param id the unique identifier of the rental record to be updated as returned
     * @return ResponseEntity<RentalDto> which contains the updated rental details after the book is returned.
     * If successful, the HTTP status is set to OK and includes the updated RentalDto. If any exceptions
     * are encountered (e.g., rental not found or book has already been returned), appropriate HTTP status codes
     * and messages are returned.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RentalDto> returnBook(@PathVariable Long id) {
        LOGGER.info("Initiating return process for rental ID: {}", id);

        var rentalEntity = rentalService.returnBook(id);
        var rentalDto = rentalMapper.mapFrom(rentalEntity);

        LOGGER.info("Book returned successfully for rental ID: {}", id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(rentalDto);
    }
}

