package inv_idx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileDataSource implements DataSource {
    private String fileName;

    public FileDataSource(String fName) {
        fileName = fName;
    }

    public String getID() {
        return fileName;
    }

    public InputStream getInputStream() throws IOException {
        return new FileInputStream(fileName);
    }
}
