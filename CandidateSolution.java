/*
 * Copyright (C) 1993-2020 ID Business Solutions Limited
 * All rights reserved
 */
package com.idbs.devassessment.solution;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import com.idbs.devassessment.core.IDBSSolutionException;
import com.idbs.devassessment.core.DifficultyLevel;
import com.idbs.devassessment.harness.DigitalTaxTracker;

public class CandidateSolution extends AssessmentSolutionBase {
    @Override
    public DifficultyLevel getDifficultyLevel() {
        return DifficultyLevel.LEVEL_3;
    }

    public long performAction(long res, String action, long termVal) {
        if (action.equals("add")) {
            res = DigitalTaxTracker.add(res, termVal);
        } else if (action.equals("subtract")) {
            res = DigitalTaxTracker.subtract(res, termVal);
        }
        return res;
    }

    public long calculatePolynomial(JsonArray terms, int xValue) {
        long result = 0;  // Use long for large results
        for (int i = 0; i < terms.size(); i++) {
            JsonObject term = terms.getJsonObject(i);
            int power = term.getInt("power");
            int multiplier = term.getInt("multiplier");
            String action = term.getString("action");

            long termValue = multiply(pow(xValue, power), multiplier);

            result = performAction(result, action, termValue);
        }
        return result;
    }

    public long pow(long base, long exponent) {
        long result = 1;
        for (int i = 0; i < exponent; i++) {
            result = multiply(result, base);
        }
        return result;
    }

    public long multiply(long num1, long num2) {
        long res = 0;
        for (int i = 0; i < num2; i++) {
            res = DigitalTaxTracker.add(res, num1);
        }
        return res;
    }

    public String inputFormat(String input) {
        input = input.trim();
        if (input.startsWith("{") && input.endsWith("}")) {
            return "Json";
        } else if (input.startsWith("numeric:") && input.contains("x =")) {
            return "Numeric";
        } else {
            return "invalid/other format";
        }
    }

    @Override
    public String getAnswer() throws IDBSSolutionException {
        if (inputFormat(getDataForQuestion()).equals("Json")) {
            return handleJsonFormat();
        } else if (inputFormat(getDataForQuestion()).equals("Numeric")) {
            return calculateNumeric();
        } else return (inputFormat(getDataForQuestion()));
    }

    public String handleJsonFormat() {
        String json = getDataForQuestion();
        JsonReader reader = Json.createReader(new StringReader(json));
        JsonObject jsonObject = reader.readObject();
        reader.close();

        // Extract the value for x
        int xVal = jsonObject.getInt("xValue");

        // Read the terms from the JSON
        JsonArray terms = jsonObject.getJsonArray("terms");

        long res = calculatePolynomial(terms, xVal);

        return Long.toString(res);
    }

    public String calculateNumeric() {
        String input = getDataForQuestion();
        // Turn the numeric input into a character array
        char[] charArray = input.toCharArray();

        // Get xValue from fixed positions (index 12 and optionally 13 for double digit)
        boolean doubleDigits = false;
        long xValue = Character.getNumericValue(charArray[12]);
        if (charArray[13] != ';') {
            xValue = DigitalTaxTracker.add(multiply(10, xValue), Character.getNumericValue(charArray[13]));
            doubleDigits = true;
        }

        // Extract the polynomial part after "y ="
        int yIndex = input.indexOf("y =");
        if (yIndex == -1) {
            return "Invalid input format";
        }
        String termsString = input.substring(yIndex + 4).trim();

        long result = 0L;
        int i = 0;
        // Process each term from the polynomial string
        while (i < termsString.length()) {
            // Get the operator ('+' or '-')
            char operator = termsString.charAt(i);
            int sign = (operator == '+') ? 1 : -1;
            i++; // Move past the operator

            // Parse the coefficient (handle one or two digits)
            int coefficient = Character.getNumericValue(termsString.charAt(i));
            i++;
            if (i < termsString.length() && Character.isDigit(termsString.charAt(i)) && termsString.charAt(i) != '.') {
                coefficient = (int) DigitalTaxTracker.add(multiply(coefficient, 10), Character.getNumericValue(termsString.charAt(i)));
                i++;
            }

            // Expect a '.' character (the multiply sign)
            if (i < termsString.length() && termsString.charAt(i) == '.') {
                i++;
            } else {
                return "Invalid term format: missing '.'";
            }

            // Skip the literal 'x'
            if (i < termsString.length() && termsString.charAt(i) == 'x') {
                i++;
            } else {
                return "Invalid term format: missing 'x'";
            }

            // Expect '^'
            if (i < termsString.length() && termsString.charAt(i) == '^') {
                i++;
            } else {
                return "Invalid term format: missing '^'";
            }

            // Parse the exponent (assume a single digit)
            int exponent = Character.getNumericValue(termsString.charAt(i));
            i++; // Move past the exponent

            // Compute the term value: coefficient * (xValue ^ exponent)
            long termValue = multiply(pow(xValue, exponent), coefficient);

            if (sign == 1) {
                result = DigitalTaxTracker.add(result, termValue);
            } else {
                result = DigitalTaxTracker.subtract(result, termValue);
            }
        }

        return Long.toString(result);
    }
}