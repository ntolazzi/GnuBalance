# Server

The server is based on [Flask](http://flask.pocoo.org/) and trivial to deploy. I personally use
[Gunicorn](http://gunicorn.org/) as a WSGI server but you could also use an Apache module or other standalone
WSGI servers.

In gunicorn you start the server with the corresponding web application via

gunicorn webapp:app
