package invidx;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StringDataSource extends AbstractDataSource {
    private String strData;

    public StringDataSource(String str) {
        strData = str;
    }

    public String getId() {
        return strData;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(strData.getBytes());
    }
}
