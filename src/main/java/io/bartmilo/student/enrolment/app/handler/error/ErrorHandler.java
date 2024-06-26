package io.bartmilo.student.enrolment.app.handler.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorHandler implements ErrorController {

  @GetMapping("/error")
  public String handleError() {
    // This will forward to /error.html without changing the URL in the browser
    return "forward:/error.html";
  }
}
