package com.arquisoft.banco2026v.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;
    @Column(nullable = false, length = 50)
    private String firstName; // firstName VARCHAR(50) NOT NULL;
    @Column(nullable = false, length = 50)
    private String lastName;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    public Customer() {
    }

    /*
    * Customer {
    *   "id":1233,
    *   "accountNumber": "2133331244",
    *   "firstName":"Estefania",
    *   "lastName":"Garces",
    *   "balance": 2123
    * }
    * */
    public Customer(
            Long id,
            String accountNumber,
            String firstName,
            String lastName,
            BigDecimal balance
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
