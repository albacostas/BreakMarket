package com.albadiego.breakmarket.utils.specifications;

import com.albadiego.breakmarket.model.Person;
import org.springframework.data.jpa.domain.Specification;

public class PeopleSpecification {

    public static Specification<Person> personNameEquals(String fullName) {
        return (root, query, builder) -> {
            if(fullName == null) {
                return builder.conjunction();
            }
            else{
                return builder.like(builder.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
            }
        };
    }

}
