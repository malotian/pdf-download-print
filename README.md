#Overview
This is a small tool meant to overcome usability issues when printing PDFs from a browser meant for different printers and configurations.

Currently there is now way to select a printer programmatic via JavaScript.

In order to print without print dialog interaction this tool resides in the tray and
 
- periodically polls predefined URLs 
- responds locally to triggers from the browser   

####Use Cases

- print to different predefined printer settings (e.g. Tray 1, Tray 2, etc.) without having to change the 'Default' printer
- poor man's location independent ePrinting solution when providing an URI endpoint with some simple logic to only provide new documents

Configuration is meant to be done by knowledgeable staff for the time being;-)

#Build

`mvn package`

#Run

`java -jar <package-name>.jar`

#Configuration
`application.properties`

Must be available and holds the configuration necessary for trigger requests

<sup>see `application.properties.sample` for details<sup>

`download.properties`

<sup>see `download.properties.sample` for details<sup>

#Examples

Single parameter

`curl http://localhost:8088/trigger?print=http://www.example.com/shopArticle/printReceipt?receiptId=666`

Multiple parameters URL encoded

`curl http://localhost:8088/trigger?print=http%3A%2F%2Fwww.example.com%2Fcontext%2Fcontroller%2Faction%3Fparameter1%3D1%26parameter2%3Dscorecard%26parameter3%3D64`

#License
Keeping it simple ...

**MIT**


