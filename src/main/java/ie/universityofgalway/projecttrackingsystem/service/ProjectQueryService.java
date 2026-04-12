package ie.universityofgalway.projecttrackingsystem.service;

import ie.universityofgalway.projecttrackingsystem.domain.core.*;
import ie.universityofgalway.projecttrackingsystem.dto.*;
import ie.universityofgalway.projecttrackingsystem.repository.core.*;
import ie.universityofgalway.projecttrackingsystem.repository.lookup.*;
import ie.universityofgalway.projecttrackingsystem.specification.ProjectSpecification;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProjectQueryService {


    private final ProjectRepository projectRepository;
    private final CostItemRepository costItemRepository;
    private final TimesheetEntryService timesheetService;
    private final ReceiptRepository receiptRepository;
    private final ProjectFinanceService financeService;

    private final ProjectCategoryRepository categoryRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProjectReportDocumentService projectReportDocumentService;
    private final ProjectStatusRepository statusRepository;
    private final ContactRepository contactRepository;

    // Constructor
    public ProjectQueryService(ProjectRepository projectRepository,
                               CostItemRepository costItemRepository,
                               TimesheetEntryService timesheetService,
                               ReceiptRepository receiptRepository,
                               ProjectFinanceService financeService,
                               ProjectCategoryRepository categoryRepository,
                               DocumentTypeRepository documentTypeRepository,
                               ProjectReportDocumentService projectReportDocumentService,
                               ProjectStatusRepository statusRepository,
                               ContactRepository contactRepository) {

        this.projectRepository = projectRepository;
        this.costItemRepository = costItemRepository;
        this.timesheetService = timesheetService;
        this.receiptRepository = receiptRepository;
        this.financeService = financeService;

        this.categoryRepository = categoryRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.projectReportDocumentService = projectReportDocumentService;
        this.statusRepository = statusRepository;
        this.contactRepository = contactRepository;
    }

    // List
    public List<ProjectView> list() {
        return projectRepository.findAll()
                .stream()
                .map(this::mapToView)
                .toList();
    }

    // Search
    public List<ProjectView> search(ProjectSearchCriteria criteria) {
        return projectRepository.findAll(ProjectSpecification.search(criteria))
                .stream()
                .map(this::mapToView)
                .toList();
    }


    // Details
    public ProjectDetailsView getProjectDetails(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<CostItemView> outlays =
                costItemRepository.findViewsByProjectAndType(project, CostItem.Type.OUTLAY);

        List<CostItemView> expenses =
                costItemRepository.findViewsByProjectAndType(project, CostItem.Type.EXPENSE);

        BigDecimal outlayTotal = financeService.getOutlayTotal(id);
        BigDecimal expenseTotal = financeService.getExpenseTotal(id);
        BigDecimal labourTotal = financeService.getLabourTotal(id);

        BigDecimal totalExVat =
                outlayTotal.add(expenseTotal).add(labourTotal);

        List<TimesheetEntryView> timesheets =
                timesheetService.findByProjectId(id);

        List<Receipt> receipts =
                receiptRepository.findByInvoiceProjectId(id);

        BigDecimal receiptsTotal = financeService.getReceiptsTotal(id);

        BigDecimal totalInvoiced = financeService.getTotalInvoiced(project);
        BigDecimal outstandingInvoices = financeService.getOutstandingInvoices(project);

        List<ProjectReportDocument> reports =
                projectReportDocumentService.getDocumentsForProject(id);

        ProjectDetailsView view = new ProjectDetailsView();

        view.setProject(project);
        view.setOutlays(outlays);
        view.setExpenses(expenses);
        view.setOutlayTotal(outlayTotal);
        view.setExpenseTotal(expenseTotal);
        view.setTimesheets(timesheets);
        view.setLabourTotal(labourTotal);
        view.setTotalExVat(totalExVat);
        view.setReceipts(receipts);
        view.setReceiptsTotal(receiptsTotal);
        view.setTotalInvoiced(totalInvoiced);
        view.setOutstandingInvoices(outstandingInvoices);
        view.setDiscountTotal(financeService.getDiscountsTotal(project.getId()));
        view.setReports(reports);
        view.setDocumentTypes(documentTypeRepository.findAll());

        return view;
    }

    // Project View Retrieve Details
    private ProjectView mapToView(Project project) {

        return new ProjectView(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getStatus() != null ? project.getStatus().getName() : null,

                project.getCategory() != null ? project.getCategory().getName() : null,
                project.getClientContact() != null ? project.getClientContact().getName() : null,
                project.getStartDate(),

                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    // Project search for forms
    public List<ProjectSearchDTO> searchProjectsForAutocomplete(String query) {
        return projectRepository.findByTitleContainingIgnoreCase(query)
                .stream()
                .map(p -> new ProjectSearchDTO(p.getId(), p.getTitle()))
                .toList();
    }

    // Lookups
    public void loadFormLookups(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        model.addAttribute("contacts", contactRepository.findAll());
    }
}