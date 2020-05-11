package com.github.newnewcoder;

import org.apache.commons.io.ByteOrderMark;
import org.apache.commons.io.input.BOMInputStream;
import scriptella.driver.csv.CsvConnection;
import scriptella.driver.text.ConsoleAdapters;
import scriptella.spi.ConnectionParameters;
import scriptella.util.IOUtils;
import scriptella.util.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class CsvWithBomConnection extends CsvConnection {

    public static final String BOM = "bom";

    private CsvWithBomConnectionParameters params;

    private final static Map<String, byte[]> bomByEncoding = new HashMap<String, byte[]>() {{
        put("UTF8", ByteOrderMark.UTF_8.getBytes());
        put("UTF16BE", ByteOrderMark.UTF_16BE.getBytes());
        put("UTF16LE", ByteOrderMark.UTF_16LE.getBytes());
        put("UTF32BE", ByteOrderMark.UTF_32BE.getBytes());
        put("UTF32LE", ByteOrderMark.UTF_32LE.getBytes());
    }};

    public CsvWithBomConnection(ConnectionParameters parameters) {
        super(parameters);
        this.params = new CsvWithBomConnectionParameters(parameters);
    }

    @Override
    protected Writer newOutputWriter() throws IOException {
        final URL url = super.getConnectionParameters().getUrl();
        final String encoding = params.getEncoding();
        Writer writer = (url == null) ? ConsoleAdapters.getConsoleWriter(encoding) : IOUtils.getWriter(getOutputStream(url), encoding);
        return writer;
    }

    @Override
    protected Reader newInputReader() throws IOException {
        final URL url = super.getConnectionParameters().getUrl();
        final String encoding = params.getEncoding();
        return url == null ? ConsoleAdapters.getConsoleReader(encoding) : IOUtils.getReader(getInputStream(url), encoding);
    }

    private InputStream getInputStream(final URL url) throws IOException {
        InputStream in = url.openStream();
        if (params.isBom()) {
            BOMInputStream bomIn = new BOMInputStream(in, ByteOrderMark.UTF_8,
                    ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_16BE,
                    ByteOrderMark.UTF_32LE, ByteOrderMark.UTF_32BE
            );
            return bomIn;
        }
        return in;
    }

    private OutputStream getOutputStream(final URL url) throws IOException {
        OutputStream os;
        if ("file".equals(url.getProtocol())) {
            os = new FileOutputStream(url.getFile());
        } else {
            os = IOUtils.getOutputStream(url);
        }
        if (params.isBom()) {
            String enc = getEncoding();
            if (bomByEncoding.containsKey(enc)) {
                os.write(bomByEncoding.get(enc));
                LOG.info("Write " + enc + " BOM to " + url.getFile());
            } else {
                LOG.warning("Write " + enc + " BOM not supported.");
            }
        }
        return os;
    }

    private String getEncoding() {
        String enc = Charset.defaultCharset().displayName();
        if (!StringUtils.isEmpty(params.getEncoding())) {
            enc = params.getEncoding();
        }
        return enc.replace("-", "").toUpperCase();
    }
}
