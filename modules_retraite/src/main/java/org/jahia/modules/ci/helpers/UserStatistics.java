package org.jahia.modules.ci.helpers;

public class UserStatistics implements Comparable<UserStatistics> {
    private String userUUID;
    private int numberOfReplies;
    private int numberOfQuestions;
    private int numberOfRepliesInOwnQuestion;

    public String getUserUUID() {
        return userUUID;
    }

    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    public int getNumberOfReplies() {
        return numberOfReplies;
    }

    public void setNumberOfReplies(int numberOfReplies) {
        this.numberOfReplies = numberOfReplies;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public int getNumberOfRepliesInOwnQuestion() {
        return numberOfRepliesInOwnQuestion;
    }

    public void setNumberOfRepliesInOwnQuestion(int numberOfRepliesInOwnQuestion) {
        this.numberOfRepliesInOwnQuestion = numberOfRepliesInOwnQuestion;
    }


    /**
     * Used for the sort (most active users)
     * 1 point acquis par question posee
     * 0.5 point par reponse apportee dans sa propre question
     * 2 points par reponse apportee à un autre membre
     *
     * @return
     */
    private double getRank() {
        return getNumberOfQuestions()
                + 2 * (getNumberOfReplies() - getNumberOfRepliesInOwnQuestion() )
                + 0.5 * getNumberOfRepliesInOwnQuestion();
    }

    public int compareTo(UserStatistics other) {
        double result = other.getRank() - this.getRank();
        if (result > 0) {
            return 1;
        } else if (result < 0) {
            return -1;
        }
        return 0;
    }
}
