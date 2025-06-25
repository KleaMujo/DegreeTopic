package com.school.degreetopicsmanagement.DataObjects;

public class TopicFillSummary {
    private String topicTitle;
    private int activeStudents;

    public TopicFillSummary(String topicTitle, int activeStudents) {
        this.topicTitle = topicTitle;
        this.activeStudents = activeStudents;
    }
    public String getTopicTitle() {
        return topicTitle;
    }

    public int getActiveStudents() {
        return activeStudents;
    }
}
