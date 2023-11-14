package br.edu.ifrs.ibiruba.ldapmanager.entities;

import java.util.Objects;

public class Group {

	private String distinguishedName;
	private String samaccountname;
	private String cn;


	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getSamaccountname() {
		return samaccountname;
	}

	public void setSamaccountname(String samaccountname) {
		this.samaccountname = samaccountname;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Group group = (Group) o;
		return Objects.equals(distinguishedName, group.distinguishedName) && Objects.equals(samaccountname, group.samaccountname) && Objects.equals(cn, group.cn);
	}

	@Override
	public int hashCode() {
		return Objects.hash(distinguishedName, samaccountname, cn);
	}

	@Override
	public String toString() {
		return "Group{" +
				"distinguishedName='" + distinguishedName + '\'' +
				", samaccountname='" + samaccountname + '\'' +
				", cn='" + cn + '\'' +
				'}';
	}
}
