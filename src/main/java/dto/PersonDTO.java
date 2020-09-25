/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Address;
import entities.Person;

/**
 *
 * @author fh
 */
public class PersonDTO {
   
    private long id;
    private String fName;
    private String lName;
    private String phone;
    private String street;
    private String city;
    private int zip;
    
    public PersonDTO(Person p) {
        this.fName = p.getFname();
        this.lName = p.getLname();
        this.phone = p.getPhone();
        this.id = p.getId();
        this.street = p.getAddress().getStreet();
        this.city = p.getAddress().getCity();
        this.zip = p.getAddress().getZip();
    }

    public PersonDTO(long id, String fName, String lName, String phone, String street, String city, int zip) {
        this.id = id;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.street = street;
        this.city = city;
        this.zip = zip;
    }

    public PersonDTO(String fn,String ln, String phone) {
        this.fName = fn;
        this.lName = ln;
        this.phone = phone;        
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getZip() {
        return zip;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
