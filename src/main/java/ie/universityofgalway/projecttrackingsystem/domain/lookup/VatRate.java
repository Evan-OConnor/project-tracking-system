package ie.universityofgalway.projecttrackingsystem.domain.lookup;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "vat_rate",
        uniqueConstraints = @UniqueConstraint(
                name ="uk_vat_rate_percent",
                columnNames = "rate_percent"
        )
)
public class VatRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vat_rate_id")
    private Long id;

    @Column(name = "rate_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal ratePercent;

    protected VatRate() {
    }

    public VatRate (BigDecimal ratePercent) {
        this.ratePercent = ratePercent;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getRatePercent() {
        return ratePercent;
    }

    public BigDecimal getRateDecimal() {
        return ratePercent.divide(BigDecimal.valueOf(100));
    }

    public void setRatePercent(BigDecimal ratePercent) {
        this.ratePercent = ratePercent;
    }

}
