package hu.beernotfoundexception.fontastic.domain.repository;

public class Responses {

    public static final String S_SERVER_HELLO = "BeeZZZ 1.0 SERVER HELLO";

    public static final String S_SERVER_SEND_ID = "SEND YOUR ID";

    public static final String S_SERVER_ID_ACK = "ID ACK";

    public static final String S_SERVER_FONT_ACK = "FONT ACK";

    public static final String S_SERVER_FONT_ACK_EOC = "FONT ACK - NO IMAGE LEFT - GOODBYE";

    public static final String IMAGES_LEFT = "IMAGES LEFT: ";

    public static final String NO_IMAGE_LEFT = "NO IMAGE LEFT";

    public static final String S_SERVER_INVALID = "INVALID";

    public enum ResponseType {
        SERVER_HELLO,
        SERVER_SEND_ID,
        SERVER_ID_ACK,
        SERVER_FONT_ACK,
        SERVER_FONT_ACK_EOC,
        SERVER_INVALID,
        SERVER_UNDEFINED
    }
}
