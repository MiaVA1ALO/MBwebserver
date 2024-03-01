This script can be used as a simple HTTP web server, where files are placed in a directory ([classpath]/site/) to be downloaded by a client.
It uses concurrent threads to allow multiple people to download files at once.

Requires significant amounts of memory, sometimes I get a OutOfMemory exception on my laptop with 6 GB RAM.

Requires a port (default is 80, recommended) to be opened in your router settings. Simply forward the router's port 80 to your local machine's port 80.
