package model;

// Generated Nov 16, 2015 2:20:09 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * CaafPeakToTroughEnd generated by hbm2java
 */
@Entity
@Table(name = "caaf_peak_to_trough_end", catalog = "capm_db")
public class CaafPeakToTroughEnd implements java.io.Serializable {

	private CaafPeakToTroughEndId id;

	public CaafPeakToTroughEnd() {
	}

	public CaafPeakToTroughEnd(CaafPeakToTroughEndId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "permno", column = @Column(name = "permno")),
			@AttributeOverride(name = "yrmo", column = @Column(name = "yrmo")),
			@AttributeOverride(name = "value1", column = @Column(name = "value1", length = 10)),
			@AttributeOverride(name = "value2", column = @Column(name = "value2", length = 10)),
			@AttributeOverride(name = "value3", column = @Column(name = "value3", length = 10)),
			@AttributeOverride(name = "value6", column = @Column(name = "value6", length = 10)),
			@AttributeOverride(name = "value24", column = @Column(name = "value24", length = 10)) })
	public CaafPeakToTroughEndId getId() {
		return this.id;
	}

	public void setId(CaafPeakToTroughEndId id) {
		this.id = id;
	}

}
