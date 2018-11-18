package shift.domain.h2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalTime;

public class UserShiftSpecification implements Specification<UserShift> {
    private SearchCriteria criteria;

    @Autowired
    public UserShiftSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<UserShift> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        String operation = criteria.getOperation();

        if (operation.equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                        root.get(criteria.getKey()), "%" + criteria.getUsername() + "%");
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
