package model;
public abstract class Property {

	protected String name;
	protected Class_ domain;

	public Property() {
		super();
	}

	@Override
	public String toString() {
		return name + " (" + getRangeName() + ")" ;
	}

	public abstract String getRangeName();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class_ getDomain() {
		return domain;
	}

	public void setDomain(Class_ domain) {
		this.domain = domain;
	}

}