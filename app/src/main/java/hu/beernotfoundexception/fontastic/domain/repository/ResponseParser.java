package hu.beernotfoundexception.fontastic.domain.repository;

/**
 * The type Response parser.
 */
public class ResponseParser {

    /**
     * Gets the server response type.
     *
     * @param msg the server command
     * @return the response type
     */
    public static Responses.ResponseType getResponseType(String msg) {

        switch (msg) {
            case Responses.S_SERVER_HELLO:
            case Responses.S_SERVER_SEND_ID:
            case Responses.S_SERVER_FONT_ACK_EOC:
                default:
                    if(msg.startsWith(Responses.S_SERVER_FONT_ACK.substring(
                            0,
                            Responses.S_SERVER_FONT_ACK.lastIndexOf(":")))) {
                        return Responses.ResponseType.SERVER_FONT_ACK;
                    }
        }
        return Responses.ResponseType.SERVER_UNDEFINED;
    }


    /**
     * Gets remaining image count from message.
     *
     * DOES NOT CHECK IF THE MESSAGE IS VALID, ONLY SEARCHES FOR 'IMAGES LEFT: %d'!
     *
     * @param msg the received command
     * @return the number of images left, according to the command
     */
    public static int getRemainingFromMessage(String msg) {
        if(msg.contains(Responses.NO_IMAGE_LEFT)) return 0;

        String countString = msg.substring(
                msg.lastIndexOf(Responses.IMAGES_LEFT) + Responses.IMAGES_LEFT.length(),
                msg.length());

        return Integer.parseInt(countString);
    }
}
