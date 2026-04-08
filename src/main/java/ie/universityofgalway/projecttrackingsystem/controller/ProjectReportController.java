package ie.universityofgalway.projecttrackingsystem.controller;

import ie.universityofgalway.projecttrackingsystem.domain.core.Employee;
import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.domain.core.ProjectReportDocument;
import ie.universityofgalway.projecttrackingsystem.domain.lookup.DocumentType;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectRepository;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.DocumentTypeRepository;
import ie.universityofgalway.projecttrackingsystem.service.EmployeeService;
import ie.universityofgalway.projecttrackingsystem.service.ProjectReportDocumentService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class ProjectReportController {

    private final ProjectRepository projectRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProjectReportDocumentService projectReportDocumentService;
    private final EmployeeService employeeService;

    // Constructor
    public ProjectReportController(
            ProjectRepository projectRepository,
            DocumentTypeRepository documentTypeRepository,
            ProjectReportDocumentService projectReportDocumentService,
            EmployeeService employeeService
    ) {
        this.projectRepository = projectRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.projectReportDocumentService = projectReportDocumentService;
        this.employeeService = employeeService;
    }

    // Upload Report
    @PostMapping("/projects/{projectId}/reports/upload")
    public String uploadReport(
            @PathVariable Long projectId,
            @RequestParam("file") MultipartFile file,
            @RequestParam String title,
            @RequestParam Long documentTypeId
    ) throws IOException {

        String uploadDir = System.getProperty("user.dir") + "/uploads/project-reports/";

        File directory = new File(uploadDir);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        File destination = new File(uploadDir + fileName);

        file.transferTo(destination);

        String filePath = "uploads/project-reports/" + fileName;

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        DocumentType type = documentTypeRepository.findById(documentTypeId)
                .orElseThrow(() -> new RuntimeException("Document type not found"));

        // Get logged-in employee
        Employee employee = employeeService.getCurrentEmployee();

        ProjectReportDocument doc = new ProjectReportDocument(
                project,
                type,
                employee,
                title,
                LocalDateTime.now(),
                filePath
        );

        projectReportDocumentService.save(doc);

        return "redirect:/projects/" + projectId;
    }


    @ControllerAdvice
    public class FileUploadExceptionHandler {

        @ExceptionHandler(MaxUploadSizeExceededException.class)
        public String handleMaxSizeException() {
            return "redirect:/error/upload-too-large";
        }
    }

    @GetMapping("/reports/download/{id}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws IOException {

        ProjectReportDocument doc = projectReportDocumentService.getById(id);

        File file = new File(System.getProperty("user.dir") + "/" + doc.getStorageLocation());

        if (!file.exists()) {
            throw new RuntimeException("File not found on disk");
        }

        Resource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
    }