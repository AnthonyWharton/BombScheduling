#!/usr/bin/env python3
from websocket import create_connection
import json
import time
import sys
server = "ws://139.59.162.84:40111"
filename = "uid.txt"
flags_start = 1

if len(sys.argv) == 1:
    print ("MORE ARGS")
else:
    if len(sys.argv) > 1:
        if sys.argv[1] == "debug":
            flags_start += 1
            server = "ws://localhost:40111"
            filename = "debug_uid.txt"
    if sys.argv[flags_start] == "register":
        ws = create_connection(server)
        print("hah")
        print ("REQ")
        ws.send("REQ")
        print ("Sent")
        print ("Reeiving...")
        result =  ws.recv()[3:]
        jr = json.loads(result) 
        print (jr)
        for key in list(jr.keys()):
            prettyKey = key.replace("_", " ")
            prettyKey = prettyKey.title()
            print(prettyKey + ": ")
            jr[key] = input()
            
        print (jr)
        ws.send("USR"+json.dumps(jr))
        uid = ws.recv()[3:]
        print (uid)
        uidfile = open(filename, "w+")
        uidfile.write(str(uid))
    elif sys.argv[flags_start] == "schedule":
        ws = create_connection(server)
        try:
            uidfile = open(filename, "rt")
            uid = uidfile.readline().rstrip()
        except FileNotFoundError:
            print("hah")
            print ("REQ")
            ws.send("REQ")
            print ("Sent")
            print ("Reeiving...")
            result =  ws.recv()[3:]
            jr = json.loads(result) 
            print (jr)
            for key in list(jr.keys()):
                prettyKey = key.replace("_", " ")
                prettyKey = prettyKey.title()
                print(prettyKey + ": ")
                jr[key] = input()
                
            print (jr)
            ws.send("USR"+json.dumps(jr))
            uid = ws.recv()[3:]
            print (uid)
            uidfile = open(filename, "w+")
            uidfile.write(str(uid))
        uid = int(uid)
        print("Sending message to UID " + str(uid))
            
        now = time.time()

        d = {}
        if len(sys.argv) == flags_start + 4:
            d["title"] = sys.argv[flags_start + 1]
            d["message"] = sys.argv[flags_start + 2]
            t = sys.argv[flags_start + 3]
            if t[0] == "+":
                print(t[1:])
                d["time"] = now + float(t[1:])
            else:
                d["time"] = float(t)
            d["uid"] = uid
        else:
            d["title"] = ""
            d["message"] = "test"
            d["time"] = now
            d["uid"] = uid

        ws.send("BMB" + json.dumps(d))
        print(ws.recv()[3:])
        while(True):
            pass
        ws.close()
    elif sys.argv[flags_start] == "login":
        ws = create_connection(server)
        try:
            uidfile = open(filename, "rt")
            uid = uidfile.readline().rstrip()
        except FileNotFoundError:
            print("hah")
            print ("REQ")
            ws.send("REQ")
            print ("Sent")
            print ("Reeiving...")
            result =  ws.recv()[3:]
            jr = json.loads(result) 
            print (jr)
            for key in list(jr.keys()):
                prettyKey = key.replace("_", " ")
                prettyKey = prettyKey.title()
                print(prettyKey + ": ")
                jr[key] = input()
                
            print (jr)
            ws.send("USR"+json.dumps(jr))
            uid = ws.recv()[3:]
            print (uid)
            uidfile = open(filename, "w+")
            uidfile.write(str(uid))
        uid = int(uid)
        print("Logging in as " + str(uid))
        ws.send("LGN" + str(uid))
        while(True):
            pass
    elif sys.argv[flags_start] == "info":
        ws = create_connection(server)
        try:
            uidfile = open(filename, "rt")
            uid = uidfile.readline().rstrip()
        except FileNotFoundError:
            print("hah")
            print ("REQ")
            ws.send("REQ")
            print ("Sent")
            print ("Reeiving...")
            result =  ws.recv()[3:]
            jr = json.loads(result) 
            print (jr)
            for key in list(jr.keys()):
                prettyKey = key.replace("_", " ")
                prettyKey = prettyKey.title()
                print(prettyKey + ": ")
                jr[key] = input()
                
            print (jr)
            ws.send("USR"+json.dumps(jr))
            uid = ws.recv()[3:]
            print (uid)
            uidfile = open(filename, "w+")
            uidfile.write(str(uid))
        uid = int(uid)
        print("Getting info for " + str(uid))
        ws.send("INF" + str(uid))
        while(True):
            pass
    elif sys.argv[flags_start] == "list":
        ws = create_connection(server)
        try:
            uidfile = open(filename, "rt")
            uid = uidfile.readline().rstrip()
        except FileNotFoundError:
            print("hah")
            print ("REQ")
            ws.send("REQ")
            print ("Sent")
            print ("Reeiving...")
            result =  ws.recv()[3:]
            jr = json.loads(result) 
            print (jr)
            for key in list(jr.keys()):
                prettyKey = key.replace("_", " ")
                prettyKey = prettyKey.title()
                print(prettyKey + ": ")
                jr[key] = input()
                
            print (jr)
            ws.send("USR"+json.dumps(jr))
            uid = ws.recv()[3:]
            print (uid)
            uidfile = open(filename, "w+")
            uidfile.write(str(uid))
        uid = int(uid)
        print("Getting info for " + str(uid))
        ws.send("LST" + str(uid))
        while(True):
            pass