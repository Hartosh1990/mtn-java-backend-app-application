package com.sap.nextgen.vlm.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.nextgen.vlm.utils.JWTTokenFactory.EmailTemplate;
import com.sap.nextgen.vlm.utils.JWTTokenFactory.Name;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Data
public class JWTTokenFactory {
	
	String key = "7f63c626-ff8a-475d-84ee-56c5d4711654";
	Name name;
	String id;
	EmailTemplate[] emails;
	String type = "employee";
	String iss;
	String aud;
	int isIntwoUser = 1;
	@Data
	public class Name {
		String givenName;
		String familyName;
		
	}
	@Data
	public class EmailTemplate{
		String value;
	}
	
	public String getJWTToken(String userId,String email,String firstName, String lastName,String type) {
		SecretKey key1 = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
		Date now = new Date();
        now.setHours(now.getHours()+12); // Add 12 hours from now.
        
        /* Create User Name Object*/
        Name userName = new Name();
        userName.setGivenName(firstName);
        userName.setFamilyName(lastName);
        
        /* Create Email Template */
        EmailTemplate emailTemplate = new EmailTemplate();
        emailTemplate.setValue(email);
        
        /*Create Map from POJO*/
        this.id = userId;
        this.name = userName;
        this.emails = new EmailTemplate[]{emailTemplate};
        this.type = type;
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = 
        mapper.convertValue(this, new TypeReference<Map<String, Object>>() {});
        
        String token = Jwts.builder().setClaims(map).setExpiration(now).signWith(key1).compact();
		return token;
	}
}


