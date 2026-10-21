package com.albadiego.breakmarket.utils.specifications;

import com.albadiego.breakmarket.model.Person;
import com.albadiego.breakmarket.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> userNameEquals(String fullName) {
        return (root, query, builder) -> {
            if(fullName == null) {
                return builder.conjunction();
            }
            else{
                return builder.like(builder.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
            }
        };
    }

    public static Specification<User> userUsernameEquals(String username) {
        return (root, query, builder) -> {
            if(username == null) {
                return builder.conjunction();
            }
            else{
                return builder.like(builder.lower(root.get("fullName")), "%" + username.toLowerCase() + "%");
            }
        };
    }

}
