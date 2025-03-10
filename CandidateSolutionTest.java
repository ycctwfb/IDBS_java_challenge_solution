package com.idbs.devassessment.solution.test;

import com.idbs.devassessment.core.DifficultyLevel;
import com.idbs.devassessment.core.IDBSSolutionException;
import com.idbs.devassessment.solution.CandidateSolution;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

class CandidateSolutionTest {

    private final CandidateSolution solution = new CandidateSolution();

    // Helper method to set the dataForQuestion field using reflection
    private void setDataForQuestion(String data) throws NoSuchFieldException, IllegalAccessException {
        Field field = CandidateSolution.class.getDeclaredField("dataForQuestion");
        field.setAccessible(true);  // Make the field accessible
        field.set(solution, data);
    }

    @Test
    void testGetDifficultyLevel() {
        assertEquals(DifficultyLevel.LEVEL_3, solution.getDifficultyLevel());
    }

    @Test
    void testPow() {
        // Test case: 2^3 = 8
        assertEquals(8, solution.pow(2, 3));

        // Test case: 3^0 = 1
        assertEquals(1, solution.pow(3, 0));

        // Test case: 0^5 = 0
        assertEquals(0, solution.pow(0, 5));
    }

    @Test
    void testMultiply() {
        // Test case: 2 * 3 = 6
        assertEquals(6, solution.multiply(2, 3));

        // Test case: 5 * 0 = 0
        assertEquals(0, solution.multiply(5, 0));

        // Test case: 0 * 5 = 0
        assertEquals(0, solution.multiply(0, 5));
    }

    @Test
    void testPerformActionAdd() {
        long result = solution.performAction(5, "add", 3);
        assertEquals(8, result);
    }

    @Test
    void testPerformActionSubtract() {
        long result = solution.performAction(5, "subtract", 3);
        assertEquals(2, result);
    }

    @Test
    void testInputFormatJson() {
        String input = "{\"xValue\": 2, \"terms\": [{\"power\": 2, \"multiplier\": 1, \"action\": \"add\"}]}";
        assertEquals("Json", solution.inputFormat(input));
    }

    @Test
    void testInputFormatNumeric() {
        String input = "numeric: x = 2; y = 3x^2 + 1x^1;";
        assertEquals("Numeric", solution.inputFormat(input));
    }

    @Test
    void testInputFormatInvalid() {
        String input = "invalid input";
        assertEquals("invalid/other format", solution.inputFormat(input));
    }

    @Test
    void testCalculatePolynomial() {
        JsonArray terms = Json.createArrayBuilder()
                .add(Json.createObjectBuilder().add("power", 2).add("multiplier", 1).add("action", "add"))
                .add(Json.createObjectBuilder().add("power", 1).add("multiplier", 1).add("action", "subtract"))
                .build();

        long result = solution.calculatePolynomial(terms, 2);
        assertEquals(2, result);
    }

}
