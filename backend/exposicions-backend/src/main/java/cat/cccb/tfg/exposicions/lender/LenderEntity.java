package cat.cccb.tfg.exposicions.lender;

import jakarta.persistence.*;

@Entity
@Table(name = "lenders")
public class LenderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String name;

    @Column(columnDefinition = "text", unique = true)
    private String code;

    @Column(columnDefinition = "text")
    private String email;

    @Column(columnDefinition = "text")
    private String phone;

    @Column(columnDefinition = "text")
    private String notes;

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
