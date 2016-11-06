package invidx;

import java.io.IOException;
import java.io.InputStream;

public interface DataSource {
    String getId();

    InputStream getInputStream() throws IOException;
}
