package invidx;

import java.io.IOException;
import java.io.InputStream;

public interface DataSource extends Comparable<DataSource> {
    String getId();

    InputStream getInputStream() throws IOException;

    default int compareTo(DataSource anDataSource) {
        return getId().compareTo(anDataSource.getId());
    }
}
