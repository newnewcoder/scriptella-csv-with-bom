package com.github.newnewcoder;

import scriptella.driver.csv.CsvConnectionParameters;
import scriptella.spi.ConnectionParameters;

public class CsvWithBomConnectionParameters extends CsvConnectionParameters {
    public static final boolean DEFAULT_BOM = false;
    protected boolean bom;

    public CsvWithBomConnectionParameters() {
        super();
        bom = DEFAULT_BOM;
    }

    public CsvWithBomConnectionParameters(ConnectionParameters parameters) {
        super(parameters);
        bom = parameters.getBooleanProperty(CsvWithBomConnection.BOM, DEFAULT_BOM);
    }

    public boolean isBom() {
        return bom;
    }

    public void setBom(boolean bom) {
        this.bom = bom;
    }
}
