package edu.ncsu.csc.iTrust2.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

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
import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;
import edu.ncsu.csc.iTrust2.services.CPTCodeService;

/**
 * Class for testing CPT Code API.
 *
 * @author Shruti Marota
 *
 */
@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class DefaultCodes {
    private MockMvc               mvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private CPTCodeService        service;

    /**
     * Sets up test
     */
    @Before
    public void setup () {
        mvc = MockMvcBuilders.webAppContextSetup( context ).build();
        service.deleteAll();
    }

    /**
     * Adds default CPT codes to the system.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @WithMockUser ( username = "billingspecialist", roles = { "USER", "BILL_SPEC" } )
    public void addDefaultCodes () throws UnsupportedEncodingException, Exception {

        // Create CPT Codes for testing
        final CPTCodeForm form1 = new CPTCodeForm();
        form1.setCode( "99202" );
        form1.setDescription( "15-29 minutes" );
        form1.setCost( 75 );
        form1.setId( (long) 1 );

        final CPTCodeForm form2 = new CPTCodeForm();
        form2.setCode( "99203" );
        form2.setDescription( "30-44 minutes" );
        form2.setCost( 150 );
        form2.setId( (long) 2 );

        final CPTCodeForm form3 = new CPTCodeForm();
        form3.setCode( "99204" );
        form3.setDescription( "45-59 minutes" );
        form3.setCost( 200 );
        form3.setId( (long) 3 );

        final CPTCodeForm form4 = new CPTCodeForm();
        form4.setCode( "99205" );
        form4.setDescription( "60-74 minutes" );
        form4.setCost( 250 );
        form4.setId( (long) 4 );

        final CPTCodeForm form5 = new CPTCodeForm();
        form5.setCode( "99212" );
        form5.setDescription( "10-19 minutes" );
        form5.setCost( 50 );
        form5.setId( (long) 5 );

        final CPTCodeForm form6 = new CPTCodeForm();
        form6.setCode( "99213" );
        form6.setDescription( "20-29 minutes" );
        form6.setCost( 100 );
        form6.setId( (long) 6 );

        final CPTCodeForm form7 = new CPTCodeForm();
        form7.setCode( "99214" );
        form7.setDescription( "30-39 minutes" );
        form7.setCost( 125 );
        form7.setId( (long) 7 );

        final CPTCodeForm form8 = new CPTCodeForm();
        form8.setCode( "99215" );
        form8.setDescription( "40-54 minutes" );
        form8.setCost( 175 );
        form8.setId( (long) 8 );

        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form2 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form3 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form4 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form5 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form6 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form7 ) ) ).andExpect( status().isOk() );
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form8 ) ) ).andExpect( status().isOk() );

    }

}
