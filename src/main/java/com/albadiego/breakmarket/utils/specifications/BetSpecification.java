package com.albadiego.breakmarket.utils.specifications;

import com.albadiego.breakmarket.model.Bet;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class BetSpecification {

    public static Specification<Bet> betCoupleUuidEquals(UUID coupleUUID) {
        return (root, query, builder) -> {
            if(coupleUUID == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("couple").get("uuid"), coupleUUID);
            }
        };
    }

    public static Specification<Bet> betDescriptionEquals(String description) {
        return (root, query, builder) -> {
            if(description == null) {
                return builder.conjunction();
            }
            else{
                return builder.like(
                        builder.lower(root.get("description")),
                        "%" + description.toLowerCase() + "%"
                );
            }
        };
    }

    public static Specification<Bet> betCreatorUuidEquals(UUID creatorUUID) {
        return (root, query, builder) -> {
            if(creatorUUID == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("creator").get("uuid"), creatorUUID);
            }
        };
    }

    public static Specification<Bet> betTypeEquals(String type) {
        return (root, query, builder) -> {
            if(type == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("type"), type);
            }
        };
    }
}
