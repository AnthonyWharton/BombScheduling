#!/usr/bin/env python3
from http.server import HTTPServer
from http.server import BaseHTTPRequestHandler
import cgi
import integrations
import sys
import inspect
import pkgutil
from os import listdir, path
from os.path import isfile, join
from integrations import test_integration

path = "../apis"
onlyfiles = [f[:-3] for f in listdir(path) if isfile(join(path, f))]
modules = []
classes = []
# for file in onlyfiles:
#     modules.append(path + "." + file)
# for module in modules:
#     __import__(module)
#     print(module)
#     print(inspect.getmodulename(module))
#     for name, obj in inspect.getmembers(module):
#         if inspect.isclass(obj) and not name[:2] == "__":
#             print(name[:2])
#             print(name)
#             classes.append(obj)
# print(classes)

for loader, module_name, is_pkg in pkgutil.iter_modules([path]):
        # Load examples
        modules = loader.find_module(module_name).load_module(module_name)
        print ("MODULE", modules)
        print (inspect.getmembers(modules, predicate=inspect.isclass))
        classes += [ func[1] for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1]

print("FINISHED LOADING MODULES")

print (classes)





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