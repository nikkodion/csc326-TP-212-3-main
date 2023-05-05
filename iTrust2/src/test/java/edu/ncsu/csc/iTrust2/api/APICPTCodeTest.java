package edu.ncsu.csc.iTrust2.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import javax.transaction.Transactional;

import org.hamcrest.Matchers;
import org.junit.Assert;
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
import edu.ncsu.csc.iTrust2.models.CPTCode;
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
public class APICPTCodeTest {
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
     * Tests basic cpt code API functionality.
     *
     * @throws UnsupportedEncodingException
     * @throws Exception
     */
    @Test
    @Transactional
    @WithMockUser ( username = "billingspecialist", roles = { "USER", "BILL_SPEC" } )
    public void testCPTCodeAPI () throws UnsupportedEncodingException, Exception {

        // Create CPT Codes for testing
        final CPTCodeForm form1 = new CPTCodeForm();
        form1.setCode( "99001" );
        form1.setDescription( "15-29 minutes" );
        form1.setCost( 75 );
        form1.setId( (long) 1 );

        final CPTCodeForm form2 = new CPTCodeForm();
        form2.setCode( "99002" );
        form2.setDescription( "30-59 minutes" );
        form2.setCost( 100 );
        form2.setId( (long) 2 );

        // Add cpt code 1 to system
        final String content1 = mvc
                .perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form1 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        // Parse response as CPTCode object
        final CPTCode cptCode1 = TestUtils.gson().fromJson( content1, CPTCode.class );
        assertEquals( form1.getCode(), cptCode1.getCode() );
        assertEquals( form1.getCost(), cptCode1.getCost() );
        assertEquals( form1.getDescription(), cptCode1.getDescription() );
        form1.setId( cptCode1.getId() );

        // Attempt to add same cpt code twice
        mvc.perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( form1 ) ) ).andExpect( status().isConflict() );

        // Add cptcode2 to system
        final String content2 = mvc
                .perform( post( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( form2 ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();

        final CPTCode cptCode2 = TestUtils.gson().fromJson( content2, CPTCode.class );
        assertEquals( form2.getCode(), cptCode2.getCode() );
        assertEquals( form2.getCost(), cptCode2.getCost() );
        assertEquals( form2.getDescription(), cptCode2.getDescription() );
        form2.setId( cptCode2.getId() );

        // Verify cpt codes have been added
        mvc.perform( get( "/api/v1/cpt_codes" ) ).andExpect( status().isOk() )
                .andExpect( content().string( Matchers.containsString( form1.getCode() ) ) )
                .andExpect( content().string( Matchers.containsString( form2.getCode() ) ) );

        // Edit first cpt code's description
        CPTCode cptCode = service.findByCode( "99001" );
        cptCode.setCost( 4 );
        final String editContent = mvc
                .perform( put( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                        .content( TestUtils.asJsonString( cptCode ) ) )
                .andExpect( status().isOk() ).andReturn().getResponse().getContentAsString();
        final CPTCode editedCPTCode = TestUtils.gson().fromJson( editContent, CPTCode.class );

        assertEquals( form1.getCode(), editedCPTCode.getCode() );
        assertEquals( form1.getDescription(), editedCPTCode.getDescription() );
        assertEquals( 4, editedCPTCode.getCost() );

        // ensure there are 2 cpt codes in the system
        assertEquals( 2, service.findAll().size() );
        final String firstCptCodeString = mvc.perform( get( "/api/v1/cpt_codes/" + cptCode.getId() ) ).andReturn()
                .getResponse().getContentAsString();
        Assert.assertTrue( firstCptCodeString.contains( "99001" ) );

        cptCode = service.findByCode( "99002" );
        final String secondCptCodeString = mvc.perform( get( "/api/v1/cpt_codes/" + cptCode.getId() ) ).andReturn()
                .getResponse().getContentAsString();
        Assert.assertTrue( secondCptCodeString.contains( "99002" ) );

        // Delete a cpt code from the system
        cptCode = service.findByCode( "99001" );
        String deletedCode = mvc.perform( delete( "/api/v1/cpt_codes/" + cptCode.getId() ) ).andReturn().getResponse()
                .getContentAsString();
        Assert.assertTrue( deletedCode.contains( cptCode.getId().toString() ) );

        cptCode = service.findByCode( "99002" );
        deletedCode = mvc.perform( delete( "/api/v1/cpt_codes/" + cptCode.getId() ) ).andReturn().getResponse()
                .getContentAsString();
        Assert.assertTrue( deletedCode.contains( cptCode.getId().toString() ) );

        // get code that does not exist
        mvc.perform( get( "/api/v1/cpt_codes/99001" ) ).andExpect( status().isNotFound() );

        // edit a cpt code that does not exist
        mvc.perform( put( "/api/v1/cpt_codes" ).contentType( MediaType.APPLICATION_JSON )
                .content( TestUtils.asJsonString( cptCode ) ) ).andExpect( status().isNotFound() );

        // delete a cpt code that does not exist
        mvc.perform( delete( "/api/v1/cpt_codes/" + cptCode.getId() ) ).andExpect( status().isNotFound() );
    }

}
