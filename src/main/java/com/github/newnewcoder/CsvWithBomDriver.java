package com.github.newnewcoder;

import scriptella.driver.csv.Driver;
import scriptella.spi.Connection;
import scriptella.spi.ConnectionParameters;

public class CsvWithBomDriver extends Driver {
    public Connection connect(ConnectionParameters connectionParameters) {
        return new CsvWithBomConnection(connectionParameters);
    }
}
