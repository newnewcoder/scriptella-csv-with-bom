package com.github.newnewcoder;

import org.apache.commons.io.ByteOrderMark;
import scriptella.configuration.StringResource;
import scriptella.spi.ConnectionParameters;
import scriptella.spi.ParametersCallback;
import scriptella.spi.QueryCallback;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

// Reference from https://github.com/scriptella/scriptella-etl/blob/master/drivers/src/test/scriptella/driver/csv/CsvConnectionTest.java
public class CsvWithBomConnectionTest extends AbstractTestCase {
    private int rows;
    private ByteArrayOutputStream out;
    private String testCsvInput;

    protected void setUp() throws Exception {
        super.setUp();
        testCsvInput = "c1,c2,c3\r\nc4,c5,c6";
        testURLHandler = new TestURLHandler() {
            public InputStream getInputStream(final URL u) {
                try {
                    byte[] testInput = testCsvInput.getBytes("UTF8");
                    byte[] testInputWithBom = prependBom(testInput, ByteOrderMark.UTF_8.getBytes());
                    return new ByteArrayInputStream(testInputWithBom);
                } catch (UnsupportedEncodingException e) {
                    throw new IllegalStateException(e.getMessage(), e);
                }
            }

            public OutputStream getOutputStream(final URL u) {
                out = new ByteArrayOutputStream();
                return out;
            }

            public int getContentLength(final URL u) {
                return -1;
            }
        };
    }

    public void testQuery() {
        Map<String, String> props = new HashMap<>();
        props.put(CsvWithBomConnection.ENCODING, "UTF8");
        props.put(CsvWithBomConnection.EOL, "\r\n");
        props.put(CsvWithBomConnection.HEADERS, "false");
        props.put(CsvWithBomConnection.SEPARATOR, ",");
        props.put(CsvWithBomConnection.BOM, "true");
        ConnectionParameters cp = new MockConnectionParameters(props, "tst://file/dummy.csv");

        CsvWithBomConnection con = new CsvWithBomConnection(cp);
        rows = 0;
        con.executeQuery(new StringResource(""), MockParametersCallbacks.UNSUPPORTED, new QueryCallback() {
            public void processRow(final ParametersCallback parameters) {
                rows++;
                switch (rows) {
                    case 1:
                        assertEquals("c1", parameters.getParameter("1"));
                        assertEquals("c2", parameters.getParameter("2"));
                        assertEquals("c3", parameters.getParameter("3"));
                        break;
                    case 2:
                        assertEquals("c4", parameters.getParameter("1"));
                        assertEquals("c5", parameters.getParameter("2"));
                        assertEquals("c6", parameters.getParameter("3"));
                        break;
                    default:
                        fail("unexpected row " + rows);
                }

            }
        });
        assertEquals(2, rows);
        assertNull("No output should be produced by a query", out);

    }

    public void testScript() throws UnsupportedEncodingException {
        Map<String, String> props = new HashMap<>();
        props.put(CsvWithBomConnection.ENCODING, "UTF8");
        props.put(CsvWithBomConnection.EOL, "\r\n");
        props.put(CsvWithBomConnection.SEPARATOR, ",");
        props.put(CsvWithBomConnection.BOM, "true");
        props.put(CsvWithBomConnection.QUOTE, "");
        ConnectionParameters cp = new MockConnectionParameters(props, "tst://file/dummy.csv");

        CsvWithBomConnection con = new CsvWithBomConnection(cp);
        rows = 0;
        con.executeScript(new StringResource("c1,c2,c3\r\nc4,c5,c6"), MockParametersCallbacks.SIMPLE);
        con.close();
        byte[] expected = prependBom("c1,c2,c3\r\nc4,c5,c6\r\n".getBytes("UTF8"), ByteOrderMark.UTF_8.getBytes());
        byte[] actual = out.toByteArray();
        assertArrayEquals(expected, actual);
    }

    private byte[] prependBom(byte[] content, byte[] bom) {
        byte[] testInputWithBom = new byte[content.length + bom.length];
        System.arraycopy(bom, 0, testInputWithBom, 0, bom.length);
        System.arraycopy(content, 0, testInputWithBom, bom.length, content.length);
        return testInputWithBom;
    }
}
