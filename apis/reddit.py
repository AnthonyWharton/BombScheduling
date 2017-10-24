import praw
import re

reddit = praw.Reddit(client_id='***REMOVED***',
                     client_secret='***REMOVED***',
                     password='***REMOVED',
                     username='BombScheduling',
                     user_agent='Sending messages (to people that want them) by /u/BombScheduling, StudentHackV project')

class RedditMessage():

    def __init__(self):
        return None

    def create(self):
        self.reddit_username      = ""

def send(details, msg):
    try:
        reddit.redditor(details.reddit_username).message(msg.message_title, msg.message_body)
    except Exception:
        print("Reddit failed")

def verify(details):
    name = details.reddit_username
    if name == "":
        return True
    p = re.compile(r'^[A-Za-z_0-9\-]{3,20}$')
    return p.search(name)

# def test():
#     msg = RedditMessage()
#     # msg.create("***REMOVED***", "Howdy", "Hello, World!")
#     msg.reddit_username      = "***REMOVED***"
#     msg.reddit_message_title = "reddit_message_asofjtitle"
#     msg.reddit_message_body  = "reddit_message_body"
#     send(msg)
#
# test()
