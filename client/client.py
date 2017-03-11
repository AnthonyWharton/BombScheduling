#!/usr/bin/env python3
from websocket import create_connection
import json
ws = create_connection("ws://139.59.162.84:40111")
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
uid = ws.recv()
print (uid)
ws.close()