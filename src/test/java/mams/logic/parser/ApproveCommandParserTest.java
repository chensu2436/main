package mams.logic.parser;

import static mams.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static mams.logic.parser.CommandParserTestUtil.assertParseFailure;
import static mams.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static mams.testutil.TypicalIndexes.INDEX_FIRST;
import static mams.testutil.TypicalIndexes.INDEX_SECOND;
import static mams.testutil.TypicalAppeals.APPEAL1;
import static mams.testutil.TypicalAppeals.APPEAL2;

import mams.commons.core.index.Index;
import mams.logic.commands.Approve;
import mams.model.appeal.Appeal;
import mams.testutil.TypicalAppeals;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class ApproveCommandParserTest {

    private ApproveCommandParser parser = new ApproveCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        //no index specified
        assertParseFailure(parser,
                "   ",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
    }

    @Test
    public void parse_noRelevantPrefixesPresent_throwParseException() {
        assertParseFailure(parser,
                " t/1 y/1 8/ -s -all",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
    }

    @Test void parse_missingParts_failure() {
        assertParseFailure(parser,
                "12 r/ok can",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
    }

    @Test
    public void parse_someIrrelevantPrefixesPresent_throwParseException() {
        // irrelevant prefix without input
        assertParseFailure(parser,
                " a/1 t/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));;
    }

    @Test
    public void parse_emptyFields_throwParseException() {
        assertParseFailure(parser,
                "a/",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
    }

    @Test
    public void parse_invalidFields_throwParseException() {
        assertParseFailure(parser,
                "a/-10",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
        assertParseFailure(parser,
                "a/asbsd",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
        assertParseFailure(parser,
                "a/-10addsf",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
        assertParseFailure(parser,
                "a/0",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, Approve.MESSAGE_USAGE_APPROVE));
    }

    @Test
    public void parse_incorrectNumberOfFields_throwParseException() {
        // checking two or more appeals by index
        assertParseFailure(parser, " a/1 a/2", Approve.MESSAGE_ONLY_ONE_ITEM_ALLOWED);
        assertParseFailure(parser, " a/1 a/4 a/6 a/7", Approve.MESSAGE_ONLY_ONE_ITEM_ALLOWED);

        //checking two or more mass appeal
        assertParseFailure(parser, " mass/ mass/", Approve.MESSAGE_ONLY_ARGUMENT_ALLOWED);
        assertParseFailure(parser, " mass/ mass/ mass/", Approve.MESSAGE_ONLY_ARGUMENT_ALLOWED);
    }


//    @Test
//    public void parse_allFields_success() {
//        ArrayList<String> validIDs = new ArrayList<>();
//        ArrayList<String> invalidIDs = new ArrayList<>();
//        validIDs.add("C000000");
//        assertParseSuccess(parser, " mass/", new MassApprove(validIDs, invalidIDs));
//
//    }

}
