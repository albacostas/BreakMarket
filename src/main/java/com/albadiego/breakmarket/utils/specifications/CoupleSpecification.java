package com.albadiego.breakmarket.utils.specifications;

import com.albadiego.breakmarket.model.Couple;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CoupleSpecification {

    public static Specification<Couple> coupleMemberEquals(String memberName) {
        return (root, query, builder) -> {
            if(memberName == null) {
                return builder.conjunction();
            }
            else{
                return builder.or(
                        builder.like(builder.lower(root.get("personA").get("fullName")), "%" + memberName.toLowerCase() + "%"),
                        builder.like(builder.lower(root.get("personB").get("fullName")), "%" + memberName.toLowerCase() + "%")
                );
            }
        };
    }
}
