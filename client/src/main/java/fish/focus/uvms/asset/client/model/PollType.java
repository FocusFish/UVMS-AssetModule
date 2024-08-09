package fish.focus.uvms.asset.client.model;

public enum PollType {

    PROGRAM_POLL,
    SAMPLING_POLL,
    MANUAL_POLL,
    CONFIGURATION_POLL,
    AUTOMATIC_POLL,
    BASE_POLL;

    public static PollType fromValue(String v) {
        return valueOf(v);
    }

    public String value() {
        return name();
    }

}
