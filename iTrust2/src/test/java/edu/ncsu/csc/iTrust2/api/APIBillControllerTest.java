package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import edu.ncsu.csc.iTrust2.common.TestUtils;
import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.forms.PaymentForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Payment;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.models.enums.PaymentMethod;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.UserService;

/**
 * Test for the API functionality for interacting with bills
 *
 * @author Jacob Beasley
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class APIBillControllerTest {

    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserService<User>     userService;

    @Autowired
    private BillService           billService;

    @Autowired
    private CPTCodeService        cservice;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();

        billService.deleteAll();
        cservice.deleteAll();

        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User billspec = new Personnel( new UserForm( "billing_specialist", "123456", Role.ROLE_BILL_SPEC, 1 ) );

        userService.saveAll( List.of( patient, hcp, billspec ) );

    }

    /**
     * Tests getting a non existent bill and ensures that the correct status is
     * returned.
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billing_specialist", roles = { "BILL_SPEC" } )
    public void testGetNonExistentBill () throws Exception {
        mvc.perform( get( "/api/v1/bills/-1" ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests BillAPI
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "hcp", roles = { "HCP", "BILL_SPEC" } )
    public void testBillAPI () throws Exception {
        assertEquals( 0, billService.count() );

        BillForm billF1 = new BillForm();
        billF1.setAmountOwed( 100 );
        billF1.setAttendingHCP( "hcp" );
        billF1.setCost( 100 );

        final CPTCode c1 = new CPTCode( "12345", "descr", 1 );
        final CPTCode c2 = new CPTCode( "12346", "descr", 2 );
        final List<CPTCode> codes = List.of( c1, c2 );
        cservice.save( c1 );
        cservice.save( c2 );
        billF1.setCptCodes( codes );

        billF1.setDate( "2030-11-19T04:50-05:00" );
        billF1.setPatient( "patient" );
        billF1.setStatus( BillStatus.UNPAID );

        /* Create the bill */
        final String tran1 = mvc
                .perform( post( "/api/v1/bills" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( billF1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        assertEquals( 1, billService.count() );

        final Bill bill1 = TestUtils.gson().fromJson( tran1, Bill.class );
        assertEquals( billF1.getAmountOwed(), bill1.getAmountOwed(), 0 );
        assertEquals( billF1.getAttendingHCP(), bill1.getAttendingHCP().getUsername() );
        assertEquals( billF1.getCost(), bill1.getCost(), 0 );
        assertEquals( billF1.getDate(), bill1.getDate().toString() );
        assertEquals( billF1.getPatient(), bill1.getPatient().getUsername() );
        assertEquals( billF1.getStatus(), bill1.getStatus() );

        mvc.perform( get( "/api/v1/bills" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        Long id = billService.findByPatient( userService.findByName( "patient" ) ).get( 0 ).getId();
        final String tran2 = mvc.perform( get( "/api/v1/bills/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        final Bill bill1ret = TestUtils.gson().fromJson( tran2, Bill.class );
        assertEquals( billF1.getAmountOwed(), bill1ret.getAmountOwed(), 0 );
        assertEquals( billF1.getAttendingHCP(), bill1ret.getAttendingHCP().getUsername() );
        assertEquals( billF1.getCost(), bill1ret.getCost(), 0 );
        assertEquals( billF1.getDate(), bill1ret.getDate().toString() );
        assertEquals( billF1.getPatient(), bill1ret.getPatient().getUsername() );
        assertEquals( billF1.getStatus(), bill1ret.getStatus() );

        /**
         * testing changing of amount owed. Status should update to paid
         */
        final PaymentForm pf1 = new PaymentForm();
        pf1.setAmount( 20 );
        pf1.setMethod( PaymentMethod.CASH );
        final Payment p1 = new Payment( pf1 );
        bill1.addPayment( p1 );
        billF1 = new BillForm( bill1 );

        id = billService.findByPatient( userService.findByName( "patient" ) ).get( 0 ).getId();
        billF1.setId( id );
        mvc.perform( put( "/api/v1/bills/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( billF1 ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );
        assertEquals( 1, billService.count() );

        final String tran3 = mvc.perform( get( "/api/v1/bills/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        Bill bill1edit = TestUtils.gson().fromJson( tran3, Bill.class );
        assertEquals( billF1.getAmountOwed(), bill1edit.getAmountOwed(), 0 );
        assertEquals( billF1.getAttendingHCP(), bill1edit.getAttendingHCP().getUsername() );
        assertEquals( billF1.getCost(), bill1edit.getCost(), 0 );
        assertEquals( billF1.getDate(), bill1edit.getDate().toString() );
        assertEquals( billF1.getPatient(), bill1edit.getPatient().getUsername() );
        assertEquals( BillStatus.PARTIALLY_PAID, bill1edit.getStatus() );

        /*
         * Add second payment
         */
        final PaymentForm pf2 = new PaymentForm();
        pf2.setAmount( 80 );
        pf2.setMethod( PaymentMethod.CREDIT_CARD );
        final Payment p2 = new Payment( pf2 );
        bill1.addPayment( p2 );
        billF1 = new BillForm( bill1 );

        id = billService.findByPatient( userService.findByName( "patient" ) ).get( 0 ).getId();
        billF1.setId( id );
        mvc.perform( put( "/api/v1/bills/" + id ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( billF1 ) ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );
        assertEquals( 1, billService.count() );

        final String tran6 = mvc.perform( get( "/api/v1/bills/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) ).andExpect( status().isOk() )
                .andReturn().getResponse().getContentAsString();
        bill1edit = TestUtils.gson().fromJson( tran6, Bill.class );
        assertEquals( billF1.getAmountOwed(), bill1edit.getAmountOwed(), 0 );
        assertEquals( BillStatus.PAID, bill1edit.getStatus() );

        /** error checking */
        mvc.perform( post( "/api/v1/bills" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( billF1 ) ) ).andExpect( status().isConflict() );

        mvc.perform( get( "/api/v1/bills/" + id ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON_VALUE ) );

        // PUT with ID not in database should fail
        final long tempId = 101;
        billF1.setId( tempId );
        mvc.perform( put( "/api/v1/bills/" + tempId ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( billF1 ) ) ).andExpect( status().isNotFound() );
    }

    /**
     * Tests bill certificate API
     *
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "patient", roles = { "PATIENT" } )
    public void testBillCertificate () throws Exception {
        final User patient = new Patient( new UserForm( "patient", "123456", Role.ROLE_PATIENT, 1 ) );
        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final Bill bill = new Bill();
        bill.setAmountOwed( 100 );
        bill.setPatient( patient );
        bill.setDate( ZonedDateTime.of( 1, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault() ) );
        bill.setAttendingHCP( hcp );
        bill.setCost( 100 );
        final CPTCode c1 = new CPTCode( "12345", "descr", 1 );
        final CPTCode c2 = new CPTCode( "12346", "descr", 2 );
        final List<CPTCode> codes = List.of( c1, c2 );
        cservice.save( c1 );
        cservice.save( c2 );
        bill.setCptCodes( codes );
        billService.save( bill );

        mvc.perform( get( "/api/v1/billcertificate" ) ).andExpect( status().isOk() )
                .andExpect( content().contentType( MediaType.APPLICATION_JSON ) );

    }

}
