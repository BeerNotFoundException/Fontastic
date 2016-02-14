package hu.beernotfoundexception.fontastic.domain.repository;

import static hu.beernotfoundexception.fontastic.domain.repository.Responses.IMAGES_LEFT;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.NO_IMAGE_LEFT;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_FONT_ACK;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_FONT_ACK_EOC;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_HELLO;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_ID_ACK;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_INVALID;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_SEND_ID;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.ResponseType.SERVER_UNDEFINED;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.S_SERVER_FONT_ACK;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.S_SERVER_FONT_ACK_EOC;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.S_SERVER_HELLO;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.S_SERVER_ID_ACK;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.S_SERVER_INVALID;
import static hu.beernotfoundexception.fontastic.domain.repository.Responses.S_SERVER_SEND_ID;

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
    public static ResponseType getResponseType(String msg) {

        if (msg.contains(S_SERVER_HELLO))
            return SERVER_HELLO;
        if (msg.contains(S_SERVER_SEND_ID))
            return SERVER_SEND_ID;
        if (msg.contains(S_SERVER_ID_ACK)) {
            return SERVER_ID_ACK;
        }
        if (msg.contains(S_SERVER_FONT_ACK)) {
            if (msg.contains(S_SERVER_FONT_ACK_EOC)) {
                return SERVER_FONT_ACK_EOC;
            } else
                return SERVER_FONT_ACK;
        }
        if (msg.contains(S_SERVER_INVALID)) {
            return SERVER_INVALID;
        }
        return SERVER_UNDEFINED;
    }


    /**
     * Gets remaining image count from message.
     * <p/>
     * DOES NOT CHECK IF THE MESSAGE IS VALID, ONLY SEARCHES FOR 'IMAGES LEFT) %d'!
     *
     * @param msg the received command
     * @return the number of images left, according to the command
     */
    public static int getRemainingFromMessage(String msg) {
        if (msg.contains(NO_IMAGE_LEFT)) return 0;

        String countString = msg.substring(
                msg.lastIndexOf(IMAGES_LEFT) + IMAGES_LEFT.length(),
                msg.length());

        return Integer.parseInt(countString);
    }
}
