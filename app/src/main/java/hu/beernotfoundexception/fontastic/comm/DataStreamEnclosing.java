package hu.beernotfoundexception.fontastic.comm;

import java.io.ByteArrayOutputStream;

import hu.beernotfoundexception.fontastic.util.Logger;

public class DataStreamEnclosing {
    public final String startMark, endMark;
    public int startCursor, endCursor;
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    String endCache = null;

    public DataStreamEnclosing(String startMark, String endMark) {
        this.startMark = startMark;
        this.endMark = endMark;
        startCursor = 0;
        endCursor = 0;
    }

    public static boolean isValidData(byte data) {
        byte[] validBytes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 'A', 'B', 'C', 'D', 'E', 'F'};
        for (byte b :
                validBytes) {
            if (b == data) return true;
        }
        return false;
    }

    public boolean isDataStarted(byte data) {
        if (startCursor >= startMark.length()) return true;
        if (startMark.getBytes()[startCursor] == data) {
            if (startCursor >= startMark.length()) return true;
            startCursor++;
        } else {
            Logger.d(TcpCommunicationClient.TAG, "The data start string was interrupted, or isDataStarted not called at the beginning");
            startCursor = 0;
        }
        return false;
    }

    public boolean isDataFinished(byte data) {
//        if (endCursor >= endMark.length()) return true;
//        if (endMark.getBytes()[endCursor] == data) {
//            if (endCursor >= endMark.length()) return true;
//            endCursor++;
//        } else {
//            endCursor = 0;
//        }
//        return false;
        return isLastByteInvalid;
    }

    private boolean isLastByteInvalid = false;

    public void addByte(byte b) {
        if (isDataStarted(b)) {
            if (isValidData(b))
                byteOutputStream.write(b);
            else
                isLastByteInvalid = true;
        }
    }

    public ByteArrayOutputStream getByteOutputStream() {
        if(isLastByteInvalid) throw new IllegalStateException("The output is not ready yet!");
        return byteOutputStream;
    }
}
