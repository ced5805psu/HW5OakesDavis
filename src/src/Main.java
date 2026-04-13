/** Project: Solo Lab5.5 javaBruteForceFreqAnalysis Homework Assignment using a Foreign Language
 * Purpose Details: To teach about frequency analysis and APIs
 * Course: IST 242 Section 001
 * Author: Conner Davis
 * Date Developed: 4/13/2026
 * Last Date Changed: 4/13/2026
 * Rev:

 */

import java.io.*;                  // For input/output streams
import java.net.HttpURLConnection; // For HTTP connection handling
import java.net.URL;               // For URL object
import java.util.Scanner;          // For user input
import java.util.*;

public class Main {
    private static final double[] ARABIC_FREQUENCIES = {
            11.6, // ا (Alif)
            4.8, // ب
            3.7, // ت
            1.1, // ث
            2.8, // ج
            2.6, // ح
            1.1, // خ
            3.5, // د
            1.0, // ذ
            4.7, // ر
            0.9, // ز
            6.5, // س
            3.0, // ش
            2.9, // ص
            1.5, // ض
            1.7, // ط
            0.7, // ظ
            3.9, // ع
            1.0, // غ
            3.0, // ف
            2.7, // ق
            3.6, // ك
            5.3, // ل
            3.1, // م
            7.2, // ن
            2.5, // ه
            6.0, // و
            6.7 // ي
    };

    public static void main(String[] args) throws Exception {


        // Create Scanner to read user input from keyboard
        Scanner sc = new Scanner(System.in);

        // Prompt user for Google API key
        System.out.print("Enter the API Key: ");
        String apiKey = sc.nextLine();

        // Prompt user for text to translate
        System.out.print("Enter the plaintext: ");
        String text = sc.nextLine();

        // Build the request URL with API key
        String urlStr = "https://translation.googleapis.com/language/translate/v2"
                + "?key=" + apiKey;

        // Create URL object
        URL url = new URL(urlStr);

        // Open HTTP connection to the API endpoint
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set request method to POST (required by API)
        conn.setRequestMethod("POST");

        // Enable sending data in request body
        conn.setDoOutput(true);

        // Set content type to JSON
        conn.setRequestProperty("Content-Type", "application/json");

        // Create JSON request body
        // q = text to translate
        // target = target language (ar = Arabic)
        String jsonInput = "{ \"q\": \"" + text + "\", \"target\": \"ar\" }";

        // Send JSON data to API
        try (OutputStream os = conn.getOutputStream()) {
            // Convert string to bytes using UTF-8 encoding
            os.write(jsonInput.getBytes("utf-8"));
        }

        // Read response from API (input stream)
        BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "utf-8"));

        // Store response text
        StringBuilder response = new StringBuilder();
        String line;

        // Read response line by line
        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }

        // Output full JSON response
        System.out.println(response.toString());

        // NOTE:
        // Response will look like:
        // {
        //   "data": {
        //     "translations": [
        //       { "translatedText": "..." }
        //     ]
        //   }
        // }
        /**
         * Tries all 28 possible Caesar shifts and selects the one that produces
         * text closest to standard Arabic letter frequencies.
         */

    }
    public static String decryptUsingFrequencyAnalysis(String ciphertext) {
        String bestDecryption = "";
        double lowestChiSquare = Double.MAX_VALUE; // Lower is better match

        // Try all possible shifts (0–25)
        for (int shift = 0; shift < 26; shift++) {

            // Decrypt using the current shift
            String decryptedText = decryptWithShift(ciphertext, shift);

            // Measure how "Arabic-like" the result is
            double chiSquare = calculateChiSquare(decryptedText);

            // Keep track of the best (lowest chi-square score)
            if (chiSquare < lowestChiSquare) {
                lowestChiSquare = chiSquare;
                bestDecryption = decryptedText;
            }
        }

        return bestDecryption;
    }

    /**
     * Decrypts a Caesar cipher using a given shift value.
     * Handles both uppercase and lowercase letters.
     */
    public static String decryptWithShift(String text, int shift) {
        StringBuilder decryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {

            // Only shift alphabetic characters
            if (Character.isLetter(c)) {

                // Determine base ASCII value ('A' or 'a')
                char base = Character.isUpperCase(c) ? 'A' : 'a';

                // Apply reverse shift with wrap-around using modulo
                decryptedText.append(
                        (char) ((c - base - shift + 26) % 26 + base)
                );

            } else {
                // Preserve spaces, punctuation, etc.
                decryptedText.append(c);
            }
        }

        return decryptedText.toString();
    }

    /**
     * Computes the Chi-Square statistic:
     * Measures how closely the letter frequency of the given text
     * matches expected Arabic letter frequencies.
     *
     * Lower value = closer match to Arabic = more likely correct decryption
     */
    public static double calculateChiSquare(String text) {

        int[] letterCounts = new int[26]; // Frequency of A–Z
        int totalLetters = 0;

        // Count occurrences of each letter
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char lowerCaseChar = Character.toLowerCase(c);
                letterCounts[lowerCaseChar - 'a']++;
                totalLetters++;
            }
        }

        double chiSquare = 0.0;

        // Compare observed vs expected frequencies
        for (int i = 0; i < 26; i++) {
            double observed = letterCounts[i];

            // Expected count based on Arabic frequency distribution
            double expected = totalLetters * ARABIC_FREQUENCIES[i] / 100;

            // Chi-square formula: Σ (O - E)^2 / E
            chiSquare += Math.pow(observed - expected, 2) / expected;
        }

        return chiSquare;
    }
}