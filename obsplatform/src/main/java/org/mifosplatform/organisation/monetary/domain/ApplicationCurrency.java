
package org.mifosplatform.organisation.monetary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.organisation.monetary.data.CurrencyData;
import org.mifosplatform.organisation.office.domain.OrganisationCurrency;

@Entity
@Table(name = "m_currency")
public class ApplicationCurrency{
	@Id
	/*@GeneratedValue(strategy = GenerationType.AUTO)*/
	private Long id;

	@Column(name = "code", nullable = false, length = 3)
	private String code;

	@Column(name = "decimal_places", nullable = false)
	private Integer decimalPlaces;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "internationalized_name_code", nullable = false, length = 50)
	private String nameCode;

	@Column(name = "display_symbol", nullable = true, length = 10)
	private String displaySymbol;
	
	@Column(name = "country_code", nullable = false, length = 50)
	private String countryCode;
	
	@Column(name = "country_name", nullable = false, length = 50)
	private String countryName;
	
	@Column(name = "type", nullable = false, length = 50)
	private String type;

	

	public static ApplicationCurrency from(final ApplicationCurrency currency,
			final int decimalPlaces) {
		return new ApplicationCurrency(currency.id,currency.code, currency.name,
				decimalPlaces, currency.nameCode, currency.displaySymbol,currency.countryCode,currency.countryName
				,currency.type);
	}

	public ApplicationCurrency() {
		
	}
	
	private ApplicationCurrency(Long id,String code, String name,int decimalPlaces, 
			String nameCode,String displaySymbol,String countryCode,String countryName,String type) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.decimalPlaces = decimalPlaces;
		this.nameCode = nameCode;
		this.displaySymbol = displaySymbol;
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.type = type;
	}

	public Long getId(){
		return this.id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Integer getDecimalPlaces() {
		return this.decimalPlaces;
	}
	
	public void setDecimalPlaces(Integer decimalPlaces) {
		this.decimalPlaces = decimalPlaces;
	}

	public String getNameCode() {
		return nameCode;
	}
	
	public void setNameCode(String nameCode) {
		this.nameCode = nameCode;
	}

	public String getDisplaySymbol() {
		return displaySymbol;
	}
	
	public void setDisplaySymbole(String displaySymbol) {
		this.displaySymbol = displaySymbol;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getCountryName() {
		return countryName;
	}
	
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	
	public static ApplicationCurrency formJson(JsonCommand command) {
		Long id = command.longValueOfParameterNamed("id");
		String code = command.stringValueOfParameterNamed("code");
		String name = command.stringValueOfParameterNamed("name");
		Integer decimalPlaces = command.integerValueOfParameterNamed("decimalPlaces");
		String nameCode  = command.stringValueOfParameterName("nameCode");
		String displaySymbol  = command.stringValueOfParameterName("displaySymbol");
		String countryCode  = command.stringValueOfParameterName("countryCode");
		String countryName  = command.stringValueOfParameterName("countryName");
		String type  = command.stringValueOfParameterNamed("type");
		
		return new ApplicationCurrency(id,code, name, decimalPlaces,
				nameCode, displaySymbol,countryCode,countryName,type);
	}
	
	
	public CurrencyData toData() {
		return new CurrencyData(null, this.code, this.name, this.decimalPlaces,
				this.displaySymbol, this.nameCode, null, null, null);
	}

	public OrganisationCurrency toOrganisationCurrency() {
		return new OrganisationCurrency(this.code, this.name,
				this.decimalPlaces, this.nameCode, this.displaySymbol);
	}
	
}