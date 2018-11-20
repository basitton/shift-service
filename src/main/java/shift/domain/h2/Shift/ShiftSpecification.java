package shift.domain.h2.Shift;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import shift.domain.h2.SearchCriteria;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * Implements {@link Specification} for querying the {@link Shift} table
 */
public class ShiftSpecification implements Specification<Shift> {
    private SearchCriteria criteria;

    @Autowired
    public ShiftSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<Shift> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        String operation = criteria.getOperation();

        if (operation.equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getUsername() + "%");
            } else if (root.get(criteria.getKey()).getJavaType() == Long.class) {
                return builder.equal(root.get(criteria.getKey()), criteria.getId());
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getTime());
            }
        } else if (operation.equalsIgnoreCase("<:")) {
            return builder.lessThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getTime());
        } else if (operation.equalsIgnoreCase(">:")) {
            return builder.greaterThanOrEqualTo(
                    root.get(criteria.getKey()), criteria.getTime());
        }
        return null;
    }
}
