package fi.tampere.filedl;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class WFSAttachmentFile extends WFSAttachment implements Closeable {

    private final InputStream file;
    private final long size;

    public WFSAttachmentFile(InputStream file, long size) {
        this.file = file;
        this.size = size;
    }

    public InputStream getFile() {
        return file;
    }

    public long getSize() {
        return size;
    }

    @Override
    public void close() throws IOException {
        if (file != null) {
            file.close();
        }
    }
}
