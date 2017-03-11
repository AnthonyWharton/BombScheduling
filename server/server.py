#!/usr/bin/env python3
from http.server import HTTPServer
from http.server import BaseHTTPRequestHandler
import cgi
import sys
import inspect
import pkgutil
from os import listdir, path
from os.path import isfile, join
import json
from collections import namedtuple

class message():
    def __init__(self):
        pass
    def create(self):
        self.message_title = ""
        self.message_body = ""


def toJSON(obj):
    return json.dumps(obj.__dict__)

def fromJSON(obj, cl):
    p = cl()
    p.__dict__ = json.loads(obj)
    return p

class integration:
    def __init__(self, k, f):
        self.data = k
        self.function = f
    def __repr__(self):
        return "data: " + str(self.data) + "\n" + "function: " + str(self.function)
    def setJsonSize(self, i):
        self.jsonSize = i

path = "../apis"
onlyfiles = [f[:-3] for f in listdir(path) if isfile(join(path, f))]
integrations = []

for loader, module_name, is_pkg in pkgutil.iter_modules([path]):
        modules = loader.find_module(module_name).load_module(module_name)
        print ("MODULE", modules)
        print (inspect.getmembers(modules, predicate=inspect.isclass))
        print (inspect.getmembers(modules, predicate=inspect.isfunction))
        data = [ func[1] for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1][0]
        print([ func[1] for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1])
        function = [ func[1] for func in inspect.getmembers(modules, predicate=inspect.isfunction) if func[0].startswith('_') is False ][::-1][0]
        integrations.append(integration(data, function)) 

print("FINISHED LOADING MODULES")

print (integrations)

bigjs = {}

for integration in integrations:
    x = integration.data()
    x.create()
    jx = toJSON(x)
    print(jx)
    print(jx.count(',') + 1)
    js = json.loads(jx)
    bigjs = {**bigjs, **js}
    integration.setJsonSize(jx.count(',') + 1)

msg = message()
msg.create()
js = json.loads(toJSON(msg))

bigjs = {**bigjs, **js}

bigjs

print (bigjs)
