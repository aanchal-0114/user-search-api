package com.example.userapi.entity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

@Entity
@Data
@Table(name="users")
@Indexed
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FullTextField  // Tokenized search
    private String firstName;

    @FullTextField
    private String lastName;

    @FullTextField  // Exact match with case normalization
    private String email;

    @FullTextField  // SSN exact match
    private String ssn;

    private Integer age;

    private String role;

    @Version  // Add this for concurrency control
    private Integer version;
}