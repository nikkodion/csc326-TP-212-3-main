package edu.ncsu.csc.iTrust2.unit;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.BillForm;
import edu.ncsu.csc.iTrust2.forms.UserForm;
import edu.ncsu.csc.iTrust2.models.Bill;
import edu.ncsu.csc.iTrust2.models.CPTCode;
import edu.ncsu.csc.iTrust2.models.Patient;
import edu.ncsu.csc.iTrust2.models.Personnel;
import edu.ncsu.csc.iTrust2.models.User;
import edu.ncsu.csc.iTrust2.models.enums.BillStatus;
import edu.ncsu.csc.iTrust2.models.enums.Role;
import edu.ncsu.csc.iTrust2.services.BillService;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;
import edu.ncsu.csc.iTrust2.services.UserService;

@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class BillTest {

    @Autowired
    private BillService       billService;

    @Autowired
    private UserService<User> userService;

    @Autowired
    private CPTCodeService    cservice;

    @Before
    public void setup () {
        billService.deleteAll();

        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );

        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );

        userService.saveAll( List.of( hcp, alice ) );
    }

    @Test
    @Transactional
    public void testCreateBill () {
        final Bill testBill = new Bill();
        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );
        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );
        final List<CPTCode> cptCodes = new ArrayList<CPTCode>();
        final CPTCode c1 = new CPTCode( "11111", "testdesc", 1 );
        cservice.save( c1 );
        cptCodes.add( c1 );

        testBill.setId( (long) 1 );
        assertEquals( testBill.getId(), 1 );
        testBill.setCost( 1 );
        assertEquals( testBill.getCost(), 1 );
        testBill.setCptCodes( cptCodes );
        assertEquals( testBill.getCptCodes(), cptCodes );
        testBill.setAttendingHCP( hcp );
        assertEquals( testBill.getAttendingHCP(), hcp );
        testBill.setAmountOwed( 1 );
        assertEquals( testBill.getAmountOwed(), 1 );
        testBill.setDate( ZonedDateTime.of( 1, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault() ) );
        assertEquals( testBill.getDate(), ZonedDateTime.of( 1, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault() ) );
        testBill.setPatient( alice );
        assertEquals( testBill.getPatient(), alice );
        testBill.setStatus( BillStatus.UNPAID );
        assertEquals( testBill.getStatus(), BillStatus.UNPAID );
    }

    @Test
    @Transactional
    public void testBillService () {
        final Bill testBill = new Bill();
        final User hcp = new Personnel( new UserForm( "hcp", "123456", Role.ROLE_HCP, 1 ) );
        final User alice = new Patient( new UserForm( "AliceThirteen", "123456", Role.ROLE_PATIENT, 1 ) );
        final List<CPTCode> cptCodes = new ArrayList<CPTCode>();
        final CPTCode c1 = new CPTCode( "11111", "testdesc", 1 );
        cservice.save( c1 );
        cptCodes.add( c1 );

        testBill.setCost( 1 );
        assertEquals( testBill.getCost(), 1 );
        testBill.setAttendingHCP( hcp );
        assertEquals( testBill.getAttendingHCP(), hcp );
        testBill.setCptCodes( cptCodes );
        assertEquals( testBill.getCptCodes(), cptCodes );
        testBill.setAmountOwed( 1 );
        assertEquals( testBill.getAmountOwed(), 1 );
        testBill.setDate( ZonedDateTime.of( 1, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault() ) );
        assertEquals( testBill.getDate(), ZonedDateTime.of( 1, 1, 1, 1, 1, 1, 1, ZoneId.systemDefault() ) );
        testBill.setPatient( alice );
        assertEquals( testBill.getPatient(), alice );
        testBill.setStatus( BillStatus.UNPAID );
        assertEquals( testBill.getStatus(), BillStatus.UNPAID );

        final BillForm billForm = new BillForm( testBill );
        assertEquals( testBill.getId(), billService.build( billForm ).getId() );
        billService.save( testBill );
        assertEquals( testBill, billService.findByPatient( alice ).get( 0 ) );

        billForm.setStatus( BillStatus.PAID );
        try {
            billService.build( billForm );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            //
        }

        billForm.setAmountOwed( 0 );
        billForm.setStatus( BillStatus.UNPAID );
        try {
            billService.build( billForm );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            //
        }

        billForm.setAmountOwed( -1 );
        try {
            billService.build( billForm );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            //
        }

        billForm.setCost( -1 );
        try {
            billService.build( billForm );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            //
        }
    }

}
