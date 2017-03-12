# import soundcloud
#
# client = soundcloud.Client(client_id="8a60af37f3a99161bca375510b1ebe55")
# tracks = client.get('/tracks', limit=10)
# for track in tracks:
#     print(track.title)

# import tkinter as tk
from tkinter import *



def interrupt(msg):


    class Fullscreen_Window:

        def __init__(self):
            self.tk = Tk()
            self.tk.attributes('-zoomed', True)  # This just maximizes it so we can see the window. It's nothing to do with fullscreen.
            self.frame = Frame(self.tk)
            self.frame.pack()
            self.state = False
            self.tk.bind("<F11>", self.toggle_fullscreen)
            self.tk.bind("<Escape>", self.end_fullscreen)

        def toggle_fullscreen(self, event=None):
            self.state = not self.state  # Just toggling the boolean
            self.tk.attributes("-fullscreen", self.state)
            return "break"

        def end_fullscreen(self, event=None):
            self.state = False
            self.tk.attributes("-fullscreen", False)
            return "break"

    w = Fullscreen_Window()
    w.tk['bg'] = 'red'
    w.toggle_fullscreen()

    Button(w.tk, text="Quit", command=w.tk.destroy).pack()
    # l = Label(w.tk, text="").pack()

    import soundcloud
    from urllib.request import urlopen

    # create a client object with your app credentials
    client = soundcloud.Client(client_id="8a60af37f3a99161bca375510b1ebe55")

    # fetch track to stream
    # track = client.get('/tracks/293')

    # find all sounds of buskers licensed under 'creative commons share alike'
    # tracks = client.get('/tracks', q='A meeting in the office')
    tracks = client.get('/tracks', q=msg.message_title)

    print(tracks[0].title)

    # get the tracks streaming URL
    stream_url = client.get(tracks[0].stream_url, allow_redirects=False)

    # print the tracks stream URL
    print(stream_url.location)

    # u = urlopen(stream_url.location)

    import vlc
    from vlc import State
    p = vlc.MediaPlayer(stream_url.location)
    p.play()
    # while not p.get_state() == 5:
    #     pass

    w.tk.mainloop()


class Message():
    def __init__(self):
        pass

message = Message()
message.message_title = "The office"

interrupt(message)
