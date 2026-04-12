package ie.universityofgalway.projecttrackingsystem.specification;

import ie.universityofgalway.projecttrackingsystem.domain.core.Project;
import ie.universityofgalway.projecttrackingsystem.dto.ProjectSearchCriteria;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecification {

    public static Specification<Project> search(ProjectSearchCriteria criteria) {

        return (root, query, cb) -> {

            var predicates = cb.conjunction();

            // Title search
            if (criteria.getTitle() != null && !criteria.getTitle().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + criteria.getTitle().toLowerCase() + "%"
                        )
                );
            }

            // Client filter
            if (criteria.getClientContactName() != null && !criteria.getClientContactName().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(
                                cb.lower(root.get("clientContact").get("name")),
                                "%" + criteria.getClientContactName().toLowerCase() + "%"
                        )
                );
            }
            // Status filter
            if (criteria.getStatus() != null && !criteria.getStatus().isBlank()) {
                predicates = cb.and(predicates,
                        cb.equal(
                                cb.upper(root.get("status").get("name")),
                                criteria.getStatus()
                        )
                );
            }

            // Start date range
            if (criteria.getStartDateFrom() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(
                                root.get("startDate"),
                                criteria.getStartDateFrom()
                        )
                );
            }

            if (criteria.getStartDateTo() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(
                                root.get("startDate"),
                                criteria.getStartDateTo()
                        )
                );
            }

            // Has invoices filter
            if (criteria.getHasInvoices() != null) {

                var invoicesJoin = root.join("invoices", JoinType.LEFT);

                if (criteria.getHasInvoices()) {
                    predicates = cb.and(predicates,
                            cb.isNotNull(invoicesJoin.get("id")));
                } else {
                    predicates = cb.and(predicates,
                            cb.isNull(invoicesJoin.get("id")));
                }

                query.distinct(true);
            }

            // Has expenses or outlays filter (CostItems)
            if (criteria.getHasExpenses() != null) {

                var costJoin = root.join("costItems", JoinType.LEFT);

                if (criteria.getHasExpenses()) {
                    predicates = cb.and(predicates,
                            cb.isNotNull(costJoin.get("id")));
                } else {
                    predicates = cb.and(predicates,
                            cb.isNull(costJoin.get("id")));
                }

                query.distinct(true);
            }

            return predicates;
        };
    }
}