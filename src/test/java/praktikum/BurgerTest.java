package praktikum;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class BurgerTest {

    private Burger burger;

    @Mock
    private Bun mockBun;

    @Mock
    private Ingredient mockIngredient1;

    @Mock
    private Ingredient mockIngredient2;

    @Mock
    private Ingredient mockIngredient3;

    private final String bunName;
    private final float bunPrice;
    private final IngredientType ingredientType;
    private final String ingredientName;
    private final float ingredientPrice;

    public BurgerTest(String bunName, float bunPrice, IngredientType ingredientType,
                      String ingredientName, float ingredientPrice) {
        this.bunName = bunName;
        this.bunPrice = bunPrice;
        this.ingredientType = ingredientType;
        this.ingredientName = ingredientName;
        this.ingredientPrice = ingredientPrice;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {"black bun", 100.0f, IngredientType.SAUCE, "hot sauce", 50.0f},
                {"white bun", 200.0f, IngredientType.FILLING, "cutlet", 75.0f},
                {"red bun", 150.5f, IngredientType.SAUCE, "chili sauce", 80.25f}
        };
    }

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        burger = new Burger();

        // Настройка моков для булки
        when(mockBun.getName()).thenReturn(bunName);
        when(mockBun.getPrice()).thenReturn(bunPrice);

        // Настройка моков для ингредиентов
        when(mockIngredient1.getType()).thenReturn(ingredientType);
        when(mockIngredient1.getName()).thenReturn(ingredientName);
        when(mockIngredient1.getPrice()).thenReturn(ingredientPrice);

        when(mockIngredient2.getType()).thenReturn(IngredientType.FILLING);
        when(mockIngredient2.getName()).thenReturn("cheese");
        when(mockIngredient2.getPrice()).thenReturn(30.0f);

        when(mockIngredient3.getType()).thenReturn(IngredientType.SAUCE);
        when(mockIngredient3.getName()).thenReturn("mayo");
        when(mockIngredient3.getPrice()).thenReturn(20.0f);
    }

    @Test
    public void testSetBuns() {
        burger.setBuns(mockBun);

        assertNotNull("Булочка должна быть установлена", burger.bun);
        assertEquals("Название булочки должно совпадать", bunName, burger.bun.getName());
        assertEquals("Цена булочки должна совпадать", bunPrice, burger.bun.getPrice(), 0.001);
    }

    @Test
    public void testAddIngredient() {
        burger.addIngredient(mockIngredient1);

        assertEquals("Список ингредиентов должен содержать 1 элемент", 1, burger.ingredients.size());
        assertEquals("Название ингредиента должно совпадать", ingredientName, burger.ingredients.get(0).getName());
    }

    @Test
    public void testRemoveIngredient() {
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        assertEquals("Начальное количество ингредиентов должно быть 2", 2, burger.ingredients.size());

        burger.removeIngredient(0);

        assertEquals("Количество ингредиентов должно быть 1 после удаления", 1, burger.ingredients.size());
        assertEquals("Оставшийся ингредиент должен быть вторым", "cheese", burger.ingredients.get(0).getName());
    }

    @Test
    public void testMoveIngredient() {
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);
        burger.addIngredient(mockIngredient3);

        // Проверяем первоначальный порядок
        assertEquals("Первый ингредиент должен быть " + ingredientName, ingredientName, burger.ingredients.get(0).getName());
        assertEquals("Второй ингредиент должен быть cheese", "cheese", burger.ingredients.get(1).getName());

        // Перемещаем первый ингредиент на позицию 2
        burger.moveIngredient(0, 2);

        // Проверяем новый порядок
        assertEquals("Первый ингредиент теперь должен быть cheese", "cheese", burger.ingredients.get(0).getName());
        assertEquals("Второй ингредиент теперь должен быть mayo", "mayo", burger.ingredients.get(1).getName());
        assertEquals("Третий ингредиент теперь должен быть " + ingredientName, ingredientName, burger.ingredients.get(2).getName());
    }

    @Test
    public void testGetPrice() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        float expectedPrice = (bunPrice * 2) + ingredientPrice + 30.0f;
        float actualPrice = burger.getPrice();

        assertEquals("Общая цена должна быть рассчитана правильно", expectedPrice, actualPrice, 0.001);
    }

    @Test
    public void testGetReceipt() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);

        String receipt = burger.getReceipt();

        assertNotNull("Чек не должен быть null", receipt);
        assertTrue("Чек должен содержать название булочки", receipt.contains(bunName));
        assertTrue("Чек должен содержать название ингредиента", receipt.contains(ingredientName));
        assertTrue("Чек должен содержать тип ингредиента", receipt.contains(ingredientType.toString().toLowerCase()));
        assertTrue("Чек должен содержать общую цену", receipt.contains("Price:"));
    }

    @Test
    public void testGetReceiptWithMultipleIngredients() {
        burger.setBuns(mockBun);
        burger.addIngredient(mockIngredient1);
        burger.addIngredient(mockIngredient2);

        String receipt = burger.getReceipt();

        assertNotNull("Чек не должен быть null", receipt);
        assertTrue("Чек должен содержать первый ингредиент", receipt.contains(ingredientName));
        assertTrue("Чек должен содержать второй ингредиент", receipt.contains("cheese"));

        // Проверяем структуру чека
        String[] lines = receipt.split("\n");
        assertTrue("Чек должен содержать несколько строк", lines.length >= 4);
        assertTrue("Первая строка должна содержать булочку", lines[0].contains(bunName));
        assertTrue("Последняя строка должна содержать цену", lines[lines.length - 1].contains("Price:"));
    }

    @Test
    public void testBurgerWithNoIngredients() {
        burger.setBuns(mockBun);

        float price = burger.getPrice();
        String receipt = burger.getReceipt();

        assertEquals("Цена должна быть равна цене булочки умноженной на 2", bunPrice * 2, price, 0.001);

        // Проверяем конкретный ожидаемый формат чека без ингредиентов
        String expectedReceipt = String.format("(==== %s ====)%n", bunName) +
                String.format("(==== %s ====)%n", bunName) +
                String.format("%nPrice: %f%n", price);

        assertEquals("Чек должен соответствовать ожидаемому формату без ингредиентов",
                expectedReceipt, receipt);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveIngredientWithInvalidIndex() {
        burger.addIngredient(mockIngredient1);
        burger.removeIngredient(5); // Неверный индекс
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testMoveIngredientWithInvalidIndex() {
        burger.addIngredient(mockIngredient1);
        burger.moveIngredient(0, 5); // Неверный индекс
    }
}