Strava-Vaadin
=============

Pre-requisites
==============
To run the application you will need to have:-

0. A Strava account
0. An application configured within your Strava account - you can do that [here](https://www.strava.com/settings/api)
0. A Vaadin charts developer license details [here](https://vaadin.com/directory#addon/vaadin-charts)
0. A MapBox access token, details [here](https://www.mapbox.com/help/create-api-access-token/)

Instructions
============
The application takes 4 parameters as follows:-

* -Dstrava.clientid - the client id of your Starav app
* -Dstrava.secret - the secret from your Strava app
* -Dvaadin.charts.developer.license - Vaadin charts developer key
* -Dmap.key - mabox access token

To run the application, use the following command

mvn clean jetty:run -Dstrava.clientid=<clientid> -Dstrava.secret=<secret> -Dvaadin.charts.developer.license=<charts_license> -Dmap.key=<map_key>