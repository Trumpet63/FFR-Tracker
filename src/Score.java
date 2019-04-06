package src;

import java.util.Date;
import java.util.Objects;
import org.json.JSONObject;

public class Score implements Comparable<Score> {
    private int timestamp;
    private String dateString;
    private Date date;
    int levelID;
    int numPerfect;
    int numGood;
    int numAverage;
    int numMiss;
    int numBoo;
    int combo;
    int difficulty;
    int score;
    private String name;
    private double AAAEquivalent;
    boolean completed;
    int rank;
    int noteCount;
    String delimiter = ",";
    String delimiterReplacement = "Zg1qW";
    
    public int getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
    public String getDateString() {
        return dateString;
    }
    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public double getAAAEquivalent() {
        return AAAEquivalent;
    }
    public void setAAAEquivalent(double AAAEquivalent) {
        this.AAAEquivalent = AAAEquivalent;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
    public Score(JSONObject gameJSON) {
        setTimestamp(gameJSON.getInt("timestamp"));
        setDateString(getDateFromTimestamp(getTimestamp()));
        levelID = gameJSON.getInt("level");
        numPerfect = gameJSON.getInt("perfect");
        numGood = gameJSON.getInt("good");
        numAverage = gameJSON.getInt("average");
        numMiss = gameJSON.getInt("miss");
        numBoo = gameJSON.getInt("boo");
        combo = gameJSON.getInt("combo");
        difficulty = gameJSON.getInt("difficulty");
        score = gameJSON.getInt("score");
        setName(gameJSON.getString("name"));
        setAAAEquivalent(gameJSON.getDouble("aaa_equivalent"));
        completed = gameJSON.getBoolean("completed");
        rank = gameJSON.getInt("rank");
        noteCount = gameJSON.getInt("note_count");
    }
    
    public Score(String line) throws NumberFormatException {
        String[] contents = line.split(delimiter);
        setTimestamp(Integer.parseInt(contents[0]));
        setDateString(getDateFromTimestamp(getTimestamp()));
        levelID = Integer.parseInt(contents[1]);
        numPerfect = Integer.parseInt(contents[2]);
        numGood = Integer.parseInt(contents[3]);
        numAverage = Integer.parseInt(contents[4]);
        numMiss = Integer.parseInt(contents[5]);
        numBoo = Integer.parseInt(contents[6]);
        combo = Integer.parseInt(contents[7]);
        difficulty = Integer.parseInt(contents[8]);
        score = Integer.parseInt(contents[9]);
        setName(replaceDelimiterFlag(contents[10]));
        setAAAEquivalent(Double.parseDouble(contents[11]));
        completed = contents[12].equals("true");
        rank = Integer.parseInt(contents[13]);
        noteCount = Integer.parseInt(contents[14]);
    }
    
    private String replaceDelimiterFlag(String string) {
        return string.replaceAll(delimiterReplacement, delimiter);
    }
    
    private String replaceDelimiterWithFlag(String string) {
        return string.replaceAll(delimiter, delimiterReplacement);
    }

    @Override
    public int compareTo(Score s) {
        return getTimestamp() - s.getTimestamp();
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Score))
            return false;
        Score s = (Score) o;
        return s.getTimestamp() == getTimestamp() &&
                s.levelID == levelID &&
                s.numPerfect == numPerfect &&
                s.numGood == numGood &&
                s.numAverage == numAverage &&
                s.numMiss == numMiss &&
                s.numBoo == numBoo &&
                s.combo == combo;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.timestamp);
        hash = 41 * hash + this.levelID;
        hash = 41 * hash + this.numPerfect;
        hash = 41 * hash + this.numGood;
        hash = 41 * hash + this.numAverage;
        hash = 41 * hash + this.numMiss;
        hash = 41 * hash + this.numBoo;
        hash = 41 * hash + this.combo;
        return hash;
    }
    
    private String getDateFromTimestamp(int timestamp) {
        Date date = new Date(((long) timestamp) * 1000);
        return Config.DATE_FORMAT.format(date);
    }

    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder();
        stb.append(getTimestamp());
        stb.append(delimiter);
        stb.append(levelID);
        stb.append(delimiter);
        stb.append(numPerfect);
        stb.append(delimiter);
        stb.append(numGood);
        stb.append(delimiter);
        stb.append(numAverage);
        stb.append(delimiter);
        stb.append(numMiss);
        stb.append(delimiter);
        stb.append(numBoo);
        stb.append(delimiter);
        stb.append(combo);
        stb.append(delimiter);
        stb.append(difficulty);
        stb.append(delimiter);
        stb.append(score);
        stb.append(delimiter);
        stb.append(replaceDelimiterWithFlag(getName()));
        stb.append(delimiter);
        stb.append(getAAAEquivalent());
        stb.append(delimiter);
        stb.append(completed);
        stb.append(delimiter);
        stb.append(rank);
        stb.append(delimiter);
        stb.append(noteCount);
        return stb.toString();
    }
}
