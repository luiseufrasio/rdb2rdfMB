package model;
public class Assertion {
	private String srcPrefix;
	private String srcClass;
	private String srcDP;
	private String srcOP;

	private String tgPrefix;
	private String tgClass;
	private String tgDP;
	private String tgOP;

	private String constraint;
	private AssertionType type;

	public Assertion(String srcPrefix, String tgPrefix) {
		super();
		this.srcPrefix = srcPrefix;
		this.tgPrefix = tgPrefix;
	}

	@Override
	public String toString() {
		String str = "UNDEFINED";

		switch (type) {
		case CLASS_TO_CLASS:
			str = tgPrefix + " : " + tgClass + "(i) ← " + srcPrefix + " : "
					+ srcClass + "(i)";
			break;
		case DP_TO_DP:
			str = tgPrefix + " : " + tgDP + "(i, v) ← " + srcPrefix + " : "
					+ srcDP + "(i, v), " + srcClass + "(i)";
			break;
		case OP_TO_OP:
			str = tgPrefix + " : " + tgOP + "(i, j) ← " + srcPrefix + " : "
					+ srcOP + "(i, j), " + srcClass + "(i)";
			break;
		default:
			break;
		}

		if (constraint != null) {
			str += ", " + constraint;
		}
		
		return str;
	}

	public String getSrcDP() {
		return srcDP;
	}

	public void setSrcDP(String srcDP) {
		this.srcDP = srcDP;
	}

	public String getSrcOP() {
		return srcOP;
	}

	public void setSrcOP(String srcOP) {
		this.srcOP = srcOP;
	}

	public String getTgDP() {
		return tgDP;
	}

	public void setTgDP(String tgDP) {
		this.tgDP = tgDP;
	}

	public String getTgOP() {
		return tgOP;
	}

	public void setTgOP(String tgOP) {
		this.tgOP = tgOP;
	}

	public String getSrcPrefix() {
		return srcPrefix;
	}

	public void setSrcPrefix(String srcPrefix) {
		this.srcPrefix = srcPrefix;
	}

	public String getSrcClass() {
		return srcClass;
	}

	public void setSrcClass(String srcClass) {
		this.srcClass = srcClass;
	}

	public String getTgPrefix() {
		return tgPrefix;
	}

	public void setTgPrefix(String tgPrefix) {
		this.tgPrefix = tgPrefix;
	}

	public String getTgClass() {
		return tgClass;
	}

	public void setTgClass(String tgClass) {
		this.tgClass = tgClass;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}

	public AssertionType getType() {
		return type;
	}

	public void setType(AssertionType type) {
		this.type = type;
	}

}
