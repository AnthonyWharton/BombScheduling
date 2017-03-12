from websocket import create_connection
import sys

if (len(sys.argv) < 2):
    exit()



ws = create_connection("ws://139.59.162.84:40111")
print("Sending 'Hello, World'...")
ws.send("LGN" + sys.argv[1])
print("Sent")
# print("Receiving...")
# result =  ws.recv()
# print("Received '%s'" % result)
# ws.close()

while True:
    pass

# def main(argv)
