package io.bartmilo.student.enrolment.app.domain.rental;

import io.bartmilo.student.enrolment.app.domain.rental.mapper.RentalMapper;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalRequest;
import io.bartmilo.student.enrolment.app.domain.rental.model.RentalResponse;
import io.bartmilo.student.enrolment.app.domain.rental.service.RentalService;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/rentals")
public class RentalController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RentalController.class);

  private final RentalService rentalService;
  private final RentalMapper rentalMapper;

  public RentalController(RentalService rentalService, RentalMapper rentalMapper) {
    this.rentalService = rentalService;
    this.rentalMapper = rentalMapper;
  }

  @PostMapping
  public ResponseEntity<RentalResponse> rentBook(@RequestBody RentalRequest rentalRequest) {
    LOGGER.info("Request to rent book: {}", rentalRequest);
    var rentalDto =
        rentalService.rentBook(
            rentalRequest.bookId(), rentalRequest.studentId(), rentalRequest.dueDate());
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(rentalDto.getId())
            .toUri();
    LOGGER.info("Rental created successfully: {}", rentalDto);
    var rentalResponse = rentalMapper.convertDtoToResponse(rentalDto);
    return ResponseEntity.created(location).body(rentalResponse);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RentalResponse> returnBook(@PathVariable Long id) {
    LOGGER.info("Initiating return process for rental ID: {}", id);
    var rentalDto = rentalService.returnBook(id);
    LOGGER.info("Book returned successfully for rental ID: {}", id);
    var rentalResponse = rentalMapper.convertDtoToResponse(rentalDto);
    return ResponseEntity.status(HttpStatus.OK).body(rentalResponse);
  }
}
