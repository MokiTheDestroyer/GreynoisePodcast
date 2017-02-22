package com.mokithedestroyer.greynoise;

import java.util.Objects;


public class Episodes {

    private String mTitle;
    private String mLink;
    private String mDate;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public String getDate(){
        return mDate;
    }

    public void setDate(String date){
        String cutDate = date.substring(0, 16);
        mDate = cutDate;
    }

    @Override
    public String toString() {
        if(getTitle().substring(0, 7).toLowerCase().contains("episode")) {
            String[] splitEpisodeContent = getTitle().split(" - ");
            String splitEpisodeNum = splitEpisodeContent[0];
            String splitEpisodeTitle = splitEpisodeContent[1];

            return splitEpisodeNum + "\n" +
                    splitEpisodeTitle + "\n" +
                    getDate();
        }else {
            return getTitle() + "\n" +
                    getDate();
        }
    }
}
