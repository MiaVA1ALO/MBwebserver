This script can be used as a simple HTTP web server, where files are placed in a directory ([classpath]/site/) to be downloaded by a client.
It uses concurrent threads to allow multiple people to download files at once.

Requires a port (default is 80, recommended) to be opened in your router settings. Simply forward the router's port 80 to your local machine's port 80.

The example site directory is a website my friend made as a joke when I first wrote this script (and it was still called MiaServer). The
upload feature is not implemented anymore, but users used to be able to upload files to be saved on the server (giving each user their own 
webpage!) the file upload feature was also modified to allow a comment section (I don't think the HTML file for that is even in there 
anymore...)

I decided not to have this compiled as a .class file, I personally prefer to use it in an IDE.

Note: sometimes I get an out of memory exception on my laptop with 6 GB RAM.
