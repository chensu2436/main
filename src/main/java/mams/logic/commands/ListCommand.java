package mams.logic.commands;

import static java.util.Objects.requireNonNull;

import mams.model.Model;

/**
 * Lists all students in the MAMS to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_LIST_SUCCESS = "Listed all following items:%1$s";

    public static final String APPEALS = "appeals";
    public static final String MODULES = "modules";
    public static final String STUDENTS = "students";

    private final boolean showAppeals;
    private final boolean showModules;
    private final boolean showStudents;

    public ListCommand(boolean showAppeals, boolean showModules, boolean showStudents) {
        this.showAppeals = showAppeals;
        this.showModules = showModules;
        this.showStudents = showStudents;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);

        StringBuilder msg = new StringBuilder();

        if (showAppeals) {
            model.updateFilteredAppealList(Model.PREDICATE_SHOW_ALL_APPEALS);
            msg.append(" ");
            msg.append(APPEALS);
        }
        if (showModules) {
            model.updateFilteredModuleList(Model.PREDICATE_SHOW_ALL_MODULES);
            msg.append(" ");
            msg.append(MODULES);
        }
        if (showStudents) {
            model.updateFilteredStudentList(Model.PREDICATE_SHOW_ALL_STUDENTS);
            msg.append(" ");
            msg.append(STUDENTS);
        }

        return new CommandResult(String.format(MESSAGE_LIST_SUCCESS, msg));
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand)) {
            return false;
        }

        // state check
        ListCommand v = (ListCommand) other;
        return showAppeals == v.showAppeals
                && showModules == v.showModules
                && showStudents == v.showStudents;
    }
}
