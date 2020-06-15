import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TorReader {

    private InputStreamReader in;

    public TorReader(InputStream in) {
        this.in = new InputStreamReader(in);
    }

    public String read() throws IOException {
        int[] buf = new int[100];
        int counter = 0;
        int c;
        while ((c = in.read()) != '\r') {
            buf[counter++] = c;
        }
        while ((c = in.read()) != '\n') ;
        return new String(buf, 0, counter);
    }
}