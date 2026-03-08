package ie.universityofgalway.projecttrackingsystem.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Custom error controller that routes to appropriate error pages based on HTTP status code.
 * Replaces the default Spring Boot Whitelabel Error Page.
 */
@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        try {
            Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            Object error = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

            int statusCode = 500; // default
            if (status != null) {
                statusCode = Integer.parseInt(status.toString());
            }

            model.addAttribute("status", statusCode);
            model.addAttribute("error", error != null ? error : "An error occurred");
            model.addAttribute("path", path);
            model.addAttribute("timestamp", java.time.LocalDateTime.now());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }

            // Fallback to generic error page for other status codes
            return "error";
        } catch (Exception e) {
            // If anything goes wrong in error handling, return generic error page
            model.addAttribute("status", 500);
            model.addAttribute("error", "An unexpected error occurred");
            model.addAttribute("timestamp", java.time.LocalDateTime.now());
            return "error";
        }
    }

    /**
     * Direct routes for error pages (used by Spring Security and direct access).
     */
    @RequestMapping("/error/403")
    public String accessDenied(Model model) {
        model.addAttribute("status", 403);
        model.addAttribute("error", "Access Denied");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error/403";
    }

    @RequestMapping("/error/404")
    public String notFound(Model model) {
        model.addAttribute("status", 404);
        model.addAttribute("error", "Page Not Found");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error/404";
    }

    @RequestMapping("/error/500")
    public String serverError(Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("error", "Internal Server Error");
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "error/500";
    }
}
