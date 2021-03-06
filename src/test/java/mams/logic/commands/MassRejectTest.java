package mams.logic.commands;

import static mams.logic.commands.CommandTestUtil.assertCommandSuccess;
import static mams.logic.commands.Reject.MESSAGE_NO_APPEALS_REJECTED;
import static mams.testutil.TypicalMams.getTypicalMams;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import mams.model.Model;
import mams.model.ModelManager;
import mams.model.UserPrefs;

public class MassRejectTest {

    private Model model = new ModelManager(getTypicalMams(), new UserPrefs());
    private Model expectedModel = new ModelManager(model.getMams(), new UserPrefs());



    @Test
    public void equal() {

        ArrayList<String> validIDs = new ArrayList<>();
        ArrayList<String> invalidIDs = new ArrayList<>();


        // same object -> returns true
        MassReject firstEmptyCommand = new MassReject(validIDs, invalidIDs);
        assertTrue(firstEmptyCommand.equals(firstEmptyCommand));

        validIDs.add("C000000");
        validIDs.add("C000023");
        validIDs.add("C000007");

        MassReject validCommand = new MassReject(validIDs, invalidIDs);
        MassReject validCommandCopy = new MassReject(validIDs, invalidIDs);
        // same values -> returns true
        assertTrue(validCommand.equals(validCommandCopy));

        ArrayList<String> secondValidIDs = new ArrayList<>();
        secondValidIDs.add("C000001");
        secondValidIDs.add("C000123");
        // different values -> returns false
        assertFalse(validCommand.equals(secondValidIDs));

        invalidIDs.add("C0123");
        invalidIDs.add("C0132");

        ArrayList<String> validListWithRightOrder = new ArrayList<>();
        validIDs.add("C000000");
        validIDs.add("C000007");
        validIDs.add("C000023");

        MassReject validCommandCopyWithRightOrder = new MassReject(validIDs, invalidIDs);
        // same values different order and different invalid list -> returns true
        assertTrue(validCommand.equals(validCommandCopyWithRightOrder));

        // different type -> returns false
        assertFalse(validCommand.equals(1));

        // different type -> returns false
        assertFalse(validCommand.equals("1"));

        // null -> returns false
        assertFalse(validCommand.equals(null));
    }

    @Test
    public void execute_nothingApproved_success() {
        ArrayList<String> validIDs = new ArrayList<>();
        ArrayList<String> invalidIDs = new ArrayList<>();
        String expectedMessage = "";
        invalidIDs.add("C0123");
        invalidIDs.add("C0132");
        MassReject nothingApprovedCommand = new MassReject(validIDs, invalidIDs);
        expectedMessage += MESSAGE_NO_APPEALS_REJECTED + "\nInvalid appeal IDs: " + invalidIDs.toString();
        assertCommandSuccess(nothingApprovedCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noSuchAppealId_success() {
        ArrayList<String> validIDs = new ArrayList<>();
        ArrayList<String> invalidIDs = new ArrayList<>();
        String expectedMessage = "";
        validIDs.add("C120000");
        validIDs.add("C999999");
        MassReject command = new MassReject(validIDs, invalidIDs);
        expectedMessage += MESSAGE_NO_APPEALS_REJECTED + "\nThese appeal IDs do not exist: " + validIDs.toString();
        assertCommandSuccess(command, model, expectedMessage, expectedModel);

    }

}
