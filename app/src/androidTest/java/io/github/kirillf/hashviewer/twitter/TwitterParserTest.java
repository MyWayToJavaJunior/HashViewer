package io.github.kirillf.hashviewer.twitter;

import android.test.AndroidTestCase;

import java.util.List;

public class TwitterParserTest extends AndroidTestCase{
    private TwitterParser parser;
    private String validJson = TwitterTestData.getValidJson();
    private String singleValidJson = TwitterTestData.getSingleValidJson();
    private String partlyValidJson = TwitterTestData.getPartlyValidJson();

    public TwitterParserTest() {
        super();
        parser = new TwitterParser();
    }

    public void testReturnNotNullListAfterValidParse() throws Exception {
        List<TwitterObject> parsedResult = parser.parse(validJson);

        assertNotNull("Null result after valid parse", parsedResult);
    }


    public void testReturnNotEmptyListAfterValidParse() throws Exception {
        List<TwitterObject> parsedResult = parser.parse(validJson);

        assertTrue("Empty list after valid parse", !parsedResult.isEmpty());
    }


    public void testReturnExceptedAmountOfTweetsAfterValidParse() throws Exception {
        List<TwitterObject> parsedResult = parser.parse(validJson);

        assertTrue("Not expected list size", parsedResult.size() == 4);
    }


    public void testGetValidTwitterObjectId() throws Exception {
        List<TwitterObject> singleResult = parser.parse(singleValidJson);
        TwitterObject result = singleResult.get(0);

        assertEquals("Incorrect parsed id", result.getId(), 250075927172759552L);
    }


    public void testGetValidTwitterObjectText() throws Exception {
        List<TwitterObject> singleResult = parser.parse(singleValidJson);
        TwitterObject result = singleResult.get(0);

        assertEquals("Incorrect parsed text", result.getText(), "Aggressive Ponytail #freebandnames");
    }


    public void testGetValidTwitterObjectUsername() throws Exception {
        List<TwitterObject> singleResult = parser.parse(singleValidJson);
        TwitterObject result = singleResult.get(0);

        assertEquals("Incorrect parsed username", result.getUserName(), "Sean Cummings");
    }


    public void testGetValidTwitterObjectDate() throws Exception {
        List<TwitterObject> singleResult = parser.parse(singleValidJson);
        TwitterObject result = singleResult.get(0);
        long targetDate = 1348457721000L;

        assertEquals("Incorrect parsed date", result.getDate(),targetDate);
    }


    public void testGetValidTwitterObjectProfileImageUrl() throws Exception {
        List<TwitterObject> singleResult = parser.parse(singleValidJson);
        TwitterObject result = singleResult.get(0);

        assertEquals("Incorrect parsed profile url", result.getProfileImageUrl(), "https://si0.twimg.com/profile_images/2359746665/1v6zfgqo8g0d3mk7ii5s_bigger.jpeg");
    }



    public void testParsePartlyFilledJson() throws Exception {
        List<TwitterObject> partlyCorrect = parser.parse(partlyValidJson);

        assertEquals("Incorrect list size", partlyCorrect.size(), 3);
    }

}