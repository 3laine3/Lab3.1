package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    private final Map<String, Map<String, String>> countryTranslateMap;

    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */
    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            countryTranslateMap = new HashMap<String, Map<String, String>>();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, String> languageTranslateMap = new HashMap<String, String>();
                for (String key: jsonObject.keySet()) {

                    if (!"id".equals(key) && !"alpha2".equals(key) && !"alpha3".equals(key)) {
                        languageTranslateMap.put(key, jsonObject.getString(key));
                    }
                }
                countryTranslateMap.put(jsonObject.getString("alpha3").toUpperCase(), languageTranslateMap);
            }

        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        Map<String, String> languageTranslateMap = countryTranslateMap.get(country.toUpperCase());
        return new ArrayList<>(languageTranslateMap.keySet());
    }

    @Override
    public List<String> getCountries() {
        return new ArrayList<>(countryTranslateMap.keySet());
    }

    @Override
    public String translate(String country, String language) {
        return countryTranslateMap.get(country.toUpperCase()).get(language);
    }
}
