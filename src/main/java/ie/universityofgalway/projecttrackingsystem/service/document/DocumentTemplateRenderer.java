package ie.universityofgalway.projecttrackingsystem.service.document;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service for rendering Thymeleaf templates to HTML strings.
 */
@Service
public class DocumentTemplateRenderer {

    private final TemplateEngine templateEngine;

    /**
     * Constructs a DocumentTemplateRenderer with the provided TemplateEngine.
     * 
     * @param templateEngine the Spring-configured Thymeleaf TemplateEngine
     */
    public DocumentTemplateRenderer(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Renders a Thymeleaf template to an HTML string.
     *
     * @param templateName the name of the template file
     * @param modelAttributeName the name of the model attribute in the template context (e.g., "invoice")
     * @param modelData the data object to be passed to the template
     * @return the rendered HTML string
     * @throws IllegalArgumentException if templateName, modelAttributeName, or modelData is null or blank
     */
    public String render(String templateName, String modelAttributeName, Object modelData) {
        
        // Validate inputs
        if (templateName == null || templateName.isBlank()) {
            throw new IllegalArgumentException("Template Name must not be null or blank");
        }
        
        if (modelAttributeName == null || modelAttributeName.isBlank()) {
            throw new IllegalArgumentException("modelAttributeName must not be null or blank");
        }
        
        if (modelData == null) {
            throw new IllegalArgumentException("modelData must not be null");
        }

        // Create and populate the Thymeleaf context
        Context context = new Context();
        context.setVariable(modelAttributeName, modelData);

        // Process the template and return the rendered HTML
        return templateEngine.process(templateName, context);
    }
}

