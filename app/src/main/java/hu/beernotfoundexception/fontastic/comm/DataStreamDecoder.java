package hu.beernotfoundexception.fontastic.comm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import hu.beernotfoundexception.fontastic.domain.Constants;
import hu.beernotfoundexception.fontastic.util.HexByteUtil;

public class DataStreamDecoder {
    public static final String START_MARK = Constants.FILE_START;
    public static final String END_MARK = Constants.FILE_END;

    public static boolean isValidData(byte data) {
        byte[] validBytes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 'a', 'b', 'c', 'd', 'e', 'f'};
        for (byte b :
                validBytes) {
            if (b == data) return true;
        }
        return false;
    }

    public static ByteArrayOutputStream parse(byte[] b) throws IOException {
        byte[] to = new byte[b.length - START_MARK.length() - END_MARK.length()];
        System.arraycopy(b, START_MARK.length(), to, 0, to.length);
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        byteOutputStream.write(to);
        return byteOutputStream;
    }

    public static byte[] trimToData(String b) {
//        byte[] to = new byte[b.length - START_MARK.length() - END_MARK.length()];
//        System.arraycopy(b, START_MARK.length(), to, 0, to.length);
        return HexByteUtil.hexStringToByteArray(b.substring(START_MARK.length(), b.length() - END_MARK.length()));
    }
}
