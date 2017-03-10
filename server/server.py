#!/usr/bin/env python
from http.server import HTTPServer
from http.server import BaseHTTPRequestHandler
import cgi

PORT = 8003
FILE_PREFIX = "."

class JSONRequestHandler (BaseHTTPRequestHandler):

    def do_GET(self):

        #send response code:
        self.send_response(200)
        #send headers:
        self.send_header("Content-type", "application/json")
        # send a blank line to end headers:
        self.wfile.write("\r\n")

        try:
            output = open(FILE_PREFIX + "/" + self.path[1:] + ".json", 'r').read()
        except Exception:
            output = "{'error': 'Could not find file " + self.path[1:] + ".json'" + "}"
        self.wfile.write(output)

    def do_POST(self):
        if self.path == "/success":
            response_code = 200
        elif self.path == "/error":
            response_code = 500
        else:
            try:
                response_code = int(self.path[1:])
            except Exception:
                response_code = 201

        try:
            self.send_response(response_code)
            self.wfile.write('Content-Type: application/json\r\n')
            self.wfile.write('Client: %s\r\n' % str(self.client_address))
            self.wfile.write('User-agent: %s\r\n' % str(self.headers['user-agent']))
            self.wfile.write('Path: %s\r\n' % self.path)

            self.end_headers()


            form = cgi.FieldStorage(
                    fp=self.rfile,
                    headers=self.headers,
                    environ={'REQUEST_METHOD':'POST',
                                     'CONTENT_TYPE':self.headers['Content-Type'],
                                     })

            self.wfile.write('{\n')
            first_key=True
            for field in form.keys():
                    if not first_key:
                        self.wfile.write(',\n')
                    else:
                        self.wfile.write('\n')
                        first_key=False
                    self.wfile.write('"%s":"%s"' % (field, form[field].value))
            self.wfile.write('\n}')

        except Exception as e:
            self.send_response(500)


server = HTTPServer(("localhost", PORT), JSONRequestHandler)
server.serve_forever()