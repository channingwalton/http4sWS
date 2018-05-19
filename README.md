# http4sws #

Welcome to http4sws!

This is as a simple example of working with Websockets and http4s, and also
an example rest api and store using doobie.

## REST Api
The api offers a simple document store.

You can test this with curl (assuming a document called _picture.jpg_:

    curl -X POST -F 'doc=@picture.jpg' -H "Content-Type: image/jpeg" http://127.0.0.1:8080/document/1
    curl http://127.0.0.1:8080/document/1 > downloaded.jpg

## Websockets
The easiest way to play is to run http4sws.Http4sWS and use Chrome's Simple Websocket Client using the
url ws://localhost:8080/ws, and click _Open_. Type anything into the request field, the server will print it.
The message log will display the current time every second.

## Contribution policy ##

Contributions via GitHub pull requests are gladly accepted from their original author. Along with
any pull requests, please state that the contribution is your original work and that you license
the work to the project under the project's open source license. Whether or not you state this
explicitly, by submitting any copyrighted material via pull request, email, or other means you
agree to license the material under the project's open source license and warrant that you have the
legal authority to do so.

## License ##

This code is open source software licensed under the
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0) license.
