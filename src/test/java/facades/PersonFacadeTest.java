package facades;

import dto.PersonDTO;
import dto.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1 = new Person ("John", "Svendson", "98989898");
    private static Person p2 = new Person ("Svend", "Johnsen", "89898989");
    private static Address a1 = new Address("Danmarksgade 22", "Aalborg", 9000);
    private static Address a2 = new Address("Svenstrupvej 99", "Svenstrup", 9230);

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            p1.setAddress(a1);
            p2.setAddress(a2);
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.createNamedQuery("Address.deleteAllRows").executeUpdate();
            em.persist(p1);
            em.persist(p2);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testPersonCount() {
        assertEquals(2, facade.getPersonCount(), "Expects two rows in the database");
    }
    
    

    @Test
    public void testgetPersonById() throws PersonNotFoundException {
        PersonDTO person = facade.getPerson(p1.getId().intValue());
        assertEquals(person.getId(), p1.getId().intValue(), "Same person");
    }

    @Test
    public void testGetAllPersons() throws PersonNotFoundException {
        PersonsDTO personsDTO = facade.getAllPersons();
        assertEquals(2, personsDTO.getAll().size(), "2");
    }
    
    @Test
    public void testGetEditPerson() throws PersonNotFoundException {
        PersonDTO person = new PersonDTO(p1.getId(), "Kim", "Svend", "90909090", "Danmarksgade", "Aalborg", 9000);
        assertEquals(person.getfName(), facade.editPerson(person).getfName(), "Kim");
    }
    
    @Test
    public void testGetAddPerson() throws PersonNotFoundException {
          assertEquals("Svendning", facade.addPerson("Svendning", "Bob", "10293029", "Derovre", "Ingen steder", 00000).getfName(), "Svendning");
    }
    
    

}
