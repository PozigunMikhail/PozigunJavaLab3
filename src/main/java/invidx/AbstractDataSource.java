package invidx;

public abstract class AbstractDataSource implements DataSource {
    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof AbstractDataSource) {
            AbstractDataSource anDataSource = (AbstractDataSource) anObject;
            return getId().equals(anDataSource.getId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
