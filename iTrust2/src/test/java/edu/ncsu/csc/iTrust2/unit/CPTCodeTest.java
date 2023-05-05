package edu.ncsu.csc.iTrust2.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.ncsu.csc.iTrust2.TestConfig;
import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.models.CPTCode;

/**
 * Class to test that CPTCode and CPTCodeForms are created from each other
 * properly.
 *
 * @author Shruti Marota
 *
 */
@RunWith ( SpringRunner.class )
@EnableAutoConfiguration
@SpringBootTest ( classes = TestConfig.class )
public class CPTCodeTest {

    /**
     * Test method to test CPTCode and CPTCodeForm objects and ensure they are
     * equivalent.
     */
    @Test
    @Transactional
    public void testCodes () {
        // create a CPTCodeForm object, set its values
        final CPTCodeForm form = new CPTCodeForm();
        form.setId( 1L );
        form.setCode( "99001" );
        form.setDescription( "Testing" );
        form.setCost( 5 );

        // create a CPTCode object, set its values to the same as the
        // CPTCodeForm
        final CPTCode base = new CPTCode();
        base.setCode( "99001" );
        base.setDescription( "Testing" );
        base.setId( 1L );
        base.setCost( 5 );

        // create a CPTCode object out of the CPTCodeForm object, ensure the
        // base and the new CPTCode are equivalent
        final CPTCode code = new CPTCode( form );
        assertEquals( code, base );

        // create a CPTCodeForm object out of the CPTCode object, ensure they
        // are equivalent
        final CPTCodeForm f2 = new CPTCodeForm( code );
        assertEquals( form, f2 );

        assertEquals( "99001", code.getCode() );
        assertTrue( code.getId().equals( 1L ) );
        assertEquals( "Testing", code.getDescription() );
        assertEquals( 5, code.getCost() );

        // tests whether the CPTCode constructor works as planned
        final CPTCode newCode = new CPTCode( "99001", "Testing", 5 );
        assertEquals( code.getCode(), newCode.getCode() );
        assertEquals( code.getDescription(), newCode.getDescription() );
        assertEquals( code.getCost(), newCode.getCost() );
    }

    /**
     * Test method to test invalid CPTCode and CPTCodeForm objects to ensure
     * they cannot be created.
     */
    @Test
    @Transactional
    public void testInvalidCodes () {
        // create a CPTCodeForm object, set its value (code will be invalid)
        final CPTCodeForm form = new CPTCodeForm();
        form.setId( 1L );
        form.setCode( "990012" );
        form.setDescription( "Testing" );
        form.setCost( 5 );

        // try to create the code, expect failure
        CPTCode code;
        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "CPT codes must be 5 characters long.", e.getMessage() );
        }

        // set the code to an invalid cost, expect failure
        form.setCode( "99001" );
        form.setCost( -5 );
        try {
            code = new CPTCode( form );
            fail();
        }
        catch ( final IllegalArgumentException e ) {
            assertEquals( "CPT code cost must be a positive integer.", e.getMessage() );
        }
    }
}
