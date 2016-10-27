package inv_idx;

import java.io.IOException;
import java.io.InputStream;

public interface DataSource {
    String getID();

    InputStream getInputStream() throws IOException;
}
