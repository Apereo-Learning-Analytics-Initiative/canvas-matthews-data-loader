# canvas-matthews-data-loader

### How to find your Matthews API Key and Secret
The Matthews admin user interface is under development so you'll have to find your API key and secret by directly accessing your MongoDB instance. Follow the commands below to find your key and secret. The commands assume that you are able to access MongoDB via the command line and that you are using the default database name (if not, you would have changed this manually in Matthews).

``````````````````
> mongo
> use test
> db.mongoOrg.find().pretty()

{
  "_id" : ObjectId("objid"),
  "_class" : "unicon.matthews.oneroster.service.repository.MongoOrg",
  "apiKey" : "abcdef",
  "apiSecret" : "123456",
  "tenantId" : "583ce4076f03bb1f88bee0ea",
  "org" : {
    "sourcedId" : "1f03f835-d992-4301-8e5c-5ad55e6489f5",
    "status" : "active",
    "metadata" : {
      "https://matthews/tenant" : "583ce4076f03bb1f88bee0ea"
    },
    "dateLastModified" : ISODate("2016-11-29T02:12:23.757Z"),
    "name" : "DEFAULT_ORG",
    "type" : "other"
  }
}
``````````````````

Find the values apiKey and apiSecret, those are the values you'll need to use to create a session with Matthews. In the example above the key is abcdef and the secret is 123456

### Running the canvas data loader
`````
mvn clean package spring-boot:run -Dmatthews.apikey=<your matthews api key> 
  -Dmatthews.apisecret=<your matthews api secret> -Dcanvas.apisecret=<your canvas api secret> 
  -Dcanvas.apikey=<your canvas api key>
`````

Note if you are not running Matthews on localhost with the default port (9966) you will also need to pass:

`````
-Dmatthews.baseurl=<your matthews base url> // e.g., -Dmatthews.baseurl=https://lrw.cloudlrs.com
`````

### Lombok
This project uses [Project Lombok](https://projectlombok.org) for data entities.
Maven builds without issue.
If you use an IDE, you will want to [install a Lombbok plugin (or enable support)](https://projectlombok.org/download.html)

