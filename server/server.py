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
from SimpleWebSocketServer import SimpleWebSocketServer, WebSocket
import time
import threading

users = []
bombs = []

class message():
    def __init__(self):
        pass
    def create(self):
        self.message_title = ""
        self.message_body = ""

class user():
    def __init__(self, id, client, opts):
        self.id = id
        self.client = client
        self.opts = opts

class bomb():
    def __init__(self, time, user):
        self.time = time
        self.user = user
     
    def check(self):
        if time.time() > self.time:
            self.dispatch()

    def dispatch(self):
        keys = json.dumps(self.user.opts[1:-1].split(","))

        parsepoint = 0
        datalist = []

        for integration in integrations:
            i = integration.jsonSize
            l = keys[parsepoint:parsepoint + i]
            parsepoint += i
            j = "{"
            for elem in l:
                j += elem.lstrip()
                j += ","
            j = j[:-1]
            j += "}"
            print(j)
            datalist.append(fromJSON(j, integration.data))

        l = keys[parsepoint:parsepoint + 2]
        parsepoint += 2
        j = "{"
        for elem in l:
            j += elem.lstrip()
            j += ","
        j = j[:-1]
        j += "}"
        print(j)
        msg = fromJSON(j, message)

        for i in range(len(integrations)):
            print(datalist[i])
            print(msg)
            integrations[i].function(datalist[i], msg)
        bombs.remove(self)

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
        datas = [ func for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1]
        for d in datas:
            if "message" in d[0].lower():
                data = d[1]
        print([ func for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1])
        functions = [ func for func in inspect.getmembers(modules, predicate=inspect.isfunction) if func[0].startswith('_') is False ][::-1]
        print([ func for func in inspect.getmembers(modules, predicate=inspect.isfunction) if func[0].startswith('_') is False ][::-1])
        for f in functions:
            if "send" in f[0].lower():
                function = f[1]
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

class SimpleEcho(WebSocket):

    def handleMessage(self):
        if self.opcode == 1:
            print(self.data)
            op = self.data[:3]
            data = self.data[3:]
            if op == "REQ": 
                print("REQ request recieved from " + self.address)
                self.sendMessage(bigjs)
            if op == "USR":
                print("USR request recieved from " + self.address)
                data = json.loads(data)
                users.append[user(users.len, self, data) ]
                self.sendMessage(str(users.len - 1))
            if op == "BMB":
                print("BMB request recieved from " + self.address)
                data = json.loads(data)
                time = data["time"]
                id = data["id"]
                bombs.append(bomb(time, users[id]))
        # echo message back to client
        self.sendMessage("")

    def handleConnected(self):
        print(self.address, 'connected')

    def handleClose(self):
        print(self.address, 'closed')

server = SimpleWebSocketServer('', 40111, SimpleEcho)

def doServer():
    print("serving")
    server.serveforever()

def doClock():
    print("clocking")
    while(True):
        for bomb in bombs:
            bomb.check() 

server_thread = threading.Thread(target=doServer)
server_thread.daemon = True
server_thread.start()


doClock()
