package gr.hua.fitTrack.core.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * API Client Entity
 */

@Entity
@Table(
        name = "api_client",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_api_client_name", columnNames = "name")
        }
)
public class APIClient {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 255)
    @Column(name = "secret", nullable=false, length = 255)
    private String secret;

    @Column(name = "permissions_csv", nullable = false,length = 255)
    private String permissionsCsv;

    @NotNull
    @NotBlank
    @Size(max = 60)
    @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*$")
    @Column(name = "name", nullable=false, length = 60)
    private String name;

    public APIClient() {
    }

    public APIClient(Long id, String secret, String permissionsCsv, String name) {
        this.id = id;
        this.secret = secret;
        this.permissionsCsv = permissionsCsv;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull @NotBlank @Size(max = 255) String getSecret() {
        return secret;
    }

    public void setSecret(@NotNull @NotBlank @Size(max = 255) String secret) {
        this.secret = secret;
    }

    public String getPermissionsCsv() {
        return permissionsCsv;
    }

    public void setPermissionsCsv(String permissionsCsv) {
        this.permissionsCsv = permissionsCsv;
    }

    public @NotNull @NotBlank @Size(max = 60) @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*$") String getName() {
        return name;
    }

    public void setName(@NotNull @NotBlank @Size(max = 60) @Pattern(regexp = "[a-zA-Z][a-zA-Z0-9_]*$") String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APIClient apiClient = (APIClient) o;
        return Objects.equals(secret, apiClient.secret) && Objects.equals(permissionsCsv, apiClient.permissionsCsv) && Objects.equals(name, apiClient.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secret, permissionsCsv, name);
    }

    @Override
    public String toString() {
        return "APIClient{" +
                "id=" + id +
                ", secret='" + secret + '\'' +
                ", permissionsCsv='" + permissionsCsv + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
