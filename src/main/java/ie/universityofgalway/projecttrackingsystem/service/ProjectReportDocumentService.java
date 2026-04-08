package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.ProjectReportDocument;
import ie.universityofgalway.projecttrackingsystem.repository.core.ProjectReportDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectReportDocumentService {

    private final ProjectReportDocumentRepository repository;

    public ProjectReportDocumentService(ProjectReportDocumentRepository repository) {
        this.repository = repository;
    }

    public List<ProjectReportDocument> getDocumentsForProject(Long projectId) {
        return repository.findByProjectId(projectId);
    }

    public ProjectReportDocument save(ProjectReportDocument document) {
        return repository.save(document);
    }

    public ProjectReportDocument getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}