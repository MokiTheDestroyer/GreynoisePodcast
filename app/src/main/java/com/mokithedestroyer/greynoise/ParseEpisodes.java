package com.mokithedestroyer.greynoise;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseEpisodes {

    private String typeOfList = "interviews";
    private Boolean titleName = false;
    private String data;
    private ArrayList<Episodes> mEpisodes;

    public void setTypeOfList(String listType){
        typeOfList = listType;
    }

    public ParseEpisodes(String xmlData) {
        this.data = xmlData;
        mEpisodes = new ArrayList<>();
    }

    public ArrayList<Episodes> getEpisodes() {
        return mEpisodes;
    }

    public boolean process(){
        boolean status = true;
        Episodes currentRecord = null;
        boolean inEntry = false;
        String textValue = "";
        String urlValue = "";

        try{
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(this.data));
            int eventType = xpp.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                String tagName = xpp.getName();
                switch(eventType){
                    case XmlPullParser.START_TAG:
                        if(tagName.equalsIgnoreCase("item")){
                            inEntry = true;
                            currentRecord = new Episodes();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;


                    case XmlPullParser.END_TAG:
                        if(inEntry){
                            if(tagName.equalsIgnoreCase("item")){
                                if(titleName) {
                                    mEpisodes.add(currentRecord);
                                }
                                inEntry = false;
                            }else if(tagName.equalsIgnoreCase("title")){
                                if(typeOfList.equalsIgnoreCase("episodes")) {
                                    if (textValue.toLowerCase().contains("episode")) {
                                        titleName = true;
                                        currentRecord.setTitle(textValue);
                                    } else {
                                        titleName = false;
                                    }
                                }else if(typeOfList.equalsIgnoreCase("interviews")){
                                    if (!textValue.toLowerCase().contains("episode")) {
                                        titleName = true;
                                        currentRecord.setTitle(textValue);
                                    } else {
                                        titleName = false;
                                    }
                                }
                            }else if(tagName.equalsIgnoreCase("enclosure")){
                                urlValue = xpp.getAttributeValue(0);
                                currentRecord.setLink(urlValue);
                            }else if(tagName.equalsIgnoreCase("pubdate")){
                                currentRecord.setDate(textValue);
                            }
                        }
                        break;
                }
                eventType = xpp.next();
            }
        }catch (Exception e){
            status = false;
            e.printStackTrace();
        }
        return true;

    }
}
