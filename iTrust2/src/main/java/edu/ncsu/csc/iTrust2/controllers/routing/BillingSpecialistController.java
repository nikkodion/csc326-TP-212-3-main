package edu.ncsu.csc.iTrust2.controllers.routing;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ncsu.csc.iTrust2.models.enums.Role;

/**
 * Controller class responsible for managing the behavior for the Billing
 * Specialist Landing Screen
 *
 * @author Jacob Beasley
 *
 */
@Controller
public class BillingSpecialistController {

    /**
     * Returns the Landing screen for the Billing Specialist
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @RequestMapping ( value = "/billing_specialist/index" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILL_SPEC')" )
    public String index ( final Model model ) {
        return Role.ROLE_BILL_SPEC.getLanding();
    }

    /**
     * Returns the page allowing the Billing Specialist to view and edit bills
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @GetMapping ( "/billing_specialist/viewBills" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILL_SPEC')" )
    public String viewBills ( final Model model ) {
        return "billing_specialist/viewBills";
    }

    /**
     * Returns the page allowing the Billing Specialist to view and edit
     * cptCodes
     *
     * @param model
     *            Data from the front end
     * @return The page to display
     */
    @GetMapping ( "/billing_specialist/manageCPTCodes" )
    @PreAuthorize ( "hasAnyRole('ROLE_BILL_SPEC')" )
    public String cptCodes ( final Model model ) {
        return "billing_specialist/manageCPTCodes";
    }
}
