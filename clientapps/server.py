from websocket import create_connection
import sys

if (len(sys.argv) < 2):
    exit()


def dostuff(s):
    print(s)

ws = create_connection("ws://139.59.162.84:40111")
print("Connected")
ws.send("LGN" + sys.argv[1])
print("Logged in")
while(True):
   mess = ws.recv()
   print(mess)
   if mess[:3] == "ALR":
       dostuff(mess[3:])

