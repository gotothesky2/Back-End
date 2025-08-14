package hackerthon.likelion13th.canfly.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Address {

    @Column(name = "zipcode", length = 20, nullable = true)
    private String zipcode;

    @Column(name = "address", nullable = true)
    private String address;

    @Column(name = "address_detail", nullable = true)
    private String addressDetail;

    public boolean isEmpty() {
        return (zipcode == null || zipcode.isBlank())
                && (address == null || address.isBlank())
                && (addressDetail == null || addressDetail.isBlank());
    }
}