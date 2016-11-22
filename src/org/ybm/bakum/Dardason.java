package org.ybm.bakum;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

@Entity
public class Dardason implements Serializable
{
	private enum AgeGroup
	{
		KID,
		TEENAGER,
		ADOLT,
		OLD
	}
	
	private UUID id;
	private String username;
	private String realName;
	private String locations;
	private Date birthday;
	private AgeGroup ageGroup;
	private boolean welcomed = false;
	private String notes;
	
	@Id
	@Type(type = "uuid-char")
	public UUID getId()
	{
		return id;
	}
	
	public void setId(UUID id)
	{
		this.id = id;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}
	
	public String getLocations()
	{
		return locations;
	}
	
	public void setLocations(String locations)
	{
		this.locations = locations;
	}
	
	public Date getBirthday()
	{
		return birthday;
	}
	
	public void setBirthday(Date birthday)
	{
		this.birthday = birthday;
	}
	
	public String getRealName()
	{
		return realName;
	}
	
	public void setRealName(String realName)
	{
		this.realName = realName;
	}
	
	public boolean isWelcomed()
	{
		return welcomed;
	}
	
	public void setWelcomed(boolean welcomed)
	{
		this.welcomed = welcomed;
	}
	
	@Column(length = 2000)
	public String getNotes()
	{
		return notes;
	}
	
	public void setNotes(String notes)
	{
		this.notes = notes;
	}
	
	@Enumerated(EnumType.STRING)
	public AgeGroup getAgeGroup()
	{
		return ageGroup;
	}
	
	public void setAgeGroup(AgeGroup ageGroup)
	{
		this.ageGroup = ageGroup;
	}
}
