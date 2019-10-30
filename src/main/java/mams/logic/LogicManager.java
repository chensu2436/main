package mams.logic;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import mams.commons.core.GuiSettings;
import mams.commons.core.LogsCenter;
import mams.commons.exceptions.DataConversionException;
import mams.logic.commands.Command;
import mams.logic.commands.CommandResult;
import mams.logic.commands.RedoCommand;
import mams.logic.commands.SaveCommand;
import mams.logic.commands.UndoCommand;
import mams.logic.commands.exceptions.CommandException;
import mams.logic.parser.MamsParser;
import mams.logic.parser.exceptions.ParseException;
import mams.model.Model;
import mams.model.ReadOnlyMams;
import mams.model.appeal.Appeal;
import mams.model.module.Module;
import mams.model.student.Student;
import mams.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_MESSAGE = "Could not save data to file: ";
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final MamsParser mamsParser;
    private final CommandHistory commandHistory;

    public LogicManager(Model model, Storage storage) {
        Optional<ReadOnlyCommandHistory> commandHistoryOptional;
        ReadOnlyCommandHistory startingCommandHistory;

        this.model = model;
        this.storage = storage;
        this.mamsParser = new MamsParser();
        try { // attempt to load CommandHistory from disk
            commandHistoryOptional = storage.readCommandHistory();
            if (!commandHistoryOptional.isPresent()) {
                logger.info("Command history data file not found. Starting with a blank command history...");
            }
            startingCommandHistory = commandHistoryOptional.orElseGet(CommandHistory::new);
        } catch (IOException | DataConversionException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty MAMS");
            startingCommandHistory = new CommandHistory();
        }
        this.commandHistory = new CommandHistory(startingCommandHistory);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command;

        try {
            command = mamsParser.parseCommand(commandText);
            if (!(command instanceof UndoCommand) && !(command instanceof RedoCommand)) {
                new SaveCommand("undo").privateExecute(model);
            }
            commandResult = command.execute(model);
            storage.saveMams(model.getMams());
            commandHistory.add(commandText, commandResult.getFeedbackToUser());
        } catch (CommandException | ParseException e) {
            commandHistory.add(commandText, e.getMessage());
            throw e; // after getting message, rethrow. stacktrace is not lost
        } catch (IOException ioe) {
            commandHistory.add(commandText, ioe.getMessage());
            throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
        }

        try {
            storage.saveCommandHistory(commandHistory);
        } catch (IOException ioe) {
            throw new CommandException(FILE_OPS_ERROR_MESSAGE + ioe, ioe);
        }
        return commandResult;
    }

    @Override
    public ReadOnlyMams getMams() {
        return model.getMams();
    }

    @Override
    public ObservableList<Student> getFilteredStudentList() {
        return model.getFilteredStudentList();
    }

    @Override
    public ObservableList<Module> getFilteredModuleList() {
        return model.getFilteredModuleList();
    }

    @Override
    public ObservableList<Appeal> getFilteredAppealList() {
        return model.getFilteredAppealList();
    }

    @Override
    public ObservableList<InputOutput> getCommandHistory() {
        return commandHistory.getInputOutputHistory();
    }

    @Override
    public Path getMamsFilePath() {
        return model.getMamsFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
