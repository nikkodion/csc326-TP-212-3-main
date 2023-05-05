package edu.ncsu.csc.iTrust2.models;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Min;

import edu.ncsu.csc.iTrust2.forms.CPTCodeForm;

/**
 * A class to represent the CPT Codes that can be added to office visits.
 *
 * @author Matthew Howard
 */
@Entity
public class CPTCode extends DomainObject {

    /* The ID number of the CPT Code */
    @Id
    @GeneratedValue
    private Long   id;

    /* The CPT Code number */
    private String code;

    /* The time range of the office visit */
    private String description;

    @Min ( 0 )
    /* The cost of the CPT Code */
    private int    cost;

    /**
     * Constructor with no fields
     */
    public CPTCode () {
        super();
    }

    /**
     * Constructs a new form from the details in the given form
     *
     * @param form
     *            The form to vase the new CPT Code on
     */
    public CPTCode ( final CPTCodeForm form ) {
        setId( form.getId() );
        setCode( form.getCode() );
        setDescription( form.getDescription() );
        setCost( form.getCost() );
    }

    /**
     * Constructor for the CPT Code with an ID number, time range, and cost.
     *
     * @param description
     *            The time range associated with the CPT Code
     * @param cost
     *            The cost of the CPT Code
     */
    public CPTCode ( final String code, final String description, final int cost ) {
        super();
        setCode( code );
        setDescription( description );
        setCost( cost );
    }

    /**
     * Gets the CPT Code ID number
     *
     * @return id The ID number
     */
    @Override
    public Long getId () {
        return id;
    }

    /**
     * Sets the ID number of the CPT Code (used by Hibernate)
     *
     * @param id
     *            The new ID number
     */
    public void setId ( final Long id ) {
        this.id = id;
    }

    /**
     * Gets the CPT Code number.
     *
     * @return code The CPT Code number
     */
    public String getCode () {
        return code;
    }

    /**
     * Sets the CPT Code number.
     *
     * @param code
     *            The new CPT Code number
     */
    public void setCode ( final String code ) {
        // this.code = code;
        if ( code.length() == 5 ) {
            this.code = code;
        }
        else {
            throw new IllegalArgumentException( "CPT codes must be 5 characters long." );
        }
    }

    /**
     * Gets the time range associated with the CPT Code
     *
     * @return description The time range of the CPT Code
     */
    public String getDescription () {
        return description;
    }

    /**
     * Sets the time range of the CPT Code
     *
     * @param description
     *            The new description
     */
    public void setDescription ( final String description ) {
        this.description = description;
    }

    /**
     * Gets the cost of the CPT Code
     *
     * @return cost The cost of the CPT Code
     */
    public int getCost () {
        return cost;
    }

    /**
     * Sets the cost of the CPT Code
     *
     * @param cost
     *            The new cost
     */
    public void setCost ( final int cost ) {
        // this.cost = cost;
        if ( cost >= 0 ) {
            this.cost = cost;
        }
        else {
            throw new IllegalArgumentException( "CPT code cost must be a positive integer." );
        }
    }

    /**
     * Checks to see if a CPT Code is the same as another
     *
     * @param obj
     *            The second object being compared
     * @return True if the two objects are equal
     */
    @Override
    public boolean equals ( final Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final CPTCode other = (CPTCode) obj;
        return cost == other.cost && Objects.equals( description, other.description ) && Objects.equals( id, other.id );
    }

}
