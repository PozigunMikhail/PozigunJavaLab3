package inv_idx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringDataSource implements DataSource {
    private String strData;

    public StringDataSource(String str) {
        strData = str;
    }

    public String getID() {
        return strData;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(strData.getBytes());
    }
}
