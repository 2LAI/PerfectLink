package by.perfectlink.telegrambot.logic;

public class PerfectLink {

     private final String linkName;
     private final String uRL;
     private final String someInformation;

    public PerfectLink(String linkName, String uRL, String someInformation) {
        this.linkName = linkName;
        this.uRL = uRL;
        this.someInformation = someInformation;
    }


    @Override
    public String toString() {
        return "PerfectLink{" +
                "linkName='" + linkName + '\'' +
                ", URL='" + uRL + '\'' +
                ", someInformation='" + someInformation + '\'' +
                '}';
    }
}
