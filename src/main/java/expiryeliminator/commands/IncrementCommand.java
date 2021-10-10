package expiryeliminator.commands;

import expiryeliminator.data.Ingredient;
import expiryeliminator.data.IngredientList;
import expiryeliminator.data.RecipeList;
import expiryeliminator.data.exception.NotFoundException;

/**
 * Increment ingredient by a specified quantity.
 */
public class IncrementCommand extends Command {
    /** Unique word associated with the command. */
    public static final String COMMAND_WORD = "increment";

    private static final String MESSAGE_INGREDIENT_NOT_FOUND = "Sorry. No matching ingredients found!";
    private static final String MESSAGE_INGREDIENT_INCREMENTED = "I've incremented this ingredient by %1$s:\n" + "%2$s";

    private final String ingredientName;
    private final int quantity;

    public IncrementCommand(String ingredientName, int quantity) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
    }

    @Override
    public String execute(IngredientList ingredients, RecipeList recipes) {
        final Ingredient ingredient;
        try {
            ingredient = ingredients.find(ingredientName);
        } catch (NotFoundException e) {
            return MESSAGE_INGREDIENT_NOT_FOUND;
        }
        ingredient.setQuantity(ingredient.getQuantity() + quantity);
        return String.format(MESSAGE_INGREDIENT_INCREMENTED, ingredient.getQuantity(), ingredient);
    }
}
