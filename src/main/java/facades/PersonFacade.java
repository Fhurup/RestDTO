/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author fh
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long PersonCount = (long) em.createQuery("SELECT COUNT(p) FROM Person p").getSingleResult();
            return PersonCount;
        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String city, int zip) {
        EntityManager em = emf.createEntityManager();
        Address address = new Address(street, city, zip);
        Person person = new Person(fName, lName, phone);
        person.setAddress(address);
        PersonDTO personDTO;

        try {
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
            personDTO = new PersonDTO(person);
            return personDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO deletePerson(int id) throws PersonNotFoundException {
        Long ID = Long.valueOf(id);
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, ID);
        
        if (person == null) {
            throw new PersonNotFoundException("Could not delete, provided id does not exist, hallo");
        }
        
        try {
            em.getTransaction().begin();
            em.remove(person.getAddress());
            em.remove(person);
            em.getTransaction().commit();
            PersonDTO pDTO = new PersonDTO(person);
            return pDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO getPerson(int id) throws PersonNotFoundException {
        Long ID = Long.valueOf(id);
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, ID);
        
        if (person == null) {
            throw new PersonNotFoundException("No person with provided id found");
        }
        try {

            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonsDTO getAllPersons() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Person> query
                    = em.createQuery("SELECT p FROM Person p", Person.class);
            List<Person> allPersons = query.getResultList();
            PersonsDTO personsDTO = new PersonsDTO(allPersons);
            return personsDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, p.getId());
        if (person == null) {
            throw new PersonNotFoundException("No person with provided id found");
        }

        try {
            em.getTransaction().begin();
            person.setFname(p.getfName());
            person.setLname(p.getlName());
            person.setPhone(p.getPhone());
            person.getAddress().setStreet(p.getStreet());
            person.getAddress().setCity(p.getCity());
            person.getAddress().setZip(p.getZip());
            em.getTransaction().commit();
            PersonDTO personDTO = new PersonDTO(person);
            return personDTO;
        } finally {
            em.close();
        }
    }

}
