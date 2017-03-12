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
import pickle
from random import randint

class message():
    def __init__(self, message, title):
        self.message_title = title
        self.message_body = message

class user():
    def __init__(self, id, opts):
        self.id = id
        self.opts = opts

class bomb():
    def __init__(self, time, uid, msg, bid):
        self.time = time
        self.uid = uid
        self.msg = msg
        self.bid  = bid
     
    def check(self):
        if time.time() > self.time:
            print("Bomb due for " + str(self.uid))
            self.dispatch()

    def dispatch(self):
        datalist = users[self.uid].opts
        print(userstosessions)
        try:
            for session in userstosessions[self.uid]:
                session.sendMessage("ALR" + self.msg.message_body)
                print("notifying " + session.address[0])
        except KeyError:
            print("Can't find any users online")

        print("about to start integrating")
        for i in range(len(integrations)):
            if not "" in list(datalist[i].__dict__.values()):
                print(datalist[i])
                print(self.msg)
                if self.msg.message_body == "":
                    self.msg.message_body = "Default Message body"
                if self.msg.message_title == "":
                    self.msg.message_title = "Incoming Bomb schedule"
                integrations[i].function(datalist[i], self.msg)
        done_bombs.append(self.bid)

def turn_json_into_classes(jsonstring):
    keys = jsonstring[1:-1].split(",")

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
    return datalist

def turn_classes_into_json(classes):
    retjson = "{"
    for c in classes:
        jx = toJSON(c)
        retjson += jx[1:-1]
        retjson += ","
        integration.setJsonSize(jx.count(',') + 1)

    retjson = retjson[:-1]
    retjson += "}"
    print(retjson)
    return retjson


def toJSON(obj):
    return json.dumps(obj.__dict__)

def fromJSON(obj, cl):
    p = cl()
    p.__dict__ = json.loads(obj)
    return p

class integration:
    def __init__(self, k, f, v, n):
        self.data = k
        self.function = f
        self.verify = v
        self.name = n
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
        datas = [func for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1]
        for d in datas:
            if "message" in d[0].lower():
                data = d[1]
        print([ func for func in inspect.getmembers(modules, predicate=inspect.isclass) if func[0].startswith('_') is False ][::-1])
        functions = [ func for func in inspect.getmembers(modules, predicate=inspect.isfunction) if func[0].startswith('_') is False ][::-1]
        print([ func for func in inspect.getmembers(modules, predicate=inspect.isfunction) if func[0].startswith('_') is False ][::-1])
        for f in functions:
            if "send" in f[0].lower():
                function = f[1]
            if "verify" in f[0].lower():
                verify = f[1]
        integrations.append(integration(data, function, verify, module_name.replace("message", "")))

print("FINISHED LOADING MODULES")

try:
    userfile = open("users.encrypted", "rb")
    users = pickle.load(userfile)
    userfile.close()
except (FileNotFoundError, EOFError):
    print("Regenerating users")
    users = {}
try:
    bombfile = open("bombs.encrypted", "rb")
    bombs = pickle.load(bombfile)
    bombfile.close()
except (FileNotFoundError, EOFError):
    print("Regenerating bombs")
    bombs = {}

print (users)
print (bombs)

integrations = sorted(integrations, key=lambda x:x.data.__name__)

print (integrations)

bigjs = "{"

for integration in integrations:
    x = integration.data()
    x.create()
    jx = toJSON(x)
    print(jx)
    print(jx.count(',') + 1)
    bigjs += jx[1:-1]
    bigjs += ","
    integration.setJsonSize(jx.count(',') + 1)

bigjs = bigjs[:-1]
bigjs += "}"
print(bigjs)

userstosessions = {}
sessionstousers = {}
done_bombs = []
new_bombs  = []

class SimpleEcho(WebSocket):

    def handleMessage(self):
        if self.opcode == 1:
            print(self.data)
            op = self.data[:3]
            data = self.data[3:]
            if op == "REQ": 
                print("REQ request recieved from " + str(self.address[0]))
                self.sendMessage(op + bigjs)
            elif op == "USR":
                print("USR request recieved from " + str(self.address[0]))
                # data = json.loads(data)
                classes = turn_json_into_classes(data)
                print(classes)
                for i in range(len(integrations)):
                    if not integrations[i].verify(classes[i]):
                        print("Invalid " + integrations[i].name)
                        self.sendMessage(op + "Invalid " + integrations[i].name)
                        return 
                uid = randint(0, 10000000)
                while(uid in list(users.keys())):
                    uid = randint(0, 10000000)
                users[uid] = user(uid, classes)
                self.sendMessage(op + str(uid))
            elif op == "BMB":
                print("BMB request recieved from " + str(self.address[0]))
                data = json.loads(data)
                print(data)
                bid = randint(0, 10000000)
                while(bid in list(bombs.keys())):
                    print("BID " + str(bid) + " Already taken")
                    bid = randint(0, 10000000)
                title = data["title"]
                body = data["message"]
                time = data["time"]
                uid = data["uid"]
                if body == "":
                    print("No message")
                    print("scheduling failure")
                    self.sendMessage(op + "FNot enough text")
                else:
                    msg = message(body, title)
                    if uid not in list(users.keys()):
                        self.sendMessage(op + "FWho's that?")
                    else:
                        new_bombs.append((bid, bomb(time, uid, msg, bid)))
                        print("Scheduling Success")
                        self.sendMessage(op + "STic Toc.. Bomb set!")
            elif op == "LGN":
                print("LGN request recieved from " + str(self.address[0]))
                print("Logged in user " + str(data))
                try:
                    userstosessions[int(data)].append(self)
                    print(userstosessions[int(data)])
                except KeyError:
                    userstosessions[int(data)] = [self]
                sessionstousers[self] = int(data)
            elif op == "LST":
                print("LST request recieved from " + str(self.address[0]))
                data = int(data)
                user_bombs = [x for x in list(bombs.values()) if x.uid == data]
                message_json = {}
                for b in user_bombs:
                    bomb_data = {}
                    bomb_data["title"] = b.msg.message_title
                    bomb_data["body"] = b.msg.message_body
                    bomb_data["time"] = b.time
                    message_json[b.bid] = bomb_data
                print(message_json)
                self.sendMessage(op + json.dumps(message_json))
            elif op == "DEL":
                print("DEL request recieved from " + str(self.address[0]))
                print(bombs)
                try:
                    done_bombs.append(data)
                    self.sendMessage(op + "Success")
                except KeyError:
                    self.sendMessage(op + "Failure")
            elif op == "INF":
                print("INF request recieved from " + str(self.address[0]))
                data = int(data)
                inf = turn_classes_into_json(users[data].opts)
                self.sendMessage(op + inf)
            elif op == "PNG":
                print("PNG request recieved from " + str(self.address[0]))
                self.sendMessage(op + "Pong")

    def handleConnected(self):
        print(self.address, 'connected')

    def handleClose(self):
        print(self.address, 'closed')
        try:
            uid = sessionstousers[self]
            userstosessions[uid].remove(self)
            if len(userstosessions[uid]) == 0:
                del userstosessions[uid]
            del sessionstousers[self]
            print("Logged out user " + str(uid))
        except KeyError:
            pass

server = SimpleWebSocketServer('', 40111, SimpleEcho)

def doServer():
    print("serving")
    server.serveforever()

def doClock():
    print("clocking")
    while(True):
        time.sleep(5)
        for bomb in bombs.values():
            bomb.check() 
        for bomb in done_bombs:
            del bombs[bomb]
        done_bombs.clear()
        for bid, bomb in new_bombs:
            bombs[bid] = bomb
        new_bombs.clear()

server_thread = threading.Thread(target=doServer)
server_thread.daemon = True
server_thread.start()

try:
    doClock()
except KeyboardInterrupt:
    print("Saving users and bombs")
    userfile = open("users.encrypted", "wb")
    bombfile = open("bombs.encrypted", "wb")

    pickle.dump(users, userfile)
    pickle.dump(bombs, bombfile)
    userfile.close()
    bombfile.close()
    sys.exit()

