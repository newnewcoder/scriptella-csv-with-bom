# [Scriptella](http://scriptella.org) extended csv driver for BOM handling

This project provides an extended csv driver for BOM handling.

By setting `bom=true`, BOM will be skipped in csv reading, while be auto-prepended in csv writing.


## Usage

1. Clone source code

   ~~~bash
   git clone https://github.com/newnewcoder/scriptella-csv-with-bom.git
   ~~~

2. Package jar and add to classpath
   
   ~~~bash
   cd scriptella-csv-with-bom
   ./gradlew jar
   # then add it to classpath
   ~~~

3. Use in etl xml like below.
    
   a. Modify `driver="csv"` to `driver="com.github.newnewcoder.CsvWithBomDriver"`.
   
   b. Add `bom=true` to csv connection config.

    ~~~xml
   <!DOCTYPE etl SYSTEM "http://scriptella.javaforge.com/dtd/etl.dtd">
   <etl>
       <connection id="out" driver="com.github.newnewcoder.CsvWithBomDriver" url="/your/csv/file.csv">
           encoding=UTF8
           eol=\r\n
           bom=true
       </connection>
   ...
    ~~~
