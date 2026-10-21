package com.albadiego.breakmarket.utils.specifications;

import com.albadiego.breakmarket.model.Evidence;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class EvidenceSpecification {

    public static Specification<Evidence> evidenceStateEquals(String state) {
        return (root, query, builder) -> {
            if(state == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("state"), state);
            }
        };
    }

    public static Specification<Evidence> evidenceBetUuidEquals(UUID evidenceUuid) {
        return (root, query, builder) -> {
            if(evidenceUuid == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("bet").get("uuid"), evidenceUuid);
            }
        };
    }
}
