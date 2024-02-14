package com.cassol.rinhadebackend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "account_transaction")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountTransaction {

    @Id
    private UUID uuid;
    private String type;
    private String description;
    private Long amount;
    private LocalDateTime createAt;
    private Long accountId;
    @Version
    private long version;

}
