package shift.domain.h2.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import shift.domain.h2.SearchCriteria;
import shift.domain.h2.User.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserSpecification implements Specification<User> {
    private SearchCriteria criteria;

    @Autowired
    public UserSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate
            (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        String operation = criteria.getOperation();

        if (operation.equalsIgnoreCase(":")) {
            return builder.like(
                    root.get(criteria.getKey()), "%" + criteria.getUsername() + "%");
        }
        return null;
    }
}
