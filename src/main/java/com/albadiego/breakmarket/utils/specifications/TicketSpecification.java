package com.albadiego.breakmarket.utils.specifications;

import com.albadiego.breakmarket.model.BetTicket;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class TicketSpecification {

    public static Specification<BetTicket> ticketUserUuidEquals(UUID authorUUID) {
        return (root, query, builder) -> {
            if(authorUUID == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("user").get("uuid"), authorUUID);
            }
        };
    }

    public static Specification<BetTicket> ticketOptionTypeEquals(String option) {
        return (root, query, builder) -> {
            if(option == null) {
                return builder.conjunction();
            }
            else{
                return builder.equal(root.get("option").get("uuid"), option);
            }
        };
    }
}
