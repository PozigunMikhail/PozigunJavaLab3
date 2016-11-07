package invidx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDataSource extends AbstractDataSource {
    private String fileName;

    public FileDataSource(String fName) {
        fileName = fName;
    }

    public String getId() {
        return fileName;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(fileName);
    }
}
