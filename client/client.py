#!/usr/bin/env python3
from websocket import create_connection
import json
import time
import sys
server = "ws://139.59.162.84:40111"
filename = "uid.txt"

if len(sys.argv) == 1:
    print ("MORE ARGS")
else:
    if len(sys.argv) == 3:
        if sys.argv[2] == "debug":
            server = "ws://localhost:40111"
            filename = "debug_uid.txt"
    if sys.argv[1] == "register":
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
    elif sys.argv[1] == "schedule":
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
        d["title"] = "Hey Sean"
        d["message"] = "It works"
        d["time"] = now
        d["uid"] = uid

        ws.send("BMB" + json.dumps(d))
        ws.close()