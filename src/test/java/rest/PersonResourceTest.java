package rest;

import dto.PersonDTO;
import entities.Address;
import entities.Person;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//Uncomment the line below, to temporarily disable this test
//@Disabled

public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static Person p1 = new Person("John", "Svendson", "98989898");
    private static Person p2 = new Person("Svend", "Johnsen", "89898989");
    private static Address a1 = new Address("Danmarksgade 22", "Aalborg", 9000);
    private static Address a2 = new Address("Svenstrupvej 99", "Svenstrup", 9230);

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
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

    @Test
    public void testServerIsUp() {
        System.out.println("Testing is server UP");
        given().when().get("/person").then().statusCode(200);
    }

    //This test assumes the database contains two rows
    @Test
    public void testDummyMsg() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("msg", equalTo("Person API works"));
    }

    @Test
    public void testCount() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/count").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("count", equalTo(2));
    }

    @Test
    public void testPersonID() throws Exception {
        given()
                .contentType("application/json")
                .get("/person/" + p1.getId()).then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("id", equalTo(p1.getId().intValue()));
    }

    @Test
    public void testGetAllPersons() throws Exception {
        List<PersonDTO> personsDTOs;

        personsDTOs = given()
                .contentType("application/json")
                .when()
                .get("/person/all")
                .then()
                .extract().body().jsonPath().getList("all", PersonDTO.class);

        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p2DTO = new PersonDTO(p2);

        assertThat(personsDTOs, containsInAnyOrder(p1DTO, p2DTO));
    }

    @Test
    public void testAddPerson() throws Exception {
        given()
                .contentType("application/json")
                .body(new PersonDTO("Kim", "Svend", "90909090"))
                .when()
                .post("person")
                .then()
                .body("fName", equalTo("Kim"))
                .body("lName", equalTo("Svend"))
                .body("phone", equalTo("90909090"))
                .body("id", notNullValue());

    }
    
        @Test
    public void testEditPerson() throws Exception {
        PersonDTO person = new PersonDTO(1, "Kim", "Svend", "90909090", "Danmarksgade", "Aalborg", 9000);
        given()
                .contentType("application/json")
                .body(person)
                .when()
                .put("person/" + p1.getId())
                .then()
                .body("fName", equalTo("Kim"))
                .body("lName", equalTo("Svend"))
                .body("phone", equalTo("90909090"))
                .body("id", notNullValue());

    }
}
