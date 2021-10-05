package expiryeliminator.parser;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import expiryeliminator.commands.AddIngredientCommand;
import expiryeliminator.commands.ByeCommand;
import expiryeliminator.commands.Command;
import expiryeliminator.commands.IncorrectCommand;

/**
 * Parses user input.
 */
public class Parser {
    //@@author bernardboey-reused
    // Reused from https://github.com/bernardboey/ip/blob/master/src/main/java/duke/parser/Parser.java
    // with minor modifications

    /** Used for initial separation of command word and args. */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("^(?<command>[^/]+)(?<args> .*)?$");
    /**
     * Used for separation of each prefix and argument.
     * E.g. this matches "/a aaaaa aaaa"
     */
    private static final Pattern ARGS_FORMAT = Pattern.compile("\\w+/([^/\\s]+( +|$))+");

    private static final String PREFIX_INGREDIENT = "i";
    private static final String PREFIX_QUANTITY = "q";
    private static final String PREFIX_EXPIRY = "e";

    private static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format!";
    private static final String MESSAGE_UNRECOGNISED_COMMAND = "I'm sorry, but I don't know what that means :-(";
    private static final String MESSAGE_INVALID_QUANTITY = "The specified quantity is not a valid integer.";

    /**
     * Parses user input as a command.
     *
     * @param userInput Input command together with any arguments.
     * @return Command that corresponds to the user input, if valid.
     */
    public static Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput);
        if (!matcher.matches()) {
            throw new RuntimeException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
        final String command = matcher.group("command");
        String args = matcher.group("args");

        // May not contain all arguments (e.g. if user did not provide).
        final HashMap<String, ArrayList<String>> prefixesToArgs;

        //catches null if user types in wrong prefix.
        try {
            prefixesToArgs = parseArgs(args);
        } catch (NullPointerException error) {
            return new IncorrectCommand(MESSAGE_INVALID_COMMAND_FORMAT);
        }

        switch (command) {
        case AddIngredientCommand.COMMAND_WORD:
            return prepareAddIngredient(prefixesToArgs);
        case ByeCommand.COMMAND_WORD:
            return new ByeCommand();
        default:
            return new IncorrectCommand(MESSAGE_UNRECOGNISED_COMMAND);
        }
    }
    //@@author

    private static HashMap<String, String> parseArgs(String args) {
    private static HashMap<String, ArrayList<String>> parseArgs(String args) {
        // TODO(bernardboey): Change the way this is done to allow for multiple of the same tags.
        // Separate to different class and feed in the argument prefixes that are expected when parsing?
        /* Changed method so that it can store multiple values for each prefix, but have to manually set up the Hash
        Map */
        args = args == null ? "" : args.trim();
        final HashMap<String, ArrayList<String>> prefixesToArgs = new HashMap<>();
        setUpPrefixesToArgs(prefixesToArgs);
        final Matcher matcher = ARGS_FORMAT.matcher(args);

        while (matcher.find()) {
            final String match = matcher.group();
            final String[] prefixAndArg = match.split("/");
            final String prefix = prefixAndArg[0];
            final String arg = prefixAndArg[1].trim();
            prefixesToArgs.put(prefix, arg);
        }
        return prefixesToArgs;
    }

    private static Command prepareAddIngredient(HashMap<String, String> prefixesToArgs) {
        final String ingredient = prefixesToArgs.get(PREFIX_INGREDIENT);
        final String quantity = prefixesToArgs.get(PREFIX_QUANTITY);
        final String expiry = prefixesToArgs.get(PREFIX_EXPIRY);
    private static void setUpPrefixesToArgs(HashMap<String, ArrayList<String>> prefixesToArgs) {
        prefixesToArgs.put(PREFIX_INGREDIENT,new ArrayList<>());
        prefixesToArgs.put(PREFIX_EXPIRY,new ArrayList<>());
        prefixesToArgs.put(PREFIX_RECIPE,new ArrayList<>());
        prefixesToArgs.put(PREFIX_QUANTITY,new ArrayList<>());
    }
        if (ingredient == null || ingredient.isBlank()) {
            return new IncorrectCommand("Missing ingredient");
        }
        if (quantity == null || quantity.isBlank()) {
            return new IncorrectCommand("Missing quantity");
        }
        if (expiry == null || expiry.isBlank()) {
            return new IncorrectCommand("Missing expiry");
        }
        try {
            return new AddIngredientCommand(ingredient, parseQuantity(quantity), expiry);
        } catch (NumberFormatException e) {
            return new IncorrectCommand(MESSAGE_INVALID_QUANTITY);
        }
    }

    private static int parseQuantity(String quantityString) throws NumberFormatException {
        return Integer.parseInt(quantityString);
    }
}
